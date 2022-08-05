package Backend.PersistenceLayer;

import Backend.DataLayer.CreateDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateBranches extends CreateDB {

    protected void createTables(Connection con) throws SQLException {
        try(Statement statement = con.createStatement()){
            statement.addBatch(createBranchesTable());
            statement.executeBatch();
            con.close();
        }
        catch (Exception e){
            throw new SQLException(e.getMessage());
        }
    }

    private String createBranchesTable() {
        return "CREATE TABLE IF NOT EXISTS Branches ("
                + "BranchAddress text PRIMARY KEY," +
                "Area text"+
                ");";
    }

}
