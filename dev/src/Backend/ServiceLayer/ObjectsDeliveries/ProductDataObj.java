package Backend.ServiceLayer.ObjectsDeliveries;

import Backend.BusinessLayer.Deliveries.Product;

public class ProductDataObj {
    private String name;
    private int quantity;
    private int catNum;

    public ProductDataObj(String name, int quantity, int catNum) {
        this.name = name;
        this.quantity = quantity;
        this.catNum = catNum;
    }

    public ProductDataObj(Product product){
        this.name = product.getName();
        this.quantity = product.getQuantity();
        this.catNum = product.getCatNum();
    }
    public String toString(){
        return "name: " + name +
                " catalog number: " + catNum +
                " quantity: " + quantity + "\n";
    }
}
