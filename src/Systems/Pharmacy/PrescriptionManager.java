package Systems.Pharmacy;

public class PrescriptionManager {
    public void receiveEPrescription(String prescriptionData) {
        // Implementation for receiving e-prescriptions
    }

    public void fillPrescription(String prescriptionId) {
        System.out.println("Filling prescription: " + prescriptionId);
    }

    public void manageRefills(String prescriptionId) {
        System.out.println("Managing refills for prescription: " + prescriptionId);
    }

    public void notifyPatientForRefill(String patientId, String prescriptionId) {
        // Implementation for notifying patients about refills
    }
}
