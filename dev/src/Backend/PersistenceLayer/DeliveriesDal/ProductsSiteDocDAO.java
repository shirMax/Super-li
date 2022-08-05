package Backend.PersistenceLayer.DeliveriesDal;

import Backend.BusinessLayer.Deliveries.Delivery;
import Backend.BusinessLayer.Deliveries.Product;
import Backend.BusinessLayer.Deliveries.SiteDocument;
import Backend.PersistenceLayer.DalController;

import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class ProductsSiteDocDAO  extends DalController {

    final String PRODUCTS_TABLE_NAME = "DeliveryProducts";
    final String SITE_DOC_ID1_COLUMN_NAME = "SiteDoc1";
    final String SITE_DOC_ID2_COLUMN_NAME = "SiteDoc2";
    final String CATEGORY_NUMBER_COLUMN_NAME = "CategoryNumber";
    final String PRODUCT_NAME_COLUMN_NAME = "ProductName";
    final String QUANTITY_COLUMN_NAME = "Quantity";
    private SiteDocDAO siteDocDAO;

    public ProductsSiteDocDAO(SiteDocDAO siteDocDAO) {
        this.siteDocDAO = siteDocDAO;
    }

    public void addProductToSiteDocs(int sourceDocID, int destinationDocID, int catNum,
                                     String name, int quantity) throws Exception {
        SiteDocument sourceDocument = siteDocDAO.getSiteDoc(sourceDocID);
        if (sourceDocument == null) {
            throw new Exception("no such site document with this id: " + sourceDocID);
        }
        SiteDocument destinationDocument = siteDocDAO.getSiteDoc(destinationDocID);
        if (destinationDocument == null) {
            throw new Exception("no such site document with this id: " + destinationDocID);
        }
        boolean isExists = isProductExists(sourceDocID,destinationDocID,catNum);
        if (!isExists) {
            try {
                insertToProductsSiteDoc(sourceDocID, destinationDocID, catNum, name, quantity);
            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
        else updateQuantity(sourceDocID,destinationDocID,catNum,quantity);
    }

    private void insertToProductsSiteDoc(int sourceDocID, int destinationDocID, int catNum,
                                         String name, int quantity) {
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}) VALUES(?,?,?,?,?)",
                PRODUCTS_TABLE_NAME, SITE_DOC_ID1_COLUMN_NAME, SITE_DOC_ID2_COLUMN_NAME,
                CATEGORY_NUMBER_COLUMN_NAME, PRODUCT_NAME_COLUMN_NAME, QUANTITY_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,sourceDocID);
            pstmt.setInt(2,destinationDocID);
            pstmt.setInt(3,catNum);
            pstmt.setString(4,name);
            pstmt.setInt(5,quantity);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void loadAllProductsSiteDoc(int id1, int id2) {
        String sql = "SELECT * FROM " + PRODUCTS_TABLE_NAME +
                (id1 != -1 || id2!= -1 ? " WHERE " +
                        ( id1 != -1 ? SITE_DOC_ID1_COLUMN_NAME + "=" + id1 : "") +
                        (id1 != -1 && id2 != -1 ? " AND " : "") +
                        ( id2 != -1 ? SITE_DOC_ID2_COLUMN_NAME + "=" + id2 : "")
                        : "");
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                loadProductsSiteDocFromReader(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private Boolean isProductExists(int siteId1,int siteId2, int prodId){
        String sql = "SELECT * FROM " + PRODUCTS_TABLE_NAME +
                " WHERE "+ SITE_DOC_ID1_COLUMN_NAME+" = "+siteId1 +
                " AND "+ SITE_DOC_ID2_COLUMN_NAME+" = "+siteId2 +
                " AND "+ CATEGORY_NUMBER_COLUMN_NAME+" = "+prodId;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next())
                return true;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return false;
    }


    private void loadProductsSiteDocFromReader(ResultSet rs) {
        try {
            int sourceDocID = rs.getInt(1);
            int destinationDocID = rs.getInt(2);
            int catNum = rs.getInt(3);
            String name = rs.getString(4);
            int quantity = rs.getInt(5);
            /*if(isProductExists(sourceDocID,destinationDocID,catNum))
                return;*/
            SiteDocument sourceDoc = siteDocDAO.getSiteDoc(sourceDocID);
            SiteDocument destinationDoc = siteDocDAO.getSiteDoc(destinationDocID);
            sourceDoc.addProduct(this, catNum,name,quantity,destinationDoc);
            destinationDoc.addProduct(this, catNum,name,quantity,sourceDoc);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

    public void updateQuantity(int siteDocID1, int siteDocID2, int catNum, int quantity) {
        super.update(PRODUCTS_TABLE_NAME, QUANTITY_COLUMN_NAME,quantity,SITE_DOC_ID1_COLUMN_NAME,siteDocID1,
                SITE_DOC_ID2_COLUMN_NAME,siteDocID2, CATEGORY_NUMBER_COLUMN_NAME,catNum);
    }
}




