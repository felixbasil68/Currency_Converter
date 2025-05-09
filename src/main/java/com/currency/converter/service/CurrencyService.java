package com.currency.converter.service;

import com.currency.converter.exception.ApiUnavailableException;
import com.currency.converter.exception.InvalidCurrencyException;
import com.currency.converter.dto.ConversionRequest;
import com.currency.converter.dto.ConversionResponse;
import com.currency.converter.dto.ExchangeRateResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    @Value("${token}")
    private String token;

    @Value("${currency.api.url}")
    private String apiUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public ExchangeRateResponse getRates(String... symbols) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUriString(apiUrl + token);

        if (symbols != null && symbols.length > 0) {
            String symbolParam = String.join(",", symbols);
            builder.queryParam("symbols", symbolParam);
        }

        String url = builder.build().toUriString();

        try {
            ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);
            if (response == null || response.getRates() == null || response.getRates().isEmpty()) {
                throw new InvalidCurrencyException("Invalid currency symbols provided.");
            }
            return response;
        } catch (RestClientException e) {
            throw new ApiUnavailableException("Currency API is currently unavailable. Please try again later.");
        }
    }

    public ConversionResponse convert(ConversionRequest request) {
        try {
            ExchangeRateResponse response = getRates(request.getFrom(), request.getTo());
            double fromRate = response.getRates().getOrDefault(request.getFrom(), 0.0);
            double toRate = response.getRates().getOrDefault(request.getTo(), 0.0);

            if (fromRate == 0.0 || toRate == 0.0) {
                throw new InvalidCurrencyException("Invalid currency code: " +
                        (fromRate == 0.0 ? request.getFrom() : request.getTo()));
            }

            double convertedAmount = request.getAmount() / fromRate * toRate;
            return new ConversionResponse(request.getFrom(), request.getTo(), request.getAmount(), convertedAmount);
        } catch (RestClientException clientException) {
            throw new ApiUnavailableException("Currency API is currently unavailable. Please try again later.");
        }
    }
}
