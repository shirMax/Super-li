package Backend.BusinessLayer.Suppliers;
import java.time.Year;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;


public class OrderCollect extends Order{
    public enum OrderState {
        Waiting,
        InProgress,
        Done;
    }
    private Date orderDate;
    private Date arrivedDate;
    private int deliveryID;
    private String contactPhone;
    private OrderState orderState;

    public OrderCollect(int supplierID, int orderID, double priceAfterDiscount, Date orderDate, Date arrivedDate, String superAddress,OrderState orderState, int deliveryID, String contactPhone) {
        super(orderID, supplierID ,priceAfterDiscount,superAddress);
        this.orderDate = orderDate;
        this.arrivedDate = arrivedDate;
        this.orderState = orderState;
        this.deliveryID = deliveryID;
        this.contactPhone = contactPhone;
    }
    public OrderCollect(int supplierID, int orderID, Date orderDate, String superAddress, HashMap<Item, Integer> itemAndAmount, HashMap<Integer,HashMap<Integer,Integer>> itemToAmountAndDiscount, OrderState orderState, int deliveryID, String contactPhone) {
        super(supplierID, orderID, superAddress, itemAndAmount, itemToAmountAndDiscount);
        this.orderDate = orderDate;
        this.arrivedDate = null;
        this.orderState = orderState;
        this.deliveryID = deliveryID;
        this.contactPhone = contactPhone;
    }
    public boolean checkDays() {
        if(arrivedDate!=null)
        {
            java.sql.Date today = new java.sql.Date(Calendar.getInstance().getTime().getTime());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(today);
            calendar.add(Calendar.DAY_OF_YEAR, 1);
            Date tommorrow = calendar.getTime();
            if(tommorrow.after(today))
                return false;
        }
            return true;
    }
    public boolean checkIfNotArrived() {
        java.sql.Date today = new java.sql.Date(Calendar.getInstance().getTime().getTime());
        return orderState == OrderCollect.OrderState.Waiting;
    }

    public Date getArrivedDate() {
        return arrivedDate;
    }

    public OrderState getOrderState() {
        return orderState;
    }

    public int getDeliveryID() {
        return deliveryID;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public Date getOrderDate() {
        return orderDate;
    }
}
