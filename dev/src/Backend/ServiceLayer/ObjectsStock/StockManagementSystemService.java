package Backend.ServiceLayer.ObjectsStock;

import Backend.BusinessLayer.Stock.Objects.MockEmployee;
import Backend.BusinessLayer.Stock.Objects.Reporter;
import Backend.BusinessLayer.Stock.Objects.StockManagementSystem;
import Backend.BusinessLayer.Suppliers.OrderItem;
import Backend.ServiceLayer.ObjectsSupplier.SOrderItem;
import Backend.ServiceLayer.Response;
import Backend.ServiceLayer.ResponseT;

import java.util.*;

public class StockManagementSystemService {

    protected StockManagementSystem sms;


    public StockManagementSystemService() {
        sms = new StockManagementSystem("global");
    }

    public Response dailyUpdate() {
        try{
            sms.dailyUpdate();
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }//checked

    public ResponseT<String> generateStockReport(){ //checked
        try{
            return new ResponseT<>(sms.generateStockReport());
        }
        catch (Exception e) { return new ResponseT<>(e.getMessage()); }
    }

    public Response addSaleForProducts(String saleName, double discount,Collection<Integer> productIDs) {//checked
        try{
            sms.addSaleForProducts(saleName,  discount, productIDs);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public Response addSaleForCategories(String saleName, double discount, Collection<List<String>> categoriesList) {//checked
        try{
            sms.addSaleForCategories(saleName,  discount, categoriesList);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public Response removeSale(String saleName){
        try{
            sms.removeSale(saleName);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public Response addCategory(List<String> newCategory) {//checked
        try{
            sms.addCategory(newCategory);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public Response removeCategory(List<String> categories) {//checked
        try{
            sms.removeCategory(categories);
            return new Response();
        }
        catch (Exception e) {
            return new Response(e.getMessage()); }
    }

    public ResponseT<String> generatePeriodicStockReport(Collection<List<String>> categoriesList) {//checked
        try{ return new ResponseT<>( sms.generatePeriodicStockReport(categoriesList)); }
        catch (Exception e) { return new ResponseT<>(e.getMessage()); }
    }

    public Response moveProductsToDefectives(int productID, String location, String status, int amount){
        try{
            sms.moveProductsToDefectives(productID,location,status,amount);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public ResponseT<String> generateDefectivesReport(){ //checked
        try{ return new ResponseT<>( sms.generateDefectivesReport()); }
        catch (Exception e) { return new ResponseT<>(e.getMessage()); }
    }

    public ResponseT<String> generateExpiredReport(){ //checked
        try{ return new ResponseT<>( sms.generateExpiredReport()); }
        catch (Exception e) { return new ResponseT<>(e.getMessage()); }
    }

    public ResponseT<String> generateTotalDefectivesReport(){ //checked
        try{ return new ResponseT<>( sms.generateTotalDefectivesReport()); }
        catch (Exception e) { return new ResponseT<>(e.getMessage()); }
    }

    public ResponseT<String> generateShortageReport(){ //checked
        try{ return new ResponseT<>( sms.generateShortageReport()); }
        catch (Exception e) { return new ResponseT<>(e.getMessage()); }
    }

    public Response addProduct(int productId, String productName, int manufacturerId, double sellingPrice, double costPrice,
                                        int storeShelfId, int warehouseShelfId, int deliveryTime, List<String> categories) {
        try{
            sms.addProduct(productId,productName,manufacturerId,sellingPrice,costPrice,storeShelfId,warehouseShelfId,deliveryTime,categories);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public Response removeProduct(int productID) {
        try{
            sms.removeProduct(productID);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public Response addProductItems(int productID,int amount) {
        try{
            sms.addProductItems(productID,amount);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public Response removeProductItems(int productID,int amount,String location) {
        try{
            sms.removeProductItems(productID,amount,location);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public Response moveProductItemsFromWarehouse(int productID,int amount) {
        try{
            sms.moveProductItems(productID,amount,"Warehouse","Store");
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public Response moveProductItemsFromStore(int productID,int amount) {
        try{
           sms.moveProductItems(productID,amount,"Store","Warehouse");
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public  ResponseT<String> getProductLocation(int productID) {
        try{ return new ResponseT<>( sms.getProductLocation(productID)); }
        catch (Exception e) { return new ResponseT<>(e.getMessage()); }
    }

    public ResponseT<String> getProductName(int productID) {
        try{ return new ResponseT<>( sms.getProductName(productID)); }
        catch (Exception e) { return new ResponseT<>(e.getMessage()); }
    }

    public ResponseT<Double> getProductSellingPriceIncludeTopSale(int productID){
        try{ return new ResponseT<>( sms.getProductSellingPriceIncludeTopSale(productID)); }
        catch (Exception e) { return new ResponseT(e.getMessage()); }
    }

    public ResponseT<Double> getProductSellingPriceIncludeAllSales(int productID){
        try{ return new ResponseT<>( sms.getProductSellingPriceIncludeAllSales(productID)); }
        catch (Exception e) { return new ResponseT(e.getMessage()); }
    }

    public ResponseT<String> getProductSalesString(int productID) {//checked
        try{ return new ResponseT<>( sms.getProductSales(productID)); }
        catch (Exception e) { return new ResponseT<>(e.getMessage()); }
    }

    public ResponseT<Integer> getProductManufacturer(int productID) {//checked
        try{ return new ResponseT<>( sms.getProductManufacturer(productID)); }
        catch (Exception e) { return new ResponseT<>(e.getMessage(),true); }
    }

    public ResponseT<Integer> getProductTotalQuantity(int productID) {//checked
        try{ return new ResponseT<>( sms.getProductTotalQuantity(productID)); }
        catch (Exception e) { return new ResponseT<>(e.getMessage(),true); }
    }

    public ResponseT<Integer> getProductStoreQuantity(int productID) {//checked
        try{ return new ResponseT<>( sms.getProductStoreQuantity(productID)); }
        catch (Exception e) { return new ResponseT<>(e.getMessage(),true); }
    }

    public ResponseT<Integer> getProductWarehouseQuantity(int productID) {//checked
        try{ return new ResponseT<>( sms.getProductWarehouseQuantity(productID)); }
        catch (Exception e) { return new ResponseT<>(e.getMessage(),true); }
    }

    public ResponseT<Integer> getProductStoreShelfID(int productID) {//checked
        try{ return new ResponseT<>( sms.getProductStoreShelfID(productID)); }
        catch (Exception e) { return new ResponseT<>(e.getMessage(),true); }
    }

    public ResponseT<Integer> getProductWarehouseShelfID(int productID) {//checked
        try{ return new ResponseT<>( sms.getProductWarehouseShelfID(productID)); }
        catch (Exception e) { return new ResponseT<>(e.getMessage(),true); }
    }

    public ResponseT<Integer> getProductDeliveryTime(int productID) {//checked
        try{ return new ResponseT<>( sms.getProductDeliveryTime(productID)); }
        catch (Exception e) { return new ResponseT<>(e.getMessage(),true); }
    }

    public ResponseT<Double> getProductDemand(int productID) {//checked
        try{ return new ResponseT<>( sms.getProductDemand(productID)); }
        catch (Exception e) { return new ResponseT<>(e.getMessage(),true); }
    }

    public ResponseT<List<String>>  getProductCategories(int productID) {//checked
        try{ return new ResponseT<>( sms.getProductCategories(productID)); }
        catch (Exception e) { return new ResponseT<>(e.getMessage(),true); }
    }

    public Response updateProductManufacturer(int productID,int manufacturerID) {//checked
        try{
            sms.updateProductManufacturer(productID,manufacturerID);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public Response updateProductStoreShelfID(int productID,int storeShelfID) {//checked
        try{
            sms.updateProductStoreShelfID(productID,storeShelfID);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public Response updateProductWarehouseShelfID(int productID,int warehouseShelfID) {//checked
        try{
            sms.updateProductWarehouseShelfID(productID,warehouseShelfID);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public Response updateProductDeliveryTime(int productID,int deliveryTime) {//checked
        try{
            sms.updateProductDeliveryTime(productID,deliveryTime);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public Response updateProductCategories(int productID,List<String> categories) {//checked
        try{
            sms.updateProductCategories(productID,categories);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public ResponseT<String> getAllAvailableCategoriesDescription() {
        try{
            return new ResponseT<>(sms.getAllAvailableCategoriesDescription());
        }catch (Exception e){
            return new ResponseT<>(e.getMessage());
        }
    }

    public ResponseT<String> getAllAvailableSalesDescription() {
        try{
            return new ResponseT<>(sms.getAllAvailableSalesDescription());
        }catch (Exception e){
            return new ResponseT<>(e.getMessage());
        }
    }

    public void shutDown() {
        sms.shutDown(); }

    public ResponseT<String> getProductInformation(int productId) {
        try{ return new ResponseT<>( sms.getProductInformation(productId)); }
        catch (Exception e) { return new ResponseT<>(e.getMessage()); }
    }

    public Response updateSellingPrice(int productId, double price) {
        try{
            sms.updateSellingPrice(productId,price);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public ResponseT<Double> getProductSellingPrice(int productId) {
        try{ return new ResponseT<>( sms.getProductSellingPrice(productId)); }
        catch (Exception e) { return new ResponseT(e.getMessage()); }
    }

    public Response updateProductCostPrice(int productId, double price) {
        try{
            sms.updateProductCostPrice(productId,price);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public ResponseT<Double> getProductCostPrice(int productId) {
        try{ return new ResponseT<>( sms.getProductCostPrice(productId)); }
        catch (Exception e) { return new ResponseT(e.getMessage()); }
    }

    public Response updateProductName(int productId, String newName) {
        try{
            sms.updateProductName(productId,newName);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    //todo remove this after tests
    public void setTests(MockEmployee me) {
        Reporter r = sms.getReporter();
        r.AddDefectivesObserver(me);
        r.AddMinimumQuantityObserver(me);
        r.AddPeriodicStockObserver(me);
        r.AddShortageObserver(me);
    }

    public Response changeProductDemand(int productId, double demand) {
        try{
            sms.changeProductDemand(productId,demand);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public ResponseT<Integer> getProductMinimumQuantity(int productId) {
        try{ return new ResponseT<Integer>( sms.getMinimumQuantity(productId)); }
        catch (Exception e) { return new ResponseT(e.getMessage()); }
    }

    public Response setDaysInStore(int productID, int days) {
        try{
            sms.changeDaysInStore(productID,days);
            return new Response();
        }
        catch (Exception e) { return new Response(e.getMessage()); }
    }

    public StockManagementSystem getSms() {
        return sms;
    }

    public void setBranch(String branchAddress) {
        sms.setBranchAddress(branchAddress);
    }

    public ResponseT<List<SOrderItem>> getOrderItems(int orderId){
        try{
            List<OrderItem> orderItems = sms.getOrderItems(orderId);
            List<SOrderItem> SOrderItems = new LinkedList<>();
            for(OrderItem orderItem : orderItems) {
                SOrderItems.add(new SOrderItem(orderItem.getOrderID(), orderItem.getItemID(), orderItem.getAmount(), orderItem.getItemPriceAfterDiscount(), orderItem.getName()));
            }
            return new ResponseT<List<SOrderItem>>(SOrderItems);
        } catch (Exception e) {
            return new ResponseT(e.getMessage(),true);
        }
    }

    public Response completeOrder(List<SOrderItem> orderItems,int orderId){
        try{
            List<OrderItem> BorderItems = new LinkedList<>();
            for(SOrderItem sOrderItem : orderItems){
                BorderItems.add(new OrderItem(sOrderItem.getOrderID(),sOrderItem.getItemID(),sOrderItem.getAmount(),sOrderItem.getItemPriceAfterDiscount(),sOrderItem.getName()));
            }
            sms.completeOrder(BorderItems,orderId);
            return new Response();
        } catch (Exception e) {
            return new Response(e.getMessage());
        }
    }
}