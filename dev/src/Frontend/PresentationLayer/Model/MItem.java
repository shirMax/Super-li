package Frontend.PresentationLayer.Model;

public class MItem {
    private int itemID;
    private String name;
    private int catalogID;
    private double price;

    public MItem(int itemID,int catalogID, String name, double price) {
        this.itemID = itemID;
        this.name = name;
        this.price = price;
        this.catalogID = catalogID;
    }

    @Override
    public String toString() {
        return "-id: "+itemID + " name: " + name +" catalogID: "+catalogID +" price: "+ price +"Shekel";
    }

    public String getName() {
        return name;
    }

    public int getItemID() {
        return itemID;
    }
}
