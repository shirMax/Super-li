package Backend.BusinessLayer.Suppliers;


import java.util.HashMap;

public class Contract {
    private HashMap<Integer, Item> items;  //itemID,Item
    private HashMap<Integer, HashMap<Integer, Integer>> discount; //itemID, <amount,discount>

    public Contract() {
        items = new HashMap<>();
        discount = new HashMap<>();

    }

    public void addItem(Item item) {
        items.putIfAbsent(item.getItemID(),item);
    }

    public void addDiscount(int itemID,HashMap<Integer,Integer> amountAndDiscount) {
        this.discount.putIfAbsent(itemID,amountAndDiscount);
    }


    public HashMap<Integer, HashMap<Integer, Integer>> getDiscount() {
        return discount;
    }


    public HashMap<Integer, Item> getItems() {
        return items;
    }
}