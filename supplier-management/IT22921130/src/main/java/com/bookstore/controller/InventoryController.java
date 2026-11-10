package com.bookstore.controller;

import com.bookstore.dto.InventoryForm;
import com.bookstore.exception.DuplicateStockException;
import com.bookstore.exception.InventoryNotFoundException;
import com.bookstore.model.EBookStock;
import com.bookstore.model.InventoryItem;
import com.bookstore.model.PrintedBookStock;
import com.bookstore.model.StockStatus;
import com.bookstore.service.InventoryService;
import com.bookstore.util.IsbnUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.support.RequestContextUtils;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/inventory")
public class InventoryController {
    private final InventoryService service;

    public InventoryController(InventoryService service) {
        this.service = service;
    }

    @ModelAttribute("active")
    public String activeModule() {
        return "inventory";
    }

    @GetMapping
    public String list(@RequestParam(value = "q", required = false) String q,
                       @RequestParam(value = "type", required = false) String type,
                       @RequestParam(value = "status", required = false) String status,
                       Model model) {
        List<InventoryItem> items = service.search(q, type, status);
        model.addAttribute("items", items);
        model.addAttribute("summary", service.getSummary());
        model.addAttribute("statuses", StockStatus.values());
        model.addAttribute("q", q);
        model.addAttribute("type", type);
        model.addAttribute("status", status);
        model.addAttribute("filtered", !isBlank(q) || !isBlank(type) || !isBlank(status));
        return "inventory/list";
    }

    @GetMapping("/{id}")
    public String view(@PathVariable("id") String id, Model model) {
        model.addAttribute("item", service.getById(id));
        return "inventory/view";
    }

    @GetMapping("/new")
    public String newForm(Model model) {
        InventoryForm form = new InventoryForm();
        form.setItemType(PrintedBookStock.TYPE);
        form.setReorderLevel(5);
        model.addAttribute("form", form);
        prepareForm(model, false, null);
        return "inventory/form";
    }

    @PostMapping
    public String create(@Valid @ModelAttribute("form") InventoryForm form, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        validateTypeSpecificFields(form, result, true);
        if (result.hasErrors()) {
            prepareForm(model, false, null);
            return "inventory/form";
        }
        try {
            InventoryItem item = service.create(form);
            redirect.addFlashAttribute("success",
                    "Added \"" + item.getBookTitle() + "\" as " + item.getItemId() + ".");
            return "redirect:/inventory/" + item.getItemId();
        } catch (DuplicateStockException e) {
            result.rejectValue("isbn", "duplicate", e.getMessage());
            prepareForm(model, false, null);
            return "inventory/form";
        }
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable("id") String id, Model model) {
        InventoryItem item = service.getById(id);
        model.addAttribute("form", service.toForm(item));
        prepareForm(model, true, item.getItemId());
        return "inventory/form";
    }

    @PostMapping("/{id}/edit")
    public String update(@PathVariable("id") String id,
                         @Valid @ModelAttribute("form") InventoryForm form, BindingResult result,
                         Model model, RedirectAttributes redirect) {
        InventoryItem existing = service.getById(id);
        form.setItemType(existing.getItemType());
        validateTypeSpecificFields(form, result, false);
        if (result.hasErrors()) {
            prepareForm(model, true, existing.getItemId());
            return "inventory/form";
        }
        try {
            InventoryItem updated = service.update(id, form);
            redirect.addFlashAttribute("success", "Saved changes to " + updated.getItemId() + ".");
            return "redirect:/inventory/" + updated.getItemId();
        } catch (DuplicateStockException e) {
            result.rejectValue("isbn", "duplicate", e.getMessage());
            prepareForm(model, true, existing.getItemId());
            return "inventory/form";
        }
    }

    @PostMapping("/{id}/adjust")
    public String adjust(@PathVariable("id") String id,
                         @RequestParam("action") String action,
                         @RequestParam(value = "amount", required = false) Integer amount,
                         RedirectAttributes redirect) {
        try {
            if (amount == null || amount <= 0) {
                throw new IllegalArgumentException("Enter a quantity greater than zero.");
            }
            InventoryItem item = switch (action) {
                case "restock" -> service.restock(id, amount);
                case "deduct" -> service.deduct(id, amount);
                default -> throw new IllegalArgumentException("Choose restock or remove.");
            };
            String verb = action.equals("restock") ? "Added " : "Removed ";
            redirect.addFlashAttribute("success", verb + amount + " unit(s). "
                    + item.getItemId() + " now has " + item.getQuantity() + ".");
        } catch (IllegalArgumentException | IllegalStateException e) {
            redirect.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/inventory/" + id;
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable("id") String id, RedirectAttributes redirect) {
        InventoryItem removed = service.delete(id);
        redirect.addFlashAttribute("success",
                "Deleted " + removed.getItemId() + " (\"" + removed.getBookTitle() + "\").");
        return "redirect:/inventory";
    }

    @ExceptionHandler(InventoryNotFoundException.class)
    public String handleNotFound(InventoryNotFoundException e, HttpServletRequest request) {
        RequestContextUtils.getOutputFlashMap(request).put("error", e.getMessage());
        return "redirect:/inventory";
    }

    private void prepareForm(Model model, boolean editMode, String itemId) {
        model.addAttribute("editMode", editMode);
        model.addAttribute("itemId", itemId);
    }

    private void validateTypeSpecificFields(InventoryForm form, BindingResult result, boolean isNew) {
        if (!result.hasFieldErrors("isbn") && !IsbnUtils.hasValidLength(form.getIsbn())) {
            result.rejectValue("isbn", "length", "An ISBN has 10 or 13 digits.");
        }

        if (PrintedBookStock.TYPE.equals(form.getItemType())) {
            if (isBlank(form.getWarehouse())) {
                result.rejectValue("warehouse", "required", "Enter the warehouse for this printed stock.");
            }
            if (isBlank(form.getShelfCode())) {
                result.rejectValue("shelfCode", "required", "Enter the shelf code, for example A-03.");
            }
        } else if (EBookStock.TYPE.equals(form.getItemType())) {
            if (isBlank(form.getPlatform())) {
                result.rejectValue("platform", "required", "Enter the platform that sells this e-book.");
            }
            if (form.getLicenseExpiry() == null) {
                result.rejectValue("licenseExpiry", "required", "Choose the license expiry date.");
            } else if (isNew && form.getLicenseExpiry().isBefore(LocalDate.now())) {
                result.rejectValue("licenseExpiry", "past", "New licenses need an expiry date in the future.");
            }
        } else {
            result.rejectValue("itemType", "invalid", "Choose printed book or e-book.");
        }
    }

    private static boolean isBlank(String value) {
        return value == null || value.isBlank();
    }
}
