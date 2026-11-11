package com.bookstore.controller;

import com.bookstore.model.DiscountedOrder;
import com.bookstore.model.Order;
import com.bookstore.model.StandardOrder;
import com.bookstore.service.OrderStorageManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Spring MVC Controller responsible for processing HTTP requests,
 * coordinating with {@link OrderStorageManager}, and binding model attributes for JSP views.
 */
@Controller
public class OrderController {

    /**
     * Data record representing a catalog book with preset retail price and discount policy.
     */
    public record BookCatalogItem(String title, double price, double discountRate) {}

    /**
     * Sample bookstore catalog with pre-defined pricing and discount rules.
     */
    public static final List<BookCatalogItem> BOOK_CATALOG = List.of(
            new BookCatalogItem("Effective Java", 45.00, 10.0),
            new BookCatalogItem("Clean Code", 40.00, 0.0),
            new BookCatalogItem("Design Patterns", 50.00, 15.0),
            new BookCatalogItem("Spring in Action", 38.00, 5.0),
            new BookCatalogItem("Java: The Complete Reference", 55.00, 0.0)
    );

    private final OrderStorageManager orderStorageManager;

    @Autowired
    public OrderController(OrderStorageManager orderStorageManager) {
        this.orderStorageManager = orderStorageManager;
    }

    /**
     * Binds bookCatalog to the model across all controller requests.
     */
    @ModelAttribute("bookCatalog")
    public List<BookCatalogItem> populateBookCatalog() {
        return BOOK_CATALOG;
    }

    /**
     * Root URL redirect to the order listing page.
     */
    @GetMapping("/")
    public String rootRedirect() {
        return "redirect:/orders";
    }

    /**
     * Endpoint 1: Renders place-order.jsp (Page 1) with an empty order form.
     */
    @GetMapping("/orders/new")
    public String showNewOrderForm(Model model) {
        model.addAttribute("isEdit", false);
        model.addAttribute("discountRate", 0.0);
        model.addAttribute("orderType", "STANDARD");
        return "place-order";
    }

    /**
     * Endpoint 2: Accepts form parameters, auto-generates order ID (ORD- + timestamp),
     * constructs StandardOrder or DiscountedOrder, saves to storage, and redirects to /orders.
     */
    @PostMapping("/orders/save")
    public String saveOrder(
            @RequestParam("customerName") String customerName,
            @RequestParam("bookName") String bookName,
            @RequestParam("quantity") int quantity,
            @RequestParam("bookPrice") double bookPrice,
            @RequestParam(value = "orderType", defaultValue = "STANDARD") String orderType,
            @RequestParam(value = "discountRate", required = false, defaultValue = "0.0") double discountRate,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam("orderDate") String orderDate) {

        String orderId = "ORD-" + System.currentTimeMillis();
        Order order;

        if ("DISCOUNTED".equalsIgnoreCase(orderType)) {
            order = new DiscountedOrder(orderId, customerName, bookName, quantity, bookPrice, paymentMethod, orderDate, discountRate);
        } else {
            order = new StandardOrder(orderId, customerName, bookName, quantity, bookPrice, paymentMethod, orderDate);
        }

        orderStorageManager.saveOrder(order);
        return "redirect:/orders";
    }

    /**
     * Endpoint 3: Fetches all orders via OrderStorageManager.getAllOrders()
     * and binds them to model attribute "orders" for order-list.jsp (Page 2).
     */
    @GetMapping("/orders")
    public String listOrders(Model model) {
        List<Order> orders = orderStorageManager.getAllOrders();
        model.addAttribute("orders", orders);
        return "order-list";
    }

    /**
     * Endpoint 4: Fetches order by ID, calculates financial figures
     * (subtotal, discount amount, final total), binds model attributes,
     * and renders order-details.jsp (Page 3).
     */
    @GetMapping("/orders/details/{id}")
    public String showOrderDetails(@PathVariable("id") String id, Model model) {
        Order order = orderStorageManager.getOrderById(id);
        if (order == null) {
            return "redirect:/orders";
        }

        double subtotal = order.calculateSubtotal();
        double discountAmount = order.calculateDiscountAmount();
        double finalTotal = order.calculateFinalTotal();

        model.addAttribute("order", order);
        model.addAttribute("subtotal", subtotal);
        model.addAttribute("discountAmount", discountAmount);
        model.addAttribute("finalTotal", finalTotal);

        if (order instanceof DiscountedOrder discountedOrder) {
            model.addAttribute("orderType", "DISCOUNTED");
            model.addAttribute("discountRate", discountedOrder.getDiscountRate());
        } else {
            model.addAttribute("orderType", "STANDARD");
            model.addAttribute("discountRate", 0.0);
        }

        return "order-details";
    }

    /**
     * Endpoint 5: Fetches existing order by ID and renders place-order.jsp
     * pre-populated in edit mode.
     */
    @GetMapping("/orders/edit/{id}")
    public String showEditOrderForm(@PathVariable("id") String id, Model model) {
        Order order = orderStorageManager.getOrderById(id);
        if (order == null) {
            return "redirect:/orders";
        }

        model.addAttribute("order", order);
        model.addAttribute("isEdit", true);

        if (order instanceof DiscountedOrder discountedOrder) {
            model.addAttribute("orderType", "DISCOUNTED");
            model.addAttribute("discountRate", discountedOrder.getDiscountRate());
        } else {
            model.addAttribute("orderType", "STANDARD");
            model.addAttribute("discountRate", 0.0);
        }

        return "place-order";
    }

    /**
     * Endpoint 6: Processes update form submission, creates updated Order object,
     * calls OrderStorageManager.updateOrder(), and redirects to /orders.
     */
    @PostMapping("/orders/update")
    public String updateOrder(
            @RequestParam("orderId") String orderId,
            @RequestParam("customerName") String customerName,
            @RequestParam("bookName") String bookName,
            @RequestParam("quantity") int quantity,
            @RequestParam("bookPrice") double bookPrice,
            @RequestParam(value = "orderType", defaultValue = "STANDARD") String orderType,
            @RequestParam(value = "discountRate", required = false, defaultValue = "0.0") double discountRate,
            @RequestParam("paymentMethod") String paymentMethod,
            @RequestParam("orderDate") String orderDate) {

        Order updatedOrder;
        if ("DISCOUNTED".equalsIgnoreCase(orderType)) {
            updatedOrder = new DiscountedOrder(orderId, customerName, bookName, quantity, bookPrice, paymentMethod, orderDate, discountRate);
        } else {
            updatedOrder = new StandardOrder(orderId, customerName, bookName, quantity, bookPrice, paymentMethod, orderDate);
        }

        orderStorageManager.updateOrder(updatedOrder);
        return "redirect:/orders";
    }

    /**
     * Endpoint 7: Calls OrderStorageManager.deleteOrder(id) and redirects to /orders.
     */
    @GetMapping("/orders/delete/{id}")
    public String deleteOrder(@PathVariable("id") String id) {
        orderStorageManager.deleteOrder(id);
        return "redirect:/orders";
    }
}
