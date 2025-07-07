package com.ytpor.api.service;

import com.ytpor.api.component.WeatherApiClient;
import com.ytpor.api.model.WeatherResponse;
import com.ytpor.api.model.WeatherResponse.Current;
import com.ytpor.api.model.WeatherResponse.Current.Condition;
import com.ytpor.api.model.WeatherResponse.Location;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WeatherServiceTest {

    private WeatherApiClient weatherApiClient;
    private WeatherService weatherService;

    @BeforeEach
    void setup() {
        weatherApiClient = mock(WeatherApiClient.class);
        weatherService = new WeatherService(weatherApiClient);
    }

    @Test
    void testGetWeatherForCity() {
        // Arrange
        String city = "Kuala Lumpur";
        WeatherResponse mockResponse = new WeatherResponse();

        Location location = new Location();
        location.setName("Kuala Lumpur");
        location.setCountry("Malaysia");

        Condition condition = new Condition();
        condition.setText("Sunny");

        Current current = new Current();
        current.setTemp_c(32.0);
        current.setCondition(condition);

        mockResponse.setLocation(location);
        mockResponse.setCurrent(current);

        when(weatherApiClient.fetchWeather(city)).thenReturn(mockResponse);

        // Act
        WeatherResponse result = weatherService.getWeatherForCity(city);

        // Assert
        assertNotNull(result);
        assertEquals("Kuala Lumpur", result.getLocation().getName());
        assertEquals("Malaysia", result.getLocation().getCountry());
        assertEquals(32.0, result.getCurrent().getTemp_c());
        assertEquals("Sunny", result.getCurrent().getCondition().getText());

        verify(weatherApiClient, times(1)).fetchWeather(city);
    }
}

