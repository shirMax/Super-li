package Backend.DataLayer;
import javax.xml.transform.Result;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public abstract class DalController {
    public DalController() {
    }
    protected Connection connect() {
        // SQLite connection string
        String userDirectory = Paths.get("").toAbsolutePath().toString();
        String url = "jdbc:sqlite:" + userDirectory + "\\superli.db";
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(url);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return conn;
    }

    protected void runDeleteQuery(String query) throws SQLException {
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new SQLException("failed to delete sale from data base");
        }
    }
    public void remove(int columnValue, String columnName, String tableName) throws Exception {
        String sql = "DELETE FROM " +tableName+ " WHERE " +columnName+ "= ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, columnValue);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void remove(String columnValue, String columnName, String tableName) throws Exception {
        String sql = "DELETE FROM " +tableName+ " WHERE " +columnName+ "= ?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, columnValue);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
//    public void getSize(String tableName){
//
//    }


    public void remove(int columnValue1, int columnValue2, int columnValue3,String columnName1,String columnName2,String columnName3, String tableName) throws Exception {
        String sql = "DELETE FROM " +tableName+ " WHERE " +columnName1+ "= ? AND "+columnName2 +"=? AND "+columnName3+"=?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, columnValue1);
            pstmt.setInt(2, columnValue2);
            pstmt.setInt(3, columnValue3);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void remove(int columnValue1, int columnValue2, String columnName1,String columnName2, String tableName) throws Exception {
        String sql = "DELETE FROM " +tableName+ " WHERE " +columnName1+ "= ? AND "+columnName2 +"=?";
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, columnValue1);
            pstmt.setInt(2, columnValue2);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void update(String tableName,  String attributeName, String attributeValue, String columnName1,String columnName2,int value1, int value2) {
        String sql = "UPDATE "+tableName+" SET "+attributeName+" = ? "
                + " WHERE "+columnName1+" = ? AND "+columnName2+" = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // set the corresponding param
            pstmt.setString(1, attributeValue);
            pstmt.setInt(2, value1);
            pstmt.setInt(3, value2);
            // update
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void update(String tableName,  String attributeName, double attributeValue, String columnName1,String columnName2,int value1, int value2) {
        String sql = "UPDATE "+tableName+" SET "+attributeName+" = ? "
                + " WHERE "+columnName1+" = ? AND "+columnName2+" = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // set the corresponding param
            pstmt.setDouble(1, attributeValue);
            pstmt.setInt(2, value1);
            pstmt.setInt(3, value2);
            // update
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void update(String tableName,  String attributeName, int attributeValue, String columnName1,String columnName2,int value1, int value2) {
        String sql = "UPDATE "+tableName+" SET "+attributeName+" = ? "
                + " WHERE "+columnName1+" = ? AND "+columnName2+" = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // set the corresponding param
            pstmt.setDouble(1, attributeValue);
            pstmt.setInt(2, value1);
            pstmt.setInt(3, value2);
            // update
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void update(String tableName,  String attributeName, double attributeValue, String columnName1,int value1) {
        String sql = "UPDATE "+tableName+" SET "+attributeName+" = ? "
                + " WHERE "+columnName1+" = ? ";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // set the corresponding param
            pstmt.setDouble(1, attributeValue);
            pstmt.setInt(2, value1);
            // update
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void update(String tableName,  String attributeName, String attributeValue, String columnName1,String value1) {
        String sql = "UPDATE "+tableName+" SET "+attributeName+" = ? "
                + " WHERE "+columnName1+" = ? ";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // set the corresponding param
            pstmt.setString(1, attributeValue);
            pstmt.setString(2, value1);
            // update
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void update(String tableName,  String attributeName1, String attributeName2, String attributeName3, String attributeValue1, int attributeValue2,String attributeValue3, String columnName1,int value1) {
        String sql = "UPDATE "+tableName+" SET "+attributeName1+" = ? ," + attributeName2+" = ?, " + attributeName3+" = ?, "
                + " WHERE "+columnName1+" = ? ";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // set the corresponding param
            pstmt.setString(1, attributeValue1);
            pstmt.setInt(2, attributeValue2);
            pstmt.setString(3, attributeValue3);
            pstmt.setInt(4, value1);
            // update
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void update(String tableName,  String attributeName1, String attributeName2, int attributeValue1, double attributeValue2, String columnName1, String columnName2, int value1, int value3 ) {
        String sql = "UPDATE "+tableName+" SET "+attributeName1+" = ? ," + attributeName2+" = ? " +
                " WHERE "+columnName1+" = ? AND "+columnName2 +" = ?";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // set the corresponding param
            pstmt.setInt(1, attributeValue1);
            pstmt.setDouble(2, attributeValue2);
            pstmt.setInt(3, value1);
            pstmt.setInt(4, value3);
            // update
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
    public void update(String tableName,  String attributeName1, String attributeName3, String attributeValue1,String attributeValue3, String columnName1,int value1) {
        String sql = "UPDATE "+tableName+" SET "+attributeName1+" = ? ,"  + attributeName3+" = ? "
                + " WHERE "+columnName1+" = ? ";

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            // set the corresponding param
            pstmt.setString(1, attributeValue1);
            pstmt.setString(2, attributeValue3);
            pstmt.setInt(3, value1);
            // update
            pstmt.executeUpdate();
        }
        catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    protected boolean checkIfNotEmpty(String query) throws SQLException {
        try (Connection conn = this.connect();
             Statement statement = conn.createStatement()){
            ResultSet res = statement.executeQuery(query);
            return res.next();
        } catch (SQLException e) {
            throw new SQLException("fail to connect with database");
        }
    }

}
