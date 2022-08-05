package Backend.ServiceLayer;

import Backend.BusinessLayer.BranchLoader;
import Backend.BusinessLayer.Suppliers.Order;
import Backend.BusinessLayer.Tools.Pair;
import Backend.ServiceLayer.ObjectsStock.StockManagementSystemService;
import Backend.ServiceLayer.ObjectsSupplier.*;

import java.time.DayOfWeek;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;

public class SuppliersAndStockService extends StockManagementSystemService {

    protected FacadeSupplier supplierService;
    private BranchLoader branchLoader = new BranchLoader();

    public SuppliersAndStockService() {supplierService = new FacadeSupplier();}

    //add supplier
    public Response addSupplier(int supplierID, int bankAccount, String payment, String typeDelivery, String supplierName,String supplierAddress, String area)  {
       return supplierService.addSupplier(supplierID,bankAccount,payment,typeDelivery,supplierName,supplierAddress, area);
    }
    //remove supplier
    public Response removeSupplier(int supplierID){
        return supplierService.removeSupplier(supplierID);
    }

    //getSupplier by ID
    public ResponseT<SSupplier> getSupplier(int supplierID) {
        return supplierService.getSupplier(supplierID);
    }

    //addOrder
    public ResponseT<SOrderConst> addOrderConst(int supplierID, List<DayOfWeek> supplyConstantDays, HashMap<Integer, Integer> items, String address) {
        return supplierService.addOrderConst(supplierID,supplyConstantDays,items,address);
    }

    public ResponseT<SOrderCollect> addOrderCollect(int supplierID, Date supplyDate, HashMap<Integer, Integer> items, String address,Date  possibleSupplyDates){
        return supplierService.addOrderCollect(supplierID,supplyDate,items,address, possibleSupplyDates);
    }

    //add contact
    public Response addContact(int supplierID,int contactID, String name, String phone) {
        return supplierService.addContact(supplierID,contactID,name,phone);
    }

    //remove contact
    public Response removeContact(int supplierID,int contactID) {
        return supplierService.removeContact(supplierID,contactID);
    }

    //add item to the contract
    public ResponseT<SItem> addItem(int supplierID,int itemID,int catalogID, double price)  {
        return supplierService.addItem(supplierID,itemID,catalogID,price);
    }

    //remove item in the contract
    public Response removeItem(int supplierID,int itemID){
        return supplierService.removeItem(supplierID,itemID);
    }

    //add new discount to the contract
    public Response addDiscount(int supplierID,int itemID, int amount, int discount){
        return supplierService.addDiscount(supplierID,itemID,amount,discount);
    }

    //get items from supplier
    public ResponseT<List<SItem>> getItemsForSupplier(int supplierID){
        return supplierService.getItemsForSupplier(supplierID);
    }


    //get orders from supplier
    public ResponseT<List<Order>> getOrdersFromSupplier(int supplierID){
        return supplierService.getOrdersFromSupplier(supplierID);
    }

    public ResponseT<List<Order>> getNotArrivedOrdersFromSupplier(int supplierID){
        return supplierService.getNotArrivedOrdersFromSupplier(supplierID);
    }

    //set contact phone
    public Response setContactPhone(int supplierID, int contactID, String phone){
        return supplierService.setContactPhone(supplierID,contactID,phone);
    }

    //set contact name
    public Response setContactname(int supplierID, int contactID, String name){
        return supplierService.setContactname(supplierID,contactID,name);
    }

    public ResponseT<List<SSupplier>> getSuppliers(){
        return supplierService.getSuppliers();
    }
    public Response addItemsToConstOrder(int supplierID,int orderID, HashMap<Integer,Integer> items){
        return supplierService.addItemsToConstOrder(supplierID,orderID,items);
    }
    public Response removeItemsToConstOrder(int supplierID,int orderID, HashMap<Integer,Integer> items){
        return supplierService.removeItemsToConstOrder(supplierID,orderID,items);
    }


    public ResponseT<List<String>> getAllBranches(){
        try{
            return new ResponseT<>(branchLoader.getAllBranches());
        }catch (Exception e){
            return new ResponseT<>(e.getMessage(),true);
        }
    }

    public Response addNewBranch(String branchAddress,String area) {
        try{
            branchLoader.addNewBranch(branchAddress,area);
            return new Response();
        }catch (Exception e){
            return new Response(e.getMessage());
        }
    }

//    public Response changeOrderArrivedStatus(int orderID,int supplierID) throws Exception {
//        return supplierService.changeOrderArrivedStatus(orderID, supplierID);
//    }

    public ResponseT<List<SContact>> getContacts(int supplierID) {
        return supplierService.getContacts(supplierID);
    }

    public ResponseT<SContract> getContract(int supplierID) {
        return supplierService.getContract(supplierID);
    }

    public ResponseT<List<Pair<Integer,String>>> getItems() {
        return supplierService.getItems();
    }

    public Response addAutomaticOrderConst(List<DayOfWeek> constantDays, HashMap<Integer,Integer> items, String superAddress) {
        return supplierService.addAutomaticOrderConst(constantDays, items,superAddress);
    }
    public Response addAutomaticOrderCollect(HashMap<Integer,Integer> items, Date current, String superAddress) {
        return supplierService.addAutomaticOrderCollect(items,current,superAddress);
    }
    public Response sendConstOrders() {
        return supplierService.sendConstOrders();
    }

    public FacadeSupplier getSupplierService() {
        return supplierService;
    }
    public Response cancelOrder(int orderID){
        return supplierService.cancelOrder(orderID);
    }

    public ResponseT<List<SOrder>> getNotArrivedOrders() {
        return supplierService.getNotArrivedOrders();
    }

    public Response removeBranch(String branchName) {
        try{
            branchLoader.removeBranch("branchName");
            return new Response();
        }catch(Exception e){
            return new Response(e.getMessage());
        }

    }


//    public Response sendingNotDeliverOrder(int orderID, List<Date> dates) {
//        return supplierService.sendingNotDelverOrder(orderID, dates);
//    }
}
