package Backend.PersistenceLayer.StockDAL;


import Backend.DataLayer.DalController;

import java.sql.*;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class CategoryDAO extends DalController {

    private static final String TABLE_NAME = "Categories";
    private static final String Category_column = "Category";

    public void addCategory(List<String> category){
        String categoryString = getCategoryString(category);
        String sqlQuery = String.format("INSERT INTO %s Values (?)",TABLE_NAME);
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(sqlQuery)) {
            statement.setString(1, categoryString);
            statement.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private String getCategoryString(List<String> category){
        if(category.size() != 3)
            throw new IllegalArgumentException("categories list must by of size 3");
        return String.join("/",category);
    }

    //todo needs to get branchAddress as arg too.
    public void deleteCategory(List<String> category) throws SQLException {
        if(!checkIfExists(category))
            throw new IllegalArgumentException("category doesn't exists!");
        String query = String.format("DELETE FROM %s Where %s = '%s' ",TABLE_NAME,Category_column,getCategoryString(category));
        try (Connection conn = this.connect();
            PreparedStatement statement = conn.prepareStatement(query)){
            statement.executeUpdate();
        }catch (Exception e){
            throw new IllegalArgumentException(String.format("fail to delete category %s from database",getCategoryString(category)));
        }
    }

    public boolean checkIfExists(List<String> category) throws SQLException {
        String sqlQuery = String.format("Select * From %s where UPPER(%s) = UPPER('%s') ",TABLE_NAME,Category_column,getCategoryString(category));
        try (Connection conn = this.connect();
            Statement statement = conn.createStatement()){
            ResultSet res = statement.executeQuery(sqlQuery);
            if(res.next())
                return true;
            return false;
        } catch (SQLException e) {
            throw new SQLException("fail to connect with database");
        }
    }


    //todo return all categories of kind   mainCat/*/* (including none as *)
    //return List of all categories (in business shape)
    public List<List<String>> getAllSubCategoriesUnder(String mainCat) throws SQLException {
        String query ="SELECT * FROM "+TABLE_NAME+" WHERE UPPER("+Category_column+") like UPPER('"+mainCat+"%')";
        try (Connection conn = this.connect();
             Statement statement = conn.createStatement()){
            ResultSet res = statement.executeQuery(query);
            List<List<String>> categories = new LinkedList<>();
            while(res.next()){
                categories.add(Arrays.asList(res.getString(Category_column).split("/")));
            }
            return categories;
        } catch (SQLException e) {
            throw new SQLException("fail to connect with database");
        }
    }

    //todo return all categories of kind   mainCat/subCat/* (including none as *)
    //return List of all categories (in business shape)
    public List<List<String>> getAllSubCategoriesUnder(String mainCat,String subCat) throws SQLException {
        String query = "SELECT * FROM "+TABLE_NAME+" WHERE UPPER("+Category_column+") like UPPER('"+String.format("%s/%s",mainCat,subCat)+"%')";
        try (Connection conn = this.connect();
             Statement statement = conn.createStatement()){
             ResultSet res =  statement.executeQuery(query);
             List<List<String>> categories = new LinkedList<>();
             while(res.next()){
                 categories.add(Arrays.asList(res.getString(Category_column).split("/")));
             }
             return categories;
        } catch (SQLException e) {
            throw new SQLException("fail to connect with database");
        }

    }

    //todo returns all categories in db.
    //return List of all categories (in business shape)
    public List<List<String>> getAllCategories() throws SQLException {
        String query = String.format("SELECT * FROM %s ",TABLE_NAME,Category_column);
        try (Connection conn = this.connect();
             Statement statement = conn.createStatement()){
             ResultSet res = statement.executeQuery(query);
             List<List<String>> categories = new LinkedList<>();
             while(res.next()){
                 categories.add(Arrays.asList(res.getString(Category_column).split("/")));
             }
             return categories;
        } catch (SQLException e) {
            throw new SQLException("fail to connect with database");
        }
    }
}
