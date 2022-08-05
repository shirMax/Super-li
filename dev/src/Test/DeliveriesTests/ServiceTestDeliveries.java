package DeliveriesTests;

import Backend.BusinessLayer.Employees.Positions;
import Backend.BusinessLayer.Employees.ShiftType;
import Backend.BusinessLayer.Stock.Objects.MockEmployee;
import Backend.BusinessLayer.Tools.DateConvertor;
import Backend.PersistenceLayer.DeliveriesDal.CreateDBDeliveries;
import Backend.ServiceLayer.Response;
import Backend.ServiceLayer.Service;
import Frontend.PresentationLayer.Model.Controller;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ServiceTestDeliveries {
    static Service service;

    @BeforeEach
    void setUp() {
        service = new Service();
        service.initial();
        loadData();
    }
/*

    private void loadEmployee(){

        service.addEmployee("David",5,11000,200023,toDate("01-02-2001"),"050");
        service.addEmployee("Yossi",123456789,11000,200323,toDate("01-02-2001"),"050");
        service.addEmployee("Michal",246813579,11000,207023,toDate("01-02-2001"),"050");
        service.addPositionToEmployee(123456789, Positions.driver());
        service.updateEmployeeLicense(123456789,"C1");
        service.addPositionToEmployee(246813579, Positions.driver());
        service.updateEmployeeLicense(246813579,"C");
        service.addPositionToEmployee(5, Positions.stockKeeper());


        service.addConstraint(3, ShiftType.morning(),123456789);
        service.addConstraint(3,ShiftType.evening(),123456789);
        service.addConstraint(3, ShiftType.morning(),246813579);
        service.addConstraint(3,ShiftType.evening(),246813579);
        service.addConstraint(3, ShiftType.morning(),5);
        service.addConstraint(3,ShiftType.evening(),5);
        service.addShift(toDate("25-05-2022"),ShiftType.morning(),"aaa");
        service.addShift(toDate("25-05-2022"),ShiftType.evening(),"aaa");
        service.addEmployeeToShift(123456789,toDate("25-05-2022"),ShiftType.morning(),"aaa");
        service.addEmployeeToShift(246813579,toDate("25-05-2022"),ShiftType.morning(),"aaa");
        service.addEmployeeToShift(123456789,toDate("25-05-2022"),ShiftType.evening(),"aaa");
        service.addEmployeeToShift(246813579,toDate("25-05-2022"),ShiftType.evening(),"aaa");
    }

    private void loadSites(){
        service.addTruck(1122233, "C", 360, 1400);
        service.addTruck(4567832, "C", 236, 998);
    }
*/

    @AfterEach
    void tearDown() {
        CreateDBDeliveries db = new CreateDBDeliveries();
        db.clearDB();
    }


    @Test
    void bookDelivery_success() {
        Response r = service.bookDelivery("25-05-2024 10:00",123456789,1122233,1);
        assertTrue(!r.isErrorOccurred());
    }

    @Test
    void inviteDelivery_failShift() {
        Response r = service.bookDelivery("13-07-2023 10:00",123456789,1122233,1);
        assertTrue(r.isErrorOccurred());
    }

    @Test
    void inviteDelivery_failTruck() {
        Response r = service.bookDelivery("25-05-2022 10:00",123456789,486728,1);
        assertTrue(r.isErrorOccurred());
    }

    @Test
    void inviteDelivery_failDriver() {
        Response r = service.bookDelivery("25-05-2022 10:00",4977366,1122233,1);
        assertTrue(r.isErrorOccurred());
    }


    @Test
    void launchDelivery_success() {
        service.addEmployeeToShift(5,toDate("25-05-2022"),ShiftType.morning(),"beer sheba");
        service.bookDelivery("25-05-2022 10:00",123456789,1122233,1);
        //loadSitesDoc();
        Response r = service.launchDelivery(1);
        assertTrue(!r.isErrorOccurred());

    }

//    private void loadSitesDoc() {
//        service.addSiteDoc(1, "Osem", "SOURCE");
//        service.addSiteDoc(1, "Snif TLV", "DESTINATION");
//
//    }


    private Date toDate(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date ret=null;
        try {
            ret = dateFormat.parse(date);
        } catch (ParseException ignored) {}
        return ret;
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
            Controller.getInstance().setService(service);
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
            java.sql.Date arrived = new java.sql.Date(Calendar.getInstance().getTime().getTime());
            Controller.getInstance().addOrderCollect(268,current,items2,"beer sheba",arrived);

            Controller.getInstance().addOrderCollect(133, arrived,items,"beer sheba",arrived);
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        //Employees and Deliveries
        service.loadData();
    }


}
