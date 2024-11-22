package Systems.Consultation;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.sql.*;
import Systems.Database.DatabaseConnection;

public class PatientConsultationPanel extends JPanel {
    private static final Color PRIMARY_COLOR = new Color(0, 123, 255);
    private static final Color SECONDARY_COLOR = new Color(108, 117, 125);
    private static final Color BACKGROUND_COLOR = new Color(248, 249, 250);
    private static final Color TEXT_COLOR = new Color(33, 37, 41);
    private static final Font MAIN_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 24);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);

    private JTextField patientNameField, ageField, genderField, previousConsultationField;
    private JTextArea chiefComplaintArea;
    private JCheckBox[] medicalHistoryCheckboxes;
    private JTextField medicationsField, allergiesField, familyHistoryField;
    private JCheckBox[] socialHistoryCheckboxes;
    private JCheckBox[][] rosCheckboxes;
    private JTextField[] vitalSignsFields;
    private JCheckBox[] physicalExamCheckboxes;
    private JCheckBox[] investigationsCheckboxes;
    private JTextField diagnosisField, secondaryDiagnosisField;
    private JTextArea treatmentRecommendationsArea;
    private JTextField nextFollowUpField, doctorNameField;

    private ConsultationParentPanel parentPanel;
    private Connection connection;


    private JCheckBox[] bloodWorkCheckboxes, imagingCheckboxes, ecgCheckboxes, urinalysisCheckboxes, otherCheckboxes;


    
    public PatientConsultationPanel(ConsultationParentPanel parentPanel) {
        this.parentPanel = parentPanel;
        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(new EmptyBorder(20, 20, 20, 20));

        initializeComponents();
        initializeDatabaseConnection();
    }

    private void initializeComponents() {
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridwidth = GridBagConstraints.REMAINDER;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        formPanel.add(createSectionPanel("Patient Information", createPatientInfoPanel()), gbc);
        formPanel.add(createSectionPanel("Chief Complaint", createChiefComplaintPanel()), gbc);
        formPanel.add(createSectionPanel("Medical History", createMedicalHistoryPanel()), gbc);
        formPanel.add(createSectionPanel("Review of Systems", createReviewOfSystemsPanel()), gbc);
        formPanel.add(createSectionPanel("Physical Examination", createPhysicalExaminationPanel()), gbc);
        formPanel.add(createSectionPanel("Investigations/Tests Ordered", createInvestigationsPanel()), gbc);
        formPanel.add(createSectionPanel("Diagnosis", createDiagnosisPanel()), gbc);
        formPanel.add(createSectionPanel("Treatment Plan", createTreatmentPlanPanel()), gbc);
        formPanel.add(createSectionPanel("Doctor's Signature", createDoctorSignaturePanel()), gbc);

        JButton submitButton = new JButton("Submit Consultation");
        submitButton.setFont(SUBTITLE_FONT);
        submitButton.setBackground(PRIMARY_COLOR);
        submitButton.setForeground(Color.WHITE);
        submitButton.setPreferredSize(new Dimension(300, 50));
        submitButton.addActionListener(e -> submitConsultation());
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        buttonPanel.setBackground(BACKGROUND_COLOR);
        buttonPanel.add(submitButton);
        formPanel.add(buttonPanel, gbc);

        JScrollPane scrollPane = new JScrollPane(formPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
    }

    private JPanel createSectionPanel(String title, JPanel content) {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, SECONDARY_COLOR),
            new EmptyBorder(10, 10, 20, 10)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(SUBTITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(content, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createPatientInfoPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 20, 10));
        panel.setBackground(BACKGROUND_COLOR);

        patientNameField = createStyledTextField(20);
        ageField = createStyledTextField(5);
        genderField = createStyledTextField(10);
        previousConsultationField = createStyledTextField(20);

        panel.add(createStyledLabel("Patient Name:"));
        panel.add(patientNameField);
        panel.add(createStyledLabel("Age:"));
        panel.add(ageField);
        panel.add(createStyledLabel("Gender:"));
        panel.add(genderField);
        panel.add(createStyledLabel("Previous Consultation Date:"));
        panel.add(previousConsultationField);
        panel.add(createStyledLabel("Current Consultation Date:"));
        panel.add(createStyledLabel(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())));

        return panel;
    }

    private JPanel createChiefComplaintPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BACKGROUND_COLOR);

        chiefComplaintArea = new JTextArea(5, 20);
        chiefComplaintArea.setFont(MAIN_FONT);
        chiefComplaintArea.setLineWrap(true);
        chiefComplaintArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(chiefComplaintArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR));

        panel.add(createStyledLabel("Chief Complaint:"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createMedicalHistoryPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 1, 0, 10));
        panel.setBackground(BACKGROUND_COLOR);

        String[] medicalHistoryOptions = {"No significant medical history", "Chronic conditions", "Previous surgeries/hospitalizations"};
        medicalHistoryCheckboxes = createStyledCheckboxes(medicalHistoryOptions);
        for (JCheckBox checkbox : medicalHistoryCheckboxes) {
            panel.add(checkbox);
        }

        panel.add(createStyledLabel("Medications:"));
        medicationsField = createStyledTextField(20);
        panel.add(medicationsField);

        panel.add(createStyledLabel("Allergies:"));
        allergiesField = createStyledTextField(20);
        panel.add(allergiesField);

        panel.add(createStyledLabel("Family History:"));
        familyHistoryField = createStyledTextField(20);
        panel.add(familyHistoryField);

        String[] socialHistoryOptions = {"Non-smoker", "Smoker", "Alcohol use", "Drug use"};
        socialHistoryCheckboxes = createStyledCheckboxes(socialHistoryOptions);
        for (JCheckBox checkbox : socialHistoryCheckboxes) {
            panel.add(checkbox);
        }

        return panel;
    }

    private JPanel createReviewOfSystemsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 4, 10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        String[][] rosOptions = {
            {"Fever", "Weight loss", "Fatigue", "Night sweats"},
            {"Chest pain", "Palpitations", "Shortness of breath"},
            {"Cough", "Wheezing", "Sputum production"},
            {"Nausea", "Vomiting", "Abdominal pain", "Diarrhea"},
            {"Joint pain", "Muscle weakness", "Back pain"},
            {"Headache", "Dizziness", "Numbness", "Seizures"},
            {"Rashes", "Itching", "Skin lesions"},
            {"Anxiety", "Depression", "Sleep disturbances"}
        };

        rosCheckboxes = new JCheckBox[rosOptions.length][];
        for (int i = 0; i < rosOptions.length; i++) {
            rosCheckboxes[i] = createStyledCheckboxes(rosOptions[i]);
            for (JCheckBox checkbox : rosCheckboxes[i]) {
                panel.add(checkbox);
            }
        }

        return panel;
    }

    private JPanel createPhysicalExaminationPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 20, 10));
        panel.setBackground(BACKGROUND_COLOR);

        String[] vitalSigns = {"BP", "Pulse", "Temperature", "Respiratory Rate"};
        vitalSignsFields = new JTextField[vitalSigns.length];
        for (int i = 0; i < vitalSigns.length; i++) {
            panel.add(createStyledLabel(vitalSigns[i] + ":"));
            vitalSignsFields[i] = createStyledTextField(10);
            panel.add(vitalSignsFields[i]);
        }

        String[] examOptions = {"Well-nourished", "Ill-appearing", "In distress", "Normal findings", "Swelling", "Tenderness", "Lymphadenopathy"};
        physicalExamCheckboxes = createStyledCheckboxes(examOptions);
        for (JCheckBox checkbox : physicalExamCheckboxes) {
            panel.add(checkbox);
        }

        return panel;
    }

    private JPanel createInvestigationsPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 20, 10));
        panel.setBackground(BACKGROUND_COLOR);

        String[] bloodWorkOptions = {
            "Complete Blood Count (CBC)", "Electrolytes (Na, K, Cl, HCO3)",
            "Liver Function Tests (LFTs)", "Renal Function Tests (RFTs)",
            "Blood Glucose Level (Fasting, Random)", "Lipid Profile",
            "Thyroid Function Tests (TFTs)", "Coagulation Profile (PT, aPTT, INR)",
            "Hemoglobin A1c", "Arterial Blood Gas (ABG)",
            "Vitamin and Mineral Levels (D, B12, Ca, Mg)", "C-Reactive Protein (CRP), ESR"
        };

        String[] imagingOptions = {
            "Chest X-ray", "Abdominal X-ray", "Bone X-ray (Specific Area)",
            "Abdominal Ultrasound", "Pelvic Ultrasound", "Renal Ultrasound",
            "Carotid Doppler", "CT Scan (Chest, Abdomen, Pelvis, Brain)",
            "MRI (Brain, Spine, Musculoskeletal)"
        };

        String[] ecgOptions = {
            "Electrocardiogram (ECG)", "Echocardiogram (ECHO)",
            "Stress Test (Treadmill, Pharmacological)", "Holter Monitor (24-hour ECG)"
        };

        String[] urinalysisOptions = {
            "Routine Urinalysis", "Urine Culture", "24-hour Urine Collection",
            "Urine Pregnancy Test", "Urine Dipstick (Glucose, Blood, Ketones)"
        };

        String[] otherOptions = {
            "Blood Cultures", "Sputum Culture", "Throat Culture", "Stool Culture",
            "Gastroscopy (EGD)", "Colonoscopy", "Bronchoscopy", "Biopsy (Tissue Sampling)",
            "HIV Screening", "Hepatitis Panel", "Tuberculosis Test (PPD or Chest X-ray)",
            "Autoimmune Screen (ANA, RF)", "Genetic Testing", "D-dimer",
            "Prostate-Specific Antigen (PSA)", "Pregnancy Test (Urine or Serum)",
            "Arterial Blood Gas (ABG)"
        };

        bloodWorkCheckboxes = createStyledCheckboxes(bloodWorkOptions);
        imagingCheckboxes = createStyledCheckboxes(imagingOptions);
        ecgCheckboxes = createStyledCheckboxes(ecgOptions);
        urinalysisCheckboxes = createStyledCheckboxes(urinalysisOptions);
        otherCheckboxes = createStyledCheckboxes(otherOptions);

        panel.add(createSectionPanel("Blood Work", bloodWorkCheckboxes));
        panel.add(createSectionPanel("Imaging", imagingCheckboxes));
        panel.add(createSectionPanel("ECG", ecgCheckboxes));
        panel.add(createSectionPanel("Urinalysis", urinalysisCheckboxes));
        panel.add(createSectionPanel("Other Tests", otherCheckboxes));

        return panel;
    }

    // Helper method to create a section with a title and checkboxes
private JPanel createSectionPanel(String sectionTitle, JCheckBox[] checkboxes) {
    JPanel sectionPanel = new JPanel();
    sectionPanel.setLayout(new BoxLayout(sectionPanel, BoxLayout.Y_AXIS));
    sectionPanel.setBackground(BACKGROUND_COLOR);

    JLabel titleLabel = new JLabel(sectionTitle);
    titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
    sectionPanel.add(titleLabel);

    for (JCheckBox checkbox : checkboxes) {
        sectionPanel.add(checkbox);
    }

    return sectionPanel;
}

// Helper method to create checkboxes with a list of options

    private JPanel createDiagnosisPanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 20, 10));
        panel.setBackground(BACKGROUND_COLOR);

        panel.add(createStyledLabel("Primary Diagnosis:"));
        diagnosisField = createStyledTextField(20);
        panel.add(diagnosisField);

        panel.add(createStyledLabel("Secondary Diagnosis:"));
        secondaryDiagnosisField = createStyledTextField(20);
        panel.add(secondaryDiagnosisField);

        return panel;
    }

    private JPanel createTreatmentPlanPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 10));
        panel.setBackground(BACKGROUND_COLOR);

        treatmentRecommendationsArea = new JTextArea(5, 20);
        treatmentRecommendationsArea.setFont(MAIN_FONT);
        treatmentRecommendationsArea.setLineWrap(true);
        treatmentRecommendationsArea.setWrapStyleWord(true);
        JScrollPane scrollPane = new JScrollPane(treatmentRecommendationsArea);
        scrollPane.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR));

        panel.add(createStyledLabel("Treatment Recommendations:"), BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel followUpPanel = new JPanel(new BorderLayout(10, 0));
        followUpPanel.setBackground(BACKGROUND_COLOR);
        followUpPanel.add(createStyledLabel("Next Follow-up:"), BorderLayout.WEST);
        nextFollowUpField = createStyledTextField(10);
        followUpPanel.add(nextFollowUpField, BorderLayout.CENTER);

        panel.add(followUpPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createDoctorSignaturePanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2, 20, 10));
        panel.setBackground(BACKGROUND_COLOR);

        panel.add(createStyledLabel("Doctor's Name:"));
        doctorNameField = createStyledTextField(20);
        panel.add(doctorNameField);

        panel.add(createStyledLabel("Date:"));
        panel.add(createStyledLabel(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())));

        return panel;
    }

    private JLabel createStyledLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(MAIN_FONT);
        label.setForeground(TEXT_COLOR);
        return label;
    }

    private JTextField createStyledTextField(int columns) {
        JTextField textField = new JTextField(columns);
        textField.setFont(MAIN_FONT);
        return textField;
    }

    private JCheckBox[] createStyledCheckboxes(String[] options) {
        JCheckBox[] checkboxes = new JCheckBox[options.length];
        for (int i = 0; i < options.length; i++) {
            checkboxes[i] = new JCheckBox(options[i]);
            checkboxes[i].setFont(MAIN_FONT);
            checkboxes[i].setBackground(BACKGROUND_COLOR);
        }
        return checkboxes;
    }

    private void initializeDatabaseConnection() {
        try {
            connection = DatabaseConnection.getConnection();
        } catch (SQLException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to connect to the database.", "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void saveConsultation(Consultation consultation) {
        String sql = "INSERT INTO Consultation (patient_name, age, gender, previous_consultation_date, " +
                "current_consultation_date, chief_complaint, medical_history, medications, allergies, " +
                "family_history, social_history, review_of_systems, bp, pulse, temperature, respiratory_rate, " +
                "physical_exam, investigations_blood_work, investigations_imaging, investigations_ecg, " +
                "investigations_urinalysis, investigations_other, primary_diagnosis, secondary_diagnosis, " +
                "treatment_recommendations, next_follow_up, doctor_name) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (PreparedStatement pstmt = connection.prepareStatement(sql)) {
            pstmt.setString(1, consultation.getPatientName());
            pstmt.setInt(2, consultation.getAge());
            pstmt.setString(3, consultation.getGender());
            pstmt.setTimestamp(4, consultation.getPreviousConsultationDate());
            pstmt.setTimestamp(5, consultation.getCurrentConsultationDate());
            pstmt.setString(6, consultation.getChiefComplaint());
            pstmt.setString(7, setToString(consultation.getMedicalHistory()));
            pstmt.setString(8, consultation.getMedications());
            pstmt.setString(9, consultation.getAllergies());
            pstmt.setString(10, consultation.getFamilyHistory());
            pstmt.setString(11, setToString(consultation.getSocialHistory()));
            pstmt.setString(12, setToString(consultation.getReviewOfSystems()));
            pstmt.setString(13, consultation.getBp());
            pstmt.setInt(14, consultation.getPulse());
            pstmt.setBigDecimal(15, consultation.getTemperature());
            pstmt.setInt(16, consultation.getRespiratoryRate());
            pstmt.setString(17, setToString(consultation.getPhysicalExam()));
            pstmt.setString(18, setToString(consultation.getInvestigationsBloodWork()));
            pstmt.setString(19, setToString(consultation.getInvestigationsImaging()));
            pstmt.setString(20, setToString(consultation.getInvestigationsEcg()));
            pstmt.setString(21, setToString(consultation.getInvestigationsUrinalysis()));
            pstmt.setString(22, setToString(consultation.getInvestigationsOther()));
            pstmt.setString(23, consultation.getPrimaryDiagnosis());
            pstmt.setString(24, consultation.getSecondaryDiagnosis());
            pstmt.setString(25, consultation.getTreatmentRecommendations());
            pstmt.setDate(26, new java.sql.Date(consultation.getNextFollowUp().getTime()));
            pstmt.setString(27, consultation.getDoctorName());

            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                System.out.println("Consultation data saved successfully.");
            } else {
                System.out.println("Failed to save consultation data.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Error saving consultation data", e);
        }
    }


    private static String setToString(Set<String> set) {
        return set != null ? String.join(",", set) : null;
    }

   
    private Set<String> getSelectedCheckboxValues(JCheckBox[][] checkboxes) {
        Set<String> selectedValues = new HashSet<>();
        for (JCheckBox[] row : checkboxes) {
            for (JCheckBox checkbox : row) {
                if (checkbox.isSelected()) {
                    selectedValues.add(checkbox.getText());
                }
            }
        }
        return selectedValues;
    }


    // Consultation class to hold the data
    public static class Consultation {
        private String patientName;
        private int age;
        private String gender;
        private Timestamp previousConsultationDate;
        private Timestamp currentConsultationDate;
        private String chiefComplaint;
        private Set<String> medicalHistory;
        private String medications;
        private String allergies;
        private String familyHistory;
        private Set<String> socialHistory;
        private Set<String> reviewOfSystems;
        private String bp;
        private int pulse;
        private BigDecimal temperature;
        private int respiratoryRate;
        private Set<String> physicalExam;
        private Set<String> investigationsBloodWork;
        private Set<String> investigationsImaging;
        private Set<String> investigationsEcg;
        private Set<String> investigationsUrinalysis;
        private Set<String> investigationsOther;
        private String primaryDiagnosis;
        private String secondaryDiagnosis;
        private String treatmentRecommendations;
        private Date nextFollowUp;
        private String doctorName;

        public Consultation(String patientName, int age, String gender) {
            this.patientName = patientName;
            this.age = age;
            this.gender = gender;
            this.currentConsultationDate = new Timestamp(System.currentTimeMillis());
        }

        // Getters and setters for all fields
        public String getPatientName() { return patientName; }
        public void setPatientName(String patientName) { this.patientName = patientName; }
        public int getAge() { return age; }
        public void setAge(int age) { this.age = age; }
        public String getGender() { return gender; }
        public void setGender(String gender) { this.gender = gender; }
        public Timestamp getPreviousConsultationDate() { return previousConsultationDate; }
        public void setPreviousConsultationDate(Timestamp previousConsultationDate) { this.previousConsultationDate = previousConsultationDate; }
        public Timestamp getCurrentConsultationDate() { return currentConsultationDate; }
        public void setCurrentConsultationDate(Timestamp currentConsultationDate) { this.currentConsultationDate = currentConsultationDate; }
        public String getChiefComplaint() { return chiefComplaint; }
        public void setChiefComplaint(String chiefComplaint) { this.chiefComplaint = chiefComplaint; }
        public Set<String> getMedicalHistory() { return medicalHistory; }
        public void setMedicalHistory(Set<String> medicalHistory) { this.medicalHistory = medicalHistory; }
        public String getMedications() { return medications; }
        public void setMedications(String medications) { this.medications = medications; }
        public String getAllergies() { return allergies; }
        public void setAllergies(String allergies) { this.allergies = allergies; }
        public String getFamilyHistory() { return familyHistory; }
        public void setFamilyHistory(String familyHistory) { this.familyHistory = familyHistory; }
        public Set<String> getSocialHistory() { return socialHistory; }
        public void setSocialHistory(Set<String> socialHistory) { this.socialHistory = socialHistory; }
        public Set<String> getReviewOfSystems() { return reviewOfSystems; }
        public void setReviewOfSystems(Set<String> reviewOfSystems) { this.reviewOfSystems = reviewOfSystems; }
        public String getBp() { return bp; }
        public void setBp(String bp) { this.bp = bp; }
        public int getPulse() { return pulse; }
        public void setPulse(int pulse) { this.pulse = pulse; }
        public BigDecimal getTemperature() { return temperature; }
        public void setTemperature(BigDecimal temperature) { this.temperature = temperature; }
        public int getRespiratoryRate() { return respiratoryRate; }
        public void setRespiratoryRate(int respiratoryRate) { this.respiratoryRate = respiratoryRate; }
        public Set<String> getPhysicalExam() { return physicalExam; }
        public void setPhysicalExam(Set<String> physicalExam) { this.physicalExam = physicalExam; }
        public Set<String> getInvestigationsBloodWork() { return investigationsBloodWork; }
        public void setInvestigationsBloodWork(Set<String> investigationsBloodWork) { this.investigationsBloodWork = investigationsBloodWork; }
        public Set<String> getInvestigationsImaging() { return investigationsImaging; }
        public void setInvestigationsImaging(Set<String> investigationsImaging) { this.investigationsImaging = investigationsImaging; }
        public Set<String> getInvestigationsEcg() { return investigationsEcg; }
        public void setInvestigationsEcg(Set<String> investigationsEcg) { this.investigationsEcg = investigationsEcg; }
        public Set<String> getInvestigationsUrinalysis() { return investigationsUrinalysis; }
        public void setInvestigationsUrinalysis(Set<String> investigationsUrinalysis) { this.investigationsUrinalysis = investigationsUrinalysis; }
        public Set<String> getInvestigationsOther() { return investigationsOther; }
        public void setInvestigationsOther(Set<String> investigationsOther) { this.investigationsOther = investigationsOther; }
        public String getPrimaryDiagnosis() { return primaryDiagnosis; }
        public void setPrimaryDiagnosis(String primaryDiagnosis) { this.primaryDiagnosis = primaryDiagnosis; }
        public String getSecondaryDiagnosis() { return secondaryDiagnosis; }
        public void setSecondaryDiagnosis(String secondaryDiagnosis) { this.secondaryDiagnosis = secondaryDiagnosis; }
        public String getTreatmentRecommendations() { return treatmentRecommendations; }
        public void setTreatmentRecommendations(String treatmentRecommendations) { this.treatmentRecommendations = treatmentRecommendations; }
        public Date getNextFollowUp() { return nextFollowUp; }
        public void setNextFollowUp(Date nextFollowUp) { this.nextFollowUp = nextFollowUp; }
        public String getDoctorName() { return doctorName; }
        public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
    }


    private void updateTransactionSlip(String patientName) {
        if (parentPanel == null) {
            System.out.println("Warning: parentPanel is null, cannot update transaction slip");
            return;
        }

        StringBuilder consultationSummary = new StringBuilder();
        consultationSummary.append("Patient Consultation Summary\n\n");
        consultationSummary.append("Patient Name: ").append(patientName).append("\n");
        consultationSummary.append("Age: ").append(ageField.getText()).append("\n");
        consultationSummary.append("Gender: ").append(genderField.getText()).append("\n");
        consultationSummary.append("Previous Consultation Date: ").append(previousConsultationField.getText()).append("\n");
        consultationSummary.append("Current Consultation Date: ").append(new SimpleDateFormat("yyyy-MM-dd HH:mm").format(new Date())).append("\n\n");
        consultationSummary.append("Chief Complaint: ").append(chiefComplaintArea.getText()).append("\n\n");
        consultationSummary.append("Diagnosis: ").append(diagnosisField.getText()).append("\n");
        consultationSummary.append("Treatment Plan: ").append(treatmentRecommendationsArea.getText()).append("\n");
        consultationSummary.append("Next Follow-up: ").append(nextFollowUpField.getText()).append("\n");
        consultationSummary.append("Doctor: ").append(doctorNameField.getText());

        parentPanel.updateTransactionSlip(consultationSummary.toString());
    }
    
    private boolean isValidDateFormat(String dateStr) {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            return false;
        }
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            sdf.setLenient(false);
            sdf.parse(dateStr.trim());
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    private void submitConsultation() {
        try {
            // Validate required fields
            if (patientNameField.getText().trim().isEmpty() || 
                ageField.getText().trim().isEmpty() || 
                genderField.getText().trim().isEmpty()) {
                JOptionPane.showMessageDialog(this,
                    "Please fill in all required fields (Patient Name, Age, and Gender)",
                    "Missing Required Fields",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Validate date formats
            if (!isValidDateFormat(previousConsultationField.getText()) || 
                !isValidDateFormat(nextFollowUpField.getText())) {
                JOptionPane.showMessageDialog(this,
                    "Please enter dates in the format YYYY-MM-DD",
                    "Invalid Date Format",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            connection.setAutoCommit(false);

            // Create consultation object with basic info
            Consultation consultation = new Consultation(
                patientNameField.getText().trim(),
                Integer.parseInt(ageField.getText().trim()),
                genderField.getText().trim()
            );

            // Set dates with proper validation
            try {
                String prevDate = previousConsultationField.getText().trim();
                consultation.setPreviousConsultationDate(Timestamp.valueOf(prevDate + " 00:00:00"));
                
                String nextDate = nextFollowUpField.getText().trim();
                consultation.setNextFollowUp(java.sql.Date.valueOf(nextDate));
            } catch (IllegalArgumentException e) {
                JOptionPane.showMessageDialog(this,
                    "Invalid date format. Please use YYYY-MM-DD",
                    "Date Format Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Set consultation details
            consultation.setChiefComplaint(chiefComplaintArea.getText());
            consultation.setMedicalHistory(getSelectedCheckboxValues(new JCheckBox[][] { medicalHistoryCheckboxes }));
            consultation.setMedications(medicationsField.getText());
            consultation.setAllergies(allergiesField.getText());
            consultation.setFamilyHistory(familyHistoryField.getText());
            consultation.setSocialHistory(getSelectedCheckboxValues(new JCheckBox[][] { socialHistoryCheckboxes }));
            consultation.setReviewOfSystems(getSelectedCheckboxValues(rosCheckboxes));

            // Set vital signs with validation
            try {
                if (!vitalSignsFields[0].getText().trim().isEmpty()) {
                    consultation.setBp(vitalSignsFields[0].getText().trim());
                }
                if (!vitalSignsFields[1].getText().trim().isEmpty()) {
                    consultation.setPulse(Integer.parseInt(vitalSignsFields[1].getText().trim()));
                }
                if (!vitalSignsFields[2].getText().trim().isEmpty()) {
                    consultation.setTemperature(new BigDecimal(vitalSignsFields[2].getText().trim()));
                }
                if (!vitalSignsFields[3].getText().trim().isEmpty()) {
                    consultation.setRespiratoryRate(Integer.parseInt(vitalSignsFields[3].getText().trim()));
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Invalid number format in vital signs",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Set examination and investigation details
            consultation.setPhysicalExam(getSelectedCheckboxValues(new JCheckBox[][] { physicalExamCheckboxes }));
            consultation.setInvestigationsBloodWork(getSelectedCheckboxValues(new JCheckBox[][] { bloodWorkCheckboxes }));
            consultation.setInvestigationsImaging(getSelectedCheckboxValues(new JCheckBox[][] { imagingCheckboxes }));
            consultation.setInvestigationsEcg(getSelectedCheckboxValues(new JCheckBox[][] { ecgCheckboxes }));
            consultation.setInvestigationsUrinalysis(getSelectedCheckboxValues(new JCheckBox[][] { urinalysisCheckboxes }));
            consultation.setInvestigationsOther(getSelectedCheckboxValues(new JCheckBox[][] { otherCheckboxes }));

            // Set diagnosis and treatment details
            consultation.setPrimaryDiagnosis(diagnosisField.getText());
            consultation.setSecondaryDiagnosis(secondaryDiagnosisField.getText());
            consultation.setTreatmentRecommendations(treatmentRecommendationsArea.getText());
            consultation.setDoctorName(doctorNameField.getText());

            // Save consultation and commit transaction
            saveConsultation(consultation);
            connection.commit();

            JOptionPane.showMessageDialog(this,
                "Consultation submitted successfully!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
            updateTransactionSlip(consultation.getPatientName());

        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Failed to submit consultation: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}