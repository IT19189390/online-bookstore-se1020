package com.bookstore.service.impl;

import com.bookstore.dto.InventoryForm;
import com.bookstore.exception.DuplicateStockException;
import com.bookstore.exception.InventoryNotFoundException;
import com.bookstore.model.EBookStock;
import com.bookstore.model.InventoryItem;
import com.bookstore.model.InventorySummary;
import com.bookstore.model.PrintedBookStock;
import com.bookstore.repository.InventoryRepository;
import com.bookstore.service.InventoryService;
import com.bookstore.util.IsbnUtils;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

@Service
public class InventoryServiceImpl implements InventoryService {
    private final InventoryRepository repository;

    public InventoryServiceImpl(InventoryRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<InventoryItem> getAll() {
        return repository.findAll().stream()
                .sorted(Comparator.comparing(InventoryItem::getItemId))
                .toList();
    }

    @Override
    public InventoryItem getById(String itemId) {
        return repository.findById(itemId)
                .orElseThrow(() -> new InventoryNotFoundException(itemId));
    }

    @Override
    public List<InventoryItem> search(String keyword, String type, String status) {
        String kw = keyword == null ? "" : keyword.trim().toLowerCase(Locale.ROOT);
        String kwIsbn = IsbnUtils.normalize(kw);

        return getAll().stream()
                .filter(i -> kw.isEmpty()
                        || i.getItemId().toLowerCase(Locale.ROOT).contains(kw)
                        || i.getBookTitle().toLowerCase(Locale.ROOT).contains(kw)
                        || (!kwIsbn.isEmpty() && IsbnUtils.normalize(i.getIsbn()).contains(kwIsbn)))
                .filter(i -> isBlank(type) || i.getItemType().equals(type))
                .filter(i -> isBlank(status) || i.getStockStatus().name().equals(status))
                .toList();
    }

    @Override
    public InventorySummary getSummary() {
        return new InventorySummary(repository.findAll());
    }

    @Override
    public List<InventoryItem> getItemsNeedingAttention(int limit) {
        return getAll().stream()
                .filter(InventoryItem::needsReorder)
                .sorted(Comparator.comparingInt(InventoryItem::getQuantity))
                .limit(limit)
                .toList();
    }

    @Override
    public InventoryItem create(InventoryForm form) {
        ensureNoDuplicate(form.getIsbn(), form.getItemType(), null);
        InventoryItem item = buildItem(repository.nextId(), form);
        repository.save(item);
        return item;
    }

    @Override
    public InventoryItem update(String itemId, InventoryForm form) {
        InventoryItem existing = getById(itemId);
        form.setItemType(existing.getItemType());
        ensureNoDuplicate(form.getIsbn(), form.getItemType(), existing.getItemId());
        InventoryItem updated = buildItem(existing.getItemId(), form);
        repository.save(updated);
        return updated;
    }

    @Override
    public InventoryItem restock(String itemId, int amount) {
        InventoryItem item = getById(itemId);
        item.restock(amount);
        repository.save(item);
        return item;
    }

    @Override
    public InventoryItem deduct(String itemId, int amount) {
        InventoryItem item = getById(itemId);
        item.deduct(amount);
        repository.save(item);
        return item;
    }

    @Override
    public InventoryItem delete(String itemId) {
        InventoryItem item = getById(itemId);
        repository.deleteById(item.getItemId());
        return item;
    }

    @Override
    public InventoryForm toForm(InventoryItem item) {
        InventoryForm form = new InventoryForm();
        form.setItemType(item.getItemType());
        form.setIsbn(item.getIsbn());
        form.setBookTitle(item.getBookTitle());
        form.setQuantity(item.getQuantity());
        form.setReorderLevel(item.getReorderLevel());
        form.setUnitCost(item.getUnitCost());

        if (item instanceof PrintedBookStock printed) {
            form.setWarehouse(printed.getWarehouse());
            form.setShelfCode(printed.getShelfCode());
        } else if (item instanceof EBookStock ebook) {
            form.setPlatform(ebook.getPlatform());
            form.setLicenseExpiry(ebook.getLicenseExpiry());
        }
        return form;
    }

    private InventoryItem buildItem(String itemId, InventoryForm f) {
        String type = f.getItemType() == null ? "" : f.getItemType();
        return switch (type) {
            case PrintedBookStock.TYPE -> new PrintedBookStock(itemId, trim(f.getIsbn()), trim(f.getBookTitle()),
                    f.getQuantity(), f.getReorderLevel(), f.getUnitCost(), LocalDate.now(),
                    trim(f.getWarehouse()), trim(f.getShelfCode()).toUpperCase(Locale.ROOT));
            case EBookStock.TYPE -> new EBookStock(itemId, trim(f.getIsbn()), trim(f.getBookTitle()),
                    f.getQuantity(), f.getReorderLevel(), f.getUnitCost(), LocalDate.now(),
                    trim(f.getPlatform()), f.getLicenseExpiry());
            default -> throw new IllegalArgumentException("Choose printed book or e-book.");
        };
    }

    private void ensureNoDuplicate(String isbn, String type, String excludeId) {
        String target = IsbnUtils.normalize(isbn);
        boolean exists = repository.findAll().stream()
                .anyMatch(i -> i.getItemType().equals(type)
                        && IsbnUtils.normalize(i.getIsbn()).equals(target)
                        && !i.getItemId().equalsIgnoreCase(excludeId == null ? "" : excludeId));
        if (exists) {
            throw new DuplicateStockException("This ISBN is already in stock as the same type. "
                    + "Edit the existing item or restock it instead.");
        }
    }

    private static String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
