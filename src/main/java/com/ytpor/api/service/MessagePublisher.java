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

    public void sendMessage(String routingKey, MessageSend message) {
        ApplicationProperties.RabbitMQ rabbitmq = applicationProperties.getRabbitmq();

        rabbitTemplate.convertAndSend(rabbitmq.getExchangeName(), routingKey, message);
        logger.info("Queue to {} {} {}", rabbitmq.getExchangeName(), routingKey, message);
    }
}
