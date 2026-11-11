package com.bookstore.service;

import com.bookstore.model.Order;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Service component responsible for thread-safe CRUD persistence operations
 * storing order records in plain text Notepad format (data/orders.txt).
 * Features structured column headers, comment skipping, and robust line parsing.
 */
@Service
public class OrderStorageManager {

    public static final String FILE_HEADER =
            "# ORDER_ID | TYPE | CUSTOMER_NAME | BOOK_NAME | QUANTITY | UNIT_PRICE | DISCOUNT_RATE | PAYMENT_METHOD | ORDER_DATE";

    private static final String DEFAULT_FILE_PATH = "data/orders.txt";
    private static final String FALLBACK_FILE_PATH = "online-bookstore/data/orders.txt";

    /**
     * Initializes the service and ensures that the storage directory and file exist with headers.
     */
    @PostConstruct
    public void init() {
        ensureFileExists();
    }

    /**
     * Resolves the storage File reference, supporting execution from either project root
     * or parent repository directory.
     *
     * @return File instance pointing to orders.txt
     */
    private File getStorageFile() {
        File defaultFile = new File(DEFAULT_FILE_PATH);
        File fallbackFile = new File(FALLBACK_FILE_PATH);

        if (!defaultFile.exists() && fallbackFile.exists()) {
            return fallbackFile;
        }
        return defaultFile;
    }

    /**
     * Automatically creates the data/ directory and orders.txt file with the header line if missing or empty.
     */
    private synchronized void ensureFileExists() {
        try {
            File file = getStorageFile();
            File parentDir = file.getParentFile();
            if (parentDir != null && !parentDir.exists()) {
                parentDir.mkdirs();
            }
            if (!file.exists()) {
                file.createNewFile();
                writeHeader(file);
            } else if (file.length() == 0) {
                writeHeader(file);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to initialize orders.txt storage file: " + e.getMessage(), e);
        }
    }

    /**
     * Writes the standard header row to the storage file.
     *
     * @param file target file
     * @throws IOException on write error
     */
    private void writeHeader(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, false))) {
            writer.write(FILE_HEADER);
            writer.newLine();
        }
    }

    /**
     * Appends an order record to data/orders.txt using BufferedWriter below the header row.
     *
     * @param order the Order entity to persist
     */
    public synchronized void saveOrder(Order order) {
        if (order == null) {
            return;
        }
        ensureFileExists();
        File file = getStorageFile();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, true))) {
            writer.write(order.toFileString());
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error writing order record to " + file.getPath(), e);
        }
    }

    /**
     * Reads orders.txt line-by-line using BufferedReader, skipping header/comments (#, //)
     * and empty lines, and safely parsing valid records with Order.fromFileString.
     *
     * @return list of parsed Order objects
     */
    public synchronized List<Order> getAllOrders() {
        ensureFileExists();
        File file = getStorageFile();
        List<Order> orders = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line;
            int lineNumber = 0;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                String trimmedLine = line.trim();
                // Skip empty lines and comment headers (# or //)
                if (trimmedLine.isEmpty() || trimmedLine.startsWith("#") || trimmedLine.startsWith("//")) {
                    continue;
                }

                try {
                    Order order = Order.fromFileString(trimmedLine);
                    if (order != null) {
                        orders.add(order);
                    }
                } catch (Exception ex) {
                    System.err.println("Warning: Skipping malformed record on line " + lineNumber 
                            + " in " + file.getPath() + ": '" + trimmedLine + "'. Reason: " + ex.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error reading orders from " + file.getPath(), e);
        }

        return orders;
    }

    /**
     * Searches and retrieves an Order by its unique orderId.
     *
     * @param orderId the order identifier to search
     * @return matching Order instance, or null if not found
     */
    public synchronized Order getOrderById(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            return null;
        }

        List<Order> orders = getAllOrders();
        for (Order order : orders) {
            if (orderId.equalsIgnoreCase(order.getOrderId())) {
                return order;
            }
        }
        return null;
    }

    /**
     * Reads all records into memory, replaces the matching orderId, and rewrites orders.txt
     * while preserving the header row.
     *
     * @param updatedOrder the updated Order instance
     */
    public synchronized void updateOrder(Order updatedOrder) {
        if (updatedOrder == null || updatedOrder.getOrderId() == null) {
            return;
        }

        List<Order> orders = getAllOrders();
        boolean found = false;

        for (int i = 0; i < orders.size(); i++) {
            if (updatedOrder.getOrderId().equalsIgnoreCase(orders.get(i).getOrderId())) {
                orders.set(i, updatedOrder);
                found = true;
                break;
            }
        }

        if (found) {
            rewriteFile(orders);
        }
    }

    /**
     * Reads all records into memory, filters out the matching orderId, and rewrites orders.txt
     * while preserving the header row.
     *
     * @param orderId unique identifier of the order to remove
     */
    public synchronized void deleteOrder(String orderId) {
        if (orderId == null || orderId.trim().isEmpty()) {
            return;
        }

        List<Order> orders = getAllOrders();
        boolean removed = orders.removeIf(order -> orderId.equalsIgnoreCase(order.getOrderId()));

        if (removed) {
            rewriteFile(orders);
        }
    }

    /**
     * Helper method to overwrite orders.txt with the standard header line followed
     * by the provided list of orders using BufferedWriter.
     *
     * @param orders complete list of orders to serialize
     */
    private void rewriteFile(List<Order> orders) {
        ensureFileExists();
        File file = getStorageFile();

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, false))) {
            writer.write(FILE_HEADER);
            writer.newLine();
            for (Order order : orders) {
                writer.write(order.toFileString());
                writer.newLine();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error rewriting order storage file: " + file.getPath(), e);
        }
    }
}
