package com.socompany.springschedulerbot.controller;

import com.socompany.springschedulerbot.service.CryptoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/crypto")
@Deprecated
public class CryptoController {

    private final CryptoService cryptoService;

    public CryptoController(CryptoService cryptoService) {
        this.cryptoService = cryptoService;
    }

    @GetMapping("/bitcoin")
    public ResponseEntity<Double> getBitcoinPrice() {
        Double price = cryptoService.getBitcoinPrice();
        return price != null ? ResponseEntity.ok(price) : ResponseEntity.status(503).build();
    }
}
