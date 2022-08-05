
package Backend.PersistenceLayer.SuppliersDAL;

import Backend.BusinessLayer.Branch;
import Backend.BusinessLayer.Deliveries.Enums.Area_Enum;
import Backend.BusinessLayer.Suppliers.*;
import Backend.DataLayer.DalController;
import Backend.PersistenceLayer.BranchesDAO;

import java.sql.*;
import java.text.MessageFormat;
import java.util.*;

public class SupplierDAO extends DalController {
    private static String SUPPLIERS_TABLE_NAME = "Suppliers";
    private static String SUPPLIER_ID_COLUMN_NAME = "SupplierID";
    private static String SUPPLIER_NAME_COLUMN_NAME = "SupplierName";
    private static String SUPPLIER_ADDRESS_COLUMN_NAME = "SupplierAddress";
    private static String BANK_ACCOUNT_COLUMN_NAME = "BankAccount";
    private static String PAYMENT_COLUMN_NAME = "Payment";
    private static String TYPE_DELIVERY_COLUMN_NAME = "TypeDelivery";
    private static String AREA_COLUMN_NAME = "Area";

    private ContactDAO contactDAO;
    private ItemDAO itemDAO;
    private OrderDAO orderDAO;
    private BranchesDAO branchesDAO;


    public SupplierDAO(ContactDAO contactDAO,ItemDAO itemDAO,OrderDAO orderDAO, BranchesDAO branchesDAO){
        this.contactDAO = contactDAO;
        this.itemDAO = itemDAO;
        this.orderDAO = orderDAO;
        this.branchesDAO = branchesDAO;
    }

    protected Supplier ConvertReaderToSupplier(ResultSet reader) {
        Supplier result = null;
        try {
            result = new Supplier(reader.getInt(SUPPLIER_ID_COLUMN_NAME), reader.getString(SUPPLIER_NAME_COLUMN_NAME), reader.getString(SUPPLIER_ADDRESS_COLUMN_NAME),reader.getInt(BANK_ACCOUNT_COLUMN_NAME),Supplier.payment.valueOf(reader.getString(PAYMENT_COLUMN_NAME)),Supplier.typeDelivery.valueOf(reader.getString(TYPE_DELIVERY_COLUMN_NAME)), Area_Enum.valueOf(reader.getString(AREA_COLUMN_NAME)),orderDAO,itemDAO,contactDAO, branchesDAO);
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }


    public void insertSupplier(int supplierID, int bankAccount, String payment, String typeDelivery,String SupplierName,String supplierAddress, String area) throws Exception {
        insertSupplierValidateExists(supplierID);
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4},{5},{6},{7}) VALUES(?,?,?,?,?,?,?)",
                SUPPLIERS_TABLE_NAME, SUPPLIER_ID_COLUMN_NAME, BANK_ACCOUNT_COLUMN_NAME,
                PAYMENT_COLUMN_NAME, TYPE_DELIVERY_COLUMN_NAME,SUPPLIER_NAME_COLUMN_NAME,SUPPLIER_ADDRESS_COLUMN_NAME,AREA_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierID);
            pstmt.setInt(2, bankAccount);
            pstmt.setString(3, payment);
            pstmt.setString(4, typeDelivery);
            pstmt.setString(5, SupplierName);
            pstmt.setString(6, supplierAddress);
            pstmt.setString(7, area);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void insertSupplierValidateExists(int supplierID) throws Exception {
        Supplier supplier = getSupplier(supplierID);
        if(supplier != null)
            throw new Exception("supplier already exists");
    }

    public void removeSupplier(int supplierID) throws Exception {
        removeSupplierValidateExists(supplierID);
        removeSuppliersOrders(supplierID);
        remove(supplierID,SUPPLIER_ID_COLUMN_NAME,SUPPLIERS_TABLE_NAME);
        contactDAO.removeContact(supplierID);
        itemDAO.removeItem(supplierID);
        itemDAO.removeItemDiscount(supplierID);
    }

    private void removeSupplierValidateExists(int supplierID) throws Exception {
        Supplier supplier = getSupplier(supplierID);
        if (supplier == null) {
            throw new Exception("supplier doesn't exist");
        }
    }

    private void removeSuppliersOrders(int supplierID) throws Exception {
        List<Order> orders = getSupplier(supplierID).getOrdersCollect();
        for(Order o: orders)
            orderDAO.removeOrderItem(o.getOrderID());
        orderDAO.removeOrdersForSupplierConst(supplierID);
        orderDAO.removeOrdersForSupplierCollect(supplierID);
    }

    public Supplier getSupplier(int supplierID) {
        return selectSupplier(supplierID,SUPPLIER_ID_COLUMN_NAME);
    }

    public Supplier selectSupplier(int id, String columnName) {
        String sql = "SELECT * FROM " + SUPPLIERS_TABLE_NAME + " WHERE " + columnName + "=" + id;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next())
                return ConvertReaderToSupplier(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    public Item addItem(int supplierID, int itemID, int catalogID, double price, String name) throws Exception {
        Supplier supplier = getSupplier(supplierID);
        if(supplier != null)
            return supplier.addItem(itemID,catalogID,name,price);
        else throw new Exception("supplier doesn't exist");
    }


    public List<Supplier> selectSuppliers(){
        List<Supplier> objects = new LinkedList<>();
        String sql = "SELECT * FROM " +SUPPLIERS_TABLE_NAME;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next())
                objects.add(ConvertReaderToSupplier(rs));
            return objects;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

}
