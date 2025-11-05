package com.supermarket.model;

/**
 * Represents a single item in the supermarket's inventory (our "database").
 * This class demonstrates ENCAPSULATION.
 */
public class StockItem {

    // 1. Fields (private for encapsulation)
    private String itemCode;
    private String itemName;
    private int currentQuantityInStock;
    private double salePrice; // The price we sell it for

    // 2. Constructor (to create new objects)
    public StockItem(String itemCode, String itemName, int initialQuantity, double salePrice) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.currentQuantityInStock = initialQuantity;
        this.salePrice = salePrice;
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
    
    // 4. "Setter" / Mutator methods (to safely change data)

    /**
     * Adds a specified quantity to the current stock.
     * @param quantityToAdd The number of items to add.
     */
    public void addStock(int quantityToAdd) {
        if (quantityToAdd > 0) {
            this.currentQuantityInStock += quantityToAdd;
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
            return true; // Sale was successful
        }
        return false; // Not enough stock
    }

    // 5. Helper method for File Handling
    
    /**
     * Formats the StockItem as a String for saving to a CSV file.
     * e.g., "IT-1001,Sunlight Soap 100g,50,175.00"
     * @return A CSV-formatted string.
     */
    public String toFileString() {
        // We use a comma (,) as the separator
        return itemCode + "," + itemName + "," + currentQuantityInStock + "," + salePrice;
    }
}
