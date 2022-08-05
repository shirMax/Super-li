package Backend.PersistenceLayer;

import Backend.DataLayer.DalController;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

public class BranchesDAO extends DalController {
    private static final String BRANCHES_TABLE_NAME = "Branches";
    private static final String BRANCH_ADDRESS_COLUMN = "BranchAddress";
    private static final String BRANCH_AREA_COLUMN = "Area";

    public BranchesDAO() {
    }

    public boolean checkIfBranchExists(String branchAddress) throws Exception {
        String sqlQuery = String.format("SELECT * FROM %s WHERE %s = '%s'",
                BRANCHES_TABLE_NAME,BRANCH_ADDRESS_COLUMN,branchAddress);
        return checkIfNotEmpty(sqlQuery);
    }

    public void addBranch(String branchAddress,String area) throws Exception {
        String sqlQuery = String.format("INSERT INTO %s Values (?,?)",BRANCHES_TABLE_NAME);
        try(Connection conn = this.connect();
            PreparedStatement statement = conn.prepareStatement(sqlQuery)){
            statement.setString(1,branchAddress);
            statement.setString(2,area);
            statement.executeUpdate();
        }catch (Exception e){
            throw new Exception("fail to insert new branch because: "+e.getMessage());
        }
    }


    public List<String> getAll() throws Exception {
        List<String> results = new LinkedList<>();
        String sqlQuery = String.format("SELECT * FROM %s",BRANCHES_TABLE_NAME);
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sqlQuery)) {
            ResultSet rsData=statement.executeQuery();
            while (rsData.next())
                results.add(rsData.getString(BRANCH_ADDRESS_COLUMN));
        }
        catch (SQLException e) {
            throw new Exception("fail to connect to branches table: "+e.getMessage());
        }
        return results;
    }

    public String getBranchesArea(String branchAddress) throws Exception {
        String sqlQuery = String.format("SELECT * FROM %s",BRANCHES_TABLE_NAME);
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sqlQuery)) {
            ResultSet rsData=statement.executeQuery();
            if(!rsData.next())
                throw new IllegalArgumentException("couldn't find branch address at the data base");
            return rsData.getString(BRANCH_AREA_COLUMN);
        }
        catch (SQLException e) {
            throw new Exception("fail to connect to branches table: "+e.getMessage());
        }
    }

    public void removeBranch(String branchName) throws Exception {
        if(!checkIfBranchExists(branchName))
            throw new IllegalArgumentException("category doesn't exists!");
        String query = String.format("DELETE FROM %s Where %s = '%s' ",BRANCHES_TABLE_NAME,BRANCH_ADDRESS_COLUMN,branchName);
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(query)){
             statement.executeUpdate();
        }catch (Exception e){
            throw new IllegalArgumentException(String.format("fail to remove branch %s from database",branchName));
        }
    }
}
