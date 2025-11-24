//src/com/supermarket/main/LoginManager.java
package com.supermarket.main;
import java.util.HashMap;

public class LoginManager {

    // Stores username -> password pairs
    private HashMap<String, String> users;

    public LoginManager() {
        this.users = new HashMap<>();
        // --- CONFIGURATION: Define your 2 users here ---
        // Format: users.put("username", "password");
        
        // User 1: The Manager
        users.put("manager", "admin123");
        
        // User 2: The Stock Keeper
        users.put("staff", "stock123");
    }

    /**
     * Checks if the provided username and password match our records.
     * @param username The entered username.
     * @param password The entered password.
     * @return true if valid, false otherwise.
     */
    public boolean authenticate(String username, String password) {
        // 1. Check if username exists
        if (this.users.containsKey(username)) {
            // 2. Check if the password matches that username
            String storedPassword = this.users.get(username);
            return storedPassword.equals(password);
        }
        return false; // Username not found
    }
}