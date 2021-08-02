package com.nice.task.mc1.controller;

import com.nice.task.mc1.dto.MessageDTO;
import com.nice.task.mc1.entity.InterruptedSession;
import com.nice.task.mc1.repository.InterruptedSessionRepository;
import com.nice.task.mc1.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@RestController
@Slf4j
public class MC1Controller {

    @Value("${message.rotation.interval.seconds}")
    private long rotationInterval;
    private long passedSeconds = 0;
    private Thread currentExecution;

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MessageService messageService;

    @Autowired
    private InterruptedSessionRepository sessionRepository;

    /**
     * Start execution. Before creating new message, check for last session Id and increment
     * MessageService takes care about nullable result
     */
    @GetMapping("/start")
    public void startExecution() {
        long sessionId = messageService.getLastSessionId() + 1;
        log.info("Start message exchange process");
        createAndSendMessage(sessionId);
    }

    /**
     * Manually stop execution. Simply create an instance of InterruptedSession entity,
     * set current running session's Id, mark as interrupted and save to DB.
     * Now on the next iteration of creating messages will find this entity and stop execution
     */
    @GetMapping("/stop")
    public void stopExecution() {
        long sessionId = messageService.getLastSessionId();
        InterruptedSession session = new InterruptedSession();
        session.setSessionId(sessionId);
        session.setInterrupted(true);
        sessionRepository.save(session);
    }

    /**
     * Receive message from MC3, log execution details and save it to DB.
     * Execute createAndSendMessage `rotationInterval` seconds (the amount of seconds in config file)
     *
     * Also there is a check that session is not interrupted, before starting new message -
     * as it could be stopped manually through /stop endpoint.
     *
     * I think this approach with additional entity *InterruptedSession* is much better than
     * sending *interrupt* event or (omg) forcibly stopping the running Thread.
     *
     * @param messageDTO message body
     * @return HttpMethod.Ok
     */
    @PostMapping(value = "/receive-message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String receiveMessage(@RequestBody MessageDTO messageDTO) {
        messageDTO.setEnd_timestamp(Date.from(Instant.now()));
        messageService.saveMessage(messageDTO);

        Duration executionTime = Duration.between(messageDTO.getMC1_timestamp().toInstant(), messageDTO.getEnd_timestamp().toInstant());
        passedSeconds += executionTime.toSeconds();

        //if we have time to run another iteration. passedSeconds will reset on the end of the execution
        if (passedSeconds < rotationInterval) {
            InterruptedSession session = sessionRepository.findInterruptedSessionBySessionId(messageDTO.getSessionId());

            //if InterruptedSession entity exists and it is marked as interrupted, just log end execution
            if (session != null && session.isInterrupted()) {
                logEndExecutionAndResetTime(messageDTO.getSessionId());
            } else {
                createAndSendMessage(messageDTO.getSessionId());
            }
        }
        if (passedSeconds >= rotationInterval) {
            logEndExecutionAndResetTime(messageDTO.getSessionId());
        }

        return HttpStatus.OK.toString();
    }

    /**
     * Create and send message to given destination using SimMessagingTemplate
     */
    private void createAndSendMessage(long sessionId) {
        MessageDTO message = messageService.createMessage(sessionId);
        simpMessagingTemplate.convertAndSend("/topic/MC2", message);
    }

    private void logEndExecutionAndResetTime(long sessionId) {
        long count = messageService.getSavedMessagesCountInThisSession(sessionId);
        log.info("End Execution");
        log.info("Execution time: {}", passedSeconds);
        log.info("Messages generated: {}", count);
        this.passedSeconds = 0;
    }

}
