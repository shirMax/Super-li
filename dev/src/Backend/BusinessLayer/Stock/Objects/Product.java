package Backend.BusinessLayer.Stock.Objects;

import Backend.BusinessLayer.Stock.Interfaces.LocationEnum;
import Backend.BusinessLayer.Stock.Interfaces.StatusEnum;
import Backend.BusinessLayer.Stock.Interfaces.QuantityWarningCallback;
import Backend.BusinessLayer.Stock.Structs.*;

import java.util.*;

public class Product{

    private static double MIN_SALE_PERCENTAGE = 0;
    private static int MIN_STORE_QUANTITY = 0;
    private static int MIN_WAREHOUSE_QUANTITY = 0;


    private int productId;
    private String productName;
    private int manufacturerId;
    private double sellingPrice;
    private double costPrice;
    private double demand;
    private int storeShelfId;
    private int warehouseShelfId;
    private HashMap<LocationEnum,Integer> quantity;
    private List<Double> costPriceHistory;
    private int deliveryTime;
    private int soldToday;
    private int daysInStore;
    private List<Sale> sales;
    private QuantityWarningCallback quantityCallback;
    private List<String> categories;

    public Product(int productId, String productName, int manufacturerId, double sellingPrice, double costPrice,
                   int storeShelfId, int warehouseShelfId, int deliveryTime, List<String> categories) {
        this.productId = productId;
        this.productName = productName;
        this.manufacturerId = manufacturerId;
        this.sellingPrice = sellingPrice;
        this.storeShelfId = storeShelfId;
        this.warehouseShelfId = warehouseShelfId;
        this.deliveryTime = deliveryTime;
        this.categories = categories;
        this.costPriceHistory = new LinkedList<>();
        quantity = new HashMap<>();
        quantity.put(LocationEnum.Store,0);
        quantity.put(LocationEnum.Warehouse,0);
        demand = 0;
        soldToday = 0;
        daysInStore =0;
        sales = new ArrayList<>();
        setCostPrice(costPrice);
    }

    protected void updateDemand(){
        double totalSold=demand* daysInStore +soldToday;
        daysInStore++;
        soldToday=0;
        demand = totalSold/ daysInStore;
    } //checked

    protected void addSale(Sale sale){   //checked
        if(sale.getDiscount() <= MIN_SALE_PERCENTAGE){
            throw new IllegalArgumentException("Sale percentage must be positive");
        }
        if(inSale(sale.getName())){
            throw new IllegalArgumentException("Sale already exits");
        }
        sales.add(sale);
    }

    protected boolean inSale(String saleName){ //checked
        for(Sale sale: sales){
            if(saleName.equalsIgnoreCase(sale.getName()))
                return true;
        }
        return false;
    }

    protected void removeSale(String saleName){ //checked
        if(!inSale(saleName))
            throw new IllegalArgumentException("Sale not exits");
        sales.remove(saleName);
    }

    protected DefectiveInfo moveDefectives(LocationEnum location,StatusEnum status){ //checked
        removeItem(location);
        return new DefectiveInfo(productId,getProductName(),status);
    }

    private void removeItem(LocationEnum location) {
        checkEnoughQuantity(location,1);
        quantity.put(location,quantity.get(location)-1);
        soldToday++;
    }

    //check for exception text
    private void checkEnoughQuantity(LocationEnum location,int amount){
        int minQuantity = getMinQuantity(location);
        if((quantity.get(location) < minQuantity+amount))
            throw new IllegalArgumentException(String.format("Not enough items at %s,requested amount: %d current available amount: %d",location,amount,quantity.get(location)));

    }

    private int getMinQuantity(LocationEnum location) {
        if(location == LocationEnum.Store)
            return MIN_STORE_QUANTITY;
        if(location == LocationEnum.Warehouse)
            return MIN_WAREHOUSE_QUANTITY;
        throw new IllegalArgumentException(String.format("Unknown location: the system couldn't find location %s",location));
    }


    protected List<DefectiveInfo> removeDefectives(LocationEnum location,StatusEnum status,int amount){ //checked
        checkEnoughQuantity(location,amount);
        List<DefectiveInfo> defectivesList = new LinkedList<>();
        for(int i = 0; i < amount;i++){
            defectivesList.add(moveDefectives(location,status));
        }
        return defectivesList;
    }

    protected int getTotalQuantity(){
        return quantity.get(LocationEnum.Warehouse)+quantity.get(LocationEnum.Store);
    }

    protected void MoveItem(LocationEnum oldlocation,LocationEnum newlocation){ //checked
        removeItem(oldlocation);
        addItem(newlocation);
    }

    private void addItem(LocationEnum location) {
        quantity.put(location,quantity.get(location)+1);
    }

    protected void MoveItem(LocationEnum oldlocation,LocationEnum newLocation,int amount){ //checked
        checkEnoughQuantity(oldlocation,amount);
        for(int i = 0; i <amount; i++){
            MoveItem(oldlocation,newLocation);
        }
    }

    protected void removeItem(LocationEnum location,int amount) throws Exception { //checked
        checkEnoughQuantity(location,amount);
        quantity.put(location,quantity.get(location) - amount);
        soldToday+=amount;
        CheckQuantityWarning();
    }



    protected void addItem(int amount){ //checked
        if(amount < 0)
            throw new IllegalArgumentException("illegal amount. amount needs to be positive");
        quantity.put(LocationEnum.Warehouse,quantity.get(LocationEnum.Warehouse)+amount);
    }

    protected void addItem(int amount,double costPrice){ //checked
        if(amount < 0)
            throw new IllegalArgumentException("illegal amount. amount needs to be positive");
        setCostPrice(costPrice);
        quantity.put(LocationEnum.Warehouse,quantity.get(LocationEnum.Warehouse)+amount);
    }


    protected StockQuantityInfo productQuantityInfo(){
        return new StockQuantityInfo(productId,productName,getTotalQuantity(),getCategoryString(),calculateMinQuantity());
    }


    private void CheckQuantityWarning() throws Exception {
        if(checkForShortage())
            quantityCallback.call(productQuantityInfo());
    } //checked

    //returns true if the total quantity is lower than or equal to the minimum quantity
    protected boolean checkForShortage(){
        return getTotalQuantity() <= calculateMinQuantity();
    }

    private int calculateMinQuantity(){
        return (int) demand*deliveryTime;
    } //checked

    public String getProductName() {
        return productName;
    }

    public int getManufacturerId() {
        return manufacturerId;
    }

    public double getSellingPrice() {
        return sellingPrice;
    }

    public double getSellingPriceTopOne(){
        if(sales.isEmpty())
            return sellingPrice;
        double discount = getTopDiscount();
        return makeADiscount(discount,sellingPrice);
    }

    private double getTopDiscount() {
        if(sales.isEmpty())
            throw new IllegalArgumentException("sales list is empty");
        double max = sales.get(0).getDiscount();
        for(Sale sale : sales){
            if(sale.getDiscount() > max)
                max = sale.getDiscount();
        }
        return max;
    }


    private double makeADiscount(double percentage,double price){
        return price - price*(percentage/100);
    }

    public double getSellingPriceIncludeAll(){
        if(sales.isEmpty())
            return sellingPrice;
        double finalPrice = sellingPrice;
        for(Sale sale : sales){
            finalPrice = makeADiscount(sale.getDiscount(),finalPrice);
        }
        return finalPrice;
    }

    public double getCostPrice() {
        return costPrice;
    }

    public int getStoreQuantity() {
        return quantity.get(LocationEnum.Store);
    }

    public int getStoreShelfId() {
        return storeShelfId;
    }

    public int getWarehouseQuantity() {
        return quantity.get(LocationEnum.Warehouse);
    }

    public int getWarehouseShelfId() {
        return warehouseShelfId;
    }

    public int getDeliveryTime() {
        return deliveryTime;
    }

    public List<String> getCategories() {
        return categories;
    }

    public int getProductID() {
        return productId;
    }

    public double getDemand() {
        return demand;
    }

    public LocationInfo getLocationInfo() {
        return new LocationInfo(warehouseShelfId,quantity.get(LocationEnum.Warehouse),storeShelfId,quantity.get(LocationEnum.Store));
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public void setManufacturerId(int manufacturerId) {
        this.manufacturerId = manufacturerId;
    }

    public void setSellingPrice(double sellingPrice) {
        this.sellingPrice = sellingPrice;
    }

    public void setCostPrice(double costPrice) {
        costPriceHistory.add(costPrice);
        this.costPrice = costPrice;
    }

    public void setStoreQuantity(int storeQuantity) {
        quantity.put(LocationEnum.Store,storeQuantity);
    }

    public void setStoreShelfId(int storeShelfId) {
        this.storeShelfId = storeShelfId;
    }

    public void setWarehouseQuantity(int warehouseQuantity) {
        quantity.put(LocationEnum.Warehouse,warehouseQuantity);
    }

    public void setWarehouseShelfId(int warehouseShelfId) {
        this.warehouseShelfId = warehouseShelfId;
    }

    public void setDeliveryTime(int deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public void setQuantityCallback(QuantityWarningCallback quantityCallback) {
        this.quantityCallback = quantityCallback;
    }

    public void setCategories(List<String> categories) {
        this.categories = categories;
    }


    public String getInformation() {
        return String.format("\nProduct ID: %d \n" +
                        "Name: %s \n" +
                        "Manufacturer ID: %d \n" +
                        "Selling price: %,.2f \n" +
                        "Cost price: %,.2f \n" +
                        "Store shelf ID: %d \n" +
                        "Store shelf quantity: %d \n" +
                        "Warehouse shelf ID: %d \n" +
                        "Warehouse shelf quantity: %d \n" +
                        "Delivery time(in days): %d \n" +
                        "Average daily demand: %,.2f  \n" +
                        "Categories %s\n",productId,productName,manufacturerId, getSellingPrice(),costPrice,storeShelfId,
                                    getStoreQuantity(),warehouseShelfId,getWarehouseQuantity(),deliveryTime,
                                    demand,getCategoryString());
    }

    private String getCategoryString() {
        return categories.get(2).equals("None") ?
                categories.get(1).equals("None") ?
                        categories.get(0) :
                        categories.get(0) +'/'+ categories.get(1) :
                categories.get(0) +'/'+ categories.get(1) +'/'+ categories.get(2);
    }

    public String getSaleString() {
        String output = "sales list\n==========\n";
        for(Sale sale : sales) {
            output += String.format("Sale Name: %s Discount percentages: %.2f ,", sale.getName(), sale.getDiscount());
        }
        return output.substring(0,output.length()-1);
    }

    public void setDemand(double demand) {
        this.demand = demand;
    }

    public Integer getMinimumQuantity() {
        return calculateMinQuantity();
    }

    public void setDaysInStore(int daysInStore) {
        this.daysInStore = daysInStore;
    }

    public void setSoldToday(int soldToday) {
        this.soldToday = soldToday;
    }

    public int getSoldToday() {
        return soldToday;
    }

    public int getDaysInStore() {
        return daysInStore;
    }


    public List<Sale> getSales() {
        return sales;
    }

    public void setSales(List<Sale> sales) {
        this.sales = sales;
    }
}
