package Systems.HealthCareFacilities;

import Systems.Dashboard.DarkMode;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableRowSorter;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.PatternSyntaxException;

public class HealthcareFacilitiesPanel extends JPanel {
    private JTable facilitiesTable;
    private DefaultTableModel tableModel;
    private JTextField nameField, addressField, contactField, searchField, hospitalIdField, patientNameField;
    private JComboBox<String> facilityTypeComboBox;
    private JButton addFacilityButton, viewDetailsButton, admitButton;
    private JTextArea facilityDetailsArea;
    private JLabel statusLabel;
    private DarkMode darkMode;

    private List<Facility> facilities;

    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font HEADER_FONT = new Font("Segoe UI", Font.BOLD, 24);

    public HealthcareFacilitiesPanel(DarkMode darkMode) {
        this.darkMode = darkMode;
        this.facilities = new ArrayList<>();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        layoutComponents();
        updateColors();

        System.out.println("HealthcareFacilitiesPanel initialized.");
    }

    private void initComponents() {
        // Initialize input fields
        nameField = createStyledTextField();
        addressField = createStyledTextField();
        contactField = createStyledTextField();
        hospitalIdField = createStyledTextField();
        patientNameField = createStyledTextField();

        // Initialize facility type dropdown
        String[] facilityTypes = {"Select Facility", "Emergency Room", "Operating Room", "Admitting Room"};
        facilityTypeComboBox = new JComboBox<>(facilityTypes);
        facilityTypeComboBox.setFont(MAIN_FONT);
        facilityTypeComboBox.addActionListener(e -> updateFieldsForFacilityType());

        // Initialize buttons
        addFacilityButton = createStyledButton("Add Facility");
        addFacilityButton.addActionListener(e -> addFacility());
        viewDetailsButton = createStyledButton("View Details");
        viewDetailsButton.addActionListener(e -> viewFacilityDetails());
        admitButton = createStyledButton("Admit Patient");
        admitButton.addActionListener(e -> admitPatient());

        // Initialize table
        tableModel = new DefaultTableModel(new String[]{"Name", "Type", "Address", "Contact"}, 0);
        facilitiesTable = new JTable(tableModel);
        customizeTable();

        // Status label
        statusLabel = new JLabel("Ready");
        statusLabel.setFont(MAIN_FONT);

        // Facility details area
        facilityDetailsArea = new JTextArea(10, 20);
        facilityDetailsArea.setFont(MAIN_FONT);
        facilityDetailsArea.setEditable(false);

        // Search functionality
        searchField = createStyledTextField();
        searchField.setToolTipText("Search facilities...");
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
        });
    }

    private void layoutComponents() {
        // Header
        JLabel headerLabel = new JLabel("Healthcare Facilities Management", JLabel.CENTER);
        headerLabel.setFont(HEADER_FONT);
        add(headerLabel, BorderLayout.NORTH);

        // Main panel with JTabbedPane
        JTabbedPane tabbedPane = new JTabbedPane();
        
        // Healthcare Facilities Management tab
        JPanel facilitiesManagementPanel = new JPanel(new BorderLayout(10, 10));
        facilitiesManagementPanel.add(createSearchPanel(), BorderLayout.NORTH);
        facilitiesManagementPanel.add(createTablePanel(), BorderLayout.CENTER);
        facilitiesManagementPanel.add(createInputPanel(), BorderLayout.WEST);
        facilitiesManagementPanel.add(createDetailsPanel(), BorderLayout.EAST);
        
        tabbedPane.addTab("Healthcare Facilities", facilitiesManagementPanel);
        
        // Emergency Room tab
        tabbedPane.addTab("Emergency Room", createEmergencyRoomPanel());
        
        // Operating Room tab
        tabbedPane.addTab("Operating Room", createOperatingRoomPanel());
        
        // Admitting Room tab
        tabbedPane.addTab("Admitting Room", createAdmittingRoomPanel());

        add(tabbedPane, BorderLayout.CENTER);

        // Status panel
        JPanel statusPanel = new JPanel(new BorderLayout());
        statusPanel.add(statusLabel, BorderLayout.WEST);
        add(statusPanel, BorderLayout.SOUTH);
    }

    private JPanel createSearchPanel() {
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        return searchPanel;
    }

    private JPanel createTablePanel() {
        JScrollPane tableScrollPane = new JScrollPane(facilitiesTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Facilities"));
        return new JPanel(new BorderLayout()) {{
            add(tableScrollPane, BorderLayout.CENTER);
        }};
    }

    private JPanel createInputPanel() {
        JPanel inputPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addLabelAndField(inputPanel, gbc, "Hospital ID:", hospitalIdField);
        addLabelAndField(inputPanel, gbc, "Patient Name:", patientNameField);
        addLabelAndField(inputPanel, gbc, "Type:", facilityTypeComboBox);
        addLabelAndField(inputPanel, gbc, "Address:", addressField);
        addLabelAndField(inputPanel, gbc, "Contact:", contactField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addFacilityButton);
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(admitButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        inputPanel.add(buttonPanel, gbc);

        return inputPanel;
    }

    private JPanel createDetailsPanel() {
        JPanel detailsPanel = new JPanel(new BorderLayout(10, 10));
        JScrollPane detailsScrollPane = new JScrollPane(facilityDetailsArea);
        detailsScrollPane.setBorder(BorderFactory.createTitledBorder("Facility Details"));
        detailsPanel.add(detailsScrollPane, BorderLayout.CENTER);
        return detailsPanel;
    }

    private JPanel createEmergencyRoomPanel() {
        // Create an EmergencyRoom instance
        EmergencyRoom emergencyRoom = new EmergencyRoom("ER1", "123 Street", "123-456-7890", 10, 5);
    
        // Create the main panel
        JPanel panel = new JPanel(new BorderLayout(10, 10));
    
        // Add triage capacity and ambulance bay fields
        JPanel topPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
    
        addLabelAndField(topPanel, gbc, "Triage Capacity:", new JTextField(String.valueOf(emergencyRoom.getTriageCapacity()), 10));
        addLabelAndField(topPanel, gbc, "Ambulance Bays:", new JTextField(String.valueOf(emergencyRoom.getAmbulanceBayCount()), 10));
    
        // Add patient admission panel from EmergencyRoom
        JPanel patientAdmissionPanel = emergencyRoom.createPatientAdmissionPanel();
    
        // Add panels to the main panel
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(patientAdmissionPanel, BorderLayout.CENTER);
    
        return panel;
    }
    
    private JPanel createOperatingRoomPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addLabelAndField(panel, gbc, "Robotic Surgery:", new JCheckBox());
        addLabelAndField(panel, gbc, "Operating Tables:", new JTextField(10));

        return panel;
    }

    private JPanel createAdmittingRoomPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        addLabelAndField(panel, gbc, "Insurance Verification:", new JCheckBox());
        addLabelAndField(panel, gbc, "Bed Management:", new JCheckBox());

        return panel;
    }

    private JPanel createLeftPanel() {
        JPanel leftPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Add input fields with labels
        addLabelAndField(leftPanel, gbc, "Hospital ID:", hospitalIdField);
        addLabelAndField(leftPanel, gbc, "Patient Name:", patientNameField);
        addLabelAndField(leftPanel, gbc, "Type:", facilityTypeComboBox);
        addLabelAndField(leftPanel, gbc, "Address:", addressField);
        addLabelAndField(leftPanel, gbc, "Contact:", contactField);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(addFacilityButton);
        buttonPanel.add(viewDetailsButton);
        buttonPanel.add(admitButton);

        gbc.gridx = 0;
        gbc.gridy++;
        gbc.gridwidth = 2;
        gbc.fill = GridBagConstraints.CENTER;
        leftPanel.add(buttonPanel, gbc);

        return leftPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));

        JScrollPane detailsScrollPane = new JScrollPane(facilityDetailsArea);
        detailsScrollPane.setBorder(BorderFactory.createTitledBorder("Facility Details"));
        rightPanel.add(detailsScrollPane, BorderLayout.CENTER);

        return rightPanel;
    }

    private void addFacility() {
        String name = nameField.getText();
        String address = addressField.getText();
        String contact = contactField.getText();
        String type = (String) facilityTypeComboBox.getSelectedItem();

        if (name.isEmpty() || address.isEmpty() || contact.isEmpty() || "Select Facility".equals(type)) {
            showError("Please fill in all fields and select a facility type.");
            return;
        }

        Facility newFacility;
        try {
            switch (type) {
                case "Emergency Room" -> newFacility = new EmergencyRoom(name, address, contact, 10, 5); // Example values
                case "Operating Room" -> newFacility = new OperatingRoom(name, address, contact, true, 2);
                case "Admitting Room" -> newFacility = new AdmittingRoom(name, address, contact, true, true);
                default -> throw new IllegalArgumentException("Invalid facility type.");
            }

            facilities.add(newFacility);
            updateFacilitiesTable();
            clearFields();
            updateStatus("Facility added successfully.");
        } catch (Exception e) {
            showError("Error adding facility: " + e.getMessage());
        }
    }

    private void admitPatient() {
        try {
            String hospitalId = hospitalIdField.getText();
            String patientName = patientNameField.getText();

            if (hospitalId.isEmpty() || patientName.isEmpty()) {
                throw new IllegalArgumentException("Hospital ID and Patient Name cannot be empty.");
            }

            tableModel.addRow(new String[]{hospitalId, patientName, addressField.getText(), contactField.getText()});
            clearFields();
            updateStatus("Patient admitted successfully.");
        } catch (Exception e) {
            showError("Error admitting patient: " + e.getMessage());
        }
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
        button.setBackground(new Color(51, 153, 255)); // Light blue background
        button.setForeground(Color.WHITE); // White text
        button.setBorder(BorderFactory.createRaisedBevelBorder());
        return button;
    }

    private void customizeTable() {
        facilitiesTable.setRowHeight(25);
        facilitiesTable.setIntercellSpacing(new Dimension(10, 5));
        facilitiesTable.setShowGrid(true);
        facilitiesTable.setGridColor(new Color(200, 200, 200));
    }

    private void checkHospitalId() {
        String hospitalId = hospitalIdField.getText();
    
        // Simulate database check (replace with actual database query)
        Timer timer = new Timer(1000, e -> {
            if (Math.random() < 0.5) { // 50% chance of existing
                patientNameField.setText("Doe, John Smith");
                addressField.setText("123 Main St, Anytown, CA");
                contactField.setText("555-1234");
            } else {
                clearFields();
            }
        });
    
        timer.setRepeats(false);
        timer.start();
    }

    private void updateFieldsForFacilityType() {
        String selectedType = (String) facilityTypeComboBox.getSelectedItem();
        // Clear existing additional fields
        removeAdditionalFields();

        switch (selectedType) {
            case "Emergency Room":
                addAdditionalField("Triage Capacity", new JTextField(15));
                addAdditionalField("Ambulance Bays", new JTextField(15));
                break;
            case "Operating Room":
                addAdditionalField("Robotic Surgery", new JCheckBox());
                addAdditionalField("Operating Tables", new JTextField(15));
                break;
            case "Admitting Room":
                addAdditionalField("Insurance Verification", new JCheckBox());
                addAdditionalField("Bed Management", new JCheckBox());
                break;
        }
        revalidate();
        repaint();
    }

    private void updateFacilitiesTable() {
        tableModel.setRowCount(0);
        for (Facility facility : facilities) {
            Vector<String> row = new Vector<>();
            row.add(facility.getName());
            row.add(facility.getType());
            row.add(facility.getAddress());
            row.add(facility.getContact());
            tableModel.addRow(row);
        }
    }
    
    private void viewFacilityDetails() {
        int selectedRow = facilitiesTable.getSelectedRow();
        if (selectedRow != -1) {
            Facility selectedFacility = facilities.get(selectedRow);
            facilityDetailsArea.setText(selectedFacility.getDetails());
        } else {
            showError("Please select a facility from the table");
        }
    }

    private void search() {
        String searchText = searchField.getText().toLowerCase();
        filterTable(searchText);
    }
    
    private void filterTable(String searchText) {
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(tableModel);
        facilitiesTable.setRowSorter(sorter);
    
        try {
            if (searchText.isEmpty()) {
                sorter.setRowFilter(null);
            } else {
                sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
            }
        } catch (PatternSyntaxException pse) {
            showError("Invalid search pattern: " + pse.getMessage());
        }
    }
    
    private void clearFields() {
        nameField.setText("");
        facilityTypeComboBox.setSelectedIndex(0);
        addressField.setText("");
        contactField.setText("");
        updateStatus("Fields cleared");
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
        updateTableHeaderColors();
        updateTextFieldColors(nameField);
        updateTextFieldColors(addressField);
        updateTextFieldColors(contactField);
        updateComponentColors(facilityTypeComboBox, darkMode.getCardBackgroundColor(), darkMode.getTextColor());
        updateButtonColors(addFacilityButton);
        updateButtonColors(viewDetailsButton);
        updateComponentColors(facilityDetailsArea, darkMode.getCardBackgroundColor(), darkMode.getTextColor());
        statusLabel.setForeground(darkMode.getTextColor());

        updatePanelColors(this);

        revalidate();
        repaint();
    }

    private void updateComponentColors(Component component, Color backgroundColor, Color foregroundColor) {
        component.setBackground(backgroundColor);
        component.setForeground(foregroundColor);
        
        if (component instanceof JTable) {
            JTable table = (JTable) component;
            table.setGridColor(darkMode.isDarkModeEnabled() ? new Color(100, 100, 100) : new Color(200, 200, 200));
            table.setSelectionBackground(darkMode.isDarkModeEnabled() ? new Color(0, 102, 204) : new Color(173, 216, 230));
            table.setSelectionForeground(Color.WHITE);
        }
    }

    private void updateTextFieldColors(JTextField textField) {
        updateComponentColors(textField, darkMode.getCardBackgroundColor(), darkMode.getTextColor());
        textField.setCaretColor(darkMode.getTextColor());
    }

    private void updateButtonColors(JButton button) {
        Color buttonColor = darkMode.isDarkModeEnabled() ? new Color(0, 102, 204) : new Color(51, 153, 255);
        button.setBackground(buttonColor);
        button.setForeground(Color.WHITE);
    }

    private void updateTableHeaderColors() {
        JTableHeader header = facilitiesTable.getTableHeader();
        header.setBackground(darkMode.isDarkModeEnabled() ? new Color(60, 60, 60) : new Color(240, 240, 240));
        header.setForeground(darkMode.isDarkModeEnabled() ? Color.WHITE : Color.BLACK);
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

    public void refreshData() {
        System.out.println("Refreshing Healthcare Facilities data");
        facilities.clear();
        updateFacilitiesTable();
        updateStatus("Healthcare Facilities data refreshed");
        
        setVisible(true);
        revalidate();
        repaint();
    }

    public void onActivate() {
        System.out.println("Healthcare panel activated");
        refreshData();
        // Add any other initialization or refresh logic here
    }

    private void removeAdditionalFields() {
        Component[] components = getComponents();
        for (Component component : components) {
            if (component instanceof JPanel && ((JPanel) component).getName() != null && ((JPanel) component).getName().startsWith("additionalField")) {
                remove(component);
            }
        }
    }

    private void addAdditionalField(String labelText, JComponent field) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setName("additionalField");
        panel.add(new JLabel(labelText));
        panel.add(field);
        add(panel, BorderLayout.SOUTH);
    }
}