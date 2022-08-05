package Frontend.PresentationLayer.Model;

import Backend.BusinessLayer.Suppliers.Order;
import Backend.BusinessLayer.Suppliers.OrderCollect;
import Backend.BusinessLayer.Suppliers.OrderConst;
import Backend.BusinessLayer.Tools.Pair;
import Backend.ServiceLayer.ObjectsSupplier.*;
import Backend.ServiceLayer.Response;
import Backend.ServiceLayer.ResponseT;
import Backend.ServiceLayer.SuppliersAndStockService;

import java.time.DayOfWeek;
import java.sql.Date;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class Controller {
    private static Controller controller;
    SuppliersAndStockService suppliersAndStockService;

    private Controller(){}
    public static Controller getInstance(){
        if(controller == null)
            controller = new Controller();
        return controller;
    }
    //add supplier
    public String addSupplier(int supplierID, int bankAccount, String payment, String typeDelivery, String supplierName,String supplierAddress, String area) throws Exception {
        Response response = suppliersAndStockService.addSupplier(supplierID,bankAccount,payment,typeDelivery,supplierName,supplierAddress, area);
        if(response.isErrorOccurred())
            throw new Exception(response.getErrorMessage());
        return "supplier added successfully";
    }
    //remove supplier
    public String removeSupplier(int supplierID) throws Exception {
        Response response = suppliersAndStockService.removeSupplier(supplierID);
        if(response.isErrorOccurred())
            throw new Exception(response.getErrorMessage());
        return "supplier removed successfully";
    }

    //getSupplier by ID
    public MSupplier getSupplier(int supplierID) throws Exception {
        ResponseT<SSupplier> sSupplierResponseT = suppliersAndStockService.getSupplier(supplierID);
        if(sSupplierResponseT.isErrorOccurred())
            throw new Exception(sSupplierResponseT.getErrorMessage());
        return new MSupplier(sSupplierResponseT.Value.getSupplierID(),sSupplierResponseT.Value.getBankAccount(),sSupplierResponseT.Value.getTypeDelivery().toString(),sSupplierResponseT.Value.getPayment().toString(),sSupplierResponseT.Value.getSupplierName(),sSupplierResponseT.Value.getSupplierAddress(),sSupplierResponseT.Value.getArea().toString());
    }

    //addOrder
    public MOrderConst addOrderConst(int supplierID, List<DayOfWeek> supplyConstantDays, HashMap<Integer, Integer> items,String address) throws Exception {
        ResponseT<SOrderConst> sOrderConstResponseT = suppliersAndStockService.addOrderConst(supplierID,supplyConstantDays,items,address);
        if(sOrderConstResponseT.isErrorOccurred())
            throw new Exception(sOrderConstResponseT.getErrorMessage());
        return new MOrderConst(sOrderConstResponseT.Value.getSupplierID(),sOrderConstResponseT.Value.getOrderID(),sOrderConstResponseT.Value.getPriceAfterDiscount(),sOrderConstResponseT.Value.getSupplyConstantDays(),sOrderConstResponseT.Value.getAddress());
    }

    public MOrderCollect addOrderCollect(int supplierID, Date supplyDate, HashMap<Integer, Integer> items,String address,Date possibleSupplyDates) throws Exception {
        ResponseT<SOrderCollect> sOrderCollectResponseT = suppliersAndStockService.addOrderCollect(supplierID,supplyDate,items,address, possibleSupplyDates);
        if(sOrderCollectResponseT.isErrorOccurred())
            throw new Exception(sOrderCollectResponseT.getErrorMessage());
        return new MOrderCollect(sOrderCollectResponseT.Value.getSupplierID(),sOrderCollectResponseT.Value.getOrderID(),sOrderCollectResponseT.Value.getPriceAfterDiscount(),sOrderCollectResponseT.Value.getArrivedDate(),sOrderCollectResponseT.Value.getArrivedDate(),sOrderCollectResponseT.Value.getAddress(), sOrderCollectResponseT.Value.getOrderState());
    }

    //add contact
    public String addContact(int supplierID,int contactID, String name, String phone) throws Exception {
        Response response = suppliersAndStockService.addContact(supplierID,contactID,name,phone);
        if(response.isErrorOccurred())
            throw new Exception(response.getErrorMessage());
        return "contact added successfully";
    }

    //remove contact
    public String removeContact(int supplierID,int contactID) throws Exception {
        Response response = suppliersAndStockService.removeContact(supplierID,contactID);
        if(response.isErrorOccurred())
            throw new Exception(response.getErrorMessage());
        return "contact removed successfully";
    }

    //add item to the contract
    public MItem addItem(int supplierID,int itemID,int catalogID, double price) throws Exception {
        ResponseT<SItem> response = suppliersAndStockService.addItem(supplierID,itemID,catalogID,price);
        if(response.isErrorOccurred())
            throw new Exception(response.getErrorMessage());
        return new MItem(response.Value.getItemID(),response.Value.getCatalogID(),response.Value.getName(),response.Value.getPrice());
    }

    //remove item in the contract
    public String removeItem(int supplierID,int itemID) throws Exception {
        Response response = suppliersAndStockService.removeItem(supplierID,itemID);
        if(response.isErrorOccurred())
            throw new Exception(response.getErrorMessage());
        return "item removed successfully";
    }

    //add new discount to the contract
    public String addDiscount(int supplierID,int itemID, int amount, int discount) throws Exception {
        Response response = suppliersAndStockService.addDiscount(supplierID,itemID,amount,discount);
        if(response.isErrorOccurred())
            throw new Exception(response.getErrorMessage());
        return "discount added successfully";
    }

    //get items from supplier
    public List<MItem> getItemsForSupplier(int supplierID) throws Exception {
        ResponseT<List<SItem>> sitemResponse = suppliersAndStockService.getItemsForSupplier(supplierID);
        if(sitemResponse.isErrorOccurred())
            throw new Exception(sitemResponse.getErrorMessage());
        List<MItem> mitemList = new LinkedList<>();
        for(SItem item : sitemResponse.Value){
            mitemList.add(new MItem(item.getItemID(),item.getCatalogID(),item.getName(),item.getPrice()));
        }
        return mitemList;
    }


    //set contact phone
    public String setContactPhone(int supplierID, int contactID, String phone) throws Exception {
        Response response = suppliersAndStockService.setContactPhone(supplierID,contactID,phone);
        if(response.isErrorOccurred())
            throw new Exception(response.getErrorMessage());
        return "set contacts phone done successfully";
    }

    //set contact name
    public String setContactname(int supplierID, int contactID, String name) throws Exception {
        Response response = suppliersAndStockService.setContactname(supplierID,contactID,name);
        if(response.isErrorOccurred())
            throw new Exception(response.getErrorMessage());
        return "set contacts name done successfully";
    }

    public List<MSupplier> getSuppliers() throws Exception {
        ResponseT<List<SSupplier>> getSuppliersByNameRes = suppliersAndStockService.getSuppliers();
        if(getSuppliersByNameRes.isErrorOccurred())
            throw new Exception(getSuppliersByNameRes.getErrorMessage());
        List<MSupplier> mSupplierList = new LinkedList<>();
        for(SSupplier supplier : getSuppliersByNameRes.Value)
            mSupplierList.add(new MSupplier(supplier.getSupplierID(),supplier.getBankAccount(),supplier.getTypeDelivery().toString(),supplier.getPayment().toString(),supplier.getSupplierName(),supplier.getSupplierAddress(), supplier.getArea().toString()));
        return mSupplierList;
    }

    public String addItemsToConstOrder(int supplierID,int orderID, HashMap<Integer,Integer> items) throws Exception {
        Response addItemToConstOrderRes = suppliersAndStockService.addItemsToConstOrder(supplierID, orderID, items);
        if(addItemToConstOrderRes.isErrorOccurred())
            throw new Exception(addItemToConstOrderRes.getErrorMessage());
        return "adding items done succesfully";

    }
    public String removeItemsToConstOrder(int supplierID,int orderID, HashMap<Integer,Integer> items) throws Exception {
        Response removeItemToConstOrderRes = suppliersAndStockService.removeItemsToConstOrder(supplierID, orderID, items);
        if(removeItemToConstOrderRes.isErrorOccurred())
            throw new Exception(removeItemToConstOrderRes.getErrorMessage());
        return "removing items done succesfully";
    }

    public void setService(SuppliersAndStockService sass) {
        suppliersAndStockService = sass;
    }

//    public String changeOrderArrivedStatus(int orderID,int supplierID) throws Exception {
//        Response changeOrderArrivedStatusRes = suppliersAndStockService.changeOrderArrivedStatus(orderID, supplierID);
//        if(changeOrderArrivedStatusRes.isErrorOccurred())
//            throw new Exception(changeOrderArrivedStatusRes.getErrorMessage());
//        return "update order status arrived done succesfully";
//    }
    public List<MOrder> getOrdersFromSupplier(int supplierID, MSupplier.TypeDelivery typeDelivery) throws Exception {
        ResponseT<List<Order>> getOrdersFromSupplierRes = suppliersAndStockService.getOrdersFromSupplier(supplierID);
        if(getOrdersFromSupplierRes.isErrorOccurred())
            throw new Exception(getOrdersFromSupplierRes.getErrorMessage());
        List<MOrder> orders = new LinkedList<>();
        if(typeDelivery == MSupplier.TypeDelivery.Const)
            for(Order order : getOrdersFromSupplierRes.Value)
                orders.add(new MOrderConst(order.getSupplierID(), order.getOrderID(), order.getPriceAfterDiscount(), ((OrderConst)order).getSupplyConstantDays(), order.getAddress()));
        else
            for(Order order : getOrdersFromSupplierRes.Value)
                orders.add(new MOrderCollect(order.getSupplierID(), order.getOrderID(), order.getPriceAfterDiscount(), ((OrderCollect)order).getOrderDate(), ((OrderCollect)order).getArrivedDate(),  order.getAddress(), ((OrderCollect)order).getOrderState()));
        return orders;
    }

    public List<MOrder> getNotArrivedOrdersFromSupplier(int supplierID) throws Exception {
        ResponseT<List<Order>> getOrdersFromSupplierRes = suppliersAndStockService.getNotArrivedOrdersFromSupplier(supplierID);
        if(getOrdersFromSupplierRes.isErrorOccurred())
            throw new Exception(getOrdersFromSupplierRes.getErrorMessage());
        List<MOrder> orders = new LinkedList<>();
//        if(typeDelivery == MSupplier.TypeDelivery.Const)
//            throw new Exception("type delivery is not Collect delivery!");
//        else
            for(OrderCollect orderCollect : (List<OrderCollect>)(List<?>)getOrdersFromSupplierRes.Value)
                orders.add(new MOrderCollect(orderCollect.getSupplierID(), orderCollect.getOrderID(), orderCollect.getPriceAfterDiscount(), orderCollect.getArrivedDate(), orderCollect.getArrivedDate(),  orderCollect.getAddress(), orderCollect.getOrderState()));
        return orders;
    }

    public List<MContact> getContacts(int supplierID) throws Exception {
        ResponseT<List<SContact>> scontact = suppliersAndStockService.getContacts(supplierID);
        List<MContact> mContactList = new LinkedList<>();
        for(SContact sContact : scontact.Value)
            mContactList.add(new MContact(sContact.getContactID(),sContact.getName(),sContact.getPhone()));
        if(scontact.isErrorOccurred())
            throw new Exception(scontact.getErrorMessage());
        return mContactList;
    }

    public MContract getContract(int supplierID) throws Exception {
        ResponseT<SContract> getContractRes = suppliersAndStockService.getContract(supplierID);
        if(getContractRes.isErrorOccurred())
            throw new Exception(getContractRes.getErrorMessage());
        HashMap<Integer, SItem> sitems = getContractRes.Value.getItems();
        HashMap<Integer,MItem> mitems = new HashMap<>();
        for(SItem sItem : sitems.values())
            mitems.put(sItem.getItemID(),new MItem(sItem.getItemID(),sItem.getCatalogID(),sItem.getName(),sItem.getPrice()));
        return new MContract(getContractRes.getValue().getDiscount(), mitems);
    }

    public List<Pair<Integer,String>> getItems() throws Exception {
        ResponseT<List<Pair<Integer,String>>> getItemsResponse = suppliersAndStockService.getItems();
        if(getItemsResponse.isErrorOccurred())
            throw new Exception(getItemsResponse.getErrorMessage());
        return getItemsResponse.Value;
    }

    public String addAutomaticOrderConst(List<DayOfWeek> constantDays, HashMap<Integer,Integer> items, String superAddress) throws Exception {
        Response addAutomaticOrder = suppliersAndStockService.addAutomaticOrderConst(constantDays, items,superAddress);
        if(addAutomaticOrder.isErrorOccurred())
            throw new Exception(addAutomaticOrder.getErrorMessage());
        return "add order done successfuly";
    }
    public String addAutomaticOrderCollect(HashMap<Integer,Integer> items, Date current, String superAddress) throws Exception {
        Response addAutomaticOrder = suppliersAndStockService.addAutomaticOrderCollect(items,current,superAddress);
        if(addAutomaticOrder.isErrorOccurred())
            throw new Exception(addAutomaticOrder.getErrorMessage());
        return "add order done successfuly";
    }

    public String sendConstOrders() throws Exception {
        Response sendConstOrderRes = suppliersAndStockService.sendConstOrders();
        if(sendConstOrderRes.isErrorOccurred())
            throw new Exception(sendConstOrderRes.getErrorMessage());
        return "send orders to next week done successfully";
    }
    public String cancelOrder(int orderID) throws Exception {
        Response cancelOrderRes = suppliersAndStockService.cancelOrder(orderID);
        if(cancelOrderRes.isErrorOccurred())
            throw new Exception(cancelOrderRes.getErrorMessage());
        return "cancel order "+orderID+ " done successfuly";
    }

    public List<MOrder> getNotArrivedOrders() throws Exception {
        ResponseT<List<SOrder>> getNotArrivedOrder = suppliersAndStockService.getNotArrivedOrders();
        if(getNotArrivedOrder.isErrorOccurred())
            throw new Exception(getNotArrivedOrder.getErrorMessage());
        List<MOrder> orders = new LinkedList<>();
        for(SOrder orderCollect : getNotArrivedOrder.Value)
            orders.add(new MOrderCollect(orderCollect.getSupplierID(), orderCollect.getOrderID(), orderCollect.getPriceAfterDiscount(), ((SOrderCollect)orderCollect).getOrderDate(), ((SOrderCollect)orderCollect).getArrivedDate(),  orderCollect.getAddress(), ((SOrderCollect)orderCollect).getOrderState()));
        return orders;
    }


//    public String sendingNotDeliverOrder(int orderID, List<Date> dates) throws Exception {
//        Response sendingNotDeliverOrderR = suppliersAndStockService.sendingNotDeliverOrder(orderID,dates);
//        if(sendingNotDeliverOrderR.isErrorOccurred())
//            throw new Exception(sendingNotDeliverOrderR.getErrorMessage());
//        return "";
//
//    }
}
