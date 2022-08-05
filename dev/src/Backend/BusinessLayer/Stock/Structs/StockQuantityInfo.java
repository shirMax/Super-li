package Backend.BusinessLayer.Stock.Structs;

public class StockQuantityInfo {
    public int productId;
    public String productName;
    public int amount;
    public int minimumQuantity;

    public String category;

    public StockQuantityInfo(int productId, String productName, int amount, String category,int minimumQuantity) {
        this.productId = productId;
        this.productName = productName;
        this.amount = amount;
        this.category=category;
        this.minimumQuantity = minimumQuantity;
    }
}
