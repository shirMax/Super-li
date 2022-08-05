package Frontend.PresentationLayer.View;

import Frontend.PresentationLayer.Model.Controller;
import java.time.DayOfWeek;
import java.util.*;
import java.sql.Date;

public class LoadData{
    public LoadData(){
    }
    public void loadData() {
        //adding suppliers
        try {
            Controller.getInstance().addSupplier(123,123456,"Cash","Const","Tnuva","address","Center");

            Controller.getInstance().addSupplier(268,129435,"BankTransfer","Collect","Osem","address1","Center");
            Controller.getInstance().addItem(123,1,2,4.90);
            Controller.getInstance().addItem(123,2,3,3.90);
            Controller.getInstance().addItem(268,3,4,9.90);
            Controller.getInstance().addItem(268,4,5,4.90);

            Controller.getInstance().addDiscount(123,1,10,50);
            Controller.getInstance().addDiscount(123,1,20,70);
            Controller.getInstance().addDiscount(123,2,20,70);

            Controller.getInstance().addContact(123,189,"shir","040995955");
            Controller.getInstance().addContact(123,183,"shira","040295955");

            Controller.getInstance().addContact(268,135,"oran","048383443");
            Controller.getInstance().addContact(268,395,"eilon","93949494");

            //adding orders
            List<DayOfWeek> days = new LinkedList<>();
            days.add(DayOfWeek.SUNDAY);
            HashMap<Integer,Integer> items = new HashMap<>();
            items.put(1,10);
            items.put(2,20);
            Controller.getInstance().addOrderConst(123,days,items,"aaa");

            HashMap<Integer,Integer> items2 = new HashMap<>();
            items2.put(3,19);
            items2.put(4,20);
            Date current = new java.sql.Date(Calendar.getInstance().getTime().getTime());
            Date arrived = new java.sql.Date(Calendar.getInstance().getTime().getTime());
            Controller.getInstance().addOrderCollect(268,current,items2,"aaa",arrived);

            System.out.println("load data done successfully");
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}
