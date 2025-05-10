package com.currency.converter.service;

import com.currency.converter.exception.ApiUnavailableException;
import com.currency.converter.exception.InvalidCurrencyException;
import com.currency.converter.dto.ConversionRequest;
import com.currency.converter.dto.ConversionResponse;
import com.currency.converter.dto.ExchangeRateResponse;
import lombok.RequiredArgsConstructor;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Map;
import java.util.TreeMap;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class CurrencyService {

    @Value("${token}")
    public String token;

    @Value("${currency.api.url}")
    public String apiUrl;

    private final RestTemplate restTemplate;

    /**
     * Retrieves exchange rates relative to the specified base currency.
     * If no base is provided, returns rates based on the default API base.
     * 
     * @param baseCurrency the desired base currency
     * @return ExchangeRateResponse with rates based on the given base currency
     */

    public ExchangeRateResponse getRates(String baseCurrency) {
        String url = UriComponentsBuilder.fromUriString(apiUrl + token)
                .build()
                .toUriString();

        try {
            ExchangeRateResponse response = restTemplate.getForObject(url, ExchangeRateResponse.class);

            if (response == null || response.getRates() == null || response.getRates().isEmpty()) {
                throw new InvalidCurrencyException("No exchange rates found.");
            }

            String originalBase = response.getBase();
            Map<String, Double> originalRates = response.getRates();

            // If no new base is specified or it's the same as the original base, return

            if (baseCurrency == null || baseCurrency.isBlank() || baseCurrency.equalsIgnoreCase(originalBase)) {
                Map<String, Double> roundedSortedRates = new TreeMap<>();
                for (Map.Entry<String, Double> entry : originalRates.entrySet()) {
                    roundedSortedRates.put(entry.getKey(), round(entry.getValue()));
                }
                response.setRates(roundedSortedRates);
                return response;
            }

            // Handle conversion to a different base currency

            Double rateOfNewBase = originalRates.get(baseCurrency.toUpperCase());
            if (rateOfNewBase == null) {
                throw new InvalidCurrencyException(
                        "Requested base currency '" + baseCurrency + "' not found in exchange rates.");
            }

            Map<String, Double> convertedRates = new TreeMap<>();
            for (Map.Entry<String, Double> entry : originalRates.entrySet()) {
                String currency = entry.getKey();
                double value = entry.getValue();
                convertedRates.put(currency, round(value / rateOfNewBase));
            }

            // Add the original base rate in terms of the new base

            convertedRates.put(originalBase.toUpperCase(), round(1.0 / rateOfNewBase));

            ExchangeRateResponse convertedResponse = new ExchangeRateResponse();
            convertedResponse.setBase(baseCurrency.toUpperCase());
            convertedResponse.setRates(convertedRates);

            return convertedResponse;

        } catch (RestClientException e) {
            throw new ApiUnavailableException("Currency API is currently unavailable. Please try again later.");
        }
    }

    /**
     * Fetches exchange rates for specific currency symbols.
     *
     * @param symbols currency codes to retrieve rates for
     * @return ExchangeRateResponse containing rounded rates for the given symbols
     */

    public ExchangeRateResponse getRatesWithValues(String... symbols) {
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

            // Round each rate to 2 decimal places

            Map<String, Double> roundedRates = new TreeMap<>();
            for (Map.Entry<String, Double> entry : response.getRates().entrySet()) {
                roundedRates.put(entry.getKey(), round(entry.getValue()));
            }
            response.setRates(roundedRates);

            return response;
        } catch (RestClientException e) {
            throw new ApiUnavailableException("Currency API is currently unavailable. Please try again later.");
        }
    }

    /**
     * Converts a given amount from one currency to another using latest exchange
     * rates.
     *
     * @param request ConversionRequest containing source, target, and amount
     * @return ConversionResponse with converted amount and details
     */

    public ConversionResponse convert(ConversionRequest request) {
        try {
            ExchangeRateResponse response = getRatesWithValues(request.getFrom().toUpperCase(),
                    request.getTo().toUpperCase());
            double fromRate = response.getRates().getOrDefault(request.getFrom().toUpperCase(), 0.0);
            double toRate = response.getRates().getOrDefault(request.getTo().toUpperCase(), 0.0);

            if (fromRate == 0.0 || toRate == 0.0) {
                throw new InvalidCurrencyException("Invalid currency code: " +
                        (fromRate == 0.0 ? request.getFrom().toUpperCase() : request.getTo().toUpperCase()));
            }

            // Convert amount using the formula: (amount / fromRate) * toRate

            double convertedAmount = request.getAmount() / fromRate * toRate;
            double roundedAmount = round(convertedAmount);

            return new ConversionResponse(
                    request.getFrom().toUpperCase(),
                    request.getTo().toUpperCase(),
                    request.getAmount(),
                    roundedAmount);
        } catch (RestClientException clientException) {
            throw new ApiUnavailableException("Currency API is currently unavailable. Please try again later.");
        }
    }

    /**
     * Utility method to round a double to 2 decimal places using HALF_UP mode.
     *
     * @param value the value to round
     * @return rounded double value
     */

    private double round(double value) {
        return new BigDecimal(value).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }
}
