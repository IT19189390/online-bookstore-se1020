package com.bookstore.exception;

public class InventoryNotFoundException extends RuntimeException {
    public InventoryNotFoundException(String itemId) {
        super("No stock item with ID " + itemId + " was found.");
    }
}
