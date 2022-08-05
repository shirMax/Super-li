package Frontend.PresentationLayer;

import Backend.BusinessLayer.Stock.Objects.MockEmployee;
import Backend.BusinessLayer.Suppliers.Contact;
import Backend.ServiceLayer.Service;
import Frontend.PresentationLayer.DeliveriesModule.DeliveryMainConsole;
import Frontend.PresentationLayer.Employees.EmployeesConsole;
import Frontend.PresentationLayer.Model.Controller;
import Frontend.PresentationLayer.View.MenuManager;

import java.sql.Date;
import java.time.DayOfWeek;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class MainRunner {

    private static Scanner scanner;
    private static Service service;
    private final String hrManager = "HR Manager";
    private final String stockKeeper = "Stock Keeper";
    private final String logisticManager = "Logistic Manager";
    private final String storeManager = "Store Manager";
    private final String deliveries = "Deliveries";
    private final String employees = "Employees";
    private final String stock = "Stock";
    private final String suppliers = "Suppliers";
    private Map<String,List<String>> accessPermissions;


    public MainRunner() {}

    public void run() {
        scanner = new Scanner(System.in);
        service = new Service();
        service.initial();
        SMSRunner sms = new SMSRunner(service);
        MenuManager mm = new MenuManager(service);
        EmployeesConsole employeesConsole =new EmployeesConsole(scanner, service);
        DeliveryMainConsole deliveryMainConsole = new DeliveryMainConsole(scanner, service);
        printWelcomeMessage();
        loadDataScreen();
        initAccessPermissions();
        while (true){
            String role = getRole();
            if(role.equalsIgnoreCase("exit"))
                break;
            System.out.println("\nMAIN MODELS MENU\n________________");
            mm.stopRun = false;
            sms.setShouldStopMainLoop(false);
            printPossibleModules(role);
            String modelName = getModelOrExit();
            if(modelName.equalsIgnoreCase(deliveries)) {
                if(!checkPermission(deliveries,role)) roleErrorMessage(role,deliveries);
                else if (role.equalsIgnoreCase("Store Manager")) deliveryMainConsole.docRun();
                else deliveryMainConsole.run();
            }
            else if(modelName.equalsIgnoreCase(employees)) {
                if(!checkPermission(employees,role)) roleErrorMessage(role,employees);
                else employeesConsole.run();
            }
            else if(modelName.equalsIgnoreCase(stock)) {
                if(!checkPermission(stock,role)) roleErrorMessage(role,stock);
                else if (role.equalsIgnoreCase("Store Manager")) sms.restrictStart();
                else sms.start();
            }
            else if(modelName.equalsIgnoreCase(suppliers)) {
                if(!checkPermission(suppliers,role)) roleErrorMessage(role,suppliers);
                else{
                    mm = new MenuManager(service);
                    mm.run();
                }
            }
            else if(modelName.equalsIgnoreCase("Exit")){
                sms.shutDown();
                mm.Terminate();
                System.out.println("Thank you for using our system! have a great day!");
                break;
            }
            else System.out.println("something went wrong please try again");
        }
    }

    private void printPossibleModules(String role) {
        String modules = "";
        for(String key :accessPermissions.keySet()){
            if(accessPermissions.get(key).contains(role))modules+=","+key;
        }
        modules = modules.substring(1);
        modules+=".";
        System.out.println(role+" is permitted to use modules: "+modules);

    }

    private boolean checkPermission(String module, String role) {
        return accessPermissions.get(module).contains(role);
    }

    private void roleErrorMessage(String role, String module) {
        System.out.println(role+" is not permitted to module "+module);
    }

    private void initAccessPermissions() {
        accessPermissions = new ConcurrentHashMap<>();
        List<String> employeesPerm = new ArrayList<>();
        List<String> stockPerm = new ArrayList<>();
        List<String> suppliersPerm = new ArrayList<>();
        List<String> deliveriesPerm = new ArrayList<>();
        employeesPerm.add(hrManager);
        //employeesPerm.add(storeManager);
        suppliersPerm.add(stockKeeper);
        //suppliersPerm.add(storeManager);
        deliveriesPerm.add(logisticManager);deliveriesPerm.add(storeManager);
        stockPerm.add(stockKeeper);stockPerm.add(storeManager);
        accessPermissions.put(employees,employeesPerm);
        accessPermissions.put(stock,stockPerm);
        accessPermissions.put(suppliers,suppliersPerm);
        accessPermissions.put(deliveries,deliveriesPerm);
    }

    private String getRole() {
        while(true) {
            System.out.println("Please choose your role:" +
                    "\n1 HR Manager" +
                    "\n2 Stock Keeper" +
                    "\n3 Logistic Manager" +
                    "\n4 Store Manager"+
                    "\ntype role number or type exit to close the system.");
            String input = getStringInput();
            if(input.equalsIgnoreCase("exit"))
                return input;
            try{
                int i = Integer.parseInt(input);
                if((i<1)|(i>4))throw new Exception("illegal number");
                return integerToRole(i);
            }
            catch (Exception e){
                System.out.println("Invalid input!");
            }
        }


    }

    private String integerToRole(int i) {
        if(i==1)return hrManager;
        if(i==2)return stockKeeper;
        if(i==3)return logisticManager;
        return storeManager;
    }

    private void loadDataScreen() {
        System.out.println("if you wish to load data type load");
        String loadOrCont = getS1OrS2("load","skip");
        if(loadOrCont.equalsIgnoreCase("load"))
            loadData();
    }

    private void loadData() {
        //stock
        // create example categories
        service.addNewBranch("beer sheba","south");
        service.setBranch("beer sheba");

        service.addCategory(Arrays.asList("Dairy","None","None"));
        service.addCategory(Arrays.asList("Meat","Chicken","None"));
        service.addCategory(Arrays.asList("BathroomProducts","Towels","Size"));

        //create example products
        service.addProduct(1,"Milk",315851824,7.90,5.0,1,11,5,Arrays.asList("Dairy","None","None"));
        service.addProduct(2,"Chicken breast",315851824,30.0,10.0,2,12,7,Arrays.asList("Meat","Chicken","None"));
        service.addProduct(3,"Face towel",31786900,15.0,7.5,3,13,14,Arrays.asList("BathroomProducts","Towels","Size"));
        service.addProduct(4,"Hand towel",31786900,10.0,7.5,4,14,14,Arrays.asList("BathroomProducts","Towels","Size"));
        //add example items
        service.addProductItems(1,50);
        service.addProductItems(2,50);
        service.addProductItems(3,50);
        service.addProductItems(4,50);

        service.changeProductDemand(1,5);
        MockEmployee me = new MockEmployee();
        service.setTests(me);

        //suppliers

        try {
            Controller.getInstance().addSupplier(123,123456,"Cash","Const","Tnuva","beer sheba","South");

            Controller.getInstance().addSupplier(268,129435,"BankTransfer","Collect","Osem","beer sheba","South");
            Controller.getInstance().addItem(123,1,2,4.90);
            Controller.getInstance().addItem(123,2,3,3.90);
            Controller.getInstance().addItem(268,3,4,9.90);
            Controller.getInstance().addItem(268,4,5,4.90);

            Controller.getInstance().addSupplier(133,13242,"BankTransfer","Order","milka","beer sheba","South");
            Controller.getInstance().addItem(133,1,2,4.90);
            Controller.getInstance().addItem(133,2,3,3.90);

            Controller.getInstance().addDiscount(123,1,10,50);
            Controller.getInstance().addDiscount(123,1,20,70);
            Controller.getInstance().addDiscount(123,2,20,70);

            Controller.getInstance().addSupplier(1919,123256,"Cash","Const","Prigat","beer sheba","South");
            Controller.getInstance().addItem(1919,1,2,5);

            Controller.getInstance().addContact(123,189,"shir","040995955");
            Controller.getInstance().addContact(123,183,"shira","040295955");

            Controller.getInstance().addContact(1919,2322,"shirush", "3232");

            Controller.getInstance().addContact(133,123,"niv","052839493");

            Controller.getInstance().addContact(268,135,"oran","048383443");
            Controller.getInstance().addContact(268,395,"eilon","93949494");

            //adding orders
            List<DayOfWeek> days = new LinkedList<>();
            days.add(DayOfWeek.SUNDAY);
            days.add(DayOfWeek.MONDAY);
            days.add(DayOfWeek.TUESDAY);
            days.add(DayOfWeek.WEDNESDAY);
            days.add(DayOfWeek.TUESDAY);
            days.add(DayOfWeek.FRIDAY);
            HashMap<Integer,Integer> items = new HashMap<>();
            items.put(1,10);
            items.put(2,20);
            Controller.getInstance().addOrderConst(123,days,items,"beer sheba");

            List<DayOfWeek> days2 = new LinkedList<>();
            days2.add(DayOfWeek.SATURDAY);
            HashMap<Integer,Integer> items3 = new HashMap<>();
            items3.put(1,20);
            Controller.getInstance().addOrderConst(1919,days2, items3, "beer sheba");

            HashMap<Integer,Integer> items2 = new HashMap<>();
            items2.put(3,19);
            items2.put(4,20);
            java.sql.Date current = new java.sql.Date(Calendar.getInstance().getTime().getTime());
            Date arrived = new java.sql.Date(Calendar.getInstance().getTime().getTime());
            Controller.getInstance().addOrderCollect(268,current,items2,"beer sheba",arrived);

            Controller.getInstance().addOrderCollect(133, arrived,items,"beer sheba",arrived);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }

        //Employees and Deliveries
        service.loadData();
    }

    private void printWelcomeMessage() {
        System.out.println("╔╦╦╦═╦╗╔═╦═╦══╦═╗  ╔══╦═╗  ╔══╦╗╔╦═╦═╦╦╗  ╔╗╔╗\n" +
                           "║║║║╦╣╚╣╠╣║║║║║╦╣  ╚╗╔╣║║  ╠╗╚╣╚╝║╔╣╦╣╔╝  ║╚╣║\n" +
                           "╚══╩═╩═╩═╩═╩╩╩╩═╝   ╚╝╚═╝  ╚══╩══╩╝╚═╩╝   ╚═╩╝\n");
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

    private String getModelOrExit() {
        System.out.println("please type model name:\n" +
                            "Deliveries\n" +
                            "Employees\n" +
                            "Stock\n" +
                            "Suppliers\n" +
                            "to close the system type exit\n");
        String[] modelsArray = {"Deliveries","Employees","Stock","Suppliers","Exit"};
        String ret = "";
        boolean should_stop = false;
        while(!should_stop){
            ret = getStringInput();
            for(String model : modelsArray)
                if(ret.equalsIgnoreCase(model))
                    should_stop = true;
            if(!should_stop)
                System.out.println("invalid model name, please try again");
        }
        return ret.toLowerCase();
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
}
