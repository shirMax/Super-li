package Backend.BusinessLayer.Suppliers;

public class Item {
    private int itemID;
    private String name;
    private int catalogID;
    private double price;

    public Item(int itemID,int catalogID, String name, double price) {
        this.itemID = itemID;
        this.name = name;
        this.price = price;
        this.catalogID = catalogID;
    }

    public int getItemID() {
        return itemID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getCatalogID() {
        return catalogID;
    }
}
