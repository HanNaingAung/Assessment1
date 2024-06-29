package org.example.service;

import org.example.dto.ItemPromoDto;
import org.example.model.Item;

import java.util.List;
import java.util.Optional;


public interface ItemService {
    List<Item> getAllItems();
    Optional<Item> getItemById(Long id);
    List<ItemPromoDto> getAllItemsWithPromotions();
    Optional<ItemPromoDto> getItemWithPromotionsById(Long id);
    Item createItem(Item item);
    Item updateItem(Long id, Item item);
    void deleteItem(Long id);
}
