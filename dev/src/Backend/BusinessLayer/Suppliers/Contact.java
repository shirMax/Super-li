package Backend.BusinessLayer.Suppliers;

public class Contact {
    private int contactID;
    private String name;
    private String phone;

    public Contact(int contactID, String name, String phone) {
        this.contactID = contactID;
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getContactID() {
        return contactID;
    }

}

