package Backend.ServiceLayer.ObjectsSupplier;

public class SItem {
    private int itemID;
    private String name;
    private int catalogID;
    private double price;

    public SItem(int itemID, int catalogID, String name, double price) {
        this.itemID = itemID;
        this.name = name;
        this.price = price;
        this.catalogID = catalogID;
    }

    public String getName() {
        return name;
    }

    public int getItemID() {
        return itemID;
    }

    public int getCatalogID() {
        return catalogID;
    }

    public double getPrice() {
        return price;
    }
}
