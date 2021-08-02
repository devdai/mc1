package com.nice.task.mc1.service;

import com.nice.task.mc1.dto.MessageDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@Slf4j
public class MessageService {

    public MessageDTO createMessage() {
        return new MessageDTO(1, Date.from(Instant.now()), null, null);
    }

}
