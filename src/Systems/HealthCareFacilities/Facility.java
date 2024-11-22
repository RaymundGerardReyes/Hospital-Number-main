// File: Systems/HealthCareFacilities/Facility.java
package Systems.HealthCareFacilities;

public abstract class Facility {
    private String name;
    private String address;
    private String contact;
    private String type;

    public Facility(String name, String address, String contact, String type) {
        this.name = name;
        this.address = address;
        this.contact = contact;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getType() {
        return type;
    }

    public String getDetails() {
        return String.format("Name: %s\nType: %s\nAddress: %s\nContact: %s", name, type, address, contact);
    }
}
