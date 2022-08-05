/*
package Backend.PersistenceLayer.DeliveriesDal;

import Backend.BusinessLayer.Deliveries.Truck;
import Backend.PersistenceLayer.DalController;

import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class SiteDAO extends DalController {
    private HashMap<String, Site> siteMap;
    final String SITE_TABLE_NAME = "Sites";
//    final String SITE_ID_COLUMN_NAME = "SiteID";
    final String SITE_NAME_COLUMN_NAME = "Name";
    final String ADDRESS_COLUMN_NAME = "Address";
    final String CONTACT_NAME_COLUMN_NAME = "ContactName";
    final String CONTACT_PHONE_COLUMN_NAME = "ContactPhone";
    final String AREA_KIND_COLUMN_NAME = "Area";


    public SiteDAO() {
        this.siteMap = new HashMap<>();
    }

    public void addSite(String siteName,String address, String contactName,
                        String contactPhone , String area) throws Exception {
            Site site = selectSite(siteName,SITE_NAME_COLUMN_NAME,SITE_TABLE_NAME);
    //    int maxSite = maxSiteID();
        if(site == null) {
            try {
                insert(siteName, address, contactName, contactPhone, area);
                addSiteToIDMap(address, siteName, contactName, contactPhone, area);

            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
    }

    private int maxSiteID() {
        String sql = "SELECT * FROM " + SITE_TABLE_NAME;
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

    private void addSiteToIDMap(String siteName, String address, String contactName, String contactPhone, String area) throws Exception {
        if(!siteMap.containsKey(siteName)){
            Site site = new Site(siteName,address,contactName,contactPhone,area);
            siteMap.put(siteName,site);
        }
    }


    public Site getSite(String siteName) throws Exception {
        if(siteMap.containsKey(siteName)){
            return siteMap.get(siteName);
        }

        Site site = selectSite(siteName,SITE_NAME_COLUMN_NAME,SITE_TABLE_NAME);
        if(site == null)
            throw new Exception("No such Site with the name : " +siteName);
        addSiteToIDMap(site);
        return site;
    }

    private void addSiteToIDMap(Site site) {
        if (!siteMap.containsKey(site.getName())){
            siteMap.put(site.getName(),site);
    }
    }

    public Collection<Site> getAllSites(){
        return selectAllSites();

    }

    private Site selectSite(String id1,String columnName1, String tableName){
        String sql = "SELECT * FROM " +tableName+ " WHERE "+ columnName1+"='"+id1+"'";
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next())
                return ConvertReaderToSite(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Collection<Site> selectAllSites(){
        String sql = "SELECT * FROM " +SITE_TABLE_NAME;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            Collection<Site> csites = new ArrayList<>();
            while(rs.next())
                csites.add(ConvertReaderToSite(rs));
            return csites;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    private Site ConvertReaderToSite(ResultSet rs) {
        Site result = null;
        try {
            result = new Site(rs.getString(2), rs.getString(1)
                    , rs.getString(3), rs.getString(4), rs.getString(5));
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;

    }
    private void insert(String siteName, String address, String contactName, String contactPhone, String area) {
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}) VALUES(?,?,?,?,?)",
                SITE_TABLE_NAME,SITE_NAME_COLUMN_NAME,ADDRESS_COLUMN_NAME,CONTACT_NAME_COLUMN_NAME,
                CONTACT_PHONE_COLUMN_NAME,AREA_KIND_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,siteName);
            pstmt.setString(2,address);
            pstmt.setString(3,contactName);
            pstmt.setString(4,contactPhone);
            pstmt.setString(5,area);

            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

}

*/
