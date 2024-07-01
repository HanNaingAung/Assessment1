package org.example.integration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.client.RestTemplate;

import java.net.URI;

@Configuration
public class IntegrationConfig {

    @Value("${api.url}")
    private String apiUrl;

    @Bean
    public MessageChannel topupWalletChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public MessageChannel replyWalletChannel(){
        return new QueueChannel();
    }

    @Bean
    public IntegrationFlow topupWalletFlow() {
        return IntegrationFlows.from("topupWalletChannel")
                .handle((message,h) -> {

                    RestTemplate restTemplate = new RestTemplate();
                    ResponseEntity<String> response = restTemplate.exchange(
                            RequestEntity.get(URI.create(apiUrl)).build(), String.class);

                    if (response.getStatusCode() == HttpStatus.MOVED_PERMANENTLY) {
                        URI newUrl = response.getHeaders().getLocation();
                        response = restTemplate.exchange(
                                RequestEntity.get(newUrl).build(), String.class);
                    }
                    return response.getBody();
                }).channel(replyWalletChannel())
                .get();
    }

}



