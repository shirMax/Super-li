package Backend.BusinessLayer.Suppliers;

import Backend.BusinessLayer.CallBacks.*;
import Backend.BusinessLayer.Deliveries.Delivery;
import Backend.BusinessLayer.Interfaces.CompleteDeliveryCallback;
import Backend.BusinessLayer.Interfaces.CompleteOrderCallback;
import Backend.BusinessLayer.Interfaces.GetProductNameCallback;
import Backend.BusinessLayer.Tools.Pair;

import Backend.PersistenceLayer.BranchesDAO;
import Backend.PersistenceLayer.SuppliersDAL.*;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;


import javax.swing.plaf.SeparatorUI;
import java.time.DayOfWeek;
import java.util.*;
import java.sql.Date;

public class SupplierController {
    private ItemDAO itemDAO;
    private OrderDAO orderDAO;
    private SupplierDAO supplierDAO;
    public static int orderIDGenerator = 0;
    private CompleteDeliveryCallback completeDeliveryCallback;
    private GetProductNameCallback getProductNameCallback;
    private AddBrunchToDeliveryCallBack addBrunchToDeliveryCallBack;
    private AddProductToSiteCallBack addProductToSiteCallBack;
    private AddSupplierToDeliveryCallBack addSupplierToDeliveryCallBack;
    private OrderDeliveryCallBack orderDeliveryCallBack;
    private RemoveDeliveryCallBack removeDeliveryCallBack;
    private UpdateProductInSiteCallBack updateProductInSiteCallBack;

    public SupplierController(){
        new CreateDBSuppliers();
        itemDAO = new ItemDAO();
        orderDAO = new OrderDAO();
        supplierDAO = new SupplierDAO(new ContactDAO(),itemDAO,orderDAO, new BranchesDAO());
        loadOrderIDGenerator();
    }

    //add supplier
    public void addSupplier(int supplierID, int bankAccount, String payment, String typeDelivery, String SupplierName, String supplierAddress,String area) throws Exception {
        supplierDAO.insertSupplier(supplierID,bankAccount,payment,typeDelivery,SupplierName,supplierAddress,area);

    }
    //remove supplier
    public void removeSupplier(int supplierID) throws Exception {
        supplierDAO.removeSupplier(supplierID);
    }
    public void loadOrderIDGenerator(){
        orderIDGenerator = orderDAO.loadOrderIDGenerator();
    }

    //getSupplier by ID
    public Supplier getSupplier(int supplierID) throws Exception {
        Supplier sup = supplierDAO.getSupplier(supplierID);
        sup.setCallbacks(addBrunchToDeliveryCallBack, addProductToSiteCallBack, orderDeliveryCallBack, addSupplierToDeliveryCallBack, completeDeliveryCallback);
        return sup;
    }

    private Supplier validateSupplierExists(int supplierID) throws Exception {
        Supplier supplier = getSupplier(supplierID);
        if(supplier == null)
            throw new Exception("supplier doesn't exist");
        return supplier;
    }

    //addOrder
    public Order addOrderConst(int supplierID, List<DayOfWeek> supplyConstantDays, HashMap<Integer, Integer> items,String superAddress) throws Exception {
        Supplier supplier = validateSupplierExists(supplierID);
        checkIfNotConst(supplier);
        return supplier.addOrderConst(supplyConstantDays, items,superAddress, orderIDGenerator++);
    }
    private void checkIfNotConst(Supplier supplier) throws Exception {
        if(supplier.getTypeDelivery() != Supplier.typeDelivery.Const)
            throw new Exception("not match type delivery-> const");
    }

    public Order addOrderCollect(int supplierID, Date orderDate,HashMap<Integer, Integer> items,String superAddress,Date possibleSupplyDates) throws Exception {
        Supplier supplier = validateSupplierExists(supplierID);
        checkIfConst(supplier);
        return supplier.addOrderCollect(items, superAddress, orderDate, orderIDGenerator++,possibleSupplyDates);
    }
    private void checkIfConst(Supplier supplier) throws Exception {
        if(supplier.getTypeDelivery() == Supplier.typeDelivery.Const)
            throw new Exception("not match type delivery->= collect or order");
    }


//    public void changeOrderArrivedStatus(int orderID,int supplierID) throws Exception {
//        Supplier supplier = getSupplier(supplierID);
//        List<OrderItem> orderItems = supplier.getOrderItems(orderID);
//        supplier.updateArrivedDate(orderID);
//        List<Date> dates = new LinkedList<>();
//        dates.add(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
//        Delivery delivery = orderDeliveryCallBack.call(dates,orderID);
//    }


    //add contact
    public void addContact(int supplierID,int contactID, String name, String phone) throws Exception {
        Supplier supplier = validateSupplierExists(supplierID);
        supplier.addContact(contactID,name,phone);
    }

    //remove contact
    public void removeContact(int supplierID,int contactID) throws Exception {
        Supplier supplier = validateSupplierExists(supplierID);
        supplier.removeContact(contactID);
    }

    //add item to the contract
    public Item addItem(int supplierID,int itemID,int catalogID, double price) throws Exception {
        Supplier supplier = validateSupplierExists(supplierID);
        String name = getProductNameCallback.getProductName(itemID);
        return supplier.addItem(itemID,catalogID,name,price);
    }

    //remove item in the contract
    public void removeItem(int supplierID,int itemID) throws Exception {
        Supplier supplier = validateSupplierExists(supplierID);
        supplier.removeItem(itemID);
    }

    //add new discount to the contract
    public void addDiscount(int supplierID,int itemID, int amount, int discount) throws Exception {
        Supplier supplier = validateSupplierExists(supplierID);
        supplier.addDiscount(itemID,amount,discount);
    }

    //get items from supplier
    public List<Item> getItemsForSupplier(int supplierID) throws Exception {
        Supplier supplier = validateSupplierExists(supplierID);
        return supplier.getItemsForSupplier();
    }

    //get orders from supplier
    public List<Order> getOrdersFromSupplier(int supplierID) throws Exception {
        Supplier supplier = validateSupplierExists(supplierID);
        if(supplier.getTypeDelivery() == Supplier.typeDelivery.Const)
            return supplier.getOrdersConst();
        return supplier.getOrdersCollect();
    }
    public List<Order> getNotArrivedOrdersFromSupplier(int supplierID) throws Exception {
        Supplier supplier = validateSupplierExists(supplierID);
        return supplier.getNotArrivedOrders();
    }


    //set contact phone
    public void setContactPhone(int supplierID, int contactID, String phone) throws Exception {
        Supplier supplier = validateSupplierExists(supplierID);
        supplier.setContactPhone(contactID,phone);
    }

    //set contact name
    public void setContactname(int supplierID, int contactID, String name) throws Exception {
        Supplier supplier = validateSupplierExists(supplierID);
        supplier.setContactName(contactID,name);
    }

    public void makeCollectOrder(Integer key, Integer value,String address) throws Exception {
        List<Supplier> suppliers = selectCollectSuppliers();
        List<Supplier> CollectSuppliers = findCollectSuppliers(suppliers);
        Pair<Supplier,HashMap<Integer,Integer>> supplierAndItems = findMinSupplier(key,value,CollectSuppliers);
        supplierAndItems.getFirst().addAutomaticOrderCollect(supplierAndItems.getSecond(),address,new java.sql.Date(Calendar.getInstance().getTime().getTime()),orderIDGenerator++);
    }

    public Pair<Supplier,HashMap<Integer,Integer>> findMinSupplier(int itemID, int amount, List<Supplier> suppliers) throws Exception {
        if (!suppliers.isEmpty()) {
            double min = Double.POSITIVE_INFINITY;
            Supplier minSupplier = null;
            for (Supplier s : suppliers) {
                if (s.calculatePrice(itemID, amount) != -1) {
                    double calculatePrice = s.calculatePrice(itemID, amount);
                    if (min > calculatePrice) {
                        min = calculatePrice;
                        minSupplier = s;
                    }
                }
            }
            if(minSupplier == null)
                throw new Exception("there is no supplier for this product");
            HashMap<Integer, Integer> items = new HashMap<>();
            items.put(itemID, amount);
            return new Pair<>(minSupplier,items);
        }
        else
            throw new Exception("there are no suppliers that supply those products yet!");
    }

    public void addItemsToConstOrder(int supplierID, int orderID, HashMap<Integer, Integer> items) throws Exception {
        Supplier supplier = validateSupplierExists(supplierID);
        supplier.addItemsToConstOrder(orderID,items);
    }
    public void removeItemsToConstOrder(int supplierID, int orderID, HashMap<Integer, Integer> items) throws Exception {
        Supplier supplier = validateSupplierExists(supplierID);
        supplier.removeItemsToConstOrder(orderID,items);
    }

    public void setCompleteDeliveryCallback(CompleteDeliveryCallback completeDeliveryCallback) {
        this.completeDeliveryCallback = completeDeliveryCallback;
    }

    public void setGetProductNameCallback(GetProductNameCallback getProductNameCallback) {
        this.getProductNameCallback = getProductNameCallback;
    }
    public List<Supplier> selectCollectSuppliers(){
        List<Supplier> supplierList = getSuppliers();
        List<Supplier> suppliersCollectList = new LinkedList<>();
        for(Supplier s : supplierList)
            if(s.getTypeDelivery() != Supplier.typeDelivery.Const)
                suppliersCollectList.add(s);
        return suppliersCollectList;
    }
    public List<Supplier> getSuppliers() {
        List<Supplier> list = supplierDAO.selectSuppliers();
        for(Supplier s : list){
            s.setCallbacks(addBrunchToDeliveryCallBack, addProductToSiteCallBack, orderDeliveryCallBack, addSupplierToDeliveryCallBack, completeDeliveryCallback);
        }
        return list;
    }

    public List<Contact> getContacts(int supplierID) throws Exception {
        Supplier supplier = validateSupplierExists(supplierID);
        return supplier.getContacts();
    }

    public Contract getContract(int supplierID) throws Exception {
        Supplier supplier = validateSupplierExists(supplierID);
        return supplier.getContract();
    }

    public List<Pair<Integer,String>> getItems() {
        return itemDAO.getItems();
    }

    public void addAutomaticOrderConst(List<DayOfWeek> constantDays, HashMap<Integer,Integer> items, String superAddres) throws Exception {
        List<Supplier> suppliers = selectConstSuppliers();
        for(Integer itemID : items.keySet()) {
            Pair<Supplier, HashMap<Integer, Integer>> supplierAndItems = findMinSupplier(itemID, items.get(itemID), suppliers);
            supplierAndItems.getFirst().addOrderConst(constantDays,supplierAndItems.getSecond(), superAddres, orderIDGenerator++);
        }
    }

    public List<Supplier> selectConstSuppliers() {
        List<Supplier> supplierList = getSuppliers();
        List<Supplier> suppliersConstList = new LinkedList<>();
        for(Supplier s : supplierList)
            if(s.getTypeDelivery() == Supplier.typeDelivery.Const)
                suppliersConstList.add(s);
        return suppliersConstList;
    }
    public void addAutomaticOrderCollect(HashMap<Integer,Integer> items, Date current, String superAddress) throws Exception {
        List<Supplier> suppliers = selectCollectSuppliers();
        List<Supplier> CollectSuppliers = findCollectSuppliers(suppliers);
        for(Integer itemID : items.keySet()) {
            Pair<Supplier, HashMap<Integer, Integer>> supplierAndItems = findMinSupplier(itemID, items.get(itemID), CollectSuppliers);
            supplierAndItems.getFirst().addAutomaticOrderCollect(supplierAndItems.getSecond(), superAddress, current,orderIDGenerator++);
        }
    }

    private List<Supplier> findCollectSuppliers(List<Supplier> suppliers) {
        List<Supplier> collectSuppliers = new LinkedList<>();
        for(Supplier supplier : suppliers)
            if(supplier.getTypeDelivery() == Supplier.typeDelivery.Collect)
                collectSuppliers.add(supplier);
        return collectSuppliers;
    }

    public void sendConstOrders() throws Exception {
        List<Supplier> suppliers = getSuppliers();
        for(Supplier supplier : suppliers)
            supplier.sendOrderConst();
    }
    public void cancelWaitingOrder(int orderID) throws Exception {
        int deliveryID = orderDAO.removeWaitingOrder(orderID);
        removeDeliveryCallBack.call(deliveryID);
    }
//    public void sendingNotDeliverOrder(int orderID, List<Date> dates) throws Exception {
//        Order order = orderDAO.getOrderCollect(orderID);
//        if(order == null)
//            throw new Exception("order does not exist");
//        Delivery delivery = orderDeliveryCallBack.call(dates,orderID);
//        if(delivery != null) {
//            int deliveryID = delivery.getId();
//            Date deliveryDate = (Date) delivery.getDeliveryDate();
//            orderDAO.updateOrderCollect(orderID, deliveryDate, deliveryID, OrderCollect.OrderState.InProgress);
//            //addSupplierToDeliveryCallBack.call(deliveryID, supplierName, contact.getName(),contact.getPhone(), area_enum, supplierAddress);
//        }
//        }

    public List<Order> getNotArrivedOrders() {
        List<Order> allOrders =  orderDAO.selectallOrderCollect();
        List<Order> notArrived = new LinkedList<>();
        for(Order o : allOrders){
            if(((OrderCollect)o).checkIfNotArrived())
                notArrived.add(o);
        }
        return notArrived;
    }

    public void setOrderDeliveryCallBack(OrderDeliveryCallBack orderDeliveryCallBack){
        this.orderDeliveryCallBack = orderDeliveryCallBack;
    }

    public void setAddSupplierToDeliveryCallBack(AddSupplierToDeliveryCallBack addSupplierToDeliveryCallBack){
        this.addSupplierToDeliveryCallBack = addSupplierToDeliveryCallBack;
    }

    public void setAddBrunchToDeliveryCallBack(AddBrunchToDeliveryCallBack addBrunchToDeliveryCallBack){
        this.addBrunchToDeliveryCallBack = addBrunchToDeliveryCallBack;
    }

    public void setAddProductToSiteCallBack(AddProductToSiteCallBack addProductToSiteCallBack){
        this.addProductToSiteCallBack = addProductToSiteCallBack;
    }

    public void setRemoveDeliveryCallBack(RemoveDeliveryCallBack removeDeliveryCallBack){
        this.removeDeliveryCallBack = removeDeliveryCallBack;
    }

    public void setUpdateProductInSiteCallBack(UpdateProductInSiteCallBack updateProductInSiteCallBack){
        this.updateProductInSiteCallBack = updateProductInSiteCallBack;
    }


    public void updateOrderProductQuantity(int orderID, int itemNum, int quantity) throws Exception {
        Order order = validateOrder(orderID);
        Supplier supplier = validateSupplierExists(order.getSupplierID());
        supplier.updateOrderProductQuantity(orderID, itemNum, quantity, order);
    }

    public void cancelOrderCallback(int orderID) throws Exception {
        orderDAO.removeOrder(orderID);
    }
    public void updateOrderDate(int orderID, Date date) throws Exception {
        validateOrder(orderID);
        orderDAO.updateOrderCollect(orderID,date, OrderCollect.OrderState.InProgress);
    }
    private Order validateOrder(int orderID) throws Exception {
        Order order = orderDAO.getOrderCollect(orderID);
        if(order == null)
            throw new Exception("there is no order with the order id: "+ orderID);
        return order;
    }

    public void completeOrderCallback(int orderID, List<OrderItem> orderProducts) throws Exception {
        Order order = validateOrder(orderID);
        Supplier supplier = validateSupplierExists(order.getSupplierID());
        for(OrderItem orderItem : orderProducts)
            supplier.updateOrderProductQuantity(orderID,orderItem.getItemID(), orderItem.getAmount(), order);
        Date today = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        supplier.updateOrderCollect(orderID, today, OrderCollect.OrderState.Done);
    }

    public List<OrderItem> getOrderItems(int orderID,String branch){
        return orderDAO.getOrderItems(orderID);
    }

}
