package com.socompany.springschedulerbot.repository;

import com.socompany.springschedulerbot.persistant.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {



}
