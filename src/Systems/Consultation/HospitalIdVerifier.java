package Systems.Consultation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class HospitalIdVerifier {

    public HospitalIdVerifier() {
        // Constructor implementation if needed
    }

    public boolean verifyHospitalID(String hospitalID, String filePath) {
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals(hospitalID)) {
                    return true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }
}
