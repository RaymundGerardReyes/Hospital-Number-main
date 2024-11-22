package Systems.Consultation;

import Systems.Database.DatabaseConnection;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.*;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableRowSorter;

public class DoctorPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(DoctorPanel.class.getName());
    private static final Color DARK_BLUE = new Color(0, 50, 100);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);

    private final DatabaseConnection dbConnection;
    private final ConsultationPanel consultationPanel;
    private final ConsultationParentPanel parentPanel;

    private JTable doctorTable;
    private DefaultTableModel model;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;
    private final Stack<Object[]> undoStack = new Stack<>();

    private DoctorManager doctorManager;
    private DoctorControlManager doctorControlManager;

    public DoctorPanel(ConsultationPanel consultationPanel, ConsultationParentPanel parentPanel, DatabaseConnection dbaseConnection) {
        super(new BorderLayout());
        this.consultationPanel = consultationPanel;
        this.parentPanel = parentPanel;
        this.dbConnection = dbaseConnection;

        initializeComponents();
        loadDoctorData();
        schedulePeriodicUpdates();
    }


    public void scheduleAppointment(String patientName, String hospitalId, int age, String sex, String specialty, String healthConcern) {
        for (int i = 0; i < model.getRowCount(); i++) {
            if (model.getValueAt(i, 1).equals(specialty) && model.getValueAt(i, 4).equals("Available")) {
                String doctorName = (String) model.getValueAt(i, 0);
                String patientConsult = (String) model.getValueAt(i, 3);
                String[] parts = patientConsult.split(" / ");
                int currentPatients = Integer.parseInt(parts[0]);
                int maxPatients = Integer.parseInt(parts[1]);

                if (currentPatients < maxPatients) {
                    currentPatients++;
                    model.setValueAt(currentPatients + " / " + maxPatients, i, 3);
                    if (currentPatients == maxPatients) {
                        model.setValueAt("Not Available", i, 4);
                    }

                    scheduleAppointmentTime(doctorName, patientName, hospitalId, age, sex, specialty, healthConcern);
                    return;
                }
            }
        }
        JOptionPane.showMessageDialog(this, "No available doctors for the selected specialty.", "Scheduling Failed", JOptionPane.ERROR_MESSAGE);
    }


    private void initializeComponents() {
        model = new DefaultTableModel(new String[]{"ID", "Name", "Specialist", "Room", "Patient Consult", "Availability", "Edit", "Refer"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6 || column == 7; // Only Edit and Refer columns are editable
            }
        };

        doctorTable = new JTable(model);
        doctorTable.setFont(MAIN_FONT);
        doctorTable.getTableHeader().setFont(TITLE_FONT);
        doctorTable.setRowHeight(40);
        doctorTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<>(model);
        doctorTable.setRowSorter(sorter);

        JScrollPane tableScrollPane = new JScrollPane(doctorTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(DARK_BLUE));

        doctorManager = new DoctorManager(dbConnection, model);
        doctorControlManager = new DoctorControlManager(dbConnection, model);

        JPanel controlPanel = createControlPanel();

        setLayout(new BorderLayout());
        add(controlPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
    }

    private void setButtonColumn(String columnName, Consumer<Integer> action) {
        TableColumn column = doctorTable.getColumn(columnName);
        column.setCellRenderer(new ButtonRenderer(columnName));
        column.setCellEditor(new ButtonEditor(new JCheckBox(), columnName, action));
    }

    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(Color.WHITE);

        searchField = new JTextField(20);
        searchField.setFont(MAIN_FONT);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) { filterDoctors(); }
            public void removeUpdate(DocumentEvent e) { filterDoctors(); }
            public void insertUpdate(DocumentEvent e) { filterDoctors(); }
        });

        JButton addDoctorButton = createStyledButton("Add New Doctor", this::addDoctor);
        JButton saveButton = createStyledButton("Save", this::saveChanges);
        JButton undoButton = createStyledButton("Undo", this::undoLastAction);

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(addDoctorButton);
        searchPanel.add(saveButton);
        searchPanel.add(undoButton);

        controlPanel.add(searchPanel, BorderLayout.CENTER);

        return controlPanel;
    }



    private JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(MAIN_FONT);
        return textField;
    }

    private JButton createStyledButton(String text, Runnable action) {
        JButton button = new JButton(text);
        button.setFont(MAIN_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(DARK_BLUE);
        button.setFocusPainted(false);
        button.addActionListener(e -> action.run());
        return button;
    }
    private void editDoctor() {
        doctorControlManager.editAction(doctorTable);
    }
    
    private void deleteDoctor() {
        doctorControlManager.deleteAction(doctorTable);
    }
    
    private void saveChanges() {
        doctorControlManager.saveAction();
    }
    
    private void undoLastAction() {
        doctorControlManager.undoAction(doctorTable);
    }

    private void filterDoctors() {
        String text = searchField.getText();
        if (text.trim().length() == 0) {
            sorter.setRowFilter(null);
        } else {
            sorter.setRowFilter(RowFilter.regexFilter("(?i)" + text));
        }
    }

    private void addDoctor() {
        doctorControlManager.addDoctor(parentPanel, doctorTable);
    }


    private JPanel createBasicInfoPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = 1;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
    
        JTextField nameField = new JTextField(20);
        JComboBox<String> specialistCombo = new JComboBox<>(getSpecialistList());
        JSpinner maxPatientsPerDaySpinner = new JSpinner(new SpinnerNumberModel(20, 1, 100, 1));
        JSpinner maxPatientsPerSlotSpinner = new JSpinner(new SpinnerNumberModel(1, 1, 10, 1));
        JSpinner slotDurationSpinner = new JSpinner(new SpinnerNumberModel(30, 5, 120, 5));
    
        addLabelAndComponent(panel, gbc, "Doctor Name:", nameField, 0, 0);
        addLabelAndComponent(panel, gbc, "Specialty:", specialistCombo, 0, 1);
        addLabelAndComponent(panel, gbc, "Max Patients Per Day:", maxPatientsPerDaySpinner, 0, 2);
        addLabelAndComponent(panel, gbc, "Max Patients Per Slot:", maxPatientsPerSlotSpinner, 0, 3);
        addLabelAndComponent(panel, gbc, "Slot Duration (minutes):", slotDurationSpinner, 0, 4);
    
        return panel;
    }
    
    private JPanel createBasicInfoPanel(Doctor doctor) {
        JPanel panel = createBasicInfoPanel();
        if (doctor != null) {
            // Set the values for existing doctor
            ((JTextField)findComponentByName(panel, "Doctor Name")).setText(doctor.getName());
            ((JComboBox<?>)findComponentByName(panel, "Specialty")).setSelectedItem(doctor.getSpecialist());
            ((JSpinner)findComponentByName(panel, "Max Patients Per Day")).setValue(doctor.getMaxPatientsPerDay());
            ((JSpinner)findComponentByName(panel, "Max Patients Per Slot")).setValue(doctor.getMaxPatientsPerSlot());
            ((JSpinner)findComponentByName(panel, "Slot Duration (minutes)")).setValue(doctor.getSlotDuration());
        }
        return panel;
    }
    
    private Component findComponentByName(Container container, String name) {
        for (Component component : container.getComponents()) {
            if (name.equals(component.getName())) {
                return component;
            }
            if (component instanceof Container) {
                Component found = findComponentByName((Container) component, name);
                if (found != null) {
                    return found;
                }
            }
        }
        return null;
    }

 
    
    private void referDoctor(int row) {
        String doctorName = (String) model.getValueAt(row, 0);
        String specialty = (String) model.getValueAt(row, 1);
        
        JDialog dialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "Schedule Appointment", true);
        dialog.setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 5));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        panel.add(new JLabel("Doctor:"));
        panel.add(new JLabel(doctorName));

        panel.add(new JLabel("Specialty:"));
        panel.add(new JLabel(specialty));

        JTextField patientNameField = new JTextField();
        panel.add(new JLabel("Patient Name:"));
        panel.add(patientNameField);

        JTextField hospitalIdField = new JTextField();
        panel.add(new JLabel("Hospital ID:"));
        panel.add(hospitalIdField);

        JSpinner ageSpinner = new JSpinner(new SpinnerNumberModel(18, 0, 120, 1));
        panel.add(new JLabel("Age:"));
        panel.add(ageSpinner);

        JComboBox<String> sexComboBox = new JComboBox<>(new String[]{"Male", "Female", "Other"});
        panel.add(new JLabel("Sex:"));
        panel.add(sexComboBox);

        JComboBox<String> dateCombo = new JComboBox<>(getAvailableDates());
        panel.add(new JLabel("Date:"));
        panel.add(dateCombo);

        JComboBox<String> timeCombo = new JComboBox<>(getAvailableTimes());
        panel.add(new JLabel("Time:"));
        panel.add(timeCombo);

        JButton scheduleButton = new JButton("Schedule Appointment");
        scheduleButton.addActionListener(e -> {
            String patientName = patientNameField.getText();
            String hospitalId = hospitalIdField.getText();
            int age = (int) ageSpinner.getValue();
            String sex = (String) sexComboBox.getSelectedItem();
            String selectedDate = (String) dateCombo.getSelectedItem();
            String selectedTime = (String) timeCombo.getSelectedItem();

            if (patientName.isEmpty() || hospitalId.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all fields.", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
            } else {
                try {
                    doctorManager.scheduleAppointment(doctorName, patientName, hospitalId, age, sex, specialty, selectedDate, selectedTime);
                    JOptionPane.showMessageDialog(dialog, "Appointment scheduled successfully.", "Success", JOptionPane.INFORMATION_MESSAGE);
                    dialog.dispose();
                    loadDoctorData(); // Refresh the table
                } catch (SQLException ex) {
                    LOGGER.log(Level.SEVERE, "Error scheduling appointment", ex);
                    JOptionPane.showMessageDialog(dialog, "Error scheduling appointment: " + ex.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        panel.add(scheduleButton);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }

private void addLabelAndComponent(JPanel panel, GridBagConstraints gbc, String label, JComponent component, int x, int y) {
    gbc.gridx = x;
    gbc.gridy = y;
    panel.add(new JLabel(label), gbc);
    gbc.gridx = x + 1;
    panel.add(component, gbc);
}


private String[] generateHours() {
    String[] hours = new String[12];
    for (int i = 0; i < 12; i++) {
        hours[i] = String.format("%02d", i == 0 ? 12 : i);
    }
    return hours;
}



private void schedulePeriodicUpdates() {
    doctorManager.schedulePeriodicUpdates();
}
        
    private List<String[]> getTableData() {
        List<String[]> rows = new ArrayList<>();
        for (int i = 0; i < model.getRowCount(); i++) {
            String[] row = new String[model.getColumnCount()];
            for (int j = 0; j < model.getColumnCount(); j++) {
                row[j] = model.getValueAt(i, j).toString();
            }
            rows.add(row);
        }
        return rows;
    }

    private String generateRoom(String specialist) {
        Map<String, Integer> specialistRoomMap = new HashMap<>();
        int roomNumber = specialistRoomMap.getOrDefault(specialist, 0) + 1;
        specialistRoomMap.put(specialist, roomNumber);
        Random random = new Random();
        return specialist.substring(0, 2).toUpperCase() + (random.nextInt(9) + 1);
    }

  private void loadDoctorData() {
        model.setRowCount(0);
        try (Connection conn = dbConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM doctors")) {

            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getString("name"),
                    rs.getString("specialist"),
                    rs.getString("room"),
                    rs.getString("patient_consult"),
                    rs.getString("availability"),
                    "Edit",
                    "Refer"
                });
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error loading doctor data", e);
            JOptionPane.showMessageDialog(this, "Error loading doctor data: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    private String[] getSpecialistList() {
        // Dummy list for demonstration purposes
        return new String[]{
            "Allergist/Immunologist",
            "Anesthesiologist",
            "Cardiologist",
            "Dermatologist",
            "Endocrinologist",
            "Gastroenterologist",
            "Geriatrician",
            "Hematologist",
            "Infectious Disease Specialist",
            "Internist",
            "Nephrologist",
            "Neurologist",
            "Obstetrician/Gynecologist",
            "Oncologist",
            "Orthopedic Surgeon",
            "Otolaryngologist (ENT)",
            "Pediatrician",
            "Physiatrist",
            "Plastic Surgeon",
            "Podiatrist",
            "Pulmonologist",
            "Rheumatologist",
            "Surgeon",
            "Urologist",
            "Vascular Surgeon"
        };
    }

    
private class ButtonRenderer extends JButton implements TableCellRenderer {
    public ButtonRenderer(String text) {
        super(text);
        setOpaque(true);
        setFont(MAIN_FONT);
        setForeground(Color.WHITE);
        setBackground(DARK_BLUE);
    }

    @Override
    public Component getTableCellRendererComponent(JTable table, Object value,
            boolean isSelected, boolean hasFocus, int row, int column) {
        return this;
    }
}

 private class ButtonEditor extends DefaultCellEditor {
    private final JButton button;
    private int clickedRow;
    private final Consumer<Integer> action;

    public ButtonEditor(JCheckBox checkBox, String text, Consumer<Integer> action) {
        super(checkBox);
        this.action = action;
        button = new JButton(text);
        button.setOpaque(true);
        button.setFont(MAIN_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(DARK_BLUE);
        button.addActionListener(e -> fireEditingStopped());
    }

    @Override
    public Component getTableCellEditorComponent(JTable table, Object value,
            boolean isSelected, int row, int column) {
        clickedRow = row;
        return button;
    }

    @Override
    public Object getCellEditorValue() {
        action.accept(clickedRow);
        return button.getText();
    }
}

    private void scheduleAppointmentTime(String doctorName, String patientName, String hospitalId, int age, String sex, String specialty, String healthConcern) {
        JPanel panel = new JPanel(new GridLayout(0, 2));
        JTextField hospitalIdField = new JTextField(hospitalId);
        JTextField patientNameField = new JTextField(patientName);
        JTextField doctorNameField = new JTextField(doctorName);
        JTextField specialtyField = new JTextField(specialty);
        JTextField ageField = new JTextField(String.valueOf(age));
        JTextField sexField = new JTextField(sex);
        JComboBox<String> dateCombo = new JComboBox<>(getAvailableDates());
        JComboBox<String> timeCombo = new JComboBox<>(getAvailableTimes());
        
        panel.add(new JLabel("Hospital ID:"));
        panel.add(hospitalIdField);
        panel.add(new JLabel("Patient Name:"));
        panel.add(patientNameField);
        panel.add(new JLabel("Doctor Name:"));
        panel.add(doctorNameField);
        panel.add(new JLabel("Specialty:"));
        panel.add(specialtyField);
        panel.add(new JLabel("Age:"));
        panel.add(ageField);
        panel.add(new JLabel("Sex:"));
        panel.add(sexField);
        panel.add(new JLabel("Select Date:"));
        panel.add(dateCombo);
        panel.add(new JLabel("Select Time:"));
        panel.add(timeCombo);
    
        JButton verifyButton = new JButton("Verify Payment");
        verifyButton.addActionListener(e -> verifyPayment(hospitalIdField.getText()));
        panel.add(verifyButton);
    
        int result = JOptionPane.showConfirmDialog(null, panel, "Schedule Appointment for " + doctorName,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (result == JOptionPane.OK_OPTION) {
            String selectedDate = (String) dateCombo.getSelectedItem();
            String selectedTime = (String) timeCombo.getSelectedItem();
            saveAppointment(doctorNameField.getText(), patientNameField.getText(), hospitalIdField.getText(),
                    ageField.getText(), sexField.getText(), specialtyField.getText(), healthConcern, selectedDate, selectedTime);
        }
    }
             // Modify the verifyPayment method to use prepared statements and handle resources properly
             private void verifyPayment(String hospitalId) {
                String query = "SELECT status, last_name, first_name, middle_name FROM receipts WHERE hospital_id = ?";
                
                try (Connection conn = dbConnection.getConnection();
                     PreparedStatement stmt = conn.prepareStatement(query)) {
            
                    stmt.setString(1, hospitalId);
                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            String firstName = rs.getString("first_name");
                            String middleName = rs.getString("middle_name");
                            String lastName = rs.getString("last_name");
                            
                            String fullName = String.format("%s %s %s", firstName, 
                                (middleName != null && !middleName.isEmpty() ? middleName + " " : ""), 
                                lastName).trim();
                            
                            String status = rs.getString("status");
                            JOptionPane.showMessageDialog(this, "Payment status for " + fullName + ": " + status, 
                                "Payment Verification", JOptionPane.INFORMATION_MESSAGE);
                        } else {
                            JOptionPane.showMessageDialog(this, "No record found for the given hospital ID.", 
                                "Record Not Found", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                } catch (SQLException e) {
                    LOGGER.log(Level.SEVERE, "Database error during payment verification", e);
                    JOptionPane.showMessageDialog(this, "Database error: " + e.getMessage(), 
                        "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

         private boolean checkPaymentStatus(String hospitalId) {
        // Implement the logic to check payment status
        // This is a placeholder implementation
        return true;
    }

    private void saveAppointment(String doctorName, String patientName, String hospitalId, String age, String sex, String specialty, String healthConcern, String selectedDate, String selectedTime) {
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(
                     "INSERT INTO appointments (doctor_name, patient_name, hospital_id, age, sex, specialty, health_concern, appointment_date, appointment_time) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            pstmt.setString(1, doctorName);
            pstmt.setString(2, patientName);
            pstmt.setString(3, hospitalId);
            pstmt.setString(4, age);
            pstmt.setString(5, sex);
            pstmt.setString(6, specialty);
            pstmt.setString(7, healthConcern);
            pstmt.setString(8, selectedDate);
            pstmt.setString(9, selectedTime);
            pstmt.executeUpdate();
    
            JOptionPane.showMessageDialog(this, "Appointment scheduled successfully for " + patientName + " with Dr. " + doctorName + " on " + selectedDate + " at " + selectedTime, "Appointment Scheduled", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Error saving appointment", e);
            JOptionPane.showMessageDialog(this, "Error saving appointment: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    private String[] getAvailableDates() {
        String[] dates = new String[7];
        LocalDate currentDate = LocalDate.now();
        for (int i = 0; i < 7; i++) {
            dates[i] = currentDate.plusDays(i).format(DateTimeFormatter.ISO_LOCAL_DATE);
        }
        return dates;
    }
private String[] getAvailableTimes() {
        return new String[]{"09:00", "10:00", "11:00", "14:00", "15:00", "16:00"};
    }
    
    public static class FileUtil {
        public static void writeToFile(String filePath, List<String[]> data) throws IOException {
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
                for (String[] row : data) {
                    writer.write(String.join(",", row));
                    writer.newLine();
                }
            }
        }

        public static List<String[]> readFromFile(String filePath) throws IOException {
            List<String[]> data = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    data.add(line.split(","));
                }
            }
            return data;
        }
    }
}
