package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.core.MessagingTemplate;

@Configuration
public class MessagingConfig {

    @Bean
    public MessagingTemplate messagingTemplate() {
        return new MessagingTemplate();
    }
}