package org.example.controller;

import org.example.exception.ResourceNotFoundException;
import org.example.model.Item;
import org.example.util.PayloadWrapper;
import org.example.util.UpdateItemPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
public class ItemController {

    @Autowired
    private MessagingTemplate messagingTemplate;
    @Autowired
    private MessageChannel replyItemChannel;

    @Autowired
    private MessageChannel itemChannel;

    @GetMapping
    public List<Item> getAllItems() {
        PayloadWrapper payload = new PayloadWrapper("GET", null,null);
        Message<PayloadWrapper> message = MessageBuilder.withPayload(payload).build();
        messagingTemplate.setDefaultDestination(replyItemChannel);
        messagingTemplate.send(itemChannel, message);
        Message<?> reply = messagingTemplate.receive();
        return (List<Item>) reply.getPayload();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable Long id) {
        PayloadWrapper payload = new PayloadWrapper("GET_BY_ID", null,id);
        Message<PayloadWrapper> message = MessageBuilder.withPayload(payload).build();
        messagingTemplate.setDefaultDestination(replyItemChannel);
        messagingTemplate.send(itemChannel, message);
        Message<?> reply = messagingTemplate.receive();
        if (Optional.empty() == reply.getPayload() || reply.getPayload() == null ) {
            throw new ResourceNotFoundException("Item not found for id ::: " + id);
        }
        return ResponseEntity.ok( reply.getPayload());
    }

    @GetMapping("/promotions")
    public ResponseEntity<?> getAllItemsWithPromos() {
        PayloadWrapper payload = new PayloadWrapper("GET_WITH_PROMO", null,null);
        Message<PayloadWrapper> message = MessageBuilder.withPayload(payload).build();
        messagingTemplate.setDefaultDestination(replyItemChannel);
        messagingTemplate.send(itemChannel, message);
        Message<?> reply = messagingTemplate.receive();
        return ResponseEntity.ok( reply.getPayload());
    }

    @GetMapping("/promotions/{id}")
    public ResponseEntity<?> getItemWithPromosById(@PathVariable Long id) {
        PayloadWrapper payload = new PayloadWrapper("GET_WITH_PROMO_BY_ID", null,id);
        Message<PayloadWrapper> message = MessageBuilder.withPayload(payload).build();
        messagingTemplate.setDefaultDestination(replyItemChannel);
        messagingTemplate.send(itemChannel, message);
        Message<?> reply = messagingTemplate.receive();
        if (Optional.empty() == reply.getPayload() || reply.getPayload() == null ) {
            throw new ResourceNotFoundException("Item not found for id ::: " + id);
        }
        return ResponseEntity.ok( reply.getPayload());
    }

    @PostMapping
    public ResponseEntity<String> createItem(@RequestBody Item item) throws Exception {
        PayloadWrapper payload = new PayloadWrapper("CREATE", item,null);
        boolean sent = itemChannel.send(MessageBuilder.withPayload(payload).build());
        if (!sent) {
            throw new Exception("Failed to initiate create operation");
        }
        return ResponseEntity.ok("Create operation initiated: " + item.toString());
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody Item item) {
        UpdateItemPayload updatePayload = new UpdateItemPayload(id,item);
        PayloadWrapper payload = new PayloadWrapper("UPDATE", updatePayload,id);
        Message<PayloadWrapper> message = MessageBuilder.withPayload(payload).build();
        messagingTemplate.setDefaultDestination(replyItemChannel);
        messagingTemplate.send(itemChannel, message);
        Message<?> reply = messagingTemplate.receive();
        if (Optional.empty() == reply.getPayload() || reply.getPayload() == null ) {
            throw new ResourceNotFoundException("Update Fail ! Old Item not found for id ::: " + id);
        }
        return ResponseEntity.ok("Update operation initiated: "+ reply.getPayload());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long id) {
        PayloadWrapper payload = new PayloadWrapper("DELETE", null,id);
        Message<PayloadWrapper> message = MessageBuilder.withPayload(payload).build();
        messagingTemplate.setDefaultDestination(replyItemChannel);
        messagingTemplate.send(itemChannel, message);
        Message<?> reply = messagingTemplate.receive();
        if (Optional.empty() == reply.getPayload() || payload == reply.getPayload() ) {
            throw new ResourceNotFoundException("Delete Fail ! Item not found for id ::: " + id);
        }
        return ResponseEntity.ok( reply.getPayload());
    }

}
