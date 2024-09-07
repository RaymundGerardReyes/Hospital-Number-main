// Employees.java

package Systems.Employees;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.regex.PatternSyntaxException;
import java.util.stream.IntStream;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.RowFilter;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class Employees extends JPanel {

    private JLabel nameLabel;
    private JLabel ageLabel;
    private JLabel birthdayLabel;
    private JLabel sexLabel;
    private JLabel addressLabel;
    private JLabel phoneLabel;
    private JLabel hospitalIdLabel;
    private JLabel healthConcernLabel;
    private JLabel prcLabel;
    private JLabel positionLabel;
    private JLabel assignAreaLabel;
    private JLabel generatedHospitalIdLabel;

    private JTextField nameField;
    private JComboBox<Integer> ageComboBox;
    private JComboBox<String> dayComboBox;
    private JComboBox<String> monthComboBox;
    private JComboBox<String> yearComboBox;
    private JComboBox<String> sexComboBox;
    private JTextField addressField;
    private JTextField phoneField;
    private JTextField hospitalIdField;
    private JTextField healthConcernField;
    private JTextField prcField;
    private JTextField positionField;
    private JTextField assignAreaField;

    private JButton saveButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton refreshButton;
    private JTable employeeTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private TableRowSorter<DefaultTableModel> sorter;

    public Employees() {
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        nameLabel = new JLabel("Name:");
        ageLabel = new JLabel("Age:");
        birthdayLabel = new JLabel("Birthday:");
        sexLabel = new JLabel("Sex:");
        addressLabel = new JLabel("Address:");
        phoneLabel = new JLabel("Phone Number:");
        hospitalIdLabel = new JLabel("Hospital ID Number:");
        healthConcernLabel = new JLabel("Health Concern:");
        prcLabel = new JLabel("PRC/License Number:");
        positionLabel = new JLabel("Position:");
        assignAreaLabel = new JLabel("Assigned Area:");
        generatedHospitalIdLabel = new JLabel("Generated Hospital ID: ");

        nameField = new JTextField(20);
        addressField = new JTextField(20);
        phoneField = new JTextField(20);
        hospitalIdField = new JTextField(20);
        healthConcernField = new JTextField(20);
        prcField = new JTextField(20);
        positionField = new JTextField(20);
        assignAreaField = new JTextField(20);

        Integer[] ages = IntStream.rangeClosed(0, 120).boxed().toArray(Integer[]::new);
        ageComboBox = new JComboBox<>(ages);
        ageComboBox.setPreferredSize(new Dimension(50, 30));
        JScrollPane ageScrollPane = new JScrollPane(ageComboBox);
        ageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        String[] days = getDayNumbers();
        String[] months = getMonthNames();
        String[] years = getYearNumbers();

        dayComboBox = new JComboBox<>(days);
        monthComboBox = new JComboBox<>(months);
        yearComboBox = new JComboBox<>(years);

        JPanel birthdayPanel = new JPanel();
        birthdayPanel.setLayout(new BoxLayout(birthdayPanel, BoxLayout.X_AXIS));
        birthdayPanel.add(dayComboBox);
        birthdayPanel.add(Box.createHorizontalStrut(5));
        birthdayPanel.add(monthComboBox);
        birthdayPanel.add(Box.createHorizontalStrut(5));
        birthdayPanel.add(yearComboBox);
        birthdayPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        String[] sexIdentities = {"Male", "Female", "Non-Binary", "Other"};
        sexComboBox = new JComboBox<>(sexIdentities);
        sexComboBox.setPreferredSize(new Dimension(200, 30));
        JScrollPane sexScrollPane = new JScrollPane(sexComboBox);
        sexScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        formPanel.add(createComponentPanel(nameLabel, nameField));
        formPanel.add(createComponentPanel(ageLabel, ageScrollPane));
        formPanel.add(createComponentPanel(birthdayLabel, birthdayPanel));
        formPanel.add(createComponentPanel(sexLabel, sexScrollPane));
        formPanel.add(createComponentPanel(addressLabel, addressField));
        formPanel.add(createComponentPanel(phoneLabel, phoneField));
        formPanel.add(createComponentPanel(hospitalIdLabel, hospitalIdField));
        formPanel.add(createComponentPanel(healthConcernLabel, healthConcernField));
        formPanel.add(createComponentPanel(prcLabel, prcField));
        formPanel.add(createComponentPanel(positionLabel, positionField));
        formPanel.add(createComponentPanel(assignAreaLabel, assignAreaField));
        formPanel.add(createComponentPanel(generatedHospitalIdLabel, new JLabel("")));

        add(formPanel, BorderLayout.NORTH);

        searchField = new JTextField(20);
        searchField.setMaximumSize(new Dimension(200, 30));
        searchField.setAlignmentX(Component.LEFT_ALIGNMENT);
        searchField.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String searchText = searchField.getText().trim();
                if (searchText.length() == 0) {
                    sorter.setRowFilter(null);
                } else {
                    try {
                        sorter.setRowFilter(RowFilter.regexFilter("(?i)" + searchText));
                    } catch (PatternSyntaxException ex) {
                        System.err.println("Error in search syntax: " + ex.getMessage());
                    }
                }
            }
        });

        JPanel searchPanel = new JPanel();
        searchPanel.setLayout(new BoxLayout(searchPanel, BoxLayout.X_AXIS));
        searchPanel.add(new JLabel("Search: "));
        searchPanel.add(Box.createHorizontalStrut(5));
        searchPanel.add(searchField);
        searchPanel.add(Box.createHorizontalGlue());
        searchPanel.setBorder(new EmptyBorder(5, 0, 10, 0));
        add(searchPanel, BorderLayout.CENTER);

        tableModel = new DefaultTableModel(new String[]{
            "Name", "Age", "Birthday", "Sex", "Address", "Phone Number", "Hospital ID", "PRC", "Position", "Assign Area"
        }, 0);
        employeeTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(employeeTable);
        add(scrollPane, BorderLayout.CENTER);

        sorter = new TableRowSorter<>(tableModel);
        employeeTable.setRowSorter(sorter);

        EmployeeDataFileHandler.readDataFromFile(tableModel);

        // Panel for buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        saveButton = new JButton("Save");
        saveButton.setFont(new Font("Arial", Font.PLAIN, 16));
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                EmployeeData employeeData = createEmployeeData();
                EmployeeDataFileHandler.saveEmployeeData(employeeData);
                tableModel.addRow(employeeData.toTableRow());
                generatedHospitalIdLabel.setText("Generated Hospital ID: " + employeeData.getHospitalIdNumber());
                clearFields();
            }
        });
        buttonPanel.add(saveButton);

        // Add Search bar beside Save button
        searchField = new JTextField(15);
        buttonPanel.add(searchField);

        // Edit button
        editButton = new JButton("Edit");
        editButton.setFont(new Font("Arial", Font.PLAIN, 16));
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = employeeTable.getSelectedRow();
                if (selectedRow != -1) {
                    EmployeeData employeeData = createEmployeeData();
                    tableModel.setValueAt(employeeData.getName(), selectedRow, 0);
                    tableModel.setValueAt(employeeData.getAge(), selectedRow, 1);
                    tableModel.setValueAt(employeeData.getBirthday(), selectedRow, 2);
                    tableModel.setValueAt(employeeData.getSexIdentity(), selectedRow, 3);
                    tableModel.setValueAt(employeeData.getAddress(), selectedRow, 4);
                    tableModel.setValueAt(employeeData.getPhoneNumber(), selectedRow, 5);
                    tableModel.setValueAt(employeeData.getHospitalId(), selectedRow, 6);
                    tableModel.setValueAt(employeeData.getPrc(), selectedRow, 7);
                    tableModel.setValueAt(employeeData.getPosition(), selectedRow, 8);
                    tableModel.setValueAt(employeeData.getAssignArea(), selectedRow, 9);
                    generatedHospitalIdLabel.setText("Edited Employee: " + employeeData.getHospitalIdNumber());
                }
            }
        });
        buttonPanel.add(editButton);

        // Delete button
        deleteButton = new JButton("Delete");
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 16));
        deleteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int selectedRow = employeeTable.getSelectedRow();
                if (selectedRow != -1) {
                    tableModel.removeRow(selectedRow);
                    generatedHospitalIdLabel.setText("Deleted Employee");
                }
            }
        });
        buttonPanel.add(deleteButton);

        // Refresh button
        refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 16));
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tableModel.setRowCount(0); // Clear table
                EmployeeDataFileHandler.readDataFromFile(tableModel); // Reload data from file
                generatedHospitalIdLabel.setText("Data Refreshed");
            }
        });
        buttonPanel.add(refreshButton);

        add(buttonPanel, BorderLayout.SOUTH);

        ActionListener birthdayListener = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateAgeField();
            }
        };
        dayComboBox.addActionListener(birthdayListener);
        monthComboBox.addActionListener(birthdayListener);
        yearComboBox.addActionListener(birthdayListener);

        updateAgeField();
    }

    private EmployeeData createEmployeeData() {
        String hospitalIdNumber = "ID_" + System.currentTimeMillis();  // Example for Hospital ID
        String name = nameField.getText().trim();
        String age = ageComboBox.getSelectedItem().toString();
        String day = dayComboBox.getSelectedItem().toString();
        String month = monthComboBox.getSelectedItem().toString();
        String year = yearComboBox.getSelectedItem().toString();
        String birthday = month + " " + day + ", " + year;
        String sex = sexComboBox.getSelectedItem().toString();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String hospitalId = hospitalIdField.getText().trim();
        String prc = prcField.getText().trim();
        String position = positionField.getText().trim();
        String assignArea = assignAreaField.getText().trim();

        return new EmployeeData(hospitalIdNumber, name, age, birthday, sex, address, phone, hospitalId, prc, position, assignArea);
    }

    private JPanel createComponentPanel(JLabel label, JComponent component) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        panel.setBorder(new EmptyBorder(5, 0, 5, 0));

        label.setPreferredSize(new Dimension(150, 30));
        component.setPreferredSize(new Dimension(200, 30));

        panel.add(label);
        panel.add(Box.createHorizontalStrut(10));
        panel.add(component);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        return panel;
    }

    private void updateAgeField() {
        String day = dayComboBox.getSelectedItem().toString();
        String month = monthComboBox.getSelectedItem().toString();
        String year = yearComboBox.getSelectedItem().toString();

        if (!day.isEmpty() && !month.isEmpty() && !year.isEmpty()) {
            Calendar birthdate = Calendar.getInstance();
            birthdate.set(Integer.parseInt(year), monthComboBox.getSelectedIndex(), Integer.parseInt(day));
            Calendar now = Calendar.getInstance();
            int age = now.get(Calendar.YEAR) - birthdate.get(Calendar.YEAR);
            if (now.get(Calendar.DAY_OF_YEAR) < birthdate.get(Calendar.DAY_OF_YEAR)) {
                age--;
            }
            ageComboBox.setSelectedItem(age);
        }
    }

    public void clearFields() {
        nameField.setText("");
        ageComboBox.setSelectedIndex(0);
        dayComboBox.setSelectedIndex(0);
        monthComboBox.setSelectedIndex(0);
        yearComboBox.setSelectedIndex(0);
        sexComboBox.setSelectedIndex(0);
        addressField.setText("");
        phoneField.setText("");
        hospitalIdField.setText("");
        healthConcernField.setText("");
        prcField.setText("");
        positionField.setText("");
        assignAreaField.setText("");
    }

    private String[] getMonthNames() {
        return new String[]{
            "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"
        };
    }

    private String[] getDayNumbers() {
        return IntStream.rangeClosed(1, 31).mapToObj(String::valueOf).toArray(String[]::new);
    }

    private String[] getYearNumbers() {
        int currentYear = Calendar.getInstance().get(Calendar.YEAR);
        return IntStream.rangeClosed(1900, currentYear).mapToObj(String::valueOf).toArray(String[]::new);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("Employees Management");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(800, 600));

            Employees employees = new Employees();
            frame.getContentPane().add(employees);

            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }
}
