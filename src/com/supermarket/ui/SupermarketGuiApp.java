package com.supermarket.ui;

import com.supermarket.ui.LoginFrame;
import javax.swing.SwingUtilities;

public class SupermarketGuiApp {

    public static void main(String[] args) {
        // Always start Swing in the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
