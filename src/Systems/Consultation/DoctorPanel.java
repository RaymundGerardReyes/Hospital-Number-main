package Systems.Consultation;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.DefaultCellEditor;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableRowSorter;

public class DoctorPanel extends JPanel {

    public static final String FILE_PATH = "src/Systems//Consultation/doctors.txt";

    private static final String EDIT_LABEL = "Edit";
    private static final String DELETE_LABEL = "Delete";
    private static final String SAVE_LABEL = "Save";
    private static final String UNDO_LABEL = "Undo";
    private static final String REFRESH_LABEL = "Refresh";
    private static final Logger LOGGER = Logger.getLogger(DoctorPanel.class.getName());

    private JTable doctorTable;
    private DefaultTableModel model;
    private ConsultationPanel consultationPanel;
    private ConsultationParentPanel parentPanel;
    private JTextField searchField;
    private JTextField addDoctorField;
    private JComboBox<String> specialistComboBox;
    private JTextField maxPatientsField;
    private JButton addButton;
    private TableRowSorter<DefaultTableModel> sorter;
    private Stack<Object[]> undoStack = new Stack<>();

    private static final Color DARK_BLUE = new Color(0, 50, 100);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);

    public DoctorPanel(ConsultationPanel consultationPanel, ConsultationParentPanel parentPanel) {
        if (consultationPanel == null || parentPanel == null) {
            throw new IllegalArgumentException("ConsultationPanel and ConsultationParentPanel must not be null");
        }

        this.consultationPanel = consultationPanel;
        this.parentPanel = parentPanel;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        setBackground(Color.WHITE);

        initializeComponents();
        loadDoctorData();
    }

    private void initializeComponents() {
        String[] columns = {"Doctor", "Specialist", "Room", "List of Patient Consult", "Refer"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only "Refer" column is editable
            }
        };

        doctorTable = new JTable(model);
        doctorTable.setFont(MAIN_FONT);
        doctorTable.getTableHeader().setFont(TITLE_FONT);
        doctorTable.setRowHeight(60);
        sorter = new TableRowSorter<>(model);
        doctorTable.setRowSorter(sorter);
        doctorTable.getColumn("Refer").setCellRenderer(new ButtonRenderer());
        doctorTable.getColumn("Refer").setCellEditor(new ButtonEditor(new JCheckBox()));

        JScrollPane tableScrollPane = new JScrollPane(doctorTable);
        tableScrollPane.setBorder(BorderFactory.createLineBorder(DARK_BLUE));

        searchField = createStyledTextField(20);
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { filterDoctors(); }
            public void removeUpdate(DocumentEvent e) { filterDoctors(); }
            public void changedUpdate(DocumentEvent e) { filterDoctors(); }
        });

        addDoctorField = createStyledTextField(20);
        specialistComboBox = new JComboBox<>(getSpecialistList());
        specialistComboBox.setFont(MAIN_FONT);
        maxPatientsField = createStyledTextField(5);

        addButton = createStyledButton("Add Doctor");
        addButton.addActionListener(e -> addDoctor());

        JButton editButton = createStyledButton(EDIT_LABEL);
        editButton.addActionListener(e -> editAction());
        editButton.setToolTipText("Edit the selected doctor");

        JButton deleteButton = createStyledButton(DELETE_LABEL);
        deleteButton.addActionListener(e -> deleteAction());
        deleteButton.setToolTipText("Delete the selected doctor");

        JButton saveButton = createStyledButton(SAVE_LABEL);
        saveButton.addActionListener(e -> saveAction());
        saveButton.setToolTipText("Save changes");

        JButton undoButton = createStyledButton(UNDO_LABEL);
        undoButton.addActionListener(e -> undoAction());
        undoButton.setToolTipText("Undo the last action");

        JButton refreshButton = createStyledButton(REFRESH_LABEL);
        refreshButton.addActionListener(e -> refreshAction());
        refreshButton.setToolTipText("Refresh the data");

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(editButton);
        searchPanel.add(deleteButton);
        searchPanel.add(saveButton);
        searchPanel.add(undoButton);
        searchPanel.add(refreshButton);

        JPanel addDoctorPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        addDoctorPanel.setBackground(Color.WHITE);
        addDoctorPanel.add(new JLabel("Add Doctor: "));
        addDoctorPanel.add(addDoctorField);
        addDoctorPanel.add(specialistComboBox);
        addDoctorPanel.add(new JLabel("Max Patients: "));
        addDoctorPanel.add(maxPatientsField);
        addDoctorPanel.add(addButton);

        JPanel controlPanel = new JPanel(new BorderLayout());
        controlPanel.setBackground(Color.WHITE);
        controlPanel.add(searchPanel, BorderLayout.NORTH);
        controlPanel.add(addDoctorPanel, BorderLayout.SOUTH);

        add(controlPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
    }

    private JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(MAIN_FONT);
        return textField;
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setFont(MAIN_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(DARK_BLUE);
        button.setFocusPainted(false);
        return button;
    }

    private void editAction() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow != -1) {
            String doctorName = (String) doctorTable.getValueAt(selectedRow, 0);
            String specialist = (String) doctorTable.getValueAt(selectedRow, 1);
            String room = (String) doctorTable.getValueAt(selectedRow, 2);
            String patientConsult = (String) doctorTable.getValueAt(selectedRow, 3);
            String availability = (String) doctorTable.getValueAt(selectedRow, 4);

            Object[] currentRowData = {doctorName, specialist, room, patientConsult, availability};
            undoStack.push(currentRowData);

            JTextField doctorNameField = new JTextField(doctorName);
            JComboBox<String> specialistField = new JComboBox<>(getSpecialistList());
            specialistField.setSelectedItem(specialist);

            JTextField roomField = new JTextField(room);
            JTextField patientConsultField = new JTextField(patientConsult);
            JTextField availabilityField = new JTextField(availability);

            Object[] message = {
                "Doctor Name:", doctorNameField,
                "Specialist:", specialistField,
                "Room:", roomField,
                "List of Patient Consult:", patientConsultField,
                "Availability:", availabilityField
            };

            int option = JOptionPane.showConfirmDialog(null, message, "Edit Doctor", JOptionPane.OK_CANCEL_OPTION);
            if (option == JOptionPane.OK_OPTION) {
                doctorTable.setValueAt(doctorNameField.getText(), selectedRow, 0);
                doctorTable.setValueAt(specialistField.getSelectedItem(), selectedRow, 1);
                doctorTable.setValueAt(roomField.getText(), selectedRow, 2);
                doctorTable.setValueAt(patientConsultField.getText(), selectedRow, 3);
                doctorTable.setValueAt(availabilityField.getText(), selectedRow, 4);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select a doctor to edit.", "No Selection", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void deleteAction() {
        int selectedRow = doctorTable.getSelectedRow();
        if (selectedRow != -1) {
            Object[] currentRowData = {
                doctorTable.getValueAt(selectedRow, 0),
                doctorTable.getValueAt(selectedRow, 1),
                doctorTable.getValueAt(selectedRow, 2),
                doctorTable.getValueAt(selectedRow, 3),
                doctorTable.getValueAt(selectedRow, 4)
            };
            undoStack.push(currentRowData);

            model.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Please select a doctor to delete.", "No Selection", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveAction() {
        try {
            List<String[]> rows = getTableData();
            FileUtil.writeToFile(FILE_PATH, rows);
            JOptionPane.showMessageDialog(this, "Data saved successfully.", "Save", JOptionPane.INFORMATION_MESSAGE);
        } catch (IOException e) {
            LOGGER.severe("Error saving table data: " + e.getMessage());
            JOptionPane.showMessageDialog(this, "An error occurred during saving. Please check the log for details.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void undoAction() {
        if (!undoStack.isEmpty()) {
            Object[] previousState = undoStack.pop();
            model.addRow(previousState);
        } else {
            JOptionPane.showMessageDialog(this, "No actions to undo.", "Undo", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void refreshAction() {
        model.setRowCount(0);
        loadDoctorData();
    }

    private void filterDoctors() {
        String searchText = searchField.getText().toLowerCase();
        RowFilter<DefaultTableModel, Object> filter = RowFilter.regexFilter("(?i)" + searchText);
        sorter.setRowFilter(filter);
    }

    private void addDoctor() {
        String doctorName = addDoctorField.getText().trim();
        String specialist = (String) specialistComboBox.getSelectedItem();
        String maxPatientsStr = maxPatientsField.getText().trim();

        if (doctorName.isEmpty() || specialist == null || maxPatientsStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "All fields must be filled in to add a doctor.", "Incomplete Input", JOptionPane.WARNING_MESSAGE);
            return;
        }

        try {
            int maxPatients = Integer.parseInt(maxPatientsStr);
            if (maxPatients <= 0) {
                throw new NumberFormatException("Max patients must be greater than 0.");
            }
            String room = generateRoom(specialist);
            Object[] newRow = {doctorName, specialist, room, "0 / " + maxPatients, "Available"};
            model.addRow(newRow);
            saveDoctorData(newRow);
            addDoctorField.setText("");
            maxPatientsField.setText("");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for max patients. " + e.getMessage(), "Invalid Input", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveDoctorData(Object[] newRow) {
        try {
            List<String[]> rows = getTableData();
            FileUtil.writeToFile(FILE_PATH, rows);
        } catch (IOException e) {
            LOGGER.severe("Error saving doctor data: " + e.getMessage());
        }
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
        return specialist.substring(0, 2).toUpperCase() + roomNumber;
    }

    private void loadDoctorData() {
        try {
            List<String[]> rows = FileUtil.readFromFile(FILE_PATH);
            for (String[] row : rows) {
                model.addRow(row);
            }
        } catch (IOException e) {
            LOGGER.severe("Error loading doctor data: " + e.getMessage());
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

    private static class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFont(MAIN_FONT);
            setForeground(Color.WHITE);
            setBackground(DARK_BLUE);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText("Refer");
            return this;
        }
    }

    private class ButtonEditor extends DefaultCellEditor {
        private final JButton button;
        private String label;
        private boolean isPushed;
    
        public ButtonEditor(JCheckBox checkBox) {
            super(checkBox);
            button = new JButton();
            button.setOpaque(true);
            button.setFont(MAIN_FONT);
            button.setForeground(Color.WHITE);
            button.setBackground(DARK_BLUE);
            button.addActionListener(e -> {
                fireEditingStopped();
                int row = doctorTable.getSelectedRow();
                if (row != -1) {
                    JOptionPane.showMessageDialog(DoctorPanel.this, "Refer button clicked for " + model.getValueAt(row, 0));
                }
            });
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            label = (value == null) ? "" : value.toString();
            button.setText(label);
            isPushed = true;
            return button;
        }

        @Override
        public Object getCellEditorValue() {
            if (isPushed) {
                // Handle button click action here
            }
            isPushed = false;
            return label;
        }

        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
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
