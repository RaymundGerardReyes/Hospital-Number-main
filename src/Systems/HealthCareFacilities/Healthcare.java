package Systems.HealthCareFacilities;

import Systems.Dashboard.DarkMode;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;

public class Healthcare extends JPanel {
    private JTable facilitiesTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, addressField, contactField, searchField;
    private JButton addFacilityButton, viewDetailsButton, clearFieldsButton, editFacilityButton;
    private JTextArea facilityDetailsArea;
    private DarkMode darkMode;
    private JLabel statusLabel;
    private JComboBox<String> facilityTypeComboBox;

    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 18);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);

    public Healthcare(DarkMode darkMode) {
        this.darkMode = darkMode;
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        initComponents();
        layoutComponents();
        updateColors();

        // Remove this duplicate setLayout call
        // setLayout(new BorderLayout(10, 10));
        // Remove this duplicate setBorder call
        // setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Remove this line as it overrides the dark mode background
        // setBackground(Color.WHITE);

        setVisible(true);
        System.out.println("Healthcare panel constructed");
    }

    private void initComponents() {
        // Initialize table
        String[] columnNames = {"Name", "Type", "Address", "Contact"};
        tableModel = new DefaultTableModel(columnNames, 0);
        facilitiesTable = new JTable(tableModel);
        facilitiesTable.setFont(MAIN_FONT);
        facilitiesTable.getTableHeader().setFont(MAIN_FONT);
        facilitiesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        facilitiesTable.getSelectionModel().addListSelectionListener(e -> updateDetailsArea());

        // Initialize input fields
        nameField = createStyledTextField();
        addressField = createStyledTextField();
        contactField = createStyledTextField();
        searchField = createStyledTextField();

        // Initialize facility type combo box
        String[] facilityTypes = {"Hospital", "Clinic", "Pharmacy", "Laboratory", "Nursing Home", "Other"};
        facilityTypeComboBox = new JComboBox<>(facilityTypes);
        facilityTypeComboBox.setFont(MAIN_FONT);

        // Initialize buttons
        addFacilityButton = createStyledButton("Add Facility");
        viewDetailsButton = createStyledButton("View Details");
        clearFieldsButton = createStyledButton("Clear Fields");
        editFacilityButton = createStyledButton("Edit Facility");

        // Initialize facility details area
        facilityDetailsArea = new JTextArea(5, 20);
        facilityDetailsArea.setFont(MAIN_FONT);
        facilityDetailsArea.setEditable(false);

        // Initialize status label
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(MAIN_FONT);

        // Add action listeners
        addFacilityButton.addActionListener(e -> {
            System.out.println("Add Facility button clicked");
            addFacility();
        });
        viewDetailsButton.addActionListener(e -> {
            System.out.println("View Details button clicked");
            viewFacilityDetails();
        });
        clearFieldsButton.addActionListener(e -> {
            System.out.println("Clear Fields button clicked");
            clearFields();
        });
        editFacilityButton.addActionListener(e -> {
            System.out.println("Edit Facility button clicked");
            editFacility();
        });

        // Add search functionality
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
        });
    }

    private void search() {
        String searchText = searchField.getText().toLowerCase();
        System.out.println("Searching for: " + searchText);
        filterTable(searchText);
    }

    private void layoutComponents() {
        // Clear the existing layout
        removeAll();
        setLayout(new BorderLayout(10, 10));

        // Header
        JLabel headerLabel = new JLabel("Healthcare Facilities Management", JLabel.CENTER);
        headerLabel.setFont(HEADER_FONT);
        add(headerLabel, BorderLayout.NORTH);

        // Main content
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        // Left panel (Add Facility Form)
        JPanel leftPanel = createLeftPanel();
        mainPanel.add(leftPanel, BorderLayout.WEST);

        // Center panel (Facilities Table)
        JScrollPane tableScrollPane = new JScrollPane(facilitiesTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(darkMode.getPrimaryColor()), "Healthcare Facilities", TitledBorder.LEFT, TitledBorder.TOP, TITLE_FONT, darkMode.getTextColor()));
        mainPanel.add(tableScrollPane, BorderLayout.CENTER);

        // Right panel (Facility Details)
        JPanel rightPanel = createRightPanel();
        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel, BorderLayout.CENTER);

        // Status bar
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);

        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        mainPanel.add(searchPanel, BorderLayout.NORTH);

        // Ensure the panel is visible and revalidate the layout
        setVisible(true);
        revalidate();
        repaint();
    }


    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(darkMode.getPrimaryColor()), "Add New Facility", TitledBorder.LEFT, TitledBorder.TOP, TITLE_FONT, darkMode.getTextColor()));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        addLabelAndField(leftPanel, gbc, "Name:", nameField);
        addLabelAndField(leftPanel, gbc, "Type:", facilityTypeComboBox);
        addLabelAndField(leftPanel, gbc, "Address:", addressField);
        addLabelAndField(leftPanel, gbc, "Contact:", contactField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addFacilityButton);
        buttonPanel.add(clearFieldsButton);
        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        leftPanel.add(buttonPanel, gbc);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(darkMode.getPrimaryColor()), "Facility Details", TitledBorder.LEFT, TitledBorder.TOP, TITLE_FONT, darkMode.getTextColor()));

        JPanel detailButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        detailButtonPanel.add(viewDetailsButton);
        detailButtonPanel.add(editFacilityButton);

        rightPanel.add(detailButtonPanel, BorderLayout.NORTH);
        rightPanel.add(new JScrollPane(facilityDetailsArea), BorderLayout.CENTER);

        return rightPanel;
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel(labelText), gbc);
        gbc.gridx = 1;
        panel.add(field, gbc);
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(15);
        textField.setFont(MAIN_FONT);
        return textField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(MAIN_FONT);
        button.setFocusPainted(false);
        return button;
    }

    private void addFacility() {
        try {
            String name = nameField.getText();
            String type = (String) facilityTypeComboBox.getSelectedItem();
            String address = addressField.getText();
            String contact = contactField.getText();

            if (name.isEmpty() || address.isEmpty() || contact.isEmpty()) {
                throw new IllegalArgumentException("Please fill in all fields");
            }

            Vector<String> row = new Vector<>();
            row.add(name);
            row.add(type);
            row.add(address);
            row.add(contact);
            tableModel.addRow(row);
            clearFields();
            updateStatus("Facility added successfully");
        } catch (IllegalArgumentException e) {
            showError(e.getMessage());
        }
    }

    private void viewFacilityDetails() {
        int selectedRow = facilitiesTable.getSelectedRow();
        if (selectedRow != -1) {
            updateDetailsArea();
        } else {
            showError("Please select a facility from the table");
        }
    }

    private void editFacility() {
        int selectedRow = facilitiesTable.getSelectedRow();
        if (selectedRow != -1) {
            String name = (String) tableModel.getValueAt(selectedRow, 0);
            String type = (String) tableModel.getValueAt(selectedRow, 1);
            String address = (String) tableModel.getValueAt(selectedRow, 2);
            String contact = (String) tableModel.getValueAt(selectedRow, 3);

            nameField.setText(name);
            facilityTypeComboBox.setSelectedItem(type);
            addressField.setText(address);
            contactField.setText(contact);

            int option = JOptionPane.showConfirmDialog(this,
                "Edit the fields and click OK to update the facility.",
                "Edit Facility",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.INFORMATION_MESSAGE);

            if (option == JOptionPane.OK_OPTION) {
                try {
                    if (nameField.getText().isEmpty() || addressField.getText().isEmpty() || contactField.getText().isEmpty()) {
                        throw new IllegalArgumentException("Please fill in all fields");
                    }

                    tableModel.setValueAt(nameField.getText(), selectedRow, 0);
                    tableModel.setValueAt(facilityTypeComboBox.getSelectedItem(), selectedRow, 1);
                    tableModel.setValueAt(addressField.getText(), selectedRow, 2);
                    tableModel.setValueAt(contactField.getText(), selectedRow, 3);
                    clearFields();
                    updateStatus("Facility updated successfully");
                } catch (IllegalArgumentException e) {
                    showError(e.getMessage());
                }
            }
        } else {
            showError("Please select a facility to edit");
        }
    }

    private void filterTable(String searchText) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        facilitiesTable.setRowSorter(sorter);

        if (searchText.length() == 0) {
            sorter.setRowFilter(null);
        } else {
            try {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
            } catch (PatternSyntaxException pse) {
                System.err.println("Bad regex pattern: " + pse.getMessage());
                showError("Invalid search pattern");
            }
        }
    }

    private void clearFields() {
        nameField.setText("");
        facilityTypeComboBox.setSelectedIndex(0);
        addressField.setText("");
        contactField.setText("");
        updateStatus("Fields cleared");
    }

    private void updateDetailsArea() {
        int selectedRow = facilitiesTable.getSelectedRow();
        if (selectedRow != -1) {
            String name = (String) tableModel.getValueAt(selectedRow, 0);
            String type = (String) tableModel.getValueAt(selectedRow, 1);
            String address = (String) tableModel.getValueAt(selectedRow, 2);
            String contact = (String) tableModel.getValueAt(selectedRow, 3);

            String details = String.format("Name: %s\nType: %s\nAddress: %s\nContact: %s",
                    name, type, address, contact);
            facilityDetailsArea.setText(details);
        } else {
            facilityDetailsArea.setText("");
        }
    }

    private void updateStatus(String message) {
        statusLabel.setText(message);
        Timer timer = new Timer(3000, e -> statusLabel.setText("Ready"));
        timer.setRepeats(false);
        timer.start();
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
    }

    public void updateColors() {
        setBackground(darkMode.getBackgroundColor());

        updateTextFieldColors(searchField);
        updateComponentColors(facilitiesTable, darkMode.getCardBackgroundColor(), darkMode.getTextColor());
        updateComponentColors(facilitiesTable.getTableHeader(), darkMode.getPrimaryColor(), darkMode.getTextColor());
        updateTextFieldColors(nameField);
        updateTextFieldColors(addressField);
        updateTextFieldColors(contactField);
        updateComponentColors(facilityTypeComboBox, darkMode.getCardBackgroundColor(), darkMode.getTextColor());
        updateButtonColors(addFacilityButton);
        updateButtonColors(viewDetailsButton);
        updateButtonColors(clearFieldsButton);
        updateButtonColors(editFacilityButton);
        updateComponentColors(facilityDetailsArea, darkMode.getCardBackgroundColor(), darkMode.getTextColor());
        statusLabel.setForeground(darkMode.getTextColor());

        updatePanelColors(this);

        revalidate();
        repaint();
    }

    private void updateComponentColors(Component component, Color backgroundColor, Color foregroundColor) {
        component.setBackground(backgroundColor);
        
        component.setForeground(foregroundColor);
    }

    private void updateTextFieldColors(JTextField textField) {
        updateComponentColors(textField, darkMode.getCardBackgroundColor(), darkMode.getTextColor());
        textField.setCaretColor(darkMode.getTextColor());
    }

    private void updateButtonColors(JButton button) {
        updateComponentColors(button, darkMode.getPrimaryColor(), darkMode.getTextColor());
    }

    public void refreshData() {
        System.out.println("Refreshing Healthcare Facilities data");
        tableModel.setRowCount(0);
        // Add logic to repopulate the table with fresh data if needed
        updateStatus("Healthcare Facilities data refreshed");
        
        // Ensure the panel is visible and revalidate the layout
        setVisible(true);
        revalidate();
        repaint();
    }

    private void updatePanelColors(JPanel panel) {
        panel.setBackground(darkMode.getBackgroundColor());
        panel.setForeground(darkMode.getTextColor());

        for (Component comp : panel.getComponents()) {
            if (comp instanceof JPanel) {
                updatePanelColors((JPanel) comp);
            } else if (comp instanceof JTextField) {
                updateTextFieldColors((JTextField) comp);
            } else if (comp instanceof JButton) {
                updateButtonColors((JButton) comp);
            } else {
                updateComponentColors(comp, darkMode.getCardBackgroundColor(), darkMode.getTextColor());
            }
        }
    }

    // Add this method to handle panel activation
    public void onActivate() {
        System.out.println("Healthcare panel activated");
        refreshData();
        // Add any other initialization or refresh logic here
    }
}