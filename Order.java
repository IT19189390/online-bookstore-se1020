package com.bookstore.model;

import java.util.regex.Pattern;

/**
 * Abstract parent class representing a generic Order in the bookstore system.
 * Demonstrates strict Object-Oriented Programming (OOP) principles:
 * - Encapsulation: Private member attributes exposed via accessors and mutators.
 * - Abstraction: Abstract methods defining contract for calculations and serialization.
 * - Polymorphism: Dynamic method dispatch for subclasses and static factory parsing.
 */
public abstract class Order {

    // Encapsulated private attributes
    private String orderId;
    private String customerName;
    private String bookName;
    private int quantity;
    private double bookPrice;
    private String paymentMethod;
    private String orderDate;

    /**
     * Default constructor following JavaBean specifications.
     */
    public Order() {
    }

    /**
     * Full parameterized constructor initializing all core order properties.
     *
     * @param orderId       unique identifier for the order
     * @param customerName  full name of the customer
     * @param bookName      title/name of the ordered book
     * @param quantity      quantity ordered
     * @param bookPrice     price per unit of the book
     * @param paymentMethod selected payment method
     * @param orderDate     date when the order was created
     */
    public Order(String orderId, String customerName, String bookName, int quantity,
                 double bookPrice, String paymentMethod, String orderDate) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.bookName = bookName;
        this.quantity = quantity;
        this.bookPrice = bookPrice;
        this.paymentMethod = paymentMethod;
        this.orderDate = orderDate;
    }

    // --- Abstract Calculation & Serialization Methods (Abstraction) ---

    /**
     * Calculates the gross subtotal amount before applying any discounts.
     *
     * @return order subtotal
     */
    public abstract double calculateSubtotal();

    /**
     * Calculates the monetary deduction based on applied discount policy.
     *
     * @return discount amount
     */
    public abstract double calculateDiscountAmount();

    /**
     * Calculates the net payable amount after deducting any discount.
     *
     * @return final total amount
     */
    public abstract double calculateFinalTotal();

    /**
     * Serializes the order state into a pipe-delimited line for persistence.
     *
     * @return formatted record string
     */
    public abstract String toFileString();

    /**
     * Returns the order classification type (e.g., "STANDARD" or "DISCOUNTED").
     *
     * @return order type string
     */
    public abstract String getOrderType();

    // --- Static Factory Parser ---

    /**
     * Reconstructs an Order instance from a pipe-separated record line.
     * Demonstrates polymorphic instantiation based on order type token.
     *
     * Expected Format:
     * ORDER_ID|TYPE|CUSTOMER_NAME|BOOK_NAME|QUANTITY|BOOK_PRICE|DISCOUNT_RATE|PAYMENT_METHOD|ORDER_DATE
     *
     * @param line pipe-delimited record string
     * @return concrete instance of {@link StandardOrder} or {@link DiscountedOrder}
     * @throws IllegalArgumentException if the record is malformed or order type is unsupported
     */
    public static Order fromFileString(String line) {
        if (line == null) {
            return null;
        }
        String trimmedLine = line.trim();
        if (trimmedLine.isEmpty() || trimmedLine.startsWith("#") || trimmedLine.startsWith("//")) {
            return null;
        }

        String[] parts = trimmedLine.split("\\|");
        if (parts.length < 9) {
            throw new IllegalArgumentException(
                    "Invalid order line format. Expected at least 9 pipe-separated tokens, got " + parts.length + ": " + line);
        }

        String orderId = parts[0].trim();
        String orderType = parts[1].trim();
        String customerName = parts[2].trim();
        String bookName = parts[3].trim();

        int quantity;
        try {
            quantity = Integer.parseInt(parts[4].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid integer quantity token: '" + parts[4].trim() + "'", e);
        }

        double bookPrice;
        try {
            bookPrice = Double.parseDouble(parts[5].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid decimal bookPrice token: '" + parts[5].trim() + "'", e);
        }

        double discountRate;
        try {
            discountRate = Double.parseDouble(parts[6].trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid decimal discountRate token: '" + parts[6].trim() + "'", e);
        }

        String paymentMethod = parts[7].trim();
        String orderDate = parts[8].trim();

        if ("STANDARD".equalsIgnoreCase(orderType)) {
            return new StandardOrder(orderId, customerName, bookName, quantity, bookPrice, paymentMethod, orderDate);
        } else if ("DISCOUNTED".equalsIgnoreCase(orderType)) {
            return new DiscountedOrder(orderId, customerName, bookName, quantity, bookPrice, paymentMethod, orderDate, discountRate);
        } else {
            throw new IllegalArgumentException("Unknown order type token: " + orderType);
        }
    }

    // --- Accessors and Mutators (Encapsulation) ---

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getBookPrice() {
        return bookPrice;
    }

    public void setBookPrice(double bookPrice) {
        this.bookPrice = bookPrice;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId='" + orderId + '\'' +
                ", customerName='" + customerName + '\'' +
                ", bookName='" + bookName + '\'' +
                ", quantity=" + quantity +
                ", bookPrice=" + bookPrice +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", orderDate='" + orderDate + '\'' +
                '}';
    }
}
