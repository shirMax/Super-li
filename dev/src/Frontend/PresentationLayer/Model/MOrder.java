package Frontend.PresentationLayer.Model;


public abstract class MOrder {
    private int supplierID;
    private int OrderID;
    private double priceAfterDiscount;
    private String superAddress;

    public MOrder(int supplierID, int orderID, double priceAfterDiscount,String superAddress) {
        this.supplierID = supplierID;
        OrderID = orderID;
        this.priceAfterDiscount = priceAfterDiscount;
        this.superAddress = superAddress;
    }

    public int getOrderID() {
        return OrderID;
    }

    public double getPriceAfterDiscount() {
        return priceAfterDiscount;
    }

    @Override
    public abstract String toString();

    public int getSupplierID() {
        return supplierID;
    }

    public String getAddress() {
        return superAddress;
    }
}
