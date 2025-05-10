package com.currency.converter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.currency.converter.dto.ConversionRequest;
import com.currency.converter.dto.ConversionResponse;
import com.currency.converter.dto.ExchangeRateResponse;
import com.currency.converter.service.CurrencyService;


@RestController
@RequestMapping("/api")
public class CurrencyController {

    @Value("${token}")
    private String token;

    @Autowired
    private CurrencyService currencyService;

    @GetMapping("/rates")
    public ResponseEntity<ExchangeRateResponse> getRates(@RequestParam(defaultValue = "USD") String base) {
        return ResponseEntity.ok(currencyService.getRates(base));
    }

    @PostMapping("/convert")
    public ResponseEntity<ConversionResponse> convert(@RequestBody ConversionRequest request) {
        return ResponseEntity.ok(currencyService.convert(request));
    }
  }
