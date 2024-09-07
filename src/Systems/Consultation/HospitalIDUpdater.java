package Systems.Consultation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.JTextField;

public class HospitalIDUpdater {

    private JTextField hospitalIdField;

    public HospitalIDUpdater(JTextField hospitalIdField) {
        this.hospitalIdField = hospitalIdField;
    }

    public void updateHospitalIdField(String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            if ((line = reader.readLine()) != null) {
                // Extract the Hospital ID from the line
                String hospitalId = line.split(",")[0].trim();
                hospitalIdField.setText(hospitalId);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
