package Backend.BusinessLayer.Employees;


import Backend.BusinessLayer.CallBacks.GetShiftsCallBack;
import Backend.BusinessLayer.Tools.DateConvertor;
import Backend.PersistenceLayer.CreateDB;
import Backend.PersistenceLayer.EmployeesDal.ConstraintsDAO;
import Backend.PersistenceLayer.EmployeesDal.CreateDBEmployees;
import Backend.PersistenceLayer.EmployeesDal.EmployeeDAO;
import Backend.PersistenceLayer.EmployeesDal.ShiftDAO;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class EmployeeController {

    private final EmployeeDAO empDAO;
    private final ConstraintsDAO conDAO;
    private final ShiftDAO shiftDAO;
    private GetShiftsCallBack getShiftsCallBack;

    public EmployeeController(EmployeeDAO empDAO, ConstraintsDAO conDAO, ShiftDAO shiftDAO)  {
        this.conDAO= conDAO;
        this.empDAO = empDAO;
        this.shiftDAO = shiftDAO;
    }
    public List<Employee> getAllDrivers() throws Exception {
        return getEmployeesByPosition(Positions.driver());
    }

    public List<Employee> getEmployeesByPosition(String pos) throws Exception {
        Collection<Employee> emps=empDAO.getAllEmployees();
        return filterByPosition(emps,pos);
    }
    private List<Employee> filterByPosition(Collection<Employee> _toFilter, String _pos){
        List<Employee> ret = new ArrayList<>();
        for(Employee e : _toFilter) if(e.hasPosition(_pos)) ret.add(e);
        return ret;
    }
    public Collection<Employee> getAllEmployees() throws Exception {
        return empDAO.getAllEmployees();
    }
    public boolean addPositionToEmployee(int _id,String _pos) throws Exception {
        checkPositionReal(_pos);
        checkExists(_id);
        empDAO.addPositionToEmployee(_id,_pos);
        return true;
    }


    private void checkPositionReal(String pos) throws Exception {
        if(!Positions.getPositions().contains(pos))throw new Exception("Posiotion "+pos+" doesnt exist!");
    }

    public boolean removePositionToEmployee(int _id,String _pos) throws Exception {
        checkPositionReal(_pos);
        checkExists(_id);
        empDAO.removePositionToEmployee(_id,_pos);
        return true;
    }
    public Employee UpdateEmployeeSalary(int _id,int _newSalary) throws Exception {
        checkExists(_id);
        empDAO.updateEmployeeSalary(_id,_newSalary);
        return getEmployee(_id);
    }
    public Employee UpdateEmployeeBankAccount(int _id,int _bankAcc) throws Exception {
        checkExists(_id);
        empDAO.updateEmployeeBankAccount(_id,_bankAcc);
        return getEmployee(_id);
    }
    public Employee UpdateEmployeeName(int _id,String _newName) throws Exception {
        checkExists(_id);
        empDAO.updateEmployeeName(_id,_newName);
        return getEmployee(_id);
    }
    public Boolean UpdateEmployeeLicense(int _id,String _license) throws Exception {
        checkExists(_id);
        empDAO.updateEmployeeLicense(_id,_license);
        return true;
    }
    public boolean addConstraint(int _day,String _shift,int _id) throws Exception {
        checkExists(_id);
        conDAO.addConstraint(toShiftNum(_day,_shift),_id);
        return true;
    }

    public List<Employee> getEmployeesForShift(int _day, String _shift) throws Exception {
        List<Integer> ids = conDAO.getEmployeeIDsForShift(toShiftNum(_day,_shift));
        List<Employee> ret = new ArrayList<>();
        for(Integer id : ids)
            ret.add(getEmployee(id));
        return ret;
    }

    public List<Employee> getEmployeesForShiftByPosition(int _day, String _shift,String _pos) throws Exception {
        List<Employee> employeesForShift = getEmployeesForShift(_day,_shift);
        return filterByPosition(employeesForShift,_pos);
    }

    public boolean removeConstraint(int _day,String _shift,int _id) throws Exception {
        checkExists(_id);
        conDAO.removeConstraint(toShiftNum(_day,_shift),_id);
        return true;
    }

    private int toShiftNum(int day, String shift) throws Exception {
        if(day>6 | day<0 | !ShiftType.isShiftType(shift))throw new Exception("Error: ilegal day and shift.");
        if(shift.equals(ShiftType.morning()))return 2*day;
        return 1+(2*day);
    }

    public void checkExists(int id) throws Exception {
        Employee e = getEmployee(id);
        if(e==null)throw new Exception("Error: employee with id:"+id+" does not exist.");
    }

    public Employee removeEmployee(int id) throws Exception {
        Employee e = getEmployee(id);
        if (e == null) throw new Exception("Error: employee with id:" + id + " does not exist.");
        if (!e.isAvailable()) throw new Exception("employee id:" + id + " cannot be removed! his on task");
        Collection<Shift> shifts = getShiftsCallBack.call();
        DateConvertor dateConvertor = new DateConvertor();
        for (Shift shift : shifts) {
            if(dateConvertor.compareDate(dateConvertor.dateToStringWithoutHour(shift.getDate()) + " 00:00") < 0 )
                if (shift.hasEmployee(id))
                    throw new Exception("the employee is already assigned to shift in: " + shift.getDate().toString());
        }
        empDAO.removeEmployee(id);
        return e;
    }

/*    public Collection<Shift> getAllShifts() throws Exception {
        Collection<Shift> shifts = shiftDAO.getAllShifts();
        for(Shift s : shifts){
            loadShiftEmployees(s);
        }
        return shifts;
    }

    public void loadShiftEmployees(Shift shift) throws Exception {
        List<Employee> employees=new ArrayList<>();
        for(Integer i : shift.getDataEmployees()){
            Employee e = empDAO.getEmployee(i);
            employees.add(e);
        }
        shift.setEmployees(employees);
    }*/

    public Employee addEmployee(String _name, int _id, int _salary, int _bankAcc,Date _startDate,String _phone) throws Exception {
        java.sql.Date sqlDate = new java.sql.Date(_startDate.getTime());
        empDAO.addEmployee(_id,_name,_salary,sqlDate,_phone,_bankAcc);
        return getEmployee(_id);
    }

    public void checkNotExists(int id) throws Exception {
        if(getEmployee(id)==null)throw new Exception("Employee with ID:"+id+" already exists!");
    }

    public Employee getEmployee(int id) throws Exception {
        return empDAO.getEmployee(id);
    }

    public void initial() {
        CreateDB createDBEmployees = new CreateDBEmployees();
        createDBEmployees.createFileIfNotExists();
    }

    public void hRManagerNotification(Date date){
        throw new NotImplementedException();
    }

    public void setGetShiftsCallBack(GetShiftsCallBack getShiftsCallBack){
        this.getShiftsCallBack = getShiftsCallBack;
    }
}
