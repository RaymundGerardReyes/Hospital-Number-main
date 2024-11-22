package Systems.PatientManagement;

public class Patient {
    private String hospitalId, lastName, firstName, middleName, birthday, sex, phone, email, address, city, state, zip;
    private String emergencyContactName, emergencyContactRelationship, emergencyContactPhone, insuranceProvider, policyNumber, notes;
    private int age;

    public Patient(String hospitalId, String lastName, String firstName, String middleName, String birthday, int age,
                   String sex, String phone, String email, String address, String city, String state, String zip,
                   String emergencyContactName, String emergencyContactRelationship, String emergencyContactPhone,
                   String insuranceProvider, String policyNumber, String notes) {
        this.hospitalId = hospitalId;
        this.lastName = lastName;
        this.firstName = firstName;
        this.middleName = middleName;
        this.birthday = birthday;
        this.age = age;
        this.sex = sex;
        this.phone = phone;
        this.email = email;
        this.address = address;
        this.city = city;
        this.state = state;
        this.zip = zip;
        this.emergencyContactName = emergencyContactName;
        this.emergencyContactRelationship = emergencyContactRelationship;
        this.emergencyContactPhone = emergencyContactPhone;
        this.insuranceProvider = insuranceProvider;
        this.policyNumber = policyNumber;
        this.notes = notes;
    }

    // Getters
    public String getHospitalId() { return hospitalId; }
    public String getLastName() { return lastName; }
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getBirthday() { return birthday; }
    public int getAge() { return age; }
    public String getSex() { return sex; }
    public String getPhone() { return phone; }
    public String getEmail() { return email; }
    public String getAddress() { return address; }
    public String getCity() { return city; }
    public String getState() { return state; }
    public String getZip() { return zip; }
    public String getEmergencyContactName() { return emergencyContactName; }
    public String getEmergencyContactRelationship() { return emergencyContactRelationship; }
    public String getEmergencyContactPhone() { return emergencyContactPhone; }
    public String getInsuranceProvider() { return insuranceProvider; }
    public String getPolicyNumber() { return policyNumber; }
    public String getNotes() { return notes; }

    // Setters (add these for better encapsulation)
    public void setHospitalId(String hospitalId) { this.hospitalId = hospitalId; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }
    public void setBirthday(String birthday) { this.birthday = birthday; }
    public void setAge(int age) { this.age = age; }
    public void setSex(String sex) { this.sex = sex; }
    public void setPhone(String phone) { this.phone = phone; }
    public void setEmail(String email) { this.email = email; }
    public void setAddress(String address) { this.address = address; }
    public void setCity(String city) { this.city = city; }
    public void setState(String state) { this.state = state; }
    public void setZip(String zip) { this.zip = zip; }
    public void setEmergencyContactName(String emergencyContactName) { this.emergencyContactName = emergencyContactName; }
    public void setEmergencyContactRelationship(String emergencyContactRelationship) { this.emergencyContactRelationship = emergencyContactRelationship; }
    public void setEmergencyContactPhone(String emergencyContactPhone) { this.emergencyContactPhone = emergencyContactPhone; }
    public void setInsuranceProvider(String insuranceProvider) { this.insuranceProvider = insuranceProvider; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }
    public void setNotes(String notes) { this.notes = notes; }
}