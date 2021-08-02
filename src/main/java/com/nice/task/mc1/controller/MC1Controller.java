package com.nice.task.mc1.controller;

import com.nice.task.mc1.dto.MessageDTO;
import com.nice.task.mc1.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.util.StopWatch;
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

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MessageService messageService;

    /**
     * Manually start execution
     */
    @GetMapping("/start")
    public void startExecution() {
        long sessionId = messageService.getLastSessionId() + 1;
        log.info("Start message exchange process");
        createAndSendMessage(sessionId);
    }

    /**
     * Manually stop execution
     */
    @GetMapping("/stop")
    public void stopExecution() {
        //stop
    }

    /**
     * Receive message from MC3, log execution details and save it to DB
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

        if (passedSeconds < rotationInterval) {
            createAndSendMessage(messageDTO.getSessionId());
        }
        if (passedSeconds >= rotationInterval) {
            long count = messageService.getSavedMessagesCountInThisSession(messageDTO.getSessionId());
            log.info("End Execution");
            log.info("Execution time: {}", rotationInterval);
            log.info("Messages generated: {}", count);
            passedSeconds = 0;
        }

        return HttpStatus.OK.toString();
    }

    /**
     * Create and send message to given destination in opened web socket
     */
    private void createAndSendMessage(long sessionId) {
        MessageDTO message = messageService.createMessage(sessionId);
        simpMessagingTemplate.convertAndSend("/topic/MC2", message);
    }


}
