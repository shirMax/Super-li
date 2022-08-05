package Backend.PersistenceLayer.DeliveriesDal;

import Backend.PersistenceLayer.CreateDB;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateDBDeliveries extends CreateDB {

    protected void createTables(Connection con) throws SQLException {
        try(Statement statement = con.createStatement()){
            statement.addBatch(createTrucksTable());
            statement.addBatch(createSitesTable());
            statement.addBatch(createSiteDocumentsTable());
            statement.addBatch(createDeliveriesTable());
            statement.addBatch(createSiteDocDeliveriesTable());
            statement.addBatch(createProductsTable());
            statement.addBatch(createCommentsTable());
            //statement.addBatch(createPendingDeliveriesTable());


            statement.executeBatch();
            con.commit();
            con.close();
        }
        catch (Exception e){
            throw new SQLException(e.getMessage());
        }
    }

    public void clearDB() {
        try {
            String userDirectory = Paths.get("").toAbsolutePath().toString();
            File file = new File(userDirectory + "\\superli.db");
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            System.out.println();
        }

    }

    private String createTrucksTable() {
        return "CREATE TABLE IF NOT EXISTS Trucks ("
                + "LicenseNumber INTEGER PRIMARY KEY,"
                + "LicenseKind text,"
                + "Weight INTEGER,"
                + "MaxWeight INTEGER,"
                + "Available BOOLEAN);";
//                + "FOREIGN KEY(SupplierID) REFERENCES Suppliers(SupplierID) ON DELETE CASCADE);";
    }

    private String createSitesTable() {
        return "CREATE TABLE IF NOT EXISTS Sites ("
                + "Name text PRIMARY KEY,"
                + "Address text,"
                + "ContactName text,"
                + "ContactPhone INTEGER,"
                + "Area text);";
//                + "FOREIGN KEY(SupplierID) REFERENCES Suppliers(SupplierID) ON DELETE CASCADE);";
    }


    private String createSiteDocumentsTable() {
        // TODO: DRIVER !!!!
        return "CREATE TABLE IF NOT EXISTS SiteDocument ("
                + "DocID INTEGER PRIMARY KEY,"
                + "SiteName text,"
                + "SiteKind text,"
                + "TruckWeight INTEGER,"
                + "Visited BOOLEAN,"
                + "TruckID Integer,"
                + "DriverID Integer,"
                + "Address text,"
                + "Area text,"
                + "ContactName text,"
                + "ContactPhone text,"
//                + "FOREIGN KEY(SiteID) REFERENCES Sites(SiteID),"
                + "FOREIGN KEY(TruckID) REFERENCES Trucks(LicenseNumber));";
    }

    private String createDeliveriesTable() {
        // TODO: DRIVER !!!!
        return "CREATE TABLE IF NOT EXISTS Deliveries ("
                + "DeliveryID INTEGER PRIMARY KEY,"
                + "DepartureDate text,"
                + "DeliveryMode text,"
                + "TruckID INTEGER,"
                + "DriverID INTEGER,"
                + "OrderID INTEGER);";    }


    private String createSiteDocDeliveriesTable() {
        // TODO: DRIVER !!!!
        return "CREATE TABLE IF NOT EXISTS SiteDocDeliveries ("
                + "DeliveryID INTEGER,"
                + "SiteID INTEGER,"
                + "CONSTRAINT PRM_KEY PRIMARY KEY (DeliveryID,SiteID),"
                + "FOREIGN KEY(DeliveryID) REFERENCES Deliveries(DeliveryID) ON DELETE CASCADE,"
                + "FOREIGN KEY(SiteID) REFERENCES SiteDocument(DocID) ON DELETE CASCADE);";
    }


    private String createPendingDeliveriesTable() {
        // TODO: DRIVER !!!!
        return "CREATE TABLE IF NOT EXISTS PendingDeliveries ("
                + "DeliveryID INTEGER,"
                + "Date text,"
                + "FOREIGN KEY(DeliveryID) REFERENCES Deliveries(DeliveryID) ON DELETE CASCADE;";
    }

    private String createProductsTable() {
        // TODO: DRIVER !!!!
        return "CREATE TABLE IF NOT EXISTS DeliveryProducts ("
                + "SiteDoc1 INTEGER,"
                + "SiteDoc2 INTEGER,"
                + "CategoryNumber INTEGER,"
                + "ProductName text,"
                + "Quantity INTEGER,"
                + "CONSTRAINT PRM_KEYS PRIMARY KEY (SiteDoc1,SiteDoc2,CategoryNumber),"
                + "FOREIGN KEY(SiteDoc1) REFERENCES SiteDocument(DocID) ON DELETE CASCADE,"
                + "FOREIGN KEY(SiteDoc2) REFERENCES SiteDocument(DocID) ON DELETE CASCADE);";
    }

    private String createCommentsTable() {
        // TODO: DRIVER !!!!
        return "CREATE TABLE IF NOT EXISTS Comments ("
                + "SiteDoc INTEGER,"
                + "Comment text,"
                + "FOREIGN KEY(SiteDoc) REFERENCES SiteDocument(DocID) ON DELETE CASCADE);";
    }
}
