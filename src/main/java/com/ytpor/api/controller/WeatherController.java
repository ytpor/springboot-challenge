package com.ytpor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ytpor.api.model.WeatherResponse;
import com.ytpor.api.service.WeatherService;

@RestController
@RequestMapping("/api/v1/weather")
@Tag(name = "Weather")
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/{city}")
    @Operation(summary = "Get weather", description = "Retrieve weather for a city")
    public ResponseEntity<WeatherResponse> getWeather(@PathVariable String city) {
        return ResponseEntity.ok(weatherService.getWeatherForCity(city));
    }
}
