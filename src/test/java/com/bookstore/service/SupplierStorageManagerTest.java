package com.bookstore.service;
import com.bookstore.model.InternationalSupplier;
import com.bookstore.model.LocalSupplier;
import com.bookstore.model.Supplier;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SupplierStorageManagerTest {
    private final File suppliersFile = new File("data/suppliers.txt");
    private SupplierStorageManager storageManager;

    @BeforeEach
    void setUp() {
        suppliersFile.delete();
        storageManager = new SupplierStorageManager();
        storageManager.init();
    }

    @AfterEach
    void tearDown() {
        suppliersFile.delete();
    }

    @Test
    void serializesAndParsesBothSupplierTypes() {
        Supplier local = new LocalSupplier("SUP-1", "Local Name", "555", "local@example.com", "Local Co", "2026-10-09", "BR-1");
        Supplier international = new InternationalSupplier("SUP-2", "Global Name", "777", "global@example.com", "Global Co", "2026-10-09", "Canada", "CA-2");

        assertEquals("SUP-1|LOCAL|Local Name|555|local@example.com|Local Co|2026-10-09|BR-1", local.toFileString());
        assertEquals("SUP-2|INTERNATIONAL|Global Name|777|global@example.com|Global Co|2026-10-09|Canada:CA-2", international.toFileString());
        assertInstanceOf(LocalSupplier.class, Supplier.fromFileString(local.toFileString()));
        assertInstanceOf(InternationalSupplier.class, Supplier.fromFileString(international.toFileString()));
    }

    @Test
    void persistsAndUpdatesSuppliers() {
        storageManager.saveSupplier(new LocalSupplier("SUP-1", "Name", "555", "name@example.com", "Co", "2026-10-09", "BR-1"));
        assertEquals(1, storageManager.getAllSuppliers().size());

        storageManager.updateSupplier(new LocalSupplier("SUP-1", "Updated", "555", "name@example.com", "Co", "2026-10-09", "BR-1"));
        List<Supplier> suppliers = storageManager.getAllSuppliers();
        assertEquals("Updated", suppliers.get(0).getSupplierName());

        storageManager.deleteSupplier("SUP-1");
        assertTrue(storageManager.getAllSuppliers().isEmpty());
    }

    @Test
    void updateCanChangeSupplierType() {
        storageManager.saveSupplier(new LocalSupplier("SUP-1", "Name", "555", "name@example.com", "Co", "2026-10-09", "BR-1"));

        storageManager.updateSupplier(new InternationalSupplier("SUP-1", "Name", "555", "name@example.com", "Co", "2026-10-09", "Canada", "CA-2"));

        Supplier updatedSupplier = storageManager.getSupplierById("SUP-1");
        assertInstanceOf(InternationalSupplier.class, updatedSupplier);
        assertEquals("International Supplier (Canada)", updatedSupplier.getSupplierCategory());
    }
}
