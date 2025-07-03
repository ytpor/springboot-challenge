package com.ytpor.api.service;

import org.springframework.stereotype.Service;
import com.ytpor.api.client.WeatherApiClient;
import com.ytpor.api.model.WeatherResponse;

@Service
public class WeatherService {

    private final WeatherApiClient weatherApiClient;

    public WeatherService(WeatherApiClient weatherApiClient) {
        this.weatherApiClient = weatherApiClient;
    }

    public WeatherResponse getWeatherForCity(String city) {
        return weatherApiClient.fetchWeather(city);
    }
}

