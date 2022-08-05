package Backend.PersistenceLayer.StockDAL;

import Backend.BusinessLayer.Tools.Pair;
import Backend.DataLayer.DalController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
public class DefectiveDAO extends DalController {
    private static final String DEFECTIVES_TABLE_NAME = "Defectives";
    private static final String BRANCH_ADDRESS_COLUMN = "BranchAddress";
    private static final String PRODUCT_ID_COLUMN = "ProductID";
    private static final String DEFECTIVE_AMOUNT_COLUMN = "DefectivesAmount";
    private static final String EXPIRED_AMOUNT_COLUMN = "ExpiredAmount";

    public DefectiveDAO(){

    }

    private boolean checkIfProductHasDefectives(int productID,String branchAddress) throws Exception {
        return checkIfNotEmpty(String.format("SELECT * FROM %s WHERE %s = %d and  %s = '%s'",
                DEFECTIVES_TABLE_NAME,PRODUCT_ID_COLUMN,productID,BRANCH_ADDRESS_COLUMN,branchAddress));
    }

    private void addNewDefective(String branchAddress,int productID, String stat, int amount) throws Exception {
        String sqlQuery = String.format("INSERT INTO %s Values (?,?,?,?)",DEFECTIVES_TABLE_NAME);
        try(Connection conn = this.connect();
            PreparedStatement statement = conn.prepareStatement(sqlQuery)){
            statement.setString(1,branchAddress);
            statement.setInt(2,productID);
            if(stat.equalsIgnoreCase("defective")){
                statement.setInt(3,amount);
                statement.setInt(4,0);
            }
            else{
                statement.setInt(3,0);
                statement.setInt(4,amount);
            }
            statement.executeUpdate();
        }catch (Exception e){
            throw new Exception("fail to insert new defective because: "+e.getMessage());
        }
    }

    private void UpdateDefective(String branchAddress,int productID, String stat, int amount) throws SQLException {
        String res;
        if(stat.equalsIgnoreCase("defective"))
            res= UpdateQuery(String.format("UPDATE %s SET %s = %s WHERE %s = %d and  %s = '%s'",
                DEFECTIVES_TABLE_NAME,DEFECTIVE_AMOUNT_COLUMN,DEFECTIVE_AMOUNT_COLUMN+"+"+amount,
                PRODUCT_ID_COLUMN,productID,BRANCH_ADDRESS_COLUMN,branchAddress));
        else
            res = UpdateQuery(String.format("UPDATE %s SET %s = %s WHERE %s = %d and  %s = '%s'",
                    DEFECTIVES_TABLE_NAME,EXPIRED_AMOUNT_COLUMN,EXPIRED_AMOUNT_COLUMN+"+"+amount,
                    PRODUCT_ID_COLUMN,productID,BRANCH_ADDRESS_COLUMN,branchAddress));
        if(res != null)
            throw new SQLException("failed to update defectives. error message: "+res);
    }

    private String UpdateQuery(String Query){
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(Query)) {
            statement.executeUpdate();
            return null;
        }
        catch (SQLException e) {
            return e.getMessage();
        }
    }

    private List<Pair<Integer,Integer>> getDataOF(String branchAddress,String statColumn) throws Exception {
        List<Pair<Integer,Integer>> results = new LinkedList<>();
        String sqlQuery = String.format("SELECT %s,%s FROM %s WHERE %s = '%s'",
                PRODUCT_ID_COLUMN,statColumn,DEFECTIVES_TABLE_NAME,BRANCH_ADDRESS_COLUMN, branchAddress);
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sqlQuery)) {
            ResultSet rsData=statement.executeQuery();
            while (rsData.next())
                results.add(new Pair<>(rsData.getInt(PRODUCT_ID_COLUMN),rsData.getInt(statColumn)));
        }
        catch (SQLException e) {
            throw new Exception("fail to connect to defectives table: "+e.getMessage());
        }
        return results;
    }

    public void addDefectives(String branchAddress,int productID, String stat, int amount) throws Exception {
        if(checkIfProductHasDefectives(productID,branchAddress))
            UpdateDefective(branchAddress,productID, stat, amount);
        else
            addNewDefective(branchAddress,productID, stat, amount);
    }

    public List<Pair<Integer,Integer>> getDefectiveInfos(String branchAddress) throws Exception {
        return getDataOF(branchAddress,DEFECTIVE_AMOUNT_COLUMN);
    }

    public List<Pair<Integer,Integer>> getExpiredInfos(String branchAddress) throws Exception {
        return getDataOF(branchAddress,EXPIRED_AMOUNT_COLUMN);
    }

}
