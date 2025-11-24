//src/com/supermarket/main/SupermarketApp.java
package com.supermarket.main;

import com.supermarket.model.StockItem;
import com.supermarket.service.OrderParser;
import com.supermarket.service.StockDatabase;
import java.util.Scanner;
import java.util.ArrayList; // added for parsed items
import com.supermarket.model.OrderItem; // added for parsed items

/**
 * The main application class that runs the Supermarket Inventory System.
 * This class provides the console-based user interface (UI) and handles login flow.
 */
public class SupermarketApp {

    public static void main(String[] args) {

        // ---------------------------------------------------------
        // STEP 1: INITIALIZE OBJECTS FIRST (Before using them!)
        // ---------------------------------------------------------
        Scanner scanner = new Scanner(System.in);
        OrderParser parser = new OrderParser();
        LoginManager loginManager = new LoginManager(); 
        
        // Database object is declared here but initialized later
        StockDatabase database;

        System.out.println("====================================================");
        System.out.println("   SUPERMARKET INVENTORY SYSTEM - LOGIN REQUIRED    ");
        System.out.println("====================================================");

        // ---------------------------------------------------------
        // STEP 2: LOGIN LOOP (Now scanner and loginManager exist)
        // ---------------------------------------------------------
        boolean isLoggedIn = false;
        while (!isLoggedIn) {
            System.out.print("\nEnter Username: ");
            String username = scanner.nextLine(); // Now scanner is valid!
            
            System.out.print("Enter Password: ");
            String password = scanner.nextLine();

            // Call the authenticate method
            if (loginManager.authenticate(username, password)) { // Now loginManager is valid!
                System.out.println("\n[SUCCESS] Login successful! Welcome, " + username + ".");
                isLoggedIn = true;
            } else {
                System.out.println("\n[ERROR] Invalid username or password. Please try again.");
            }
        }
        
        // ---------------------------------------------------------
        // STEP 3: LOAD DATABASE & START MAIN APP
        // ---------------------------------------------------------
        database = new StockDatabase(); // Only load data if login succeeds

        boolean isRunning = true;
        while (isRunning) {
            // Display the menu
            System.out.println("\nPlease select an option:");
            System.out.println("  1. Scan Supplier Bill (Update Stock)");
            System.out.println("  2. View Current Stock Report");
            System.out.println("  3. View / Update Item Price");
            System.out.println("  4. Exit");
            System.out.print("Enter your choice (1-4): ");

            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    System.out.println("\n--- Update Stock ---");
                    System.out.print("Please scan the QR code (paste the data string and press Enter): \n> ");
                    String qrCodeData = scanner.nextLine();

                    ArrayList<OrderItem> parsedItems = parser.parseOrder(qrCodeData);

                    if (parsedItems.isEmpty()) {
                        System.err.println("No valid items were parsed from the input.");
                    } else {
                        database.updateStockFromOrder(parsedItems, qrCodeData); 
                    }
                    break;

                case "2":
                    database.printStockReport();
                    break;

                case "3":
                    System.out.println("\n--- View / Update Item Price ---");
                    System.out.print("Enter the Item Code to look up: ");
                    String itemCode = scanner.nextLine();

                    StockItem item = database.findItem(itemCode);

                    if (item == null) {
                        System.err.println("Error: Item code '" + itemCode + "' not found.");
                    } else {
                        System.out.println("Found item: " + item.getItemName());
                        System.out.println("Current sale price: " + item.getSalePrice());

                        System.out.print("\nEnter new price (or leave blank to keep current): ");
                        String newPriceString = scanner.nextLine();

                        if (!newPriceString.isEmpty()) {
                            try {
                                double newPrice = Double.parseDouble(newPriceString);
                                database.updateItemPrice(itemCode, newPrice); 
                            } catch (NumberFormatException e) {
                                System.err.println("Invalid price. Please enter numbers only.");
                            }
                        } else {
                            System.out.println("Price not changed.");
                        }
                    }
                    break;

                case "4":
                    isRunning = false; 
                    break;

                default:
                    System.err.println("Invalid choice. Please enter 1, 2, 3, or 4.");
                    break;
            }
        }

        System.out.println("\nThank you for using the system. Goodbye!");
        scanner.close();
    }
}