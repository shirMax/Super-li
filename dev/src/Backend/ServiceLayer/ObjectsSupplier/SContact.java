package Backend.ServiceLayer.ObjectsSupplier;

public class SContact {
    private int contactID;
    private String name;
    private String phone;

    public SContact(int contactID, String name, String phone) {
        this.contactID = contactID;
        this.name = name;
        this.phone = phone;
    }

    public int getContactID() {
        return contactID;
    }

    public String getName() {
        return name;
    }

    public String getPhone() {
        return phone;
    }
}
