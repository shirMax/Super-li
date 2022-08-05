package Backend.PersistenceLayer.SuppliersDAL;

import Backend.BusinessLayer.CallBacks.OrderDeliveryCallBack;
import Backend.BusinessLayer.Suppliers.*;
import Backend.DataLayer.DalController;
import org.omg.CORBA.PRIVATE_MEMBER;

import java.sql.*;
import java.sql.Date;
import java.text.MessageFormat;
import java.time.DayOfWeek;
import java.util.*;

public class OrderDAO extends DalController {
    private static String ORDERCONST_TABLE_NAME = "OrdersConst";
    private static String CONTACT_PHONE_COLUMN_NAME = "ContactPhone";
    private static String DAYS_COLUMN_NAME = "Days";
    private static String DELIVERY_ID_COLUMN_NAME = "DeliveryID";
    private static String NAME_COLUMN_NAME = "Name";

    private static String ORDERITEMS_TABLE_NAME = "OrderItems";
    private static String ITEM_ID_COLUMN_NAME = "ItemID";
    private static String AMOUNT_COLUMN_NAME = "Amount";
    private static String ITEM_PRICE_AFTER_DISCOUNT_NAME = "ItemPriceAfterDiscount";
    private static String ORDERCOLLECT_TABLE_NAME = "OrdersCollect";
    private static String ORDER_STATE_COLUMN_NAME = "OrderState";
    //columnNames
    private static String ORDER_ID_COLUMN_NAME = "OrderID";
    private static String SUPPLIER_ID_COLUMN_NAME = "SupplierID";
    private static String SUPPLY_DATE_COLUMN_NAME = "OrderDate";
    private static String ARRIVED_DATE_COLUMN_NAME = "ArrivedDate";
    private static String SUPER_ADDRESS_COLUMN_NAME = "SuperAddress";
    private static String PRICE_AFTER_DISCOUNT_COLUMN_NAME = "PriceAfterDiscount";

    public OrderDAO(){
    }

    protected Order ConvertReaderToOrderCollect(ResultSet reader){
        Order result = null;
        try {
            result = new OrderCollect(reader.getInt(SUPPLIER_ID_COLUMN_NAME), reader.getInt(ORDER_ID_COLUMN_NAME), reader.getDouble(PRICE_AFTER_DISCOUNT_COLUMN_NAME),Date.valueOf(reader.getString(3)), reader.getString(4) == null ? null : Date.valueOf(reader.getString(4)), reader.getString(5), OrderCollect.OrderState.valueOf(reader.getString(7)),reader.getInt(8),reader.getString(9));
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;

    }
    protected Order ConvertReaderToOrderConst(ResultSet reader){
        Order result = null;
        try {
            String days = reader.getString(DAYS_COLUMN_NAME);
            List<String> supplyConstantStrings;
            List<DayOfWeek> supplyConstantDays = new LinkedList<>();
            supplyConstantStrings = Arrays.asList(days.split(" "));
            for(String s : supplyConstantStrings) {
                s.trim();
                supplyConstantDays.add(DayOfWeek.valueOf(s));
            }
            result = new OrderConst(reader.getInt(SUPPLIER_ID_COLUMN_NAME), reader.getInt(ORDER_ID_COLUMN_NAME), reader.getDouble(PRICE_AFTER_DISCOUNT_COLUMN_NAME),supplyConstantDays, reader.getString(SUPER_ADDRESS_COLUMN_NAME));
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;

    }
    public Order getOrderCollect(int orderID) {
        return selectOrderCollect(orderID,ORDER_ID_COLUMN_NAME,ORDERCOLLECT_TABLE_NAME);
    }


    public void insertOrderCollect(int supplierID, int orderID, double price, Date supplyDate, Date arrivedDate, String superAddress, int deliveryID, String contactPhone, OrderCollect.OrderState orderState) {
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7},{8},{9}) VALUES(?,?,?,?,?,?,?,?,?)",
                ORDERCOLLECT_TABLE_NAME, ORDER_ID_COLUMN_NAME, SUPPLIER_ID_COLUMN_NAME,
                SUPPLY_DATE_COLUMN_NAME, ARRIVED_DATE_COLUMN_NAME , SUPER_ADDRESS_COLUMN_NAME,
                PRICE_AFTER_DISCOUNT_COLUMN_NAME,ORDER_STATE_COLUMN_NAME,DELIVERY_ID_COLUMN_NAME,CONTACT_PHONE_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderID);
            pstmt.setInt(2, supplierID);
            pstmt.setString(3, supplyDate.toString());
            pstmt.setString(4, arrivedDate == null? null : arrivedDate.toString());
            pstmt.setString(5, superAddress);
            pstmt.setDouble(6, price);
            pstmt.setString(7, orderState.toString());
            pstmt.setInt(8, deliveryID);
            pstmt.setString(9, contactPhone);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void insertOrderItems(int itemID, int orderID, int amount, double itemPriceAfterDiscount, String name) {
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}) VALUES(?,?,?,?,?)",
                ORDERITEMS_TABLE_NAME, ORDER_ID_COLUMN_NAME, ITEM_ID_COLUMN_NAME,
                AMOUNT_COLUMN_NAME, ITEM_PRICE_AFTER_DISCOUNT_NAME,NAME_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderID);
            pstmt.setInt(2, itemID);
            pstmt.setInt(3, amount);
            pstmt.setDouble(4, itemPriceAfterDiscount);
            pstmt.setString(5, name);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Order getOrderConst(int orderID) {
        return  selectOrderConst(orderID,ORDER_ID_COLUMN_NAME,ORDERCONST_TABLE_NAME);
    }
    public List<Order> selectallOrderConst(int supplierID){
        String sql = "SELECT * FROM " +ORDERCONST_TABLE_NAME+" WHERE "+SUPPLIER_ID_COLUMN_NAME+"="+supplierID;
        List<Order> allOrderConst = new LinkedList<>();
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                allOrderConst.add(ConvertReaderToOrderConst(rs));
            return allOrderConst;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }

    public List<Order> selectallOrderCollect(int supplierID){
        String sql = "SELECT * FROM " +ORDERCOLLECT_TABLE_NAME +" WHERE "+SUPPLIER_ID_COLUMN_NAME+"="+supplierID;
        List<Order> allOrderConst = new LinkedList<>();
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                allOrderConst.add(ConvertReaderToOrderCollect(rs));
            return allOrderConst;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }



    public List<Order> selectallOrderCollect(){
        String sql = "SELECT * FROM " +ORDERCOLLECT_TABLE_NAME;
        List<Order> allOrderConst = new LinkedList<>();
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                allOrderConst.add(ConvertReaderToOrderCollect(rs));
            return allOrderConst;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
            return null;
        }
    }


    public void insertOrderConst(int supplierID , int orderID, double price, String day,String superAddress){
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4},{5}) VALUES(?,?,?,?,?)",
                ORDERCONST_TABLE_NAME, ORDER_ID_COLUMN_NAME, SUPPLIER_ID_COLUMN_NAME,
                DAYS_COLUMN_NAME, SUPER_ADDRESS_COLUMN_NAME,PRICE_AFTER_DISCOUNT_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderID);
            pstmt.setInt(2, supplierID);
            pstmt.setString(3, day);
            pstmt.setString(4, superAddress);
            pstmt.setDouble(5, price);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public Order selectOrderConst(int id1, String columnName1, String tableName){
        String sql = "SELECT * FROM " +tableName+ " WHERE "+ columnName1+"="+id1;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next())
                return ConvertReaderToOrderConst(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public Order selectOrderCollect(int id1,String columnName1, String tableName){
        String sql = "SELECT * FROM " +tableName+ " WHERE "+ columnName1+"="+id1;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next())
                return ConvertReaderToOrderCollect(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public void removeOrderItems(int orderID, int itemID) {
        String sql = "DELETE FROM " +ORDERITEMS_TABLE_NAME+ " WHERE " +ORDER_ID_COLUMN_NAME+ "=?" +" AND "+ITEM_ID_COLUMN_NAME+"=?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, orderID);
            pstmt.setInt(2, itemID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void updateOrderItemPrice(double newPrice, int orderID, int itemID) {
        update(ORDERITEMS_TABLE_NAME,ITEM_PRICE_AFTER_DISCOUNT_NAME,newPrice,ORDER_ID_COLUMN_NAME,ITEM_ID_COLUMN_NAME,orderID,itemID);
    }

    public void updateOrderItemAmount(int newAmount, int orderID, int itemID) {
        update(ORDERITEMS_TABLE_NAME,AMOUNT_COLUMN_NAME,newAmount,ORDER_ID_COLUMN_NAME,ITEM_ID_COLUMN_NAME,orderID,itemID);
    }

    public OrderItem getOrderItem(int orderID, int itemID) {
        return selectOrderItem(orderID,itemID,ORDER_ID_COLUMN_NAME,ITEM_ID_COLUMN_NAME);
    }
    public OrderItem selectOrderItem(int id1, int id2 ,String columnName1,String columnName2){
        String sql = "SELECT * FROM " +ORDERITEMS_TABLE_NAME+ " WHERE "+ columnName1+"="+id1+" AND "+columnName2 +" = "+id2;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next())
                return ConvertReaderToOrderItem(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }
    public List<OrderItem> selectOrderItems(int id1 ,String columnName1){
        String sql = "SELECT * FROM " +ORDERITEMS_TABLE_NAME+ " WHERE "+ columnName1+"="+id1;
        List<OrderItem> orderItems = new LinkedList<>();
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while(rs.next())
                orderItems.add(ConvertReaderToOrderItem(rs));
            return orderItems;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private OrderItem ConvertReaderToOrderItem(ResultSet reader) {
        OrderItem result = null;
        try {
            result = new OrderItem(reader.getInt(1), reader.getInt(2),reader.getInt(3),reader.getDouble(4),reader.getString(5));
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return result;
    }


    public List<Order> getOrdersCollect(int supplierID) {
        return selectallOrderCollect(supplierID);
    }

    public List<Order> getOrdersConst(int supplierID) {
        return selectallOrderConst(supplierID);
    }

    public List<OrderItem> getOrderItems(int orderID) {
        return selectOrderItems(orderID,ORDER_ID_COLUMN_NAME);
    }

    public int loadOrderIDGenerator() {
        return Math.max(getLastOrderCollectID(ORDERCONST_TABLE_NAME),getLastOrderCollectID(ORDERCOLLECT_TABLE_NAME))+1;
    }

    private int getLastOrderCollectID(String tableName) {
        String sql = "SELECT "+ORDER_ID_COLUMN_NAME+" FROM " +tableName+ " ORDER BY  "+ ORDER_ID_COLUMN_NAME +" DESC LIMIT 1";
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next())
                return rs.getInt(ORDER_ID_COLUMN_NAME);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    public List<Order> getNotArrivedOrders(int supplierID) {
        List<Order> allOrders =  selectallOrderCollect(supplierID);
        List<Order> notArrived = new LinkedList<>();
        for(Order o : allOrders){
            if(((OrderCollect)o).checkIfNotArrived())
                notArrived.add(o);
        }
        return notArrived;
    }


//    public void updateArrivedDate(int supplierID, int orderID, OrderCollect.OrderState orderState) {
//        update(ORDERCOLLECT_TABLE_NAME,ORDER_STATE_COLUMN_NAME, ARRIVED_DATE_COLUMN_NAME,orderState.toString(),new java.sql.Date(Calendar.getInstance().getTime().getTime()),ORDER_ID_COLUMN_NAME,SUPPLIER_ID_COLUMN_NAME,orderID,supplierID);
//    }
    public void removeOrdersForSupplierConst(int supplierID) throws Exception {
        remove(supplierID,SUPPLIER_ID_COLUMN_NAME,ORDERCONST_TABLE_NAME);
    }
    public void removeOrdersForSupplierCollect(int supplierID) throws Exception {
        remove(supplierID,SUPPLIER_ID_COLUMN_NAME,ORDERCOLLECT_TABLE_NAME);
    }
    public void removeOrderItem( int orderID) throws Exception {
        remove(orderID,ORDER_ID_COLUMN_NAME,ORDERITEMS_TABLE_NAME);
    }

    public int removeWaitingOrder(int orderID) throws Exception {
        Order order = getOrderCollect(orderID);
        validateOrder(order);
        if(((OrderCollect)order).getOrderState() == OrderCollect.OrderState.Done)
            throw new Exception("order arrived");
        remove(orderID,ORDER_ID_COLUMN_NAME,ORDERCOLLECT_TABLE_NAME);
        remove(orderID,ORDER_ID_COLUMN_NAME,ORDERITEMS_TABLE_NAME);
        return ((OrderCollect)order).getDeliveryID();
    }
    public int removeOrder(int orderID) throws Exception {
        Order order = getOrderCollect(orderID);
        validateOrder(order);
        remove(orderID,ORDER_ID_COLUMN_NAME,ORDERCOLLECT_TABLE_NAME);
        remove(orderID,ORDER_ID_COLUMN_NAME,ORDERITEMS_TABLE_NAME);
        return ((OrderCollect)order).getDeliveryID();
    }

    private void validateOrder(Order order) throws Exception {
        if(order == null)
            throw new Exception("order does not exist!");
    }

    public void updateOrderCollect(int orderID, Date deliveryDate, int deliveryID, OrderCollect.OrderState orderState) {
        update(ORDERCOLLECT_TABLE_NAME, ARRIVED_DATE_COLUMN_NAME, DELIVERY_ID_COLUMN_NAME, ORDER_STATE_COLUMN_NAME, deliveryDate.toString(), deliveryID, orderState.toString(), ORDER_ID_COLUMN_NAME, orderID);
    }
    public void updateOrderCollect(int orderID, Date deliveryDate, OrderCollect.OrderState orderState) {
        update(ORDERCOLLECT_TABLE_NAME, ARRIVED_DATE_COLUMN_NAME, ORDER_STATE_COLUMN_NAME, deliveryDate.toString(), orderState.toString(), ORDER_ID_COLUMN_NAME, orderID);
    }
    public void updateOrderCollectPrice(int orderID, double price) {
        update(ORDERCOLLECT_TABLE_NAME, PRICE_AFTER_DISCOUNT_COLUMN_NAME, price, ORDER_ID_COLUMN_NAME, orderID);
    }
    public void updateOrderConstPrice(int orderID, double price) {
        update(ORDERCONST_TABLE_NAME, PRICE_AFTER_DISCOUNT_COLUMN_NAME, price, ORDER_ID_COLUMN_NAME, orderID);
    }

    public void updateOrderProducts(int orderID, int itemID, int amount, double price) {
        OrderItem orderItem = getOrderItem(orderID,itemID);
        if (orderItem!= null)
            update(ORDERITEMS_TABLE_NAME, AMOUNT_COLUMN_NAME, ITEM_PRICE_AFTER_DISCOUNT_NAME, amount, price, ORDER_ID_COLUMN_NAME, ITEM_ID_COLUMN_NAME, orderID, itemID);
    }

    public void setContactPhone(String oldPhone, String phone) {
        update(ORDERCOLLECT_TABLE_NAME, CONTACT_PHONE_COLUMN_NAME, phone, CONTACT_PHONE_COLUMN_NAME, oldPhone);
    }
}

