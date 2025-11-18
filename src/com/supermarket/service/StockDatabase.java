package com.supermarket.service;

import com.supermarket.exception.ItemNotFoundException;
import com.supermarket.model.OrderItem;
import com.supermarket.model.StockItem;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Manages the supermarket's inventory.
 * This class demonstrates:
 * 1. COLLECTIONS & GENERICS (using HashMap)
 * 2. FILE HANDLING (using BufferedReader/Writer)
 * 3. Throws our custom EXCEPTION
 */



public class StockDatabase {

    // 1. COLLECTIONS: Use a HashMap for fast item lookup by itemCode.
    private HashMap<String, StockItem> inventory;
    
    // 2. FILE HANDLING: Define the "database" file name.
    private final String DATABASE_FILE = "inventory.csv";
    private final String LOG_FILE = "order_log.csv"; // File for logging orders

    /**
     * Constructor for the StockDatabase.
     * It initializes the inventory and loads the data from the file.
     */
    public StockDatabase() {
        this.inventory = new HashMap<>();
        // Load the database from the file as soon as the object is created.
        this.loadDatabaseFromFile();
    }

    /**
     * Updates the stock quantities based on a parsed list of OrderItems.
     * @param orderItems The list of items from the QR code.
     * @param originalQrData The raw QR string for logging purposes.
     */
    public void updateStockFromOrder(ArrayList<OrderItem> orderItems, String originalQrData) {
        boolean stockUpdated = false;

        for (OrderItem orderItem : orderItems) {
            try {
                // Find the matching item in our inventory
                StockItem stockItem = this.inventory.get(orderItem.getItemCode());

                if (stockItem == null) {
                    // Item not found! Throw our custom exception.
                    throw new ItemNotFoundException(orderItem.getItemCode());
                }

                // If found, add the stock using its encapsulated method
                stockItem.addStock(orderItem.getQuantityToAdd());
                System.out.println("Updated stock for: " + stockItem.getItemName() 
                                    + ". New quantity: " + stockItem.getCurrentQuantityInStock());
                
                stockUpdated = true;

            } catch (ItemNotFoundException e) {
                // Catch our custom exception
                System.err.println(e.getMessage());
            }
        }
        
        // After updating all items, save the changes back to the file.
        if (stockUpdated) {
            this.saveDatabaseToFile();
            System.out.println("Database file successfully updated.");
            
            // Log the original order that was just processed
            logOrder(originalQrData); 
        }
    }

    /**
     * Reads the inventory.csv file and populates the HashMap.
     * This is a key FILE HANDLING method.
     */
    private void loadDatabaseFromFile() {
        System.out.println("Loading inventory from " + DATABASE_FILE + "...");
        
        try (BufferedReader reader = new BufferedReader(new FileReader(DATABASE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                
                if (parts.length >= 4) {
                    // Format: Code,Name,Quantity,Price,[Timestamp]
                    String itemCode = parts[0];
                    String itemName = parts[1];
                    int quantity = Integer.parseInt(parts[2]);
                    double price = Double.parseDouble(parts[3]);
                    
                    // Check if there is a 5th column (Timestamp)
                    String timestamp = "N/A";
                    if (parts.length == 5) {
                        timestamp = parts[4];
                    }
                    
                    StockItem item = new StockItem(itemCode, itemName, quantity, price, timestamp);
                    this.inventory.put(itemCode, item); // Add to HashMap
                }
            }
            System.out.println("Inventory loaded. " + inventory.size() + " items found.");
        } catch (IOException e) {
            // This happens if the file doesn't exist yet (e.g., first time run)
            System.err.println("Notice: Could not read " + DATABASE_FILE + ". File may not exist yet.");
        } catch (NumberFormatException e) {
            System.err.println("Error: Database file contains malformed data.");
        }
    }

    /**
     * Writes the current state of the inventory HashMap back to the inventory.csv file.
     * This is the second key FILE HANDLING method.
     */
    private void saveDatabaseToFile() {
        System.out.println("Saving inventory to " + DATABASE_FILE + "...");
        
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATABASE_FILE))) {
            
            // Loop through all the values (StockItem objects) in the HashMap
            for (StockItem item : this.inventory.values()) {
                // Use the helper method we defined in StockItem
                writer.write(item.toFileString());
                writer.newLine(); // Write a new line
            }
            System.out.println("Save complete.");
        } catch (IOException e) {
            System.err.println("Error: Could not write to " + DATABASE_FILE);
            e.printStackTrace();
        }
    }

    /**
     * Appends a record of a processed order to a log file.
     * This is part of the new "order-logging" feature.
     * @param originalQrData The raw QR string that was processed.
     */
    private void logOrder(String originalQrData) {
        // Use 'true' in FileWriter to enable "append" mode
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(LOG_FILE, true))) {
            
            // Create a simple timestamp
            String timestamp = java.time.LocalDateTime.now().toString();
            
            // Write the log line
            writer.write(timestamp + "," + originalQrData);
            writer.newLine();
            
        } catch (IOException e) {
            System.err.println("Warning: Could not write to " + LOG_FILE);
            e.printStackTrace();
        }
    }

    /**
     * A helper method to print a report of the current stock levels.
     */
    public void printStockReport() {
        System.out.println("\n--- CURRENT STOCK REPORT ---");
        System.out.println("(* = modified in this session)");
        System.out.println("------------------------------------------------------------------------------------------");
        // Adjusted width and added "Last Updated" column
        System.out.printf("%-1s %-10s | %-45s | %-10s | %-30s\n", "", "Item Code", "Item Name", "Qty", "Last Updated");
        System.out.println("------------------------------------------------------------------------------------------");

        if (this.inventory.isEmpty()) {
            System.out.println("Inventory is empty.");
        } else {
            // Loop through all items and print their details
            for (StockItem item : this.inventory.values()) {
                
                String marker = item.hasBeenModified() ? "*" : " ";

                System.out.printf("%-1s %-10s | %-45s | %-10d | %-30s\n", 
                    marker,
                    item.getItemCode(), 
                    item.getItemName(), 
                    item.getCurrentQuantityInStock(),
                    item.getLastUpdated());
            }
        }
        System.out.println("-------------------------------------------------------------------------------------------\n");
    }

    /**
     * Finds a single item in the inventory by its code.
     * @param itemCode The code to search for.
     * @return The StockItem object, or null if not found.
     */
    public StockItem findItem(String itemCode) {
        return this.inventory.get(itemCode); 
    }

    /**
     * Updates the price for a given item and saves the change.
     * @param itemCode The code of the item to update.
     * @param newPrice The new sale price.
     */
    public void updateItemPrice(String itemCode, double newPrice) {
        StockItem item = findItem(itemCode);
        
        if (item != null) {
            item.setSalePrice(newPrice);
            System.out.println("Price updated for " + item.getItemName() + ".");
            
            // CRITICAL: We must save the database file after this change.
            this.saveDatabaseToFile();
            
        } else {
            System.err.println("Error: Could not update price. Item not found.");
        }
    }
}