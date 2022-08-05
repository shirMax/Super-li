package Backend.BusinessLayer.Stock.Objects;


import Backend.BusinessLayer.Interfaces.CollectOrderCallBack;
import Backend.BusinessLayer.Interfaces.CompleteOrderCallback;
import Backend.BusinessLayer.Interfaces.GetBranchOrdersCallBack;
import Backend.BusinessLayer.Interfaces.GetOrderItemsCallback;
import Backend.BusinessLayer.Stock.Interfaces.LocationEnum;
import Backend.BusinessLayer.Stock.Interfaces.StatusEnum;
import Backend.BusinessLayer.Stock.Structs.LocationInfo;
import Backend.BusinessLayer.Stock.Structs.StockQuantityInfo;
import Backend.BusinessLayer.Suppliers.OrderItem;
import Backend.BusinessLayer.Tools.Pair;
import Backend.PersistenceLayer.StockDAL.*;


import java.sql.SQLException;
import java.util.*;

public class StockManagementSystem {
    private final static int EXTRA_FOR_ORDER = 3;
    private String branchAddress;
    private CollectOrderCallBack collectOrderCallBack;

    private GetBranchOrdersCallBack branchOrdersCallBack;
    private GetOrderItemsCallback getOrderItemsCallback;
    private CompleteOrderCallback completeOrderCallback;
    private final Reporter reporter=new Reporter();

    private final ProductDAO products=new ProductDAO();
    private final CategoryDAO categories=new CategoryDAO();
    private final SalesDAO sales=new SalesDAO();
    private final DefectiveDAO defectives=new DefectiveDAO();

    public StockManagementSystem(String branchAddress) {
        this.branchAddress=branchAddress;
        new CreateDBStock();
//        new Thread(reporter).start();
    }

    private synchronized Collection<StockQuantityInfo> getShortageInfo() throws SQLException {
        Collection<StockQuantityInfo> shortageInfos= new LinkedList<>();
        List<Integer> productIds=products.getProductIds(branchAddress);
        for (Integer ID: productIds){
            Product p=products.getProduct(ID,branchAddress);
            if(p.checkForShortage())
                shortageInfos.add(p.productQuantityInfo());
        }
        return shortageInfos;
    }

    private synchronized Collection<StockQuantityInfo> getStockInfo() throws SQLException {
        Collection<StockQuantityInfo> stockInfos= new LinkedList<>();
        List<Integer> productIds=products.getProductIds(branchAddress);
        for (Integer ID: productIds)
            stockInfos.add(products.getProduct(ID,branchAddress).productQuantityInfo());

        return stockInfos;
    }

    private synchronized Collection<StockQuantityInfo> getPeriodicStockInfo(Collection<List<String>> categoryList) throws SQLException {
        Collection<StockQuantityInfo> periodicStockInfos= new LinkedList<>();
        Collection<Integer> ProductIds= new LinkedList<>();

        for (List<String> categories : categoryList)
            ProductIds.addAll(products.getProductIdsByCategory(categories,branchAddress));

        for(Integer productId : ProductIds)
            periodicStockInfos.add(products.getProduct(productId,branchAddress).productQuantityInfo());

        return periodicStockInfos;
    }

    private void activateLowQuantityAlert(StockQuantityInfo info) throws Exception {
//        String alert="Warning! product "+info.productId+" "+info.productName+" arrived at minimum quantity, current amount: "+info.amount+", current minimum quantity "+info.minimumQuantity;
//        reporter.sendMinimumQuantityAlert(alert);
        collectOrderCallBack.makeCollectOrder(new Pair<>(info.productId,info.minimumQuantity*EXTRA_FOR_ORDER),branchAddress);
    }

    private StatusEnum getStatusEnum(String status) {
        if(status.equalsIgnoreCase("Expired")) return StatusEnum.Expired;
        else if(status.equalsIgnoreCase("Defective")) return StatusEnum.Defective;
        else return null;
    }

    private synchronized LocationEnum getLocationEnum(String s){
        if(s.equalsIgnoreCase("Store")) return LocationEnum.Store;
        else if(s.equalsIgnoreCase("Warehouse")) return LocationEnum.Warehouse;
        else return null;
    }

    private synchronized void addMainCategory(String mainCat) throws Exception {
        if(checkCategoryExist(Arrays.asList(mainCat,"None","None")))
            throw new Exception("category "+ mainCat +" already exists in system");
        categories.addCategory(Arrays.asList(mainCat,"None","None"));
    }

    private synchronized void addSubCategory(String mainCat,String subCat) throws Exception {
        if(checkCategoryExist(Arrays.asList(mainCat,subCat,"None")))
            throw new Exception("category "+ mainCat+"/"+subCat+" already exists in system");
        if(!categories.checkIfExists(Arrays.asList(mainCat,"None","None")))
            addMainCategory(mainCat);
        categories.addCategory(Arrays.asList(mainCat,subCat,"None"));
    }

    private synchronized void addSubSubCategory(String mainCat,String subCat,String subSubCat) throws Exception {
        if(checkCategoryExist(Arrays.asList(mainCat,subCat,subSubCat)))
            throw new Exception("category "+ mainCat+"/"+subCat+"/"+subSubCat+" already exists in system");
        if(!categories.checkIfExists(Arrays.asList(mainCat,subCat,"None")))
            addSubCategory(mainCat,subCat);
        categories.addCategory(Arrays.asList(mainCat,subCat,subSubCat));
    }

    private synchronized boolean checkForRelatedProducts(List<String> categoryList) throws Exception {
        return products.checkForRelatedProducts(categoryList,branchAddress);
    }

    private synchronized void ValidateCategory(List<String> newCategory) {
        if(newCategory.size()!=3)
            throw new IllegalArgumentException("max number of categories is 3");

        if(newCategory.get(0).equalsIgnoreCase("None") | (newCategory.get(1).equalsIgnoreCase("None") & !newCategory.get(2).equalsIgnoreCase("None")))
            throw new IllegalArgumentException("Illegal category");
    }

    public synchronized void setCollectOrderCallBack(CollectOrderCallBack collectOrderCallBack) {
        this.collectOrderCallBack = collectOrderCallBack;
    }

    public synchronized void dailyUpdate() throws Exception {
        List<Integer> productIDs=products.getProductIds(branchAddress);
        for(Integer ID: productIDs) {
            Product p =products.getProduct(ID,branchAddress);
            p.updateDemand();
            products.updateProduct(p,branchAddress);
        }
    }

    public synchronized void shutDown(){
        reporter.Terminate();
    }

    public synchronized boolean checkCategoryExist(List<String> categoryList) throws SQLException {
        return categories.checkIfExists(categoryList);
    }

    public synchronized String generateStockReport() throws SQLException {
        Collection<StockQuantityInfo> stockInfos=getStockInfo();
        StringBuilder report= new StringBuilder("Stock report\n" +
                "=====================");
        for(StockQuantityInfo productInfo: stockInfos)
            report.append("\nproduct id: ").append(productInfo.productId).append(" product name: ").append(productInfo.productName).append(" total amount in stock: ").append(productInfo.amount);

        return report.toString();
    }


    public synchronized void addSaleForProducts(String saleName, double discount,Collection<Integer> productIDs) throws Exception {
        sales.addSale(saleName,discount,branchAddress);
        for(Integer productID: productIDs) {
            Product p = products.getProduct(productID,branchAddress);
            p.addSale(new Sale(saleName,discount));
            products.updateProduct(p,branchAddress);
        }
    }

    public synchronized void addSaleForCategories(String saleName, double discount,Collection<List<String>> categoriesList) throws Exception {
        sales.addSale(saleName,discount,branchAddress);
        List<Integer> productIDs=new LinkedList<>();
        List<List<String>> categoriesInSale = AllCategoriesInSale(categoriesList);
        for (List<String> category: categoriesInSale){
            productIDs.addAll(products.getProductIdsByCategory(category,branchAddress));
        }

        for(int ID : productIDs){
            Product p=products.getProduct(ID,branchAddress);
            p.addSale(new Sale(saleName,discount));
            products.updateProduct(p,branchAddress);
        }

    }

    private List<List<String>> AllCategoriesInSale(Collection<List<String>> categoriesList) throws SQLException {
        List<List<String>> subCategoriesToAdd = new LinkedList<>();
        for(List<String> category : categoriesList){
            if(category.size() != 3)
                throw new IllegalArgumentException("category length must br of length 3");
            if(category.get(0).equalsIgnoreCase("none"))
                throw new IllegalArgumentException("Main category cannot be None");
            if(category.get(1).equalsIgnoreCase("none"))
                subCategoriesToAdd.addAll(categories.getAllSubCategoriesUnder(category.get(0)));
            else if (category.get(2).equalsIgnoreCase("None"))
                subCategoriesToAdd.addAll(categories.getAllSubCategoriesUnder(category.get(0),category.get(1)));
        }
        return subCategoriesToAdd;
    }

    public synchronized void removeSale(String saleName) throws Exception {
        sales.deleteSale(saleName);
    }

    public String getAllAvailableSalesDescription() throws SQLException {
        StringBuilder categoriesDescription= new StringBuilder("All available sales\n" +
                "============================\n");
        List<Pair<String,Double>> salesList=sales.getAllSales();
        for (Pair<String,Double> sale : salesList)
            categoriesDescription.append("sale name: "+sale.getFirst()+" sale discount: "+sale.getSecond()).append("\n");

        if(categoriesDescription.toString().equals("All available sales\n" +
                "============================\n"))
            categoriesDescription.append("No sales so far!\n");
        return categoriesDescription.toString();
    }

    public synchronized void addCategory(List<String> newCategory) throws Exception {
        ValidateCategory(newCategory);

        String mainCat=newCategory.get(0);
        String subCat=newCategory.get(1);
        String subSubCat=newCategory.get(2);

        if(subCat.equalsIgnoreCase("None") & subSubCat.equalsIgnoreCase("None"))
            addMainCategory(mainCat);
        else
            if(subSubCat.equalsIgnoreCase("None"))
                addSubCategory(mainCat,subCat);
            else
                addSubSubCategory(mainCat,subCat,subSubCat);
    }

    public synchronized void removeCategory(List<String> categoryList) throws Exception
    {
        ValidateCategory(categoryList);

        String mainCat=categoryList.get(0);
        String subCat=categoryList.get(1);
        String subSubCat=categoryList.get(2);

        if(subSubCat.equalsIgnoreCase("None")& subCat.equalsIgnoreCase("None"))
            removeMainCat(mainCat);
        else
            if(subSubCat.equalsIgnoreCase("None"))
                removeSubCat(mainCat,subCat);
            else
                removeSubSubCat(mainCat,subCat,subSubCat);
    }

    private void removeMainCat(String mainCat) throws Exception {
        if(!checkCategoryExist(Arrays.asList(mainCat, "None", "None")))
            throw new Exception("category "+ mainCat +" doesn't exists in system.");

        List<List<String>> subCategories=categories.getAllSubCategoriesUnder(mainCat);
        for(List<String> subcategory: subCategories){
            if(checkForRelatedProducts(subcategory))
                throw new Exception("category "+ mainCat+" has attached products to it.");
        }
        for(List<String> subcategory: subCategories)
            categories.deleteCategory(subcategory);

    }

    private void removeSubCat(String mainCat, String subCat) throws Exception {
        if(!checkCategoryExist(Arrays.asList(mainCat, subCat, "None")))
            throw new Exception("category "+ mainCat +"/"+ subCat +" doesn't exists in system.");

        List<List<String>> subSubCategories=categories.getAllSubCategoriesUnder(mainCat,subCat);

        for(List<String> subcategory: subSubCategories)
        {
            if(checkForRelatedProducts(subcategory))
                throw new Exception("category "+ mainCat+"/"+ subCat+" has attached products to it.");
        }
        for(List<String> subcategory: subSubCategories)
            categories.deleteCategory(subcategory);

    }

    private void removeSubSubCat(String mainCat, String subCat, String subSubCat) throws Exception {
        if(!checkCategoryExist(Arrays.asList(mainCat, subCat, subSubCat)))
            throw new Exception("category "+ mainCat +"/"+ subCat +"/"+ subSubCat +" doesn't exists in system.");
        if(checkForRelatedProducts(Arrays.asList(mainCat, subCat, subSubCat)))
            throw new Exception("category "+ mainCat +"/"+ subCat +"/"+ subSubCat +" has attached products to it.");
        categories.deleteCategory(Arrays.asList(mainCat, subCat, subSubCat));
    }

    public synchronized String generatePeriodicStockReport(Collection<List<String>> categoriesList) throws SQLException {
        StringBuilder report= new StringBuilder("Periodic Stock Report\n" +
                "============================");
        Collection<StockQuantityInfo> stockInfos= getPeriodicStockInfo(categoriesList);
        for(StockQuantityInfo productInfo: stockInfos)
            report.append("\nCategory: ").append(productInfo.category).append(", product id: ").append(productInfo.productId).append(", product name: ").append(productInfo.productName).append(", total amount in stock: ").append(productInfo.amount);

        return report.toString();
    }

    public void moveProductsToDefectives(int productID, String location, String status, int amount) throws Exception {
        LocationEnum loc = getLocationEnum(location);
        StatusEnum stat= getStatusEnum(status);
        if(loc==null) throw new Exception("location can be only Store or Warehouse");
        if(stat==null) throw new Exception("status can be only Expired or Defective");
        Product p=products.getProduct(productID,branchAddress);
        p.setQuantityCallback(this::activateLowQuantityAlert);
        p.removeDefectives(loc, stat, amount);
        defectives.addDefectives(branchAddress,p.getProductID(),stat.name(), amount);
        products.updateProduct(p,branchAddress);
    }

    public synchronized String generateDefectivesReport() throws Exception {
        StringBuilder report= new StringBuilder("Defectives Report\n" +
                "============================");
        List<Pair<Integer,Integer>> Infos=defectives.getDefectiveInfos(branchAddress);
        for(Pair<Integer,Integer> info: Infos){
            int pid= info.getFirst();
            int amount=info.getSecond();
            for(int i=0;i<amount;i++)
                report.append("\nproduct id: ").append(pid).append(" product name: ").append(products.getProduct(pid,branchAddress).getProductName()).append(" status: ").append(StatusEnum.Defective);
        }
        if(report.toString().equals("Defectives Report\n" +
                "============================"))
            report.append("\nNo defectives so far!");
        return report.toString();
    }

    //todo implement for it presentation
    public synchronized String generateExpiredReport() throws Exception {
        StringBuilder report= new StringBuilder("Expired Report\n" +
                "============================");
        List<Pair<Integer,Integer>> Infos=defectives.getExpiredInfos(branchAddress);
        for(Pair<Integer,Integer> info: Infos){
            int pid= info.getFirst();
            int amount=info.getSecond();
            for(int i=0;i<amount;i++)
                report.append("\nproduct id: ").append(pid).append(" product name: ").append(products.getProduct(pid,branchAddress).getProductName()).append(" status: ").append(StatusEnum.Expired);
        }
        if(report.toString().equals("Expired Report\n" +
                "============================"))
            report.append("\nNo expired items so far!");
        return report.toString();
    }

    //todo implement for it presentation
    public synchronized String generateTotalDefectivesReport() throws Exception {
        return "Total Defectives Report\n" +
                "================================================" +
                "\n"+generateDefectivesReport() +
                "\n"+generateExpiredReport();
    }

    public synchronized String generateShortageReport() throws SQLException {
        StringBuilder report= new StringBuilder("Shortage Report\n" +
                "============================");
        Collection<StockQuantityInfo> infos=getShortageInfo();
        if(infos.size()==0)
            report.append("\nNo shortages so far!");
        else
            for(StockQuantityInfo productInfo: infos)
                report.append("\nproduct id: ").append(productInfo.productId).append(" product name: ").append(productInfo.productName).append(" total amount in stock: ").append(productInfo.amount);

        return report.toString();
    }

    public synchronized void addProduct(int productId, String productName, int manufacturerId, double sellingPrice, double costPrice,
                      int storeShelfId, int warehouseShelfId, int deliveryTime, List<String> categories) throws Exception {
        if(products.checkProductExistsBranch(productId,branchAddress))
            throw new Exception("The system already has a product with id "+productId);
        else {
            if(!checkCategoryExist(categories))
                throw new IllegalArgumentException("product categories doesn't exist");
            Product p=new Product(productId, productName, manufacturerId, sellingPrice, costPrice,
                    storeShelfId, warehouseShelfId, deliveryTime, categories);
            products.addProduct(p,branchAddress);
        }
    }

    public synchronized void removeProduct(int productID) throws SQLException {
        products.deleteProduct(productID,branchAddress);
    }

    public synchronized void addProductItems(int productID,int amount) throws Exception {
        Product p=products.getProduct(productID,branchAddress);
        p.addItem(amount);
        products.updateProduct(p,branchAddress);
    }

//    public synchronized void addProductItems(int productID,int amount,double costPrice) throws Exception {
////        Product p=products.getProduct(productID,branchAddress);
////        p.addItem(amount,costPrice);
////        p.setCostPrice(costPrice);
////        products.updateProduct(p,branchAddress);
////    }

    public synchronized void removeProductItems(int productID,int amount,String location) throws Exception{
        LocationEnum loc=getLocationEnum(location);
        if(loc==null) throw new Exception("location can be only Store or Warehouse");
        Product p=products.getProduct(productID,branchAddress);
        p.setQuantityCallback(this::activateLowQuantityAlert);
        p.removeItem(loc,amount);
        products.updateProduct(p,branchAddress);
    }

    public void moveProductItems(int productID, int amount,String from, String to) throws Exception {
        Product p=products.getProduct(productID,branchAddress);
        p.MoveItem(getLocationEnum(from),getLocationEnum(to),amount);
        products.updateProduct(p,branchAddress);
    }

    public String getAllAvailableCategoriesDescription() throws SQLException {
        StringBuilder categoriesDescription= new StringBuilder("All available categories\n" +
                "============================\n");
        List<List<String>> categoryList=categories.getAllCategories();
        for (List<String> category : categoryList)
            categoriesDescription.append(String.join("/",category)).append("\n");

        if(categoriesDescription.toString().equals("All available categories\n" +
                "============================\n"))
            categoriesDescription.append("No categories so far!\n");
        return categoriesDescription.toString();
    }

    public synchronized  String getProductSales(int productId) throws SQLException {
        return products.getProduct(productId,branchAddress).getSaleString();
    }

    public synchronized String getProductLocation(int productID) throws SQLException {
        LocationInfo info=products.getProduct(productID,branchAddress).getLocationInfo();
        return "Location info of product "+productID+": Store shelf id - "+info.storeShelfId+", Store amount - "+info.storeAmount+
                                                     " , Warehouse shelf id - "+info.wareHouseShelfId+", Warehouse amount - "+info.wareHouseAmount;
    }

    public synchronized String getProductName(int productID) throws SQLException {
        return products.getChainProductName(productID);
    }

    public synchronized int getProductManufacturer(int productID) throws SQLException {
        return products.getProduct(productID,branchAddress).getManufacturerId();
    }

    public synchronized int getProductTotalQuantity(int productID) throws SQLException {
        return products.getProduct(productID,branchAddress).getTotalQuantity();
    }

    public synchronized int getProductStoreQuantity(int productID) throws SQLException {
        return products.getProduct(productID,branchAddress).getStoreQuantity();
    }

    public synchronized int getProductWarehouseQuantity(int productID) throws SQLException {
        return products.getProduct(productID,branchAddress).getWarehouseQuantity();
    }

    public synchronized int getProductStoreShelfID(int productID) throws SQLException {
        return products.getProduct(productID,branchAddress).getStoreShelfId();
    }

    public synchronized int getProductWarehouseShelfID(int productID) throws SQLException {
        return products.getProduct(productID,branchAddress).getWarehouseShelfId();
    }

    public synchronized int getProductDeliveryTime(int productID) throws SQLException {
        return products.getProduct(productID,branchAddress).getDeliveryTime();
    }

    public synchronized double getProductDemand(int productID) throws SQLException {
        return products.getProduct(productID,branchAddress).getDemand();
    }

    public synchronized List<String> getProductCategories(int productID) throws SQLException {
        return products.getProduct(productID,branchAddress).getCategories();
    }

    public String getProductInformation(int productId) throws SQLException {
        return products.getProduct(productId,branchAddress).getInformation();
    }

    public Double getProductSellingPrice(int productId) throws SQLException {
        return products.getProduct(productId,branchAddress).getSellingPrice();
    }

    public double getProductSellingPriceIncludeTopSale(int productId) throws SQLException {
        return products.getProduct(productId,branchAddress).getSellingPriceTopOne();
    }

    public double getProductSellingPriceIncludeAllSales(int productId) throws SQLException {
        return products.getProduct(productId,branchAddress).getSellingPriceIncludeAll();
    }

    public Double getProductCostPrice(int productId) throws SQLException {
        return products.getProduct(productId,branchAddress).getCostPrice();
    }

    public Integer getMinimumQuantity(int productId) throws SQLException {
        return products.getProduct(productId,branchAddress).getMinimumQuantity();
    }

    public Reporter getReporter(){
        return reporter;
    }

    public void updateProductManufacturer(int productID,int manufacturerID) throws Exception {
        Product p=products.getProduct(productID,branchAddress);
        p.setManufacturerId(manufacturerID);
        products.updateProduct(p,branchAddress);
    }

    public void updateProductStoreShelfID(int productID,int storeShelfID) throws Exception {
        Product p=products.getProduct(productID,branchAddress);
        p.setStoreShelfId(storeShelfID);
        products.updateProduct(p,branchAddress);
    }

    public void updateProductWarehouseShelfID(int productID,int warehouseShelfID) throws Exception {
        Product p=products.getProduct(productID,branchAddress);
        p.setWarehouseShelfId(warehouseShelfID);
        products.updateProduct(p,branchAddress);
    }

    public void updateProductDeliveryTime(int productID,int deliveryTime) throws Exception {
        Product p=products.getProduct(productID,branchAddress);
        p.setDeliveryTime(deliveryTime);
        products.updateProduct(p,branchAddress);
    }

    public void updateProductCategories(int productID,List<String> categories) throws Exception{
        if(checkCategoryExist(categories)) {
            Product p=products.getProduct(productID,branchAddress);
            p.setCategories(categories);
            products.updateProduct(p,branchAddress);
        }
        else throw new Exception("the new category "+categories.get(0)+"/"+categories.get(1)+"/"+categories.get(2)+" is not in the system");
    }

    public void updateSellingPrice(int productId, double price) throws Exception {
        Product p=products.getProduct(productId,branchAddress);
        p.setSellingPrice(price);
        products.updateProduct(p,branchAddress);
    }

    public void updateProductCostPrice(int productId, double price) throws Exception {
        Product p=products.getProduct(productId,branchAddress);
        p.setCostPrice(price);
        products.updateProduct(p,branchAddress);
    }

    public void updateProductName(int productId, String newName) throws Exception {
        Product p=products.getProduct(productId,branchAddress);
        p.setProductName(newName);
        products.updateProduct(p,branchAddress);
    }

    //for tests

    public void changeProductDemand(int productId, double demand) throws Exception {
        Product p=products.getProduct(productId,branchAddress);
        p.setDemand(demand);
        products.updateProduct(p,branchAddress);
    }

    public void changeDaysInStore(int productID, int days) throws Exception {
        Product p = products.getProduct(productID,branchAddress);
        p.setDaysInStore(days);
        products.updateProduct(p,branchAddress);
    }

    public void setGetOrderItemsCallback(GetOrderItemsCallback getOrderItemsCallback) {
        this.getOrderItemsCallback = getOrderItemsCallback;
    }

    public void setBranchOrdersCallBack(GetBranchOrdersCallBack branchOrdersCallBack) {
        this.branchOrdersCallBack = branchOrdersCallBack;
    }

    public void setCompleteOrderCallback(CompleteOrderCallback completeOrderCallback) {
        this.completeOrderCallback = completeOrderCallback;
    }

    public List<Integer> getAllPendingOrders(){
        if(branchOrdersCallBack == null)
            throw new IllegalArgumentException("branch callback is null");
        return branchOrdersCallBack.getBranchOrders(branchAddress);
    }

    public List<OrderItem> getOrderItems(int orderId){
        if(getOrderItemsCallback == null)
            throw new IllegalArgumentException("get order call back is null");
        return getOrderItemsCallback.getOrderItems(orderId,branchAddress);
    }

    public void completeOrder(List<OrderItem> items,int orderId) throws Exception {
        if(completeOrderCallback == null)
            throw new IllegalArgumentException("complete order callback is null");
        for(OrderItem orderItem : items){
            addProductItems(orderItem.getItemID(),orderItem.getAmount());
        }
        completeOrderCallback.completeOrder(orderId,items);
    }

    public void setBranchAddress(String branchAddress) {
        this.branchAddress = branchAddress;
    }

}