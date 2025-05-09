package com.currency.converter.controller;

import org.springframework.beans.factory.annotation.Autowired;
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

/**
 * REST controller that handles currency conversion and exchange rate retrieval.
 */
@RestController
@RequestMapping("/api")
public class CurrencyController {

    /*
     * Currency service to handle business logic related to currency conversion and rates.
     */

    @Autowired
    private CurrencyService currencyService;

    /*
     * Endpoint to retrieve exchange rates based on the given base currency.
     * If no base is specified, USD is used by default */

    @GetMapping("/rates")
    public ResponseEntity<ExchangeRateResponse> getRates(@RequestParam(defaultValue = "USD") String base) {
        return ResponseEntity.ok(currencyService.getRates(base));
    }

    /*
     * Endpoint to convert an amount from one currency to another
     */

    @PostMapping("/convert")
    public ResponseEntity<ConversionResponse> convert(@RequestBody ConversionRequest request) {
        return ResponseEntity.ok(currencyService.convert(request));
    }
}
