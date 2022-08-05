
package TestsStock.BusinessLayer.Objects;

import Backend.BusinessLayer.Stock.Objects.StockManagementSystem;
import Backend.BusinessLayer.Suppliers.Order;
import Backend.BusinessLayer.Suppliers.OrderItem;
import Backend.BusinessLayer.Suppliers.Supplier;
import Backend.BusinessLayer.Suppliers.SupplierController;
import Backend.PersistenceLayer.SuppliersDAL.OrderDAO;
import Backend.ServiceLayer.Service;
import Backend.ServiceLayer.SuppliersAndStockService;
import org.junit.*;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class StockManagementSystemTest1 {

    SuppliersAndStockService smsSup;
    StockManagementSystem sms;
    SupplierController sc;
    OrderDAO orderDAO = new OrderDAO();

    @Before
    public void setUp() throws Exception {
        smsSup = new Service();
        sms = smsSup.getSms();
        sc = smsSup.getSupplierService().getSupplierController();
        smsSup.addNewBranch("testB","south");
        smsSup.setBranch("testB");
        sms.addCategory(Arrays.asList("testCategory", "None", "None"));
        sms.addProduct(404,"Milk",315851824,7.90,5.0,1,11,5,Arrays.asList("testCategory", "None", "None"));
    }

    @After
    public void tearDown() throws Exception {
        sms.removeProduct(404);
        sms.removeCategory(Arrays.asList("testCategory","None","None"));
        smsSup.removeBranch("testB");
        sms = null;
    }

    @Test
    public void test_addCategory() {
        try {
            Assert.assertTrue(sms.checkCategoryExist(Arrays.asList("testCategory", "None", "None")));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void removeCategory_withoutProducts() {
        try {
            sms.addCategory(Arrays.asList("testCategory1", "None", "None"));
            sms.removeCategory(Arrays.asList("testCategory1","None","None"));
            Assert.assertFalse(sms.checkCategoryExist(Arrays.asList("testCategory1","None","None")));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void addProduct_categoryExist() {
        try {
            Assert.assertEquals("Milk",sms.getProductName(404));
        } catch (Exception e) {
            Assert.fail();
        }
    }

    @Test
    public void addProduct_categoryDoesntExist() {
        try {
            sms.addProduct(404,"Milk",315851824,7.90,5.0,1,11,5,Arrays.asList("testCategory404","None","None"));
            Assert.fail();
        } catch (Exception ignored) {}
    }

    @Test
    public void removeCategory_withProducts() throws SQLException {
        try {
            sms.removeCategory(Arrays.asList("testCategory","None","None"));
            Assert.fail();
        } catch (Exception e) {
            Assert.assertTrue(sms.checkCategoryExist(Arrays.asList("testCategory","None","None")));
        }
    }

    @Test
    public void ProductSellingPrice(){
        try{
            double currentPrice = sms.getProductSellingPrice(404);
            double newPrice = 15.3;
            sms.updateSellingPrice(404,15.3);
            double newSellingPrice = sms.getProductSellingPrice(404);
            Assert.assertTrue(newSellingPrice != currentPrice);
            Assert.assertEquals(newPrice,newSellingPrice,0);
        }catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void ProductCostPrice(){
        try{
            double currentPrice = sms.getProductCostPrice(404);
            double newPrice = 15.3;
            sms.updateProductCostPrice(404,15.3);
            double newCostPrice = sms.getProductCostPrice(404);
            Assert.assertTrue(newCostPrice != currentPrice);
            Assert.assertEquals(newPrice,newCostPrice,0);
        }catch (Exception e){
            Assert.fail();
        }
    }

    @Test
    public void ProductName(){
        try {
            sms.updateProductName(404, "tnova milk");
            Assert.assertEquals("tnova milk", sms.getProductName(404));
            sms.updateProductName(404, "Milk");
        }catch (Exception e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void ProductNameFailToFindProduct(){
        try{
            sms.updateProductName(20,"tara milk");
            Assert.fail();
        }catch (Exception e){
            Assert.assertEquals("product not in chain products",e.getMessage());
        }
    }

    @Test
    public void moveToDefectives(){
        try{
            sms.addProductItems(404,1);
            sms.moveProductsToDefectives(404,"warehouse","defective",1);
            Assert.assertEquals(sms.getProductTotalQuantity(404),0);
        }catch (Exception e){
            Assert.fail();
        }
    }


    //assignment 2 tests
    @Test
    public void changeDemand(){
        try{
            sms.changeProductDemand(404,5);
            Assert.assertTrue(sms.getProductDemand(404) == 5);
        }catch (Exception e){
            Assert.fail(e.getMessage());
        }
    }

    @Test
    public void getMinQuantity(){
        try{
            sms.changeProductDemand(404,5);
            Assert.assertTrue(sms.getMinimumQuantity(404) == 25);
        }catch (Exception e){
            Assert.fail(e.getMessage());
        }
    }


    private boolean checkOrderExists(int productId,int supID) {
        List<Order> supOrders = orderDAO.selectallOrderCollect(supID);
        for(Order order : supOrders){
            List<OrderItem> items= orderDAO.getOrderItems(order.getOrderID());
            for(OrderItem oi : items){
                if(oi.getItemID() == productId)
                    return true;
            }
        }
        return false;
    }

    @Test
    public void checkMakeOrderFail() {
        try{
            sms.changeProductDemand(404,5);
            sms.addProductItems(404,30);
            sms.removeProductItems(404,20,"warehouse");
            Assert.assertTrue(checkOrderExists(404,404));
            Assert.fail();
        }catch (Exception e){
        }
    }

    @Test
    public void checkNotAlerting(){
        try{
            sc.addSupplier(404,435,"Cash","Collect","testSup","testAd", "Center");
            Supplier sup = sc.getSupplier(404);
            sup.addItem(404,454,"Milk",10);
            sms.changeProductDemand(404,5);
            sms.addProductItems(404,40);
            sms.removeProductItems(404,5,"warehouse");
            Assert.assertFalse(checkOrderExists(404,404));
            sc.removeSupplier(404);
        }catch (Exception e){
            Assert.fail(e.getMessage());
        }
    }
}

