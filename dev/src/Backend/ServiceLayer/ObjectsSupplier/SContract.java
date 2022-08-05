package Backend.ServiceLayer.ObjectsSupplier;

import Backend.BusinessLayer.Suppliers.Contract;
import Backend.BusinessLayer.Suppliers.Item;

import java.util.HashMap;

public class SContract {
    private HashMap<Integer, SItem> items;  //itemID,Item
    private HashMap<Integer, HashMap<Integer, Integer>> discount; //itemID, <amount,discount>

    public SContract() {
        items = new HashMap<>();
        discount = new HashMap<>();
    }
    public SContract(Contract c) {
        discount = c.getDiscount();
        items = new HashMap<>();
        for(Item i : c.getItems().values())
            items.put(i.getItemID(),new SItem(i.getItemID(),i.getCatalogID(),i.getName(),i.getPrice()));
    }

    public HashMap<Integer, HashMap<Integer, Integer>> getDiscount() {
        return discount;
    }

    public HashMap<Integer, SItem> getItems() {
        return items;
    }
}
