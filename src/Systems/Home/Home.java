package Systems.Home;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import Systems.Dashboard.DarkMode;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.util.Random;

import Systems.Dashboard.DarkMode;

public class Home extends JPanel {
    private JLabel totalPatientsLabel, doctorsOnDutyLabel, patientsInORLabel, availableBedsLabel;
    private JList<Doctor> doctorsList;
    private JList<Patient> patientList;
    private JList<Appointment> appointmentList;
    private Timer updateTimer;
    private JToggleButton darkModeToggle;
    private boolean isDarkMode = false;
     private DarkMode darkMode;
    

    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font CARD_TITLE_FONT = new Font("Segoe UI", Font.BOLD, 16);
    private static final Font CARD_VALUE_FONT = new Font("Segoe UI", Font.BOLD, 36);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 14);

    public Home(DarkMode darkMode) {
        this.darkMode = darkMode;
        setLayout(new BorderLayout());
        initComponents();
        startUpdateTimer();
    }

    public void updateColors(DarkMode darkMode) {
        setBackground(darkMode.getBackgroundColor());
    }

    private void initComponents() {
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(new EmptyBorder(20, 20, 20, 20));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.1;
        gbc.fill = GridBagConstraints.BOTH;
        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, gbc);

        gbc.gridy = 1;
        gbc.weighty = 0.2;
        JPanel statsPanel = createStatsPanel();
        contentPanel.add(statsPanel, gbc);

        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 0.5;
        JPanel doctorsPanel = createDoctorsPanel();
        contentPanel.add(doctorsPanel, gbc);

        gbc.gridx = 1;
        JPanel patientPanel = createPatientPanel();
        contentPanel.add(patientPanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.weighty = 0.35;
        JPanel appointmentsPanel = createAppointmentsPanel();
        contentPanel.add(appointmentsPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(contentPanel);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBorder(null);
        add(scrollPane, BorderLayout.CENTER);

        updateColors();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JLabel titleLabel = new JLabel("Healthcare Dashboard");
        titleLabel.setFont(TITLE_FONT);
        panel.add(titleLabel, BorderLayout.WEST);

        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton notificationButton = new JButton(new ImageIcon("path/to/bell-icon.png"));
        notificationButton.setBorderPainted(false);
        notificationButton.setContentAreaFilled(false);
        notificationButton.setFocusPainted(false);
        rightPanel.add(notificationButton);

        darkModeToggle = new JToggleButton(new ImageIcon("path/to/sun-icon.png"));
        darkModeToggle.setSelectedIcon(new ImageIcon("path/to/moon-icon.png"));
        darkModeToggle.setBorderPainted(false);
        darkModeToggle.setContentAreaFilled(false);
        darkModeToggle.setFocusPainted(false);
        darkModeToggle.addActionListener(e -> {
            isDarkMode = darkModeToggle.isSelected();
            updateColors();
        });
        rightPanel.add(darkModeToggle);

        JLabel avatarLabel = new JLabel(new ImageIcon("path/to/avatar.png"));
        avatarLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        rightPanel.add(avatarLabel);

        panel.add(rightPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 10, 0));
        panel.setOpaque(false);

        totalPatientsLabel = createStatsCard("Total Patients", "222");
        doctorsOnDutyLabel = createStatsCard("Doctors On Duty", "68");
        patientsInORLabel = createStatsCard("Patients in OR", "16");
        availableBedsLabel = createStatsCard("Available Beds", "78");

        panel.add(totalPatientsLabel.getParent());
        panel.add(doctorsOnDutyLabel.getParent());
        panel.add(patientsInORLabel.getParent());
        panel.add(availableBedsLabel.getParent());

        return panel;
    }

    private JLabel createStatsCard(String title, String initialValue) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(CARD_TITLE_FONT);
        card.add(titleLabel, BorderLayout.NORTH);

        JLabel valueLabel = new JLabel(initialValue);
        valueLabel.setFont(CARD_VALUE_FONT);
        card.add(valueLabel, BorderLayout.CENTER);

        JLabel changeLabel = new JLabel(getRandomChange());
        changeLabel.setFont(MAIN_FONT);
        card.add(changeLabel, BorderLayout.SOUTH);

        return valueLabel;
    }

    private String getRandomChange() {
        Random random = new Random();
        double change = (random.nextDouble() * 5) - 2.5; // Random change between -2.5% and 2.5%
        String direction = change >= 0 ? "+" : "";
        return direction + new DecimalFormat("#0.0").format(change) + "% from last period";
    }

    private JPanel createDoctorsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Available Doctors");
        titleLabel.setFont(CARD_TITLE_FONT);
        panel.add(titleLabel, BorderLayout.NORTH);

        DefaultListModel<Doctor> doctorsModel = new DefaultListModel<>();
        doctorsModel.addElement(new Doctor("Dr. Raymund Gerard", "Cardiology", "Available"));
        doctorsModel.addElement(new Doctor("Dr. Emily Chen", "Neurology", "In Surgery"));
        doctorsModel.addElement(new Doctor("Dr. Michael Lee", "Pediatrics", "Available"));
        doctorsModel.addElement(new Doctor("Dr. Sarah Johnson", "Oncology", "On Call"));
        doctorsModel.addElement(new Doctor("Dr. David Brown", "Orthopedics", "Available"));

        doctorsList = new JList<>(doctorsModel);
        doctorsList.setCellRenderer(new DoctorListCellRenderer());
        doctorsList.setFixedCellHeight(60);
        JScrollPane scrollPane = new JScrollPane(doctorsList);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createPatientPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Recent Patients");
        titleLabel.setFont(CARD_TITLE_FONT);
        panel.add(titleLabel, BorderLayout.NORTH);

        DefaultListModel<Patient> patientsModel = new DefaultListModel<>();
        patientsModel.addElement(new Patient("Alice Thompson", "Stable", "Ward A"));
        patientsModel.addElement(new Patient("Bob Martinez", "Critical", "ICU"));
        patientsModel.addElement(new Patient("Carol White", "Recovering", "Ward B"));
        patientsModel.addElement(new Patient("David Brown", "Stable", "ER"));
        patientsModel.addElement(new Patient("Eva Garcia", "Under Observation", "Ward C"));

        patientList = new JList<>(patientsModel);
        patientList.setCellRenderer(new PatientListCellRenderer());
        patientList.setFixedCellHeight(60);
        JScrollPane scrollPane = new JScrollPane(patientList);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createAppointmentsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("Upcoming Appointments");
        titleLabel.setFont(CARD_TITLE_FONT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton addAppointmentButton = new JButton("Add New Appointment");
        addAppointmentButton.setFont(MAIN_FONT);
        addAppointmentButton.setFocusPainted(false);
        addAppointmentButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Add New Appointment functionality to be implemented."));
        headerPanel.add(addAppointmentButton, BorderLayout.EAST);

        panel.add(headerPanel, BorderLayout.NORTH);

        DefaultListModel<Appointment> appointmentsModel = new DefaultListModel<>();
        appointmentsModel.addElement(new Appointment("Alice Brown", "09:00 AM", "Dr. Smith"));
        appointmentsModel.addElement(new Appointment("Charlie Davis", "10:30 AM", "Dr. Johnson"));
        appointmentsModel.addElement(new Appointment("Eva White", "02:00 PM", "Dr. Williams"));

        appointmentList = new JList<>(appointmentsModel);
        appointmentList.setCellRenderer(new AppointmentListCellRenderer());
        appointmentList.setFixedCellHeight(60);
        JScrollPane scrollPane = new JScrollPane(appointmentList);
        scrollPane.setBorder(null);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private void startUpdateTimer() {
        updateTimer = new Timer(5000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDashboardInfo();
            }
        });
        updateTimer.start();
    }

    private void updateDashboardInfo() {
        Random random = new Random();
        SwingUtilities.invokeLater(() -> {
            totalPatientsLabel.setText(String.valueOf(200 + random.nextInt(100)));
            doctorsOnDutyLabel.setText(String.valueOf(50 + random.nextInt(30)));
            patientsInORLabel.setText(String.valueOf(10 + random.nextInt(15)));
            availableBedsLabel.setText(String.valueOf(50 + random.nextInt(50)));

            ((JLabel)totalPatientsLabel.getParent().getComponent(2)).setText(getRandomChange());
            ((JLabel)doctorsOnDutyLabel.getParent().getComponent(2)).setText(getRandomChange());
            ((JLabel)patientsInORLabel.getParent().getComponent(2)).setText(getRandomChange());
            ((JLabel)availableBedsLabel.getParent().getComponent(2)).setText(getRandomChange());

            DefaultListModel<Doctor> doctorModel = (DefaultListModel<Doctor>) doctorsList.getModel();
            for (int i = 0; i < doctorModel.size(); i++) {
                Doctor doctor = doctorModel.get(i);
                doctor.status = random.nextBoolean() ? "Available" : "In Surgery";
                doctorModel.set(i, doctor);
            }

            DefaultListModel<Patient> patientModel = (DefaultListModel<Patient>) patientList.getModel();
            String[] conditions = {"Stable", "Critical", "Recovering", "Under Observation"};
            for (int i = 0; i < patientModel.size(); i++) {
                Patient patient = patientModel.get(i);
                patient.condition = conditions[random.nextInt(conditions.length)];
                patientModel.set(i, patient);
            }
        });
    }

    public void stopUpdateTimer() {
        if (updateTimer != null && updateTimer.isRunning()) {
            updateTimer.stop();
        }
    }

    private void updateColors() {
        Color backgroundColor = isDarkMode ? new Color(18, 18, 18) : Color.WHITE;
        Color textColor = isDarkMode ? Color.WHITE : Color.BLACK;
        Color cardBackground = isDarkMode ? new Color(30, 30, 30) : new Color(245, 245, 245);
    
        setBackground(backgroundColor);
        updateComponentColors(this, backgroundColor, textColor, cardBackground);
        repaint();
        revalidate();
    }
    
    private void updateComponentColors(Container container, Color backgroundColor, Color textColor, Color cardBackground) {
        for (Component comp : container.getComponents()) {
            if (comp instanceof JPanel) {
                comp.setBackground(cardBackground);
                updateComponentColors((Container) comp, backgroundColor, textColor, cardBackground);
            } else if (comp instanceof JLabel) {
                comp.setForeground(textColor);
            } else if (comp instanceof JButton) {
                ((JButton) comp).setForeground(textColor);
                ((JButton) comp).setBackground(cardBackground);
            } else if (comp instanceof JScrollPane) {
                comp.setBackground(cardBackground);
                JScrollPane scrollPane = (JScrollPane) comp;
                scrollPane.getViewport().setBackground(cardBackground);
                updateComponentColors(scrollPane.getViewport(), backgroundColor, textColor, cardBackground);
            } else if (comp instanceof JList) {
                comp.setBackground(cardBackground);
                comp.setForeground(textColor);
            }
        }
    }

    private static class Doctor {
        String name;
        String specialty;
        String status;

        

        Doctor(String name, String specialty, String status) {
            this.name = name;
            this.specialty = specialty;
            this.status = status;
        }
    }

    private static class Patient {
        String name;
        String condition;
        String location;

        Patient(String name, String condition, String location) {
            this.name = name;
            this.condition = condition;
            this.location = location;
        }
    }

    private static class Appointment {
        String patientName;
        String time;
        String doctorName;

        Appointment(String patientName, String time, String doctorName) {
            this.patientName = patientName;
            this.time = time;
            this.doctorName = doctorName;
        }
    }

    private class DoctorListCellRenderer extends JPanel implements ListCellRenderer<Doctor> {
        private JLabel nameLabel = new JLabel();
        private JLabel specialtyLabel = new JLabel();
        private JLabel statusLabel = new JLabel();

        DoctorListCellRenderer() {
            setLayout(new BorderLayout(10, 0));
            JPanel infoPanel = new JPanel(new GridLayout(2, 1));
            infoPanel.setOpaque(false);
            infoPanel.add(nameLabel);
            infoPanel.add(specialtyLabel);
            add(infoPanel, BorderLayout.CENTER);
            add(statusLabel, BorderLayout.EAST);
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Doctor> list, Doctor doctor, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            nameLabel.setText(doctor.name);
            nameLabel.setFont(MAIN_FONT.deriveFont(Font.BOLD));
            specialtyLabel.setText(doctor.specialty);
            specialtyLabel.setFont(MAIN_FONT);
            statusLabel.setText(doctor.status);
            statusLabel.setFont(MAIN_FONT.deriveFont(Font.BOLD));

            Color textColor = isDarkMode ? Color.WHITE : Color.BLACK;
            Color mutedTextColor = isDarkMode ? new Color(170, 170, 170) : new Color(100, 100, 100);

            nameLabel.setForeground(textColor);
            specialtyLabel.setForeground(mutedTextColor);

            if (doctor.status.equals("Available")) {
                statusLabel.setForeground(new Color(25, 135, 84));
            } else {
                statusLabel.setForeground(mutedTextColor);
            }

            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            return this;
        }
    }

    private class PatientListCellRenderer extends JPanel implements ListCellRenderer<Patient> {
        private JLabel nameLabel = new JLabel();
        private JLabel locationLabel = new JLabel();
        private JLabel conditionLabel = new JLabel();

        PatientListCellRenderer() {
            setLayout(new BorderLayout(10, 0));
            JPanel infoPanel = new JPanel(new GridLayout(2, 1));
            infoPanel.setOpaque(false);
            infoPanel.add(nameLabel);
            infoPanel.add(locationLabel);
            add(infoPanel, BorderLayout.CENTER);
            add(conditionLabel, BorderLayout.EAST);
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Patient> list, Patient patient, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            nameLabel.setText(patient.name);
            nameLabel.setFont(MAIN_FONT.deriveFont(Font.BOLD));
            locationLabel.setText(patient.location);
            locationLabel.setFont(MAIN_FONT);
            conditionLabel.setText(patient.condition);
            conditionLabel.setFont(MAIN_FONT.deriveFont(Font.BOLD));

            Color textColor = isDarkMode ? Color.WHITE : Color.BLACK;
            Color mutedTextColor = isDarkMode ? new Color(170, 170, 170) : new Color(100, 100, 100);

            nameLabel.setForeground(textColor);
            locationLabel.setForeground(mutedTextColor);

            switch (patient.condition) {
                case "Stable":
                    conditionLabel.setForeground(new Color(25, 135, 84));
                    break;
                case "Critical":
                    conditionLabel.setForeground(new Color(220, 53, 69));
                    break;
                default:
                    conditionLabel.setForeground(new Color(255, 193, 7));
                    break;
            }

            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            return this;
        }
    }

    private class AppointmentListCellRenderer extends JPanel implements ListCellRenderer<Appointment> {
        private JLabel nameLabel = new JLabel();
        private JLabel doctorLabel = new JLabel();
        private JLabel timeLabel = new JLabel();

        AppointmentListCellRenderer() {
            setLayout(new BorderLayout(10, 0));
            JPanel infoPanel = new JPanel(new GridLayout(2, 1));
            infoPanel.setOpaque(false);
            infoPanel.add(nameLabel);
            infoPanel.add(doctorLabel);
            add(infoPanel, BorderLayout.CENTER);
            add(timeLabel, BorderLayout.EAST);
            setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends Appointment> list, Appointment appointment, int index,
                                                      boolean isSelected, boolean cellHasFocus) {
            nameLabel.setText(appointment.patientName);
            nameLabel.setFont(MAIN_FONT.deriveFont(Font.BOLD));
            doctorLabel.setText(appointment.doctorName);
            doctorLabel.setFont(MAIN_FONT);
            timeLabel.setText(appointment.time);
            timeLabel.setFont(MAIN_FONT.deriveFont(Font.BOLD));

            Color textColor = isDarkMode ? Color.WHITE : Color.BLACK;
            Color mutedTextColor = isDarkMode ? new Color(170, 170, 170) : new Color(100, 100, 100);
            Color primaryColor = isDarkMode ? new Color(79, 70, 229) : new Color(59, 130, 246);

            nameLabel.setForeground(textColor);
            doctorLabel.setForeground(mutedTextColor);
            timeLabel.setForeground(primaryColor);

            setBackground(isSelected ? list.getSelectionBackground() : list.getBackground());
            return this;
        }
    }
}