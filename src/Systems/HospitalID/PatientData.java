package Systems.HospitalID;

public class PatientData {
    private String name;
    private int age;
    private String birthday;
    private String sex;
    private String address;
    private String phone;
    private String healthConcern;
    private String hospitalId;

    // Constructor
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

    // Getter methods
    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getBirthday() {
        return birthday;
    }

    public String getSex() {
        return sex;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public String getHealthConcern() {
        return healthConcern;
    }

    public String getHospitalId() {
        return hospitalId;
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

    // Static method to parse a PatientData object from a string
    public static PatientData fromString(String record) {
        System.out.println("Processing record:\n" + record); // Debugging output

        String[] lines = record.split("\n");
        if (lines.length < 8) {
            throw new IllegalArgumentException("Invalid data format: Insufficient lines in record.");
        }

        try {
            String hospitalId = extractValue(lines[0], "Hospital ID Number");
            String name = extractValue(lines[1], "Name");
            int age = Integer.parseInt(extractValue(lines[2], "Age"));
            String birthday = extractValue(lines[3], "Birthday");
            String sex = extractValue(lines[4], "Sex");
            String address = extractValue(lines[5], "Address");
            String phone = extractValue(lines[6], "Phone");
            String healthConcern = extractValue(lines[7], "Health Concern");

            // Debugging output
            System.out.println("Hospital ID: " + hospitalId);
            System.out.println("Name: " + name);
            System.out.println("Age: " + age);
            System.out.println("Birthday: " + birthday);
            System.out.println("Sex: " + sex);
            System.out.println("Address: " + address);
            System.out.println("Phone: " + phone);
            System.out.println("Health Concern: " + healthConcern);

            return new PatientData(name, age, birthday, sex, address, phone, healthConcern, hospitalId);
        } catch (ArrayIndexOutOfBoundsException | NumberFormatException e) {
            System.err.println("Error processing record: " + record);
            e.printStackTrace();
            throw new IllegalArgumentException("Invalid data format: " + record, e);
        }
    }

    // Helper method to extract values from lines
    private static String extractValue(String line, String expectedKey) {
        if (!line.startsWith(expectedKey + ":")) {
            throw new IllegalArgumentException("Invalid data format: Expected key \"" + expectedKey + "\" but found: " + line);
        }

        String[] parts = line.split(":", 2); // Limit to 2 parts to avoid extra splits
        if (parts.length < 2 || parts[1].trim().isEmpty()) {
            throw new ArrayIndexOutOfBoundsException("Invalid format, missing ':' or value in line: " + line);
        }
        return parts[1].trim();
    }
}
