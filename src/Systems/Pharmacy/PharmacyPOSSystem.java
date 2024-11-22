package Systems.Pharmacy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class PharmacyPOSSystem extends JFrame {
    private JTextField searchField;
    private JTable productTable;
    private JTable cartTable;
    private JLabel totalLabel;
    private JComboBox<String> paymentMethodCombo;
    private JTextField amountReceivedField;
    private JButton checkoutButton;
    private JButton searchButton;

    private List<Product> products;
    private List<CartItem> cart;
    private DefaultTableModel productTableModel;
    private DefaultTableModel cartTableModel;

    public PharmacyPOSSystem() {
        setTitle("Pharmacy POS System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeProducts();
        initializeComponents();
        layoutComponents();

        setVisible(true);
    }

    private void initializeProducts() {
        products = new ArrayList<>();
        products.add(new Product(1, "Aspirin 500mg", "Painkillers", 5.99, 100));
        products.add(new Product(2, "Ibuprofen 200mg", "Painkillers", 6.99, 80));
        products.add(new Product(3, "Vitamin C 1000mg", "Vitamins", 8.99, 150));
        products.add(new Product(4, "Amoxicillin 500mg", "Antibiotics", 12.99, 50));
        products.add(new Product(5, "Loratadine 10mg", "Allergy", 7.99, 70));

        cart = new ArrayList<>();
    }

    private void initializeComponents() {
        searchField = new JTextField(20);
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchProducts());

        productTableModel = new DefaultTableModel(new Object[]{"Name", "Price", "Stock", "Action"}, 0);
        productTable = new JTable(productTableModel);
        updateProductTable(products);

        cartTableModel = new DefaultTableModel(new Object[]{"Product", "Quantity", "Price", "Actions"}, 0);
        cartTable = new JTable(cartTableModel);

        totalLabel = new JLabel("Total: $0.00");
        paymentMethodCombo = new JComboBox<>(new String[]{"Cash", "Credit/Debit Card", "Mobile Payment"});
        amountReceivedField = new JTextField(10);
        checkoutButton = new JButton("Complete Transaction");
        checkoutButton.addActionListener(e -> handleCheckout());
    }

    private void layoutComponents() {
        JPanel searchPanel = new JPanel();
        searchPanel.add(new JLabel("Search Products:"));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);
        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> searchProducts());
    

        JPanel productPanel = new JPanel(new BorderLayout());
        productPanel.add(searchPanel, BorderLayout.NORTH);
        productPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        JPanel cartPanel = new JPanel(new BorderLayout());
        cartPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel checkoutPanel = new JPanel(new GridLayout(0, 2));
        checkoutPanel.add(totalLabel);
        checkoutPanel.add(new JLabel("Payment Method:"));
        checkoutPanel.add(paymentMethodCombo);
        checkoutPanel.add(new JLabel("Amount Received:"));
        checkoutPanel.add(amountReceivedField);
        checkoutPanel.add(checkoutButton);

        cartPanel.add(checkoutPanel, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, productPanel, cartPanel);
        splitPane.setDividerLocation(500);

        add(splitPane, BorderLayout.CENTER);
    }

    private void searchProducts() {
        String searchTerm = searchField.getText().toLowerCase();
        List<Product> filteredProducts = products.stream()
                .filter(p -> p.name.toLowerCase().contains(searchTerm) || p.category.toLowerCase().contains(searchTerm))
                .toList();
        updateProductTable(filteredProducts);
    }

    private void updateProductTable(List<Product> productsToShow) {
        productTableModel.setRowCount(0);
        for (Product product : productsToShow) {
            JButton addButton = new JButton("Add to Cart");
            addButton.addActionListener(e -> addToCart(product));
            productTableModel.addRow(new Object[]{product.name, String.format("$%.2f", product.price), product.stock, addButton});
        }
    }

    private void addToCart(Product product) {
        for (CartItem item : cart) {
            if (item.product.id == product.id) {
                item.quantity++;
                updateCartTable();
                return;
            }
        }
        cart.add(new CartItem(product, 1));
        updateCartTable();
    }

    private void updateCartTable() {
        cartTableModel.setRowCount(0);
        double total = 0;
        for (CartItem item : cart) {
            JButton removeButton = new JButton("Remove");
            removeButton.addActionListener(e -> removeFromCart(item));
            cartTableModel.addRow(new Object[]{item.product.name, item.quantity, String.format("$%.2f", item.product.price * item.quantity), removeButton});
            total += item.product.price * item.quantity;
        }
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    private void removeFromCart(CartItem item) {
        cart.remove(item);
        updateCartTable();
    }

    private void handleCheckout() {
        String paymentMethod = (String) paymentMethodCombo.getSelectedItem();
        double total = cart.stream().mapToDouble(item -> item.product.price * item.quantity).sum();

        if (paymentMethod.equals("Cash")) {
            try {
                double amountReceived = Double.parseDouble(amountReceivedField.getText());
                if (amountReceived >= total) {
                    double change = amountReceived - total;
                    JOptionPane.showMessageDialog(this, String.format("Transaction complete. Change: $%.2f", change));
                    clearCart();
                } else {
                    JOptionPane.showMessageDialog(this, "Insufficient amount received", "Error", JOptionPane.ERROR_MESSAGE);
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Invalid amount entered", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Transaction complete");
            clearCart();
        }
    }

    private void clearCart() {
        cart.clear();
        updateCartTable();
        amountReceivedField.setText("");
    }

    private static class Product {
        int id;
        String name;
        String category;
        double price;
        int stock;

        Product(int id, String name, String category, double price, int stock) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
            this.stock = stock;
        }
    }

    private static class CartItem {
        Product product;
        int quantity;

        CartItem(Product product, int quantity) {
            this.product = product;
            this.quantity = quantity;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PharmacyPOSSystem::new);
    }
}