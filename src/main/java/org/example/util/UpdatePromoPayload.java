package org.example.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.example.model.Promotion;
import org.example.service.PromotionService;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UpdatePromoPayload {
    private Long id;
    private Promotion promotion;
}
