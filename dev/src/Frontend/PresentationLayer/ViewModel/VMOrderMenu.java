package Frontend.PresentationLayer.ViewModel;
import Backend.BusinessLayer.Tools.Pair;
import Frontend.PresentationLayer.Model.Controller;
import Frontend.PresentationLayer.Model.MItem;
import Frontend.PresentationLayer.Model.MOrder;
import Frontend.PresentationLayer.View.Menu;
import Frontend.PresentationLayer.View.SuppliersMenu;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.*;

public class VMOrderMenu {
    private Scanner scanner;

    public VMOrderMenu() {
        scanner = new Scanner(System.in);
    }

    public Menu orderConst() {
        try {
            System.out.println("please enter the super address");
            String superAddress = scanner.nextLine();
            displayItems();
            System.out.println("choose items and amount that you want to order and press -9 to finish the order: ");
            HashMap<Integer, Integer> items = choosingItems();
            System.out.println(Controller.getInstance().addAutomaticOrderConst(choosingConstantDays(),items,superAddress));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new SuppliersMenu();
    }

    public List<DayOfWeek> choosingConstantDays(){
        System.out.println("please enter days that you prefer to receive the order and -9 to finish: ");
        System.out.println("press 7 to Sunday, 1 to Monday, 2 to Tuesday, 3 to Wednesday, 4 to Thursday, 5 to Friday, 6 to Saturday");
        List<DayOfWeek> days = new LinkedList<>();
        int day = 0;
        while (day != -9) {
            day = Integer.parseInt(scanner.nextLine());
            if (day != -9) {
                days.add(DayOfWeek.of(day));
            }
        }
        return days;
    }
    public Menu orderCollect() {
        try {
            Date current = new Date(Calendar.getInstance().getTime().getTime());
            System.out.println("please enter the super address");
            String superAddress = scanner.nextLine();
            displayItems();
            System.out.println("choose items and amount that you want to order and press -9 to finish the order: ");
            System.out.println(Controller.getInstance().addAutomaticOrderCollect(choosingItems(),current, superAddress));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new SuppliersMenu();
    }
    public HashMap<Integer, Integer> choosingItems() throws Exception {

        HashMap<Integer,Integer> items = new HashMap<>();
        int itemID = 0;
        int amount;
        while (itemID != -9) {
            System.out.println("choose item: ");
            itemID = Integer.parseInt(scanner.nextLine());
            if (itemID != -9) {
                System.out.println("choose amount: ");
                amount = Integer.parseInt(scanner.nextLine());
                if(amount<=0)
                    throw new Exception("amount cannot be smaller than zero");
                items.put(itemID, amount);
            }
        }
        return items;
    }

    private void validateID(int id) throws Exception {
        if(id<=0)
            throw new Exception("id is not valid");
    }
        private void displayItems () throws Exception {
            System.out.println("please choose items that you want to order: ");
            List<Pair<Integer, String>> items = Controller.getInstance().getItems();
            if(items.size() == 0)
                throw new Exception("there are no items yet");
            for (Pair<Integer, String> item : items)
                System.out.println("itemID: " + item.getFirst() + " itemName: " + item.getSecond());
        }

    public void cancelOrder() {
        try {
            System.out.println("please choose order id that you want to remove: ");
            List<MOrder> mOrders = displayAllNotArrivedOrders();
            int id = Integer.parseInt(scanner.nextLine());
            checkIfOrderExists(id, mOrders);
            System.out.println(Controller.getInstance().cancelOrder(id));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
    private void checkIfOrderExists(int orderID, List<MOrder> mItems) {
        for(MOrder order : mItems)
            if(order.getOrderID() == orderID)
                return;
        throw new IllegalArgumentException("order id does not exist!");
    }

    public List<MOrder> displayAllNotArrivedOrders() throws Exception {
        try{
            List<MOrder> orderList = Controller.getInstance().getNotArrivedOrders();
            if(orderList.isEmpty())
                throw new IllegalArgumentException("there are no not arrived orders");
            for(MOrder order : orderList)
                System.out.println(order);
            return orderList;
        }
        catch (Exception e) {
            throw e;
        }
    }


    public void sendConstOrders() {
        try {
            System.out.println(Controller.getInstance().sendConstOrders());
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
