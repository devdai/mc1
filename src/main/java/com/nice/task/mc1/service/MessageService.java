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

    public MessageDTO createMessage(long sessionId) {
        return new MessageDTO(sessionId, Date.from(Instant.now()), null, null, null);
    }

    public long getLastSessionId() {
        Long lastSessionId = repository.findMaxSessionId();

        return lastSessionId == null ? 0 : lastSessionId;
    }

    public long getSavedMessagesCountInThisSession(long sessionId) {
        Long count = repository.countAllBySessionId(sessionId);

        return count == null ? 0 : count;
    }

    public void saveMessage(MessageDTO dto) {
        Message message = new Message();
        message.setSessionId(dto.getSessionId());
        message.setMC1_timestamp(dto.getMC1_timestamp());
        message.setMC2_timestamp(dto.getMC2_timestamp());
        message.setMC3_timestamp(dto.getMC3_timestamp());
        message.setEnd_timestamp(dto.getEnd_timestamp());

        repository.save(message);
    }

}
