package com.socompany.springschedulerbot.controller;

import com.socompany.springschedulerbot.persistant.enums.CountryCode;
import com.socompany.springschedulerbot.service.WeatherService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Deprecated
public class WeatherController {
    private final WeatherService weatherService;

    @RequestMapping("/temp/PL")
    public ResponseEntity<Double> getWeather() {
        Double temp = weatherService.getTemperatureByCity(CountryCode.PL);
        return temp != null ? ResponseEntity.ok(temp) : ResponseEntity.status(503).build();
    }
}
