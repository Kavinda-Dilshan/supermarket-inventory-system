package com.supermarket.ui;

import com.supermarket.main.LoginManager;
import com.supermarket.service.OrderParser;
import com.supermarket.service.StockDatabase;
import javax.swing.*;
import java.awt.*;

public class LoginFrame extends JFrame {

    private final JTextField txtUsername;
    private final JPasswordField txtPassword;
    private final JLabel lblStatus;

    private final LoginManager loginManager;

    public LoginFrame() {
        this.loginManager = new LoginManager();

        setTitle("Supermarket Inventory - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 250);
        setLocationRelativeTo(null); // center on screen
        setLayout(new BorderLayout(10, 10));

        // ---------- Title label ----------
        JLabel lblTitle = new JLabel("Supermarket Inventory System", SwingConstants.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 16f));
        add(lblTitle, BorderLayout.NORTH);

        // ---------- Form panel ----------
        JPanel panelCenter = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblUsername = new JLabel("Username:");
        JLabel lblPassword = new JLabel("Password:");

        txtUsername = new JTextField(20);
        txtPassword = new JPasswordField(20);

        gbc.gridx = 0; gbc.gridy = 0;
        panelCenter.add(lblUsername, gbc);
        gbc.gridx = 1; gbc.gridy = 0;
        panelCenter.add(txtUsername, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        panelCenter.add(lblPassword, gbc);
        gbc.gridx = 1; gbc.gridy = 1;
        panelCenter.add(txtPassword, gbc);

        add(panelCenter, BorderLayout.CENTER);

        // ---------- Bottom panel ----------
        JPanel panelBottom = new JPanel(new BorderLayout());

        lblStatus = new JLabel(" ", SwingConstants.CENTER);
        lblStatus.setForeground(Color.RED);

        JButton btnLogin = new JButton("Login");
        btnLogin.addActionListener(e -> handleLogin());

        panelBottom.add(lblStatus, BorderLayout.CENTER);
        panelBottom.add(btnLogin, BorderLayout.EAST);

        add(panelBottom, BorderLayout.SOUTH);
    }

    private void handleLogin() {
        String username = txtUsername.getText().trim();
        String password = new String(txtPassword.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            lblStatus.setText("Please enter both username and password.");
            return;
        }

        boolean ok = loginManager.authenticate(username, password);

        if (ok) {
            lblStatus.setText("Login successful!");

            // Create shared backend objects
            StockDatabase database = new StockDatabase();
            OrderParser parser = new OrderParser();

            // Open main window
            MainFrame mainFrame = new MainFrame(username, database, parser);
            mainFrame.setVisible(true);

            // Close login window
            dispose();
        } else {
            lblStatus.setText("Invalid username or password.");
        }
    }
}
