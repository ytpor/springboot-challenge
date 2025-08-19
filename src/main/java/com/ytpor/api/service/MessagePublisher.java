package com.ytpor.api.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import com.ytpor.api.configuration.ApplicationProperties;
import com.ytpor.api.model.MessageSend;

@Service
public class MessagePublisher {

    private final ApplicationProperties applicationProperties;
    private final RabbitTemplate rabbitTemplate;
    private static final Logger logger = LoggerFactory.getLogger(MessagePublisher.class);

    public MessagePublisher(
        ApplicationProperties applicationProperties,
        RabbitTemplate rabbitTemplate
    ) {
        this.applicationProperties = applicationProperties;
        this.rabbitTemplate = rabbitTemplate;
    }

    public boolean isRabbitMqReachable() {
        try {
            rabbitTemplate.execute(channel -> null);
            return true;
        } catch (Exception e) {
            logger.error("RabbitMQ is not reachable: {}", e.getMessage());
            return false;
        }
    }

    public void sendMessage(String routingKey, MessageSend message) {
        if (isRabbitMqReachable()) {
            rabbitTemplate.convertAndSend(applicationProperties.getRabbitmq().getExchangeName(), routingKey, message);
            logger.info("Queue to {} {} {}", applicationProperties.getRabbitmq().getExchangeName(), routingKey, message);
        } else {
            logger.error("Message not sent, RabbitMQ is reachable.");
        }
    }
}
