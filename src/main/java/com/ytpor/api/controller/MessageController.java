package com.ytpor.api.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.ytpor.api.configuration.ApplicationProperties;
import com.ytpor.api.exception.MissingRequestBodyException;
import com.ytpor.api.model.MessageReceived;
import com.ytpor.api.model.MessageSend;
import com.ytpor.api.service.MessagePublisher;

@RestController
@RequestMapping("/api/v1/message")
@Tag(name = "Message")
public class MessageController {

    private static final String REQUEST_BODY_MISSING = "Request body is missing.";

    private final ApplicationProperties applicationProperties;
    private final MessagePublisher publisher;

    public MessageController(
        ApplicationProperties applicationProperties,
        MessagePublisher publisher
    ) {
        this.applicationProperties = applicationProperties;
        this.publisher = publisher;
    }

    @PostMapping("/send-category")
    @Operation(summary = "Send category message", description = "Send a message to the category queue")
    public ResponseEntity<MessageReceived> sendCategory(@Valid @RequestBody(required = false) MessageSend messageSend) {
        if (messageSend == null) {
            throw new MissingRequestBodyException(REQUEST_BODY_MISSING);
        }

        ApplicationProperties.RabbitMQ rabbitmq = applicationProperties.getRabbitmq();

        publisher.sendMessage(rabbitmq.getQueueCategoryKey(), messageSend);

        MessageReceived response = new MessageReceived();
        response.setStatus("Message received");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/send-item-attribute")
    @Operation(summary = "Send item attribute message", description = "Send a message to the item attribute queue")
    public ResponseEntity<MessageReceived> sendItemAttribute(@Valid @RequestBody(required = false) MessageSend messageSend) {
        if (messageSend == null) {
            throw new MissingRequestBodyException(REQUEST_BODY_MISSING);
        }

        ApplicationProperties.RabbitMQ rabbitmq = applicationProperties.getRabbitmq();

        publisher.sendMessage(rabbitmq.getQueueItemAttributeKey(), messageSend);

        MessageReceived response = new MessageReceived();
        response.setStatus("Message received");

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
}
