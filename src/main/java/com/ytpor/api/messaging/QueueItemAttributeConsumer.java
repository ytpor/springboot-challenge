package com.ytpor.api.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.ytpor.api.model.MessageSend;
import com.ytpor.api.service.ItemAttributeService;

@Component
public class QueueItemAttributeConsumer {
    private final ItemAttributeService itemAttributeService;

    public QueueItemAttributeConsumer(ItemAttributeService itemAttributeService) {
        this.itemAttributeService = itemAttributeService;
    }

    @RabbitListener(queues = "${application.rabbitmq.queue-item-attribute}")
    public void receiveMessage(MessageSend message) {
        itemAttributeService.backgroundProcess(message);
    }
}