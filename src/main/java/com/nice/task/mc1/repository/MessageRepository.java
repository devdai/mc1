package com.nice.task.mc1.repository;

import com.nice.task.mc1.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Integer> {

}
