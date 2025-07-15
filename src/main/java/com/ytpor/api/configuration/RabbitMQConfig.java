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
        return new Queue(applicationProperties.getRabbitmq().getQueueCategory(), applicationProperties.getRabbitmq().isQueueCategoryDurable());
    }

    @Bean
    public Queue queueItemAttribute() {
        return new Queue(applicationProperties.getRabbitmq().getQueueItemAttribute(), applicationProperties.getRabbitmq().isQueueItemAttributeDurable());
    }

    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(applicationProperties.getRabbitmq().getExchangeName());
    }

    @Bean
    public Binding bindingCategory(Queue queueCategory, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueCategory).to(topicExchange)
                .with(applicationProperties.getRabbitmq().getQueueCategoryKey());
    }

    @Bean
    public Binding bindingItemAttribute(Queue queueItemAttribute, TopicExchange topicExchange) {
        return BindingBuilder.bind(queueItemAttribute).to(topicExchange)
                .with(applicationProperties.getRabbitmq().getQueueItemAttributeKey());
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
