package com.ytpor.api.service;

import com.ytpor.api.configuration.ApplicationProperties;
import com.ytpor.api.model.MessageSend;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.amqp.rabbit.core.RabbitTemplate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
class MessagePublisherTest {

    @Mock
    private ApplicationProperties applicationProperties;

    @Mock
    private RabbitTemplate rabbitTemplate;

    @InjectMocks
    private MessagePublisher messagePublisher;

    @Test
    void testIsRabbitMqReachable_success() {
        when(rabbitTemplate.execute(any())).thenReturn(null);
        assertTrue(messagePublisher.isRabbitMqReachable());
    }

    @Test
    void testIsRabbitMqReachable_failure() {
        when(rabbitTemplate.execute(any())).thenReturn(false);
        assertFalse(!messagePublisher.isRabbitMqReachable());
    }

    @Test
    void testSendMessage_rabbitMqReachable() {
        MessageSend message = new MessageSend(); // You may need to mock or construct this properly
        String routingKey = "test.key";

        ApplicationProperties.RabbitMQ rabbitmq = mock(ApplicationProperties.RabbitMQ.class);
        when(applicationProperties.getRabbitmq()).thenReturn(rabbitmq);
        when(rabbitmq.getExchangeName()).thenReturn("test.exchange");
        when(rabbitTemplate.execute(any())).thenReturn(null); // Simulate reachable

        messagePublisher.sendMessage(routingKey, message);

        verify(rabbitTemplate).convertAndSend("test.exchange", routingKey, message);
    }

    @Test
    void testSendMessage_rabbitMqNotReachable() {
        MessageSend message = new MessageSend();
        String routingKey = "test.key";

        when(rabbitTemplate.execute(any())).thenThrow(new RuntimeException("Connection failed"));

        messagePublisher.sendMessage(routingKey, message);

        verify(rabbitTemplate, never()).convertAndSend(any(String.class), any(String.class), any(MessageSend.class));
    }
}
