package com.socompany.springschedulerbot.repository;

import com.socompany.springschedulerbot.persistant.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;
import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByChatId(Long chatId);

    List<User> findByDailyReminderTime(LocalTime dailyReminderTime);

    @Query("SELECT u FROM User u WHERE " +
            "u.isWeatherReminderEnabled = true OR " +
            "u.isEventsReminderEnabled = true OR " +
            "u.isBitcoinPriceReminderEnabled = true OR " +
            "u.isCurrencyPriceReminderEnabled = true")
    List<User> findUsersWithAnyReminderEnabled();
}
