package Backend.BusinessLayer.Deliveries;

import Backend.PersistenceLayer.DeliveriesDal.ProductsSiteDocDAO;
import Backend.PersistenceLayer.ProductDTO;

public class Product {
    private String name;
    private int quantity;
    private int catNum;
    private int siteDocID1;
    private int siteDocID2;
    private ProductsSiteDocDAO productsSiteDocDAO;

    public Product(ProductsSiteDocDAO productsSiteDocDAO, int catNum, String name, int quantity,
                   int siteDocID1, int siteDocID2) {
        this.catNum = catNum;
        this.name = name;
        this.quantity = quantity;
        this.productsSiteDocDAO = productsSiteDocDAO;
        this.siteDocID1 = siteDocID1;
        this.siteDocID2 = siteDocID2;
    }

    public Product(ProductDTO product){
        this.name = product.getName();
        this.quantity = product.getQuantity();
        this.catNum = product.getCatNum();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        productsSiteDocDAO.updateQuantity(siteDocID1,siteDocID2,catNum,quantity);
        this.quantity = quantity;
    }

    public int getCatNum() {
        return catNum;
    }

    public void setCatNum(int catNum) {
        this.catNum = catNum;
    }

    public boolean isEmpty() {
        return quantity==0;
    }
}
