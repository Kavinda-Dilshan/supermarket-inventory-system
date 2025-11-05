package com.supermarket.main;

import com.supermarket.model.StockItem; // <-- ADD THIS LINE
import com.supermarket.service.OrderParser;
import com.supermarket.service.StockDatabase;
import java.util.Scanner;

/**
 * The main application class that runs the Supermarket Inventory System. This
 * class provides the console-based user interface (UI).
 */
public class SupermarketApp {

    public static void main(String[] args) {

        // 1. Initialize all the necessary objects
        Scanner scanner = new Scanner(System.in);
        OrderParser parser = new OrderParser();
        StockDatabase database; // This will auto-load "inventory.csv"
        database = new StockDatabase();

        System.out.println("Welcome to the Supermarket Inventory Management System");
        System.out.println("====================================================");

        // 2. Start the main application loop
        boolean isRunning = true;
        while (isRunning) {
            // Display the menu
            // Change this section
                System.out.println("\nPlease select an option:");
                System.out.println("  1. Scan Supplier Bill (Update Stock)");
                System.out.println("  2. View Current Stock Report");
                System.out.println("  3. View / Update Item Price"); // <-- NEW
                System.out.println("  4. Exit"); // <-- MOVED
                System.out.print("Enter your choice (1-4): "); // <-- UPDATED
            // Read the user's choice
            String choice = scanner.nextLine();

            // 3. Handle the user's choice
            // 3. Handle the user's choice
switch (choice) {
    case "1":
        // --- Scan New Order ---
        System.out.println("\n--- Update Stock ---");
        System.out.print("Please scan the QR code (paste the data string and press Enter): \n> ");
        String qrCodeData = scanner.nextLine();

        var parsedItems = parser.parseOrder(qrCodeData); 

        if (parsedItems.isEmpty()) {
            System.err.println("No valid items were parsed from the input.");
        } else {
            database.updateStockFromOrder(parsedItems, qrCodeData); // We pass both
        }
        break;

    case "2":
        // --- View Stock Report ---
        database.printStockReport();
        break;

    case "3":
        // --- NEW FEATURE: View / Update Price ---
        System.out.println("\n--- View / Update Item Price ---");
        System.out.print("Enter the Item Code to look up: ");
        String itemCode = scanner.nextLine();

                StockItem item = database.findItem(itemCode);
        if (item == null) {
            System.err.println("Error: Item code '" + itemCode + "' not found.");
        } else {
            // Item was found, show details
            System.out.println("Found item: " + item.getItemName());
            System.out.println("Current sale price: " + item.getSalePrice());

            System.out.print("\nEnter new price (or leave blank to keep current): ");
            String newPriceString = scanner.nextLine();

            if (!newPriceString.isEmpty()) {
                try {
                    double newPrice = Double.parseDouble(newPriceString);
                    database.updateItemPrice(itemCode, newPrice); // Call our new method
                } catch (NumberFormatException e) {
                    System.err.println("Invalid price. Please enter numbers only.");
                }
            } else {
                System.out.println("Price not changed.");
            }
        }
        break; // <-- Don't forget this break

    case "4":
        // --- Exit ---
        isRunning = false; // This will cause the while loop to end
        break;

    default:
        // --- Invalid Choice ---
        System.err.println("Invalid choice. Please enter 1, 2, 3, or 4.");
        break;
}
        }

        // 4. Clean up and exit
        System.out.println("\nThank you for using the system. Goodbye!");
        scanner.close(); // Good practice to close the scanner
    }
}
