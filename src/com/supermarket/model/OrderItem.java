//src/com/supermarket/model/OrderItem.java
package com.supermarket.model;

/**
 * A simple "data class" to temporarily hold the details
 * of one item from a supplier's QR code.
 */
public class OrderItem {

    // Fields can be public for a simple data-transfer-object
    // Or we can make them private with a constructor and getters.
    // Let's use the private approach as it's better OOP practice.

    private String itemCode;
    private int quantityToAdd;
    private double supplierPrice; // Price from the supplier bill

    /**
     * Constructor to create a new OrderItem.
     * @param itemCode The product's unique code.
     * @param quantityToAdd The quantity being added to stock.
     * @param supplierPrice The price we paid the supplier for this item.
     */
    public OrderItem(String itemCode, int quantityToAdd, double supplierPrice) {
        this.itemCode = itemCode;
        this.quantityToAdd = quantityToAdd;
        this.supplierPrice = supplierPrice;
    }

    // --- Getter Methods ---
    
    public String getItemCode() {
        return itemCode;
    }

    public int getQuantityToAdd() {
        return quantityToAdd;
    }

    public double getSupplierPrice() {
        return supplierPrice;
    }
}
