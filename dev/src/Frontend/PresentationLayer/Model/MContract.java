package Frontend.PresentationLayer.Model;

import java.util.HashMap;

public class MContract {
    private HashMap<Integer, MItem> items;  //itemID,Item
    private HashMap<Integer, HashMap<Integer, Integer>> discount; //itemID, <amount,discount>

    public MContract() {
        items = new HashMap<>();
        discount = new HashMap<>();
    }
    public MContract(HashMap<Integer, HashMap<Integer, Integer>> discount,HashMap<Integer, MItem> items) {
        this.items = items;
        this.discount = discount;
    }

    public String addItemToTheContract(int supplierID, int itemid, int catalogid, double price) throws Exception {
        MItem addItemToTheContractRes = Controller.getInstance().addItem(supplierID,itemid,catalogid,price);
        MItem mitem = new MItem(itemid,catalogid,addItemToTheContractRes.getName(),price);
        items.put(itemid, mitem);
        return "item added successfully";
    }

    public String removeItemFromTheContract(int supplierID,int itemID) throws Exception {
        String removeItemFromTheContractRes = Controller.getInstance().removeItem(supplierID,itemID);
        items.remove(itemID);
        return removeItemFromTheContractRes;
    }

    public String addDiscountToTheContract(int supplierID, int itemID, int amount, int dis) throws Exception {
        String addDiscountToTheContractRes = Controller.getInstance().addDiscount(supplierID,itemID,amount,dis);
        if(!discount.containsKey(itemID)) {
            discount.put(itemID, new HashMap<>());
            discount.get(itemID).put(amount,dis);
        }
        else {
            if (!discount.get(itemID).containsKey(amount))
                discount.get(itemID).put(amount, dis);
        }
        return addDiscountToTheContractRes;
    }
}
