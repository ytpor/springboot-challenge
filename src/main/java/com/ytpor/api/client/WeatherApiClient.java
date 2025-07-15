package com.ytpor.api.client;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import java.time.Duration;
import com.ytpor.api.configuration.ApplicationProperties;
import com.ytpor.api.model.WeatherResponse;

@Component
public class WeatherApiClient {
    private final ApplicationProperties applicationProperties;
    private final RestTemplate restTemplate;

    public WeatherApiClient(
        ApplicationProperties applicationProperties,
        RestTemplateBuilder builder
    ) {
        this.applicationProperties = applicationProperties;

        this.restTemplate = builder
            .connectTimeout(Duration.ofSeconds(5))
            .readTimeout(Duration.ofSeconds(5))
            .build();
    }

    public WeatherResponse fetchWeather(String city) {
        String url = String.format("https://api.weatherapi.com/v1/current.json?key=%s&q=%s", applicationProperties.getWeatherapi().getKey(), city);
        return restTemplate.getForObject(url, WeatherResponse.class);
    }
}
