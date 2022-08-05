package Backend.PersistenceLayer.EmployeesDal;

import Backend.PersistenceLayer.CreateDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDBEmployees  extends CreateDB {
    @Override
    protected void createTables(Connection con) throws SQLException {
        try(Statement statement = con.createStatement()){
            statement.addBatch(createEmployeeTable());
            statement.addBatch(createShiftTable());
            statement.addBatch(createConstraintsTable());
            statement.addBatch(createMessagesTable());
            statement.executeBatch();
            con.commit();
            con.close();
        }
        catch (Exception e){
            throw new SQLException(e.getMessage());
        }
    }
    private String createMessagesTable(){
        return "CREATE TABLE IF NOT EXISTS Messages ("
                + "MessageDate text,"
                + "MessageContent text )";

    }

    private String createConstraintsTable() {
        return "CREATE TABLE IF NOT EXISTS Constraints ("
                + "ShiftID INTEGER,"
                + "EmployeeID INTEGER,"
                + "CONSTRAINT PRM_KEYS PRIMARY KEY (ShiftID,EmployeeID)," +
                "FOREIGN KEY(EmployeeID) REFERENCES Employee(EmployeeID) ON DELETE CASCADE )";
    }

    private String createEmployeeTable() {
        return "CREATE TABLE IF NOT EXISTS Employees ("
                + "EmployeeID INTEGER PRIMARY KEY,"
                + "Name text,"
                + "Salary INTEGER,"
                + "StartDate text,"
                + "Positions text,"
                + "Phone text,"
                + "License text,"
                + "BankAcc INTEGER,"
                + "IsAvailable BOOLEAN )";
    }
    private String createShiftTable() {
        return "CREATE TABLE IF NOT EXISTS Shift ("
                + "Date text ,"
                + "Type text ,"
                + "Employees text ,"
                + "Branch text ,"
                + "CONSTRAINT PRM_KEYS PRIMARY KEY (Date,Type,Branch))";
    }




}
