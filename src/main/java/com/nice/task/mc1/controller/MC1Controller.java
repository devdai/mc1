package com.nice.task.mc1.controller;

import com.nice.task.mc1.dto.MessageDTO;
import com.nice.task.mc1.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public void stop() {
        //stop
    }

}
