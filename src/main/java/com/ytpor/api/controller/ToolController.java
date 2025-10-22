package com.ytpor.api.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirements;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Random;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.ytpor.api.model.SerialVersionUID;
import com.ytpor.api.model.SystemStatus;
import com.ytpor.api.service.MessagePublisher;
import com.ytpor.api.service.MinioService;

@RestController
@RequestMapping("/tool")
@Tag(name = "Tool", description = "Tools used for specific tasks")
@SecurityRequirements()
public class ToolController {

    private final Random random = new Random();

    private final MessagePublisher messagePublisher;
    private final MinioService minioService;

    public ToolController(
        MessagePublisher messagePublisher,
        MinioService minioService
    ) {
        this.messagePublisher = messagePublisher;
        this.minioService = minioService;
    }

    @GetMapping("/random-uid")
    @Operation(summary = "Generate UID", description = "Generate Random Serial Version UID")
    public ResponseEntity<SerialVersionUID> generateSerialVersionUID() {
        SerialVersionUID serialVersionUID = new SerialVersionUID();
        serialVersionUID.setUid(random.nextLong() + "L");

        return ResponseEntity.ok(serialVersionUID);
    }

    @GetMapping("/status")
    @Operation(summary = "System status", description = "Display various system statuses")
    public ResponseEntity<SystemStatus> generateStatus() {
        SystemStatus systemStatus = new SystemStatus();
        systemStatus.setMinio(minioService.isMinioReachable());
        systemStatus.setRabbitmq(messagePublisher.isRabbitMqReachable());
        return ResponseEntity.ok(systemStatus);
    }
}
