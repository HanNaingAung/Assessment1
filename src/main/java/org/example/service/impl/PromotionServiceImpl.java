package org.example.service.impl;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.model.Promotion;
import org.example.repository.PromotionRepository;
import org.example.service.PromotionService;
import org.example.service.SequenceGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class  PromotionServiceImpl implements PromotionService {

    private static final Logger serviceLogger = LogManager.getLogger("serviceLogs." + ItemServiceImpl.class.getName());
    @Autowired
    private PromotionRepository promotionRepository;
    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @Override
    public List<Promotion> getAllPromotions() {
        String objectName = "Promotions";
        serviceLogger.info("[START] : >>> --- Transaction start for fetching " + objectName + " information's.  ---");
        List<Promotion> resultList = promotionRepository.findAll();
        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for fetching " + objectName + " information's. ---");
        return resultList;
    }

    @Override
    public Optional<Promotion> getPromotionById(Long id) {
        String objectName = "Promotion";
        serviceLogger.info("[START] : >>> --- Transaction start for fetching " + objectName + " information's by id "+ id+ ". ---");
        Optional<Promotion> result =  promotionRepository.findById(id);
        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for fetching " + objectName + " information's. ---");
        return result;
    }

    @Override
    public Optional<Promotion> getPromotionByItemId(Long itemId) {
        String objectName = "Promotion";
        serviceLogger.info("[START] : >>> --- Transaction start for fetching " + objectName + " information's by item id "+ itemId+ ". ---");
        Optional<Promotion> result =  promotionRepository.findByItemId(itemId);
        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for fetching " + objectName + " information's. ---");
        return result;
    }

    @Override
    public Promotion createPromotion(Promotion promotion) {
        String objectName = "Promotion";
        serviceLogger.info("[START] : >>> --- Transaction start for registering new " + objectName + " information's. ---");

        Long id = sequenceGeneratorService.generatePromoSequence("promo_sequence");
        promotion.setId(id);
        promotion.setCreatedAt(convertToDate(LocalDateTime.now()));
        promotion.setUpdatedAt(convertToDate(LocalDateTime.now()));
        Promotion result = promotionRepository.save(promotion);

        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for registering new " + objectName + " information's. ---");
        return result;
    }

    @Override
    public Promotion updatePromotion(Long id, Promotion promotion) {
        String objectName = "Promotion";
        serviceLogger.info("[START] : >>> --- Transaction start for updating " + objectName + " information's. ---");

        promotion.setUpdatedAt(convertToDate(LocalDateTime.now()));
        promotionRepository.save(promotion);

        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for updating " + objectName + " information's. ---");
        return promotion;
    }

    @Override
    public void deletePromotion(Long id) {
        String objectName = "Promotion";
        serviceLogger.info("[START] : >>> --- Transaction start for removing " + objectName + " information's. ---");

        promotionRepository.deleteById(id);

        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for removing " + objectName + " information's. ---");
    }

    private Date convertToDate(LocalDateTime dateToConvert) {
        return Date.from(dateToConvert.atZone(ZoneId.systemDefault()).toInstant());
    }
}
