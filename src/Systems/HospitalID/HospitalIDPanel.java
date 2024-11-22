package Systems.HospitalID;

import Systems.Dashboard.DarkMode;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLSyntaxErrorException;

import Systems.Database.DatabaseConnection;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.text.JTextComponent;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.regex.Pattern;

public class HospitalIDPanel extends JPanel {
    private DarkMode darkMode;
    private JTextField LastNameField, FirstNameField, MiddleNameField, ExtensionNameField, streetAddressField, cityField, zipCodeField;
    private JFormattedTextField birthdayField, phoneField, emergencyContactPhoneField;
    private JTextArea notesArea;
    private JComboBox<String> sexComboBox, stateComboBox;
    private JTextField hospitalIdField, ageField, emailField;
    private JTextField emergencyContactNameField, emergencyContactRelationshipField;
    private JTextField insuranceProviderField, policyNumberField;
    private JButton generateButton, clearButton, saveButton;
    private JTextArea outputArea;
    private Map<String, String> patientDatabase;

    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Color FONT_EMERALD_COLOR = new Color(0, 103, 66);
    

    public HospitalIDPanel(DarkMode darkMode) {
        this.darkMode = darkMode;
        this.patientDatabase = new HashMap<>();
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initComponents();
        layoutComponents();
        addListeners();
        updateColors(darkMode);
    }

    

    private Connection getConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new SQLException("MySQL JDBC Driver not found", e);
        }
    }

    private void initComponents() {
        LastNameField = createStyledTextField();
        FirstNameField = createStyledTextField();
        MiddleNameField = createStyledTextField();
        ExtensionNameField = createStyledTextField();

        streetAddressField = createStyledTextField();
        cityField = createStyledTextField();
        zipCodeField = createStyledTextField();

        try {
            MaskFormatter birthdayFormatter = new MaskFormatter("##/##/####");
            birthdayField = new JFormattedTextField(birthdayFormatter);
            birthdayField.setFont(MAIN_FONT);

            MaskFormatter phoneFormatter = new MaskFormatter("####-###-####");
            phoneField = new JFormattedTextField(phoneFormatter);
            phoneField.setFont(MAIN_FONT);

            MaskFormatter emergencyPhoneFormatter = new MaskFormatter("####-###-####");
            emergencyContactPhoneField = new JFormattedTextField(emergencyPhoneFormatter);
            emergencyContactPhoneField.setFont(MAIN_FONT);
        } catch (ParseException e) {
            e.printStackTrace();
            emergencyContactPhoneField = new JFormattedTextField();
            emergencyContactPhoneField.setFont(MAIN_FONT);
        }

        sexComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other", "Prefer not to say"});
        sexComboBox.setFont(MAIN_FONT);

        String[] regions = {
            "Select Region",
            "NCR - National Capital Region", 
            "CAR - Cordillera Administrative Region", 
            "Region I - Ilocos Region", 
            "Region II - Cagayan Valley", 
            "Region III - Central Luzon", 
            "Region IV-A - CALABARZON", 
            "Region IV-B - MIMAROPA", 
            "Region V - Bicol Region", 
            "Region VI - Western Visayas", 
            "Region VII - Central Visayas", 
            "Region VIII - Eastern Visayas", 
            "Region IX - Zamboanga Peninsula", 
            "Region X - Northern Mindanao", 
            "Region XI - Davao Region", 
            "Region XII - SOCCSKSARGEN", 
            "Region XIII - Caraga", 
            "BARMM - Bangsamoro Autonomous Region in Muslim Mindanao"
        };
        
        
        stateComboBox = new JComboBox<>(regions);
        stateComboBox.setFont(MAIN_FONT);

        notesArea = new JTextArea(4, 20);
        notesArea.setFont(MAIN_FONT);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);

        hospitalIdField = createStyledTextField();
        hospitalIdField.setEditable(false);
        ageField = createStyledTextField();
        ageField.setEditable(false);
        emailField = createStyledTextField();

        emergencyContactNameField = createStyledTextField();
        emergencyContactRelationshipField = createStyledTextField();

        insuranceProviderField = createStyledTextField();
        policyNumberField = createStyledTextField();

        generateButton = createStyledButton("Generate Hospital ID");
        clearButton = createStyledButton("Clear Fields");
        saveButton = createStyledButton("Save Patient Info");

        outputArea = new JTextArea(10, 30);
        outputArea.setEditable(false);
        outputArea.setFont(MAIN_FONT);
    }

    private void layoutComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBorder(createTitledBorder("Hospital ID Registration"));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(5, 5, 5, 5);

        addLabelAndField(inputPanel, gbc, "Last Name:", LastNameField);
        addLabelAndField(inputPanel, gbc, "First Name:", FirstNameField);
        addLabelAndField(inputPanel, gbc, "Middle Name:", MiddleNameField);
        addLabelAndField(inputPanel, gbc, "Extension Name: (If applicable)", ExtensionNameField);
        addLabelAndField(inputPanel, gbc, "Birthday:", birthdayField);
        addLabelAndField(inputPanel, gbc, "Age:", ageField);
        addLabelAndField(inputPanel, gbc, "Sex:", sexComboBox);
        addLabelAndField(inputPanel, gbc, "Phone:", phoneField);
        addLabelAndField(inputPanel, gbc, "Email:", emailField);
        addLabelAndField(inputPanel, gbc, "Street Address:", streetAddressField);
        addLabelAndField(inputPanel, gbc, "City:", cityField);
        addLabelAndField(inputPanel, gbc, "State:", stateComboBox);
        addLabelAndField(inputPanel, gbc, "ZIP Code:", zipCodeField);
        addLabelAndField(inputPanel, gbc, "Emergency Contact Name:", emergencyContactNameField);
        addLabelAndField(inputPanel, gbc, "Emergency Contact Relationship:", emergencyContactRelationshipField);
        addLabelAndField(inputPanel, gbc, "Emergency Contact Phone:", emergencyContactPhoneField);
        addLabelAndField(inputPanel, gbc, "Insurance Provider:", insuranceProviderField);
        addLabelAndField(inputPanel, gbc, "Policy Number:", policyNumberField);
        addLabelAndField(inputPanel, gbc, "Medical History:", new JScrollPane(notesArea));
        addLabelAndField(inputPanel, gbc, "Hospital ID:", hospitalIdField);

        JScrollPane scrollPane = new JScrollPane(inputPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        buttonPanel.add(generateButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(clearButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel outputPanel = new JPanel(new BorderLayout());
        outputPanel.setBorder(createTitledBorder("System Output"));
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        add(outputPanel, BorderLayout.SOUTH);
    }

    private void addLabelAndField(JPanel panel, GridBagConstraints gbc, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy++;
        panel.add(new JLabel(labelText), gbc);

        gbc.gridx = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.weightx = 1.0;
        panel.add(field, gbc);
        gbc.weightx = 0.0;
        gbc.fill = GridBagConstraints.NONE;
    }

    private void addListeners() {
        generateButton.addActionListener(e -> generateHospitalId());
        clearButton.addActionListener(e -> clearFields());
        saveButton.addActionListener(e -> savePatientInfo());
        birthdayField.addPropertyChangeListener("value", evt -> updateAge());
    }

    private void updateAge() {
        String birthdayText = birthdayField.getText();
        if (isValidDate(birthdayText)) {
            LocalDate birthDate = LocalDate.parse(birthdayText, DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            int age = Period.between(birthDate, LocalDate.now()).getYears();
            ageField.setText(String.valueOf(age));
        } else {
            ageField.setText("");
        }
    }

    private void generateHospitalId() {
        String lastName = LastNameField.getText().trim();
        String birthday = birthdayField.getText().trim();
        String age = ageField.getText().trim();

        if (checkExistingPatient(lastName, birthday, age)) {
            showMessage("Information already exists. Patient is already registered.", true);
            return;
        }

        if (!validateFields()) {
            return;
        }

        String newId = generateUniqueId();
        hospitalIdField.setText(newId);
        showMessage("New patient registered. Hospital ID: " + newId, false);
    }


    private static final String DB_URL = "jdbc:mysql://localhost:3306/hospital_management";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Raymund@Estaca01"; // Replace with your actual password

    
   // Method to check if a patient with the same Last Name, Birthday, and Age exists in the database
   private boolean checkExistingPatient(String lastName, String birthday, String age) {
    String query = "SELECT COUNT(*) FROM patients WHERE last_name = ? AND birthday = STR_TO_DATE(?, '%m/%d/%Y') AND age = ?";
    try (Connection conn = getConnection();
         PreparedStatement pstmt = conn.prepareStatement(query)) {
        
        pstmt.setString(1, lastName);
        pstmt.setString(2, birthday);
        pstmt.setString(3, age);

        try (ResultSet rs = pstmt.executeQuery()) {
            return rs.next() && rs.getInt(1) > 0;
        }
    } catch (SQLException e) {
        handleDatabaseError("Error checking existing patient", e);
        return false;
    }
}


    private boolean validateFields() {
        if (!isValidDate(birthdayField.getText())) {
            showMessage("Please enter a valid birthday (MM/DD/YYYY).", true);
            return false;
        }
        if (ageField.getText().isEmpty() || !ageField.getText().matches("^\\d+$")) {
            showMessage("Please enter a valid age (positive integer).", true);
            return false;
        }
        if (!isValidEmail(emailField.getText())) {
            showMessage("Please enter a valid email address.", true);
            return false;
        }
        if (streetAddressField.getText().trim().isEmpty()) {
            showMessage("Please enter a street address.", true);
            return false;
        }
        if (cityField.getText().trim().isEmpty()) {
            showMessage("Please enter a city.", true);
            return false;
        }
        if (!zipCodeField.getText().matches("^\\d{5}(-\\d{4})?$")) {
            showMessage("Please enter a valid ZIP code (5 digits or 5+4 format).", true);
            return false;
        }
        if (emergencyContactNameField.getText().trim().isEmpty()) {
            showMessage("Please enter an emergency contact name.", true);
            return false;
        }
        if (emergencyContactRelationshipField.getText().trim().isEmpty()) {
            showMessage("Please enter the emergency contact's relationship.", true);
            return false;
        }
        if (!isValidPhoneNumber(emergencyContactPhoneField.getText())) {
            showMessage("Please enter a valid emergency contact phone number (####-###-####).", true);
            return false;
        }
        if (!isValidPhoneNumber(phoneField.getText())) {
            showMessage("Please enter a valid phone number (####-###-####).", true);
            return false;
        }

        return true;
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        String phoneNumberRegex = "^\\d{4}-\\d{3}-\\d{4}$";
        return phoneNumber != null && phoneNumber.matches(phoneNumberRegex);
    }

  
    private boolean isValidDate(String date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        sdf.setLenient(false);
        try {
            sdf.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }


    public void refreshData() {
        // Logic to refresh the data displayed in the panel
        // For example, you can fetch the updated data from a database or API
        // and update the UI components accordingly

        // Example:
        // Fetch updated data from a database or API
        // List<HospitalID> updatedHospitalIDs = fetchUpdatedHospitalIDs();

        // Clear the existing data in the panel
        // this.removeAll();

        // Add the updated data to the panel
        // for (HospitalID hospitalID : updatedHospitalIDs) {
        //     JLabel label = new JLabel(hospitalID.toString());
        //     this.add(label);
        // }

        // Revalidate and repaint the panel
        // this.revalidate();
        // this.repaint();
    }

    private boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        return email != null && email.matches(emailRegex);
    }

    private String generateUniqueId() {
        Random random = new Random();
        StringBuilder idBuilder = new StringBuilder();
    
        for (int i = 0; i < 10; i++) { // Generates a 10-digit random number
            idBuilder.append(random.nextInt(10)); // Append a random digit (0-9)
        }
    
        return idBuilder.toString();
    }
    

    private void savePatientInfo() {
        String hospitalId = hospitalIdField.getText().trim();
        if (hospitalId.isEmpty()) {
            showMessage("Please generate a Hospital ID first.", true);
            return;
        }
    
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO patients (hospital_id, last_name, first_name, middle_name, extension_name, birthday, age, sex, phone, email, " +
                     "street_address, city, state, zip_code, emergency_contact_name, emergency_contact_relationship, emergency_contact_phone, " +
                     "insurance_provider, policy_number, additional_notes) " +
                     "VALUES (?, ?, ?, ?, ?, STR_TO_DATE(?, '%m/%d/%Y'), ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) " +
                     "ON DUPLICATE KEY UPDATE " +
                     "last_name = VALUES(last_name), first_name = VALUES(first_name), middle_name = VALUES(middle_name), extension_name = VALUES(extension_name), " +
                     "birthday = STR_TO_DATE(VALUES(birthday), '%m/%d/%Y'), age = VALUES(age), sex = VALUES(sex), phone = VALUES(phone), " +
                     "email = VALUES(email), street_address = VALUES(street_address), city = VALUES(city), state = VALUES(state), " +
                     "zip_code = VALUES(zip_code), emergency_contact_name = VALUES(emergency_contact_name), emergency_contact_relationship = VALUES(emergency_contact_relationship), " +
                     "emergency_contact_phone = VALUES(emergency_contact_phone), insurance_provider = VALUES(insurance_provider), " +
                     "policy_number = VALUES(policy_number), additional_notes = VALUES(additional_notes)")) {
    
            // Set values for the prepared statement
            pstmt.setString(1, hospitalId);
            pstmt.setString(2, LastNameField.getText().trim());
            pstmt.setString(3, FirstNameField.getText().trim());
            pstmt.setString(4, MiddleNameField.getText().trim());
            pstmt.setString(5, ExtensionNameField.getText().trim());
            pstmt.setString(6, birthdayField.getText());
            pstmt.setInt(7, Integer.parseInt(ageField.getText().trim()));
            pstmt.setString(8, (String) sexComboBox.getSelectedItem());
            pstmt.setString(9, phoneField.getText().trim());
            pstmt.setString(10, emailField.getText().trim());
            pstmt.setString(11, streetAddressField.getText().trim());
            pstmt.setString(12, cityField.getText().trim());
            pstmt.setString(13, (String) stateComboBox.getSelectedItem());
            pstmt.setString(14, zipCodeField.getText().trim());
            pstmt.setString(15, emergencyContactNameField.getText().trim());
            pstmt.setString(16, emergencyContactRelationshipField.getText().trim());
            pstmt.setString(17, emergencyContactPhoneField.getText().trim());
            pstmt.setString(18, insuranceProviderField.getText().trim());
            pstmt.setString(19, policyNumberField.getText().trim());
            pstmt.setString(20, notesArea.getText().trim());
    
            int affectedRows = pstmt.executeUpdate();
    
            if (affectedRows > 0) {
                showMessage("Patient information saved successfully to the database.", false);
            } else {
                showMessage("Failed to save patient information to the database.", true);
            }
        } catch (SQLException e) {
            handleDatabaseError("Error saving patient information", e);
        }
    }
    

    private void handleDatabaseError(String message, SQLException e) {
        e.printStackTrace();
        String errorDetails = e.getMessage();
        if (e instanceof SQLSyntaxErrorException) {
            errorDetails = "Database syntax error. Please check your SQL query.";
        } else if (e instanceof SQLIntegrityConstraintViolationException) {
            errorDetails = "Database constraint violation. Possible duplicate entry.";
        }
        showMessage(message + ": " + errorDetails, true);
    }
    

    private void clearFields() {
        LastNameField.setText("");
        FirstNameField.setText("");
        MiddleNameField.setText("");
        ExtensionNameField.setText("");
        birthdayField.setText("");
        ageField.setText("");
        sexComboBox.setSelectedIndex(0);
        phoneField.setText("");
        emailField.setText("");
        streetAddressField.setText("");
        cityField.setText("");
        stateComboBox.setSelectedIndex(0);
        zipCodeField.setText("");
        emergencyContactNameField.setText("");
        emergencyContactRelationshipField.setText("");
        emergencyContactPhoneField.setText("");
        insuranceProviderField.setText("");
        policyNumberField.setText("");
        notesArea.setText("");
        hospitalIdField.setText("");
        showMessage("All fields cleared.", false);
    }

    private void showMessage(String message, boolean isError) {
        outputArea.setForeground(isError ? Color.RED : darkMode.getTextColor());
        outputArea.setText(message);
    }

    public void updateColors(DarkMode darkMode) {
        setBackground(darkMode.getBackgroundColor());
        updateComponentColors(this);
    }

    private void updateComponentColors(Container container) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(darkMode.getBackgroundColor());
                updateComponentColors((Container) comp);
            } else if (comp instanceof JLabel) {
                comp.setForeground(darkMode.getTextColor());
            } else if (comp instanceof JTextComponent) {
                comp.setBackground(darkMode.getCardBackgroundColor());
                comp.setForeground(darkMode.getTextColor());
                ((JTextComponent) comp).setCaretColor(darkMode.getTextColor());
            } else if (comp instanceof JButton) {
                comp.setBackground(darkMode.getPrimaryColor());
                comp.setForeground(Color.WHITE);
            } else if (comp instanceof JComboBox) {
                comp.setBackground(darkMode.getCardBackgroundColor());
                comp.setForeground(darkMode.getTextColor());
            } else if (comp instanceof JScrollPane) {
                comp.setBackground(darkMode.getBackgroundColor());
                updateComponentColors(((JScrollPane) comp).getViewport());
            }
        }
    }

    private JTextField createStyledTextField() {
        JTextField textField = new JTextField(20);
        textField.setFont(MAIN_FONT);
        return textField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(MAIN_FONT);
        button.setFocusPainted(false);
        button.setBackground(FONT_EMERALD_COLOR);
        button.setForeground(Color.WHITE);
        button.setOpaque(true);
        button.setBorderPainted(false);
        
        // Remove the hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                // Do nothing
            }
    
            public void mouseExited(java.awt.event.MouseEvent evt) {
                // Do nothing
            }
        });
    
        return button;
    }


    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(darkMode.getBorderColor()),
                title,
                TitledBorder.LEFT,
                TitledBorder.TOP,
                TITLE_FONT,
                darkMode.getTextColor()
        );
        border.setTitleColor(darkMode.getTextColor());
        return border;
    }
}
