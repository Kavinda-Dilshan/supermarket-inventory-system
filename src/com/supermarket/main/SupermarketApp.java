package com.supermarket.main;

import com.supermarket.service.OrderParser;
import com.supermarket.service.StockDatabase;
import java.util.Scanner;

/**
 * The main application class that runs the Supermarket Inventory System.
 * This class provides the console-based user interface (UI).
 */
public class SupermarketApp {

    public static void main(String[] args) {
        
        // 1. Initialize all the necessary objects
        Scanner scanner = new Scanner(System.in);
        OrderParser parser = new OrderParser();
        StockDatabase database = new StockDatabase(); // This will auto-load "inventory.csv"

        System.out.println("Welcome to the Supermarket Inventory Management System");
        System.out.println("====================================================");

        // 2. Start the main application loop
        boolean isRunning = true;
        while (isRunning) {
            // Display the menu
            System.out.println("\nPlease select an option:");
            System.out.println("  1. Scan Supplier Bill (Update Stock)");
            System.out.println("  2. View Current Stock Report");
            System.out.println("  3. Exit");
            System.out.print("Enter your choice (1-3): ");

            // Read the user's choice
            String choice = scanner.nextLine();

            // 3. Handle the user's choice
            switch (choice) {
                case "1":
                    // --- Scan New Order ---
                    System.out.println("\n--- Update Stock ---");
                    System.out.print("Please scan the QR code (paste the data string and press Enter): \n> ");
                    String qrCodeData = scanner.nextLine();
                    
                    // Call the parser to get the list of items
                    var parsedItems = parser.parseOrder(qrCodeData); // 'var' is modern Java
                    
                    if (parsedItems.isEmpty()) {
                        System.err.println("No valid items were parsed from the input.");
                    } else {
                        // Call the database to update the stock
                            database.updateStockFromOrder(parsedItems, qrCodeData);                    }
                    break;
                    
                case "2":
                    // --- View Stock Report ---
                    database.printStockReport();
                    break;
                    
                case "3":
                    // --- Exit ---
                    isRunning = false; // This will cause the while loop to end
                    break;
                    
                default:
                    // --- Invalid Choice ---
                    System.err.println("Invalid choice. Please enter 1, 2, or 3.");
                    break;
            }
        }

        // 4. Clean up and exit
        System.out.println("\nThank you for using the system. Goodbye!");
        scanner.close(); // Good practice to close the scanner
    }
}