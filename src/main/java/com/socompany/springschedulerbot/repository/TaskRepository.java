package com.socompany.springschedulerbot.repository;

import com.socompany.springschedulerbot.persistant.entity.Task;
import com.socompany.springschedulerbot.persistant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface TaskRepository extends JpaRepository<Task, Integer> {
    public List<Task> findByUser(User user);
    @Query("SELECT t FROM Task t WHERE t.reminderTime = :reminderTime")
    List<Task> findTasksByReminderTime(LocalTime reminderTime);

}
