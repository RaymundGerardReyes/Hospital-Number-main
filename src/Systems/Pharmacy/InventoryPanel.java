package Systems.Pharmacy;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import Systems.Dashboard.DarkMode;
import Systems.Pharmacy.Product;
import Systems.Database.DatabaseConnection;

public class InventoryPanel extends JPanel {
    private final Pharmacy pharmacy;
    private final DarkMode darkMode;
    private JTable inventoryTable;
    private DefaultTableModel inventoryTableModel;
    private JTextField searchField;
    private JButton addProductButton;
    private JButton removeProductButton;
    private JButton updateProductButton;
    private DatabaseConnection databaseConnection;

    public InventoryPanel(Pharmacy pharmacy, DarkMode darkMode) {
        this.pharmacy = pharmacy;
        this.darkMode = darkMode;
        setLayout(new BorderLayout());
        initComponents();
        layoutComponents();
        updateColors();
    }

    private void initComponents() {
        String[] columnNames = {"Product ID", "Name", "Quantity", "Unit Price", "Expiration Date"};
        inventoryTableModel = new DefaultTableModel(columnNames, 0);
        inventoryTable = new JTable(inventoryTableModel);
        searchField = new JTextField(20);
        addProductButton = new JButton("Add Product");
        removeProductButton = new JButton("Remove Product");
        updateProductButton = new JButton("Update Product");

        addProductButton.addActionListener(e -> addProduct());
        removeProductButton.addActionListener(e -> removeProduct());
        updateProductButton.addActionListener(e -> updateProduct());
    }

   private void layoutComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search: "));
        topPanel.add(searchField);
        topPanel.add(addProductButton);
        topPanel.add(removeProductButton);
        topPanel.add(updateProductButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(inventoryTable), BorderLayout.CENTER);
    }

    public void updateColors() {
        setBackground(darkMode.getBackgroundColor());
        inventoryTable.setBackground(darkMode.getCardBackgroundColor());
        inventoryTable.setForeground(darkMode.getTextColor());
        searchField.setBackground(darkMode.getCardBackgroundColor());
        searchField.setForeground(darkMode.getTextColor());
    }

   public void refreshData() {
        inventoryTableModel.setRowCount(0);
        
        // Use DatabaseConnection to get a connection and retrieve data
        try (Connection connection = DatabaseConnection.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery("SELECT * FROM products")) {

            while (resultSet.next()) {
                Object[] rowData = new Object[]{
                    resultSet.getInt("product_id"),
                    resultSet.getString("name"),
                    resultSet.getInt("quantity"),
                    resultSet.getBigDecimal("unit_price"),
                    resultSet.getDate("expiration_date")
                };
                inventoryTableModel.addRow(rowData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void addProduct() {
        JTextField nameField = new JTextField(15);
        JTextField quantityField = new JTextField(5);
        JTextField priceField = new JTextField(10);
        JTextField expirationDateField = new JTextField(10); // Expected format: yyyy-MM-dd
    
        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Unit Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Expiration Date (yyyy-MM-dd):"));
        panel.add(expirationDateField);
    
        int result = JOptionPane.showConfirmDialog(this, panel, "Add Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            Connection connection = null;
            try {
                connection = DatabaseConnection.getConnection();
                String insertQuery = "INSERT INTO products (name, quantity, unit_price, expiration_date) VALUES (?, ?, ?, ?)";
                try (PreparedStatement statement = connection.prepareStatement(insertQuery)) {
                    statement.setString(1, nameField.getText());
                    statement.setInt(2, Integer.parseInt(quantityField.getText()));
                    statement.setBigDecimal(3, new BigDecimal(priceField.getText()));
                    statement.setDate(4, java.sql.Date.valueOf(expirationDateField.getText()));
                    statement.executeUpdate();
                }
                refreshData();
            } catch (SQLException | IllegalArgumentException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to add product. Check inputs and try again.");
            } finally {
                if (connection != null) {
                    try {
                        connection.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
    


    private void removeProduct() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to remove.");
            return;
        }
    
        int productId = (int) inventoryTableModel.getValueAt(selectedRow, 0);
    
        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to remove this product?", "Confirm Remove", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                String deleteQuery = "DELETE FROM products WHERE product_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(deleteQuery)) {
                    statement.setInt(1, productId);
                    statement.executeUpdate();
                }
                refreshData();
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to remove product.");
            }
        }
    }

    private void updateProduct() {
        int selectedRow = inventoryTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a product to update.");
            return;
        }
    
        int productId = (int) inventoryTableModel.getValueAt(selectedRow, 0);
        String currentName = (String) inventoryTableModel.getValueAt(selectedRow, 1);
        int currentQuantity = (int) inventoryTableModel.getValueAt(selectedRow, 2);
        BigDecimal currentPrice = (BigDecimal) inventoryTableModel.getValueAt(selectedRow, 3);
        String currentExpirationDate = inventoryTableModel.getValueAt(selectedRow, 4).toString();
    
        JTextField nameField = new JTextField(currentName, 15);
        JTextField quantityField = new JTextField(String.valueOf(currentQuantity), 5);
        JTextField priceField = new JTextField(currentPrice.toString(), 10);
        JTextField expirationDateField = new JTextField(currentExpirationDate, 10);
    
        JPanel panel = new JPanel(new GridLayout(4, 2));
        panel.add(new JLabel("Name:"));
        panel.add(nameField);
        panel.add(new JLabel("Quantity:"));
        panel.add(quantityField);
        panel.add(new JLabel("Unit Price:"));
        panel.add(priceField);
        panel.add(new JLabel("Expiration Date (yyyy-MM-dd):"));
        panel.add(expirationDateField);
    
        int result = JOptionPane.showConfirmDialog(this, panel, "Update Product", JOptionPane.OK_CANCEL_OPTION);
        if (result == JOptionPane.OK_OPTION) {
            try (Connection connection = DatabaseConnection.getConnection()) {
                String updateQuery = "UPDATE products SET name = ?, quantity = ?, unit_price = ?, expiration_date = ? WHERE product_id = ?";
                try (PreparedStatement statement = connection.prepareStatement(updateQuery)) {
                    statement.setString(1, nameField.getText());
                    statement.setInt(2, Integer.parseInt(quantityField.getText()));
                    statement.setBigDecimal(3, new BigDecimal(priceField.getText()));
                    statement.setDate(4, java.sql.Date.valueOf(expirationDateField.getText()));
                    statement.setInt(5, productId);
                    statement.executeUpdate();
                }
                refreshData();
            } catch (SQLException | IllegalArgumentException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(this, "Failed to update product. Check inputs and try again.");
            }
        }
    }
}
    // This method will be called from the main application to refresh all panels
  /*   public void refreshAllPanels(POSPanel posPanel, SalesHistoryPanel salesHistoryPanel) {
        refreshData();
        posPanel.refreshData();
        salesHistoryPanel.refreshData();
    }
}
    */