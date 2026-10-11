package com.bookstore.service;

import com.bookstore.dto.InventoryForm;
import com.bookstore.model.InventoryItem;
import com.bookstore.model.InventorySummary;

import java.util.List;

public interface InventoryService {
    List<InventoryItem> getAll();

    InventoryItem getById(String itemId);

    List<InventoryItem> search(String keyword, String type, String status);

    InventorySummary getSummary();

    List<InventoryItem> getItemsNeedingAttention(int limit);

    InventoryItem create(InventoryForm form);

    InventoryItem update(String itemId, InventoryForm form);

    InventoryItem restock(String itemId, int amount);

    InventoryItem deduct(String itemId, int amount);

    InventoryItem delete(String itemId);

    InventoryForm toForm(InventoryItem item);
}
