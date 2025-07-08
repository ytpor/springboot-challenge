package com.ytpor.api.configuration;

import org.springframework.amqp.core.*;
import org.springframework.amqp.support.converter.DefaultJackson2JavaTypeMapper;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    private final ApplicationProperties applicationProperties;

    public RabbitMQConfig(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Bean
    public Queue queueCategory() {
        ApplicationProperties.RabbitMQ rabbitmq = applicationProperties.getRabbitmq();

        return new Queue(rabbitmq.getQueueCategory(), rabbitmq.isQueueCategoryDurable());
    }

    @Bean
    public Queue queueItemAttribute() {
        ApplicationProperties.RabbitMQ rabbitmq = applicationProperties.getRabbitmq();

        return new Queue(rabbitmq.getQueueItemAttribute(), rabbitmq.isQueueItemAttributeDurable());
    }

    @Bean
    public TopicExchange topicExchange() {
        ApplicationProperties.RabbitMQ rabbitmq = applicationProperties.getRabbitmq();

        return new TopicExchange(rabbitmq.getExchangeName());
    }

    @Bean
    public Binding bindingCategory(Queue queueCategory, TopicExchange topicExchange) {
        ApplicationProperties.RabbitMQ rabbitmq = applicationProperties.getRabbitmq();

        return BindingBuilder.bind(queueCategory).to(topicExchange)
                .with(rabbitmq.getQueueCategoryKey());
    }

    @Bean
    public Binding bindingItemAttribute(Queue queueItemAttribute, TopicExchange topicExchange) {
        ApplicationProperties.RabbitMQ rabbitmq = applicationProperties.getRabbitmq();

        return BindingBuilder.bind(queueItemAttribute).to(topicExchange)
                .with(rabbitmq.getQueueItemAttributeKey());
    }

    @Bean
    public Jackson2JsonMessageConverter jackson2JsonMessageConverter() {
        Jackson2JsonMessageConverter converter = new Jackson2JsonMessageConverter();
        DefaultJackson2JavaTypeMapper typeMapper = new DefaultJackson2JavaTypeMapper();
        typeMapper.setTrustedPackages("com.ytpor.api.model"); // Trust your package
        converter.setJavaTypeMapper(typeMapper);

        return converter;
    }
}
