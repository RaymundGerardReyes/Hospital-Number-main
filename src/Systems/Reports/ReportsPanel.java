package Systems.Reports;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Arc2D;
import java.awt.geom.Rectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class ReportsPanel extends JPanel {
    private JComboBox<String> reportTypeComboBox;
    private JTextArea reportTextArea;
    private JButton generateReportButton;
    private JPanel chartPanel;
    private JLabel dateTimeLabel;
    private Timer timer;
    private JProgressBar progressBar;
    private JButton exportButton;
    private JButton printButton;

    private static final Color BACKGROUND_COLOR = new Color(240, 240, 240);
    private static final Color ACCENT_COLOR = new Color(70, 130, 180);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);

    public ReportsPanel() {
        setLayout(new BorderLayout(10, 10));
        setBorder(new EmptyBorder(20, 20, 20, 20));
        setBackground(BACKGROUND_COLOR);

        // Create components
        JPanel topPanel = createTopPanel();
        JPanel centerPanel = createCenterPanel();
        JPanel bottomPanel = createBottomPanel();

        // Add components to the main panel
        add(topPanel, BorderLayout.NORTH);
        add(centerPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Initialize and start the timer for real-time updates
        initializeTimer();
    }

    private JPanel createTopPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Hospital Reports Dashboard");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(ACCENT_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel selectionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        selectionPanel.setBackground(BACKGROUND_COLOR);

        JLabel reportTypeLabel = new JLabel("Select Report Type:");
        reportTypeLabel.setFont(LABEL_FONT);
        reportTypeLabel.setForeground(TEXT_COLOR);
        String[] reportTypes = {"Number of Patients", "Number of Doctors", "Common Illnesses", "Department Performance", "Patient Demographics", "Hospital Resources"};
        reportTypeComboBox = new JComboBox<>(reportTypes);
        reportTypeComboBox.setFont(LABEL_FONT);
        reportTypeComboBox.setBackground(Color.WHITE);
        reportTypeComboBox.setForeground(TEXT_COLOR);

        selectionPanel.add(reportTypeLabel);
        selectionPanel.add(reportTypeComboBox);

        panel.add(selectionPanel, BorderLayout.CENTER);

        dateTimeLabel = new JLabel();
        dateTimeLabel.setFont(LABEL_FONT);
        dateTimeLabel.setForeground(TEXT_COLOR);
        panel.add(dateTimeLabel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createCenterPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        reportTextArea = new JTextArea(10, 40);
        reportTextArea.setEditable(false);
        reportTextArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        reportTextArea.setBackground(Color.WHITE);
        reportTextArea.setForeground(TEXT_COLOR);
        JScrollPane scrollPane = new JScrollPane(reportTextArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR));

        chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawChart((Graphics2D) g);
            }
        };
        chartPanel.setPreferredSize(new Dimension(400, 300));
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setBorder(BorderFactory.createLineBorder(ACCENT_COLOR));

        panel.add(scrollPane, BorderLayout.WEST);
        panel.add(chartPanel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        generateReportButton = createStyledButton("Generate Report", "\uD83D\uDCC8");
        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                generateReport();
            }
        });

        exportButton = createStyledButton("Export", "\uD83D\uDCE4");
        exportButton.addActionListener(e -> exportReport());

        printButton = createStyledButton("Print", "\uD83D\uDDA8");
        printButton.addActionListener(e -> printReport());

        buttonPanel.add(generateReportButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(printButton);

        panel.add(buttonPanel, BorderLayout.CENTER);

        progressBar = new JProgressBar(0, 100);
        progressBar.setStringPainted(true);
        progressBar.setString("Ready");
        progressBar.setForeground(ACCENT_COLOR);
        panel.add(progressBar, BorderLayout.SOUTH);

        return panel;
    }

    private JButton createStyledButton(String text, String icon) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(LABEL_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(ACCENT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void generateReport() {
        String selectedReportType = (String) reportTypeComboBox.getSelectedItem();
        StringBuilder report = new StringBuilder();
        report.append("Report Type: ").append(selectedReportType).append("\n");
        report.append("Generated on: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date())).append("\n\n");

        Random random = new Random();

        SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
            @Override
            protected Void doInBackground() throws Exception {
                for (int i = 0; i <= 100; i += 10) {
                    Thread.sleep(100);
                    publish(i);
                }
                return null;
            }

            @Override
            protected void process(java.util.List<Integer> chunks) {
                int i = chunks.get(chunks.size() - 1);
                progressBar.setValue(i);
                progressBar.setString("Generating report: " + i + "%");
            }

            @Override
            protected void done() {
                switch (selectedReportType) {
                    case "Number of Patients":
                        generatePatientReport(report, random);
                        break;
                    case "Number of Doctors":
                        generateDoctorReport(report, random);
                        break;
                    case "Common Illnesses":
                        generateIllnessReport(report, random);
                        break;
                    case "Department Performance":
                        generateDepartmentReport(report, random);
                        break;
                    case "Patient Demographics":
                        generateDemographicsReport(report, random);
                        break;
                    case "Hospital Resources":
                        generateResourcesReport(report, random);
                        break;
                }

                reportTextArea.setText(report.toString());
                chartPanel.repaint();
                progressBar.setString("Report generated successfully");
            }
        };

        worker.execute();
    }

    private void generatePatientReport(StringBuilder report, Random random) {
        int totalPatients = random.nextInt(1000) + 500;
        int inpatients = random.nextInt(totalPatients);
        int outpatients = totalPatients - inpatients;
        report.append("Total number of patients: ").append(totalPatients).append("\n");
        report.append("Inpatients: ").append(inpatients).append("\n");
        report.append("Outpatients: ").append(outpatients).append("\n");
    }

    private void generateDoctorReport(StringBuilder report, Random random) {
        int totalDoctors = random.nextInt(200) + 50;
        int specialists = random.nextInt(totalDoctors);
        int generalPractitioners = totalDoctors - specialists;
        report.append("Total number of doctors: ").append(totalDoctors).append("\n");
        report.append("Specialists: ").append(specialists).append("\n");
        report.append("General Practitioners: ").append(generalPractitioners).append("\n");
    }

    private void generateIllnessReport(StringBuilder report, Random random) {
        report.append("Top 5 Common Illnesses:\n");
        String[] illnesses = {"Influenza", "Hypertension", "Diabetes", "Asthma", "Arthritis"};
        int[] cases = new int[5];
        for (int i = 0; i < 5; i++) {
            cases[i] = random.nextInt(500 - i * 100) + 100;
            report.append(i + 1).append(". ").append(illnesses[i]).append(": ").append(cases[i]).append(" cases\n");
        }
    }

    private void generateDepartmentReport(StringBuilder report, Random random) {
        report.append("Department Performance (Patients treated):\n");
        String[] departments = {"Emergency", "Surgery", "Pediatrics", "Cardiology", "Neurology"};
        int[] patients = new int[5];
        for (int i = 0; i < 5; i++) {
            patients[i] = random.nextInt(1000 - i * 200) + 200;
            report.append(departments[i]).append(": ").append(patients[i]).append("\n");
        }
    }

    private void generateDemographicsReport(StringBuilder report, Random random) {
        int totalPatients = random.nextInt(1000) + 500;
        report.append("Total Patients: ").append(totalPatients).append("\n\n");
        report.append("Age Groups:\n");
        int[] ageGroups = new int[4];
        String[] ageLabels = {"0-18", "19-40", "41-65", "65+"};
        for (int i = 0; i < 4; i++) {
            ageGroups[i] = random.nextInt(totalPatients / 4);
            totalPatients -= ageGroups[i];
            report.append(ageLabels[i]).append(": ").append(ageGroups[i]).append("\n");
        }
        ageGroups[3] += totalPatients; // Add remaining patients to the last group
        report.append("\nGender:\n");
        int malePatients = random.nextInt(totalPatients);
        report.append("Male: ").append(malePatients).append("\n");
        report.append("Female: ").append(totalPatients - malePatients).append("\n");
    }

    private void generateResourcesReport(StringBuilder report, Random random) {
        report.append("Hospital Resources:\n\n");
        report.append("Total Beds: ").append(random.nextInt(500) + 200).append("\n");
        report.append("ICU Beds: ").append(random.nextInt(50) + 20).append("\n");
        report.append("Operating Rooms: ").append(random.nextInt(20) + 5).append("\n");
        report.append("Ambulances: ").append(random.nextInt(15) + 5).append("\n");
        report.append("Ventilators: ").append(random.nextInt(100) + 50).append("\n");
        report.append("X-ray Machines: ").append(random.nextInt(10) + 3).append("\n");
        report.append("MRI Machines: ").append(random.nextInt(5) + 1).append("\n");
    }

    private void drawChart(Graphics2D g2d) {
        String selectedReportType = (String) reportTypeComboBox.getSelectedItem();
        int[] data;
        String[] labels;
        Random random = new Random();

        switch (selectedReportType) {
            case "Number of Patients":
                data = new int[]{random.nextInt(500), random.nextInt(500)};
                labels = new String[]{"Inpatients", "Outpatients"};
                break;
            case "Number of Doctors":
                data = new int[]{random.nextInt(100), random.nextInt(100)};
                labels = new String[]{"Specialists", "General Practitioners"};
                break;
            case "Common Illnesses":
                data = new int[]{random.nextInt(500), random.nextInt(400), random.nextInt(300), random.nextInt(200), random.nextInt(100)};
                labels = new String[]{"Influenza", "Hypertension", "Diabetes", "Asthma", "Arthritis"};
                break;
            case "Department Performance":
                data = new int[]{random.nextInt(1000), random.nextInt(500), random.nextInt(300), random.nextInt(400), random.nextInt(200)};
                labels = new String[]{"Emergency", "Surgery", "Pediatrics", "Cardiology", "Neurology"};
                break;
            case "Patient Demographics":
                data = new int[]{random.nextInt(250), random.nextInt(333), random.nextInt(333), random.nextInt(250)};
                labels = new String[]{"0-18", "19-40", "41-65", "65+"};
                break;
            case "Hospital Resources":
                data = new int[]{random.nextInt(500), random.nextInt(50), random.nextInt(20), random.nextInt(15), random.nextInt(100), random.nextInt(10), random.nextInt(5)};
                labels = new String[]{"Total Beds", "ICU Beds", "Operating Rooms", "Ambulances", "Ventilators", "X-ray Machines", "MRI Machines"};
                break;
            default:
                return;
        }

        int total = 0;
        for (int value : data) {
            total += value;
        }

        // Set up the coordinate system
        g2d.translate(chartPanel.getWidth() / 2, chartPanel.getHeight() / 2);
        g2d.scale(0.8, 0.8); // Scale down to 80% to fit within the panel

        // Draw the pie chart
        double startAngle = 0;
        for (int i = 0; i < data.length; i++) {
            double arcAngle = 360.0 * data[i] / total;
            g2d.setColor(getColor(i));
            Arc2D.Double arc = new Arc2D.Double(-100, -100, 200, 200, startAngle, arcAngle, Arc2D.PIE);
            g2d.fill(arc);
            startAngle += arcAngle;
        }

        // Draw the legend
        g2d.translate(120, -80);
        for (int i = 0; i < labels.length; i++) {
            g2d.setColor(getColor(i));
            g2d.fill(new Rectangle2D.Double(0, i * 20, 10, 10));
            g2d.setColor(Color.BLACK);
            g2d.drawString(labels[i], 15, i * 20 + 10);
        }
    }

    private Color getColor(int index) {
        Color[] colors = {
                new Color(70, 130, 180),   // Steel Blue
                new Color(255, 99, 71),    // Tomato
                new Color(60, 179, 113),   // Medium Sea Green
                new Color(238, 130, 238),  // Violet
                new Color(255, 165, 0),    // Orange
                new Color(106, 90, 205),   // Slate Blue
                new Color(255, 69, 0)      // Red-Orange
        };
        return colors[index % colors.length];
    }

    private void initializeTimer() {
        timer = new Timer(1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateDateTime();
            }
        });
        timer.start();
    }

    private void updateDateTime() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        dateTimeLabel.setText("Current Time: " + sdf.format(new Date()));
    }

    private void exportReport() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Export Report");
        int userSelection = fileChooser.showSaveDialog(this);

        if (userSelection == JFileChooser.APPROVE_OPTION) {
            // Implement export functionality here
            JOptionPane.showMessageDialog(this, "Report exported successfully!", "Export", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void printReport() {
        // Implement print functionality here
        JOptionPane.showMessageDialog(this, "Printing report...", "Print", JOptionPane.INFORMATION_MESSAGE);
    }
}