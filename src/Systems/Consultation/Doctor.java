package Systems.Consultation;

import javax.swing.JPanel;

public class Doctor {
    private String name;
    private String specialist;
    private String room;
    private String patientConsult;
    private String availability;
    private int maxPatientsPerDay;
    private int maxPatientsPerSlot;
    private int slotDuration;

    public Doctor(String name, String specialist, String room, String patientConsult,
                  String availability, int maxPatientsPerDay, int maxPatientsPerSlot, int slotDuration) {
        this.name = name;
        this.specialist = specialist;
        this.room = room;
        this.patientConsult = patientConsult;
        this.availability = availability;
        this.maxPatientsPerDay = maxPatientsPerDay;
        this.maxPatientsPerSlot = maxPatientsPerSlot;
        this.slotDuration = slotDuration;
    }

    // Getters
    public String getName() { return name; }
    public String getSpecialist() { return specialist; }
    public String getRoom() { return room; }
    public String getPatientConsult() { return patientConsult; }
    public String getAvailability() { return availability; }
    public int getMaxPatientsPerDay() { return maxPatientsPerDay; }
    public int getMaxPatientsPerSlot() { return maxPatientsPerSlot; }
    public int getSlotDuration() { return slotDuration; }

    // Setters
    public void setName(String name) { this.name = name; }
    public void setSpecialist(String specialist) { this.specialist = specialist; }
    public void setRoom(String room) { this.room = room; }
    public void setPatientConsult(String patientConsult) { this.patientConsult = patientConsult; }
    public void setAvailability(String availability) { this.availability = availability; }
    public void setMaxPatientsPerDay(int maxPatientsPerDay) { this.maxPatientsPerDay = maxPatientsPerDay; }
    public void setMaxPatientsPerSlot(int maxPatientsPerSlot) { this.maxPatientsPerSlot = maxPatientsPerSlot; }
    public void setSlotDuration(int slotDuration) { this.slotDuration = slotDuration; }

    public JPanel createAvailabilityPanel() {
        JPanel panel = new JPanel();
        // Add components to the panel based on the doctor's availability information
        // This method can be implemented later with the specific UI components needed
        return panel;
    }

    @Override
    public String toString() {
        return "Doctor{" +
               "name='" + name + '\'' +
               ", specialist='" + specialist + '\'' +
               ", room='" + room + '\'' +
               ", patientConsult='" + patientConsult + '\'' +
               ", availability='" + availability + '\'' +
               ", maxPatientsPerDay=" + maxPatientsPerDay +
               ", maxPatientsPerSlot=" + maxPatientsPerSlot +
               ", slotDuration=" + slotDuration +
               '}';
    }
}