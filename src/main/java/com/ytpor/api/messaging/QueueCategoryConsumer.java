package com.ytpor.api.messaging;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import com.ytpor.api.model.MessageSend;
import com.ytpor.api.service.CategoryService;

@Component
public class QueueCategoryConsumer {
    private final CategoryService categoryService;

    public QueueCategoryConsumer(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @RabbitListener(queues = "${application.rabbitmq.queue-category}")
    public void receiveMessage(MessageSend message) {
        categoryService.backgroundProcess(message);
    }
}