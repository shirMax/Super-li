package Backend.PersistenceLayer.DeliveriesDal;

import Backend.BusinessLayer.Deliveries.SiteDocument;
import Backend.PersistenceLayer.DalController;

import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class PendingDeliveriesDAO extends DalController {

    final String PENDING_TABLE_NAME = "PendingDeliveries";
    final String DELIVERY_ID_COLUMN_NAME = "DeliveryID";
    final String DATE_COLUMN_NAME = "Date";
    private HashMap<Integer, List<String>> pending;

    public PendingDeliveriesDAO(){
        pending = new HashMap<>();
    }

    public void addDateToPending(int deliveryID, String date) throws Exception {
        try {
            insertToPending(deliveryID, date);
        } catch (Exception e) {
            throw new Exception(e.getMessage());
        }
    }

    private void insertToPending(int deliveryID, String date) {
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}) VALUES(?,?)",
                PENDING_TABLE_NAME, DELIVERY_ID_COLUMN_NAME, DATE_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,deliveryID);
            pstmt.setString(2,date);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public List<String> loadAllPending(int id1) {
        String sql = "SELECT * FROM " + PENDING_TABLE_NAME + " WHERE " + DELIVERY_ID_COLUMN_NAME + " = " + id1;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                loadPendingFromReader(rs);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return pending.get(id1);
    }

    private void loadPendingFromReader(ResultSet rs) {
        try {
            int deliveryID = rs.getInt(1);
            String date = rs.getString(2);
            if(!pending.containsKey(deliveryID) || pending.get(deliveryID) == null)
                pending.put(deliveryID, new ArrayList<>());
            pending.get(deliveryID).add(date);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }

    }

}




