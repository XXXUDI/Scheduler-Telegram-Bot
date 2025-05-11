package com.socompany.springschedulerbot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;


@Service
@Slf4j
public class CryptoService {

    private final RestTemplate restTemplate;

    private final String apiKey;

    private final String BASE_URL;


    public CryptoService(RestTemplateBuilder restTemplateBuilder,
                         Environment environment) {
        this.restTemplate = restTemplateBuilder.build();
        apiKey = environment.getProperty("coinmarketcap.api.key");
        BASE_URL = environment.getProperty("coinmarketcap.api.url");
    }


    public Double getBitcoinPrice() {
        log.info("Getting Bitcoin price");
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-CMC_PRO_API_KEY", apiKey);
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));

        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(BASE_URL)
                .queryParam("start", "1")
                .queryParam("limit", "100")
                .queryParam("convert", "USD");

        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            log.info("Sending request to {}", uriBuilder.toUriString());
            ResponseEntity<Map> response = restTemplate.exchange(
                    uriBuilder.toUriString(),
                    HttpMethod.GET,
                    entity,
                    Map.class
            );

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> data = (List<Map<String, Object>>) response.getBody().get("data");
                log.info("Received {} cryptocurrencies", data.size());
                for (Map<String, Object> crypto : data) {
                    if ("Bitcoin".equalsIgnoreCase((String) crypto.get("name"))) {
                        Map<String, Object> quote = (Map<String, Object>) crypto.get("quote");
                        Map<String, Object> usd = (Map<String, Object>) quote.get("USD");
                        log.info("Bitcoin price: {}", usd.get("price"));
                        return (Double) usd.get("price");
                    }
                }
            }

        } catch (Exception e) {
            log.error("Error retrieving Bitcoin price", e);
        }

        return null;
    }
}
