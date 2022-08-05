package Backend.ServiceLayer.ObjectsSupplier;



import Backend.BusinessLayer.Tools.Pair;

import java.util.HashMap;

public abstract class SOrder {
    private int supplierID;
    private int OrderID;
    private int isArrived;

    private double priceAfterDiscount;
    private String superAddress;

    public SOrder(int supplierID, int orderID, double priceAfterDiscount,String superAddress) {
        this.supplierID = supplierID;
        OrderID = orderID;
        this.priceAfterDiscount = priceAfterDiscount;
        this.superAddress = superAddress;
    }

    public double getPriceAfterDiscount() {
        return priceAfterDiscount;
    }

    public int getSupplierID() {
        return supplierID;
    }


    public int getOrderID() {
        return OrderID;
    }

    public String getAddress() {
        return superAddress;
    }
}
