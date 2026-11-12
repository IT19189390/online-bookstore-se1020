package com.bookstore.service;

import com.bookstore.model.Supplier;
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
import java.util.Locale;

@Service
public class SupplierStorageManager {
    public static final String FILE_HEADER = "# SUPPLIER_ID | TYPE | SUPPLIER_NAME | PHONE | EMAIL | COMPANY | REG_DATE | EXTRA_DETAILS";
    private static final String FILE_PATH = "data/suppliers.txt";

    @PostConstruct
    public void init() {
        ensureFileExists();
    }

    private File storageFile() {
        return new File(FILE_PATH);
    }

    private synchronized void ensureFileExists() {
        try {
            File file = storageFile();
            File parent = file.getParentFile();
            if (parent != null && !parent.exists() && !parent.mkdirs()) {
                throw new IOException("Unable to create " + parent);
            }
            if (!file.exists()) {
                file.createNewFile();
                writeHeader(file);
            } else if (file.length() == 0) {
                writeHeader(file);
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to initialize suppliers.txt", exception);
        }
    }

    private void writeHeader(File file) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(file, StandardCharsets.UTF_8, false))) {
            writer.write(FILE_HEADER);
            writer.newLine();
        }
    }

    public synchronized void saveSupplier(Supplier supplier) {
        if (supplier == null) return;
        ensureFileExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(storageFile(), StandardCharsets.UTF_8, true))) {
            writer.write(supplier.toFileString());
            writer.newLine();
        } catch (IOException exception) {
            throw new IllegalStateException("Error writing supplier record", exception);
        }
    }

    public synchronized List<Supplier> getAllSuppliers() {
        ensureFileExists();
        List<Supplier> suppliers = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(storageFile(), StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                try {
                    Supplier supplier = Supplier.fromFileString(line);
                    if (supplier != null) suppliers.add(supplier);
                } catch (IllegalArgumentException exception) {
                    System.err.println("Skipping malformed supplier record: " + exception.getMessage());
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Error reading suppliers.txt", exception);
        }
        return suppliers;
    }

    public synchronized Supplier getSupplierById(String supplierId) {
        if (supplierId == null || supplierId.trim().isEmpty()) return null;
        return getAllSuppliers().stream()
                .filter(supplier -> supplierId.equalsIgnoreCase(supplier.getSupplierId()))
                .findFirst().orElse(null);
    }

        public synchronized List<Supplier> searchSuppliers(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) return new ArrayList<>();
        String normalizedKeyword = keyword.trim().toLowerCase(Locale.ROOT);
        return getAllSuppliers().stream()
            .filter(supplier -> supplier.getSupplierName().toLowerCase(Locale.ROOT).contains(normalizedKeyword)
                || supplier.getCompanyName().toLowerCase(Locale.ROOT).contains(normalizedKeyword))
            .toList();
        }

    public synchronized void updateSupplier(Supplier updatedSupplier) {
        if (updatedSupplier == null || updatedSupplier.getSupplierId() == null) return;
        List<Supplier> suppliers = getAllSuppliers();
        for (int index = 0; index < suppliers.size(); index++) {
            if (updatedSupplier.getSupplierId().equalsIgnoreCase(suppliers.get(index).getSupplierId())) {
                suppliers.set(index, updatedSupplier);
                rewriteFile(suppliers);
                return;
            }
        }
    }

    public synchronized void deleteSupplier(String supplierId) {
        if (supplierId == null || supplierId.trim().isEmpty()) return;
        List<Supplier> suppliers = getAllSuppliers();
        if (suppliers.removeIf(supplier -> supplierId.equalsIgnoreCase(supplier.getSupplierId()))) rewriteFile(suppliers);
    }

    private void rewriteFile(List<Supplier> suppliers) {
        ensureFileExists();
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(storageFile(), StandardCharsets.UTF_8, false))) {
            writer.write(FILE_HEADER);
            writer.newLine();
            for (Supplier supplier : suppliers) {
                writer.write(supplier.toFileString());
                writer.newLine();
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Error rewriting suppliers.txt", exception);
        }
    }
}