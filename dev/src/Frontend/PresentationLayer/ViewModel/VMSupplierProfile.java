package Frontend.PresentationLayer.ViewModel;

import Backend.BusinessLayer.Tools.DateConvertor;
import Frontend.PresentationLayer.Model.*;
import Frontend.PresentationLayer.View.Menu;
import Frontend.PresentationLayer.View.SupplierProfile;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.sql.Date;
import java.util.*;

public class VMSupplierProfile {
    private MSupplier mSupplier;
    private Scanner scanner;
    public VMSupplierProfile(MSupplier mSupplier){
        this.mSupplier = mSupplier;
        scanner = new Scanner(System.in);
    }

    public Menu displaySuppliersOrders() {
        try{
            List<MOrder> mSupplierList = mSupplier.displaySupplierOrders();
            if(mSupplierList.isEmpty())
                System.out.println("there are no orders for this supplier");
            for(MOrder order : mSupplierList)
                System.out.println(order);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return new SupplierProfile(mSupplier);
    }

    public List<MOrder> displaySupplierOrders() throws Exception {
        try{
            List<MOrder> mSupplierList = mSupplier.displaySupplierOrders();
            if(mSupplierList.isEmpty())
                throw new IllegalArgumentException("there are no orders for this supplier");
            for(MOrder order : mSupplierList)
                System.out.println(order);
            return mSupplierList;
        }
        catch (Exception e){
            throw e;
        }
    }

    public Menu displayNotArrivedSupplierOrders() throws Exception {
        try{
            List<MOrder> mSupplierList = mSupplier.displayNotArrivedSupplierOrders();
            if(mSupplierList.isEmpty())
                throw new IllegalArgumentException("there are no not arrived orders for this supplier");
            for(MOrder order : mSupplierList)
                System.out.println(order);
        }
        catch (Exception e){
            throw e;
        }
        return new SupplierProfile(mSupplier);
    }

    public MSupplier getmSupplier() {
        return mSupplier;
    }

    public Menu addOrder() {
        try {
            switch(mSupplier.getTypeDelivery().toString()){
                case "Collect" : {addOrderCollectOrder(); break;}
                case "Order" : {addOrderDeliveryOrder(); break;}
                case "Const" : {addOrderConst(); break;}
            }
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return new SupplierProfile(mSupplier);
    }


    public void addOrderConst(){
        try {
            System.out.println("please enter days that you prefer to receive the order and -9 to finish: ");
            System.out.println("press 7 to Sunday, 1 to Monday, 2 to Tuesday, 3 to Wednesday, 4 to Thursday, 5 to Friday, 6 to Saturday");
            List<DayOfWeek> days = new LinkedList<>();
            String dayIn = "0";
            int day = 0;
            while (day != -9) {
                dayIn = scanner.next();
                day = Integer.parseInt(dayIn);
                if (day != -9) {
                    validateDays(day);
                    days.add(DayOfWeek.of(day));
                }
            }
            System.out.println("please enter the super address");
            String supplierAddress = scanner.nextLine();
            validateAddress(supplierAddress);
            System.out.println("choose items and amount that you want to order and press -9 to finish the order: ");
            System.out.println(mSupplier.addOrderConst(days,choosingItems(),supplierAddress));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
    private void validateDays(int day) throws Exception {
        if(day>7 || day<1)
            throw new Exception("invalid day");
    }
    public List<MItem> displayingItemsPerSupplier() throws Exception {
        try {
            List<MItem> mitems = Controller.getInstance().getItemsForSupplier(mSupplier.getSupplierID());
            if(mitems.size() == 0)
                throw new IllegalArgumentException("there are no items in the system yet");
            for (MItem item : mitems)
                System.out.println(item);
            return mitems;
        }
        catch (Exception e) {
            throw e;
        }
    }

    public HashMap<Integer, Integer> choosingItems() throws Exception {
        List<MItem> mItemsList = displayingItemsPerSupplier();
        HashMap<Integer, Integer> items = new HashMap<>();
        int itemID = 0;
        int amount;
        String itemIDIn;
        String amountIn;
        while (itemID != -9) {
            System.out.println("choose item: ");
            itemIDIn = scanner.next();
            itemID = Integer.parseInt(itemIDIn);
            if (itemID != -9) {
                checkIfItemIdInContract(itemID, mItemsList);
                System.out.println("choose amount: ");
                amountIn = scanner.next();
                amount = Integer.parseInt(amountIn);
                if(amount<=0)
                    throw new Exception("amount cannot be smaller than zero");
                items.put(itemID, amount);
            }
        }
        return items;
    }


    private void checkIfItemIdInContract(int itemID, List<MItem> items) {
        for(MItem item : items)
            if(itemID == item.getItemID())
                return;
        throw new IllegalArgumentException("item id does not exist");
    }

    public void addOrderCollectOrder() {
        try {
            Date current = new Date(Calendar.getInstance().getTime().getTime());
            System.out.println("please enter the super address");
            String superAddress = scanner.nextLine();
            validateAddress(superAddress);
            Date possibleSupplyDate = choosingDate();
            System.out.println("choose items and amount that you want to order and press -9 to finish the order: ");
            System.out.println(mSupplier.addOrderCollectOrder(current,choosingItems(),superAddress, possibleSupplyDate));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private void validateAddress(String address) throws Exception {
        if(address.length()==0)
            throw new Exception("invalid address");
    }
    public void addOrderDeliveryOrder() {
        try {
            Date current = new Date(Calendar.getInstance().getTime().getTime());
            System.out.println("please enter the super address");
            String superAddress = scanner.nextLine();
            validateAddress(superAddress);
            System.out.println("choose items and amount that you want to order and press -9 to finish the order: ");
            System.out.println(mSupplier.addOrderCollectOrder(current,choosingItems(),superAddress, null));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    public Date choosingDate() throws Exception {
        System.out.println("please enter the date that you prefer to collect the order in the format dd/MM/yyyy");
        String arrivedDate = scanner.nextLine();
        DateConvertor dateConvertor = new DateConvertor();
        Date arrived = new Date(dateConvertor.stringToDate(arrivedDate + " 00:00").getTime());
        validateDate(arrived);
        return arrived;
    }
    private void validateDate(Date arrived) throws Exception {
        Date current = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(current);
        calendar.add(Calendar.DAY_OF_YEAR, -1);
        Date yesterday = new java.sql.Date(calendar.getTime().getTime());
        if (arrived.before(yesterday))
            throw new Exception("the date is before today");
    }

    public Menu addItemsToConstOrder() {
        try{
            mSupplier.checkIfConst();
            System.out.println("please choose an order that you want to edit");
            List<MOrder> mOrders = displaySupplierOrders();
            int orderID = Integer.parseInt(scanner.nextLine());
            checkIfOrderExists(orderID, mOrders);
            System.out.println("choose items and amount that you want to add to the order and press -9 to finish the order: ");
            System.out.println(Controller.getInstance().addItemsToConstOrder(mSupplier.getSupplierID(),orderID,choosingItems()));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return new SupplierProfile(mSupplier);
    }

    public Menu removeItemsToConstOrder() {
        try{
            mSupplier.checkIfConst();
            System.out.println("please choose an order that you want to edit");
            List<MOrder> mOrders = displaySupplierOrders();
            int orderID = Integer.parseInt(scanner.nextLine());
            checkIfOrderExists(orderID, mOrders);
            System.out.println("choose items and amount that you want to remove from the order and press -9 to finish the order: ");
            System.out.println(Controller.getInstance().removeItemsToConstOrder(mSupplier.getSupplierID(),orderID,choosingItems()));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return new SupplierProfile(mSupplier);
    }

    private void checkIfOrderExists(int orderID, List<MOrder> mItems) {
        for(MOrder order : mItems)
            if(order.getOrderID() == orderID)
                return;
        throw new IllegalArgumentException("order id does not exist!");
    }
}


