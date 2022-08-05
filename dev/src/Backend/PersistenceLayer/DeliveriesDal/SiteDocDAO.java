package Backend.PersistenceLayer.DeliveriesDal;

import Backend.BusinessLayer.Deliveries.Delivery;
import Backend.BusinessLayer.Deliveries.Enums.Area_Enum;
import Backend.BusinessLayer.Deliveries.SiteDocument;
import Backend.BusinessLayer.Deliveries.Truck;
import Backend.BusinessLayer.Suppliers.Contact;
import Backend.PersistenceLayer.DalController;

import java.sql.*;
import java.text.MessageFormat;
import java.util.HashMap;

public class SiteDocDAO  extends DalController {
    private HashMap<Integer, SiteDocument> siteDocMap;
    private TruckDAO truckDAO;
    //private SiteDAO siteDAO;
    final String SITE_DOCUMENTS_TABLE_NAME = "SiteDocument";
    final String DOC_ID_COLUMN_NAME = "DocID";
    final String SITE_NAME_COLUMN_NAME = "SiteName";
    final String SITE_KIND_COLUMN_NAME = "SiteKind";
    final String TRUCK_WEIGHT_COLUMN_NAME = "TruckWeight";
    final String VISITED_COLUMN_NAME = "Visited";
    final String TRUCK_ID_COLUMN_NAME = "TruckID";
    final String DRIVER_ID_COLUMN_NAME = "DriverID";
    //final String NAME_COLUMN_NAME = "Name";
    final String ADDRESS_COLUMN_NAME = "Address";
    final String CONTACT_NAME_COLUMN_NAME = "ContactName";
    final String CONTACT_PHONE_COLUMN_NAME = "ContactPhone";
    final String AREA_KIND_COLUMN_NAME = "Area";

    final String COMMENTS_TABLE_NAME = "Comments";
    final String COMMENTS_DOC_ID_COLUMN_NAME = "SiteDoc";
    final String COMMENT_COLUMN_NAME = "Comment";
    public SiteDocDAO(TruckDAO truckDAO/*, SiteDAO siteDAO*/) {
        this.siteDocMap = new HashMap<>();
        this.truckDAO = truckDAO;
        //this.siteDAO = siteDAO;
    }

    public SiteDocument addSiteDoc(String siteKind, int truckW, int driverID, int truckID, String address,
                                   String name, Area_Enum area, String contactName, String contactPhone) throws Exception {
//        Delivery delivery = selectDeliveryByFields(departueDate, truckID, driverID);
//        if (delivery == null) {
            int maxSiteDOCID = maxSiteDOCID();
            maxSiteDOCID++;
            SiteDocument siteDocument = selectSiteDoc(maxSiteDOCID, DOC_ID_COLUMN_NAME, SITE_DOCUMENTS_TABLE_NAME);
            if (siteDocument == null) {
                try {
                    insert(name, siteKind, truckW, maxSiteDOCID, driverID, truckID,address,contactName,
                            contactPhone,area.toString());
                    siteDocument = new SiteDocument(this, /*site,*/ siteKind, maxSiteDOCID, driverID,
                            truckDAO.getTruck(truckID),address,name,area,contactName,contactPhone);
                    addSiteDocToIDMap(siteDocument);
                } catch (Exception e) {
                    throw new Exception(e.getMessage());
                }
            }
            return siteDocument;
        }



        private int maxSiteDOCID() {
            String sql = "SELECT * FROM " + SITE_DOCUMENTS_TABLE_NAME;
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


    public void addComment(int docID, String comment) throws Exception {
        SiteDocument siteDocument = getSiteDoc(docID);
        if (siteDocument == null) {
            throw new Exception("no such site document with this id: " + docID);
        }
        try {
            insertCommentToSiteDoc(docID, comment);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private void insertCommentToSiteDoc(int sourceDocID, String comment) {
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}) VALUES(?,?)",
                COMMENTS_TABLE_NAME, COMMENTS_DOC_ID_COLUMN_NAME, COMMENT_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,sourceDocID);
            pstmt.setString(2,comment);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadAllSiteDocComments(int id1, String columnName1, String tableName) {
        String sql = "SELECT * FROM " + tableName + " WHERE " + columnName1 + "=" + id1;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next())
                loadCommentsSiteDocFromReader(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void loadCommentsSiteDocFromReader(ResultSet rs) {
        try {
            int docID = rs.getInt(1);
            String comment = rs.getString(2);
            SiteDocument siteDoc = getSiteDoc(docID);
            siteDoc.addCommentFromDB(comment);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }


    public SiteDocument getSiteDoc(int siteDocId) throws Exception {
        if(siteDocMap.containsKey(siteDocId)){
            return siteDocMap.get(siteDocId);
        }
        SiteDocument siteDocument = selectSiteDoc(siteDocId,DOC_ID_COLUMN_NAME,SITE_DOCUMENTS_TABLE_NAME);
        if(siteDocument == null)
            throw new Exception("No such siteDocument with the given id : " + siteDocId);
        addSiteDocToIDMap(siteDocument);
        loadAllSiteDocComments(siteDocId,COMMENTS_DOC_ID_COLUMN_NAME,COMMENTS_TABLE_NAME);
        return siteDocument;
    }
//
//    public Collection<Truck> getAllTrucks(){
//        return selectAllTrucks(TRUCKS_TABLE_NAME);
//
//    }
//
    private void addSiteDocToIDMap(SiteDocument siteDocument) {
        if (!siteDocMap.containsKey(siteDocument.getSiteDocId())){
            siteDocMap.put(siteDocument.getSiteDocId(), siteDocument);
        }
    }
//
//    private void addSiteDocToIDMap(int licenseNumber, String licenseKind, int weight, int maxWeight, boolean isAvailable) throws Exception {
//        if(!truckMap.containsKey(licenseNumber)){
//            Truck truck = new Truck(licenseNumber,licenseKind,weight,maxWeight,isAvailable);
//            truckMap.put(licenseNumber,truck);
//        }
//    }
//
    private SiteDocument selectSiteDoc(int id1,String columnName1, String tableName){
        String sql = "SELECT * FROM " +tableName+ " WHERE "+ columnName1+"="+id1;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next()) {
                String s = rs.getString(6);
                if (s == null || s.length() == 0)
                    return ConvertPendingReaderToSiteDOC(rs);
                else
                    return ConvertReaderToSiteDOC(rs);
            }
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
//                ctrucks.add(ConvertReaderToSiteDOC(rs));
//            return ctrucks;
//        } catch (SQLException e) {
//            System.out.println(e.getMessage());
//        }
//        return null;
//    }
//
//
private SiteDocument ConvertReaderToSiteDOC(ResultSet rs) {
    SiteDocument result = null;
    try {
        result = new SiteDocument(this, rs.getString(3),
                rs.getInt(1),rs.getInt(7),truckDAO.getTruck(rs.getInt(6)) ,
                rs.getBoolean(5),rs.getString(8),rs.getString(2),
                rs.getString(9), rs.getString(10),rs.getString(11));
    }
    catch (SQLException throwables) {
        throwables.printStackTrace();
    } catch (Exception exception) {
        exception.printStackTrace();
    }
    return result;

}
    private SiteDocument ConvertPendingReaderToSiteDOC(ResultSet rs) {
        SiteDocument result = null;
        try {
            result = new SiteDocument(this, rs.getString(3), rs.getInt(1),
                    rs.getString(8),rs.getString(2),rs.getString(9),rs.getString(10),
                    rs.getString(11));
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;

    }


    private void insert(String siteName, String siteKind, int truckW, int siteDocId, int driverID, int truckID,
                        String address, String contactName, String contactPhone, String area) {
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}, {10}, {11}) VALUES(?,?,?,?,?,?,?,?,?,?,?)",
                SITE_DOCUMENTS_TABLE_NAME,DOC_ID_COLUMN_NAME, SITE_NAME_COLUMN_NAME, SITE_KIND_COLUMN_NAME,
                TRUCK_WEIGHT_COLUMN_NAME,VISITED_COLUMN_NAME,TRUCK_ID_COLUMN_NAME, DRIVER_ID_COLUMN_NAME,
                ADDRESS_COLUMN_NAME,AREA_KIND_COLUMN_NAME,CONTACT_NAME_COLUMN_NAME,
                CONTACT_PHONE_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,siteDocId);
            pstmt.setString(2,siteName);
            pstmt.setString(3,siteKind);
            pstmt.setInt(4,truckW);
            pstmt.setBoolean(5,false);
            pstmt.setInt(6,truckID);
            pstmt.setInt(7,driverID);
            pstmt.setString(8,address);
            pstmt.setString(9,area);
            pstmt.setString(10,contactName);
            pstmt.setString(11,contactPhone);
//            pstmt.setString(9,name);
//            pstmt.setString(10,area);
//            pstmt.setString(11,contactName);
//            pstmt.setString(12,contactPhone);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void insertPending(String siteName, String siteKind, int siteDocId,
                               String address, String contactName, String contactPhone, String area) {
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}) VALUES(?,?,?,?,?,?,?,?)",
                SITE_DOCUMENTS_TABLE_NAME,DOC_ID_COLUMN_NAME, SITE_NAME_COLUMN_NAME, SITE_KIND_COLUMN_NAME,
                VISITED_COLUMN_NAME, ADDRESS_COLUMN_NAME,AREA_KIND_COLUMN_NAME,
                CONTACT_NAME_COLUMN_NAME, CONTACT_PHONE_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,siteDocId);
            pstmt.setString(2,siteName);
            pstmt.setString(3,siteKind);
            pstmt.setBoolean(4,false);
            pstmt.setString(5,address);
            pstmt.setString(6,area);
            pstmt.setString(7,contactName);
            pstmt.setString(8,contactPhone);
/*            pstmt.setString(6,name);
            pstmt.setString(7,area);
            pstmt.setString(8,contactName);
            pstmt.setString(9,contactPhone);*/
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void updateVisit(boolean visited, int siteDocId) throws Exception {
        super.update(SITE_DOCUMENTS_TABLE_NAME,VISITED_COLUMN_NAME,visited,DOC_ID_COLUMN_NAME,siteDocId);
    }

    public void updateTruckWeight(int truckWeight, int siteDocId) {
        super.update(SITE_DOCUMENTS_TABLE_NAME,TRUCK_WEIGHT_COLUMN_NAME,truckWeight,DOC_ID_COLUMN_NAME,siteDocId);
    }

    public void updateDriver(int driverID, int siteDocId) {
        super.update(SITE_DOCUMENTS_TABLE_NAME,DRIVER_ID_COLUMN_NAME,driverID,DOC_ID_COLUMN_NAME,siteDocId);
    }

    public void updateContact(Contact contact, int siteDocId) throws Exception {
        super.update(SITE_DOCUMENTS_TABLE_NAME,CONTACT_NAME_COLUMN_NAME,contact.getName(),DOC_ID_COLUMN_NAME,siteDocId);
        super.update(SITE_DOCUMENTS_TABLE_NAME,CONTACT_PHONE_COLUMN_NAME,contact.getPhone(),DOC_ID_COLUMN_NAME,siteDocId);
    }

    public void updateTruck(int licenseNumber, int siteDocId) {
        super.update(SITE_DOCUMENTS_TABLE_NAME,TRUCK_ID_COLUMN_NAME,licenseNumber,DOC_ID_COLUMN_NAME,siteDocId);
    }

    public void removeSiteDoc(SiteDocument siteDocument) throws Exception {
        remove(siteDocument.getSiteDocId(), DOC_ID_COLUMN_NAME, SITE_DOCUMENTS_TABLE_NAME);
    }

    public SiteDocument addSiteDoc(String siteKind, String address, String name, Area_Enum area,
                                   String contactName, String contactPhone) throws Exception {
        int maxSiteDOCID = maxSiteDOCID();
        maxSiteDOCID++;
        SiteDocument siteDocument = selectSiteDoc(maxSiteDOCID, DOC_ID_COLUMN_NAME, SITE_DOCUMENTS_TABLE_NAME);
        if (siteDocument == null) {
            try {
                insertPending(name, siteKind, maxSiteDOCID,address,contactName,
                        contactPhone,area.toString());
                siteDocument = new SiteDocument(this, siteKind, maxSiteDOCID, address,name,
                        area.toString(),contactName,contactPhone);
                addSiteDocToIDMap(siteDocument);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
        return siteDocument;
    }
}