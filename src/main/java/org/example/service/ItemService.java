package org.example.service;

import org.example.dto.ItemPromoDto;
import org.example.model.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;


public interface ItemService {
    List<Item> getAllItems();
    Page<Item> getAllItemsWithPagination(Pageable pageable);
    Optional<Item> getItemById(Long id);
    List<ItemPromoDto> getAllItemsWithPromotions();
    Optional<ItemPromoDto> getItemWithPromotionsById(Long id);
    Item createItem(Item item);
    Item updateItem(Long id, Item item);
    void deleteItem(Long id);
}
