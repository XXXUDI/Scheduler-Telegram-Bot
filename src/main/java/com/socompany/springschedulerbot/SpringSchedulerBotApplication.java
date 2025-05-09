package com.socompany.springschedulerbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableJpaAuditing
@EnableScheduling
public class SpringSchedulerBotApplication {

    public static void main(String[] args) {
        System.out.println("\uD83C\uDF12\uD83C\uDF13\uD83C\uDF14");
        SpringApplication.run(SpringSchedulerBotApplication.class, args);
    }

}
