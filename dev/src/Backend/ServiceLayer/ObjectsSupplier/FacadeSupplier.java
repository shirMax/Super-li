package Backend.ServiceLayer.ObjectsSupplier;

import Backend.BusinessLayer.Suppliers.*;
import Backend.BusinessLayer.Tools.Pair;
import Backend.ServiceLayer.Response;
import Backend.ServiceLayer.ResponseT;

import java.time.DayOfWeek;
import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class FacadeSupplier {
    private SupplierController supplierController;

    public FacadeSupplier(){
        supplierController = new SupplierController();
    }

    //add supplier
    public Response addSupplier(int supplierID, int bankAccount, String payment, String typeDelivery, String supplierName,String supplierAddress, String area)  {
        try{
            supplierController.addSupplier(supplierID, bankAccount, payment, typeDelivery,supplierName,supplierAddress, area);
            return new Response();
        }
        catch (Exception e){
            return new Response(e.getMessage());
        }
    }
    //remove supplier
    public Response removeSupplier(int supplierID){
        try{
            supplierController.removeSupplier(supplierID);
            return new Response();
        }
        catch(Exception e) {
            return new Response(e.getMessage());
        }
    }

    //getSupplier by ID
    public ResponseT<SSupplier> getSupplier(int supplierID) {
        try{
            Supplier supplier = supplierController.getSupplier(supplierID);
            SSupplier ssupplier = new SSupplier (supplier.getSupplierID(),supplier.getBankAccount(),supplier.getTypeDelivery(),supplier.getPayment(),supplier.getSupplierName(),supplier.getSupplierAddress(),supplier.getArea_enum());
            return new ResponseT<>(ssupplier);
        }
        catch(Exception e) {
            return new ResponseT<>(e.getMessage(),true);
        }
    }

    //addOrder
    public ResponseT<SOrderConst> addOrderConst(int supplierID, List<DayOfWeek> supplyConstantDays, HashMap<Integer, Integer> items,String address) {
        try{
            Order order = supplierController.addOrderConst(supplierID, supplyConstantDays, items,address);
            SOrderConst sOrder = new SOrderConst(order.getSupplierID(),order.getOrderID(),order.getPriceAfterDiscount(),((OrderConst)order).getSupplyConstantDays(),order.getAddress());
            return new ResponseT(sOrder);
        }
        catch(Exception e) {
            return new ResponseT(e.getMessage(), true);
        }
    }

    public ResponseT<SOrderCollect> addOrderCollect(int supplierID, Date supplyDate, HashMap<Integer, Integer> items,String address,Date possibleSupplyDates){
        try{
            Order order = supplierController.addOrderCollect(supplierID, supplyDate, items,address, possibleSupplyDates);
            SOrderCollect sOrder = new SOrderCollect(order.getSupplierID(),order.getOrderID(),order.getPriceAfterDiscount(),((OrderCollect)order).getArrivedDate(),((OrderCollect)order).getArrivedDate(),order.getAddress(),((OrderCollect)order).getOrderState());
            return new ResponseT(sOrder);
        }
        catch(Exception e) {
            return new ResponseT(e.getMessage(), true);
        }
    }


    //add contact
    public Response addContact(int supplierID,int contactID, String name, String phone) {
        try{
            supplierController.addContact(supplierID, contactID, name, phone);
            return new Response();
        }
        catch(Exception e) {
            return new Response(e.getMessage());
        }
    }

    //remove contact
    public Response removeContact(int supplierID,int contactID) {
        try{
            supplierController.removeContact(supplierID,contactID);
            return new Response();
        }
        catch(Exception e) {
            return new Response(e.getMessage());
        }
    }

    //add item to the contract
    public ResponseT<SItem> addItem(int supplierID,int itemID,int catalogID, double price)  {
        try{
            Item item = supplierController.addItem(supplierID,itemID,catalogID, price);
            return new ResponseT(new SItem(item.getItemID(),item.getCatalogID(),item.getName(),item.getPrice()));
        }
        catch(Exception e) {
            return new ResponseT(e.getMessage(), true);
        }
    }

    //remove item in the contract
    public Response removeItem(int supplierID,int itemID){
        try{
            supplierController.removeItem(supplierID, itemID);
            return new Response();
        }
        catch(Exception e) {
            return new Response(e.getMessage());
        }
    }

    //add new discount to the contract
    public Response addDiscount(int supplierID,int itemID, int amount, int discount){
        try{
            supplierController.addDiscount(supplierID, itemID, amount, discount);
            return new Response();
        }
        catch(Exception e) {
            return new Response(e.getMessage());
        }
    }

    //get items from supplier
    public ResponseT<List<SItem>> getItemsForSupplier(int supplierID){
        try{
            List<Item> items = supplierController.getItemsForSupplier(supplierID);
            List<SItem> Sitems = new LinkedList<>();
            for(Item item : items){
                Sitems.add(new SItem(item.getItemID(),item.getCatalogID(),item.getName(),item.getPrice()));
            }
            return new ResponseT<>(Sitems);
        }
        catch(Exception e) {
            return new ResponseT<>(e.getMessage(),true);
        }
    }

    //get orders from supplier
    public ResponseT<List<Order>> getOrdersFromSupplier(int supplierID){
        try{
            List<Order> orders = supplierController.getOrdersFromSupplier(supplierID);
            return new ResponseT<>(orders);
        }
        catch (Exception e){
            return new ResponseT(e.getMessage(), true);
        }
    }

    public ResponseT<List<Order>> getNotArrivedOrdersFromSupplier(int supplierID){
        try{
            List<Order> orders = supplierController.getNotArrivedOrdersFromSupplier(supplierID);
            return new ResponseT<>(orders);
        }
        catch (Exception e){
            return new ResponseT(e.getMessage(), true);
        }
    }

    //set contact phone
    public Response setContactPhone(int supplierID, int contactID, String phone){
        try{
            supplierController.setContactPhone(supplierID, contactID, phone);
            return new Response();
        }
        catch(Exception e) {
            return new Response(e.getMessage());
        }
    }

    //set contact name
    public Response setContactname(int supplierID, int contactID, String name){
        try{
            supplierController.setContactname(supplierID, contactID, name);
            return new Response();
        }
        catch(Exception e) {
            return new Response(e.getMessage());
        }
    }

    public ResponseT<List<SSupplier>> getSuppliers(){
        try{
            List<Supplier> suppliers = supplierController.getSuppliers();
            List<SSupplier> ssupplier = new LinkedList<>();
            for(Supplier s : suppliers)
                ssupplier.add(new SSupplier(s.getSupplierID(),s.getBankAccount(),s.getTypeDelivery(),s.getPayment(),s.getSupplierName(),s.getSupplierAddress(),s.getArea_enum()));
            return new ResponseT<>(ssupplier);
        }
        catch (Exception e){
            return new ResponseT<>(e.getMessage(),true);
        }
    }
    public Response addItemsToConstOrder(int supplierID,int orderID, HashMap<Integer,Integer> items){
        try {
            supplierController.addItemsToConstOrder(supplierID, orderID, items);
            return new Response();
        }
        catch (Exception e) {
            return new Response(e.getMessage());
        }
    }
    public Response removeItemsToConstOrder(int supplierID,int orderID, HashMap<Integer,Integer> items){
        try{
            supplierController.removeItemsToConstOrder(supplierID, orderID, items);
            return new Response();
        }
        catch (Exception e) {
            return new Response(e.getMessage());
        }
    }

    public SupplierController getSupplierController() {
        return supplierController;
    }

//    public Response changeOrderArrivedStatus(int orderID,int supplierID){
//        try{
//            supplierController.changeOrderArrivedStatus(orderID, supplierID);
//            return new Response();
//        }
//        catch (Exception e) {
//            return new Response(e.getMessage());
//        }
//    }

    public ResponseT<List<SContact>> getContacts(int supplierID) {
        try {
            List<Contact> contacts = supplierController.getContacts(supplierID);
            List<SContact> sContacts = new LinkedList<>();
            for(Contact c : contacts)
                sContacts.add(new SContact(c.getContactID(), c.getName(), c.getPhone()));
            return new ResponseT<>(sContacts);
        }
        catch (Exception e) {
            return new ResponseT<>(e.getMessage(),true);
        }
    }

    public ResponseT<SContract> getContract(int supplierID) {
        try {
            Contract contract = supplierController.getContract(supplierID);
            SContract sContract = new SContract(contract);
            return new ResponseT<>(sContract);
        }
        catch (Exception e) {
            return new ResponseT<>(e.getMessage(),true);
        }
    }

    public ResponseT<List<Pair<Integer,String>>> getItems() {
        try {
            return new ResponseT<>(supplierController.getItems());
        }
        catch (Exception e) {
            return new ResponseT<>(e.getMessage(),true);
        }
    }

    public Response addAutomaticOrderConst(List<DayOfWeek> constantDays,HashMap<Integer,Integer> items, String superAddress) {
        try{
            supplierController.addAutomaticOrderConst(constantDays,items,superAddress);
            return new Response();
        }
        catch (Exception e){
            return new Response(e.getMessage());
        }
    }
    public Response addAutomaticOrderCollect(HashMap<Integer,Integer> items, Date current, String superAddress) {
        try{
            supplierController.addAutomaticOrderCollect(items,current,superAddress);
            return new Response();
        }
        catch (Exception e){
            return new Response(e.getMessage());
        }
    }

    public Response sendConstOrders() {
        try{
            supplierController.sendConstOrders();
            return new Response();
        }
        catch (Exception e){
         return new Response(e.getMessage());
        }
    }

    public Response cancelOrder(int orderID) {
        try{
            supplierController.cancelWaitingOrder(orderID);
            return new Response();
        }
        catch (Exception e){
            return new Response(e.getMessage());
        }
    }

    public ResponseT<List<SOrder>> getNotArrivedOrders() {
        try{
            List<Order> orders = supplierController.getNotArrivedOrders();
            List<SOrder> sOrders = new LinkedList<>();
            for(Order order : orders)
                sOrders.add(new SOrderCollect(order.getSupplierID(),order.getOrderID(),order.getPriceAfterDiscount(),((OrderCollect)order).getOrderDate(),((OrderCollect) order).getArrivedDate(),order.getAddress(),((OrderCollect) order).getOrderState()));
            return new ResponseT<>(sOrders);
        }
        catch (Exception e){
            return new ResponseT(e.getMessage(), true);
        }
    }

//    public Response sendingNotDelverOrder(int orderID, List<Date> dates) {
//        try{
//            supplierController.sendingNotDeliverOrder(orderID, dates);
//            return new Response();
//        }
//        catch (Exception e){
//            return new Response(e.getMessage());
//        }
//    }
}
