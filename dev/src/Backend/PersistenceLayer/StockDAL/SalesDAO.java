package Backend.PersistenceLayer.StockDAL;

import Backend.BusinessLayer.Tools.Pair;
import Backend.DataLayer.DalController;

import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class SalesDAO extends DalController {

    private static final String SALES_TABLE = "Sales";
    private static final String SALE_NAME_COLUMN = "SaleName";

    private static final String DISCOUNT_COLUMN = "Discount";

    static final String PRODUCTS_SALES_TABLE = "SalesProducts";

    public SalesDAO() {}

    //range = (Null for global) or (branch address for specific branch)
    public void addSale(String saleName,double discount,String range) throws Exception {
        if(checkSaleExists(saleName))
            throw new Exception("sale name already exists");
        String query = String.format("INSERT INTO %s Values ('%s','%s',%.2f)",SALES_TABLE,saleName,range,discount);
        try(Connection conn = connect();
            PreparedStatement statement = conn.prepareStatement(query)){
            statement.executeUpdate();
        }catch (Exception e){
            throw new SQLException("failed to insert sale to sales table");
        }
    }

    //todo branchAddress should be arg too.
    //sale name is the primary ket of sales table, so we dont need branch address
    public void deleteSale(String saleName) throws Exception {
        if(!checkSaleExists(saleName))
            throw new Exception("no such sale name");
        String sql = String.format("DELETE FROM %s WHERE %s = '%s'",SALES_TABLE,SALE_NAME_COLUMN,saleName);
        runDeleteQuery(sql);
        deleteSaleFromProductsSales(saleName);
    }

    public List<Pair<String,Double>> getAllSales() throws SQLException {
        String query = String.format("SELECT * FROM %s ",SALES_TABLE);
        try (Connection conn = this.connect();
             Statement statement = conn.createStatement()){
            ResultSet res = statement.executeQuery(query);
            List<Pair<String,Double>> Sales = new LinkedList<>();
            while(res.next()){
                Sales.add(new Pair<>(res.getString(SALE_NAME_COLUMN),res.getDouble(DISCOUNT_COLUMN)));
            }
            return Sales;
        } catch (SQLException e) {
            throw new SQLException("fail to connect with database");
        }
    }


    private void deleteSaleFromProductsSales(String saleName) throws SQLException {
        String query = String.format("DELETE FROM %s WHERE %s = '%s'",PRODUCTS_SALES_TABLE,SALE_NAME_COLUMN,saleName);
        runDeleteQuery(query);
    }

    private boolean checkSaleExists(String saleName) throws SQLException {
        String query = String.format("SELECT * from %s where %s = '%s'",SALES_TABLE,SALE_NAME_COLUMN,saleName);
        return checkIfNotEmpty(query);
    }
}
