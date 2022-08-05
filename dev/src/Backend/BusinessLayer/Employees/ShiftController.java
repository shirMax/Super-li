package Backend.BusinessLayer.Employees;







import Backend.BusinessLayer.CallBacks.IsEmployeeAvailableCallBack;
import Backend.BusinessLayer.Deliveries.Enums.License_Enum;
import Backend.BusinessLayer.Suppliers.Contact;
import Backend.BusinessLayer.Tools.DateConvertor;
import Backend.PersistenceLayer.BranchesDAO;
import Backend.PersistenceLayer.EmployeesDal.ConstraintsDAO;
import Backend.PersistenceLayer.EmployeesDal.EmployeeDAO;
import Backend.PersistenceLayer.EmployeesDal.ShiftDAO;

import java.util.*;


public class ShiftController {

    private final BranchesDAO branchesDAO;
    private final ShiftDAO shiftDAO;
    private final EmployeeDAO employeeDAO;
    private final ConstraintsDAO conDAO;
    private final int SHOULD_IGNORE = -1;
    private IsEmployeeAvailableCallBack isEmployeeAvailableCallBack;

    public ShiftController(ShiftDAO shiftDAO,EmployeeDAO employeeDAO,ConstraintsDAO conDAO){
        this.shiftDAO = shiftDAO;
        this.employeeDAO = employeeDAO;
        this.conDAO = conDAO;
        this.branchesDAO = new BranchesDAO();
    }

    public Collection<Shift> getAllShifts() throws Exception {
        Collection<Shift> shifts = shiftDAO.getAllShifts();
        for(Shift s : shifts){
            loadShiftEmployees(s);
        }
        return shifts;
    }
    public void loadShiftEmployees(Shift shift) throws Exception {
        List<Employee> employees=new ArrayList<>();
        for(Integer i : shift.getDataEmployees()){
            Employee e = employeeDAO.getEmployee(i);
            employees.add(e);
        }
        shift.setEmployees(employees);
    }

    public boolean addEmployeeToShift(int toAdd,Date _date,String _shiftType,String _branch) throws Exception {
        if(!ShiftType.isShiftType(_shiftType))throw new Exception("Ilegal shift type"+_shiftType);
        checkExists(_date,_shiftType,_branch);
        Shift s = getShift(_date,_shiftType,_branch);
        if(s.hasEmployee(toAdd))throw new Exception("Error: employee with id:"+toAdd+" is already working in this shift");
        List<Integer> canWork = conDAO.getEmployeeIDsForShift(toShiftNum(_date.getDay(), _shiftType));
        if(!canWork.contains(toAdd))throw new Exception("Employee "+toAdd+" cannot work in this shift");
        Employee e = employeeDAO.getEmployee(toAdd);
        shiftDAO.addEmployeeToShift(e,new java.sql.Date(_date.getTime()),_shiftType, _branch);
        return true;
    }
    public boolean removeEmployeeFromShift(int toRemove,Date _date,String _shiftType,String _branch) throws Exception {
        checkExists(_date,_shiftType,_branch);
        Shift s = getShift(_date,_shiftType,_branch);
        Employee e = employeeDAO.getEmployee(toRemove);
        //DateConvertor dateConvertor = new DateConvertor();
        if(e==null)throw new Exception("no such employee id:"+toRemove);
        //if(dateConvertor.compareDate(dateConvertor.dateToString(_date))<0)
            if(isEmployeeAvailableCallBack.call(e.getName(),e.getPhoneNumber(), _date))
                throw new Exception("employee id:"+toRemove + " cannot be removed! his on task");
        if(!e.isAvailable())
            throw new Exception("employee id:"+toRemove + " cannot be removed! his on task");
        if(s.hasEmployee(toRemove)){
            shiftDAO.removeEmployeeFromShift(e,new java.sql.Date(_date.getTime()),_shiftType,_branch );
            s.removeEmployee(e);
            return true;
        }
        else throw new Exception("Erorr: employee "+toRemove+" is not registered to shift.");
    }

    public Shift addShift(Date _date , String _type,String _branch) throws Exception {
        if(!branchesDAO.checkIfBranchExists(_branch))throw new Exception("Branch does not exist");
        shiftDAO.addShift(new java.sql.Date(_date.getTime()),_type,_branch);
        return getShift(_date, _type,_branch);
    }
    public Shift removeShift(Date _date , String _type) throws Exception {
        throw new NoSuchElementException();
    }
    public Shift getShift(Date _date , String type,String branch) throws Exception {

        Shift shift= shiftDAO.getShift(new java.sql.Date(_date.getTime()),type,branch);
        if(shift.getEmployees().isEmpty()) {
            List<Integer> emps = shift.getDataEmployees();
            if(emps!=null) {
                for (Integer id : emps) {
                    shift.addEmployee(employeeDAO.getEmployee(id));
                }
            }
        }
        return shift;
    }

    public boolean CheckShiftAvailability(Date date, int employeeId , License_Enum license) {
        String shift;
        int hour = date.getHours();
        int SHIFT_SWITCH_HOUR = 15;
        if(hour> SHIFT_SWITCH_HOUR)shift=ShiftType.evening();
        else shift=ShiftType.morning();
        String sLicense = license.name();
        try {
            Employee employee = employeeDAO.getEmployee(employeeId);
            Collection<Shift> shifts = getShiftsByDate(date,shift);
            if(shifts == null || shifts.size() == 0)
                return false;
            for(Shift s : shifts){
                if(!s.hasEmployee(employeeId))return false;
            }
            License_Enum license_enum = StringToLicense(employee.getLicense());
            if(license_enum==null)return false;
            if(!license.canIDriveIt(license))return false;
            return employee.isAvailable();
        }catch (Exception e){
            return false;
        }
    }
    private License_Enum StringToLicense(String license){
        License_Enum b = License_Enum.B;
        License_Enum c1 = License_Enum.C1;
        License_Enum c = License_Enum.C;
        License_Enum ce = License_Enum.CE;
        List<License_Enum> enums = new ArrayList<>();
        enums.add(b);enums.add(c1);enums.add(ce);enums.add(c);
        for (License_Enum e : enums){
            if(e.name().equals(license))return e;
        }
        return null;
    }

    private int toShiftNum(int day, String shift) throws Exception {
        if(day>6 | day<0 | !ShiftType.isShiftType(shift))throw new Exception("Error: ilegal day and shift.");
        if(shift.equals(ShiftType.morning()))return 2*day;
        return 1+(2*day);
    }
    private void checkExists(Date date,String type,String branch) throws Exception {
        Shift s = getShift(date, type,branch);
        if(s==null)throw new Exception("Error: shift does not exist.");
    }
    public void updateEmployeeAvailability(int id,boolean isAvailable) throws Exception {
        employeeDAO.updateEmployeeAvailability(id,isAvailable);
    }

    public boolean SwitchDrivers(Date date, int oldDriver, int newDriver) {
        if(newDriver != SHOULD_IGNORE){
            try{
                Employee employee = employeeDAO.getEmployee(oldDriver);
                switchAvailability(newDriver);
                switchAvailability(oldDriver);
                return true;
            } catch (Exception exception) {
                return false;
            }
        }
        else{
            try{
                Employee employee = employeeDAO.getEmployee(oldDriver);
                switchAvailability(oldDriver);
                return true;
            }catch (Exception e){
                return false;
            }
        }
    }

    private void switchAvailability(int Driver) throws Exception {
        Employee e = employeeDAO.getEmployee(Driver);
        updateEmployeeAvailability(Driver,!e.isAvailable());
    }

    public Contact CheckStockKeeper(Date date, String branchAddress) {
        String shift;
        int hour = date.getHours();
        int SHIFT_SWITCH_HOUR = 15;
        if(hour> SHIFT_SWITCH_HOUR)shift=ShiftType.evening();
        else shift=ShiftType.morning();
        try{
            Shift s = getShift(date,shift,branchAddress);
            if(!s.hasPosition(Positions.stockKeeper()))return null;
            for(Employee e : s.getEmployees()){
                if(e.hasPosition(Positions.stockKeeper()))
                    return new Contact(e.getId(),e.getName(),e.getPhoneNumber());
            }
            return null;
        } catch (Exception exception) {
            return null;
        }
    }

    private List<Employee> filterByAvailability(List<Employee> drivers){
        List<Employee> filtered = new ArrayList<>();
        for(Employee e:drivers){
            if(e.isAvailable())filtered.add(e);
        }
        return filtered;
    }
    private List<Employee> filterByPosition(Collection<Employee> _toFilter, String _pos){
        List<Employee> ret = new ArrayList<>();
        for(Employee e : _toFilter) if(e.hasPosition(_pos)) ret.add(e);
        return ret;
    }

    public Collection<Employee> getAllAvailableDrivers(String shiftDate) throws Exception {
        DateConvertor d1 = new DateConvertor();
        Date date = d1.validateDate(shiftDate);
        String shift;
        int hour = date.getHours();
        int SHIFT_SWITCH_HOUR = 15;
        if(hour> SHIFT_SWITCH_HOUR)shift=ShiftType.evening();
        else shift=ShiftType.morning();
        List<Shift> shifts = getShiftsByDate(date,shift);
        List<Employee> drivers = new ArrayList<>();
        for(Shift s : shifts){
            drivers.addAll(filterByPosition(s.getEmployees(),Positions.driver()));
        }
        return filterByAvailability(drivers);
    }
    private List<Shift> getShiftsByDate(Date date,String shift) throws Exception {
        Calendar cal1 = Calendar.getInstance();
        cal1.setTime(date);
        List<Shift> ret= new ArrayList<>();
        for(Shift s : getAllShifts()){
            if(compareDate(cal1, s.getDate()) & s.getType().equals(shift))ret.add(s);
        }
        return ret;
    }

    private boolean compareDate(Calendar cal1, Date date){
        Calendar cal2 = Calendar.getInstance();
        cal2.setTime(date);
        return cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
    }

    public Collection<Employee> getAllAvailableDriversByLicense(String shiftDate, License_Enum license) throws Exception {
        Collection<Employee> availableDrivers = getAllAvailableDrivers(shiftDate);
        Collection<Employee> ret = new ArrayList<>();
        License_Enum b = License_Enum.B;
        License_Enum c1 = License_Enum.C1;
        License_Enum c = License_Enum.C;
        License_Enum ce = License_Enum.CE;
        List<License_Enum> enums = new ArrayList<>();
        enums.add(b);enums.add(c1);enums.add(ce);enums.add(c);
        for(License_Enum l : enums){
            if(l.canIDriveIt(license))ret.addAll(filterByLicense(availableDrivers,l));
        }
        return ret;
    }

    private Collection<Employee> filterByLicense(Collection<Employee> availableDrivers, License_Enum license) {
        List<Employee> filtered = new ArrayList<>();
        for(Employee e : availableDrivers){
            if(e.getLicense().equals(license.name()))filtered.add(e);
        }
        return filtered;
    }

    public void hRManagerNotification(Date date, License_Enum license_enum)  {
        String msg = "Please assign a driver with license "+license_enum.name()+"\n" +
                "to work in date "+date.toLocaleString().substring(9)+", thank you.";
        try {
            employeeDAO.addMessage(msg);
        }catch (Exception e){
            System.out.println("Do Nothing");
        }
    }

    public Collection<String> getAllHRNotifications() throws Exception {
        return employeeDAO.getAllMessages();
    }

    public void setIsEmployeeAvailableCallBack(IsEmployeeAvailableCallBack isEmployeeAvailableCallBack) {
        this.isEmployeeAvailableCallBack = isEmployeeAvailableCallBack;
    }
}
