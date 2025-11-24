//src/com/supermarket/service/OrderParser.java
package com.supermarket.service;

import com.supermarket.model.OrderItem;
import java.util.ArrayList;

/**
 * A service class responsible for parsing the "QR Code" string.
 * This class demonstrates EXCEPTION HANDLING.
 */
public class OrderParser {

    /**
     * Parses the raw QR code string into a list of OrderItem objects.
     *
     * @param qrCodeData The raw string from the QR code scanner.
     * Format: "Code1,Qty1,Price1;Code2,Qty2,Price2;..."
     * @return An ArrayList of OrderItem objects.
     */
    public ArrayList<OrderItem> parseOrder(String qrCodeData) {
        
        // Use Collections & Generics as required [cite: 28]
        ArrayList<OrderItem> orderItems = new ArrayList<>();

        // 1. Check for null or empty string
        if (qrCodeData == null || qrCodeData.isEmpty()) {
            System.err.println("Error: QR Code data is empty or null.");
            return orderItems; // Return an empty list
        }

        // 2. Split the main string into individual item strings
        // e.g., ["IT-1001,50,150.00", "IT-1002,25,899.50"]
        String[] items = qrCodeData.split(";");

        // 3. Loop through each item string
        for (String itemString : items) {
            
            // 4. Split the item string into its parts
            // e.g., ["IT-1001", "50", "150.00"]
            String[] parts = itemString.split(",");

            // 5. This is the Exception Handling part 
            try {
                // We expect exactly 3 parts: Code, Quantity, Price
                if (parts.length != 3) {
                    // This is a custom error log, not a thrown exception
                    System.err.println("Skipping malformed item: " + itemString);
                    continue; // Skip this item and move to the next
                }

                // Parse the parts
                String itemCode = parts[0].trim();
                int quantity = Integer.parseInt(parts[1].trim());
                double price = Double.parseDouble(parts[2].trim());

                // Create the OrderItem and add it to our list
                OrderItem newItem = new OrderItem(itemCode, quantity, price);
                orderItems.add(newItem);

            } catch (NumberFormatException e) {
                // This 'catch' block runs if Integer.parseInt() or Double.parseDouble() fails.
                // For example, if the QR data was "IT-1001,FIFTY,150.00"
                System.err.println("Error parsing number for item: " + itemString);
                // We log the error and continue to the next item.
            
            } catch (Exception e) {
                // A general catch-all for any other unexpected errors
                System.err.println("An unexpected error occurred parsing item: " + itemString);
            }
        }

        return orderItems; // Return the list of successfully parsed items
    }
}
