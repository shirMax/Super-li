package Backend.PersistenceLayer;

import Backend.BusinessLayer.Deliveries.Product;

public class ProductDTO {
    private String name;
    private int quantity;
    private int catNum;

    public ProductDTO(String name, int quantity, int catNum) {
        this.name = name;
        this.quantity = quantity;
        this.catNum = catNum;
    }

    public ProductDTO(Product product){
        this.name = product.getName();
        this.quantity = product.getQuantity();
        this.catNum = product.getCatNum();
    }
    public String toString(){
        return "name: " + name +
                " catalog number: " + catNum +
                " quantity: " + quantity + "\n";
    }

    public String getName() {
        return name;
    }

    public int getQuantity() {
        return quantity;
    }

    public int getCatNum() {
        return catNum;
    }
}
