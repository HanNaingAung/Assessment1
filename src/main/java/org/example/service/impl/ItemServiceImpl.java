package org.example.service.impl;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.dto.ItemPromoDto;
import org.example.model.Item;
import org.example.model.Promotion;
import org.example.repository.ItemRepository;
import org.example.service.ItemService;
import org.example.service.PromotionService;
import org.example.service.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class ItemServiceImpl implements ItemService {

    private static final Logger serviceLogger = LogManager.getLogger("serviceLogs." + ItemServiceImpl.class.getName());

    @Autowired
    private ItemRepository itemRepository;

    @Autowired
    private PromotionService promotionService;

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public List<Item> getAllItems() {
        String objectName = "Items";
        serviceLogger.info("[START] : >>> --- Transaction start for fetching " + objectName + " information's.  ---");
        List<Item> resultList = itemRepository.findAll();
        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for fetching " + objectName + " information's. ---");
        return resultList;
    }

    @Override
    public Page<Item> getAllItemsWithPagination(Pageable pageable) {
        String objectName = "Items";
        serviceLogger.info("[START] : >>> --- Transaction start for fetching " + objectName + " information's with pagination.  ---");
        Page<Item> resultList = itemRepository.findAll(pageable);
        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for fetching " + objectName + " information's. ---");
        return resultList;
    }

    @Override
    public Optional<Item> getItemById(Long id) {
        String objectName = "Item";
        serviceLogger.info("[START] : >>> --- Transaction start for fetching " + objectName + " information's by id "+ id+ ". ---");
        Optional<Item> result = itemRepository.findById(id);
        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for fetching " + objectName + " information's. ---");
        return result;
    }

    @Override
    public List<ItemPromoDto> getAllItemsWithPromotions() {
        String objectName = "Items";
        serviceLogger.info("[START] : >>> --- Transaction start for fetching " + objectName + " information's with promotions. ---");

        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.lookup("promotions", "_id", "itemId", "promotions"),
                Aggregation.project(Fields.from(Fields.field("items", "$$ROOT"))).andInclude("promotions")
        );

        AggregationResults<ItemPromoDto> results = mongoTemplate.aggregate(aggregation, "items", ItemPromoDto.class);

        List<ItemPromoDto> resultList = results.getMappedResults();

        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for fetching " + objectName + " information's with promotions. ---");
        return resultList;
    }

    @Override
    public Optional<ItemPromoDto> getItemWithPromotionsById(Long id) {
        String objectName = "Item";
        serviceLogger.info("[START] : >>> --- Transaction start for fetching " + objectName + " information's with promotion by id "+ id+ " ---");
        Optional<Item> item = itemRepository.findById(id);
        Optional<ItemPromoDto>  result = item.map(s -> new ItemPromoDto(s, getPromotionsForItem(s.getId())));
        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for fetching " + objectName + " information's with promotion. ---");
        return result;
    }


    @Override
    public Item createItem(Item item) {
        String objectName = "Item";
        serviceLogger.info("[START] : >>> --- Transaction start for registering new " + objectName + " information's. ---");

        Long id = sequenceGeneratorService.generateSequence("item_sequence");
        item.setId(id);
        item.setCreatedAt(new Date());
        item.setUpdatedAt(new Date());
        Item result = itemRepository.save(item);

        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for registering new " + objectName + " information's. ---");
        return result;
    }

    @Override
    public Item updateItem(Long id, Item item) {
        String objectName = "Item";
        serviceLogger.info("[START] : >>> --- Transaction start for updating " + objectName + " information's. ---");

        Item existingItem = itemRepository.findById(id).orElseThrow();

        // Update only the fields that are not null or empty
        updateIfNotNull(existingItem, item, Item::getName, i -> i.setName(item.getName()));
        updateIfNotNull(existingItem, item, Item::getDuration, i -> i.setDuration(item.getDuration()));
        updateIfNotNull(existingItem, item, Item::getDistance, i -> i.setDistance(item.getDistance()));
        updateIfNotNull(existingItem, item, Item::getRating, i -> i.setRating(item.getRating()));
        updateIfNotNull(existingItem, item, Item::getDeliveryFee, i -> i.setDeliveryFee(item.getDeliveryFee()));
        updateIfNotNull(existingItem, item, Item::getCategories, i -> i.setCategories(item.getCategories()));
        updateIfNotNull(existingItem, item, Item::getImageUrl, i -> i.setImageUrl(item.getImageUrl()));

        existingItem.setUpdatedAt(new Date());
        itemRepository.save(existingItem);

        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for updating " + objectName + " information's. ---");
        return existingItem;

    }

    @Override
    public void deleteItem(Long id) {
        String objectName = "Item";
        serviceLogger.info("[START] : >>> --- Transaction start for removing " + objectName + " information's. ---");

        itemRepository.deleteById(id);

        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for removing " + objectName + " information's. ---");
    }

    private List<Promotion> getPromotionsForItem(Long itemId) {
        String objectName = "Item";
        serviceLogger.info("[START] : >>> --- Transaction start for fetching " + objectName + " information's by itemId "+ itemId+ "---");

        List<Promotion> promotionList =  promotionService.getAllPromotions()
                .stream()
                .filter(promotion -> itemId.equals(promotion.getItemId()))
                .collect(Collectors.toList());

        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for fetching " + objectName + " information's. ---");
        return promotionList;
    }

    private void updateIfNotNull(Item existingItem, Item item, Function<Item, ?> getter, Consumer<Item> setter) {
        Object value = getter.apply(item);
        if (value != null) {
            if (value instanceof String) {
                String stringValue = (String) value;
                if (!stringValue.trim().isEmpty()) {
                    setter.accept(existingItem);
                }
            } else if (value instanceof Collection) {
                Collection<?> collectionValue = (Collection<?>) value;
                if (!collectionValue.isEmpty()) {
                    setter.accept(existingItem);
                }
            } else {
                setter.accept(existingItem);
            }
        }
    }
}
