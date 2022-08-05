package Backend.ServiceLayer.ObjectsEmployees;

import Backend.BusinessLayer.Deliveries.Enums.License_Enum;
import Backend.BusinessLayer.Employees.*;
import Backend.BusinessLayer.Tools.DateConvertor;
import Backend.PersistenceLayer.CreateDB;
import Backend.PersistenceLayer.EmployeesDal.ConstraintsDAO;
import Backend.PersistenceLayer.EmployeesDal.CreateDBEmployees;
import Backend.PersistenceLayer.EmployeesDal.EmployeeDAO;
import Backend.PersistenceLayer.EmployeesDal.ShiftDAO;
import Frontend.PresentationLayer.Model.Controller;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ServiceEmployees {
    private ShiftController shiftController;
    private EmployeeController employeeController;

    public ShiftController getShiftController() {
        return shiftController;
    }

    public EmployeeController getEmployeeController() {
        return employeeController;
    }

    public ServiceEmployees() {
        //CreateDBEmployees createDBEmployees = new CreateDBEmployees();
        //  createDBEmployees.createFileIfNotExists();
        ConstraintsDAO conDAO = new ConstraintsDAO();
        ShiftDAO shiftDAO = new ShiftDAO();
        EmployeeDAO empDAO = new EmployeeDAO();
        employeeController = new EmployeeController(empDAO,conDAO,shiftDAO);
        shiftController = new ShiftController(shiftDAO,empDAO,conDAO);

    }


    public void init() {
        addEmployee("Mike",1,15000,20001,toDate("01-01-2005"),"0500000000");
        addEmployee("Hank",2,9000,20002,toDate("01-05-2010"),"0500000001");
        addEmployee("Steve",3,11000,20003,toDate("01-02-2001"),"0500000002");
        addEmployee("Joe",4,11000,200023,toDate("01-02-2001"),"0500000003");
        addEmployee("StorKi",5,11000,200023,toDate("01-02-2001"),"0500000004");

        updateEmployeeLicense(4,"B");
        addEmployee("Yossi",123456789,11000,200323,toDate("01-02-2001"),"0500000005");
        addEmployee("Michal",246813579,11000,207023,toDate("01-02-2001"),"0500000006");
        addPositionToEmployee(123456789, Positions.driver());
        updateEmployeeLicense(123456789,"C1");
        addPositionToEmployee(246813579, Positions.driver());
        updateEmployeeLicense(246813579,"C");
        addPositionToEmployee(5, Positions.stockKeeper());


        addPositionToEmployee(1,Positions.shiftManager());
        addPositionToEmployee(1,Positions.stockKeeper());
        addPositionToEmployee(2,Positions.cashier());
        addPositionToEmployee(3, Positions.mover());
        addPositionToEmployee(4, Positions.driver());
        for(int i =0; i<8;i++){
            addConstraint(i,ShiftType.morning(),1);
            addConstraint(i,ShiftType.evening(),1);
            addConstraint(i,ShiftType.morning(),2);
            addConstraint(i,ShiftType.evening(),2);
            addConstraint(i, ShiftType.morning(),3);
            addConstraint(i,ShiftType.evening(),3);
            addConstraint(i, ShiftType.morning(),4);
            addConstraint(i,ShiftType.evening(),4);
            addConstraint(i, ShiftType.morning(),5);
            addConstraint(i,ShiftType.evening(),5);
            addConstraint(i, ShiftType.morning(),6);
            addConstraint(i,ShiftType.evening(),6);
            addConstraint(i, ShiftType.morning(),123456789);
            addConstraint(i,ShiftType.evening(),123456789);
            addConstraint(i, ShiftType.morning(),246813579);
            addConstraint(i,ShiftType.evening(),246813579);
        }

//        addConstraint(4, ShiftType.morning(),123456789);
//        addConstraint(4,ShiftType.evening(),123456789);
//        addConstraint(4, ShiftType.morning(),246813579);
//        addConstraint(4,ShiftType.evening(),246813579);

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new java.sql.Date(Calendar.getInstance().getTime().getTime()));
        calendar.add(Calendar.DAY_OF_YEAR, 1);
        Date date = new Date(calendar.getTime().getTime());
        DateConvertor dateConvertor = new DateConvertor();
        String tomorow = dateConvertor.dateToString(date);
        addShift(toDate(tomorow),ShiftType.morning(),"beer sheba");
        addShift(toDate(tomorow),ShiftType.morning(),"beer sheba");
        addEmployeeToShift(1,toDate(tomorow),ShiftType.morning(),"beer sheba");
        addEmployeeToShift(123456789,toDate(tomorow),ShiftType.morning(),"beer sheba");

        addShift(toDate("25-05-2024"),ShiftType.morning(),"beer sheba");
        addShift(toDate("25-05-2024"),ShiftType.evening(),"beer sheba");
        addEmployeeToShift(1,toDate("25-05-2024"),ShiftType.morning(),"beer sheba");
        addEmployeeToShift(3,toDate("25-05-2024"),ShiftType.morning(),"beer sheba");
        addEmployeeToShift(4,toDate("25-05-2024"),ShiftType.morning(),"beer sheba");
        addEmployeeToShift(5,toDate("25-05-2024"),ShiftType.morning(),"beer sheba");
        addEmployeeToShift(123456789,toDate("25-05-2024"),ShiftType.morning(),"beer sheba");
        addEmployeeToShift(246813579,toDate("25-05-2024"),ShiftType.morning(),"beer sheba");
        addEmployeeToShift(123456789,toDate("25-05-2024"),ShiftType.evening(),"beer sheba");
        addEmployeeToShift(246813579,toDate("25-05-2024"),ShiftType.evening(),"beer sheba");

    }
    private static Date toDate(String date){
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Date ret=null;
        try {
            ret = dateFormat.parse(date);
        } catch (ParseException ignored) {}
        return ret;
    }
public Result<Collection<String>> getAllHRNotifications(){
        try{
            return new Result<Collection<String>>(false, shiftController.getAllHRNotifications());
        }catch (Exception e){
            return new Result<>(true,e.getMessage());
        }
}
public Result<Boolean> addHRNotification(Date date, License_Enum license_enum){
        try{
            shiftController.hRManagerNotification(date,license_enum);
            return new Result<Boolean>(false,true);
        }catch (Exception e){
            return new Result<>(true,e.getMessage());
        }
}
    // public EmployeeController()
public Result<Boolean> addConstraint(int _day,String _shift,int _id){
            try {
                return new Result<Boolean>(false,employeeController.addConstraint(_day,_shift,_id));
            }catch (Exception e){
                return new Result<Boolean>(true,e.getMessage());
            }

}
    public Result<List<SEmployee>> getEmployeesForShift(int _day, String _shift) {
        try {
            List<Employee> Emplist =  employeeController.getEmployeesForShift(_day,_shift);
            List<SEmployee> SerEmpList=new ArrayList<>();
            for (Employee emp:Emplist) {
                SerEmpList.add(new SEmployee(emp));
            }
            return new Result<List<SEmployee>>(false,SerEmpList);
        }catch (Exception e){
            return new Result<List<SEmployee>>(true,e.getMessage());
        }

    }
//    public Result<Boolean> updateEmployeeAvailability(int id, boolean isAvailable){
//        try{
//            employeeController.updateEmployeeAvailability(id,isAvailable);
//            return new Result<Boolean>(false,true);
//        }catch (Exception e){
//            return new Result<Boolean>(true,e.getMessage());
//        }
//    }
    public Result<List<SEmployee>> getAllEmployees(){
        try{
            List<SEmployee> ret = new ArrayList<>();
            for(Employee e :employeeController.getAllEmployees()){
                ret.add(new SEmployee(e));
            }
            return new Result<List<SEmployee>>(false,ret);
        }
        catch (Exception e){
            return new Result<List<SEmployee>>(true,e.getMessage());
        }
    }

    public Result<Boolean> removeConstraint(int _day,String _shift,int _id) {
        try {
            return new Result<Boolean>(false,employeeController.removeConstraint(_day,_shift,_id));
        }catch (Exception e){
            return new Result<Boolean>(true,e.getMessage());
        }
    }
    public Result<List<SShift>> getAllShifts(){
        try{
            List<SShift> ret = new ArrayList<>();
            List<Shift> l = new ArrayList<>(shiftController.getAllShifts());
            for(Shift s : l){
                ret.add(new SShift(s));
            }
            return new Result<List<SShift>>(false,ret);
        }
        catch (Exception e){
            return new Result<List<SShift>>(true,e.getMessage());
        }
    }




    public Result checkExists(int id) {
        try {
            employeeController.checkExists(id);
        }catch (Exception e){
            return new Result(true,e.getMessage());
        }
        return new Result(false,"");
    }

    public Result<SEmployee> removeEmployee(int id) {
        try {
            return new Result<SEmployee>(false,new SEmployee(employeeController.removeEmployee(id)));
        }catch (Exception e){
            return new Result<SEmployee>(true,e.getMessage());
        }
    }
    public Result<SEmployee> addEmployee(String _name, int _id, int _salary, int _bankAcc, Date _startDate,String _phone) {
        try {
            return new Result<SEmployee>(false,new SEmployee(employeeController.
                    addEmployee(_name,_id,_salary,_bankAcc,_startDate,_phone)));
        }catch (Exception e){
            return new Result<SEmployee>(true,e.getMessage());
        }
    }


    public Result checkNotExists(int id) {
        try {
            employeeController.checkNotExists(id);
        }catch (Exception e){
            return new Result(true,e.getMessage());
        }
        return new Result(false,"");
    }

    public Result<SEmployee> getEmployee(int id) {
        try {
            return new Result<SEmployee>(false,new SEmployee(employeeController.getEmployee(id )));
        }catch (Exception e){
            return new Result<SEmployee>(true,e.getMessage());
        }
    }


    public Result<Boolean> addEmployeeToShift( int toAdd,Date _date,String _shiftType,String branch) {
        try {
            return new Result<Boolean>(false,shiftController.addEmployeeToShift( toAdd, _date,_shiftType,branch));
        }catch (Exception e){
            return new Result<Boolean>(true,e.getMessage());
        }
    }
    public Result<Boolean> removeEmployeeFromShift( int toRemove,Date _date,String _shiftType,String branch) {
        try {
            return new Result<Boolean>(false,shiftController.removeEmployeeFromShift(toRemove,_date,_shiftType,branch));
        }catch (Exception e){
            return new Result<Boolean>(true,e.getMessage());
        }
    }
    public Result<SShift> addShift(Date _date , String _type,String branch) {
        try {
            return new Result<SShift>(false,new SShift(shiftController.addShift(_date ,_type,branch)));
        }catch (Exception e){
            return new Result<SShift>(true,e.getMessage());
        }
    }
    public Result<SShift> removeShift(Date _date , String type) {
        try {
            return new Result<SShift>(false,new SShift(shiftController.removeShift(_date ,type)));
        }catch (Exception e){
            return new Result<SShift>(true,e.getMessage());
        }
    }
    public Result<SShift> getShift(Date _date  , String type,String branch) {
        try {
            return new Result<SShift>(false,new SShift(shiftController.getShift(_date ,type,branch)));
        }catch (Exception e){
            return new Result<SShift>(true,e.getMessage());
        }
    }
    public Result<Boolean> addPositionToEmployee(int _id,String _pos){
        try{
            return new Result<Boolean>(false,employeeController.addPositionToEmployee(_id,_pos));
        }
        catch (Exception e){
            return new Result<Boolean>(true,e.getMessage());
        }
    }
    public Result<Boolean> removePositionToEmployee(int _id,String _pos){
        try{
            return new Result<Boolean>(false,employeeController.removePositionToEmployee(_id,_pos));
        }
        catch (Exception e){
            return new Result<Boolean>(true,e.getMessage());
        }
    }
    public Result<Boolean> UpdateEmployeeSalary(int _id,int _newSalary){
        try{
            employeeController.UpdateEmployeeSalary(_id,_newSalary);
            return new Result<Boolean>(false,true);
        }
        catch (Exception e){
            return new Result<Boolean>(true,e.getMessage());
        }
    }
    public Result<Boolean> UpdateEmployeeBankAccount(int _id,int _bankAcc){
        try{
            employeeController.UpdateEmployeeBankAccount(_id,_bankAcc);
            return new Result<Boolean>(false,true);
        }
        catch (Exception e){
            return new Result<Boolean>(true,e.getMessage());
        }
    }
    public Result<Boolean> UpdateEmployeeName(int _id,String _newName){
        try{
            employeeController.UpdateEmployeeName(_id, _newName);
            return new Result<Boolean>(false,true);
        }
        catch (Exception e){
            return new Result<Boolean>(true,e.getMessage());
        }
    }
    public Result<List<SEmployee>> getEmployeesByPosition(String _pos){
        try{
            List<SEmployee> ret = new ArrayList<>();
            List<Employee> employees = employeeController.getEmployeesByPosition(_pos);
            for(Employee e : employees)ret.add(new SEmployee(e));
            return new Result<List<SEmployee>>(false,ret);
        }
        catch (Exception e){
            return new Result<List<SEmployee>>(true,e.getMessage());
        }
    }
    public Result<Boolean> updateEmployeeLicense(int id,String license){
        try{
            boolean b =employeeController.UpdateEmployeeLicense(id,license);
            return new Result<Boolean>(false,b);
        }catch (Exception e){
            return new Result<Boolean>(true,e.getMessage());
        }
    }

    public Result<Collection<DriverDataObj>> getAvailableDrivers(String shiftDate) {
        try{
            Collection<Employee> drivers = shiftController.getAllAvailableDrivers(shiftDate);
            Collection<DriverDataObj> dataDrivers = new ArrayList<>();
            for(Employee e : drivers){
                dataDrivers.add(new DriverDataObj(e));
            }
            return new Result<Collection<DriverDataObj>>(false,dataDrivers);
        }catch (Exception e){
            return new Result<Collection<DriverDataObj>>(true,e.getMessage());
        }
    }

    public Result<List<DriverDataObj>> getAvailableDriversByLicense(String shiftDate, License_Enum license) {
        try{
            Collection<Employee> drivers = shiftController.getAllAvailableDriversByLicense(shiftDate,license);
            List<DriverDataObj> dataDrivers = new ArrayList<>();
            for(Employee e : drivers){
                dataDrivers.add(new DriverDataObj(e));
            }
            return new Result<List<DriverDataObj>>(false,dataDrivers);
        }catch (Exception e){
            return new Result<List<DriverDataObj>>(true,e.getMessage());
        }
    }

    public Result initial() {
        try{
            employeeController.initial();
            return new Result(false,"");
        }catch (Exception e){
            return new Result(true,e.getMessage());
        }
    }

//    public Result<List<SEmployee>> getEmployeesByPositionAndShift(int _day,String _shift,String _pos){
//        try{
//            List<SEmployee> ret = new ArrayList<>();
//            List<Employee> employees = employeeController.getEmployeesByPositionAndShift(_day, _shift, _pos);
//            for(Employee e : employees)ret.add(new SEmployee(e));
//            return new Result<List<SEmployee>>(false,ret);
//        }
//        catch (Exception e){
//            return new Result<List<SEmployee>>(true,e.getMessage());
//        }
//    }



}
