package org.example.controller;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.common.annotation.RestLoggable;
import org.example.exception.AlreadyExistsException;
import org.example.exception.ResourceNotFoundException;
import org.example.model.Promotion;
import org.example.util.PayloadWrapper;
import org.example.util.UpdatePromoPayload;
import org.springframework.beans.factory.annotation.Autowired;
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
@RequestMapping("/api/promotions")
@RestLoggable
public class PromotionController {

    private static final Logger applicationLogger = LogManager.getLogger("applicationLogs." + PromotionController.class.getName());
    @Autowired
    private MessagingTemplate messagingTemplate;

    @Autowired
    private MessageChannel replyChannel;

    @Autowired
    private MessageChannel promoChannel;


    @GetMapping
    public ResponseEntity<?> getAllPromotions() {
        applicationLogger.info("Request received to get all promotions.");
        Optional<List> promotions = sendAndReceiveMessage("GET", null, null, List.class);
        applicationLogger.info("Response sent with {} promotions.", promotions.get().size());
        return ResponseEntity.ok(promotions);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getPromotionById(@PathVariable Long id) {
        applicationLogger.info("Request received to get promotion by id: {}", id);
        Optional<Promotion> promotion = sendAndReceiveMessage("GET_BY_ID", null, id, Promotion.class);
        if (promotion == null) {
            throw new ResourceNotFoundException("Promotion not found for id ::: " + id);
        }
        applicationLogger.info("Response sent with promotion details for id: {}", id);
        return ResponseEntity.ok(promotion);
    }

    @GetMapping("/item/{itemId}")
    public ResponseEntity<Object> getPromotionByItemId(@PathVariable Long itemId) {
        applicationLogger.info("Request received to get promotion by item id: {}", itemId);
        Optional<Promotion> promotion = sendAndReceiveMessage("GET_BY_ITEM_ID", null, itemId, Promotion.class);
        if (promotion == null) {
            throw new ResourceNotFoundException("Promotion not found for item id ::: " + itemId);
        }
        applicationLogger.info("Response sent with promotion details by item id: {}", itemId);
        return ResponseEntity.ok( promotion);
    }

    @PostMapping
    public ResponseEntity<Object> createPromotion(@Valid @RequestBody Promotion promotion) throws Exception {
        applicationLogger.info("Request received to create promotion: {}", promotion);
        Optional<Promotion> promo = sendAndReceiveMessage("CREATE", promotion, null,Promotion.class);
        if (promo == null) {
            throw new AlreadyExistsException("Create Promo Fail! Item already exist with item id "+ promotion.getItemId());
        }
        applicationLogger.info("Create operation initiated for promotion: {}", promotion);
        return ResponseEntity.ok("Create operation initiated: " + promo);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updatePromotion(@PathVariable Long id, @RequestBody Promotion promotion) {
        applicationLogger.info("Request received to update promotion with id: {}", id);
        UpdatePromoPayload updatePayload = new UpdatePromoPayload(id,promotion);
        Optional<Promotion> promo = sendAndReceiveMessage("UPDATE", updatePayload, id,Promotion.class);
        if (promo == null) {
            throw new ResourceNotFoundException("Update Fail ! Old Promotion not found for id ::: " + id);
        }

        applicationLogger.info("Update operation initiated for promotion with id: {}", id);
        return ResponseEntity.ok("Update operation initiated.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deletePromotion(@PathVariable Long id) {
        applicationLogger.info("Request received to delete promotion with id: {}", id);
        Optional<Promotion> promotion = sendAndReceiveMessage("DELETE", null, id,Promotion.class);
        if (promotion == null) {
            throw new ResourceNotFoundException("Delete Fail ! Promotion not found for id ::: " + id);
        }
        applicationLogger.info("Delete operation initiated for promotion with id: {}", id);
        return ResponseEntity.ok( "Delete operation initiated.");
    }

    private PayloadWrapper createPayload(String action, Object entity, Long id) {
        return new PayloadWrapper(action, entity, id);
    }

    private <T> Optional<T> sendAndReceiveMessage(String action, Object entity, Long id, Class<T> responseType) {
        PayloadWrapper payload = createPayload(action, entity, id);
        Message<PayloadWrapper> message = MessageBuilder.withPayload(payload).build();
        messagingTemplate.setDefaultDestination(replyChannel);
        messagingTemplate.send(promoChannel, message);
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

}
