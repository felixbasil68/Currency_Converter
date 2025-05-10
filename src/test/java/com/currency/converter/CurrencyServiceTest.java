package com.currency.converter;

import com.currency.converter.dto.ConversionRequest;
import com.currency.converter.dto.ConversionResponse;
import com.currency.converter.dto.ExchangeRateResponse;
import com.currency.converter.exception.ApiUnavailableException;
import com.currency.converter.exception.InvalidCurrencyException;
import com.currency.converter.service.CurrencyService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;

import static org.mockito.Mockito.*;

class CurrencyServiceTest {

    @InjectMocks
    private CurrencyService currencyService;

    @Mock
    private RestTemplate restTemplate;

    /**
     * Initializes mocks and sets up configuration values before each test case.
     */
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        currencyService.token = "access_key=test-token";
        currencyService.apiUrl = "https://api.exchangeratesapi.io/latest?";
    }

    /**
     * Test that verifies the successful fetching of exchange rates for given currencies.
     */
    @Test
    void testGetRates_success() {
        ExchangeRateResponse mockResponse = new ExchangeRateResponse();
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("EUR", 0.9);
        mockResponse.setRates(rates);

        // Mocking API call
        when(restTemplate.getForObject(contains("symbols=USD,EUR"), eq(ExchangeRateResponse.class)))
                .thenReturn(mockResponse);

        // Execute the service method
        ExchangeRateResponse response = currencyService.getRatesWithValues("USD", "EUR");

        // Validate the response
        assertNotNull(response);
        assertEquals(2, response.getRates().size());
        assertEquals(1.0, response.getRates().get("USD"));
    }

    /**
     * Test that verifies exception is thrown when an invalid currency is used (i.e., no rates returned).
     */
    @Test
    void testGetRates_invalidCurrency() {
        ExchangeRateResponse mockResponse = new ExchangeRateResponse();
        mockResponse.setRates(new HashMap<>());

        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class)))
                .thenReturn(mockResponse);

        // Expect InvalidCurrencyException due to empty rates map
        assertThrows(InvalidCurrencyException.class, () -> currencyService.getRates("XYZ"));
    }

    /**
     * Test that verifies exception is thrown when the external API is unavailable or throws an error.
     */
    @Test
    void testApiUnavailable() {
        // Simulate a failure in API call
        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class)))
                .thenThrow(new RestClientException("API error"));

        // Expect ApiUnavailableException to be thrown
        assertThrows(ApiUnavailableException.class, () -> currencyService.getRates("USD"));
    }

    /**
     * Test that verifies the successful conversion of currency when valid data is provided.
     */
    @Test
    void testConvert_success() {
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.0);
        rates.put("INR", 75.0);

        ExchangeRateResponse response = new ExchangeRateResponse();
        response.setRates(rates);

        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class)))
                .thenReturn(response);

        ConversionRequest request = new ConversionRequest("USD", "INR", 100.0);
        ConversionResponse result = currencyService.convert(request);

        // Validate conversion logic
        assertEquals("USD", result.getFrom());
        assertEquals("INR", result.getTo());
        assertEquals(100.0, result.getAmount());
        assertEquals(7500.0, result.getConvertedAmount(), 0.001);
    }

    /**
     * Test that verifies exception is thrown when conversion is attempted with a missing target currency rate.
     */
    @Test
    void testConvert_invalidCurrency() {
        Map<String, Double> rates = new HashMap<>();
        rates.put("USD", 1.0);  

        ExchangeRateResponse response = new ExchangeRateResponse();
        response.setRates(rates);

        when(restTemplate.getForObject(anyString(), eq(ExchangeRateResponse.class)))
                .thenReturn(response);

        ConversionRequest request = new ConversionRequest("USD", "INR", 100.0);

        // Expect InvalidCurrencyException due to missing INR rate
        assertThrows(InvalidCurrencyException.class, () -> currencyService.convert(request));
    }
}
