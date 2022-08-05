package Backend.PersistenceLayer.StockDAL;

import Backend.BusinessLayer.Stock.Objects.Product;
import Backend.BusinessLayer.Stock.Objects.Sale;
import Backend.DataLayer.DalController;

import java.sql.*;
import java.util.*;

public class ProductDAO extends DalController {

    private static final String CHAIN_TABLE_NAME = "ChainProducts";
    private static final String PRODUCTS_TABLE_NAME = "Products";
    private static final String PRODUCTS_SALES_TABLE = "SalesProducts";

    private static final String PRODUCT_ID_COLUMN = "ProductID";
    private static final String BRANCH_ADDRESS_COLUMN = "BranchAddress";
    private static final String PRODUCT_NAME_COLUMN = "ProductName";
    private static final String MANUFACTURER_COLUMN = "ManufacturerId";
    private static final String SELLING_PRICE_COLUMN = "SellingPrice";
    private static final String COST_PRICE_COLUMN = "costPrice";
    private static final String STORE_SHELF = "StoreShelfID";
    private static final String WAREHOUSE_SHELF = "WarehouseShelfID";
    private static final String DELIVERY_TIME = "DeliveryTime";
    private static final String CATEGORY = "Category";
    private static final String DEMAND = "Demand";
    private static final String STORE_QUANTITY = "StoreQuantity";
    private static final String WAREHOUSE_QUANTITY = "WarehouseQuantity";
    private static final String SOLD_TODAY = "SoldToday";
    private static final String DAYS_IN_STORE = "DaysInStore";

    private static final String SALES_TABLE = "Sales";
    private static final String SALE_NAME_COLUMN = "SaleName";
    private static final String DISCOUNT_COLUMN = "Discount";

    public ProductDAO() {
    }



    public Product getProduct(int productId,String branch) throws SQLException {
        checkProductExists(productId,branch);
        String ChainSqlQuery = String.format("Select * From %s where %s = %d ",CHAIN_TABLE_NAME, PRODUCT_ID_COLUMN,productId);
        String BranchSqlQuery = String.format("Select * from %s where %s = %d and %s = '%s' ",PRODUCTS_TABLE_NAME, PRODUCT_ID_COLUMN,productId,BRANCH_ADDRESS_COLUMN,branch);
        String productName;
        int manufacturer;
        String category;
        Product product;
        try (Connection conn = this.connect();
             Statement statement = conn.createStatement()){
            ResultSet resChain = statement.executeQuery(ChainSqlQuery);
            productName = resChain.getString(PRODUCT_NAME_COLUMN);
            manufacturer = resChain.getInt(MANUFACTURER_COLUMN);
            category = resChain.getString(CATEGORY);
        } catch (SQLException e) {
            throw new SQLException("fail to connect with database");
        }
        try (Connection conn = this.connect();
             Statement statement = conn.createStatement()){
            ResultSet resBranch = statement.executeQuery(BranchSqlQuery);
            product =  new Product(productId,productName,manufacturer,
                    resBranch.getDouble(SELLING_PRICE_COLUMN),resBranch.getDouble(COST_PRICE_COLUMN),
                    resBranch.getInt(STORE_SHELF),resBranch.getInt(WAREHOUSE_SHELF),resBranch.getInt(DELIVERY_TIME)
                    ,getCategoriesFromString(category));
            product.setDemand(resBranch.getDouble(DEMAND));
            product.setStoreQuantity(resBranch.getInt(STORE_QUANTITY));
            product.setWarehouseQuantity(resBranch.getInt(WAREHOUSE_QUANTITY));
            product.setSoldToday(resBranch.getInt(SOLD_TODAY));
            product.setDaysInStore(resBranch.getInt(DAYS_IN_STORE));
        } catch (SQLException e) {
            throw new SQLException("fail to connect with database");
        }
    product.setSales(getProductsSales(productId,branch));
        return product;
    }

    private List<Sale> getProductsSales(int productId,String branch) throws SQLException {
        String query = String.format("SELECT Sales.SaleName,Products.productID,Discount from (Sales join SalesProducts on sales.SaleName = SalesProducts.SaleName) as s join Products on Products.productID = s.productID Where Products.productID = %d and (Sales.BranchAddress = '%s' or Sales.BranchAddress = NULL)",productId,branch);
        try (Connection conn = this.connect();
             Statement statement = conn.createStatement()){
            ResultSet res = statement.executeQuery(query);
            List<Sale> sales = new ArrayList<>();
            while(res.next()){
                sales.add(new Sale(res.getString(SALE_NAME_COLUMN),res.getDouble(DISCOUNT_COLUMN)));
            }
            return sales;
        } catch (SQLException e) {
            throw new SQLException("fail to connect with database");
        }
    }

    private List<String> getCategoriesFromString(String string) {
        return Arrays.asList(string.split("/"));
    }


    private void checkProductExists(int productId,String branch) throws SQLException {
        if(!checkProductExistsInChain(productId))
            throw new IllegalArgumentException("product not in chain products");
        if(!checkProductExistsBranch(productId,branch))
            throw new IllegalArgumentException("product not in branch products");
    }

    public boolean checkProductExistsBranch(int productId,String branch) throws SQLException {
        String sqlQuery = String.format("Select * From %s where %s = %d AND %s = '%s' ",PRODUCTS_TABLE_NAME, PRODUCT_ID_COLUMN,productId,BRANCH_ADDRESS_COLUMN,branch);
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

    //todo cool function but when will we use it?
    //todo answer: it's a private method for internal use
    private boolean checkProductExistsInChain(int productId) throws SQLException {
        String sqlQuery = String.format("Select * From %s where %s = %d  ",CHAIN_TABLE_NAME, PRODUCT_ID_COLUMN,productId);
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

    public void updateProduct(Product product,String branch) throws Exception {
        checkProductExists(product.getProductID(),branch);
        updateProductInChain(product);
        updateProductInBranch(product,branch);
        updateProductInSalesProducts(product,branch);
    }

    private void updateProductInSalesProducts(Product product, String branch) throws SQLException {
        List<Sale> sales = product.getSales();
        for(Sale sale : sales){
            if(!checkIfExistsInSalesProducts(sale.getName(),product.getProductID())){
                String query = String.format("INSERT INTO %s VALUES ('%s',%d)",PRODUCTS_SALES_TABLE,sale.getName(),product.getProductID());
                try(Connection conn = connect();
                    PreparedStatement statement = conn.prepareStatement(query)){
                    statement.executeUpdate();
                }catch (SQLException e){
                    throw new SQLException("failed to insert to salesProducts table error message : "+e.getMessage());
                }
            }
        }
    }

    private boolean checkIfExistsInSalesProducts(String sale, int productID) throws SQLException {
        return checkIfNotEmpty(String.format("SELECT * FROM %s WHERE %s = '%s' and %s = %d",PRODUCTS_SALES_TABLE,SALE_NAME_COLUMN,sale,PRODUCT_ID_COLUMN,productID));
    }

    private void updateProductInBranch(Product product, String branch) throws SQLException {
        String query = String.format("UPDATE %s SET %s = %d, %s = '%s'," +
                        "%s = %f,%s = %f,%s = %f,%s = %d,%s = %d, %s=%d, %s=%d, %s = %d, %s = %d , %s = %d" +
                        " WHERE %s = %d and  %s = '%s'",
                PRODUCTS_TABLE_NAME,PRODUCT_ID_COLUMN,product.getProductID(),BRANCH_ADDRESS_COLUMN,branch,SELLING_PRICE_COLUMN,product.getSellingPrice(),COST_PRICE_COLUMN,
                product.getCostPrice(),DEMAND,product.getDemand(),STORE_QUANTITY,product.getStoreQuantity(),
                WAREHOUSE_QUANTITY,product.getWarehouseQuantity(),STORE_SHELF,product.getStoreShelfId(),
                WAREHOUSE_SHELF,product.getWarehouseShelfId(),DELIVERY_TIME,product.getDeliveryTime(),
                SOLD_TODAY,product.getSoldToday(),DAYS_IN_STORE,product.getDaysInStore(),PRODUCT_ID_COLUMN,product.getProductID(),BRANCH_ADDRESS_COLUMN,branch);
        String res = UpdateQuery(query);
        if(res != null)
            throw new SQLException("failed to update products at branch table. error message: "+res);
    }

    private void updateProductInChain(Product product) throws Exception {
        String res = UpdateQuery(String.format("UPDATE %s SET %s = %d , %s = '%s', %s = '%s',%s = %d WHERE %s = %d",CHAIN_TABLE_NAME,
                PRODUCT_ID_COLUMN,product.getProductID(),CATEGORY,getCategoryString(product.getCategories()),
                PRODUCT_NAME_COLUMN,product.getProductName(),MANUFACTURER_COLUMN,product.getManufacturerId(), PRODUCT_ID_COLUMN,product.getProductID()));
        if(res != null)
            throw new Exception("failed to update product at chain table. error message: "+res);
    }

    private String UpdateQuery(String Query){
        try (Connection conn = this.connect();
             PreparedStatement statement = conn.prepareStatement(Query)) {
            statement.executeUpdate();
            return null;
        }
        catch (SQLException e) {
            return e.getMessage();
        }
    }

    public void deleteProduct(int productId,String branch) throws SQLException {
        if(!checkProductExistsBranch(productId,branch))
            throw new IllegalArgumentException("product doesn't exists in store");
        String query = String.format("DELETE FROM %s Where %s = %d and %s = '%s' ",PRODUCTS_TABLE_NAME, PRODUCT_ID_COLUMN,productId,BRANCH_ADDRESS_COLUMN,branch);
        runDeleteQuery(query);
    }

    public void addProduct(Product product,String branch) throws Exception {
        if(checkProductExistsBranch(product.getProductID(),branch))
            throw new IllegalArgumentException("product already exists in store");
        insertToChain(product);
        insertToBranch(product,branch);
    }

    private void insertToBranch(Product product, String branch) throws Exception {
        String sqlQuery = String.format("INSERT INTO %s Values (?,?,?,?,?,?,?,?,?,?,?,?)",PRODUCTS_TABLE_NAME);
        try(Connection conn = this.connect();
            PreparedStatement statement = conn.prepareStatement(sqlQuery)){
            statement.setInt(1,product.getProductID());
            statement.setString(2,branch);
            statement.setDouble(3,product.getSellingPrice());
            statement.setDouble(4,product.getCostPrice());
            statement.setDouble(5,product.getDemand());
            statement.setInt(6,product.getStoreQuantity());
            statement.setInt(7,product.getWarehouseQuantity());
            statement.setInt(8,product.getStoreShelfId());
            statement.setInt(9,product.getWarehouseShelfId());
            statement.setInt(10,product.getDeliveryTime());
            statement.setInt(11,product.getSoldToday());
            statement.setInt(12,product.getDaysInStore());
            statement.executeUpdate();
        }catch (Exception e){
            throw new Exception("fail to insert to branch table because: "+e.getMessage());
        }
    }

    private void insertToChain(Product product) throws Exception {
        if(checkProductExistsInChain(product.getProductID()))
            return;
        String sqlQuery = String.format("INSERT INTO %s Values (?,?,?,?)",CHAIN_TABLE_NAME);
        try(Connection conn = this.connect();
            PreparedStatement statement = conn.prepareStatement(sqlQuery)){
            statement.setInt(1,product.getProductID());
            statement.setString(2,getCategoryString(product.getCategories()));
            statement.setString(3,product.getProductName());
            statement.setInt(4,product.getManufacturerId());
            statement.executeUpdate();
        }catch (Exception e){
            throw new Exception("fail to insert to chain table because: "+e.getMessage());
        }
    }

    private String getCategoryString(List<String> category){
        if(category.size() != 3)
            throw new IllegalArgumentException("categories list must by of size 3");
        return String.join("/",category);
    }

    //todo return all product ids in db.
    //ask idan why he needs this method, and if we should return all product id's in chain or just in the specific branch
    public List<Integer> getProductIds(String branchAddress) throws SQLException {
        String sqlQuery = String.format("SELECT %s From %s Where %s = '%s' ",PRODUCT_ID_COLUMN,PRODUCTS_TABLE_NAME,BRANCH_ADDRESS_COLUMN,branchAddress);
        try (Connection conn = this.connect();
             Statement statement = conn.createStatement()){
             ResultSet res =  statement.executeQuery(sqlQuery);
            List<Integer> ids = new LinkedList<>();
            while(res.next())
                ids.add(res.getInt(PRODUCT_ID_COLUMN));
            return ids;
        } catch (SQLException e) {
            throw new SQLException("fail to connect with database");
        }

    }

    //todo get all products id with the given category.
    //tell idan this method also pull all products belong to the sub categories of the category specified
    public List<Integer> getProductIdsByCategory(List<String> categories, String branchAddress) throws SQLException {
        String sqlQuery = "SELECT "+PRODUCTS_TABLE_NAME+"."+PRODUCT_ID_COLUMN+","+PRODUCTS_TABLE_NAME+"."+BRANCH_ADDRESS_COLUMN+","+CATEGORY+" from "+PRODUCTS_TABLE_NAME+" JOIN "+CHAIN_TABLE_NAME+" on "+
                PRODUCTS_TABLE_NAME+"." +PRODUCT_ID_COLUMN+"="+CHAIN_TABLE_NAME+"."+
                PRODUCT_ID_COLUMN+" where "+BRANCH_ADDRESS_COLUMN+"='"+branchAddress+"' and UPPER("+CATEGORY+") like UPPER('"+getCategoryString(categories)+"')";
        try (Connection conn = this.connect();
             Statement statement = conn.createStatement()){
            ResultSet res = statement.executeQuery(sqlQuery);
            List<Integer> ids = new LinkedList<>();
            while(res.next())
                ids.add(res.getInt(PRODUCT_ID_COLUMN));
            return ids;
        } catch (SQLException e) {
            throw new SQLException("fail to connect with database");
        }
    }

    //todo check if there are product with the given category.
    //done returns true if there products under this category
    public boolean checkForRelatedProducts(List<String> categoryList, String branchAddress) throws SQLException {
        List<Integer> ids = getProductIdsByCategory(categoryList,branchAddress);
        if(!ids.isEmpty())
            return true;
        return false;
    }

    public String getChainProductName(int productID) throws SQLException {
        String sqlQuery = "SELECT * from "+CHAIN_TABLE_NAME+" where "+PRODUCT_ID_COLUMN+" = "+productID;
        try (Connection conn = this.connect();
             Statement statement = conn.createStatement()){
            ResultSet res = statement.executeQuery(sqlQuery);
            if(res.next())
                return res.getString(PRODUCT_NAME_COLUMN);
            throw new IllegalArgumentException("Product not in chain products");
        } catch (SQLException e) {
            throw new SQLException("fail to connect with database");
        }
    }
}
