package Systems.PatientInfo;

import java.awt.*;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.stream.IntStream;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class PatientInfoButton extends JPanel {

    private JLabel nameLabel;
    private JLabel ageLabel;
    private JLabel birthdayLabel;
    private JLabel sexLabel;
    private JLabel addressLabel;
    private JLabel phoneLabel;
    private JLabel hospitalIdLabel;
    private JLabel healthConcernLabel;
    private JLabel prcLabel; // Added for PRC or License Number
    private JLabel positionLabel; // Added for Position
    private JLabel assignAreaLabel; // Added for Assign Area

    private JTextField nameField;
    private JComboBox<Integer> ageComboBox;
    private JComboBox<String> dayComboBox;
    private JComboBox<String> monthComboBox;
    private JComboBox<String> yearComboBox;
    private JComboBox<String> sexComboBox; // ComboBox for Sex Identity
    private JTextField addressField;
    private JTextField phoneField;
    private JTextField hospitalIdField;
    private JTextField healthConcernField;
    private JTextField prcField; // Added for PRC or License Number
    private JTextField positionField; // Added for Position
    private JTextField assignAreaField; // Added for Assign Area

    private JButton saveButton;
    private PatientInformationViewPanel viewPanel; // Reference to PatientInformationViewPanel

    public PatientInfoButton(PatientInformationViewPanel viewPanel) {
        this.viewPanel = viewPanel; // Initialize reference to viewPanel

        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(new EmptyBorder(10, 10, 10, 10));

        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // Initialize labels
        nameLabel = new JLabel("Name:");
        ageLabel = new JLabel("Age:");
        birthdayLabel = new JLabel("Birthday:");
        sexLabel = new JLabel("Sex Identity:");
        addressLabel = new JLabel("Address:");
        phoneLabel = new JLabel("Phone Number:");
        hospitalIdLabel = new JLabel("Hospital ID Number:");
        healthConcernLabel = new JLabel("Health Concern:");
        

        // Initialize text fields with appropriate sizes
        nameField = new JTextField(20);
        addressField = new JTextField(20);
        phoneField = new JTextField(20);
        hospitalIdField = new JTextField(20);
        healthConcernField = new JTextField(20);
    

        // Initialize combo boxes for age
        Integer[] ages = IntStream.rangeClosed(0, 120).boxed().toArray(Integer[]::new);
        ageComboBox = new JComboBox<>(ages);
        ageComboBox.setPreferredSize(new Dimension(50, 30));
        JScrollPane ageScrollPane = new JScrollPane(ageComboBox);
        ageScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Initialize combo boxes for birthday
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

        // Initialize combo box for sex identity
        String[] sexIdentities = {"Male", "Female", "Non-Binary", "Other"};
        sexComboBox = new JComboBox<>(sexIdentities);
        sexComboBox.setPreferredSize(new Dimension(200, 30));
        JScrollPane sexScrollPane = new JScrollPane(sexComboBox);
        sexScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);

        // Add components to the form panel
        formPanel.add(createComponentPanel(nameLabel, nameField));
        formPanel.add(createComponentPanel(ageLabel, ageScrollPane));
        formPanel.add(createComponentPanel(birthdayLabel, birthdayPanel));
        formPanel.add(createComponentPanel(sexLabel, sexScrollPane));
        formPanel.add(createComponentPanel(addressLabel, addressField));
        formPanel.add(createComponentPanel(phoneLabel, phoneField));
        formPanel.add(createComponentPanel(hospitalIdLabel, hospitalIdField));
        formPanel.add(createComponentPanel(healthConcernLabel, healthConcernField));

        add(formPanel, BorderLayout.NORTH);

        // Initialize save button
        saveButton = new JButton("Save");
        saveButton.setFont(new Font("Arial", Font.PLAIN, 16));
        saveButton.addActionListener(e -> {
            String[] data = getEmployeeData();
            PatientInformationHandler.saveDataToFile(data);
            viewPanel.updateTable(data); // Notify the view panel to update its table
            clearFields();
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Add listener to birthday components to calculate age dynamically
        ActionListener birthdayListener = e -> updateAgeField();
        dayComboBox.addActionListener(birthdayListener);
        monthComboBox.addActionListener(birthdayListener);
        yearComboBox.addActionListener(birthdayListener);

        // Initialize age based on initial birthdate
        updateAgeField();
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

    public String[] getEmployeeData() {
        String name = nameField.getText().trim();
        String age = ageComboBox.getSelectedItem().toString();
        String day = dayComboBox.getSelectedItem().toString();
        String month = monthComboBox.getSelectedItem().toString();
        String year = yearComboBox.getSelectedItem().toString();
        String birthday = day + "-" + month + "-" + year;
        String sex = sexComboBox.getSelectedItem().toString();
        String address = addressField.getText().trim().replaceAll(",", "&&"); // Replace commas with && to preserve data integrity
        String phone = phoneField.getText().trim().replaceAll(",", "&&");
        String hospitalId = hospitalIdField.getText().trim().replaceAll(",", "&&");
        String healthConcern = healthConcernField.getText().trim().replaceAll(",", "&&");
        return new String[]{name, age, birthday, sex, address, phone, hospitalId, healthConcern};
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
    }

    private String[] getMonthNames() {
        return new String[]{"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
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
            JFrame frame = new JFrame("Patient Info");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setPreferredSize(new Dimension(800, 600));

            // Initialize PatientInformationViewPanel and pass it to PatientInfoButton
            PatientInformationViewPanel viewPanel = new PatientInformationViewPanel();
            PatientInfoButton panel = new PatientInfoButton(viewPanel);

            // Add panels to frame
            frame.getContentPane().setLayout(new BorderLayout());
            frame.getContentPane().add(panel, BorderLayout.NORTH);
            frame.getContentPane().add(viewPanel, BorderLayout.CENTER);

            frame.pack();
            frame.setLocationRelativeTo(null); // Center the frame
            frame.setVisible(true);
        });
    }
}
