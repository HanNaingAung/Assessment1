package org.example.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class WalletController {

    @Autowired
    private MessageChannel topupWalletChannel;

    @Autowired
    public MessageChannel replyWalletChannel;

    @Autowired
    private MessagingTemplate messagingTemplate;

    @PostMapping("/topup")
    public ResponseEntity<?> topupWallet() {
        Message<String> message = MessageBuilder.withPayload("").build();
        messagingTemplate.setDefaultDestination(replyWalletChannel);
        messagingTemplate.send(topupWalletChannel, message);
        Message<?> reply = messagingTemplate.receive();
        return ResponseEntity.ok(reply.getPayload());
    }
}

