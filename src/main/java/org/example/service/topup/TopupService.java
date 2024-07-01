package org.example.service.topup;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class TopupService {

    @Autowired
    private MessageChannel requestChannel;

    public void topupWallet(String redirectUrl, long amount, String authToken, String signature, String nonce, String timestamp) {
        Map<String, Object> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Authorization", "Bearer " + authToken);
        headers.put("X-Signature", signature);
        headers.put("X-Nonce-Str", nonce);
        headers.put("X-Timestamp", timestamp);

        Map<String, Object> payload = new HashMap<>();
        payload.put("redirect", redirectUrl);
        payload.put("amount", amount);

        requestChannel.send(MessageBuilder.withPayload(payload).copyHeaders(headers).build());
    }
}
