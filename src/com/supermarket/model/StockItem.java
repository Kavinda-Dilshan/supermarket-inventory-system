package com.supermarket.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single item in the supermarket's inventory (our "database").
 * This class demonstrates ENCAPSULATION.
 */
public class StockItem {

    // 1. Fields (private for encapsulation)
    private String itemCode;
    private String itemName;
    private int currentQuantityInStock;
    private double salePrice;
    private String lastUpdated; // The new field for the timestamp
    private boolean hasBeenModified = false; // To track session changes

    // 2. Constructor (to create new objects)
    public StockItem(String itemCode, String itemName, int initialQuantity, double salePrice) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.currentQuantityInStock = initialQuantity;
        this.salePrice = salePrice;
        this.lastUpdated = "N/A"; // Default value for new items
    }

    // Constructor used when loading from file (includes timestamp)
    public StockItem(String itemCode, String itemName, int initialQuantity, double salePrice, String lastUpdated) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.currentQuantityInStock = initialQuantity;
        this.salePrice = salePrice;
        this.lastUpdated = lastUpdated;
    }

    // 3. "Getter" methods (to read data)
    public String getItemCode() {
        return itemCode;
    }

    public String getItemName() {
        return itemName;
    }

    public int getCurrentQuantityInStock() {
        return currentQuantityInStock;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public String getLastUpdated() {
        return lastUpdated;
    }

    public boolean hasBeenModified() {
        return hasBeenModified;
    }

    // 4. "Setter" / Mutator methods (to safely change data)

    /**
     * Adds a specified quantity to the current stock.
     * @param quantityToAdd The number of items to add.
     */
    public void addStock(int quantityToAdd) {
        if (quantityToAdd > 0) {
            this.currentQuantityInStock += quantityToAdd;
            this.hasBeenModified = true;
            updateTimestamp(); // Update time automatically
        }
    }

    /**
     * Removes a specified quantity from the current stock.
     * @param quantityToRemove The number of items to remove.
     * @return true if successful, false if not enough stock.
     */
    public boolean removeStock(int quantityToRemove) {
        if (quantityToRemove > 0 && quantityToRemove <= this.currentQuantityInStock) {
            this.currentQuantityInStock -= quantityToRemove;
            this.hasBeenModified = true;
            updateTimestamp(); // Update time automatically
            return true; 
        }
        return false; 
    }

    /**
     * Updates the sale price of the item.
     * @param newSalePrice The new price to set.
     */
    public void setSalePrice(double newSalePrice) {
        if (newSalePrice >= 0) {
            this.salePrice = newSalePrice;
            this.hasBeenModified = true;
            updateTimestamp(); // Update time automatically
        } else {
            System.err.println("Error: Price cannot be negative.");
        }
    }

    // Helper to set the lastUpdated field to the current time.
    private void updateTimestamp() {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        this.lastUpdated = LocalDateTime.now().format(dtf);
    }

    // 5. Helper method for File Handling
    /**
     * Formats the StockItem as a String for saving to a CSV file.
      A CSV-formatted string.
     */
    public String toFileString() {
        // Appends the timestamp as the 5th column
        return itemCode + "," + itemName + "," + currentQuantityInStock + "," + salePrice + "," + lastUpdated;
    }
}