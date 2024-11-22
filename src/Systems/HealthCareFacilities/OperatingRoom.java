// File: Systems/HealthCareFacilities/OperatingRoom.java
package Systems.HealthCareFacilities;

public class OperatingRoom extends Facility {
    private boolean hasRoboticSurgery;
    private int numberOfOperatingTables;

    public OperatingRoom(String name, String address, String contact, boolean hasRoboticSurgery, int numberOfOperatingTables) {
        super(name, address, contact, "Operating Room");
        this.hasRoboticSurgery = hasRoboticSurgery;
        this.numberOfOperatingTables = numberOfOperatingTables;
    }

    public boolean hasRoboticSurgery() {
        return hasRoboticSurgery;
    }

    public void setHasRoboticSurgery(boolean hasRoboticSurgery) {
        this.hasRoboticSurgery = hasRoboticSurgery;
    }

    public int getNumberOfOperatingTables() {
        return numberOfOperatingTables;
    }

    public void setNumberOfOperatingTables(int numberOfOperatingTables) {
        this.numberOfOperatingTables = numberOfOperatingTables;
    }

    @Override
    public String getDetails() {
        return String.format("%s\nRobotic Surgery: %s\nNumber of Operating Tables: %d",
                super.getDetails(), hasRoboticSurgery ? "Yes" : "No", numberOfOperatingTables);
    }
}
