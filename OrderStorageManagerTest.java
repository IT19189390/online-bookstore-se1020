package com.bookstore.service;

import com.bookstore.model.DiscountedOrder;
import com.bookstore.model.Order;
import com.bookstore.model.StandardOrder;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class OrderStorageManagerTest {

    private OrderStorageManager storageManager;
    private File ordersFile;

    @BeforeEach
    void setUp() {
        ordersFile = new File("data/orders.txt");
        if (ordersFile.exists()) {
            ordersFile.delete();
        }
        storageManager = new OrderStorageManager();
        storageManager.init();
    }

    @AfterEach
    void tearDown() {
        if (ordersFile.exists()) {
            ordersFile.delete();
        }
    }

    @Test
    void testHeaderInitialized() throws IOException {
        assertTrue(ordersFile.exists());
        try (BufferedReader reader = new BufferedReader(new FileReader(ordersFile, StandardCharsets.UTF_8))) {
            String firstLine = reader.readLine();
            assertNotNull(firstLine);
            assertEquals(OrderStorageManager.FILE_HEADER, firstLine);
        }
    }

    @Test
    void testSaveAndGetAllOrdersWithFormattedOutput() throws IOException {
        Order std = new StandardOrder("ORD-1001", "Alice Smith", "Effective Java", 2, 45.0, "Credit Card", "2026-10-09");
        Order disc = new DiscountedOrder("ORD-1002", "Bob Jones", "Clean Code", 1, 40.0, "PayPal", "2026-10-09", 10.0);

        storageManager.saveOrder(std);
        storageManager.saveOrder(disc);

        List<Order> orders = storageManager.getAllOrders();
        assertEquals(2, orders.size());

        // Verify exact pipe formatting
        assertEquals("ORD-1001|STANDARD|Alice Smith|Effective Java|2|45.00|0.0|Credit Card|2026-10-09", std.toFileString());
        assertEquals("ORD-1002|DISCOUNTED|Bob Jones|Clean Code|1|40.00|10.0|PayPal|2026-10-09", disc.toFileString());

        Order fetched1 = storageManager.getOrderById("ORD-1001");
        assertNotNull(fetched1);
        assertInstanceOf(StandardOrder.class, fetched1);
        assertEquals(90.0, fetched1.calculateFinalTotal());

        Order fetched2 = storageManager.getOrderById("ORD-1002");
        assertNotNull(fetched2);
        assertInstanceOf(DiscountedOrder.class, fetched2);
        assertEquals(36.0, fetched2.calculateFinalTotal());
    }

    @Test
    void testSkipCommentsAndMalformedLines() throws IOException {
        try (FileWriter writer = new FileWriter(ordersFile, StandardCharsets.UTF_8, true)) {
            writer.write("# This is a comment\n");
            writer.write("// Another comment style\n");
            writer.write("\n");
            writer.write("ORD-1003|STANDARD|Charlie|Spring in Action|1|38.00|0.0|Cash|2026-10-09\n");
            writer.write("INVALID|MALFORMED|RECORD\n"); // Corrupted line should be skipped safely
        }

        List<Order> orders = storageManager.getAllOrders();
        assertEquals(1, orders.size());
        assertEquals("ORD-1003", orders.get(0).getOrderId());
    }

    @Test
    void testUpdatePreservesHeader() throws IOException {
        Order std = new StandardOrder("ORD-1004", "David", "Head First Java", 1, 32.0, "Cash", "2026-10-09");
        storageManager.saveOrder(std);

        Order updated = new StandardOrder("ORD-1004", "David Brown", "Head First Java 3rd Ed", 2, 32.0, "Debit Card", "2026-10-10");
        storageManager.updateOrder(updated);

        try (BufferedReader reader = new BufferedReader(new FileReader(ordersFile, StandardCharsets.UTF_8))) {
            String firstLine = reader.readLine();
            assertEquals(OrderStorageManager.FILE_HEADER, firstLine);
        }

        Order result = storageManager.getOrderById("ORD-1004");
        assertNotNull(result);
        assertEquals("David Brown", result.getCustomerName());
        assertEquals(2, result.getQuantity());
    }

    @Test
    void testDeletePreservesHeader() throws IOException {
        Order std = new StandardOrder("ORD-1005", "Emma", "Refactoring", 1, 55.0, "Credit Card", "2026-10-09");
        storageManager.saveOrder(std);
        assertEquals(1, storageManager.getAllOrders().size());

        storageManager.deleteOrder("ORD-1005");
        assertEquals(0, storageManager.getAllOrders().size());

        try (BufferedReader reader = new BufferedReader(new FileReader(ordersFile, StandardCharsets.UTF_8))) {
            String firstLine = reader.readLine();
            assertEquals(OrderStorageManager.FILE_HEADER, firstLine);
        }
    }
}
