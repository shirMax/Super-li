package Backend.PersistenceLayer;

import java.io.File;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public abstract class CreateDB {

    public CreateDB() {
        createFileIfNotExists();
    }
    public void createFileIfNotExists()
    {
        try{
            String userDirectory = Paths.get("").toAbsolutePath().toString();
            File file = new File(userDirectory+"\\superli.db");
            if(!file.exists())
            {
                file.createNewFile();
            }
            connect();
        }
        catch (Exception e){
            System.out.println();
        }

    }
    public void connect() {
        Connection conn = null;
        try {
            // db parameters
            String pathtoProject = Paths.get("").toAbsolutePath().toString();
            String url = "jdbc:sqlite:"+pathtoProject+"\\superli.db";
            // create a connection to the database
            conn = DriverManager.getConnection(url);
            conn.setAutoCommit(false);
            createTables(conn);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        } finally {
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }

    protected abstract void createTables(Connection con) throws SQLException;

}

