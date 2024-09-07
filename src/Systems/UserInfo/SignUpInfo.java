package Systems.UserInfo;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SignUpInfo implements Serializable {
    private String username;
    private String name;
    private String age;
    private String sex;
    private String address;
    private String phone;
    private String email;
    private String password;

    public static final String FILE_PATH = "src/Systems/UserInfo/signup_info.txt";

    // Constructor to initialize signup information
    public SignUpInfo(String username, String name, String age, String sex, String address, String phone, String email, String password) {
        this.username = username;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.address = address;
        this.phone = phone;
        this.email = email;
        this.password = password;
    }

    // Getter methods for username and password
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Method to save signup information to file
    public boolean saveToFile() {
        try {
            // Create file if it doesn't exist
            Path filePath = Paths.get(FILE_PATH);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }

            // Read existing data
            StringBuilder fileContent = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(FILE_PATH))) {
                String line;
                boolean userFound = false;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("Username: ")) {
                        String existingUsername = line.substring(10).trim();
                        if (existingUsername.equals(username)) {
                            // Skip old data for this user
                            for (int i = 0; i < 8; i++) {
                                reader.readLine();
                            }
                            // Append new data
                            fileContent.append(formatUserData());
                            userFound = true;
                        } else {
                            // Keep existing data
                            fileContent.append(line).append("\n");
                        }
                    } else {
                        fileContent.append(line).append("\n");
                    }
                }
                if (!userFound) {
                    // Append new user data
                    fileContent.append(formatUserData());
                }
            }

            // Write updated data back to file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(FILE_PATH))) {
                writer.write(fileContent.toString());
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private String formatUserData() {
        return "Username: " + username + "\n" +
               "Name: " + name + "\n" +
               "Age: " + age + "\n" +
               "Sex: " + sex + "\n" +
               "Address: " + address + "\n" +
               "Phone: " + phone + "\n" +
               "Email: " + email + "\n" +
               "Password: " + password + "\n" +
               "---------------------------\n";
    }
}
