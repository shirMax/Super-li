package Frontend.PresentationLayer.Model;

public class MContact {
    private int contactID;
    private String name;
    private String phone;

    public MContact(int contactID, String name, String phone) {
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

    public String editContactsName(String name,int supplierID) throws Exception {
        String editContactName = Controller.getInstance().setContactname(supplierID,contactID,name);
        this.name = name;
        return editContactName;
    }

    public String editingContactsPhone(int supplierID,String phone) throws Exception {
        String editingContactsPhone = Controller.getInstance().setContactPhone(supplierID,contactID,phone);
        this.phone = phone;
        return editingContactsPhone;
    }

    @Override
    public String toString() {
        return "Contact: " + "contactID=" + contactID + ", name='" + name + '\'' + ", phone='" + phone + '\'';
    }
}
