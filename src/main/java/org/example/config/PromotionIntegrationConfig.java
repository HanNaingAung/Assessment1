package org.example.config;

import org.example.model.Promotion;
import org.example.service.PromotionService;
import org.example.util.PayloadWrapper;
import org.example.util.UpdatePromoPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.MessageChannel;

import java.util.Optional;

@Configuration
@EnableIntegration
public class PromotionIntegrationConfig {

    @Autowired
    private PromotionService promotionService;

    @Bean
    public MessageChannel replyChannel() {
        return new QueueChannel();
    }

    @Bean
    public MessageChannel promoChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public IntegrationFlow handlePromoFlow() {
        return IntegrationFlows.from("promoChannel")
                .routeToRecipients(router -> router
                        .recipientFlow(payload -> payload instanceof PayloadWrapper &&
                                        ((PayloadWrapper) payload).getAction().equals("CREATE"),
                                flow -> flow.handle(m -> {
                                    PayloadWrapper payload = (PayloadWrapper) m.getPayload();
                                    Promotion item = (Promotion)payload.getEntity();
                                    promotionService.createPromotion(item);
                                }))
                        .recipientFlow(payload -> payload instanceof PayloadWrapper &&
                                        ((PayloadWrapper) payload).getAction().equals("UPDATE"),
                                flow -> flow.handle((m,headers) -> {
                                    PayloadWrapper payload = (PayloadWrapper) m;
                                    UpdatePromoPayload updatePromoPayload = (UpdatePromoPayload)payload.getEntity();
                                    Optional<Promotion> promotion = promotionService.getPromotionById(updatePromoPayload.getId());
                                    if (promotion.isPresent()) {
                                        Promotion newPromotion = updatePromoPayload.getPromotion();
                                        newPromotion.setId(updatePromoPayload.getId());
                                        return MessageBuilder.withPayload(promotionService.updatePromotion(updatePromoPayload.getId(), newPromotion)).build();
                                    } else {
                                        return MessageBuilder.withPayload(promotion).build();
                                    }
                                }).channel(replyChannel()))
                        .recipientFlow(payload -> payload instanceof PayloadWrapper &&
                                        ((PayloadWrapper) payload).getAction().equals("GET"),
                                flow -> flow.handle((m,headers) -> {
                                    return MessageBuilder.withPayload(promotionService.getAllPromotions()).build();
                                }).channel(replyChannel()))
                        .recipientFlow(payload -> payload instanceof PayloadWrapper &&
                                        ((PayloadWrapper) payload).getAction().equals("GET_BY_ID"),
                                flow -> flow.handle((m,headers) -> {
                                    return MessageBuilder.withPayload(promotionService.getPromotionById(((PayloadWrapper) m).getId())).build();
                                }).channel(replyChannel()))
                        .recipientFlow(payload -> payload instanceof PayloadWrapper &&
                                        ((PayloadWrapper) payload).getAction().equals("GET_BY_ITEM_ID"),
                                flow -> flow.handle((m,headers) -> {
                                    Long itemId = ((PayloadWrapper) m).getId();
                                    return MessageBuilder.withPayload(promotionService.getPromotionByItemId(itemId)
                                    ).build();
                                }).channel(replyChannel()))
                        .recipientFlow(payload -> payload instanceof PayloadWrapper &&
                                        ((PayloadWrapper) payload).getAction().equals("DELETE"),
                                flow -> flow.handle((m,headers) -> {
                                    PayloadWrapper payload = (PayloadWrapper) m;
                                    Long  id = payload.getId();
                                    Optional<Promotion> promotion = promotionService.getPromotionById(id);
                                    if (promotion.isPresent()) {
                                        promotionService.deletePromotion(id);
                                        return MessageBuilder.withPayload("Promotion deleted successfully").build();
                                    } else {
                                        return MessageBuilder.withPayload(promotion).build();
                                    }
                                }).channel(replyChannel()))
                        .defaultOutputToParentFlow()
                )
                .get();
    }

}