package org.example.service;


import org.example.model.Promotion;

import java.util.List;
import java.util.Optional;

public interface PromotionService {
    List<Promotion> getAllPromotions();
    Optional<Promotion> getPromotionById(Long id);
    Optional<Promotion> getPromotionByItemId(Long itemId);
    Promotion createPromotion(Promotion promotion);
    Promotion updatePromotion(Long id, Promotion promotion);
    void deletePromotion(Long id);
}
