package com.ytpor.api.model;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
public class SystemStatus {
    @Schema(description = "MinIO", example = "true")
    private Boolean minio;

    @Schema(description = "RadditMQ", example = "true")
    private Boolean rabbitmq;
}
