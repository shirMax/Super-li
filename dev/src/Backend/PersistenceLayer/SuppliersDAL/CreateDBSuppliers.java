package Backend.PersistenceLayer.SuppliersDAL;

import Backend.DataLayer.CreateDB;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDBSuppliers extends CreateDB {

    protected void createTables(Connection con) throws SQLException {
        try(Statement statement = con.createStatement()){
            statement.addBatch(createSupplierTable());
            statement.addBatch(createContactTable());
            statement.addBatch(createOrderConstTable());
            statement.addBatch(createOrderItemsTable());
            statement.addBatch(createItemTable());
            statement.addBatch(createItemDiscountTable());
            statement.addBatch(createOrderCollectTable());
            statement.executeBatch();
            con.close();
        }
        catch (Exception e){
            throw new SQLException(e.getMessage());
        }
    }

    private String createContactTable() {
        return "CREATE TABLE IF NOT EXISTS Contacts ("
                + "SupplierID INTEGER,"
                + "ContactID INTEGER,"
                + "Name text,"
                + "Phone text,"
                + "PRIMARY KEY (SupplierID, ContactID)," +
                "FOREIGN KEY(SupplierID) REFERENCES Suppliers(SupplierID) ON DELETE CASCADE);";
    }


    private String createItemTable() {
        return "CREATE TABLE IF NOT EXISTS Items (\n"
                + "	SupplierID INTEGER,\n"
                + "	ItemID INTEGER,\n"
                + "	CatalogID INTEGER,\n"
                + "	Name TEXT,\n"
                + " Price REAL,\n"
                + "PRIMARY KEY (SupplierID, ItemID)"
                +"FOREIGN KEY(SupplierID) REFERENCES Suppliers(SupplierID) ON DELETE CASCADE);";
    }

    private String createItemDiscountTable() {
        return "CREATE TABLE IF NOT EXISTS ItemsDiscount (\n"
                + "	SupplierID INTEGER ,\n"
                + "	ItemID INTEGER ,\n"
                + "	Amount INTEGER,\n"
                + "Discount INTEGER,\n"
                + "PRIMARY KEY (SupplierID, ItemID,Amount)"
                +"FOREIGN KEY(SupplierID) REFERENCES Suppliers(SupplierID) ON DELETE CASCADE,"
                +"FOREIGN KEY(ItemID) REFERENCES Items(ItemID) ON DELETE CASCADE);";
    }

    private String createOrderItemsTable() {
        return  "CREATE TABLE IF NOT EXISTS OrderItems (\n"
                + "	OrderID INTEGER ,\n"
                + "	ItemID INTEGER ,\n"
                + "	Amount INTEGER,\n"
                + "ItemPriceAfterDiscount REAL,\n"
                + "	Name TEXT,\n"
                + "PRIMARY KEY (OrderID,ItemID)"
                +"FOREIGN KEY(OrderID) REFERENCES OrdersCollect(OrderID) ON DELETE CASCADE,"
                +"FOREIGN KEY(OrderID) REFERENCES OrdersConst(OrderID) ON DELETE CASCADE);";

    }

    private String createOrderCollectTable() {
        return  "CREATE TABLE IF NOT EXISTS OrdersCollect (\n"
                + "	OrderID INTEGER PRIMARY KEY,\n"
                + "	SupplierID INTEGER,\n"
                + "	OrderDate TEXT,\n"
                + "	ArrivedDate TEXT,\n"
                + "	SuperAddress TEXT,\n"
                + "PriceAfterDiscount REAL,\n"
                + "OrderState TEXT, \n"
                + "DeliveryID INTEGER, \n"
                + "ContactPhone TEXT, \n"
                +"FOREIGN KEY(SupplierID) REFERENCES Suppliers(SupplierID) ON DELETE CASCADE);";
    }

    private String createOrderConstTable() {
        return  "CREATE TABLE IF NOT EXISTS OrdersConst (\n"
                + "	OrderID INTEGER PRIMARY KEY,\n"
                + "	SupplierID INTEGER ,\n"
                + "	Days TEXT ,\n"
                + "	SuperAddress TEXT,\n"
                + "PriceAfterDiscount REAL,\n"
                +"FOREIGN KEY(SupplierID) REFERENCES Suppliers(SupplierID) ON DELETE CASCADE);";
    }

    private String createSupplierTable() {
        return  "CREATE TABLE IF NOT EXISTS Suppliers (\n"
                + "	SupplierID INTEGER PRIMARY KEY,\n"
                + "	SupplierName TEXT,\n"
                + "	SupplierAddress TEXT,\n"
                + "	BankAccount INTEGER,\n"
                + "	Payment TEXT,\n"
                + "	TypeDelivery TEXT, \n"
                + "	Area TEXT);";
    }
}
