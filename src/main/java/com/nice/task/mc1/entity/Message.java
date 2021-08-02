package com.nice.task.mc1.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.util.Date;

@Entity
@Getter
@Setter
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private Long sessionId;
    private Date MC1_timestamp;
    private Date MC2_timestamp;
    private Date MC3_timestamp;
    private Date end_timestamp;;

}
