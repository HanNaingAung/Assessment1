package org.example.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.util.TopupPayload;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.*;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class IntegrationConfig {

    @Value("${api.base.url}")
    private String apiBaseUrl;

    @Value("${api.sandbox.url}")
    private String apiSanBoxUrl;

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
                    /*TopupPayload topupPayload = (TopupPayload) message;
                    // Create headers
                    HttpHeaders headers = new HttpHeaders();
                    headers.setContentType(MediaType.APPLICATION_JSON);
                    headers.set("Authorization", "Bearer " + topupPayload.getAuthToken());
                    headers.set("X-Signature", topupPayload.getSignature());
                    headers.set("X-Nonce-Str", topupPayload.getNonce());
                    headers.set("X-Timestamp", topupPayload.getTimestamp());

                    // Create payload
                    Map<String, Object> payload = new HashMap<>();
                    payload.put("redirect", topupPayload.getRedirectUrl());
                    payload.put("amount", topupPayload.getAmount());
                    ObjectMapper objectMapper = new ObjectMapper();
                    String jsonPayload;
                    try {
                         jsonPayload = objectMapper.writeValueAsString(payload);
                    } catch (JsonProcessingException e) {
                        throw new RuntimeException(e);
                    }

                    // Create HttpEntity with payload and headers
                    HttpEntity<String> entity = new HttpEntity<>(jsonPayload, headers);
                    // Create RestTemplate instance
                    RestTemplate restTemplate = new RestTemplate();
                    // Send request and handle response
                    String url = topupPayload.getEnv().equals("PROD")?apiBaseUrl:apiSanBoxUrl;
                    ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);

                    // Handle API response
                    return MessageBuilder.withPayload(response.getBody()).build();*/

                   /* RestTemplate restTemplate = new RestTemplate();
                    ResponseEntity<String> response = restTemplate.getForEntity(apiUrl, String.class);
                    return MessageBuilder.withPayload(response.getBody()).build();*/

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



