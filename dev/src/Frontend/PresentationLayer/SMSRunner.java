package Frontend.PresentationLayer;

import Backend.ServiceLayer.ObjectsSupplier.SOrderItem;
import Backend.ServiceLayer.Response;
import Backend.ServiceLayer.ResponseT;
import Backend.BusinessLayer.Stock.Objects.MockEmployee;
import Backend.ServiceLayer.Service;


import java.util.*;
import java.util.stream.Collectors;

public class SMSRunner {
    final static int MAIN_REPORTS = 1;
    final static int MAIN_PRODUCT = 2;
    final static int MAIN_SALES = 3;
    final static int MAIN_CATEGORIES = 4;
    final static int MAIN_PENDING_ORDERS = 5;
    final static int MAIN_TESTS_OPTIONS = 6;
    final static int BACK_OR_EXIT_INPUT = 0;
    final static int MAIN_TOTAL_CATEGORIES_COUNT = 6;
    final static int MAX_PRODUCT_ACTIONS_AMOUNT = 100;

    Service serviceSMS;
    boolean shouldStopMainLoop = false;

    public SMSRunner(Service serviceSMS) {
        this.serviceSMS = serviceSMS;

    }

    public void start(){
        printWelcomeMessage();
        String branch = getBranch();
        if(branch == null)
            return;
        serviceSMS.setBranch(branch);
//        int i = takeInt("to load data type 1 \nto continue without loading type 2",1,2);
//        if(i == 1)
//            loadData();
        while(!shouldStopMainLoop){
            printMainOptions();
            int input = takeInt(BACK_OR_EXIT_INPUT,MAIN_TOTAL_CATEGORIES_COUNT);
            parseInput(input);
        }
    }

    public void restrictStart(){
        printWelcomeMessage();
        String branch = getBranch();
        if(branch == null)
            return;
        serviceSMS.setBranch(branch);
        handleReports();
    }

    private void parseInput(int input){
        switch (input) {
            case (MAIN_REPORTS):
                handleReports();
                break;
            case (MAIN_PRODUCT):
                 handleProducts();
                 break;
            case (MAIN_SALES):
                handleSales();
                break;
            case (MAIN_CATEGORIES):
                handleCategories();
                break;
            case (MAIN_PENDING_ORDERS) :
                handlePendingOrders();
                break;
            case (BACK_OR_EXIT_INPUT) :
                shouldStopMainLoop = true;
                break;
            case (MAIN_TESTS_OPTIONS) :
                handleTestsOptions();
                break;
            default :
                printInvalidInputMessage();
        }
    }

    private void handlePendingOrders() {
        ResponseT<List<Integer>> pendingOrdersR = serviceSMS.getAllPendingOrders();
        if(pendingOrdersR.isErrorOccurred()) {
            System.out.println("something went wrong: " + pendingOrdersR.errorMessage);
            return;
        }
        List<Integer> pendingOrders = pendingOrdersR.Value;
        if(pendingOrders.isEmpty()){
            System.out.println("No pending orders so far!");
            return;
        }
        int orderNumber = -1;
        boolean shouldStop = false;
        while(!shouldStop){
            printPendingMenu(pendingOrders);
            orderNumber = takeInt();
            if(orderNumber == -1)
                return;
            if(!pendingOrders.contains(orderNumber)){
                System.out.println("please choose order From the list!");
            }
            if(pendingOrders.contains(orderNumber)){
                shouldStop = true;
            }
        }
        handleSpecificOrder(orderNumber);
    }

    private void handleSpecificOrder(int orderNumber) {
        ResponseT<List<SOrderItem>> orderItemsR = serviceSMS.getOrderItems(orderNumber);
        if(orderItemsR.isErrorOccurred()){
            System.out.println("something went wrong: "+orderItemsR.errorMessage);
            return;
        }
        List<SOrderItem> orderItems = orderItemsR.Value;
        printOrderItems(orderItems);
        System.out.println("to edit products list please type 1 \nto approve order arrived and add items to stock type 2\nto go back type 0 \n");
        int userChoose = takeInt(0,2);
        if(userChoose == 0)
            return;
        if(userChoose == 1){
            orderItems = handleEditItems(orderItems);
            if(orderItems == null)
                return;
        }
        //user choose 2 or finish with edit
        Response res = serviceSMS.completeOrder(orderItems, orderNumber);
        handleResponse(res);
    }

    private List<SOrderItem> handleEditItems(List<SOrderItem> orderItems) {
        while (true){
            int deleteUpdate = takeInt("to update amount type 1\nto delete product from the list type 2\nto finish edit and approve order type 3\nto return to main menu type 0",0,3);
            if(deleteUpdate == 0)
                return null;
            if(deleteUpdate == 3)
                return orderItems;
            if(deleteUpdate == 2){
                SOrderItem currentItem = getCurrentItem(orderItems);
                orderItems.remove(currentItem);
            }
            if(deleteUpdate == 1){
                int amount = takeInt("please type new amount (must be positive)",0,Integer.MAX_VALUE);
                SOrderItem currentItem = getCurrentItem(orderItems);
                currentItem.setAmount(amount);
            }
            printOrderItems(orderItems);
        }


    }

    private SOrderItem getCurrentItem(List<SOrderItem> orderItems) {
        while(true){
            int itemId = takeInt("type item id (must be positive)",0,Integer.MAX_VALUE);
            SOrderItem currentItem = null;
            for(SOrderItem orderItem : orderItems){
                if(orderItem.getItemID() == itemId)
                    currentItem = orderItem;
            }
            if(currentItem == null){
                System.out.println("item not in list please try again.");
                continue;
            }
            return currentItem;
        }
    }

    private void printOrderItems(List<SOrderItem> orderItems) {
        System.out.println("order items: \n ");
        for(SOrderItem orderItem : orderItems){
            System.out.println("item id: "+ orderItem.getItemID() + ", item name: "+orderItem.getName() + " amount: "+orderItem.getAmount() + "\n");
        }
    }

    private void printPendingMenu(List<Integer> value) {
        System.out.println("please choose order:\n "+
                String.join("\n",value.stream().map(String::valueOf).collect(Collectors.toList())) + "\n"
        +"type -1 to return\n");
    }


    //todo remove this: for tests
    private void handleTestsOptions() {
        final int changeDemand = 1;
        final int getMinimumQuantity = 2;
        final int daysInStore = 3;
        final int totalOptionsCount = 3;
        int input = -1;
        while(input != BACK_OR_EXIT_INPUT){
            printTestOptionsMenu();
            input = takeInt(BACK_OR_EXIT_INPUT,totalOptionsCount);
            switch (input) {
                case (changeDemand) :
                    handleChangeDemand();
                    break;
                case (getMinimumQuantity) :
                    handleMinimumQuantity();
                    break;
                case (daysInStore) :
                    handleDaysInStore();
                    break;
            }
        }
    }

    private void handleDaysInStore() {
        int productID = takeInt("product id: ",0,Integer.MAX_VALUE);
        int days = takeInt("type amount to set: ",0,Integer.MAX_VALUE);
        Response res = serviceSMS.setDaysInStore(productID,days);
        handleResponse(res);
    }

    private void handleMinimumQuantity() {
        int productId = takeInt("type product id",0,Integer.MAX_VALUE);
        ResponseT<Integer> res = serviceSMS.getProductMinimumQuantity(productId);
        handleResponse(res);
    }

    //todo remove this: for tests
    private void handleChangeDemand() {
        int productId = takeInt("type product id",0,Integer.MAX_VALUE);
        double demand = takeDouble("type new demand: " ,0,20000000);
        Response res = serviceSMS.changeProductDemand(productId,demand);
        handleResponse(res);
    }

    //todo remove this: for tests
    private void printTestOptionsMenu() {
        System.out.println("\n-------tests menu--------- \n" +
                           "1. to change product demand type 1 \n" +
                           "2. to get product minimum quantity type 2 \n" +
                           "3. to change days in store type 3 \n" +
                           "to go back type 0\n" );
    }

    private void handleProducts() {
        printProductMenu();
        final int addProduct = 1;
        final int removeProduct =2;
        final int handleSpecificProduct =3;
        int input = takeInt(0,3);
        switch (input){
            case(addProduct) :
                handleAddProduct();
                break;
            case(removeProduct) :
                handleRemoveProduct();
                break;
            case(handleSpecificProduct) :
                handleSpecificProduct();;
                break;
        }
    }

    private void handleRemoveProduct() {
        int productID = takeInt("please type product id",0,Integer.MAX_VALUE);
        Response res = serviceSMS.removeProduct(productID);
        handleResponse(res);
    }

    private void printProductMenu() {
        System.out.println(" \n1.to add a new product please type 1 \n" +
                           "2.to remove an existing product type 2 \n" +
                           "3.to enter menu for specific product type 3 \n" +
                           "to go back to main menu type 0 \n");
    }

    private void handleAddProduct() {
        int productId = takeInt("please type product ID",0,Integer.MAX_VALUE);
        String productName = getStringInput("please type product name");
        int manufacturer = takeInt("please type manufacturer id",0,Integer.MAX_VALUE);
        double sellingPrice = takeDouble("please type selling price",0,Integer.MAX_VALUE);
        double costPrice = takeDouble("please type cost price",0,Integer.MAX_VALUE);
        int shelfIDStore = takeInt("please enter store shelf number",0,Integer.MAX_VALUE);
        int shelfIdWarehouse = takeInt("please enter warehouse shelf id",0,Integer.MAX_VALUE);
        int deliveryTime = takeInt("please enter delivery time (days)",0,1000);
        List<String> categories = getCategory();
        Response res = serviceSMS.addProduct(productId,productName,manufacturer,sellingPrice,costPrice,shelfIDStore,shelfIdWarehouse,deliveryTime,categories);
        handleResponse(res);
    }




    private void handleReports(){
        final int stockReport = 1;
        final int periodicStockReport = 2;
        final int totalDefectivesReport = 3;
        final int defectivesReport= 4;
        final int expiredReport = 5;
        final int shortageReport = 6;
        final int totalOptionsCount = 6;
        int input = -1;
        while(input != BACK_OR_EXIT_INPUT){
            printHandleReportsMenu();
            input = takeInt(BACK_OR_EXIT_INPUT,totalOptionsCount);
            switch (input) {
                case (stockReport) :
                    handleStockReport();
                    break;
                case (periodicStockReport) :
                    handlePeriodicStockReport();
                    break;
                case (totalDefectivesReport) :
                    handleTotalDefectivesReport();
                    break;
                case (defectivesReport) :
                    handleDefectivesReport();
                    break;
                case (expiredReport) :
                    handleExpiredReport();
                    break;
                case (shortageReport) :
                    handleShortageReport();
                    break;
            }
        }
    }

    private void handleExpiredReport() {
        ResponseT<String> res = serviceSMS.generateExpiredReport();
        handleResponse(res);
    }

    private void handleTotalDefectivesReport() {
        ResponseT<String> res = serviceSMS.generateTotalDefectivesReport();
        handleResponse(res);
    }

    private void handleShortageReport() {
        ResponseT<String> res = serviceSMS.generateShortageReport();
        handleResponse(res);

    }

    private void handleDefectivesReport() {
        ResponseT<String> res = serviceSMS.generateDefectivesReport();
        handleResponse(res);
    }

    private void handlePeriodicStockReport() {
        List<List<String>> categories = getCategories();
        if (categories==null) return;
        ResponseT<String> res = serviceSMS.generatePeriodicStockReport(categories);
        handleResponse(res);
    }

    private List<List<String>> getCategories() {
        System.out.print("\nplease type the categories you like to choose.\n " +
                         "format: Main_category/sub_category/sub_sub_category Main_category/sub_category/sub_sub_category ...\n " +
                         "You are not allowed to put whitespace in a category name (use underscore instead, for example Dairy_products)\n " +
                         "You are not allowed to use the name \"none\" for category.\n " +
                         "if you like to choose more general category , you can leave the sub categories slot empty\n " +
                        "example: Main_category Main_category/sub_category Main_category Main_category/sub_category/sub_sub_category ...\n" );
        String input = getStringInput();
        return parseCategoriesInput(input);
    }

    private List<List<String>> parseCategoriesInput(String input) {
        String[] allCategories = input.split(" ");
        List<List<String>> allCategoriesLists = new ArrayList<>();
        for(String cat : allCategories){
            String[] categoryArray = cat.split("/");

            LinkedList<String> categoryList = asList(categoryArray);
            if(categoryList.size()>3) {
                printSomethingWentWrongMessage();
                return null;
            }
            while(categoryList.size()<3) categoryList.add("None");
            allCategoriesLists.add(categoryList);
        }
        return allCategoriesLists;

    }

    private String getStringInput() {
        System.out.print("please type here: ");
        Scanner sc = new Scanner(System.in);
        return sc.nextLine();
    }

    private String getStringInput(String msg) {
        System.out.println(msg);
        return getStringInput();
    }


    private void handleStockReport() {
        ResponseT<String> res = serviceSMS.generateStockReport();
        handleResponse(res);
    }

    private void printHandleReportsMenu() {
        System.out.println("\n-------Reports Menu-------- \n" +
                           "1.for stock report please type 1 \n" +
                           "2.for periodic stock report please type 2 \n" +
                           "3.for total defectives report please type 3 \n" +
                           "4.for defectives report please type 4 \n" +
                           "5.for expired report please type 5 \n" +
                           "6.for shortage report please type 6 \n" +
                           "to go back to main menu please type 0\n");
    }

    private void handleSpecificProduct(){
        final int Information = 1;
        final int name = 2;
        final int manufacturer = 3;
        final int TOTALQuantity = 4;
        final int storeQuantity = 5;
        final int warehouseQuantity = 6;
        final int storeShelf = 7;
        final int warehouseShelf = 8;
        final int deliveryTime = 9;
        final int demand = 10;
        final int categories = 11;
        final int moveToDefectives = 12;
        final int addNewItems = 13;
        final int removeItems = 14;
        final int moveItemFromWarehouse = 15;
        final int moveItemsFromStore = 16;
        final int itemFullLocation = 17;
        final int sellingPrice = 18;
        final int costPrice = 19;
        final int sales = 20;
        final int totalOptionsCount = 20;
        int productId = getProductId();
        int input = -1;
        while(input != BACK_OR_EXIT_INPUT){
            printHandleProductInfoMenu();
            input = takeInt(BACK_OR_EXIT_INPUT,totalOptionsCount);
            switch (input) {
                case (Information) :
                    handleProductInformationInput(productId);
                    break;
                case (name) :
                    handleProductName(productId);
                    break;
                case (manufacturer) :
                    handleManufacturer(productId);
                    break;
                case (TOTALQuantity) :
                    handleTotalQuantity(productId);
                    break;
                case (warehouseQuantity) :
                    handleWarehouseQuantity(productId);
                    break;
                case (storeShelf) :
                    handleStoreShelf(productId);
                    break;
                case (warehouseShelf) :
                    handleWarehouseShelf(productId);
                    break;
                case (storeQuantity) :
                    handleStoreQuantity(productId);
                    break;
                case (deliveryTime) :
                    handleDeliveryTime(productId);
                    break;
                case (demand) :
                    handleDemand(productId);
                    break;
                case (categories) :
                    handleCategoriesInput(productId);
                    break;
                case (moveToDefectives) :
                    handleMoveToDefectives(productId);
                    break;
                case (addNewItems) :
                    handleAddNewItems(productId);
                    break;
                case (removeItems) :
                    handleRemoveItems(productId);
                    break;
                case (moveItemFromWarehouse) :
                    handleMoveItemsFromWarehouse(productId);
                    break;
                case (moveItemsFromStore) :
                    handleMoveItemsFromStore(productId);
                    break;
                case (itemFullLocation) :
                    handleItemFullLocation(productId);
                    break;
                case (sellingPrice) :
                    handleProductSellingPrice(productId);
                    break;
                case (costPrice) :
                    handleProductCostPrice(productId);
                    break;
                case (sales) :
                    handleProductSales(productId);
                    break;
            }
        }

    }

    private void handleProductSales(int productId) {
        ResponseT<String> res = serviceSMS.getProductSalesString(productId);
        handleResponse(res);
    }

    private void handleProductCostPrice(int productId) {
        String infoOrUpdate = getS1OrS2("info","update");
        if(infoOrUpdate.equalsIgnoreCase("update")){
            System.out.println("please type new cost price");
            double price = takeDouble(0,Integer.MAX_VALUE);
            Response res = serviceSMS.updateProductCostPrice(productId,price);
            handleResponse(res);
            return;
        }
        else if(infoOrUpdate.equalsIgnoreCase("info")){
            ResponseT<Double> res = serviceSMS.getProductCostPrice(productId);
            handleResponse(res);
            return;
        }
        printSomethingWentWrongMessage();
    }

    private void handleProductSellingPrice(int productId) {
        String infoOrUpdate = getS1OrS2("info","update");
        if(infoOrUpdate.equalsIgnoreCase("update")){
            System.out.println("please type new selling price");
            double price = takeDouble(0,Integer.MAX_VALUE);
            Response res = serviceSMS.updateSellingPrice(productId,price);
            handleResponse(res);
        }
        else if(infoOrUpdate.equalsIgnoreCase("info")){
            String saleOrOriginal = getS1OrS2("include sales","original price");
            if(saleOrOriginal.equalsIgnoreCase("include sales")){
                String TopOrAll = getS1OrS2("Top sale","All sales");
                if(TopOrAll.equalsIgnoreCase("Top sale")){
                    ResponseT<Double> res = serviceSMS.getProductSellingPriceIncludeTopSale(productId);
                    handleResponse(res);
                }
                else if (TopOrAll.equalsIgnoreCase("all sales")){
                    ResponseT<Double> res = serviceSMS.getProductSellingPriceIncludeAllSales(productId);
                    handleResponse(res);
                }
            }
            else if(saleOrOriginal.equalsIgnoreCase("original price")){
                ResponseT<Double> res = serviceSMS.getProductSellingPrice(productId);
                handleResponse(res);
            }
        }
        else printSomethingWentWrongMessage();
    }

    private void handleProductName(int productId) {
        String infoOrUpdate = getS1OrS2("info","update");
        if(infoOrUpdate.equals("update")){
            String newName = getStringInput("please type new name");
            Response res = serviceSMS.updateProductName(productId,newName);
            handleResponse(res);
            return;
        }
        else if(infoOrUpdate.equals("info")){
            ResponseT<String> res = serviceSMS.getProductName(productId);
            handleResponse(res);
            return;
        }
        printSomethingWentWrongMessage();
    }

    private void handleProductInformationInput(int productId) {
        ResponseT<String> res = serviceSMS.getProductInformation(productId);
        handleResponse(res);
    }

    private void handleItemFullLocation(int productId) {
        ResponseT<String> res = serviceSMS.getProductLocation(productId);
        handleResponse(res);
    }

    private void handleMoveItemsFromStore(int productId) {
        int amount = takeInt("please type amount to move",0,MAX_PRODUCT_ACTIONS_AMOUNT);
        Response res = serviceSMS.moveProductItemsFromStore(productId,amount);
        handleResponse(res);
    }

    private void handleMoveItemsFromWarehouse(int productId) {
        int amount = takeInt("please type amount to move",0,MAX_PRODUCT_ACTIONS_AMOUNT);
        Response res = serviceSMS.moveProductItemsFromWarehouse(productId,amount);
        handleResponse(res);
    }


    private void handleRemoveItems(int productId) {
        int amount = takeInt("please type amount to remove",0,MAX_PRODUCT_ACTIONS_AMOUNT);
        String location=getS1OrS2("Store","Warehouse");
        Response res = serviceSMS.removeProductItems(productId,amount,location);
        handleResponse(res);
    }

    private void handleAddNewItems(int productId) {
        int amount = takeInt("amount of new items: ",0,MAX_PRODUCT_ACTIONS_AMOUNT);
        Response res = serviceSMS.addProductItems(productId,amount);
        handleResponse(res);
    }

    private void handleMoveToDefectives(int productId) {
        String location = getS1OrS2("Store","Warehouse");
        String status = getStringInput("please type product status (Expired/defective)");
        int amount =takeInt("please type amount",0,MAX_PRODUCT_ACTIONS_AMOUNT);
        Response res = serviceSMS.moveProductsToDefectives(productId,location,status,amount);
        handleResponse(res);
    }

    private void handleCategoriesInput(int productId) {
        String infoOrUpdate = getS1OrS2("info","update");
        if(infoOrUpdate.equals("update")){
            List<String> categories = getCategory();
            Response res = serviceSMS.updateProductCategories(productId,categories);
            handleResponse(res);
            return;
        }
        else if(infoOrUpdate.equals("info")){
            ResponseT<List<String>> res = serviceSMS.getProductCategories(productId);
            handleCollectionResponse(res);
            return;
        }
        printSomethingWentWrongMessage();
    }

    private List<String> getCategory() {
        System.out.println(" please type the category you like.\n " +
                           "format: Main_category/sub_category/sub_sub_category (maximum 3) \n" +
                           "if you wish you can leave some of the slots empty");
        String catString = getStringInput();
        LinkedList<String> category=asList(catString.split("/"));
        if(category.size()>3) {
            System.out.println("maximum 3 categories");
            return null;
        }
        while(category.size()<3) category.add("None");
        return category;
    }

    private LinkedList<String> asList(String[] split) {
        return new LinkedList<>(Arrays.asList(split));
    }

    private String getS1OrS2(String s1,String s2) {
        String ret = "";
        boolean should_stop = false;
        while(!should_stop){
            System.out.println("please type "+s1+" or "+s2);
            ret = getStringInput();
            if(ret.equalsIgnoreCase(s1) || ret.equalsIgnoreCase(s2)){
                should_stop = true;
            }
        }
        return ret.toLowerCase();
    }


    private void handleDemand(int productId) {
        ResponseT<Double> res = serviceSMS.getProductDemand(productId);
        handleResponse(res);
    }

    private void handleDeliveryTime(int productId) {
        String infoOrUpdate = getS1OrS2("info","update");
        if(infoOrUpdate.equals("update")){
            int deliveryTime = takeInt("please type delivery time in days ",0,Integer.MAX_VALUE);
            Response res = serviceSMS.updateProductDeliveryTime(productId,deliveryTime);
            handleResponse(res);
            return;
        }
        else if(infoOrUpdate.equals("info")){
            ResponseT<Integer> res = serviceSMS.getProductDeliveryTime(productId);
            handleResponse(res);
            return;
        }
        printSomethingWentWrongMessage();
    }

    private void handleWarehouseShelf(int productId) {
        String infoOrUpdate = getS1OrS2("info","update");
        if(infoOrUpdate.equals("update")){
            int shelfNumber = takeInt("shelf number: ",0,Integer.MAX_VALUE);
            Response res = serviceSMS.updateProductWarehouseShelfID(productId,shelfNumber);
            handleResponse(res);
            return;
        }
        else if(infoOrUpdate.equals("info")){
            ResponseT<Integer> res = serviceSMS.getProductWarehouseShelfID(productId);
            handleResponse(res);
            return;
        }
        printSomethingWentWrongMessage();
    }

    private void handleStoreShelf(int productId) {
        String infoOrUpdate = getS1OrS2("info","update");
        if(infoOrUpdate.equals("update")){
            int shelfNumber = takeInt("shelf number: ",0,Integer.MAX_VALUE);
            Response res = serviceSMS.updateProductStoreShelfID(productId,shelfNumber);
            handleResponse(res);
            return;
        }
        else if(infoOrUpdate.equals("info")){
            ResponseT<Integer> res = serviceSMS.getProductStoreShelfID(productId);
            handleResponse(res);
            return;
        }
        System.out.println("something went wrong");
    }

    private void handleWarehouseQuantity(int productId) {
        ResponseT<Integer> res = serviceSMS.getProductWarehouseQuantity(productId);
        handleResponse(res);
    }

    private void handleStoreQuantity(int productId) {
        ResponseT<Integer> res = serviceSMS.getProductStoreQuantity(productId);
        handleResponse(res);
    }

    private void handleTotalQuantity(int productId) {
        ResponseT<Integer> res = serviceSMS.getProductTotalQuantity(productId);
        handleResponse(res);
    }

    private void handleManufacturer(int productId) {
        String infoOrUpdate = getS1OrS2("info","update");
        if(infoOrUpdate.equals("update")){
            System.out.println("type manufacturer id: ");
            int manufacturer = takeInt(0,Integer.MAX_VALUE);
            Response res = serviceSMS.updateProductManufacturer(productId,manufacturer);
            handleResponse(res);
        }
        else if(infoOrUpdate.equals("info")){
            ResponseT<Integer> res = serviceSMS.getProductManufacturer(productId);
            handleResponse(res);
        }
    }

    private int getProductId() {
        System.out.println("please type product id");
        return takeInt(0,Integer.MAX_VALUE);
    }




    private void printHandleProductInfoMenu() {
        System.out.println("\n-------Product Menu-------- \n" +
                           "1.for product full information please type 1 \n" +
                           "2.for product name please type 2 \n" +
                           "3.for product manufacturer please type 3 \n" +
                           "4.for product TOTAL quantity please type 4 \n" +
                           "5.for product store quantity please type 5 \n" +
                           "6.for product warehouse quantity please type 6 \n" +
                           "7.for product store shelf number please type 7 \n" +
                           "8.for product warehouse shelf number please type 8 \n" +
                           "9.for product delivery time please type 9 \n" +
                           "10.for product demand (Average daily sold quantity) please type 10 \n" +
                           "11.for product categories please type 11 \n" +
                           "12.to move product to defectives please type 12 \n" +
                           "13. to add new items please type 13 \n" +
                           "14. to remove items please type 14 \n" +
                           "15. to move items from warehouse please type 15 \n" +
                           "16. to move items from store please type 16 \n" +
                           "17. to get item full location please type 17 \n" +
                           "18. to product selling price please type 18 \n" +
                           "19. to product cost price please type 19 \n" +
                           "20. to get product current sales type 20 \n" +
                           "to go back to main menu please type 0\n");
    }


    private void handleSales(){
        printSalesMenu();
        final int addSale = 1;
        final int removeSale = 2;
        final int seeSales = 3;
        final int totalOptions = 3;
        int input = takeInt(BACK_OR_EXIT_INPUT,totalOptions);
        switch (input) {
            case (addSale) :
                handleAddSale();
                break;
            case (removeSale) :
                handleRemoveSale();
                break;
            case (seeSales) :
                handleSeeSales();
                break;
        }
    }

    private void handleRemoveSale() {
        String saleName = getStringInput("please enter sale name");
        Response res = serviceSMS.removeSale(saleName);
        handleResponse(res);
    }

    private void handleAddSale() {
        String productsOrCategories = getProductsOrCategories();
        String saleName = getStringInput("please enter sale name");
        double discount = takeDouble("please enter sale discount (%): ",0,Integer.MAX_VALUE);
        if(productsOrCategories.equals("categories")){
            List<List<String>> categories = getCategories();
            if(categories==null) return;
            Response res = serviceSMS.addSaleForCategories(saleName,discount,categories);
            handleResponse(res);
        }
        else if(productsOrCategories.equals("products")){
            List<Integer> productsNumbers = getProductsNumbers();
            if(productsNumbers == null){
                System.out.println("Invalid products numbers input");
                return;
            }
            Response res = serviceSMS.addSaleForProducts(saleName,discount,productsNumbers);
            handleResponse(res);
        }
    }

    private void printSomethingWentWrongMessage(){
        System.out.println("something went wrong, please try again");
    }

    private List<Integer> getProductsNumbers() {
        System.out.println("please type products number.\n " +
                           "format: pid1 pid2 pid3 ... ");
        String productsString = getStringInput();
        String[] productsStringArr = productsString.split(" ");
        List<Integer> ret= new LinkedList<>();
        for(String str : productsStringArr){
            Integer nextId = tryParse(str);
            if(nextId == null)
                return null;
            ret.add(nextId);
        }
        return ret;
    }

    private double takeDouble() {
        boolean wentToCatch;
        double input = 0;
        Scanner sc = new Scanner(System.in);
        do {
            try {
                System.out.print("please type here: ");
                wentToCatch = false;
                input = sc.nextDouble(); // sc is an object of scanner class
            } catch (InputMismatchException e) {
                sc.next();
                wentToCatch = true;
                printInvalidInputMessage();
            }
        } while (wentToCatch);
        return input;
    }

    private double takeDouble(int start,int end){
        double i = takeDouble();
        while(i < start || i > end){
            printInvalidInputMessage();
            i = takeDouble();
        }
        return i;
    }

    private double takeDouble(String msg,int start,int end){
        System.out.println(msg);
        return takeDouble(start,end);
    }

    private String getProductsOrCategories() {
        String ret;
        do {
            System.out.println("please type categories or products");
            ret = getStringInput();
        } while (!ret.equalsIgnoreCase("categories") && !ret.equalsIgnoreCase("products"));
        return ret.toLowerCase();
    }

    private void printSalesMenu() {
        System.out.println("\n------sales menu------ \n" +
                           "1.to add a new sale please type 1 \n" +
                           "2.to remove a sale please type 2 \n" +
                           "3.to see all available sales type 3 \n" +
                           "to go back please type 0\n");
    }

    private void handleCategories() {
        printCategoriesMenu();
        int input = takeInt();
        final int addCategory = 1;
        final int removeCategory = 2;
        final int seeCategories = 3;
        switch (input){
            case (addCategory) :
                handleAddCategory();
                break;
            case (removeCategory) :
                handleRemoveCategory();
                break;
            case (seeCategories) :
                handleSeeCategories();
                break;
        }
    }

    private void handleSeeCategories() {
        ResponseT<String> res= serviceSMS.getAllAvailableCategoriesDescription();
        handleResponse(res);
    }

    private void handleSeeSales() {
        ResponseT<String> res= serviceSMS.getAllAvailableSalesDescription();
        handleResponse(res);
    }


    private void handleRemoveCategory() {
        List<String> category = getCategory();
        if(category == null){
            printSomethingWentWrongMessage();
            return;
        }
        Response res = serviceSMS.removeCategory(category);
        handleResponse(res);
    }

    private void handleAddCategory(){
        List<String> category = getCategory();
        if(category == null)
            return;
        if(category.isEmpty()){
            printSomethingWentWrongMessage();
            return;
        }
        Response res = serviceSMS.addCategory(category);
        handleResponse(res);
    }

    private void printCategoriesMenu() {
        System.out.println("\n-----Categories Menu------ \n" +
                           "1. to add category please type 1 \n" +
                           "2. to remove category please type 2 \n" +
                           "3. to see all available categories type 3 \n" +
                           "to go back to main menu type 0\n");
    }

    protected void shutDown() {
        //todo what happens if daily update fails?
        serviceSMS.dailyUpdate();
        serviceSMS.shutDown();
    }

    private void printInvalidInputMessage() {
        System.out.println("Invalid Input. please try again");
    }


    private void printMainOptions(){
        System.out.print("----------------Main Menu---------------- \n\n" +
                         "please choose your option: \n\n" +
                         "1.for reports (stock report,periodic stock report,defectives report,shortage report) \n" +
                         "please type 1 \n\n" +
                         "2. for product options(add product,remove product and specific product actions) \n" +
                         "please type 2 \n\n" +
                         "3. for sales (add new sale,remove sale, see all available sales) \n" +
                         "please type 3 \n\n" +
                         "4.for categories (add category, remove category, see all available categories) \n" +
                         "please type 4 \n\n" +
                         "5.for pending orders \n"+
                         "please type 5 \n\n"+
                         "6.for tests menu \n" +
                         "please type 6 \n\n" +
                         "To log out and perform a daily update\n" +
                         "please type 0\n\n");
    }

    private void handleCollectionResponse(ResponseT<List<String>> res){
        if(res.isErrorOccurred()){
            System.out.printf("Error occurred: %s%n",res.getErrorMessage());
        }
        else{
            printStringCollection(res.getValue());
        }
    }

    private void printStringCollection(List<String> value) {
        System.out.printf("%s/%s/%s",value.get(0),value.get(1),value.get(2));
    }

    private void handleResponse(ResponseT res){
        if(res.isErrorOccurred()){
            System.out.printf("Error occurred: %s%n",res.getErrorMessage());
        }
        else{
            System.out.println(res.getValue());
        }
    }

    private void handleResponse(Response res){
        if(res.isErrorOccurred()){
            System.out.printf("Error occurred: %s%n",res.getErrorMessage());
        }
        else{
            System.out.println("Done");
        }
    }

    private void printWelcomeMessage(){
        System.out.println("╔══╦╗╔╦═╦═╦╦╗ ╔╗╔═╦═╗  ╔══╦══╦═╦═╦╦╗  ╔══╦═╦═╦╦═╦═╦═╦══╦═╦═╦╦══╗  ╔══╦╦╦══╦══╦═╦══╗\n" +
                           "╠╗╚╣╚╝║╔╣╦╣╔╝ ║╚╣╦╣╦╣  ╠╗╚╬╗╔╣║║╠╣═╣  ║║║║╩║║║║╩║╔╣╦╣║║║╦╣║║╠╗╔╝  ╠╗╚╬╗╠╗╚╬╗╔╣╦╣║║║\n" +
                           "╚══╩══╩╝╚═╩╝  ╚═╩═╩═╝  ╚══╝╚╝╚═╩═╩╩╝  ╚╩╩╩╩╩╩═╩╩╩═╩═╩╩╩╩═╩╩═╝╚╝   ╚══╝╚╩══╝╚╝╚═╩╩╩╝\n");
        System.out.println("welcome to super lee Stock Management System! (SMS)");
    }

    private int takeInt(){
        boolean wentToCatch;
        int input = 0;
        Scanner sc = new Scanner(System.in);
        do {
            try {
                System.out.print("please type here: ");
                wentToCatch = false;
                input = sc.nextInt(); // sc is an object of scanner class
            } catch (InputMismatchException e) {
                sc.next();
                wentToCatch = true;
                printInvalidInputMessage();
            }
        } while (wentToCatch);
        return input;
    }

    private int takeInt(String msg,int start,int end){
        System.out.println(msg);
        return takeInt(start,end);
    }

    public Integer tryParse(Object obj) {
        Integer retVal;
        try {
            retVal = Integer.parseInt((String) obj);
        } catch (NumberFormatException nfe) {
            retVal = null; // or null if that is your preference
        }
        return retVal;
    }

    private int takeInt(int start,int end){
        int i = takeInt();
        while(i < start || i > end){
            printInvalidInputMessage();
            i = takeInt();
        }
        return i;
    }

    public void loadData(){
        // create example categories
        serviceSMS.addCategory(Arrays.asList("Dairy","None","None"));
        serviceSMS.addCategory(Arrays.asList("Meat","Chicken","None"));
        serviceSMS.addCategory(Arrays.asList("BathroomProducts","Towels","Size"));

        //create example products
        serviceSMS.addProduct(1,"Milk",315851824,7.90,5.0,1,11,5,Arrays.asList("Dairy","None","None"));
        serviceSMS.addProduct(2,"Chicken breast",315851824,30.0,10.0,2,12,7,Arrays.asList("Meat","Chicken","None"));
        serviceSMS.addProduct(3,"Face towel",31786900,15.0,7.5,3,13,14,Arrays.asList("BathroomProducts","Towels","Size"));
        serviceSMS.addProduct(4,"Hand towel",31786900,10.0,7.5,4,14,14,Arrays.asList("BathroomProducts","Towels","Size"));
        //add example items
        serviceSMS.addProductItems(1,50);
        serviceSMS.addProductItems(2,50);
        serviceSMS.addProductItems(3,50);
        serviceSMS.addProductItems(4,50);

        serviceSMS.changeProductDemand(1,5);

        MockEmployee me = new MockEmployee();
        serviceSMS.setTests(me);
    }

    private String getBranch() {
        serviceSMS = new Service();
        List<String> branches = serviceSMS.getAllBranches().Value;
        String branchAddress = "";
        boolean shouldStop = false;
        while(!shouldStop) {
            String branch = getStringInput("please choose branch: \n" +
                    String.join("\n", branches)+"\n\n"+
                    "NEW for a new branch\n" +
                    "type EXIT to close the system\n");
            if (branch.equalsIgnoreCase("NEW")){
                String address =  getStringInput("Please type branch address.");
                String area = getStringInput("please type branch area (south,center,north)");
                Response res = serviceSMS.addNewBranch(address,area);
                if(res.isErrorOccurred()){
                    System.out.println(res.errorMessage);
                    continue;
                }
                branchAddress = address;
                shouldStop = true;
            }

            else if (getBranchSystemName(branch,branches) != null){
                branchAddress = branch;
                shouldStop = true;
            }

            else if (branch.equalsIgnoreCase("EXIT")){
                branchAddress = null;
                shouldStop = true;
            }

            else System.out.println("please choose a branch from the list.");
        }
        serviceSMS.shutDown();
        return branchAddress;
    }

    private String getBranchSystemName(String branch, List<String> branches) {
        for(String b : branches){
            if(branch.equalsIgnoreCase(b))
                return b;
        }
        return null;
    }

    public void setShouldStopMainLoop(boolean shouldStopMainLoop) {
        this.shouldStopMainLoop = shouldStopMainLoop;
    }
}
