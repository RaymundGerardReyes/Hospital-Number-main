package Systems.PatientManagement;

import Systems.Dashboard.DarkMode;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PatientInfoTable extends JPanel {
    private JTable table;
    private DefaultTableModel model;
    private JTextField searchField;
    private JButton searchButton;
    private TableRowSorter<DefaultTableModel> sorter;
    private List<Patient> patients;
    private DarkMode darkMode;

    public PatientInfoTable(DarkMode darkMode) {
        this.darkMode = darkMode;
        setLayout(new BorderLayout());
        initializeComponents();
        layoutComponents();
        fetchPatients(); // Move this here from the constructor
        updateColors();
    }

    private void initializeComponents() {
        // Initialize table
        String[] columnNames = {"ID", "Name", "Age", "Gender", "Contact Number", "Actions"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only make the "Actions" column editable
            }
        };
        table = new JTable(model);
        sorter = new TableRowSorter<>(model);
        table.setRowSorter(sorter);

        // Initialize search components
        searchField = new JTextField(20);
        searchButton = new JButton("Search");
        searchButton.addActionListener(e -> performSearch());

        // Add column sorting functionality
        table.getTableHeader().addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                int column = table.columnAtPoint(evt.getPoint());
                String columnName = table.getColumnName(column);
                sorter.setSortKeys(List.of(new RowSorter.SortKey(column, SortOrder.ASCENDING)));
                sorter.sort();
            }
        });
    }


    public void refreshData() {
        SwingUtilities.invokeLater(() -> {
            fetchPatients(); // This method should update the patients list
            updateTable();   // This method should update the JTable with the new data
            revalidate();
            repaint();
        });
    }

    private void layoutComponents() {
        // Layout for search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(searchField);
        searchPanel.add(searchButton);

        // Add components to main panel
        add(searchPanel, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void fetchPatients() {
        // Simulating data fetch
        patients = new ArrayList<>();
        patients.add(new Patient(1, "John Doe", 35, "Male", "123-456-7890"));
        patients.add(new Patient(2, "Jane Smith", 28, "Female", "098-765-4321"));
        patients.add(new Patient(3, "Bob Johnson", 42, "Male", "555-555-5555"));

        updateTable();
    }

    private void updateTable() {
        model.setRowCount(0);
        for (Patient patient : patients) {
            model.addRow(new Object[]{
                    patient.getId(),
                    patient.getName(),
                    patient.getAge(),
                    patient.getGender(),
                    patient.getContactNumber(),
                    "Edit | Delete"
            });
        }
    }

    private void performSearch() {
        String searchTerm = searchField.getText().toLowerCase();
        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchTerm));
    }

    public void updateColors() {
        setBackground(darkMode.getBackgroundColor());
        table.setBackground(darkMode.getCardBackgroundColor());
        table.setForeground(darkMode.getTextColor());
        table.getTableHeader().setBackground(darkMode.getPrimaryColor());
        table.getTableHeader().setForeground(darkMode.getTextColor());
        searchField.setBackground(darkMode.getCardBackgroundColor());
        searchField.setForeground(darkMode.getTextColor());
        searchButton.setBackground(darkMode.getPrimaryColor());
        searchButton.setForeground(darkMode.getTextColor());
    }

    private class Patient {
        private int id;
        private String name;
        private int age;
        private String gender;
        private String contactNumber;

        public Patient(int id, String name, int age, String gender, String contactNumber) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.contactNumber = contactNumber;
        }

        // Getters
        public int getId() { return id; }
        public String getName() { return name; }
        public int getAge() { return age; }
        public String getGender() { return gender; }
        public String getContactNumber() { return contactNumber; }
    }

    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Patient Information");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.add(new PatientInfoTable(new DarkMode()));
            frame.pack();
            frame.setVisible(true);
        });
    }
}