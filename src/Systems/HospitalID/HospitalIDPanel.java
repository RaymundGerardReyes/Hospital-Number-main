package Systems.HospitalID;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;

public class HospitalIDPanel extends JPanel {
    private static final Logger LOGGER = Logger.getLogger(HospitalIDPanel.class.getName());
    public static final String FILE_PATH = "C:\\Users\\User\\Desktop\\Code\\Code Practice Summer\\Hospital Management System\\Hospital Number\\src\\Systems\\HospitalID\\hospital_ids.txt";

    private JLabel nameLabel, ageLabel, birthdayLabel, sexLabel, addressLabel, phoneLabel, healthConcernLabel, hospitalIdLabel;
    private JTextField nameField, birthdayField, sexField, addressField, phoneField, healthConcernField, searchField;
    private JComboBox<Integer> ageComboBox;
    private JButton generateButton, viewHospitalIDButton, backButton, editButton, deleteButton, saveButton, refreshButton;
    private HospitalIDGenerator idGenerator;
    private DefaultTableModel tableModel;
    private JTable table;
    private TableRowSorter<DefaultTableModel> sorter;
    private JPanel searchPanel;

    public HospitalIDPanel() {
        setLayout(new GridBagLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));
        initializeComponents();
        configureLayout();
        addEventListeners();
    }

    private void initializeComponents() {
        nameLabel = new JLabel("Name:");
        ageLabel = new JLabel("Age:");
        birthdayLabel = new JLabel("Birthday:");
        sexLabel = new JLabel("Sex:");
        addressLabel = new JLabel("Address:");
        phoneLabel = new JLabel("Phone:");
        healthConcernLabel = new JLabel("Health Concern:");
        hospitalIdLabel = new JLabel("Generated Hospital ID: "); 

        nameField = new JTextField(20);
        ageComboBox = new JComboBox<>(createAgeRange());
        birthdayField = new JTextField(20);
        sexField = new JTextField(20);
        addressField = new JTextField(20);
        phoneField = new JTextField(20);
        healthConcernField = new JTextField(20);
        searchField = new JTextField(20);

        generateButton = new JButton("Generate Hospital ID");
        viewHospitalIDButton = new JButton("View Hospital ID Patient");
        backButton = new JButton("Back");
        editButton = new JButton("Edit");
        deleteButton = new JButton("Delete");
        saveButton = new JButton("Save");
        refreshButton = new JButton("Refresh");

        idGenerator = new HospitalIDGenerator();

        tableModel = new DefaultTableModel(
            new String[]{"#", "Hospital ID", "Name", "Age", "Birthday", "Sex", "Address", "Phone", "Health Concern"}, 0
        ) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        sorter = new TableRowSorter<>(tableModel);
        table.setRowSorter(sorter);
        table.setPreferredScrollableViewportSize(new Dimension(800, 400));
        table.setFillsViewportHeight(true);
    }

    private Integer[] createAgeRange() {
        Integer[] ageRange = new Integer[100];
        for (int i = 0; i < 100; i++) {
            ageRange[i] = i + 1;
        }
        return ageRange;
    }

    private void configureLayout() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;

        addComponent(nameLabel, 0, 0, gbc);
        addComponent(nameField, 1, 0, gbc);
        addComponent(ageLabel, 0, 1, gbc);
        addComponent(ageComboBox, 1, 1, gbc);
        addComponent(birthdayLabel, 0, 2, gbc);
        addComponent(birthdayField, 1, 2, gbc);
        addComponent(sexLabel, 0, 3, gbc);
        addComponent(sexField, 1, 3, gbc);
        addComponent(addressLabel, 0, 4, gbc);
        addComponent(addressField, 1, 4, gbc);
        addComponent(phoneLabel, 0, 5, gbc);
        addComponent(phoneField, 1, 5, gbc);
        addComponent(healthConcernLabel, 0, 6, gbc);
        addComponent(healthConcernField, 1, 6, gbc);

        gbc.gridx = 1;
        gbc.gridy = 7;
        gbc.gridwidth = 2;
        add(generateButton, gbc);

        gbc.gridy = 8;
        gbc.anchor = GridBagConstraints.WEST;
        add(hospitalIdLabel, gbc); 
        
        System.out.println(hospitalIdLabel);

        gbc.gridx = 1;
        gbc.gridy = 23;
        gbc.anchor = GridBagConstraints.CENTER;
        add(viewHospitalIDButton, gbc);
    }

    private void addComponent(Component component, int gridx, int gridy, GridBagConstraints gbc) {
        gbc.gridx = gridx;
        gbc.gridy = gridy;
        add(component, gbc);
    }

    private void refreshTableData() {
        new SwingWorker<List<PatientData>, Void>() {
            @Override
            protected List<PatientData> doInBackground() throws Exception {
                return FileHandler.loadPatientData();
            }

            @Override
            protected void done() {
                try {
                    List<PatientData> patientDataList = get();
                    tableModel.setRowCount(0); // Clear existing rows
                    for (int i = 0; i < patientDataList.size(); i++) {
                        PatientData data = patientDataList.get(i);
                        tableModel.addRow(new Object[]{
                                i + 1,
                                data.getHospitalId(),
                                data.getName(),
                                data.getAge(),
                                data.getBirthday(),
                                data.getSex(),
                                data.getAddress(),
                                data.getPhone(),
                                data.getHealthConcern()
                        });
                    }
                } catch (InterruptedException | ExecutionException e) {
                    LOGGER.severe("Failed to refresh table data: " + e.getMessage());
                }
            }
        }.execute();
    }

    private void saveAllData() {
        new SwingWorker<Void, Void>() {
            @Override
            protected Void doInBackground() throws Exception {
                List<PatientData> patientDataList = new ArrayList<>();
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    patientDataList.add(new PatientData(
                            tableModel.getValueAt(i, 2).toString(),
                            Integer.parseInt(tableModel.getValueAt(i, 3).toString()),
                            tableModel.getValueAt(i, 4).toString(),
                            tableModel.getValueAt(i, 5).toString(),
                            tableModel.getValueAt(i, 6).toString(),
                            tableModel.getValueAt(i, 7).toString(),
                            tableModel.getValueAt(i, 8).toString(),
                            tableModel.getValueAt(i, 1).toString()
                    ));
                }
                FileHandler.saveAllPatientData(patientDataList);
                return null;
            }

            @Override
            protected void done() {
                refreshTableData();
            }
        }.execute();
    }

    private void addButtonActionListener(JButton button, ActionListener listener) {
        if (button != null) {
            button.addActionListener(listener);
        }
    }

    private void addEventListeners() {
        addButtonActionListener(generateButton, new GenerateButtonListener());
        addButtonActionListener(viewHospitalIDButton, new ViewHospitalIDButtonListener());
        addButtonActionListener(editButton, new EditButtonListener(table, tableModel));
        addButtonActionListener(deleteButton, new DeleteButtonListener());
        addButtonActionListener(refreshButton, e -> refreshTableData());
    }

    private class GenerateButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText().trim();
            int age = (int) ageComboBox.getSelectedItem();
            String birthday = birthdayField.getText().trim();
            String sex = sexField.getText().trim();
            String address = addressField.getText().trim();
            String phone = phoneField.getText().trim();
            String healthConcern = healthConcernField.getText().trim();

            if (areFieldsValid(name, birthday, sex, address, phone, healthConcern)) {
                processPatientData(name, age, birthday, sex, address, phone, healthConcern);
                hospitalIdLabel.repaint();
                refreshTableData();
            } else {
                JOptionPane.showMessageDialog(HospitalIDPanel.this, "All fields must be filled!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }

        private boolean areFieldsValid(String name, String birthday, String sex, String address, String phone, String healthConcern) {
            return !name.isEmpty() && !birthday.isEmpty() && !sex.isEmpty() && !address.isEmpty() && !phone.isEmpty() && !healthConcern.isEmpty();
        }

        private void processPatientData(String name, int age, String birthday, String sex, String address, String phone, String healthConcern) {
            String hospitalId = idGenerator.generateHospitalID();
            PatientData patientData = new PatientData(name, age, birthday, sex, address, phone, healthConcern, hospitalId);
            tableModel.addRow(new Object[]{
                    tableModel.getRowCount() + 1,
                    hospitalId,
                    name,
                    age,
                    birthday,
                    sex,
                    address,
                    phone,
                    healthConcern
            });
            FileHandler.savePatientData(patientData);
        }
    }

    private class ViewHospitalIDButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            JFrame frame = new JFrame("View Hospital IDs");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setSize(new Dimension(900, 600));
            frame.setLayout(new BorderLayout());

            JScrollPane scrollPane = new JScrollPane(table);
            frame.add(scrollPane, BorderLayout.CENTER);

            JPanel buttonPanel = new JPanel();
            buttonPanel.add(editButton);
            buttonPanel.add(deleteButton);
            buttonPanel.add(saveButton);
            buttonPanel.add(refreshButton);

            frame.add(buttonPanel, BorderLayout.SOUTH);
            frame.setVisible(true);
        }
    }

    private class EditButtonListener implements ActionListener {
        private final JTable table;
        private final DefaultTableModel tableModel;

        public EditButtonListener(JTable table, DefaultTableModel tableModel) {
            this.table = table;
            this.tableModel = tableModel;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                String name = (String) tableModel.getValueAt(selectedRow, 2);
                int age = (int) tableModel.getValueAt(selectedRow, 3);
                String birthday = (String) tableModel.getValueAt(selectedRow, 4);
                String sex = (String) tableModel.getValueAt(selectedRow, 5);
                String address = (String) tableModel.getValueAt(selectedRow, 6);
                String phone = (String) tableModel.getValueAt(selectedRow, 7);
                String healthConcern = (String) tableModel.getValueAt(selectedRow, 8);

                nameField.setText(name);
                ageComboBox.setSelectedItem(age);
                birthdayField.setText(birthday);
                sexField.setText(sex);
                addressField.setText(address);
                phoneField.setText(phone);
                healthConcernField.setText(healthConcern);

                deleteButton.addActionListener(e1 -> {
                    tableModel.removeRow(selectedRow);
                    saveAllData();
                });

                saveButton.addActionListener(e2 -> {
                    tableModel.setValueAt(nameField.getText(), selectedRow, 2);
                    tableModel.setValueAt(ageComboBox.getSelectedItem(), selectedRow, 3);
                    tableModel.setValueAt(birthdayField.getText(), selectedRow, 4);
                    tableModel.setValueAt(sexField.getText(), selectedRow, 5);
                    tableModel.setValueAt(addressField.getText(), selectedRow, 6);
                    tableModel.setValueAt(phoneField.getText(), selectedRow, 7);
                    tableModel.setValueAt(healthConcernField.getText(), selectedRow, 8);
                    saveAllData();
                });
            } else {
                JOptionPane.showMessageDialog(HospitalIDPanel.this, "Please select a row to edit.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private class DeleteButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int selectedRow = table.getSelectedRow();
            if (selectedRow != -1) {
                tableModel.removeRow(selectedRow);
                saveAllData();
            } else {
                JOptionPane.showMessageDialog(HospitalIDPanel.this, "Please select a row to delete.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
