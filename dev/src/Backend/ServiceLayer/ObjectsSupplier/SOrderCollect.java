package Backend.ServiceLayer.ObjectsSupplier;


import Backend.BusinessLayer.Suppliers.OrderCollect;

import java.util.Date;

public class SOrderCollect extends SOrder {
    private Date orderDate;
    private Date arrivedDate;
    private OrderCollect.OrderState orderState;


    public SOrderCollect(int supplierID, int orderID, double priceAfterDiscount, Date orderDate, Date arrivedDate, String superAddress, OrderCollect.OrderState orderState) {
        super(supplierID ,orderID ,priceAfterDiscount ,superAddress);
        this.orderDate = orderDate;
        this.arrivedDate = arrivedDate;
        this.orderState = orderState;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public Date getArrivedDate() {
        return arrivedDate;
    }

    public OrderCollect.OrderState getOrderState() {
        return orderState;
    }
}
