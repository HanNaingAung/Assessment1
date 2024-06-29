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

    /*@Autowired
    private MessageChannel getItemChannel;
    @Autowired
    private MessageChannel getItemByIdChannel;
    @Autowired
    private MessageChannel getItemWithPromotionsChannel;
    @Autowired
    private MessageChannel getItemWithPromotionsByIdChannel;
    @Autowired
    private MessageChannel postItemChannel;
    @Autowired
    private MessageChannel putItemChannel;
    @Autowired
    private MessageChannel deleteItemChannel;*/
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

/*    @GetMapping
    public List<Item> getAllItems() {
        return itemService.getAllItems();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Item> getItemById(@PathVariable String id) {
        return itemService.getItemById(id)
                .map(item -> ResponseEntity.ok().body(item))
                .orElseThrow(() ->(new ResourceNotFoundException("Item not found for id :"+id)));
    }

    @GetMapping("/promos")
    public List<ItemPromoDto> getAllItemsWithPromos() {
        return itemService.getAllItemsWithPromotions();
    }

    @GetMapping("/promos/{id}")
    public ResponseEntity<ItemPromoDto> getItemWithPromosById(@PathVariable String id) {
        return itemService.getItemWithPromotionsById(id)
                .map(item -> ResponseEntity.ok().body(item))
                .orElseThrow(() ->(new ResourceNotFoundException("Item not found for id :"+id)));
    }

    @PostMapping
    public Item createItem(@RequestBody Item Item) {
        return itemService.createItem(Item);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Item> updateItem(@PathVariable String id, @RequestBody Item Item) {
        return itemService.getItemById(id)
                .map(existingItem -> {
                    Item.setId(id);
                    return ResponseEntity.ok().body(itemService.updateItem(id, Item));
                })
                .orElseThrow(() ->(new ResourceNotFoundException("Item not found for id :"+id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable String id) {
        return itemService.getItemById(id)
                .map(existingStore -> {
                    itemService.deleteItem(id);
                    return ResponseEntity.ok().build();
                })
                .orElseThrow(() ->(new ResourceNotFoundException("Item not found for id :"+id)));
    }*/


    //Test with integration dsl patterns
//    @GetMapping
//    public List<Item> getAllItems() {
//        Message<String> message = MessageBuilder.withPayload("").build();
//        messagingTemplate.setDefaultDestination(replyItemChannel);
//        messagingTemplate.send(getItemChannel, message);
//        Message<?> reply = messagingTemplate.receive();
//        return (List<Item>) reply.getPayload();
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<Object> getItemById(@PathVariable Long id) {
//        Message<Long> message = MessageBuilder.withPayload(id).build();
//        messagingTemplate.setDefaultDestination(replyItemChannel);
//        messagingTemplate.send(getItemByIdChannel, message);
//        Message<?> reply = messagingTemplate.receive();
//        Object payload = reply.getPayload();
//        if (Optional.empty() == payload || payload == null ) {
//            throw new ResourceNotFoundException("Item not found for id ::: " + id);
//        }
//        return ResponseEntity.ok( reply.getPayload());
//    }

//    @GetMapping("/promotions")
//    public ResponseEntity<?> getAllItemsWithPromos() {
//        Message<String> message = MessageBuilder.withPayload("").build();
//        messagingTemplate.setDefaultDestination(replyItemChannel);
//        messagingTemplate.send(getItemWithPromotionsChannel, message);
//        Message<?> reply = messagingTemplate.receive();
//        return ResponseEntity.ok( reply.getPayload());
//    }
//
//    @GetMapping("/promotions/{id}")
//    public ResponseEntity<?> getItemWithPromosById(@PathVariable Long id) {
//        Message<Long> message = MessageBuilder.withPayload(id).build();
//        messagingTemplate.setDefaultDestination(replyItemChannel);
//        messagingTemplate.send(getItemWithPromotionsByIdChannel, message);
//        Message<?> reply = messagingTemplate.receive();
//        Object payload = reply.getPayload();
//        if (Optional.empty() == payload || payload == null ) {
//            throw new ResourceNotFoundException("Item not found for id ::: " + id);
//        }
//        return ResponseEntity.ok( payload);
//    }
//
//
//    @PostMapping
//    public ResponseEntity<Object> createItem(@RequestBody Item item) {
//        Message<Item> message = MessageBuilder.withPayload(item).build();
//        messagingTemplate.setDefaultDestination(replyItemChannel);
//        messagingTemplate.send(postItemChannel, message);
//        Message<?> reply = messagingTemplate.receive();
//        return ResponseEntity.ok( reply.getPayload());
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody Item item) {
//        UpdateItemPayload updateItemPayload = new UpdateItemPayload(id,item);
//        Message<UpdateItemPayload> message = MessageBuilder.withPayload(updateItemPayload).build();
//        messagingTemplate.setDefaultDestination(replyItemChannel);
//        messagingTemplate.send(putItemChannel, message);
//        Message<?> reply = messagingTemplate.receive();
//        Object payload = reply.getPayload();
//        if (Optional.empty() == payload || payload == null ) {
//            throw new ResourceNotFoundException("Item not found for id ::: " + id);
//        }
//        return ResponseEntity.ok( payload);
//    }

//    @DeleteMapping("/{id}")
//    public ResponseEntity<Object> deleteItem(@PathVariable Long id) {
//        Message<Long> message = MessageBuilder.withPayload(id).build();
//        messagingTemplate.setDefaultDestination(replyItemChannel);
//        messagingTemplate.send(deleteItemChannel, message);
//        Message<?> reply = messagingTemplate.receive();
//        Object payload = reply.getPayload();
//        if (Optional.empty() == payload || payload == null ) {
//            throw new ResourceNotFoundException("Item not found for id ::: " + id);
//        }
//        return ResponseEntity.ok( payload);
//    }

}
