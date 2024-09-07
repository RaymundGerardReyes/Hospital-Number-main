package Systems.Employees;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import javax.swing.table.DefaultTableModel;

public class EmployeeDataFileHandler {

    // Fixed directory path
    public static final String FILE_PATH = "src/Systems/Employees/employee_data.txt";

    // Save single employee data
    public static void saveEmployeeData(EmployeeData employeeData) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH, true))) {
            writer.write(employeeData.toString());
            writer.newLine();
        } catch (IOException e) {
            System.err.println("Error saving employee data");
            e.printStackTrace();
        }
    }

    // Save all employee data
    public static void saveAllEmployeeData(List<EmployeeData> employeeDataList) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            for (EmployeeData data : employeeDataList) {
                writer.write(data.toString());
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving employee data");
            e.printStackTrace();
        }
    }

    // Read data from file and update table model
    public static void readDataFromFile(DefaultTableModel tableModel) {
        List<EmployeeData> employeeDataList = loadEmployeeData();
        for (EmployeeData data : employeeDataList) {
            tableModel.addRow(data.toTableRow());
        }
    }

    // Load employee data from file
    public static List<EmployeeData> loadEmployeeData() {
        List<EmployeeData> employeeDataList = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
            StringBuilder recordBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("-----------------------------------------------------")) {
                    String record = recordBuilder.toString().trim();
                    if (!record.isEmpty()) {
                        try {
                            employeeDataList.add(EmployeeData.fromString(record));
                        } catch (IllegalArgumentException e) {
                            System.err.println("Skipping invalid data record: " + record);
                        }
                    }
                    recordBuilder.setLength(0); // Clear the builder for the next record
                } else {
                    recordBuilder.append(line).append("\n");
                }
            }
            if (recordBuilder.length() > 0) {
                String record = recordBuilder.toString().trim();
                if (!record.isEmpty()) {
                    try {
                        employeeDataList.add(EmployeeData.fromString(record));
                    } catch (IllegalArgumentException e) {
                        System.err.println("Skipping invalid data record: " + record);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading employee data");
            e.printStackTrace();
        }
        return employeeDataList;
    }

    // Clear the employee data file
    public static void clearFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
            writer.write("");
        } catch (IOException e) {
            System.err.println("Error clearing employee data file");
            e.printStackTrace();
        }
    }

    // Watch for changes in the employee data file
    public static void watchFile(DefaultTableModel tableModel) {
        Path path = Paths.get(FILE_PATH).getParent();
        try (WatchService watchService = FileSystems.getDefault().newWatchService()) {
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);
            WatchKey key;
            while ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    if (event.context().toString().equals(FILE_PATH)) {
                        tableModel.setRowCount(0);  // Clear the table
                        readDataFromFile(tableModel);  // Reload the data
                    }
                }
                key.reset();
            }
        } catch (IOException | InterruptedException e) {
            System.err.println("Error watching employee data file");
            e.printStackTrace();
        }
    }
}
