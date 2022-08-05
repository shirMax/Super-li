package Backend.PersistenceLayer.StockDAL;

import Backend.DataLayer.DalController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class PendingOrdersDAO extends DalController {
    private static final String PENDING_ORDERS_TABLE_NAME = "PendingOrders";
    private static final String ORDER_ID_COLUMN = "orderId";
    private static final String BRANCH_ADDRESS_COLUMN = "branchAddress";


    public PendingOrdersDAO() {
    }

    public void addPendingOrder(int orderID,String address) {
        String sqlQuery = String.format("INSERT INTO %s Values (?,?)",PENDING_ORDERS_TABLE_NAME);
        try(Connection conn = this.connect();
            PreparedStatement statement = conn.prepareStatement(sqlQuery)){
            statement.setInt(1,orderID);
            statement.setString(2,address);
            statement.executeUpdate();
        }catch (Exception e){
            throw new IllegalArgumentException("fail to insert new branch because: "+e.getMessage());
        }
    }

    public List<Integer> getBranchPendingOrders(String branchAddress){
        String sqlQuery = String.format("SELECT * FROM %s WHERE %s = '%s' ", PENDING_ORDERS_TABLE_NAME,BRANCH_ADDRESS_COLUMN,branchAddress);
        List<Integer> results = new LinkedList<>();
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sqlQuery)) {
            ResultSet rsData=statement.executeQuery();
            while (rsData.next())
                results.add(rsData.getInt(ORDER_ID_COLUMN));
        }
        catch (SQLException e) {
            throw new IllegalArgumentException("fail to connect to pending orders table: "+e.getMessage());
        }
        return results;
    }

    public void removePendingOrder(int orderID) {
        String query = String.format("DELETE FROM %s Where %s = %d ",PENDING_ORDERS_TABLE_NAME,ORDER_ID_COLUMN,orderID);
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(query)){
            statement.executeUpdate();
        }catch (Exception e){
            throw new IllegalArgumentException(String.format("fail to delete pending order from database (%s)",e.getMessage()));
        }
    }
}
