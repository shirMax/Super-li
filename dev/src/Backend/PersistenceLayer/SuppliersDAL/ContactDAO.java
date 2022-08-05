package Backend.PersistenceLayer.SuppliersDAL;

import Backend.BusinessLayer.Suppliers.Contact;
import Backend.DataLayer.DalController;

import java.sql.*;
import java.text.MessageFormat;
import java.util.LinkedList;
import java.util.List;

public class ContactDAO extends DalController {
    private static String CONTACTS_TABLE_NAME = "Contacts";
    private static String SUPPLIER_ID_COLUMN_NAME = "SupplierID";
    private static String CONTACT_ID_COLUMN_NAME = "ContactID";
    private static String NAME_COLUMN_NAME = "Name";
    private static String PHONE_COLUMN_NAME = "Phone";
    public ContactDAO() {
    }


    public Contact getContact(int supplierID, int contactID) {
        return selectContact(supplierID, contactID, SUPPLIER_ID_COLUMN_NAME, CONTACT_ID_COLUMN_NAME, CONTACTS_TABLE_NAME);
    }


    public void insert(int supplierID, int contactID, String name, String phone) throws Exception {
        validateContactExists(supplierID,contactID);
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}) VALUES(?,?,?,?)",
                CONTACTS_TABLE_NAME,SUPPLIER_ID_COLUMN_NAME, CONTACT_ID_COLUMN_NAME, NAME_COLUMN_NAME,
                PHONE_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierID);
            pstmt.setInt(2, contactID);
            pstmt.setString(3, name);
            pstmt.setString(4, phone);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void validateContactExists(int supplierID, int contactID) throws Exception {
        Contact contact = getContact(supplierID, contactID);
        if (contact != null)
             throw new Exception("the contact is already exists");
    }

    public void removeContact(int supplierID, int contactID) throws Exception {
        validateContactDoesntExists(supplierID,contactID);
        remove(supplierID, contactID, SUPPLIER_ID_COLUMN_NAME, CONTACT_ID_COLUMN_NAME, CONTACTS_TABLE_NAME);
    }

    private void validateContactDoesntExists(int supplierID, int contactID) throws Exception {
        Contact contact = getContact(supplierID, contactID);
        if (contact == null)
            throw new Exception("contact does not exist");
    }

    public void removeContact(int supplierID) throws Exception {
        remove(supplierID, SUPPLIER_ID_COLUMN_NAME, CONTACTS_TABLE_NAME);
    }

    public Contact selectContact(int id1,int id2, String columnName1,String columnName2, String tableName){
        String sql = "SELECT * FROM " +tableName+ " WHERE "+ columnName1+"="+id1 + " AND "+columnName2 +" = "+id2;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next())
                return ConvertReaderToContact(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Contact ConvertReaderToContact(ResultSet reader) {
        Contact result = null;
        try {
            result = new Contact(reader.getInt(2), reader.getString(3), reader.getString(4));
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public String setContactPhone(int supplierID, int id, String phone) throws Exception {
        Contact contact = checkIfContactExists(supplierID,id);
        String oldPhone = contact.getPhone();
        update(CONTACTS_TABLE_NAME, PHONE_COLUMN_NAME, phone, CONTACT_ID_COLUMN_NAME, SUPPLIER_ID_COLUMN_NAME, id, supplierID);
        return oldPhone;
    }


    public void setContactName(int supplierID, int id, String name) throws Exception {
        checkIfContactExists(supplierID,id);
        update(CONTACTS_TABLE_NAME, "Name", name, "ContactID", "SupplierID", id, supplierID);
    }
    public Contact checkIfContactExists(int supplierID, int contactID) throws Exception {
        Contact contact = getContact(supplierID, contactID);
        if (contact == null)
            throw new Exception("contact does not exist");
        return contact;
    }


    public List<Contact> getContacts(int supplierID) {
        String sql = "SELECT * FROM " +CONTACTS_TABLE_NAME+ " WHERE "+ SUPPLIER_ID_COLUMN_NAME+"="+supplierID;
        List<Contact> contacts = new LinkedList<>();
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next())
                contacts.add(ConvertReaderToContact(rs));
            return contacts;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
}