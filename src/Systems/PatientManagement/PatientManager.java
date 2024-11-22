package Systems.PatientManagement;

import Systems.Database.DatabaseConnection;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Stack;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class PatientManager {
    private List<Patient> patients;
    private DefaultTableModel tableModel;
    private Patient editingPatient;
    private int editingRow = -1;
    private Stack<Object[]> undoStack = new Stack<>();

    public PatientManager(DefaultTableModel tableModel) {
        this.patients = new ArrayList<>();
        this.tableModel = tableModel;
        loadPatientsFromDatabase();
    }

    public List<Patient> getPatients() {
        return new ArrayList<>(patients);
    }

    public void loadPatientsFromDatabase() {
        String query = "SELECT * FROM patients";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            patients.clear();
            while (rs.next()) {
                patients.add(createPatientFromResultSet(rs));
            }
            refreshTable();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error loading patients: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }


    public void editSelectedPatient(int selectedRow) {
        if (selectedRow >= 0 && selectedRow < patients.size()) {
            editingPatient = patients.get(selectedRow);
            editingRow = selectedRow;
            
            // You can implement further actions here, such as populating a form or dialog
            JOptionPane.showMessageDialog(null, "Editing Patient: " + editingPatient.getFirstName() + " " + editingPatient.getLastName());
        } else {
            JOptionPane.showMessageDialog(null, "Select a valid patient to edit.", "Edit Patient", JOptionPane.WARNING_MESSAGE);
        }
    }
    private Patient createPatientFromResultSet(ResultSet rs) throws SQLException {
        return new Patient(
            rs.getString("hospital_id"),
            rs.getString("last_name"),
            rs.getString("first_name"),
            rs.getString("middle_name"),
            rs.getString("birthday"),
            rs.getInt("age"),
            rs.getString("sex"),
            rs.getString("phone"),
            rs.getString("email"),
            rs.getString("street_address"),
            rs.getString("city"),
            rs.getString("state"),
            rs.getString("zip_code"),
            rs.getString("emergency_contact_name"),
            rs.getString("emergency_contact_relationship"),
            rs.getString("emergency_contact_phone"),
            rs.getString("insurance_provider"),
            rs.getString("policy_number"),
            rs.getString("additional_notes")
        );
    }



    // Edit the selected patient based on the data in the model and adds an undo point
    public void editPatient(int selectedRow) {
        if (selectedRow != -1) {
            // Retrieve patient data from tableModel
            Object[] currentRowData = new Object[tableModel.getColumnCount()];
            for (int i = 0; i < tableModel.getColumnCount(); i++) {
                currentRowData[i] = tableModel.getValueAt(selectedRow, i);
            }
            undoStack.push(currentRowData);

            Patient patient = patients.get(selectedRow);
            editPatientInDatabase(patient);
            patients.set(selectedRow, patient);

            // Update the tableModel with the edited patient data
            tableModel.setValueAt(patient.getHospitalId(), selectedRow, 0);
            tableModel.setValueAt(patient.getLastName(), selectedRow, 1);
            tableModel.setValueAt(patient.getFirstName(), selectedRow, 2);
            tableModel.setValueAt(patient.getMiddleName(), selectedRow, 3);
            tableModel.setValueAt(patient.getBirthday(), selectedRow, 4);
            tableModel.setValueAt(patient.getAge(), selectedRow, 5);
            tableModel.setValueAt(patient.getSex(), selectedRow, 6);
        } else {
            JOptionPane.showMessageDialog(null, "Please select a patient to edit.", "No Selection", JOptionPane.ERROR_MESSAGE);
        }
    }

    // Undo the last edit operation
    public void undo(int selectedRow) {
        if (!undoStack.isEmpty() && selectedRow != -1) {
            Object[] previousData = undoStack.pop();
            for (int i = 0; i < previousData.length; i++) {
                tableModel.setValueAt(previousData[i], selectedRow, i);
            }
            // Update the patients list with the restored data
            patients.set(selectedRow, createPatientFromRowData(previousData));
        }
    }

    private Patient createPatientFromRowData(Object[] rowData) {
        return new Patient(
            (String) rowData[0],
            (String) rowData[1],
            (String) rowData[2],
            (String) rowData[3],
            (String) rowData[4],
            (int) rowData[5],
            (String) rowData[6],
            "", "", "", "", "", "", "", "", "", "", "", ""
        );
    }

    public void refreshTable() {
        tableModel.setRowCount(0);
        for (Patient patient : patients) {
            addPatientToTable(patient);
        }
    }

    private void addPatientToTable(Patient patient) {
        tableModel.addRow(new Object[]{
            patient.getHospitalId(),
            patient.getLastName(),
            patient.getFirstName(),
            patient.getMiddleName(),
            patient.getBirthday(),
            patient.getAge(),
            patient.getSex()
        });
    }

  public void saveEditedPatient() {
        if (editingPatient != null && editingRow != -1) {
            editPatientInDatabase(editingPatient);
            patients.set(editingRow, editingPatient);
            refreshTable();
            cancelEdit();
        }
    }

    public void cancelEdit() {
        editingPatient = null;
        editingRow = -1;
    }

    public void deleteSelectedPatient(int selectedRow) {
        if (selectedRow != -1) {
            Patient selectedPatient = patients.get(selectedRow);
            int confirmation = JOptionPane.showConfirmDialog(null,
                "Are you sure you want to delete patient " + selectedPatient.getFirstName() + " " + selectedPatient.getLastName() + "?",
                "Delete Patient", JOptionPane.YES_NO_OPTION);
            if (confirmation == JOptionPane.YES_OPTION) {
                deletePatientFromDatabase(selectedPatient);
                patients.remove(selectedRow);
                refreshTable();
            }
        } else {
            JOptionPane.showMessageDialog(null, "Select a patient to delete.", "Delete Patient", JOptionPane.WARNING_MESSAGE);
        }
    }

    public void search(String searchTerm) {
        List<Patient> filteredPatients = patients.stream()
            .filter(patient -> patientMatchesSearch(patient, searchTerm))
            .collect(Collectors.toList());

        tableModel.setRowCount(0);
        for (Patient patient : filteredPatients) {
            addPatientToTable(patient);
        }
    }

    private boolean patientMatchesSearch(Patient patient, String searchTerm) {
        searchTerm = searchTerm.toLowerCase();
        return (patient.getHospitalId() != null && patient.getHospitalId().toLowerCase().contains(searchTerm)) ||
               (patient.getLastName() != null && patient.getLastName().toLowerCase().contains(searchTerm)) ||
               (patient.getFirstName() != null && patient.getFirstName().toLowerCase().contains(searchTerm)) ||
               (patient.getMiddleName() != null && patient.getMiddleName().toLowerCase().contains(searchTerm)) ||
               (patient.getBirthday() != null && patient.getBirthday().toLowerCase().contains(searchTerm)) ||
               String.valueOf(patient.getAge()).contains(searchTerm) ||
               (patient.getSex() != null && patient.getSex().toLowerCase().contains(searchTerm));
    }

    private void deletePatientFromDatabase(Patient patient) {
        String deleteQuery = "DELETE FROM patients WHERE hospital_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(deleteQuery)) {
            pstmt.setString(1, patient.getHospitalId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error deleting patient: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void editPatientInDatabase(Patient patient) {
        String updateQuery = "UPDATE patients SET last_name = ?, first_name = ?, middle_name = ?, birthday = ?, " +
                             "age = ?, sex = ?, phone = ?, email = ?, street_address = ?, city = ?, state = ?, " +
                             "zip_code = ?, emergency_contact_name = ?, emergency_contact_relationship = ?, " +
                             "emergency_contact_phone = ?, insurance_provider = ?, policy_number = ?, " +
                             "additional_notes = ? WHERE hospital_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(updateQuery)) {
            pstmt.setString(1, patient.getLastName());
            pstmt.setString(2, patient.getFirstName());
            pstmt.setString(3, patient.getMiddleName());
            pstmt.setString(4, patient.getBirthday());
            pstmt.setInt(5, patient.getAge());
            pstmt.setString(6, patient.getSex());
            pstmt.setString(7, patient.getPhone());
            pstmt.setString(8, patient.getEmail());
            pstmt.setString(9, patient.getAddress());
            pstmt.setString(10, patient.getCity());
            pstmt.setString(11, patient.getState());
            pstmt.setString(12, patient.getZip());
            pstmt.setString(13, patient.getEmergencyContactName());
            pstmt.setString(14, patient.getEmergencyContactRelationship());
            pstmt.setString(15, patient.getEmergencyContactPhone());
            pstmt.setString(16, patient.getInsuranceProvider());
            pstmt.setString(17, patient.getPolicyNumber());
            pstmt.setString(18, patient.getNotes());
            pstmt.setString(19, patient.getHospitalId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Error updating patient: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
