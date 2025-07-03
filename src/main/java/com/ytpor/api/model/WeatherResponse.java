package com.ytpor.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class WeatherResponse {
    private Location location;
    private Current current;

    @Data
    @Schema(description = "Weather location")
    public static class Location {
        @Schema(description = "Location", example = "Paris")
        private String name;
        @Schema(description = "Region of location", example = "Ile-de-France")
        private String region;
        @Schema(description = "Country of location", example = "Ile-de-France")
        private String country;
    }

    @Data
    @Schema(description = "Current weather")
    public static class Current {
        @Schema(description = "Temperature in Fehrenheit", example = "16.4")
        private double temp_c;
        @Schema(description = "Temperature in Celcius", example = "61.5")
        private double temp_f;
        private Condition condition;

        @Data
        @Schema(description = "Weather condition")
        public static class Condition {
            @Schema(description = "Text", example = "Sunny")
            private String text;
            @Schema(description = "Icon", example = "//cdn.weatherapi.com/weather/64x64/day/113.png")
            private String icon;
        }
    }
}
