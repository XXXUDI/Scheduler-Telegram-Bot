package com.socompany.springschedulerbot.persistant.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "task_table")
public class Task extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer taskId;

    private String taskName;

    @Column(nullable = false)
    private LocalTime reminderTime;

    @ManyToOne()
    @JoinColumn(name = "chat_id", referencedColumnName = "chatId", nullable = false)
    private User user;

}
