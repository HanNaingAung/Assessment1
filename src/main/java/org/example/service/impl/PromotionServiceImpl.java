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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

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

        Promotion existingPromo = promotionRepository.findById(id).orElseThrow();

        // Update only the fields that are not null or empty
        updateIfNotNull(existingPromo, promotion, Promotion::getItemId, i -> i.setItemId(promotion.getItemId()));
        updateIfNotNull(existingPromo, promotion, Promotion::getDescription, i -> i.setDescription(promotion.getDescription()));
        updateIfNotNull(existingPromo, promotion, Promotion::getStartDate, i -> i.setStartDate(promotion.getStartDate()));
        updateIfNotNull(existingPromo, promotion, Promotion::getEndDate, i -> i.setEndDate(promotion.getEndDate()));
        updateIfNotNull(existingPromo, promotion, Promotion::getPromoteFrom, i -> i.setPromoteFrom(promotion.getPromoteFrom()));

        existingPromo.setUpdatedAt(convertToDate(LocalDateTime.now()));
        promotionRepository.save(existingPromo);

        serviceLogger.info("[FINISH] : >>> --- Transaction finished successfully for updating " + objectName + " information's. ---");
        return existingPromo;
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

    private void updateIfNotNull(Promotion existingPromo, Promotion promo, Function<Promotion, ?> getter, Consumer<Promotion> setter) {
        Object value = getter.apply(promo);
        if (value != null) {
            if (value instanceof String) {
                String stringValue = (String) value;
                if (!stringValue.trim().isEmpty()) {
                    setter.accept(existingPromo);
                }
            } else if (value instanceof Collection) {
                Collection<?> collectionValue = (Collection<?>) value;
                if (!collectionValue.isEmpty()) {
                    setter.accept(existingPromo);
                }
            } else {
                setter.accept(existingPromo);
            }
        }
    }
}
