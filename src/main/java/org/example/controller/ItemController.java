package org.example.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.common.annotation.RestLoggable;
import org.example.common.pagination.Pagination;
import org.example.dto.ItemPromoDto;
import org.example.dto.PageResponseDto;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Item;
import org.example.util.PayloadWrapper;
import org.example.util.UpdateItemPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.integration.core.MessagingTemplate;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/items")
@RestLoggable
public class ItemController {

    private static final Logger applicationLogger = LogManager.getLogger("applicationLogs." + ItemController.class.getName());

    @Autowired
    private MessagingTemplate messagingTemplate;
    @Autowired
    private MessageChannel replyItemChannel;

    @Autowired
    private MessageChannel itemChannel;

    @GetMapping
    public ResponseEntity<?> getAllItems() {
        applicationLogger.info("Request received to get all items.");
        Optional<List> items = sendAndReceiveMessage("GET", null, null, List.class);
        applicationLogger.info("Response sent with {} items.", items.get().size());
        return ResponseEntity.ok(items);
    }

    @GetMapping("/paging")
    public ResponseEntity<?> getAllItemsWithPagination(@RequestParam(defaultValue = "1") int page,
                                                       @RequestParam(defaultValue = "10") int pageSize) {
        applicationLogger.info("Request received to get all items based on pagination.");
        Pagination pagination = new Pagination();
        pagination.setPageNum(page);
        pagination.setPageSize(pageSize);
        Optional<Page> items = sendAndReceiveMessage("GET_WITH_PAGINATION", pagination, null, Page.class);

        Page paging = items.get();
        PageResponseDto<Item> response = new PageResponseDto<>();
        response.setPageNumber(paging.getTotalPages());
        response.setPageSize(paging.getSize());
        response.setData(paging.getContent());
        response.setTotalCount(paging.getTotalElements());

        applicationLogger.info("Page " + (page) + " of " + paging.getTotalPages() + " containing " + paging.getTotalElements() + " instances");
        return ResponseEntity.ok(paging);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItemById(@PathVariable Long id) {
        applicationLogger.info("Request received to get item by id: {}", id);
        Optional<Item> item = sendAndReceiveMessage("GET_BY_ID", null, id, Item.class);
        if (item == null) {
            throw new ResourceNotFoundException("Item not found for id ::: " + id);
        }
        applicationLogger.info("Response sent with item details for id: {}", id);
        return ResponseEntity.ok(item);
    }

    @GetMapping("/promotions")
    public ResponseEntity<?> getAllItemsWithPromos() {
        applicationLogger.info("Request received to get all items with promotions.");
        Optional<List> items = sendAndReceiveMessage("GET_WITH_PROMO", null, null, List.class);
        applicationLogger.info("Response sent with {} promotional items.", items.get().size());
        return ResponseEntity.ok( items);
    }

    @GetMapping("/promotions/{id}")
    public ResponseEntity<?> getItemWithPromosById(@PathVariable Long id) {
        applicationLogger.info("Request received to get item with promotions by id: {}", id);
        Optional<ItemPromoDto> item = sendAndReceiveMessage("GET_WITH_PROMO_BY_ID", null, id, ItemPromoDto.class);
        if (item == null) {
            throw new ResourceNotFoundException("Item not found for id ::: " + id);
        }
        applicationLogger.info("Response sent with promotional item details for id: {}", id);
        return ResponseEntity.ok(item);
    }

    @PostMapping
    public ResponseEntity<String> createItem(@Valid @RequestBody Item item) throws Exception {
        applicationLogger.info("Request received to create item: {}", item);
        boolean success = sendMessage("CREATE", item, null);
        if (!success) {
            throw new Exception("Failed to initiate create operation");
        }
        applicationLogger.info("Create operation initiated for item: {}", item);
        return ResponseEntity.ok("Create operation initiated: " + item.toString());
    }


    @PutMapping("/{id}")
    public ResponseEntity<?> updateItem(@PathVariable Long id, @RequestBody Item item) {
        applicationLogger.info("Request received to update item with id: {}", id);
        UpdateItemPayload updatePayload = new UpdateItemPayload(id,item);
        Optional<Item> result = sendAndReceiveMessage("UPDATE", updatePayload, id,Item.class);
        if (result == null) {
            throw new ResourceNotFoundException("Update Fail ! Old Item not found for id ::: " + id);
        }
        applicationLogger.info("Update operation initiated for item with id: {}", id);
        return ResponseEntity.ok("Update operation initiated.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(@PathVariable Long id) {
        applicationLogger.info("Request received to delete item with id: {}", id);
        Optional<Item> item = sendAndReceiveMessage("DELETE", null, id,Item.class);
        if (item == null) {
            throw new ResourceNotFoundException("Delete Fail ! Item not found for id ::: " + id);
        }
        applicationLogger.info("Delete operation initiated for item with id: {}", id);
        return ResponseEntity.ok("Delete operation initiated.");
    }

    private PayloadWrapper createPayload(String action, Object entity, Long id) {
        return new PayloadWrapper(action, entity, id);
    }
    private <T> Optional<T> sendAndReceiveMessage(String action, Object entity, Long id, Class<T> responseType) {
        PayloadWrapper payload = createPayload(action, entity, id);
        Message<PayloadWrapper> message = MessageBuilder.withPayload(payload).build();
        messagingTemplate.setDefaultDestination(replyItemChannel);
        messagingTemplate.send(itemChannel, message);
        Message<?> reply = messagingTemplate.receive();
        if (reply != null) {
            Object payloadReply = reply.getPayload();
            if (responseType.isInstance(payloadReply)) {
                return Optional.of(responseType.cast(payloadReply));
            } else if (payloadReply instanceof Optional) {
                Optional<?> optionalPayload = (Optional<?>) payloadReply;
                if (optionalPayload.isPresent() && responseType.isInstance(optionalPayload.get())) {
                    return Optional.of(responseType.cast(optionalPayload.get()));
                }
            }
        }
        return null;
    }

    private boolean sendMessage(String action, Object entity, Long id) {
        PayloadWrapper payload = createPayload(action, entity, id);
        Message<PayloadWrapper> message = MessageBuilder.withPayload(payload).build();
        return itemChannel.send(message);
    }


}
