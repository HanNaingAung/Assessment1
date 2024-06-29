package org.example.controller;

import org.example.exception.ResourceNotFoundException;
import org.example.model.Promotion;
import org.example.service.PromotionService;
import org.example.util.PayloadWrapper;
import org.example.util.UpdateItemPayload;
import org.example.util.UpdatePromoPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/promotions")
public class PromotionController {

    @Autowired
    private MessagingTemplate messagingTemplate;

    @Autowired
    private MessageChannel replyChannel;

    @Autowired
    private MessageChannel promoChannel;


      @GetMapping
    public ResponseEntity<?> getAllPromotions() {
        PayloadWrapper payload = new PayloadWrapper("GET", null,null);
        Message<PayloadWrapper> message = MessageBuilder.withPayload(payload).build();
        messagingTemplate.setDefaultDestination(replyChannel);
        messagingTemplate.send(promoChannel, message);
        Message<?> reply = messagingTemplate.receive();
        return ResponseEntity.ok( reply.getPayload());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPromotionById(@PathVariable Long id) {
        PayloadWrapper payload = new PayloadWrapper("GET_BY_ID", null,id);
        Message<PayloadWrapper> message = MessageBuilder.withPayload(payload).build();
        messagingTemplate.setDefaultDestination(replyChannel);
        messagingTemplate.send(promoChannel, message);
        Message<?> reply = messagingTemplate.receive();
        if (Optional.empty() == reply.getPayload() || reply.getPayload() == null) {
            throw new ResourceNotFoundException("Promotion not found for id ::: " + id);
        }
        return ResponseEntity.ok( reply.getPayload());
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<Object> getPromotionByStoreId(@PathVariable Long itemId) {
        PayloadWrapper payload = new PayloadWrapper("GET_BY_ITEM_ID", null,itemId);
        Message<PayloadWrapper> message = MessageBuilder.withPayload(payload).build();
        messagingTemplate.setDefaultDestination(replyChannel);
        messagingTemplate.send(promoChannel, message);
        Message<?> reply = messagingTemplate.receive();
        if (Optional.empty() == reply.getPayload() || reply.getPayload() == null ) {
            throw new ResourceNotFoundException("Promotion not found for item id ::: " + itemId);
        }
        return ResponseEntity.ok( reply.getPayload());
    }

    @PostMapping
    public ResponseEntity<Object> createPromotion(@RequestBody Promotion promotion) throws Exception {
        PayloadWrapper payload = new PayloadWrapper("CREATE", promotion,null);
        boolean sent = promoChannel.send(MessageBuilder.withPayload(payload).build());
        if (!sent) {
            throw new Exception("Failed to initiate create operation");
        }
        return ResponseEntity.ok("Create operation initiated: " + promotion.toString());
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePromotion(@PathVariable Long id, @RequestBody Promotion promotion) {
        UpdatePromoPayload updatePayload = new UpdatePromoPayload(id,promotion);
        PayloadWrapper payload = new PayloadWrapper("UPDATE", updatePayload,id);
        Message<PayloadWrapper> message = MessageBuilder.withPayload(payload).build();
        messagingTemplate.setDefaultDestination(replyChannel);
        messagingTemplate.send(promoChannel, message);
        Message<?> reply = messagingTemplate.receive();
        if (Optional.empty() == reply.getPayload() || payload == null ) {
            throw new ResourceNotFoundException("Update Fail ! Old Promotion not found for id ::: " + id);
        }
        return ResponseEntity.ok("Update operation initiated: "+ reply.getPayload());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePromotion(@PathVariable Long id) {
        PayloadWrapper payload = new PayloadWrapper("DELETE", null,id);
        Message<PayloadWrapper> message = MessageBuilder.withPayload(payload).build();
        messagingTemplate.setDefaultDestination(replyChannel);
        messagingTemplate.send(promoChannel, message);
        Message<?> reply = messagingTemplate.receive();
        if (Optional.empty() == reply.getPayload() || payload == reply.getPayload() ) {
            throw new ResourceNotFoundException("Delete Fail ! Promotion not found for id ::: " + id);
        }
        return ResponseEntity.ok( reply.getPayload());
    }
}
