package com.bookstore.model;

/**
 * Discounted Order concrete implementation extending {@link Order}.
 * Represents sales orders with percentage discount applied.
 * Demonstrates Inheritance, Encapsulation, and Polymorphism.
 */
public class DiscountedOrder extends Order {

    // Encapsulated private field for discount percentage (e.g., 10.0 for 10%)
    private double discountRate;

    /**
     * Default constructor following JavaBean standards.
     */
    public DiscountedOrder() {
        super();
    }

    /**
     * Parameterized constructor initializing base fields via super(...) and discountRate.
     *
     * @param orderId       unique identifier for the order
     * @param customerName  full name of the customer
     * @param bookName      title/name of the ordered book
     * @param quantity      quantity ordered
     * @param bookPrice     price per unit of the book
     * @param paymentMethod selected payment method
     * @param orderDate     date when the order was created
     * @param discountRate  discount percentage (e.g., 10.0 for 10%)
     */
    public DiscountedOrder(String orderId, String customerName, String bookName, int quantity,
                           double bookPrice, String paymentMethod, String orderDate, double discountRate) {
        super(orderId, customerName, bookName, quantity, bookPrice, paymentMethod, orderDate);
        this.discountRate = discountRate;
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
     * Calculates discount deduction amount as calculateSubtotal() * (discountRate / 100.0).
     *
     * @return discount monetary amount
     */
    @Override
    public double calculateDiscountAmount() {
        return calculateSubtotal() * (discountRate / 100.0);
    }

    /**
     * Calculates final total payable as calculateSubtotal() - calculateDiscountAmount().
     *
     * @return net total amount
     */
    @Override
    public double calculateFinalTotal() {
        return calculateSubtotal() - calculateDiscountAmount();
    }

    /**
     * Serializes discounted order to the pipe-delimited format:
     * ORDER_ID|DISCOUNTED|CUSTOMER_NAME|BOOK_NAME|QUANTITY|BOOK_PRICE|DISCOUNT_RATE|PAYMENT_METHOD|ORDER_DATE
     *
     * @return formatted record string
     */
    @Override
    public String toFileString() {
        String rateFormatted = (discountRate % 1 == 0)
                ? String.format(java.util.Locale.US, "%.1f", discountRate)
                : String.valueOf(discountRate);
        return String.format(java.util.Locale.US, "%s|DISCOUNTED|%s|%s|%d|%.2f|%s|%s|%s",
                getOrderId() != null ? getOrderId().trim() : "",
                getCustomerName() != null ? getCustomerName().trim() : "",
                getBookName() != null ? getBookName().trim() : "",
                getQuantity(),
                getBookPrice(),
                rateFormatted,
                getPaymentMethod() != null ? getPaymentMethod().trim() : "",
                getOrderDate() != null ? getOrderDate().trim() : "");
    }

    @Override
    public String getOrderType() {
        return "DISCOUNTED";
    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    @Override
    public String toString() {
        return "DiscountedOrder{" +
                "discountRate=" + discountRate +
                "} " + super.toString();
    }
}
