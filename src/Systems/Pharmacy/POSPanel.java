/*package Systems.Pharmacy;

import Systems.Dashboard.DarkMode;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class POSPanel extends JPanel {
    private final Pharmacy pharmacy;
    private final DarkMode darkMode;
    private JTextField searchField;
    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    private JLabel totalPriceLabel;
    private JComboBox<String> paymentMethodCombo;
    private JTextField hospitalIdField;
    private JButton finalizePaymentButton;
    private List<CartItem> cartItems;
    private JTextField amountReceivedField;

    public POSPanel(Pharmacy pharmacy, DarkMode darkMode) {
        this.pharmacy = pharmacy;
        this.darkMode = darkMode;
        this.cartItems = new ArrayList<>();
        setSize(1000, 600);
        setLayout(new BorderLayout());

        initComponents();
        layoutComponents();
        updateColors();
    }

    private void initComponents() {
        searchField = new JTextField(20);
        
        String[] columnNames = {"Product Name", "Quantity", "Unit Price", "Total"};
        cartTableModel = new DefaultTableModel(columnNames, 0);
        cartTable = new JTable(cartTableModel);

        totalPriceLabel = new JLabel("Total: $0.00");
        paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "Credit", "Debit"});
        hospitalIdField = new JTextField(10);
        amountReceivedField = new JTextField(10);
        finalizePaymentButton = new JButton("Complete Sale");

        finalizePaymentButton.addActionListener(e -> handleCheckout());
    }

    private void layoutComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search: "));
        topPanel.add(searchField);

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        bottomPanel.add(totalPriceLabel);
        bottomPanel.add(new JLabel("Payment Method: "));
        bottomPanel.add(paymentMethodCombo);
        bottomPanel.add(new JLabel("Hospital ID: "));
        bottomPanel.add(hospitalIdField);
        bottomPanel.add(new JLabel("Amount Received: "));
        bottomPanel.add(amountReceivedField);
        bottomPanel.add(finalizePaymentButton);

        add(topPanel, BorderLayout.NORTH);
        add(cartPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    public void updateColors() {
        setBackground(darkMode.getBackgroundColor());
        cartTable.setBackground(darkMode.getCardBackgroundColor());
        cartTable.setForeground(darkMode.getTextColor());
        searchField.setBackground(darkMode.getCardBackgroundColor());
        searchField.setForeground(darkMode.getTextColor());
        paymentMethodCombo.setBackground(darkMode.getCardBackgroundColor());
        paymentMethodCombo.setForeground(darkMode.getTextColor());
        hospitalIdField.setBackground(darkMode.getCardBackgroundColor());
        hospitalIdField.setForeground(darkMode.getTextColor());
        amountReceivedField.setBackground(darkMode.getCardBackgroundColor());
        amountReceivedField.setForeground(darkMode.getTextColor());
    }

    public void refreshData() {
        cartTableModel.setRowCount(0);
        for (CartItem item : cartItems) {
            cartTableModel.addRow(new Object[]{
                item.getProductName(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getQuantity() * item.getUnitPrice()
            });
        }
        updateTotalPrice();
    }

    private void clearCart() {
        cartItems.clear();
        refreshData();
    }

    private void handleCheckout() {
        String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
        String hospitalId = hospitalIdField.getText();
        double total = calculateTotalPrice();

        if (paymentMethod.equals("Cash")) {
            try {
                double amountReceived = Double.parseDouble(amountReceivedField.getText());
                if (amountReceived >= total) {
                    double change = amountReceived - total;
                    Receipt receipt = pharmacy.getPointOfSale().finalizeSale(cartItems, paymentMethod, hospitalId, total);
                    JOptionPane.showMessageDialog(this, String.format("Transaction complete. Change: $%.2f\nReceipt Number: %s", change, receipt.getReceiptNumber()));
                    clearCart();
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient amount received", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            Receipt receipt = pharmacy.getPointOfSale().finalizeSale(cartItems, paymentMethod, hospitalId, total);
            JOptionPane.showMessageDialog(this, String.format("Transaction complete.\nReceipt Number: %s", receipt.getReceiptNumber()));
            clearCart();
        }
    }

    private void updateTotalPrice() {
        double total = calculateTotalPrice();
        totalPriceLabel.setText(String.format("Total: $%.2f", total));
    }

    private double calculateTotalPrice() {
        return cartItems.stream().mapToDouble(item -> item.getQuantity() * item.getUnitPrice()).sum();
    }

    private void showReceipt(Receipt receipt) {
        JOptionPane.showMessageDialog(this, receipt.toString(), "Receipt", JOptionPane.INFORMATION_MESSAGE);
    }

    // Inner class to represent cart items
    public static class CartItem {
        private final String productName;
        private final int quantity;
        private final double unitPrice;

        public CartItem(String productName, int quantity, double unitPrice) {
            this.productName = productName;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
        }

        public String getProductName() { return productName; }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
    }
}
    */