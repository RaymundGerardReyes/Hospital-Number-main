package Systems.HospitalID;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {
    public static List<PatientData> loadPatientData() throws IOException {
        List<PatientData> patientDataList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HospitalIDPanel.FILE_PATH))) {
            StringBuilder recordBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("----------------------------------------------------")) {
                    String record = recordBuilder.toString().trim();
                    if (!record.isEmpty()) {
                        try {
                            patientDataList.add(PatientData.fromString(record));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Skipping invalid data record: " + record);
                        }
                    }
                    recordBuilder.setLength(0);
                } else {
                    recordBuilder.append(line).append("\n");
                }
            }
            if (recordBuilder.length() > 0) {
                String record = recordBuilder.toString().trim();
                if (!record.isEmpty()) {
                    try {
                        patientDataList.add(PatientData.fromString(record));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Skipping invalid data record: " + record);
                    }
                }
            }
        }
        return patientDataList;
    }

    public static void savePatientData(PatientData patientData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HospitalIDPanel.FILE_PATH, true))) {
            writer.write(patientData.toString());
            writer.newLine();
            writer.flush();
        } catch (IOException e) {
            System.err.println("Error saving patient data");
            e.printStackTrace();
        }
    }

    public static void saveAllPatientData(List<PatientData> patientDataList) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HospitalIDPanel.FILE_PATH))) {
            for (PatientData data : patientDataList) {
                writer.write(data.toString());
                writer.newLine();
            }
            writer.flush();
        }
    }
}
