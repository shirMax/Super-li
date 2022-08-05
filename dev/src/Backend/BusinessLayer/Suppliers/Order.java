package Backend.BusinessLayer.Suppliers;

import Backend.BusinessLayer.Tools.Pair;

import java.sql.Date;
import java.util.Calendar;
import java.util.Comparator;
import java.util.HashMap;

public abstract class Order {
    private int supplierID;
    private int OrderID;
    private double priceAfterDiscount;
    private String superAddress;

    public Order(int orderID, int supplierID, double priceAfterDiscount,String superAddress) {
        this.supplierID = supplierID;
        OrderID = orderID;
        this.priceAfterDiscount = priceAfterDiscount;
        this.superAddress = superAddress;
    }
    public Order(int supplierID, int orderID, String superAddress, HashMap<Item, Integer> itemAndAmount, HashMap<Integer,HashMap<Integer,Integer>> itemToAmountAndDiscount) {
        this.supplierID = supplierID;
        OrderID = orderID;
        this.priceAfterDiscount = calculateOrderPrice(itemAndAmount, itemToAmountAndDiscount);
        this.superAddress = superAddress;
    }

    private double calculateOrderPrice(HashMap<Item, Integer> itemAndAmount, HashMap<Integer, HashMap<Integer, Integer>> itemToAmountAndDiscount) {
        double price = 0;
        for(Item item : itemAndAmount.keySet()) {
            price+= calculatePrice(itemAndAmount.get(item), item.getPrice(), itemToAmountAndDiscount.get(item.getItemID()));
        }
        return price;
    }

    public double calculatePrice(int amount, double price, HashMap<Integer,Integer> amountAndDiscount){
        double itemPrice = price * amount;
        if(amountAndDiscount.keySet().size() == 0 )
            return itemPrice;
        int key = amountAndDiscount.keySet().stream()
                .filter(x -> x <= amount).max(Comparator.naturalOrder()).orElse(-1);
        if(key==-1)
            return itemPrice;
        return itemPrice - (itemPrice * (amountAndDiscount.get(key) / 100.0));
    }


    public void setPriceAfterDiscount(double priceAfterDiscount) {
        this.priceAfterDiscount = priceAfterDiscount;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public void setSupplierID(int supplierID) {
        this.supplierID = supplierID;
    }

    public int getOrderID() {
        return OrderID;
    }

    public double getPriceAfterDiscount() {
        return priceAfterDiscount;
    }

    public String getAddress() {
        return superAddress;
    }

}
