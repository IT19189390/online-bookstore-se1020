package com.bookstore.controller;

import com.bookstore.model.InternationalSupplier;
import com.bookstore.model.LocalSupplier;
import com.bookstore.model.Supplier;
import com.bookstore.service.SupplierStorageManager;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/suppliers")
public class SupplierController {
    private final SupplierStorageManager supplierStorageManager;

    public SupplierController(SupplierStorageManager supplierStorageManager) {
        this.supplierStorageManager = supplierStorageManager;
    }

    @GetMapping("/new")
    public String showNewSupplierForm(Model model) {
        model.addAttribute("isEdit", false);
        model.addAttribute("supplierType", "LOCAL");
        return "add-supplier";
    }

    @PostMapping("/save")
    public String saveSupplier(@RequestParam String supplierName, @RequestParam String phoneNumber,
            @RequestParam String emailAddress, @RequestParam String companyName, @RequestParam String registrationDate,
            @RequestParam String supplierType, @RequestParam(required = false, defaultValue = "") String businessRegNumber,
            @RequestParam(required = false, defaultValue = "") String country,
            @RequestParam(required = false, defaultValue = "") String customsCode) {
        supplierStorageManager.saveSupplier(createSupplier("SUP-" + System.currentTimeMillis(), supplierName,
                phoneNumber, emailAddress, companyName, registrationDate, supplierType, businessRegNumber, country, customsCode));
        return "redirect:/suppliers";
    }

    @GetMapping
    public String listSuppliers(@RequestParam(required = false) String search, Model model) {
        List<Supplier> suppliers = search == null || search.trim().isEmpty()
                ? supplierStorageManager.getAllSuppliers()
                : supplierStorageManager.searchSuppliers(search);
        model.addAttribute("suppliers", suppliers);
        return "supplier-list";
    }

    @GetMapping("/details/{id}")
    public String showSupplierDetails(@PathVariable String id, Model model) {
        Supplier supplier = supplierStorageManager.getSupplierById(id);
        if (supplier == null) return "redirect:/suppliers";
        model.addAttribute("supplier", supplier);
        return "supplier-details";
    }

    @GetMapping("/edit/{id}")
    public String showEditSupplierForm(@PathVariable String id, Model model) {
        Supplier supplier = supplierStorageManager.getSupplierById(id);
        if (supplier == null) return "redirect:/suppliers";
        model.addAttribute("supplier", supplier);
        model.addAttribute("isEdit", true);
        model.addAttribute("supplierType", supplier instanceof InternationalSupplier ? "INTERNATIONAL" : "LOCAL");
        return "add-supplier";
    }

    @PostMapping("/update")
    public String updateSupplier(@RequestParam String supplierId, @RequestParam String supplierName,
            @RequestParam String phoneNumber, @RequestParam String emailAddress, @RequestParam String companyName,
            @RequestParam String registrationDate, @RequestParam String supplierType,
            @RequestParam(required = false, defaultValue = "") String businessRegNumber,
            @RequestParam(required = false, defaultValue = "") String country,
            @RequestParam(required = false, defaultValue = "") String customsCode) {
        supplierStorageManager.updateSupplier(createSupplier(supplierId, supplierName, phoneNumber, emailAddress,
                companyName, registrationDate, supplierType, businessRegNumber, country, customsCode));
        return "redirect:/suppliers";
    }

    @GetMapping("/delete/{id}")
    public String deleteSupplier(@PathVariable String id) {
        supplierStorageManager.deleteSupplier(id);
        return "redirect:/suppliers";
    }

    private Supplier createSupplier(String id, String name, String phone, String email, String company, String date,
            String type, String businessRegNumber, String country, String customsCode) {
        if ("INTERNATIONAL".equalsIgnoreCase(type)) {
            return new InternationalSupplier(id, name, phone, email, company, date, country, customsCode);
        }
        return new LocalSupplier(id, name, phone, email, company, date, businessRegNumber);
    }

}