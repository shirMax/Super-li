package Backend.PersistenceLayer.EmployeesDal;

import Backend.BusinessLayer.Deliveries.Truck;
import Backend.BusinessLayer.Employees.Employee;
import Backend.PersistenceLayer.DalController;

import java.sql.*;
import java.sql.Date;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.*;


public class EmployeeDAO extends DalController {
    private Map<Integer, Employee> EmployeeMap;
    final String EMPLOYEE_TABLE_NAME="Employees";
    final String ID_COLUMN_NAME = "EmployeeID";
    final String NAME_COLUMN_NAME = "Name";
    final String SALARY_COLUMN_NAME = "Salary";
    final String START_DATE_COLUMN_NAME = "StartDate";
    final String POSITIONS_COLUMN_NAME="Positions";
    final String PHONE_COLUMN_NAME="Phone";
    final String LICENSE_COLUMN_NAME="License";
    final String BANK_ACC_COLUMN_NAME = "BankAcc";
    final String IS_AVAILABLE_COLUMN_NAME = "IsAvailable";
    final String MESSAGES_TABLE_NAME = "Messages";
    final String MESSAGE_DATE_COLUMN_NAME="MessageDate";
    final String MESSAGE_CONTENT_COLUMN_NAME="MessageContent";

    public EmployeeDAO() {
        EmployeeMap = new HashMap<Integer, Employee>() ;
    }
    public void addEmployee(int id, String name, int salary, Date startDate , String phone,int bankAcc) throws Exception {
        Employee employee = selectEmployee(id,ID_COLUMN_NAME,EMPLOYEE_TABLE_NAME);
        if (employee == null) {
            try {
                insert( id,  name, salary,  startDate , phone,bankAcc);
                addEmployeeToIDMap(id,  name, salary,  startDate , phone,bankAcc);

            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
    }

    private void addEmployeeToIDMap(int id, String name, int salary, Date startDate, String phone,int bankAcc) throws Exception {
        if(!EmployeeMap.containsKey(id)){
            try {
                Employee e = new Employee(name, id, salary, bankAcc, startDate, phone);
                EmployeeMap.put(id,e);
            }catch (Exception exception){
                throw new Exception(exception.getMessage());
            }
        }
    }

    private void insert(int id, String name, int salary, Date startDate, String phone,int bankAcc) throws Exception {
        String positions = "";
        String license = "none";
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}, {9}) VALUES(?,?,?,?,?,?,?,?,?)",
                EMPLOYEE_TABLE_NAME,ID_COLUMN_NAME,NAME_COLUMN_NAME,SALARY_COLUMN_NAME,START_DATE_COLUMN_NAME,POSITIONS_COLUMN_NAME,PHONE_COLUMN_NAME,
                LICENSE_COLUMN_NAME,BANK_ACC_COLUMN_NAME,IS_AVAILABLE_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,id);
            pstmt.setString(2,name);
            pstmt.setInt(3,salary);
            pstmt.setString(4, new java.sql.Date(startDate.getTime()).toString());
            pstmt.setString(5,positions);
            pstmt.setString(6,phone);
            pstmt.setString(7,license);
            pstmt.setInt(8,bankAcc);
            pstmt.setBoolean(9,true);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }
    }
    private void insert(int id, String name, int salary, Date startDate, String phone,int bankAcc,String license) throws Exception {
        String positions = "Driver";
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}, {6}, {7}, {8}) VALUES(?,?,?,?,?,?,?,?)",
                EMPLOYEE_TABLE_NAME,ID_COLUMN_NAME,NAME_COLUMN_NAME,SALARY_COLUMN_NAME,START_DATE_COLUMN_NAME,POSITIONS_COLUMN_NAME,PHONE_COLUMN_NAME,
                LICENSE_COLUMN_NAME,BANK_ACC_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,id);
            pstmt.setString(2,name);
            pstmt.setInt(3,salary);
            pstmt.setString(4, new java.sql.Date(startDate.getTime()).toString());
            pstmt.setString(5,positions);
            pstmt.setString(6,phone);
            pstmt.setString(7,license);
            pstmt.setInt(98,bankAcc);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }
    }
    public Collection<Employee> getAllEmployees() throws Exception {
        return selectAllEmployees(EMPLOYEE_TABLE_NAME);
    }

    private Employee selectEmployee( int id1,String columnName1, String tableName) throws Exception {
        String sql = "SELECT * FROM " +tableName+ " WHERE "+ columnName1+"="+id1;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next())
                return ConvertReaderToEmployee(rs);
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }
        return null;
    }

    public void updateEmployeeLicense(int id,String newLicense) throws Exception {
        Employee e = getEmployee(id);
        update(EMPLOYEE_TABLE_NAME,LICENSE_COLUMN_NAME,newLicense,ID_COLUMN_NAME,id);
        e.setLicense(newLicense);
    }
    public void updateEmployeeName(int id,String newName) throws Exception {
        Employee e = getEmployee(id);
        update(EMPLOYEE_TABLE_NAME,NAME_COLUMN_NAME,newName,ID_COLUMN_NAME,id);
        e.setName(newName);
    }
    public void updateEmployeeSalary(int id,int newSalary) throws Exception {
        Employee e = getEmployee(id);
        update(EMPLOYEE_TABLE_NAME,SALARY_COLUMN_NAME,newSalary,ID_COLUMN_NAME,id);
        e.setSalary(newSalary);
    }
    public void updateEmployeeBankAccount(int id,int bankAcc) throws Exception {
        Employee e = getEmployee(id);
        update(EMPLOYEE_TABLE_NAME,BANK_ACC_COLUMN_NAME,bankAcc,ID_COLUMN_NAME,id);
        e.setBankAccount(bankAcc);
    }
    public void addPositionToEmployee(int id,String position) throws Exception {
        Employee e = getEmployee(id);
        if(e.hasPosition(position))throw new Exception("Employee with id:"+id+" already is a "+position);
        List<String> newPos = new ArrayList<>(e.getPositions());
        newPos.add(position);
        String parsed = parsePositions(newPos);
        updateEmployeePositions(id,parsed);
        e.addPosition(position);
    }
    public void updateEmployeeAvailability(int employeeId,boolean isAvailable) throws Exception {
        Employee e = getEmployee(employeeId);
        if(e==null)throw new Exception("Error: employee with id:"+employeeId+" does not exist");
        update(EMPLOYEE_TABLE_NAME,IS_AVAILABLE_COLUMN_NAME,isAvailable,ID_COLUMN_NAME,employeeId);
        e.setAvailable(isAvailable);
    }

    public void removePositionToEmployee(int id,String position) throws Exception {
        Employee e = getEmployee(id);
        if(!e.hasPosition(position))throw new Exception("Employee with id:"+id+" already is not a "+position);
        List<String> newPos = new ArrayList<>(e.getPositions());
        newPos.remove(position);
        String parsed = parsePositions(newPos);
        updateEmployeePositions(id,parsed);
        e.removePosition(position);
    }

    private void updateEmployeePositions(int id, String newPositions) throws Exception {
        update(EMPLOYEE_TABLE_NAME,POSITIONS_COLUMN_NAME,newPositions,ID_COLUMN_NAME,id);
    }

    private Employee ConvertReaderToEmployee(ResultSet rs) {
        Employee result = null;
        try {
            java.util.Date date = StringToDate(rs.getString(4));
            result = new Employee(rs.getString(2),rs.getInt(1),rs.getInt(3),rs.getInt(8),date,rs.getString(6));
            result.setPositions(rs.getString(5));
            result.setLicense(rs.getString(7));
            result.setAvailable(rs.getBoolean(IS_AVAILABLE_COLUMN_NAME));
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }
    public Collection<Employee> selectAllEmployees(String tableName) throws Exception {
        String sql = "SELECT * FROM " +tableName;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            Collection<Employee> emps = new ArrayList<>();
            while(rs.next())
                emps.add(ConvertReaderToEmployee(rs));
            return emps;
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }
    }

    public Employee getEmployee(int empID) throws Exception {
        if(EmployeeMap.containsKey(empID)){
            return EmployeeMap.get(empID);
        }
        Employee e = selectEmployee(empID,ID_COLUMN_NAME,EMPLOYEE_TABLE_NAME);
        if(e == null)
            throw new Exception("Error : No such employee with the given id : " +empID);
        addEmployeeToIDMap(e);
        return e;
    }
    private void addEmployeeToIDMap(Employee e) throws Exception {
        if (!EmployeeMap.containsKey(e.getId())){
            EmployeeMap.put(e.getId(),e);
        }
        else throw new Exception("Error: employee already exists.");
    }

    public void removeEmployee(int id) throws Exception {
        remove(id,ID_COLUMN_NAME,EMPLOYEE_TABLE_NAME);
    }
    private String parsePositions(List<String> positions){
        StringBuilder stringBuilder = new StringBuilder();
        for(String s : positions){
            stringBuilder.append(s);
            stringBuilder.append("-");
        }
        String ret = stringBuilder.toString();
        if(ret.length()==0)return ret;
        return ret.substring(0,ret.length()-1);
    }
    private java.util.Date StringToDate(String string) throws ParseException {
        java.util.Date date1=new SimpleDateFormat("yyyy-MM-dd").parse(string);
        return date1;
    }
    public void addMessage(String msg) throws Exception {
        String msgTime = LocalDateTime.now().toString();
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}) VALUES(?,?)",
                MESSAGES_TABLE_NAME,MESSAGE_DATE_COLUMN_NAME,MESSAGE_CONTENT_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1,msgTime);
            pstmt.setString(2,msg);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }
    }
    public Collection<String> getAllMessages() throws Exception {
        String sql = "SELECT * FROM " +MESSAGES_TABLE_NAME;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            Collection<String> messages = new ArrayList<>();
            while(rs.next())
                messages.add(ConvertReaderToMessage(rs));
            return messages;
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }
    }

    private String ConvertReaderToMessage(ResultSet rs) throws Exception {
        try{
            return "Message from "+splitDate(rs.getString(MESSAGE_DATE_COLUMN_NAME))+":\n" +
                    "Content: "+rs.getString(MESSAGE_CONTENT_COLUMN_NAME);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
    private String splitDate(String date){
        String[] arr = date.split("T");
        return arr[0]+" "+arr[1];
    }
}
