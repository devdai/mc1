package com.nice.task.mc1.repository;

import com.nice.task.mc1.entity.InterruptedSession;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InterruptedSessionRepository extends JpaRepository<InterruptedSession, Integer> {

    InterruptedSession findInterruptedSessionBySessionId(Long sessionId);
}
