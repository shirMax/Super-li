package Backend.BusinessLayer.Stock.Objects;

public class Sale {

    private String name;
    private double discount;

    public Sale(String name, double discount) {
        checkInputName(name);
        checkInputDiscount(discount);
        this.name = name;
        this.discount = discount;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        checkInputDiscount(discount);
        this.discount = discount;
    }

    private void checkInputDiscount(double discount){
        if(discount < 0)
            throw new IllegalArgumentException("sale discount cant be negative");
    }

    private void checkInputName(String name){
        if(name.equalsIgnoreCase(""))
            throw new IllegalArgumentException("sale name cant be empty");
    }
}
