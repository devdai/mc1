package com.nice.task.mc1.controller;

import com.nice.task.mc1.dto.MessageDTO;
import com.nice.task.mc1.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Date;

@RestController
public class MC1Controller {

    @Autowired
    private SimpMessagingTemplate simpMessagingTemplate;

    @Autowired
    private MessageService messageService;

    @GetMapping("/start")
    public void startMessage() {
        MessageDTO message = messageService.createMessage();

        System.out.println("Start message exchange process");
        simpMessagingTemplate.convertAndSend("/topic/MC2", message);
    }

    @GetMapping("/stop")
    public void stopExecution() {
        //stop
    }

    @PostMapping(value = "/receive-message", consumes = MediaType.APPLICATION_JSON_VALUE)
    public String receiveMessage(@RequestBody MessageDTO messageDTO) {
        messageDTO.setEnd_timestamp(Date.from(Instant.now()));
        messageService.saveMessage(messageDTO);

        System.out.println("End Execution");
        System.out.println("Execution time: " + 11 + "s");
        System.out.println("Messages generated: " + 2);
        return HttpStatus.OK.toString();
    }

}
