// EmployeeData.java

package Systems.Employees;

public class EmployeeData {
    private String hospitalIdNumber;
    private String name;
    private String age;
    private String birthday;
    private String sexIdentity;
    private String address;
    private String phoneNumber;
    private String hospitalId;
    private String prc;
    private String position;
    private String assignArea;

    public EmployeeData(String hospitalIdNumber, String name, String age, String birthday, String sexIdentity, String address,
                        String phoneNumber, String hospitalId, String prc, String position, String assignArea) {
        this.hospitalIdNumber = hospitalIdNumber;
        this.name = name;
        this.age = age;
        this.birthday = birthday;
        this.sexIdentity = sexIdentity;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.hospitalId = hospitalId;
        this.prc = prc;
        this.position = position;
        this.assignArea = assignArea;
    }

    public static EmployeeData fromString(String data) {
        try {
            String[] lines = data.split("\n");
            if (lines.length != 11) {
                throw new IllegalArgumentException("Invalid data format, expected 11 lines, got " + lines.length);
            }
            String hospitalIdNumber = lines[0].split(": ")[1].trim();
            String name = lines[1].split(": ")[1].trim();
            String age = lines[2].split(": ")[1].trim();
            String birthday = lines[3].split(": ")[1].trim();
            String sexIdentity = lines[4].split(": ")[1].trim();
            String address = lines[5].split(": ")[1].trim();
            String phoneNumber = lines[6].split(": ")[1].trim();
            String hospitalId = lines[7].split(": ")[1].trim();
            String prc = lines[8].split(": ")[1].trim();
            String position = lines[9].split(": ")[1].trim();
            String assignArea = lines[10].split(": ")[1].trim();
            return new EmployeeData(hospitalIdNumber, name, age, birthday, sexIdentity, address, phoneNumber, hospitalId, prc, position, assignArea);
        } catch (ArrayIndexOutOfBoundsException | NullPointerException e) {
            throw new IllegalArgumentException("Error parsing employee data, invalid format: " + data, e);
        }
    }

    @Override
    public String toString() {
        return "Hospital ID Number: " + hospitalIdNumber + "\n" +
               "Name: " + name + "\n" +
               "Age: " + age + "\n" +
               "Birthday: " + birthday + "\n" +
               "Sex: " + sexIdentity + "\n" +
               "Address: " + address + "\n" +
               "Phone: " + phoneNumber + "\n" +
               "Hospital ID: " + hospitalId + "\n" +
               "PRC: " + prc + "\n" +
               "Position: " + position + "\n" +
               "Assigned Area: " + assignArea + "\n" +
               "-----------------------------------------------------";
    }

    public Object[] toTableRow() {
        return new Object[]{name, age, birthday, sexIdentity, address, phoneNumber, hospitalId, prc, position, assignArea};
    }

    // Getters for each field (optional, if needed)
    public String getHospitalIdNumber() { return hospitalIdNumber; }
    public String getName() { return name; }
    public String getAge() { return age; }
    public String getBirthday() { return birthday; }
    public String getSexIdentity() { return sexIdentity; }
    public String getAddress() { return address; }
    public String getPhoneNumber() { return phoneNumber; }
    public String getHospitalId() { return hospitalId; }
    public String getPrc() { return prc; }
    public String getPosition() { return position; }
    public String getAssignArea() { return assignArea; }
}
