package Systems.Consultation;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class PatientDataManager {

    private Map<String, PatientData> patientDataMap;
    private final ExecutorService executorService;

    public PatientDataManager(String filePath) {
        patientDataMap = new HashMap<>();
        executorService = Executors.newCachedThreadPool();
        loadPatientDataFromFile(filePath);
        watchFileChanges(filePath);
    }

    public ExecutorService getExecutorService() {
        return executorService;
    }

    public void loadPatientDataFromFile(String filePath) {
        patientDataMap.clear();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            StringBuilder recordBuilder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.trim().equals("----------------------------------------------------")) {
                    String record = recordBuilder.toString().trim();
                    if (!record.isEmpty()) {
                        try {
                            PatientData patientData = PatientData.fromString(record);
                            patientDataMap.put(patientData.hospitalId, patientData);
                        } catch (IllegalArgumentException e) {
                            System.err.println("Skipping invalid data record: " + record);
                        }
                    }
                    recordBuilder.setLength(0); // Reset the builder for the next record
                } else {
                    recordBuilder.append(line).append("\n");
                }
            }
            // Process the last record if the file does not end with the separator
            if (recordBuilder.length() > 0) {
                String record = recordBuilder.toString().trim();
                if (!record.isEmpty()) {
                    try {
                        PatientData patientData = PatientData.fromString(record);
                        patientDataMap.put(patientData.hospitalId, patientData);
                    } catch (IllegalArgumentException e) {
                        System.err.println("Skipping invalid data record: " + record);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load patient data from file: " + e.getMessage());
        }
    }

    public PatientData getPatientDataByHospitalId(String hospitalId) {
        return patientDataMap.get(hospitalId);
    }

    public Collection<PatientData> getAllPatientData() {
        return patientDataMap.values();
    }

    private void watchFileChanges(String filePath) {
        Path path = Paths.get(filePath).getParent();
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_MODIFY);

            executorService.submit(() -> {
                while (true) {
                    WatchKey key;
                    try {
                        key = watchService.take();
                    } catch (InterruptedException ex) {
                        return;
                    }

                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        if (kind == StandardWatchEventKinds.OVERFLOW) {
                            continue;
                        }

                        WatchEvent<Path> ev = (WatchEvent<Path>) event;
                        Path fileName = ev.context();

                        if (fileName.toString().equals(Paths.get(filePath).getFileName().toString())) {
                            loadPatientDataFromFile(filePath);
                            System.out.println("File updated, data reloaded.");
                        }
                    }

                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }
                }
            });
        } catch (IOException e) {
            System.err.println("Failed to set up file watcher: " + e.getMessage());
        }
    }

    public static class PatientData {
        String name;
        int age;
        String birthday;
        String sex;
        String address;
        String phone;
        String healthConcern;
        String hospitalId;

        public PatientData(String name, int age, String birthday, String sex, String address, String phone, String healthConcern, String hospitalId) {
            this.name = name;
            this.age = age;
            this.birthday = birthday;
            this.sex = sex;
            this.address = address;
            this.phone = phone;
            this.healthConcern = healthConcern;
            this.hospitalId = hospitalId;
        }

        @Override
        public String toString() {
            return "Hospital ID Number: " + hospitalId + "\n" +
                   "Name: " + name + "\n" +
                   "Age: " + age + "\n" +
                   "Birthday: " + birthday + "\n" +
                   "Sex: " + sex + "\n" +
                   "Address: " + address + "\n" +
                   "Phone: " + phone + "\n" +
                   "Health Concern: " + healthConcern + "\n" +
                   "----------------------------------------------------";
        }

        public static PatientData fromString(String record) {
            String[] lines = record.split("\n");
            if (lines.length < 8) {
                throw new IllegalArgumentException("Invalid data format: " + record);
            }
        
            String hospitalId = extractValue(lines[0], "Hospital ID Number");
            String name = extractValue(lines[1], "Name");
            int age = Integer.parseInt(extractValue(lines[2], "Age"));
            String birthday = extractValue(lines[3], "Birthday");
            String sex = extractValue(lines[4], "Sex");
            String address = extractValue(lines[5], "Address");
            String phone = extractValue(lines[6], "Phone");
            String healthConcern = extractValue(lines[7], "Health Concern");
        
            return new PatientData(name, age, birthday, sex, address, phone, healthConcern, hospitalId);
        }
        
        private static String extractValue(String line, String expectedKey) {
            String[] parts = line.split(":", 2);
            if (parts.length < 2 || parts[1].trim().isEmpty()) {
                throw new IllegalArgumentException("Invalid format for " + expectedKey + ": " + line);
            }
            return parts[1].trim();
        }
        
    }
}
