package Systems.PatientManagement;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class PatientManagement {
    // Other methods and fields

    public String[][] getCurrentPatientData() {
         System.out.println("Fetching patient data");
        // For example, you can use a database query or read data from a file

        // Assuming patientData is a 2D array of strings representing the patient data
        String[][] patientData = {
            {"Name", "John Doe"},
            {"Birthday", "1990-01-01"},
            // Add more fields as needed
        };

        return patientData;
    }
}