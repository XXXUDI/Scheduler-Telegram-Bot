package com.socompany.springschedulerbot.service;

import com.socompany.springschedulerbot.persistant.enums.CountryCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Map;

@Service
@Slf4j
public class WeatherService {

    private final RestTemplate restTemplate;
    private final String apiKey;
    private final String BASE_URL;

    public static final Map<CountryCode, String> COUNTRY_CAPITALS = Map.ofEntries(
            Map.entry(CountryCode.US, "Washington"),
            Map.entry(CountryCode.CA, "Ottawa"),
            Map.entry(CountryCode.GB, "London"),
            Map.entry(CountryCode.DE, "Berlin"),
            Map.entry(CountryCode.FR, "Paris"),
            Map.entry(CountryCode.IT, "Rome"),
            Map.entry(CountryCode.ES, "Madrid"),
            Map.entry(CountryCode.PL, "Warsaw"),
            Map.entry(CountryCode.RU, "Moscow"),
            Map.entry(CountryCode.UA, "Kyiv"),
            Map.entry(CountryCode.IN, "New Delhi"),
            Map.entry(CountryCode.CN, "Beijing"),
            Map.entry(CountryCode.JP, "Tokyo"),
            Map.entry(CountryCode.KR, "Seoul"),
            Map.entry(CountryCode.BR, "Brasília"),
            Map.entry(CountryCode.AU, "Canberra"),
            Map.entry(CountryCode.ZA, "Pretoria"), // ???
            Map.entry(CountryCode.NG, "Abuja"),
            Map.entry(CountryCode.TR, "Ankara"),
            Map.entry(CountryCode.MX, "Mexico City")
    );

    public WeatherService(RestTemplateBuilder builder,
                          Environment environment) {
        this.restTemplate = builder.build();
        this.apiKey = environment.getProperty("weather.api.key");
        this.BASE_URL = environment.getProperty("weather.api.url");
    }

    public Double getTemperatureByCity(CountryCode countryCode) {
        log.info("Getting weather for country: {}", countryCode.name());

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("q", COUNTRY_CAPITALS.get(countryCode))
                .queryParam("appid", apiKey)
                .queryParam("units", "metric"); // To get temperature in °C

        try {
            log.info("Sending request to {}", uriBuilder.toUriString());
            ResponseEntity<Map> response = restTemplate.getForEntity(uriBuilder.toUriString(), Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> main = (Map<String, Object>) response.getBody().get("main");
                Double temp = (Double) main.get("temp");
                log.info("Temperature in {}: {}°C", COUNTRY_CAPITALS.get(countryCode), temp);
                return temp;
            }
        } catch (Exception e) {
            log.error("Error retrieving weather for city: {}", COUNTRY_CAPITALS.get(countryCode), e);
        }

        return null;
    }
}
