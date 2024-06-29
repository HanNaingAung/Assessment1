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
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.Fields;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
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

//    @Override
//    public List<ItemPromoDto> getAllItemsWithPromotions() {
//        String objectName = "Items";
//        serviceLogger.info("[START] : >>> --- Transaction start for fetching " + objectName + " information's with promotions. ---");
//        List<Item> items = itemRepository.findAll();
//        List<ItemPromoDto> resultList =  items.stream()
//                .map(Item -> new ItemPromoDto(Item, getPromotionsForItem(Item.getId())))
//                .collect(Collectors.toList());
//        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for fetching " + objectName + " information's with promotions. ---");
//        return resultList;
//    }

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

        item.setUpdatedAt(new Date());
        itemRepository.save(item);

        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for updating " + objectName + " information's. ---");
        return item;

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

        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for removing " + objectName + " information's. ---");
        return promotionList;
    }



}
