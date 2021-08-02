package com.nice.task.mc1.service;

import com.nice.task.mc1.dto.MessageDTO;
import com.nice.task.mc1.entity.Message;
import com.nice.task.mc1.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
public class MessageService {

    @Autowired
    private MessageRepository repository;

    public MessageDTO createMessage() {
        return new MessageDTO(1, Date.from(Instant.now()), null, null, null);
    }

    public void saveMessage(MessageDTO dto) {
        Message message = new Message();
        message.setMC1_timestamp(dto.getMC1_timestamp());
        message.setMC2_timestamp(dto.getMC2_timestamp());
        message.setMC3_timestamp(dto.getMC3_timestamp());
        message.setEnd_timestamp(dto.getEnd_timestamp());

        repository.save(message);
    }

}
