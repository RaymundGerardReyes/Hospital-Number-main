package Systems.Laboratory;

import Systems.Database.DatabaseConnection;
import Systems.Laboratory.LaboratoryManager.LaboratoryTest;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TestSelectionPanel extends JDialog {
    private static final Logger LOGGER = Logger.getLogger(TestSelectionPanel.class.getName());
    
    private JTable testsTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JComboBox<String> availabilityFilter;
    private TableRowSorter<DefaultTableModel> sorter;
    private LaboratoryManager labManager;
    private LaboratoryTest selectedTest;

    public TestSelectionPanel(Window owner, LaboratoryManager labManager) {
        super(owner, "Select Laboratory Test", ModalityType.APPLICATION_MODAL);
        this.labManager = labManager;
        initComponents();
        layoutComponents();
        setupListeners();
        loadTestsFromDatabase();
        setSize(800, 600);
        setLocationRelativeTo(owner);
    }

    private void initComponents() {
        String[] columnNames = {"Test Name", "Category", "Test Code", "Price", "Availability"};
        tableModel = new DefaultTableModel(columnNames, 0);
        testsTable = new JTable(tableModel);
        testsTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        searchField = new JTextField(20);
        categoryFilter = new JComboBox<>();
        categoryFilter.addItem("All Categories");
        availabilityFilter = new JComboBox<>(new String[]{"All", "Available", "Unavailable"});

        sorter = new TableRowSorter<>(tableModel);
        testsTable.setRowSorter(sorter);
    }

    private void layoutComponents() {
        setLayout(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search:"));
        topPanel.add(searchField);
        topPanel.add(new JLabel("Category:"));
        topPanel.add(categoryFilter);
        topPanel.add(new JLabel("Availability:"));
        topPanel.add(availabilityFilter);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton selectButton = new JButton("Select");
        selectButton.addActionListener(e -> selectTest());
        bottomPanel.add(selectButton);

        add(topPanel, BorderLayout.NORTH);
        add(new JScrollPane(testsTable), BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void setupListeners() {
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { filter(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { filter(); }
        });

        categoryFilter.addActionListener(e -> filter());
        availabilityFilter.addActionListener(e -> filter());
    }

    private void loadTestsFromDatabase() {
        populateCategoryFilter();
        populateTableFromDatabase();
    }

    private void populateCategoryFilter() {
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement("SELECT DISTINCT category_name FROM categories ORDER BY category_name");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                categoryFilter.addItem(resultSet.getString("category_name"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading categories", e);
            JOptionPane.showMessageDialog(this, "Error loading categories: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void populateTableFromDatabase() {
        tableModel.setRowCount(0);
        String query = "SELECT t.test_name, t.test_id, t.price, c.category_name " +
                       "FROM tests t JOIN categories c ON t.category_id = c.category_id";
        
        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String testName = resultSet.getString("test_name");
                String testCode = String.format("TC-%03d", resultSet.getInt("test_id"));
                double price = resultSet.getDouble("price");
                String category = resultSet.getString("category_name");
                String availability = determineAvailability(testName);

                tableModel.addRow(new Object[]{testName, category, testCode, String.format("$%.2f", price), availability});
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading data from the database", e);
            JOptionPane.showMessageDialog(this, "Error loading data from the database: " + e.getMessage(),
                    "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String determineAvailability(String testName) {
        // This is a placeholder method. In a real application, you would implement
        // logic to determine the availability of a test based on your business rules.
        // For example, you might check against a separate inventory table, or use
        // some other criteria to determine if a test is available.
        return Math.random() < 0.8 ? "Available" : "Unavailable";
    }

    private void filter() {
        RowFilter<DefaultTableModel, Object> rf = null;
        List<RowFilter<Object,Object>> filters = new ArrayList<>();
        
        String searchText = searchField.getText().toLowerCase();
        if (!searchText.isEmpty()) {
            filters.add(RowFilter.regexFilter("(?i)" + searchText));
        }
        
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        if (!"All Categories".equals(selectedCategory)) {
            filters.add(RowFilter.regexFilter(selectedCategory, 1));
        }
        
        String selectedAvailability = (String) availabilityFilter.getSelectedItem();
        if (!"All".equals(selectedAvailability)) {
            filters.add(RowFilter.regexFilter(selectedAvailability, 4));
        }
        
        rf = RowFilter.andFilter(filters);
        sorter.setRowFilter(rf);
    }

    private void selectTest() {
        int selectedRow = testsTable.getSelectedRow();
        if (selectedRow != -1) {
            selectedRow = testsTable.convertRowIndexToModel(selectedRow);
            String testName = (String) tableModel.getValueAt(selectedRow, 0);
            String category = (String) tableModel.getValueAt(selectedRow, 1);
            String testCode = (String) tableModel.getValueAt(selectedRow, 2);
            String availability = (String) tableModel.getValueAt(selectedRow, 4);

            if ("Available".equals(availability)) {
                try {
                    selectedTest = new LaboratoryManager.LaboratoryTest(
                        testName,
                        category,
                        testCode,
                        new Date(),
                        "Pending"
                    );
                    dispose();
                } catch (IllegalArgumentException e) {
                    JOptionPane.showMessageDialog(this, "Error creating test: " + e.getMessage(), "Test Creation Error", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "The selected test is currently unavailable.", "Test Unavailable", JOptionPane.WARNING_MESSAGE);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a test.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }

    public LaboratoryTest getSelectedTest() {
        return selectedTest;
    }
}

