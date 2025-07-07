package com.ytpor.api.component;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;
import com.ytpor.api.model.WeatherResponse;

@Component
public class WeatherApiClient {

    @Value("${weatherapi.key}")
    private String apiKey;

    private final RestTemplate restTemplate;

    public WeatherApiClient(RestTemplateBuilder builder) {
        this.restTemplate = builder
            .connectTimeout(Duration.ofSeconds(5))
            .readTimeout(Duration.ofSeconds(5))
            .build();
    }

    public WeatherResponse fetchWeather(String city) {
        String url = String.format("https://api.weatherapi.com/v1/current.json?key=%s&q=%s", apiKey, city);
        return restTemplate.getForObject(url, WeatherResponse.class);
    }
}
