package org.example.config;

import org.apache.tomcat.util.http.fileupload.UploadContext;
import org.example.model.Item;
import org.example.service.ItemService;
import org.example.util.PayloadWrapper;
import org.example.util.UpdateItemPayload;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.integration.channel.QueueChannel;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.dsl.MessageChannels;
import org.springframework.integration.http.dsl.Http;
import org.springframework.integration.support.MessageBuilder;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;

import java.util.Optional;

@Configuration
public class ItemIntegrationConfig {

    @Autowired
    private ItemService itemService;

    @Bean
    public MessageChannel replyItemChannel() {
        return new QueueChannel();
    }

    @Bean
    public MessageChannel itemChannel() {
        return MessageChannels.direct().get();
    }

    @Bean
    public IntegrationFlow handleFlow() {
        return IntegrationFlows.from("itemChannel")
                .routeToRecipients(router -> router
                        .recipientFlow(payload -> payload instanceof PayloadWrapper &&
                                        ((PayloadWrapper) payload).getAction().equals("CREATE"),
                                flow -> flow.handle(m -> {
                                    PayloadWrapper payload = (PayloadWrapper) m.getPayload();
                                    Item item = (Item)payload.getEntity();
                                    itemService.createItem(item);
                                }))
                        .recipientFlow(payload -> payload instanceof PayloadWrapper &&
                                        ((PayloadWrapper) payload).getAction().equals("UPDATE"),
                                flow -> flow.handle((m,headers) -> {
                                    PayloadWrapper payload = (PayloadWrapper) m;
                                    UpdateItemPayload updateItem = (UpdateItemPayload)payload.getEntity();
                                    Optional<Item> item = itemService.getItemById(updateItem.getId());
                                    if (item.isPresent()) {
                                        Item newItem = updateItem.getItem();
                                        newItem.setId(updateItem.getId());
                                        return MessageBuilder.withPayload(itemService.updateItem(updateItem.getId(), newItem)).build();
                                    } else {
                                        return MessageBuilder.withPayload(item).build();
                                    }
                                }).channel(replyItemChannel()))
                        .recipientFlow(payload -> payload instanceof PayloadWrapper &&
                                        ((PayloadWrapper) payload).getAction().equals("GET"),
                                flow -> flow.handle((m,headers) -> {
                                     return MessageBuilder.withPayload(itemService.getAllItems()).build();
                                }).channel(replyItemChannel()))
                        .recipientFlow(payload -> payload instanceof PayloadWrapper &&
                                        ((PayloadWrapper) payload).getAction().equals("GET_BY_ID"),
                                flow -> flow.handle((m,headers) -> {
                                    return MessageBuilder.withPayload(itemService.getItemById(((PayloadWrapper) m).getId())).build();
                                }).channel(replyItemChannel()))
                        .recipientFlow(payload -> payload instanceof PayloadWrapper &&
                                        ((PayloadWrapper) payload).getAction().equals("GET_WITH_PROMO"),
                                flow -> flow.handle((m,headers) -> {
                                    return MessageBuilder.withPayload(itemService.getAllItemsWithPromotions()).build();
                                }).channel(replyItemChannel()))
                        .recipientFlow(payload -> payload instanceof PayloadWrapper &&
                                        ((PayloadWrapper) payload).getAction().equals("GET_WITH_PROMO_BY_ID"),
                                flow -> flow.handle((m,headers) -> {
                                    return MessageBuilder.withPayload(itemService.getItemWithPromotionsById(((PayloadWrapper)m).getId())).build();
                                }).channel(replyItemChannel()))
                        .recipientFlow(payload -> payload instanceof PayloadWrapper &&
                                        ((PayloadWrapper) payload).getAction().equals("DELETE"),
                                flow -> flow.handle((m,headers) -> {
                                    PayloadWrapper payload = (PayloadWrapper) m;
                                    Long  id = payload.getId();
                                    Optional<Item> item = itemService.getItemById(id);
                                    if (item.isPresent()) {
                                        itemService.deleteItem(id);
                                        return MessageBuilder.withPayload("Item deleted successfully").build();
                                    } else {
                                        return MessageBuilder.withPayload(item).build();
                                    }
                                }).channel(replyItemChannel()))
                        .defaultOutputToParentFlow()
                )
                .get();
    }

}
