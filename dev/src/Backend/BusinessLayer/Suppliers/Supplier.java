package Backend.BusinessLayer.Suppliers;

import Backend.BusinessLayer.CallBacks.*;
import Backend.BusinessLayer.Deliveries.Delivery;

import Backend.BusinessLayer.Deliveries.Enums.Area_Enum;

import Backend.BusinessLayer.Interfaces.CompleteDeliveryCallback;
import Backend.PersistenceLayer.BranchesDAO;
import Backend.PersistenceLayer.SuppliersDAL.ContactDAO;
import Backend.PersistenceLayer.SuppliersDAL.ItemDAO;
import Backend.PersistenceLayer.SuppliersDAL.OrderDAO;
import Frontend.PresentationLayer.Model.MSupplier;


import java.time.DayOfWeek;
import java.util.*;
import java.sql.Date;


public class Supplier {
    private Area_Enum area_enum;

    public enum typeDelivery{Collect,Order,Const}
    public enum payment{Cash,BankTransfer}
    private int supplierID;
    private String supplierName;
    private int bankAccount;
    private typeDelivery typeDelivery;
    private payment payment;
    private OrderDAO orderDAO;
    private ContactDAO contactsDAO;
    private String supplierAddress;
    private ItemDAO itemDAO;
    private BranchesDAO branchesDAO;
    private AddSupplierToDeliveryCallBack addSupplierToDeliveryCallBack;
    private AddProductToSiteCallBack addProductToSiteCallBack;
    private OrderDeliveryCallBack orderDeliveryCallBack;
    private CompleteDeliveryCallback completeDeliveryCallback;
    private AddBrunchToDeliveryCallBack addBrunchToDeliveryCallBack;
    public Supplier(int supplierID, String supplierName, String supplierAddress, int bankAccount , payment payment, typeDelivery typeDelivery, Area_Enum area_enum, OrderDAO orderDAO, ItemDAO itemDAO, ContactDAO contactsDAO, BranchesDAO branchesDAO) {
        this.supplierID = supplierID;
        this.bankAccount = bankAccount;
        this.typeDelivery = typeDelivery;
        this.payment = payment;
        this.supplierName = supplierName;
        this.supplierAddress = supplierAddress;
        this.contactsDAO = contactsDAO;
        this.orderDAO = orderDAO;
        this.itemDAO = itemDAO;
        this.area_enum = area_enum;
        this.branchesDAO = branchesDAO;
    }

    public List<Contact> getContacts() {
        return contactsDAO.getContacts(supplierID);
    }

    public double calculatePrice(int key, int value) {
        try {
            Item item;
            HashMap<Integer,Integer> amountAndDiscount;
            item = itemDAO.checkIfItemInCatalog(supplierID,key); //gets the amount of order items and the item and check if the item in the catalog
            amountAndDiscount = itemDAO.getItemToAmountAndDiscount(supplierID,key); //gets the amount and discount from the contract
            return calculatePrice(key,item.getPrice(),amountAndDiscount);
        }
        catch (Exception e)
        {
            return -1;
        }
    }
    private double calculatePrice(int amount, double price, HashMap<Integer,Integer> amountAndDiscount){
        double itemPrice = price * amount;
        if(amountAndDiscount.keySet().size() == 0 )
            return itemPrice;
        int key = amountAndDiscount.keySet().stream()
                .filter(x -> x <= amount).max(Comparator.naturalOrder()).orElse(-1);
        if(key==-1)
            return itemPrice;
        return itemPrice - (itemPrice * (amountAndDiscount.get(key) / 100.0));
    }


    public String getSupplierAddress() {
        return supplierAddress;
    }

    public typeDelivery getTypeDelivery(){return this.typeDelivery;}

//    public void removeOrdersForSupplier() throws Exception {
//        if(typeDelivery == typeDelivery.Const)
//             orderDAO.removeOrdersForSupplierConst(supplierID);
//        else orderDAO.removeOrdersForSupplierCollect(supplierID);
//    }

    public Order addOrderConst(List<DayOfWeek> supplyConstantDays, HashMap<Integer, Integer> items, String superAddress,int orderID) throws Exception {
        checkBranch(superAddress);
        HashMap<Item, Integer> itemAndAmount = new HashMap<>();
        HashMap<Integer,HashMap<Integer,Integer>> itemToAmountAndDiscount = new HashMap<>();
        prepareItemsForOrder(items, itemAndAmount, itemToAmountAndDiscount);
        Order orderConst = orderDAO.getOrderConst(orderID);
        validateOrderIsNull(orderConst);
        String days = "";
        for (DayOfWeek day : supplyConstantDays)
            days = days.equals("")? day.toString() : days + " "+day;
        orderConst = new OrderConst(supplierID, orderID, supplyConstantDays, superAddress, itemAndAmount, itemToAmountAndDiscount);
        orderDAO.insertOrderConst(supplierID, orderID, orderConst.getPriceAfterDiscount(), days, superAddress);
        for(Item item : itemAndAmount.keySet())
            orderDAO.insertOrderItems(item.getItemID(), orderConst.getOrderID(), itemAndAmount.get(item), orderConst.calculatePrice(itemAndAmount.get(item),item.getPrice(),itemToAmountAndDiscount.get(item.getItemID())),item.getName());
        return orderConst;
    }


    public double getOrderPrice(int orderID) throws Exception {
        if(typeDelivery == typeDelivery.Const) {
            Order orderConst = orderDAO.getOrderConst(orderID);
            if (orderConst != null)
                return orderConst.getPriceAfterDiscount();
            else
                throw new Exception("there is no order like that");
        }
        else {
            Order orderCollect = orderDAO.getOrderCollect(orderID);
            if(orderCollect != null)
                return orderCollect.getPriceAfterDiscount();
            else
                throw new Exception("there is no order like that");
        }
    }

    private void checkBranch(String superAddress) throws Exception {
        if(!branchesDAO.checkIfBranchExists(superAddress))
            throw new Exception("branch does not exists!");
    }
    public void updateOrderProductQuantity(int orderID, int itemNum, int quantity, Order order) throws Exception {
        double newPrice = calculatePrice(itemNum, quantity);
        OrderItem orderItem = orderDAO.getOrderItem(orderID, itemNum);
        if(orderItem == null){
            orderDAO.insertOrderItems(itemNum, orderID, quantity, newPrice, itemDAO.getItemFromCatalog(supplierID, itemNum).getName());
            orderDAO.updateOrderCollectPrice(orderID,order.getPriceAfterDiscount() + newPrice);
        }
        else{
            double oldPrice = orderItem.getItemPriceAfterDiscount();
            orderDAO.updateOrderCollectPrice(orderID,order.getPriceAfterDiscount() - oldPrice + newPrice);
            orderDAO.updateOrderProducts(orderID, itemNum, quantity, oldPrice);
        }
    }

    public Order addOrderCollect(HashMap<Integer, Integer> items, String superAddress, Date orderDate, int orderID , Date  possibleSupplyDates) throws Exception {
        checkBranch(superAddress);
        return prepareOrderCollect(items,superAddress,orderDate,orderID,possibleSupplyDates);
    }

    private Order prepareOrderCollect(HashMap<Integer, Integer> items, String superAddress, Date orderDate, int orderID, Date possibleSupplyDates) throws Exception {
        HashMap<Item, Integer> itemAndAmount = new HashMap<>();
        HashMap<Integer,HashMap<Integer,Integer>> itemToAmountAndDiscount = new HashMap<>();
        prepareItemsForOrder(items, itemAndAmount, itemToAmountAndDiscount);
        Order orderCollect = orderDAO.getOrderCollect(orderID);
        validateOrderIsNull(orderCollect);
        Contact contact = getRandomContact();
        orderCollect = new OrderCollect(supplierID, orderID ,orderDate,superAddress, itemAndAmount, itemToAmountAndDiscount, OrderCollect.OrderState.Waiting, -1, contact.getPhone());
        int deliveryID = -1;
        if(typeDelivery == typeDelivery.Order) {
            prepareOrderTypeOrder(superAddress,orderDate,orderID,deliveryID,contact,orderCollect);
        }
        else if(typeDelivery == typeDelivery.Collect) {
            deliveryID = prepareOrderTypeCollect(superAddress,orderDate,orderID,possibleSupplyDates,orderCollect,contact);
        }
        for(Item item : itemAndAmount.keySet()) {
            orderDAO.insertOrderItems(item.getItemID(), orderCollect.getOrderID(), itemAndAmount.get(item), orderCollect.calculatePrice(itemAndAmount.get(item), item.getPrice(), itemToAmountAndDiscount.get(item.getItemID())),item.getName());
            if(typeDelivery == typeDelivery.Collect) {
                addProductToSiteCallBack.call(deliveryID, item.getItemID(), item.getName(), itemAndAmount.get(item));
            }
        }
        return orderCollect;
    }

    private void prepareOrderTypeOrder(String superAddress, Date orderDate, int orderID, int deliveryID, Contact contact, Order orderCollect){
        orderDAO.insertOrderCollect(supplierID, orderID, orderCollect.getPriceAfterDiscount(), orderDate, orderDate, superAddress, deliveryID,contact.getPhone(), OrderCollect.OrderState.Done);
        completeDeliveryCallback.completeDelivery(orderID, superAddress);
    }

    private int prepareOrderTypeCollect(String superAddress, Date orderDate, int orderID, Date possibleSupplyDates, Order orderCollect, Contact contact) throws Exception {
        return deliever(superAddress, orderDate, orderID, possibleSupplyDates, orderCollect.getPriceAfterDiscount(), contact);
    }
    private Contact getRandomContact() throws Exception {
            List<Contact> contacts = getContacts();
            if (contacts.size() == 0)
                throw new Exception("there are no contact in the supplier!");
            Random randomGenerator = new Random();
            int index = randomGenerator.nextInt(contacts.size());
            return contacts.get(index);
    }

    public Order addOrderCollectFromConst(List<OrderItem> items, String superAddress, Date orderDate, int orderID, Date possibleSupplyDates, double price) throws Exception {
        Order orderCollect = orderDAO.getOrderCollect(orderID);
        validateOrderIsNull(orderCollect);
        Contact contact = getRandomContact();
        int deliveryID = deliever(superAddress,orderDate,orderID,possibleSupplyDates, price, contact);
        for(OrderItem orderItem : items) {
            orderDAO.insertOrderItems(orderItem.getItemID(), orderID, orderItem.getAmount(), orderItem.getItemPriceAfterDiscount(),orderItem.getName());
            addProductToSiteCallBack.call(deliveryID,orderItem.getItemID(),orderItem.getName(),orderItem.getAmount());
        }
        return orderCollect;
    }
    public void updateOrderCollect(int orderID, Date today, OrderCollect.OrderState done) {
        orderDAO.updateOrderCollect(orderID,today,done);
    }
    private void validateOrderIsNull(Order order) throws Exception {
        if(order != null)
            throw new Exception("order is already exists");
    }


    public int deliever(String superAddress, Date orderDate, int orderID, Date  possibleSupplyDates, double price, Contact contact) throws Exception {
        int deliveryID = orderDeliveryCallBack.call(possibleSupplyDates,orderID);
        Date deliveryDate = null;
        orderDAO.insertOrderCollect(supplierID, orderID, price, orderDate, deliveryDate, superAddress, deliveryID,contact.getPhone(), OrderCollect.OrderState.Waiting);
        addSupplierToDeliveryCallBack.call(deliveryID, supplierName, contact.getName(),contact.getPhone(), area_enum, supplierAddress);
        addBrunchToDeliveryCallBack.call(deliveryID,area_enum, superAddress);
        return deliveryID;
    }

    public Order addAutomaticOrderCollect(HashMap<Integer, Integer> items, String superAddress, Date orderDate, int orderID) throws Exception {
        checkBranch(superAddress);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date = new java.sql.Date(calendar.getTime().getTime());
        return addOrderCollect(items,superAddress,orderDate,orderID, date);
    }

    //add item to the contract
    public Item addItem(int itemID, int catalogID, String name, double price) throws Exception {
        Item item = itemDAO.getItemFromCatalog(supplierID,itemID);
        if(item == null)
            itemDAO.insert(supplierID, itemID, catalogID, name, price);
        else throw new Exception("the item is already in the catalog");
        return new Item(itemID,catalogID,name,price);
    }

    //remove item from the contract
    public void removeItem(int itemID) throws Exception {
        itemDAO.checkIfItemInCatalog(supplierID,itemID); //throws exception
        itemDAO.removeItem(supplierID, itemID);
    }

    //add contact
    public void addContact(int id, String name, String phone) throws Exception {
        contactsDAO.insert(supplierID, id, name, phone);
    }

    //remove contact
    public void removeContact(int contactID) throws Exception {
        contactsDAO.removeContact(supplierID, contactID);
    }


    //set contact name
    public void setContactName(int id, String name) throws Exception {
        contactsDAO.setContactName(supplierID,id,name);
    }

    //set contact phone
    public void setContactPhone(int id, String phone) throws Exception {
        String oldPhone = contactsDAO.setContactPhone(supplierID,id,phone);
        orderDAO.setContactPhone(oldPhone, phone);
    }

    public void addDiscount(int itemID, int amount, int discount) throws Exception {
        itemDAO.checkIfItemInCatalog(supplierID,itemID); //throws exception
        itemDAO.insert(supplierID,itemID,amount,discount);
    }

    public List<Item> getItemsForSupplier() {
        return itemDAO.getItemsForSupplier(supplierID);
    }

    public void addItemsToConstOrder(int orderID, HashMap<Integer, Integer> items) throws Exception {
        if (typeDelivery == typeDelivery.Const) {
            HashMap<Item, Integer> itemAndAmount = new HashMap<>();
            HashMap<Integer,HashMap<Integer,Integer>> itemToAmountAndDiscount = new HashMap<>();
            prepareItemsForOrder(items, itemAndAmount, itemToAmountAndDiscount);
            addItemsToConstOrder(orderID,itemAndAmount,itemToAmountAndDiscount);
        }
        else throw new Exception("cannot add items to collect order");
    }

    public void addItemsToConstOrder(int orderID, HashMap<Item, Integer> itemAndAmount,HashMap<Integer,HashMap<Integer,Integer>> itemToAmountAndDiscount ) throws Exception {
        Order orderConst = orderDAO.getOrderConst(orderID);
        if(orderConst != null) {
            ((OrderConst)orderConst).checkDays();
            double priceOfOrder = orderConst.getPriceAfterDiscount();
            for(Item item:itemAndAmount.keySet()){
                OrderItem orderItem = orderDAO.getOrderItem(orderID,item.getItemID());
                if(orderItem == null) {
                    double priceAfterDiscount = orderConst.calculatePrice(itemAndAmount.get(item),item.getPrice(),itemToAmountAndDiscount.get(item.getItemID()));
                    orderDAO.insertOrderItems(item.getItemID(), orderID, itemAndAmount.get(item), priceAfterDiscount, item.getName());
                    priceOfOrder += priceAfterDiscount;
                }
                else {
                    double oldPrice = orderItem.getItemPriceAfterDiscount();
                    int newAmount  = orderItem.getAmount() + itemAndAmount.get(item);
                    double newPrice = orderConst.calculatePrice(newAmount, item.getPrice(), itemToAmountAndDiscount.get(item.getItemID()));
                    orderDAO.updateOrderItemAmount(newAmount,orderID, item.getItemID()); // update db table
                    orderDAO.updateOrderItemPrice(newPrice,orderID,item.getItemID()); // update db table
                    orderItem.setAmount(newAmount); // update cache
                    orderItem.setItemPriceAfterDiscount(newPrice); // update cache
                    priceOfOrder+= newPrice - oldPrice;
                }
            }
            orderDAO.updateOrderConstPrice(orderID, priceOfOrder);
        }
        else throw new Exception("order does not exist");
    }

    public void removeItemsToConstOrder(int orderID, HashMap<Item, Integer> itemAndAmount,HashMap<Integer,HashMap<Integer,Integer>> itemToAmountAndDiscount ) throws Exception {
        Order orderConst = orderDAO.getOrderConst(orderID);
        if(orderConst != null) {
            ((OrderConst)orderConst).checkDays();
            double priceOfOrder = orderConst.getPriceAfterDiscount();
            for(Item item:itemAndAmount.keySet()){
                OrderItem orderItem = orderDAO.getOrderItem(orderID,item.getItemID());
                if(orderItem == null) {
                    throw new Exception("can't remove items from this order");
                }
                else {
                    double oldPrice = orderItem.getItemPriceAfterDiscount();
                    int newAmount  = orderItem.getAmount() - itemAndAmount.get(item);
                    if(newAmount <= 0 ) {
                        priceOfOrder -= oldPrice;
                        orderDAO.removeOrderItems(orderID, item.getItemID());
                    }
                    else {
                        double newPrice = orderConst.calculatePrice(newAmount, item.getPrice(), itemToAmountAndDiscount.get(item.getItemID()));
                        orderDAO.updateOrderItemAmount(newAmount,orderID, item.getItemID()); // update db table
                        orderDAO.updateOrderItemPrice(newPrice,orderID,item.getItemID()); // update db table
                        priceOfOrder += newPrice - oldPrice;
                    }
                }
            }

            orderDAO.updateOrderConstPrice(orderID,priceOfOrder);
        }
        else throw new Exception("order does not exist");
    }

    public void prepareItemsForOrder(HashMap<Integer, Integer> items, HashMap<Item, Integer> itemAndAmount, HashMap<Integer,HashMap<Integer,Integer>> itemToAmountAndDiscount) throws Exception {
        for(Integer itemID : items.keySet()){
            itemAndAmount.put(itemDAO.checkIfItemInCatalog(supplierID,itemID),items.get(itemID)); //gets the amount of order items and the item and check if the item in the catalog
            itemToAmountAndDiscount.put(itemID,itemDAO.getItemToAmountAndDiscount(supplierID,itemID)); //gets the amount and discount from the contract
        }
    }

    public void removeItemsToConstOrder(int orderID, HashMap<Integer, Integer> items) throws Exception {
        if (typeDelivery == typeDelivery.Const) {
            HashMap<Item, Integer> itemAndAmount = new HashMap<>();
            HashMap<Integer,HashMap<Integer,Integer>> itemToAmountAndDiscount = new HashMap<>();
            prepareItemsForOrder(items, itemAndAmount, itemToAmountAndDiscount);
            removeItemsToConstOrder(orderID,itemAndAmount,itemToAmountAndDiscount);
        }
        else throw new Exception("cannot remove items to collect order");
    }

    //    public void updateArrivedDate(int orderID) {
//         orderDAO.updateArrivedDate(supplierID,orderID);
//    }
    public List<Order> getNotArrivedOrders() {
        return orderDAO.getNotArrivedOrders(supplierID);
    }

    public void sendOrderConst() throws Exception {
        if(typeDelivery == typeDelivery.Const) {
            List<Order> orders = orderDAO.selectallOrderConst(supplierID);
            for(Order order : orders) {
                OrderConst orderConst = (OrderConst)order;
                if (orderConst.isSuppliedToday())
                    createOrderCollectFromConst(orderConst);
            }
        }
    }
    private void createOrderCollectFromConst(OrderConst orderConst) throws Exception {
        Date today = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(today);
        calendar.add(Calendar.DAY_OF_YEAR, 7);
        Date nextWeek = new java.sql.Date(calendar.getTime().getTime());
        int orderCollectID = SupplierController.orderIDGenerator++;
        addOrderCollectFromConst(orderDAO.getOrderItems(orderConst.getOrderID()),orderConst.getAddress(),today, orderCollectID ,nextWeek , orderConst.getPriceAfterDiscount());
    }

    public void setCallbacks(AddBrunchToDeliveryCallBack addBrunchToDeliveryCallBack, AddProductToSiteCallBack addProductToSiteCallBack, OrderDeliveryCallBack orderDeliveryCallBack, AddSupplierToDeliveryCallBack addSupplierToDeliveryCallBack, CompleteDeliveryCallback completeDeliveryCallback) {
        this.orderDeliveryCallBack = orderDeliveryCallBack;
        this.addSupplierToDeliveryCallBack = addSupplierToDeliveryCallBack;
        this.addProductToSiteCallBack = addProductToSiteCallBack;
        this.addBrunchToDeliveryCallBack = addBrunchToDeliveryCallBack;
        this.completeDeliveryCallback = completeDeliveryCallback;
    }
    public List<OrderItem> getOrderItems(int orderID) {
        return orderDAO.getOrderItems(orderID);
    }

    public List<Order> getOrdersCollect(){
        return orderDAO.getOrdersCollect(supplierID);
    }

    public List<Order> getOrdersConst(){
        return orderDAO.getOrdersConst(supplierID);
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }


    public int getBankAccount() {
        return bankAccount;
    }

    public Contract getContract() {
        return itemDAO.getContract(supplierID);
    }

    public Supplier.payment getPayment() {
        return payment;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public Area_Enum getArea_enum() {
        return area_enum;
    }
}
