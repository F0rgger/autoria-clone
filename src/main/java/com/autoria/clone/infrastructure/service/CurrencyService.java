package com.autoria.clone.infrastructure.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Сервис для получения курсов валют через API ПриватБанка.
 */
@Service
@RequiredArgsConstructor
public class CurrencyService {

    private final RestTemplate restTemplate;

    @Value("${privatbank.api.url}")
    private String privatBankApiUrl;

    /**
     * Получает текущие курсы валют с кэшированием (обновление раз в день).
     *
     * @param currencies Список валют (например, USD, EUR)
     * @return Карта с курсами валют (валюта -> курс к UAH)
     */
    @Cacheable(value = "exchangeRates", sync = true)
    public Map<String, BigDecimal> getExchangeRates(String... currencies) {
        String url = privatBankApiUrl + "?json&exchange&coursid=5";
        PrivatBankExchangeRateResponse[] response = restTemplate.getForObject(url, PrivatBankExchangeRateResponse[].class);

        Map<String, BigDecimal> rates = new HashMap<>();
        if (response != null) {
            for (PrivatBankExchangeRateResponse rate : response) {
                for (String currency : currencies) {
                    if (currency.equals(rate.getCcy()) && "UAH".equals(rate.getBase_ccy())) {
                        rates.put(currency, new BigDecimal(rate.getSale()));
                    }
                }
            }
        }
        return rates;
    }
}

/**
 * DTO для ответа от API ПриватБанка.
 */
@Data
class PrivatBankExchangeRateResponse {
    private String ccy;
    private String base_ccy;
    private String buy;
    private String sale;
}