package Backend.PersistenceLayer.StockDAL;

import Backend.DataLayer.CreateDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDBStock extends CreateDB {

    protected void createTables(Connection con) throws SQLException {
        try(Statement statement = con.createStatement()){
            statement.addBatch(createCategoriesTable());
            statement.addBatch(createChainProductsTable());
            statement.addBatch(createProductsTable());
            statement.addBatch(createSalesTable());
            statement.addBatch(createSalesProductsTable());
            statement.addBatch(createDefectivesTable());
            statement.addBatch(createPendingOrdersTable());
            statement.executeBatch();
            con.close();
        }
        catch (Exception e){
            throw new SQLException(e.getMessage());
        }
    }


    private String createDefectivesTable() {
        return "CREATE TABLE IF NOT EXISTS Defectives ("
                + "BranchAddress text,"
                + "ProductID INTEGER,"
                + "DefectivesAmount INTEGER,"
                + "ExpiredAmount INTEGER,"
                + "CONSTRAINT PK_Products PRIMARY KEY (productID,BranchAddress),"
                + "FOREIGN KEY(productID) REFERENCES ChainProducts(productID),"
                + "FOREIGN KEY(BranchAddress) REFERENCES Branches(BranchAddress)"
                +");";
    }

    private String createPendingOrdersTable() {
        return "CREATE TABLE IF NOT EXISTS PendingOrders ("
                + "orderId INTEGER,"
                + "branchAddress text,"
                + " FOREIGN KEY(branchAddress) REFERENCES Branches(BranchAddress)"
                +");";
    }

    private String createCategoriesTable(){
        return "CREATE TABLE IF NOT EXISTS Categories(" +
                " Category text PRIMARY KEY " +
                ");";
    }

    private String createChainProductsTable() {
        return "CREATE TABLE IF NOT EXISTS ChainProducts ("
                + "productID INTEGER PRIMARY KEY,"
                + "Category text,"
                + "ProductName text,"
                + "ManufacturerId Integer,"
                + "FOREIGN KEY(Category) REFERENCES Categories(Category)" +
                ");";
    }

    private String createProductsTable() {
        return "CREATE TABLE IF NOT EXISTS Products ("
                + "productID INTEGER,"
                + "BranchAddress text,"
                + "sellingPrice DOUBLE,"
                + "costPrice DOUBLE,"
                + "Demand DOUBLE,"
                + "StoreQuantity INTEGER,"
                + "WarehouseQuantity INTEGER,"
                + "StoreShelfID INTEGER,"
                + "WarehouseShelfID INTEGER,"
                + "DeliveryTime INTEGER,"
                + "SoldToday INTEGER,"
                + "DaysInStore INTEGER,"
                + "CONSTRAINT PK_Products PRIMARY KEY (productID,BranchAddress),"
                + "FOREIGN KEY(productID) REFERENCES ChainProducts(productID),"
                + "FOREIGN KEY(BranchAddress) REFERENCES Branches(BranchAddress) ON DELETE CASCADE" +
                ");";
    }


    private String createSalesTable() {
        return "CREATE TABLE IF NOT EXISTS Sales ("
                + "SaleName text PRIMARY KEY,"
                + "BranchAddress text,"
                + "Discount DOUBLE,"
                + "FOREIGN KEY(BranchAddress) REFERENCES Branches(BranchAddress) ON DELETE CASCADE" +
                ");";
    }

    private String createSalesProductsTable() {
        return "CREATE TABLE IF NOT EXISTS SalesProducts ("
                + "SaleName text,"
                + "productID INTEGER,"
                + "CONSTRAINT PK_SalesProducts PRIMARY KEY (SaleName,productID),"
                + "CONSTRAINT FK_SALES_TO_ProductsSales FOREIGN KEY(SaleName) REFERENCES Sales(SaleName) ON DELETE CASCADE ON UPDATE CASCADE"
                + ");";
    }
}
