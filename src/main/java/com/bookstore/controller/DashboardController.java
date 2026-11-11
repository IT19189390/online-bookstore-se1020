
package com.bookstore.controller;

import com.bookstore.model.Book;
import com.bookstore.model.DashboardModule;
import com.bookstore.service.BookService;
import com.bookstore.service.InventoryService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Controller
public class DashboardController {

    private final InventoryService inventoryService;
    private final BookService bookService;

    public DashboardController(
            InventoryService inventoryService,
            BookService bookService) {
        this.inventoryService = inventoryService;
        this.bookService = bookService;
    }

    @GetMapping("/")
    public String dashboard(Model model) {

        // Existing inventory dashboard data
        model.addAttribute("active", "dashboard");
        model.addAttribute("summary", inventoryService.getSummary());
        model.addAttribute("attentionItems",
                inventoryService.getItemsNeedingAttention(5));

        // Retrieve books from the database
        List<Book> allBooks = bookService.getAllBooks();

        // Sort by book ID, newest first
        List<Book> sortedBooks = allBooks.stream()
                .sorted(Comparator.comparingInt(Book::getBookId).reversed())
                .toList();

        // Total books
        long totalBooks = sortedBooks.size();

        // Count distinct categories
        long totalCategories = sortedBooks.stream()
                .map(Book::getCategory)
                .filter(category -> category != null && !category.isBlank())
                .distinct()
                .count();

        // Calculate average book price
        List<BigDecimal> prices = sortedBooks.stream()
                .map(Book::getPrice)
                .filter(Objects::nonNull)
                .toList();

        BigDecimal averageBookPrice = BigDecimal.ZERO;

        if (!prices.isEmpty()) {
            BigDecimal totalPrice = prices.stream()
                    .reduce(BigDecimal.ZERO, BigDecimal::add);

            averageBookPrice = totalPrice.divide(
                    BigDecimal.valueOf(prices.size()),
                    2,
                    RoundingMode.HALF_UP
            );
        }

        // Get the five most recently inserted books
        List<Book> recentlyAddedBooks = sortedBooks.stream()
                .limit(5)
                .toList();

        // Send catalogue information to dashboard
        model.addAttribute("totalBooks", totalBooks);
        model.addAttribute("totalCategories", totalCategories);
        model.addAttribute("averageBookPrice", averageBookPrice);
        model.addAttribute("recentlyAddedBooks", recentlyAddedBooks);

        model.addAttribute("latestBook",
                recentlyAddedBooks.isEmpty()
                        ? null
                        : recentlyAddedBooks.get(0));

        return "dashboard";
    }

    @GetMapping("/modules/{key}")
    public String placeholder(
            @PathVariable("key") String key,
            Model model) {

        DashboardModule module =
                DashboardModule.findByKey(key).orElse(null);

        if (module == null) {
            return "redirect:/";
        }

        model.addAttribute("active", key);
        model.addAttribute("module", module);

        return "placeholder";
    }
}
