package Backend.PersistenceLayer.SuppliersDAL;


import Backend.BusinessLayer.Suppliers.Contract;
import Backend.BusinessLayer.Suppliers.Item;
import Backend.BusinessLayer.Tools.Pair;
import Backend.DataLayer.DalController;

import java.sql.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class ItemDAO extends DalController {
    private static String SUPPLIER_ID_COLUMN_NAME = "SupplierID";
    private static String ITEM_ID_COLUMN_NAME = "ItemID";
    private static String AMOUNT_COLUMN_NAME = "Amount";
    private static String NAME_COLUMN_NAME = "Name";
    private static String PRICE_COLUMN_NAME = "Price";
    private static String DISCOUNT_COLUMN_NAME = "Discount";
    private static String CATALOG_ID_COLUMN_NAME = "CatalogID";

    private static String ITEMDISCOUNT_TABLE_NAME = "ItemsDiscount";
    private static String ITEMS_TABLE_NAME = "Items";

    public ItemDAO() {
    }


    public void insert(int supplierID,int itemID, int catalogID, String name, double price){
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4},{5}) VALUES(?,?,?,?,?)",
                ITEMS_TABLE_NAME,SUPPLIER_ID_COLUMN_NAME, ITEM_ID_COLUMN_NAME, CATALOG_ID_COLUMN_NAME,NAME_COLUMN_NAME,PRICE_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierID);
            pstmt.setInt(2, itemID);
            pstmt.setInt(3, catalogID);
            pstmt.setString(4, name);
            pstmt.setDouble(5, price);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void insert(int supplierID,int itemID, int amount, int discount) throws Exception {
        validateDiscountExists(supplierID,itemID,amount);
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}) VALUES(?,?,?,?)",
                ITEMDISCOUNT_TABLE_NAME,SUPPLIER_ID_COLUMN_NAME, ITEM_ID_COLUMN_NAME, AMOUNT_COLUMN_NAME,DISCOUNT_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, supplierID);
            pstmt.setInt(2, itemID);
            pstmt.setInt(3, amount);
            pstmt.setInt(4, discount);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void validateDiscountExists(int supplierID, int itemID, int amount) throws Exception {
        Integer disc = getDiscount(supplierID,itemID,amount);
        if(disc != null)
           throw new Exception("discount already exists");
    }

    public void removeItem(int supplierID, int itemID) throws Exception {
        remove(supplierID, itemID, SUPPLIER_ID_COLUMN_NAME, ITEM_ID_COLUMN_NAME, ITEMS_TABLE_NAME);
    }
    public void removeItem(int supplierID) throws Exception {
        remove(supplierID, SUPPLIER_ID_COLUMN_NAME, ITEMS_TABLE_NAME);
    }

    public Item checkIfItemInCatalog(int supplierID,int itemID) throws Exception {
        Item item =  getItemFromCatalog(supplierID,itemID); // throws exception
        if(item == null)
            throw new Exception("the item "+itemID+ " is not in the contract");
        return item;
    }

    public Item getItemFromCatalog(int supplierID,int itemID) {
        return selectItem(supplierID,itemID,"SupplierID","ItemID",ITEMS_TABLE_NAME);
    }
    public Item selectItem(int id1,int id2, String columnName1,String columnName2, String tableName){
        String sql = "SELECT * FROM " +tableName+ " WHERE "+ columnName1+"="+id1 + " AND "+columnName2 +" = "+id2;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next())
                return ConvertReaderToItem(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    public Integer getDiscount(int supplierID, int itemID, int amount) {
        String sql = "SELECT * FROM " +ITEMDISCOUNT_TABLE_NAME+ " WHERE "+ SUPPLIER_ID_COLUMN_NAME+"="+supplierID+" AND "+ITEM_ID_COLUMN_NAME +" = "+itemID +" AND "+AMOUNT_COLUMN_NAME+"="+amount;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement())
        {
            ResultSet rs = stmt.executeQuery(sql);
            if(!rs.next())
                return null;
            return rs.getInt(4);
        } catch (Exception e) {
            throw new IllegalArgumentException(String.format("enable to get item discount supplierID: %s ItemID: %d ", supplierID,itemID ));
        }
    }

    public List<Item> getItemsForSupplier(int supplierID) {
        return selectAllItemsForSupplier(supplierID,"SupplierID");
    }
    public List<Item> selectAllItemsForSupplier(int columnValue,String columnName){
        List<Item> objects = new LinkedList<>();
        String sql = "SELECT * FROM " +ITEMS_TABLE_NAME+" WHERE "+columnName +"="+columnValue;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next())
                objects.add(ConvertReaderToItem(rs));
            return objects;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    private Item ConvertReaderToItem(ResultSet reader) {
        Item result = null;
        try {
            result = new Item(reader.getInt(2), reader.getInt(3), reader.getString(4),reader.getDouble(5));
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }

    public HashMap<Integer, Integer> getItemToAmountAndDiscount(int supplierID, int itemID) {
        return selectAmountAndDiscountOfItem(supplierID,itemID);
    }

    private HashMap<Integer, Integer> selectAmountAndDiscountOfItem(int supplierID, int itemID) {
        String sql = "SELECT * FROM " +ITEMDISCOUNT_TABLE_NAME+ " WHERE "+ SUPPLIER_ID_COLUMN_NAME+"="+supplierID+" AND "+ITEM_ID_COLUMN_NAME +"="+itemID;
        HashMap<Integer,Integer> amountAndDiscount = new HashMap<>();
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                amountAndDiscount.put(rs.getInt(3),rs.getInt(4));
            return amountAndDiscount;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }
    public Contract getContract(int supplierID) {
        Contract contract = new Contract();
        List<Item> items = getItemsForSupplier(supplierID);
        for(Item item : items) {
            HashMap<Integer, Integer> amountAndDiscount = selectAmountAndDiscountOfItem(supplierID, item.getItemID());
            contract.addItem(item);
            contract.addDiscount(item.getItemID(),amountAndDiscount);
        }
        return contract;
    }
    public List<Pair<Integer,String>> getItems() {
        List<Pair<Integer,String>> objects = new LinkedList<>();
        String sql = "SELECT DISTINCT "+ITEM_ID_COLUMN_NAME+","+NAME_COLUMN_NAME+"  FROM " +ITEMS_TABLE_NAME;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next())
                objects.add(new Pair<>(rs.getInt(ITEM_ID_COLUMN_NAME),rs.getString(NAME_COLUMN_NAME)));
            return objects;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }

    }

    public void removeItemDiscount(int supplierID) throws Exception {
        remove(supplierID,SUPPLIER_ID_COLUMN_NAME,ITEMDISCOUNT_TABLE_NAME);
    }
}
