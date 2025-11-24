//src/com/supermarket/exception/ItemNotFoundException.java
package com.supermarket.exception;

/**
 * A custom exception that is thrown when an item from a supplier
 * order cannot be found in our main stock database.
 */
public class ItemNotFoundException extends Exception {

    /**
     * Constructor that takes the item code that was not found.
     * @param itemCode The item code that does not exist in the database.
     */
    public ItemNotFoundException(String itemCode) {
        // Pass a descriptive message to the parent Exception class
        super("Error: Item with code '" + itemCode + "' was not found in the database.");
    }
}
