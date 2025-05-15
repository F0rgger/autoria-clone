package com.autoria.clone.infrastructure.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;

@SpringBootTest
public class CurrencyServiceTest {

    @Autowired
    private CurrencyService currencyService;

    @MockBean
    private RestTemplate restTemplate;

    @BeforeEach
    public void setUp() {
        ReflectionTestUtils.setField(currencyService, "privatBankApiUrl", "https://api.privatbank.ua/p24api/pubinfo");
    }

    @Test
    public void testGetExchangeRatesSuccess() {
        PrivatBankExchangeRateResponse[] response = new PrivatBankExchangeRateResponse[]{
                new PrivatBankExchangeRateResponse() {{
                    setCcy("USD");
                    setBase_ccy("UAH");
                    setSale("41.0");
                }}
        };
        when(restTemplate.getForObject("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5",
                PrivatBankExchangeRateResponse[].class)).thenReturn(response);

        Map<String, BigDecimal> rates = currencyService.getExchangeRates("USD");
        assertEquals(new BigDecimal("41.0"), rates.get("USD"));
    }

    @Test
    public void testGetExchangeRatesEmptyResponse() {
        when(restTemplate.getForObject("https://api.privatbank.ua/p24api/pubinfo?json&exchange&coursid=5",
                PrivatBankExchangeRateResponse[].class)).thenReturn(null);

        Map<String, BigDecimal> rates = currencyService.getExchangeRates("USD");
        assertTrue(rates.isEmpty());
    }
}