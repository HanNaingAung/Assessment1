package org.example.config;


import org.example.model.Item;
import org.example.model.Promotion;
import org.example.service.PromotionService;
import org.example.util.PayloadWrapper;
import org.example.util.UpdateItemPayload;
import org.example.util.UpdatePromoPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import javax.swing.*;
import java.util.Optional;
import java.util.stream.Collectors;

@Configuration
@EnableIntegration
public class PromotionIntegrationConfig {

    @Autowired
    private PromotionService promotionService;
   /* @Bean
    public IntegrationFlow promotionFlow() {
        return IntegrationFlows.from(Http.inboundGateway("/api/promotions")
                        .requestMapping(m -> m.methods(HttpMethod.GET, HttpMethod.POST, HttpMethod.PUT, HttpMethod.DELETE))
                        .requestPayloadType(String.class))
                .<String, Message<String>>transform(p -> MessageBuilder.withPayload(p).build())
                .route(Message.class, message -> message.getHeaders().get("http_requestMethod").toString(),
                        mapping -> mapping
                                .subFlowMapping(HttpMethod.GET.name(), sf -> sf.channel("getPromotionChannel"))
                                .subFlowMapping(HttpMethod.GET.name(), sf -> sf.channel("getPromotionByIdChannel"))
                                .subFlowMapping(HttpMethod.GET.name(), sf -> sf.channel("getPromotionByStoreIdChannel"))
                                .subFlowMapping(HttpMethod.POST.name(), sf -> sf.channel("postPromotionChannel"))
                                .subFlowMapping(HttpMethod.PUT.name(), sf -> sf.channel("putPromotionChannel"))
                                .subFlowMapping(HttpMethod.DELETE.name(), sf -> sf.channel("deletePromotionChannel")))
                .get();
    }

    @Bean
    public MessageChannel getAllPromotionsChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public MessageChannel getPromotionChannel(){
        return  MessageChannels.direct().get();
    }

    @Bean
    public MessageChannel getPromotionByIdChannel(){
        return  MessageChannels.direct().get();
    }

    @Bean
    public MessageChannel getPromotionByStoreIdChannel(){
        return  MessageChannels.direct().get();
    }
    @Bean
    public MessageChannel postPromotionChannel(){
        return  MessageChannels.direct().get();
    }
    @Bean
    public MessageChannel putPromotionChannel(){
        return  MessageChannels.direct().get();
    }
    @Bean
    public MessageChannel deletePromotionChannel(){
        return  MessageChannels.direct().get();
    }

    @Bean
    public MessageChannel replyChannel() {
        return new QueueChannel();
    }

    @Bean
    public IntegrationFlow getAllPromotionsFlow() {
        return IntegrationFlows.from(getAllPromotionsChannel())
                .handle((payload, headers) -> promotionService.getAllPromotions())
                .channel(replyChannel())
                .get();
    }

    @Bean
    public IntegrationFlow handleGetPromotions() {
        return IntegrationFlows.from("getPromotionChannel")
                .handle((payload, headers) -> promotionService.getAllPromotions())
                .channel(replyChannel())
                .get();
    }

    @Bean
    public IntegrationFlow handleGetPromotionById() {
        return IntegrationFlows.from("getPromotionByIdChannel")
                .handle((payload, headers) -> promotionService.getPromotionById((Long) payload))
                .channel(replyChannel())
                .get();
    }

    @Bean
    public IntegrationFlow handleGetPromotionByStoreId() {
        return IntegrationFlows.from("getPromotionByStoreIdChannel")
                .handle((payload, headers) -> {
                    return promotionService.getAllPromotions()
                            .stream()
                            .filter(promotion -> payload.equals(promotion.getItemId()))
                            .collect(Collectors.toList());
                })
                .channel(replyChannel())
                .get();

    }

    @Bean
    public IntegrationFlow handlePostPromotion() {
        return IntegrationFlows.from("postPromotionChannel")
                .handle((payload, headers) -> {
                    Promotion Promotion = (Promotion)payload;
                    return promotionService.createPromotion(Promotion);
                })
                .channel(replyChannel())
                .get();
    }

    @Bean
    public IntegrationFlow handlePutPromotion() {
        return IntegrationFlows.from("putPromotionChannel")
                .handle((payload, headers) -> {
                    UpdatePromoPayload updatePromotion = (UpdatePromoPayload) payload;
                    Optional<Promotion> Promotion = promotionService.getPromotionById(updatePromotion.getId());
                    if (Promotion.isPresent()) {
                        return MessageBuilder.withPayload(promotionService.updatePromotion(updatePromotion.getId(), updatePromotion.getPromotion())).build();
                    } else {
                        return MessageBuilder.withPayload(Promotion).build();
                    }
                })
                .channel(replyChannel())
                .get();
    }

    @Bean
    public IntegrationFlow handleDeletePromotion() {
        return IntegrationFlows.from("deletePromotionChannel")
                .handle((payload, headers) -> {
                    promotionService.deletePromotion((Long)payload);
                    return MessageBuilder.withPayload("Promotion deleted successfully").build();
                })
                .channel(replyChannel())
                .get();
    }*/

    //================================New Features===============================================================//
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