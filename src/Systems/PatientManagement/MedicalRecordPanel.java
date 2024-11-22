package Systems.PatientManagement;

import Systems.Dashboard.DarkMode;
import Systems.Database.DatabaseConnection;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicalRecordPanel extends JPanel {
    private JTable recordTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> sortComboBox;
    private DarkMode darkMode;
    private TableRowSorter<DefaultTableModel> sorter;

    private static final String[] COLUMN_NAMES = {
        "ID", "Patient Name", "Age", "Gender", "Current Consultation Date", "Chief Complaint", "Primary Diagnosis"
    };

    public MedicalRecordPanel(DarkMode darkMode) {
        this.darkMode = darkMode;
        setLayout(new BorderLayout());
        initComponents();
        layoutComponents();
        addListeners();
        loadMedicalRecords();
        updateColors();
        setVisible(true);
    }

    private void initComponents() {
        tableModel = new DefaultTableModel(COLUMN_NAMES, 0);
        recordTable = new JTable(tableModel);
        recordTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        sorter = new TableRowSorter<>(tableModel);
        recordTable.setRowSorter(sorter);

        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));

        sortComboBox = new JComboBox<>(COLUMN_NAMES);
    }

    private void layoutComponents() {
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.add(new JLabel("Search: "));
        topPanel.add(searchField);
        topPanel.add(new JLabel("Sort by: "));
        topPanel.add(sortComboBox);

        JScrollPane tableScrollPane = new JScrollPane(recordTable);

        add(topPanel, BorderLayout.NORTH);
        add(tableScrollPane, BorderLayout.CENTER);
    }

    private void addListeners() {
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void changedUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { search(); }
            public void insertUpdate(javax.swing.event.DocumentEvent e) { search(); }
        });

        sortComboBox.addActionListener(e -> sortTable());
    }

    private void search() {
        String searchTerm = searchField.getText().toLowerCase();
        RowFilter<DefaultTableModel, Object> rf = RowFilter.regexFilter("(?i)" + searchTerm);
        sorter.setRowFilter(rf);
    }

    private void sortTable() {
        int columnIndex = sortComboBox.getSelectedIndex();
        sorter.setSortKeys(List.of(new RowSorter.SortKey(columnIndex, SortOrder.ASCENDING)));
    }

    private void loadMedicalRecords() {
        tableModel.setRowCount(0);
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, patient_name, age, gender, current_consultation_date, chief_complaint, primary_diagnosis FROM consultation")) {

            while (rs.next()) {
                tableModel.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("patient_name"),
                    rs.getInt("age"),
                    rs.getString("gender"),
                    rs.getTimestamp("current_consultation_date"),
                    rs.getString("chief_complaint"),
                    rs.getString("primary_diagnosis")
                });
            }
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error loading medical records: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void updateColors() {
        setBackground(darkMode.getBackgroundColor());
        recordTable.setBackground(darkMode.getCardBackgroundColor());
        recordTable.setForeground(darkMode.getTextColor());
        recordTable.getTableHeader().setBackground(darkMode.getCardBackgroundColor());
        recordTable.getTableHeader().setForeground(darkMode.getTextColor());

        searchField.setBackground(darkMode.getCardBackgroundColor());
        searchField.setForeground(darkMode.getTextColor());
        searchField.setCaretColor(darkMode.getTextColor());

        sortComboBox.setBackground(darkMode.getCardBackgroundColor());
        sortComboBox.setForeground(darkMode.getTextColor());

        repaint();
    }
}