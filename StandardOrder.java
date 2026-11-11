package com.bookstore.model;

/**
 * Standard Order concrete implementation extending {@link Order}.
 * Represents sales orders without discounts applied.
 * Demonstrates Inheritance and Polymorphic calculation methods.
 */
public class StandardOrder extends Order {

    /**
     * Default constructor following JavaBean standards.
     */
    public StandardOrder() {
        super();
    }

    /**
     * Parameterized constructor delegating initialization to the superclass constructor.
     *
     * @param orderId       unique identifier for the order
     * @param customerName  full name of the customer
     * @param bookName      title/name of the ordered book
     * @param quantity      quantity ordered
     * @param bookPrice     price per unit of the book
     * @param paymentMethod selected payment method
     * @param orderDate     date when the order was created
     */
    public StandardOrder(String orderId, String customerName, String bookName, int quantity,
                         double bookPrice, String paymentMethod, String orderDate) {
        super(orderId, customerName, bookName, quantity, bookPrice, paymentMethod, orderDate);
    }

    /**
     * Calculates subtotal as bookPrice * quantity.
     *
     * @return subtotal amount
     */
    @Override
    public double calculateSubtotal() {
        return getBookPrice() * getQuantity();
    }

    /**
     * Calculates discount amount for a standard order, which is always 0.0.
     *
     * @return 0.0
     */
    @Override
    public double calculateDiscountAmount() {
        return 0.0;
    }

    /**
     * Calculates final total for a standard order, equal to the subtotal.
     *
     * @return subtotal amount
     */
    @Override
    public double calculateFinalTotal() {
        return calculateSubtotal();
    }

    /**
     * Serializes standard order to the pipe-delimited format:
     * ORDER_ID|STANDARD|CUSTOMER_NAME|BOOK_NAME|QUANTITY|BOOK_PRICE|0.0|PAYMENT_METHOD|ORDER_DATE
     *
     * @return formatted record string
     */
    @Override
    public String toFileString() {
        return String.format(java.util.Locale.US, "%s|STANDARD|%s|%s|%d|%.2f|0.0|%s|%s",
                getOrderId() != null ? getOrderId().trim() : "",
                getCustomerName() != null ? getCustomerName().trim() : "",
                getBookName() != null ? getBookName().trim() : "",
                getQuantity(),
                getBookPrice(),
                getPaymentMethod() != null ? getPaymentMethod().trim() : "",
                getOrderDate() != null ? getOrderDate().trim() : "");
    }

    @Override
    public String getOrderType() {
        return "STANDARD";
    }
}
