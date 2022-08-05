package Frontend.PresentationLayer.Model;



import Backend.BusinessLayer.Suppliers.OrderCollect;
import Backend.BusinessLayer.Tools.Pair;

import java.util.Date;

public class MOrderCollect extends MOrder {
    private Date orderDate;
    private Date arrivedDate;
    private OrderCollect.OrderState orderState;

    public MOrderCollect(int supplierID, int orderID, double priceAfterDiscount, Date orderDate, Date arrivedDate, String superAddress, OrderCollect.OrderState orderState) {
        super(supplierID ,orderID ,priceAfterDiscount ,superAddress);
        this.orderDate = orderDate;
        this.arrivedDate = arrivedDate;
        this.orderState = orderState;
    }

    @Override
    public String toString() {
        String arrivedDate = this.arrivedDate == null ? ", arrived date: not arrived" : ", arrived date: "+this.arrivedDate;
        return "" +
                "-supplierID: " + getSupplierID() +
                ", OrderID: " + getOrderID() +
                ", priceAfterDiscount: " + getPriceAfterDiscount() +
                ", orderDate: " + orderDate +
                arrivedDate+
                ", superAddress: "+ getAddress() + ", order state: "+ orderState.toString();
    }
}
