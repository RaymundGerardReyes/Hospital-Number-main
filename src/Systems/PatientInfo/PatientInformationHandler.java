package Systems.PatientInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import javax.swing.table.DefaultTableModel;

public class PatientInformationHandler {
    public static final String FILE_PATH = "C:\\Users\\User\\Desktop\\Code\\Code Practice Summer\\Hospital Management System\\Hospital Number\\src\\Systems\\PatientInfo\\patient_data.txt";
    public static final String FILE_PATH1 = "C:\\Users\\User\\Desktop\\Code\\Code Practice Summer\\Hospital Management System\\Hospital Number\\src\\Systems\\HospitalID\\hospital_ids.txt";
    public static void saveDataToFile(String[] data) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(formatDataForFile(data));
            writer.newLine(); // Add a new line for each entry
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    

    private static String formatDataForFile(String[] data) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < data.length; i++) {
            builder.append("\"").append(data[i]).append("\""); // Enclose each field in quotes
            if (i < data.length - 1) {
                builder.append(","); // Use a comma to separate the data
            }
        }
        return builder.toString();
    }

    public static void readDataFromFile(DefaultTableModel tableModel) {
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] data = line.split("\",\"");
                for (int i = 0; i < data.length; i++) {
                    data[i] = data[i].replace("\"", "").replace("&&", ",");
                }
                tableModel.addRow(data);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Example method to clear the file (if needed)
    public static void clearFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
