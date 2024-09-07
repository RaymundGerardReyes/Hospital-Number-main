package Systems.HospitalID;

public class HospitalIDGenerator {
    public String generateHospitalID() {
        return "ID_" + System.currentTimeMillis(); // Simple ID generation based on current time
    }
}
