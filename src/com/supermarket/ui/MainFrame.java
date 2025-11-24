package com.supermarket.ui;

import com.supermarket.model.OrderItem;
import com.supermarket.model.StockItem;
import com.supermarket.service.OrderParser;
import com.supermarket.service.StockDatabase;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

public class MainFrame extends JFrame {

    private final String loggedInUser;
    private final StockDatabase database;
    private final OrderParser parser;

    // --- Scan Supplier Bill (manual + optional import) ---
    private JTextField txtItemCode;
    private JTextField txtQuantity;
    private JTextField txtSupplierPrice;
    private DefaultTableModel billTableModel;
    private JTable tblBill;
    private final ArrayList<OrderItem> currentBillItems = new ArrayList<>();

    // --- Stock Report tab ---
    private DefaultTableModel stockTableModel;
    private JTable tblStock;

    // --- Price tab ---
    private JTextField txtItemCodePrice;
    private JLabel lblItemName;
    private JLabel lblCurrentPrice;
    private JTextField txtNewPrice;
    private StockItem selectedItemForPriceUpdate;

    // --- Messages area ---
    private JTextArea txtMessages;

    public MainFrame(String username, StockDatabase database, OrderParser parser) {
        this.loggedInUser = username;
        this.database = database;
        this.parser = parser;

        setTitle("Supermarket Inventory - Logged in as: " + loggedInUser);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Top title
        JLabel lblTitle = new JLabel("Supermarket Inventory System", SwingConstants.CENTER);
        lblTitle.setFont(lblTitle.getFont().deriveFont(Font.BOLD, 18f));
        add(lblTitle, BorderLayout.NORTH);

        // Tabs
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Scan Supplier Bill", createScanTab());
        tabbedPane.addTab("Stock Report", createStockTab());
        tabbedPane.addTab("View / Update Price", createPriceTab());
        add(tabbedPane, BorderLayout.CENTER);

        // Messages / logs
        txtMessages = new JTextArea(4, 80);
        txtMessages.setEditable(false);
        txtMessages.setLineWrap(true);
        txtMessages.setWrapStyleWord(true);
        JScrollPane msgScroll = new JScrollPane(txtMessages);
        msgScroll.setBorder(BorderFactory.createTitledBorder("Messages / Logs"));
        add(msgScroll, BorderLayout.SOUTH);

        // Initial stock load
        refreshStockTable();
    }

    // =====================================================
    // TAB 1: Scan Supplier Bill (UX-friendly)
    // =====================================================
    private JPanel createScanTab() {
        JPanel panel = new JPanel(new BorderLayout(8, 8));

        // Header text
        JPanel top = new JPanel(new BorderLayout(5, 5));
        JLabel lblHeader = new JLabel("Add items from supplier bill");
        lblHeader.setFont(lblHeader.getFont().deriveFont(Font.BOLD, 14f));

        JLabel lblHint = new JLabel(
                "<html>Enter each item from the supplier bill and click <b>Add to Bill</b>.<br>" +
                        "When finished, click <b>Process Bill & Update Stock</b>. " +
                        "You can also <b>Import Encoded Bill</b> if you have QR text.</html>"
        );

        top.add(lblHeader, BorderLayout.NORTH);
        top.add(lblHint, BorderLayout.SOUTH);
        panel.add(top, BorderLayout.NORTH);

        // Center: form + table
        JPanel center = new JPanel(new BorderLayout(10, 10));

        // --- Form row ---
        JPanel form = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(4, 4, 4, 4);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtItemCode = new JTextField(10);
        txtQuantity = new JTextField(5);
        txtSupplierPrice = new JTextField(7);

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        form.add(new JLabel("Item Code:"), gbc);
        gbc.gridx = 1;
        form.add(txtItemCode, gbc);

        gbc.gridx = 2;
        form.add(new JLabel("Quantity:"), gbc);
        gbc.gridx = 3;
        form.add(txtQuantity, gbc);

        gbc.gridx = 4;
        form.add(new JLabel("Supplier Price:"), gbc);
        gbc.gridx = 5;
        form.add(txtSupplierPrice, gbc);

        JButton btnAddToBill = new JButton("Add to Bill");
        btnAddToBill.addActionListener(e -> handleAddToBill());
        gbc.gridx = 6;
        form.add(btnAddToBill, gbc);

        center.add(form, BorderLayout.NORTH);

        // --- Bill table ---
        billTableModel = new DefaultTableModel(
                new Object[]{"Item Code", "Quantity", "Supplier Price"}, 0
        ) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        tblBill = new JTable(billTableModel);
        JScrollPane billScroll = new JScrollPane(tblBill);
        billScroll.setBorder(BorderFactory.createTitledBorder("Current Supplier Bill"));
        center.add(billScroll, BorderLayout.CENTER);

        panel.add(center, BorderLayout.CENTER);

        // --- Bottom buttons: Import / Process / Clear ---
        JPanel bottomButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));

        JButton btnImportEncoded = new JButton("Import Encoded Bill...");
        btnImportEncoded.addActionListener(e -> handleImportEncodedBill());

        JButton btnClearBill = new JButton("Clear Bill");
        btnClearBill.addActionListener(e -> clearCurrentBill());

        JButton btnProcessBill = new JButton("Process Bill & Update Stock");
        btnProcessBill.addActionListener(e -> handleProcessBill());

        bottomButtons.add(btnImportEncoded);
        bottomButtons.add(btnClearBill);
        bottomButtons.add(btnProcessBill);

        panel.add(bottomButtons, BorderLayout.SOUTH);

        return panel;
    }

    private void handleAddToBill() {
        String code = txtItemCode.getText().trim();
        String qtyStr = txtQuantity.getText().trim();
        String priceStr = txtSupplierPrice.getText().trim();

        if (code.isEmpty() || qtyStr.isEmpty() || priceStr.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please fill in Item Code, Quantity and Supplier Price.",
                    "Missing Data",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            int quantity = Integer.parseInt(qtyStr);
            double supplierPrice = Double.parseDouble(priceStr);

            if (quantity <= 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Quantity must be a positive number.",
                        "Invalid Quantity",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            if (supplierPrice < 0) {
                JOptionPane.showMessageDialog(
                        this,
                        "Supplier price cannot be negative.",
                        "Invalid Price",
                        JOptionPane.ERROR_MESSAGE
                );
                return;
            }

            OrderItem item = new OrderItem(code, quantity, supplierPrice);
            currentBillItems.add(item);

            billTableModel.addRow(new Object[]{code, quantity, supplierPrice});
            appendMessage("Added to bill: " + code + " x " + quantity);

            // Reset fields for next item
            txtItemCode.setText("");
            txtQuantity.setText("");
            txtSupplierPrice.setText("");
            txtItemCode.requestFocus();

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Quantity and Supplier Price must be numeric.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    private void handleImportEncodedBill() {
        JTextArea textArea = new JTextArea(5, 40);
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(textArea);

        int result = JOptionPane.showConfirmDialog(
                this,
                scrollPane,
                "Paste Encoded Bill (e.g. IT-1001,50,150.00;IT-1002,30,250.00)",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        String encoded = textArea.getText().trim();
        if (encoded.isEmpty()) {
            return;
        }

        appendMessage("Importing encoded bill: " + encoded);

        ArrayList<OrderItem> parsedItems = parser.parseOrder(encoded);
        if (parsedItems.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "No valid items found in the encoded bill.",
                    "Import Failed",
                    JOptionPane.INFORMATION_MESSAGE
            );
            return;
        }

        // Clear current bill and load parsed items
        currentBillItems.clear();
        billTableModel.setRowCount(0);

        for (OrderItem item : parsedItems) {
            currentBillItems.add(item);
            billTableModel.addRow(new Object[]{
                    item.getItemCode(),
                    item.getQuantityToAdd(),
                    item.getSupplierPrice()
            });
        }

        JOptionPane.showMessageDialog(
                this,
                "Encoded bill imported successfully.",
                "Import Successful",
                JOptionPane.INFORMATION_MESSAGE
        );
    }

    private void handleProcessBill() {
        if (currentBillItems.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "There are no items in the current bill.",
                    "Nothing to Process",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        // Build a synthetic "QR" string for logging (Code,Qty,Price;...)
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < currentBillItems.size(); i++) {
            OrderItem item = currentBillItems.get(i);
            sb.append(item.getItemCode())
                    .append(",").append(item.getQuantityToAdd())
                    .append(",").append(item.getSupplierPrice());
            if (i < currentBillItems.size() - 1) {
                sb.append(";");
            }
        }
        String encodedForLog = sb.toString();

        // Use the same backend method you already wrote
        database.updateStockFromOrder(currentBillItems, encodedForLog);

        // Refresh stock table
        refreshStockTable();

        JOptionPane.showMessageDialog(
                this,
                "Bill processed and stock updated (for all matching item codes).",
                "Success",
                JOptionPane.INFORMATION_MESSAGE
        );

        appendMessage("Processed supplier bill with " + currentBillItems.size() + " items.");
        clearCurrentBill();
    }

    private void clearCurrentBill() {
        currentBillItems.clear();
        billTableModel.setRowCount(0);
        appendMessage("Current bill cleared.");
    }

    // =====================================================
    // TAB 2: Stock Report
    // =====================================================
    private JPanel createStockTab() {
        JPanel panel = new JPanel(new BorderLayout(5, 5));

        stockTableModel = new DefaultTableModel(
                new Object[]{"Item Code", "Item Name", "Quantity", "Sale Price", "Last Updated"}, 0
        ) {
            @Override
            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return false;
            }
        };

        tblStock = new JTable(stockTableModel);
        JScrollPane scroll = new JScrollPane(tblStock);
        panel.add(scroll, BorderLayout.CENTER);

        JButton btnRefresh = new JButton("Refresh Stock Report");
        btnRefresh.addActionListener(e -> refreshStockTable());
        panel.add(btnRefresh, BorderLayout.SOUTH);

        return panel;
    }

    private void refreshStockTable() {
        if (stockTableModel == null) return;

        stockTableModel.setRowCount(0);
        for (StockItem item : database.getAllItems()) {
            stockTableModel.addRow(new Object[]{
                    item.getItemCode(),
                    item.getItemName(),
                    item.getCurrentQuantityInStock(),
                    item.getSalePrice(),
                    item.getLastUpdated()
            });
        }

        appendMessage("Stock report refreshed. Total items: " + stockTableModel.getRowCount());
    }

    // =====================================================
    // TAB 3: View / Update Price
    // =====================================================
    private JPanel createPriceTab() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        txtItemCodePrice = new JTextField(15);
        lblItemName = new JLabel("-");
        lblCurrentPrice = new JLabel("-");
        txtNewPrice = new JTextField(10);

        JButton btnFindItem = new JButton("Find Item");
        btnFindItem.addActionListener(e -> handleFindItem());

        JButton btnUpdatePrice = new JButton("Update Price");
        btnUpdatePrice.addActionListener(e -> handleUpdatePrice());

        int row = 0;

        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Item Code:"), gbc);
        gbc.gridx = 1;
        panel.add(txtItemCodePrice, gbc);
        gbc.gridx = 2;
        panel.add(btnFindItem, gbc);

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Item Name:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(lblItemName, gbc);
        gbc.gridwidth = 1;

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("Current Sale Price:"), gbc);
        gbc.gridx = 1; gbc.gridwidth = 2;
        panel.add(lblCurrentPrice, gbc);
        gbc.gridwidth = 1;

        row++;
        gbc.gridx = 0; gbc.gridy = row;
        panel.add(new JLabel("New Price:"), gbc);
        gbc.gridx = 1;
        panel.add(txtNewPrice, gbc);
        gbc.gridx = 2;
        panel.add(btnUpdatePrice, gbc);

        return panel;
    }

    private void handleFindItem() {
        String code = txtItemCodePrice.getText().trim();

        if (code.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter an item code.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        selectedItemForPriceUpdate = database.findItem(code);

        if (selectedItemForPriceUpdate == null) {
            lblItemName.setText("-");
            lblCurrentPrice.setText("-");
            txtNewPrice.setText("");
            appendMessage("Item code '" + code + "' not found.");
            JOptionPane.showMessageDialog(
                    this,
                    "Item code '" + code + "' not found in inventory.",
                    "Not Found",
                    JOptionPane.ERROR_MESSAGE
            );
        } else {
            lblItemName.setText(selectedItemForPriceUpdate.getItemName());
            lblCurrentPrice.setText(String.valueOf(selectedItemForPriceUpdate.getSalePrice()));
            txtNewPrice.setText("");
            appendMessage("Found item '" + selectedItemForPriceUpdate.getItemName() + "' for code " + code + ".");
        }
    }

    private void handleUpdatePrice() {
        if (selectedItemForPriceUpdate == null) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please find an item first before updating its price.",
                    "No Item Selected",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String newPriceStr = txtNewPrice.getText().trim();
        if (newPriceStr.isEmpty()) {
            JOptionPane.showMessageDialog(
                    this,
                    "Please enter a new price.",
                    "Input Required",
                    JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        try {
            double newPrice = Double.parseDouble(newPriceStr);
            String code = selectedItemForPriceUpdate.getItemCode();

            database.updateItemPrice(code, newPrice);
            lblCurrentPrice.setText(String.valueOf(selectedItemForPriceUpdate.getSalePrice()));
            refreshStockTable();

            appendMessage("Updated price for " + selectedItemForPriceUpdate.getItemName()
                    + " to " + newPrice);

            JOptionPane.showMessageDialog(
                    this,
                    "Price updated successfully.",
                    "Success",
                    JOptionPane.INFORMATION_MESSAGE
            );
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(
                    this,
                    "Invalid price. Please enter a numeric value.",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE
            );
        }
    }

    // =====================================================
    // Helper: logging area
    // =====================================================
    private void appendMessage(String msg) {
        if (txtMessages == null) return;
        txtMessages.append(msg + "\n");
        txtMessages.setCaretPosition(txtMessages.getDocument().getLength());
    }
}
