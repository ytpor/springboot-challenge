package com.ytpor.api.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.web.config.PageableHandlerMethodArgumentResolverCustomizer;

@Configuration
public class PageableConfig {
    @Bean
    public PageableHandlerMethodArgumentResolverCustomizer pageableResolverCustomizer() {
        /**
         * Override default page index from 0 to 1.
         * Page index on frontend UI normally starts from 1 instead of 0.
         */
        return resolver -> resolver.setOneIndexedParameters(true);
    }
}
