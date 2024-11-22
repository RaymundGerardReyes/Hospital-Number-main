// File: Systems/HealthCareFacilities/AdmittingRoom.java
package Systems.HealthCareFacilities;

public class AdmittingRoom extends Facility {
    private boolean hasInsuranceVerification;
    private boolean hasBedManagement;

    public AdmittingRoom(String name, String address, String contact, boolean hasInsuranceVerification, boolean hasBedManagement) {
        super(name, address, contact, "Admitting Room");
        this.hasInsuranceVerification = hasInsuranceVerification;
        this.hasBedManagement = hasBedManagement;
    }

    public boolean hasInsuranceVerification() {
        return hasInsuranceVerification;
    }

    public void setHasInsuranceVerification(boolean hasInsuranceVerification) {
        this.hasInsuranceVerification = hasInsuranceVerification;
    }

    public boolean hasBedManagement() {
        return hasBedManagement;
    }

    public void setHasBedManagement(boolean hasBedManagement) {
        this.hasBedManagement = hasBedManagement;
    }

    @Override
    public String getDetails() {
        return String.format("%s\nInsurance Verification: %s\nBed Management Integration: %s\nPatient Registration System: Yes",
                super.getDetails(), hasInsuranceVerification ? "Yes" : "No", hasBedManagement ? "Yes" : "No");
    }
}
