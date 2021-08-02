package com.nice.task.mc1.repository;

import com.nice.task.mc1.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface MessageRepository extends JpaRepository<Message, Integer> {

    @Query("select max(m.sessionId) from Message m")
    Long findMaxSessionId();

    Long countAllBySessionId(Long sessionId);

}
