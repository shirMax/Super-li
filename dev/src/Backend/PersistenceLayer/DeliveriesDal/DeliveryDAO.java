package Backend.PersistenceLayer.DeliveriesDal;

import Backend.BusinessLayer.Deliveries.*;
import Backend.BusinessLayer.Deliveries.Driver;
import Backend.BusinessLayer.Deliveries.Enums.DeliveryMode_Enum;
import Backend.BusinessLayer.Tools.DateConvertor;
import Backend.PersistenceLayer.DalController;

import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class DeliveryDAO extends DalController {

    private HashMap<Integer, Delivery> deliveryMap;
    final String DELIVERY_TABLE_NAME = "Deliveries";
    final String DELIVERY_ID_COLUMN_NAME = "DeliveryID";
    final String DEPARTURE_DATE_COLUMN_NAME = "DepartureDate";
    final String DELIVERY_MODE_COLUMN_NAME = "DeliveryMode";
    final String TRUCK_ID_COLUMN_NAME = "TruckID";
    final String DRIVER_ID_COLUMN_NAME = "DriverID";
    final String ORDERID_ID_COLUMN_NAME = "OrderID";

    final String DELIVERY_SITE_DOC_TABLE_NAME = "SiteDocDeliveries";
    final String SITE_DOC_ID_DOC_DELIVERY_COLUMN_NAME = "SiteID";
    final String DELIVERY_ID_DOC_DELIVERY_COLUMN_NAME = "DeliveryID";
    private TruckDAO truckDAO;
    private SiteDocDAO siteDocDAO;
    private ProductsSiteDocDAO productsSiteDocDAO;
    private boolean allLoaded;

    public DeliveryDAO(TruckDAO truckDAO, SiteDocDAO siteDocDAO, ProductsSiteDocDAO productsSiteDocDAO) {
        this.deliveryMap = new HashMap<>();
        this.truckDAO = truckDAO;
        this.siteDocDAO = siteDocDAO;
        this.productsSiteDocDAO = productsSiteDocDAO;
        this.allLoaded = false;
    }

    public Delivery addDelivery(String departueDate,String deliveryMode,int truckID ,int driverID, int orderID) throws Exception {
        Delivery delivery = selectDeliveryByFields(departueDate, truckID, driverID);
        if(delivery == null) {
            int maxDelivery = maxDeliveryID();
            maxDelivery++;
            delivery = selectDelivery(maxDelivery, DELIVERY_ID_COLUMN_NAME, DELIVERY_TABLE_NAME);

            if (delivery == null) {
                try {
                    insert(maxDelivery, departueDate, deliveryMode, truckID, driverID, orderID);
                    addDeliveryToIDMap(maxDelivery, departueDate, deliveryMode, truckID, driverID, orderID);
                    delivery = deliveryMap.get(maxDelivery);
                } catch (Exception e) {
                    throw new Exception(e.getMessage());
                }
            }
        }
        return delivery;
    }
    public Delivery addPendingDelivery(int orderID, String date) throws Exception {
        Delivery delivery;
        int maxDelivery = maxDeliveryID();
        maxDelivery++;
        delivery = selectDelivery(maxDelivery, DELIVERY_ID_COLUMN_NAME, DELIVERY_TABLE_NAME);

        if (delivery == null) {
            try {
                insert(maxDelivery, date, orderID);
                addDeliveryToIDMap(maxDelivery, date, orderID);
                delivery = deliveryMap.get(maxDelivery);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }

        //addDatesToPendingDelivery(delivery.getId(), date);

        return delivery;
    }

/*
    public Delivery addPendingDelivery(int orderID, String date) throws Exception {
        Delivery delivery;
        int maxDelivery = maxDeliveryID();
        maxDelivery++;
        delivery = selectDelivery(maxDelivery, DELIVERY_ID_COLUMN_NAME, DELIVERY_TABLE_NAME);

        if (delivery == null) {
            try {
                insert(maxDelivery, orderID);
                addDeliveryToIDMap(maxDelivery, date, orderID);
                delivery = deliveryMap.get(maxDelivery);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }

        //addDatesToPendingDelivery(delivery.getId(), myDates);

        return delivery;
    }
*/

/*    private void addDatesToPendingDelivery(int id, List<String> myDates) throws Exception {
        for(String date : myDates)
            pendingDeliveriesDAO.addDateToPending(id, date);
    }*/

    public void addSiteDocToDelivery(int deliveryID, int siteDocID) throws Exception {
        Delivery delivery = getDelivery(deliveryID);
        if (delivery == null) {
            throw new Exception("no such delivery with this id: " + deliveryID);
        }
        SiteDocument siteDocument = siteDocDAO.getSiteDoc(siteDocID);
        if (siteDocument == null) {
            throw new Exception("no such site document with this id: " + siteDocID);
        }
        if(delivery.getSite(siteDocID) == null){
            try {
                insertToSiteDocDelivery(deliveryID, siteDocID);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
    }

    private void insertToSiteDocDelivery(int deliveryID, int siteDocID) {
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}) VALUES(?,?)",
                DELIVERY_SITE_DOC_TABLE_NAME, DELIVERY_ID_DOC_DELIVERY_COLUMN_NAME, SITE_DOC_ID_DOC_DELIVERY_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,deliveryID);
            pstmt.setInt(2,siteDocID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private void loadAllSiteDocDelivery(int id1, String columnName1, int id2, String columnName2, String tableName) {
        String sql = "SELECT * FROM " + tableName +
                (id1 != -1 || id2!= -1 ? " WHERE " +
                        ( id1 != -1 ? columnName1 + "=" + id1 : "") +
                        (id1 != -1 && id2 != -1 ? " AND " : "") +
                        ( id2 != -1 ? columnName2 + "=" + id2 : "")
                        : "");
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                loadDeliverySiteDocFromReader(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadDeliverySiteDocFromReader(ResultSet rs) {
        try {
            int deliveryId = rs.getInt(1);
            int siteDocId = rs.getInt(2);
            Delivery delivery = getDelivery(deliveryId);
            SiteDocument siteDocument = siteDocDAO.getSiteDoc(siteDocId);
            delivery.addSiteDoc(siteDocument);
            //TODO: add the add option without checking
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    private void addDeliveryToIDMap(int deliveryID , String departueDate,String deliveryMode,int truckID,
                                    int driverID, int orderID) throws Exception {
        if(!deliveryMap.containsKey(deliveryID)){
            Delivery delivery;
            if(deliveryMode.equals(DeliveryMode_Enum.Pending.toString()))
                delivery = new Delivery(deliveryID,departueDate,this, orderID);
            else
                delivery = new Delivery(deliveryID,departueDate,deliveryMode,truckDAO.getTruck(truckID),
                        driverID ,this, orderID);
            deliveryMap.put(deliveryID,delivery);
        }
    }

    private void addDeliveryToIDMap(int deliveryID,String date, int orderID) throws Exception {
        if(!deliveryMap.containsKey(deliveryID)){
            Delivery delivery = new Delivery(deliveryID,date,this, orderID);
            deliveryMap.put(deliveryID,delivery);
        }
    }

    private int maxDeliveryID() {
        String sql = "SELECT * FROM " + DELIVERY_TABLE_NAME;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            int maxID=0;
            while(rs.next())
                maxID=Math.max(maxID,(rs.getInt(1)));
            return maxID;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return -1;
    }

    public Delivery getDelivery(int deliveryId) throws Exception {
        if (deliveryMap.containsKey(deliveryId)) {
            return deliveryMap.get(deliveryId);
        }

        Delivery delivery = selectDelivery(deliveryId,DELIVERY_ID_COLUMN_NAME,DELIVERY_TABLE_NAME);
        if (delivery == null)
            throw new Exception("No such delivery with the given id : " + deliveryId);
        addDeliveryToIDMap(delivery);
        return delivery;
    }

    private void addDeliveryToIDMap(Delivery delivery) {
        if (!deliveryMap.containsKey(delivery.getId())) {
            deliveryMap.put(delivery.getId(), delivery);
            loadAllSiteDocDelivery(delivery.getId(), DELIVERY_ID_COLUMN_NAME, -1, SITE_DOC_ID_DOC_DELIVERY_COLUMN_NAME, DELIVERY_SITE_DOC_TABLE_NAME);
            for (SiteDocument siteDocument : delivery.getAllSite())
                productsSiteDocDAO.loadAllProductsSiteDoc(siteDocument.getSiteDocId(), -1);
        }
    }

    //
//    public Collection<Truck> getAllTrucks(){
//        return selectAllTrucks(TRUCKS_TABLE_NAME);
//
//    }
//

    //
//    private void addSiteDocToIDMap(int licenseNumber, String licenseKind, int weight, int maxWeight, boolean isAvailable) throws Exception {
//        if(!truckMap.containsKey(licenseNumber)){
//            Truck truck = new Truck(licenseNumber,licenseKind,weight,maxWeight,isAvailable);
//            truckMap.put(licenseNumber,truck);
//        }
//    }
//

    public void update(int deliveryID ,String deliveryMode) throws Exception {
        super.update(DELIVERY_TABLE_NAME,DELIVERY_MODE_COLUMN_NAME,deliveryMode,DELIVERY_ID_COLUMN_NAME,deliveryID);
    }
/*    public void update(int deliveryID ,String deliveryMode){
        super.update(DELIVERY_TABLE_NAME,DELIVERY_MODE_COLUMN_NAME,deliveryMode,DELIVERY_ID_COLUMN_NAME,deliveryID);
    }*/

    private Delivery selectDelivery(int id1, String columnName1, String tableName) {
        String sql = "SELECT * FROM " + tableName + " WHERE " + columnName1 + "=" + id1;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next())
                return ConvertReaderDelivery(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Delivery selectDeliveryByFields(String departueDate, int truckID, int driverID) {
        String sql = "SELECT * FROM " + DELIVERY_TABLE_NAME +
                " WHERE " + TRUCK_ID_COLUMN_NAME + "=" + truckID +
                " AND " + DRIVER_ID_COLUMN_NAME + "=" + driverID;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next())
                return ConvertReaderDelivery(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    public Collection<Delivery> getAllDeliveries(String deliveryMode) throws Exception {
//        if(allLoaded)
//            return deliveryMap.values();

        Collection<Delivery> colDeliveries = selectAllDeliveries(deliveryMode);
        if(colDeliveries.isEmpty())
            throw new Exception("no deliveries in :" +deliveryMode+ " Mode");
        addAllDeliveriesToIDMap(colDeliveries);
        allLoaded=true;
        return colDeliveries;

    }

    public Collection<Delivery> getAllPendingDeliveries(String deliveryMode) throws Exception {
//        if(allLoaded)
//            return deliveryMap.values();

        Collection<Delivery> colDeliveries = selectAllPendingDeliveries(deliveryMode);
        if(colDeliveries.isEmpty())
            throw new Exception("no deliveries in :" +deliveryMode+ " Mode");
        addAllDeliveriesToIDMap(colDeliveries);
        allLoaded=true;
        return colDeliveries;

    }

    public Collection<Delivery> getAllDeliveries() throws Exception {
        if(allLoaded)
            return deliveryMap.values();

        Collection<Delivery> colDeliveries = selectAllDeliveries();
        if(colDeliveries.isEmpty())
            throw new Exception("no deliveries");
        addAllDeliveriesToIDMap(colDeliveries);
        allLoaded=true;
        return deliveryMap.values();

    }

    private Collection<Delivery> selectAllDeliveries() {
        String sql = "SELECT * FROM " +DELIVERY_TABLE_NAME;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            Collection<Delivery> cDeliveries = new ArrayList<>();
            while(rs.next())
                cDeliveries.add(ConvertReaderDelivery(rs));
            return cDeliveries;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private void addAllDeliveriesToIDMap(Collection<Delivery> colDeliveries) {
        for (Delivery delivery : colDeliveries){
            if(!deliveryMap.containsKey(delivery.getId())) {
                addDeliveryToIDMap(delivery);
                //loadAllSiteDocDelivery(delivery.getId(), DELIVERY_ID_COLUMN_NAME, -1, SITE_DOC_ID_DOC_DELIVERY_COLUMN_NAME, DELIVERY_SITE_DOC_TABLE_NAME);
                //for (SiteDocument siteDocument : delivery.getAllSite())
                //    productsSiteDocDAO.loadAllProductsSiteDoc(siteDocument.getSiteDocId(), -1);
            }
        }
    }

    private Collection<Delivery> selectAllDeliveries(String deliveryMode){
        String sql = "SELECT * FROM " +DELIVERY_TABLE_NAME +" WHERE "+DELIVERY_MODE_COLUMN_NAME+" = '"+deliveryMode+"'";
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            Collection<Delivery> cDeliveries = new ArrayList<>();
            while(rs.next())
                cDeliveries.add(ConvertReaderDelivery(rs));
            return cDeliveries;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;

    }

    private Collection<Delivery> selectAllPendingDeliveries(String deliveryMode){
        String sql = "SELECT * FROM " +DELIVERY_TABLE_NAME +" WHERE "+DELIVERY_MODE_COLUMN_NAME+" = '"+deliveryMode+"'";
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            Collection<Delivery> cDeliveries = new ArrayList<>();
            while(rs.next())
                cDeliveries.add(ConvertReaderPendingDelivery(rs));
            return cDeliveries;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;

    }



    //
//    public Collection<Truck> selectAllTrucks(String tableName){
//        String sql = "SELECT * FROM " +tableName;
//        try (Connection conn = this.connect();
//             Statement stmt = conn.createStatement();
//             ResultSet rs = stmt.executeQuery(sql)) {
//            Collection<Truck> ctrucks = new ArrayList<>();
//            while(rs.next())
//                ctrucks.add(ConvertReaderToTruck(rs));
//            return ctrucks;
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//        return null;
//    }
//
//
    private Delivery ConvertReaderDelivery(ResultSet rs) {
        Delivery result = null;
        try {
            int id = rs.getInt(1);
            if(deliveryMap.containsKey(id))
                return deliveryMap.get(id);
            if(rs.getString(3).equals(DeliveryMode_Enum.Pending.toString()))
                result = new Delivery(rs.getInt(1),rs.getString(2),this, rs.getInt(6));
            else result = new Delivery(rs.getInt(1),rs.getString(2), rs.getString(3),
                    truckDAO.getTruck(rs.getInt(4)),rs.getInt(5),this, rs.getInt(6));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;

    }
    private Delivery ConvertReaderPendingDelivery(ResultSet rs) {
        Delivery result = null;
        try {
            int id = rs.getInt(1);
            if(deliveryMap.containsKey(id))
                return deliveryMap.get(id);
            result = new Delivery(rs.getInt(1),rs.getString(2),
                    this, rs.getInt(6));

            } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;

    }


    private void insert(int deliveryID , String departureDate,
                        String deliveryMode , int truckID, int driverID, int orderID) {
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}) VALUES(?,?,?,?,?,?)",
                DELIVERY_TABLE_NAME, DELIVERY_ID_COLUMN_NAME, DEPARTURE_DATE_COLUMN_NAME, DELIVERY_MODE_COLUMN_NAME,
                TRUCK_ID_COLUMN_NAME, DRIVER_ID_COLUMN_NAME, ORDERID_ID_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,deliveryID);
            pstmt.setString(2,departureDate);
            pstmt.setString(3,deliveryMode);
            pstmt.setInt(4,truckID);
            pstmt.setInt(5,driverID);
            pstmt.setInt(6,orderID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    private void insert(int deliveryID, String date, int orderID) {
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}) VALUES(?,?,?,?)",
                DELIVERY_TABLE_NAME, DELIVERY_ID_COLUMN_NAME, DEPARTURE_DATE_COLUMN_NAME,
                ORDERID_ID_COLUMN_NAME, DELIVERY_MODE_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,deliveryID);
            pstmt.setString(2,date);
            pstmt.setInt(3,orderID);
            pstmt.setString(4,DeliveryMode_Enum.Pending.toString());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }



    public void updateTruckAvaillabillity(int licenseNumber, boolean b) throws Exception {
        truckDAO.updateAvailabillity(licenseNumber,b);
    }

    public void updateTruck(int deliveryID , int truckID) {
        super.update(DELIVERY_TABLE_NAME,TRUCK_ID_COLUMN_NAME,truckID,DELIVERY_ID_COLUMN_NAME,deliveryID);
    }

    public void removeDelivery(int deliveryID) throws Exception {
        Delivery delivery = getDelivery(deliveryID);
        for(SiteDocument siteDocument: delivery.getAllSite())
            siteDocDAO.removeSiteDoc(siteDocument);
        remove(deliveryID,DELIVERY_ID_COLUMN_NAME,DELIVERY_TABLE_NAME);
    }

    public void updateDriver(int deliveryID, int driverID) {
        super.update(DELIVERY_TABLE_NAME,DRIVER_ID_COLUMN_NAME,driverID,DELIVERY_ID_COLUMN_NAME,deliveryID);
    }

    public void updateDate(int deliveryID, Date deliveryDate) throws Exception {
        DateConvertor dateConvertor = new DateConvertor();
        super.update(DELIVERY_TABLE_NAME,DEPARTURE_DATE_COLUMN_NAME,dateConvertor.dateToString(deliveryDate),
                DELIVERY_ID_COLUMN_NAME,deliveryID);

    }
}



