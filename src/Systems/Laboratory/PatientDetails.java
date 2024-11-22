package Systems.Laboratory;

public class PatientDetails {
    private String hospitalID;
    private String patientName;
    private int age;
    private String sex;
    private String doctorName;

    public PatientDetails(String hospitalID, String patientName, int age, String sex, String doctorName) {
        this.hospitalID = hospitalID;
        this.patientName = patientName;
        this.age = age;
        this.sex = sex;
        this.doctorName = doctorName;
    }

    // Getters and setters
    public String getHospitalID() { return hospitalID; }
    public void setHospitalID(String hospitalID) { this.hospitalID = hospitalID; }

    public String getPatientName() { return patientName; }
    public void setPatientName(String patientName) { this.patientName = patientName; }

    public int getAge() { return age; }
    public void setAge(int age) { this.age = age; }

    public String getSex() { return sex; }
    public void setSex(String sex) { this.sex = sex; }

    public String getDoctorName() { return doctorName; }
    public void setDoctorName(String doctorName) { this.doctorName = doctorName; }
}
