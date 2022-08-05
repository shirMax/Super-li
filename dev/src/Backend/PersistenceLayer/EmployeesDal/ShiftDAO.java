package Backend.PersistenceLayer.EmployeesDal;

import Backend.BusinessLayer.Employees.Employee;
import Backend.BusinessLayer.Employees.Shift;
import Backend.BusinessLayer.Tools.DateConvertor;
import Backend.PersistenceLayer.DalController;

import java.sql.*;
import java.sql.Date;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ShiftDAO extends DalController {
    private final Map<String, Map<String, Shift[]>> shiftsByDateAndBranch;
    final String SHIFT_TABLE_NAME = "Shift";
    final String DATE_COLUMN_NAME = "Date";
    final String SHIFT_TYPE_COLUMN_NAME = "Type";
    final String EMPLOYEES_COLUMN_NAME = "Employees";
    final String BRANCH_COLUMN_NAME = "Branch";

    public ShiftDAO() {
        shiftsByDateAndBranch = new ConcurrentHashMap<>();
    }

    public void addShift(Date _date, String _type, String _branch) throws Exception {

        Shift s = selectShift(_date, _type, _branch, DATE_COLUMN_NAME, SHIFT_TYPE_COLUMN_NAME, BRANCH_COLUMN_NAME, SHIFT_TABLE_NAME);
        if (s == null) {
            try {
                insert(_date, _type, _branch);
                addShiftToIDMap(_date, _type, _branch);

            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
    }

    public Collection<Shift> getAllShifts() throws Exception {
        return selectAllShifts(SHIFT_TABLE_NAME);
    }

    private void insert(Date date, String type, String branch) throws Exception {
        java.sql.Date sqlDate = new java.sql.Date(date.getTime());
        String employees = "";
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}) VALUES(?,?,?,?)",
                SHIFT_TABLE_NAME, DATE_COLUMN_NAME, SHIFT_TYPE_COLUMN_NAME, EMPLOYEES_COLUMN_NAME, BRANCH_COLUMN_NAME);

        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, sqlDate.toString());
            pstmt.setString(2, type);
            pstmt.setString(3, employees);
            pstmt.setString(4, branch);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }
    }

    private void addShiftToIDMap(Date date, String type, String branch) throws Exception {
        Shift shift = new Shift(date, type, branch);
        if (!shiftsByDateAndBranch.containsKey(branch)) {
            shiftsByDateAndBranch.put(branch, new ConcurrentHashMap<>());
        }
        Map<String, Shift[]> shiftsByDate = shiftsByDateAndBranch.get(branch);

        DateConvertor dateConvertor = new DateConvertor();
        String key = dateConvertor.dateToStringWithoutHour(date);

        if (!shiftsByDate.containsKey(key)) {
            Shift[] shiftArr = new Shift[2];

            if (type.equals("Morning")) shiftArr[0] = shift;
            else shiftArr[1] = shift;
            shiftsByDate.put(key, shiftArr);
        } else {
            if (type.equals("Morning")) {
                if (shiftsByDate.get(key)[0] == null) {
                    shiftsByDate.get(key)[0] = shift;
                } else throw new Exception("shift exist");
            } else if (type.equals("Evening")) {
                if (shiftsByDate.get(key)[1] == null) {
                    shiftsByDate.get(key)[1] = shift;
                } else throw new Exception("shift exist");
            } else throw new Exception("shift exist");
        }

    }

    private Shift selectShift(Date property1, String property2, String property3, String columnName1, String columnName2, String columnName3, String tableName) throws Exception {
        java.sql.Date sqlDate = new Date(property1.getTime());
        String sql = "SELECT * FROM " + tableName + " WHERE " + columnName1 + "='" + sqlDate + "' AND " + columnName2 + "='" + property2 + "' AND " + columnName3 + "='" + property3 + "'";
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next())
                return ConvertReaderToShift(rs);
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }
        return null;
    }

    private Shift ConvertReaderToShift(ResultSet rs) {
        Shift result = null;
        try {
            java.util.Date date = StringToDate(rs.getString(1));
            result = new Shift(date, rs.getString(2), rs.getString(4));
            result.setDataEmployees(rs.getString(3));
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }

    private java.util.Date StringToDate(String string) throws ParseException {
        java.util.Date date1 = new SimpleDateFormat("yyyy-MM-dd").parse(string);
        return date1;
    }

    private boolean UpdateShift(String propToUpadte, String ColToUpdate, Date property1, String property2, String property3, String columnName1, String columnName2, String columnName3, String tableName) throws Exception {
        java.sql.Date sqlDate = new Date(property1.getTime());
        String sql = "UPDATE " + tableName + " SET " + ColToUpdate + "='" + propToUpadte + "' WHERE " + columnName1 + "='" + sqlDate + "' AND " + columnName2 + "='" + property2 + "' AND " + columnName3 + "='" + property3 + "'";
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();) {
            stmt.executeUpdate(sql);
            return true;
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }
    }

    public void removeEmployeeFromShift(Employee employee, Date date, String shiftType, String branch) throws Exception {
        Shift shift = getShift(date, shiftType, branch);//TODO
        List<Employee> emps = new ArrayList<>(shift.getEmployees());
        emps.remove(employee);
        String newEmployees = employeesToString(emps);
        UpdateShift(newEmployees, EMPLOYEES_COLUMN_NAME, date, shiftType, branch, DATE_COLUMN_NAME, SHIFT_TYPE_COLUMN_NAME, BRANCH_COLUMN_NAME ,SHIFT_TABLE_NAME);
        shift.removeEmployee(employee);

    }


    public Collection<Shift> selectAllShifts(String tableName) throws Exception {
        String sql = "SELECT * FROM " + tableName;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            Collection<Shift> shifts = new ArrayList<>();
            while (rs.next())
                shifts.add(ConvertReaderToShift(rs));
            return shifts;
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }
    }

    public void addEmployeeToShift(Employee employee, Date date, String shiftType, String branch) throws Exception {
        Shift shift = getShift(date, shiftType, branch);//TODO
        List<Employee> emps = new ArrayList<>(shift.getEmployees());
        emps.add(employee);
        String newEmployees = employeesToString(emps);
        UpdateShift(newEmployees, EMPLOYEES_COLUMN_NAME, date, shiftType, branch, DATE_COLUMN_NAME, SHIFT_TYPE_COLUMN_NAME, BRANCH_COLUMN_NAME, SHIFT_TABLE_NAME);
        shift.addEmployee(employee);
    }

    private String employeesToString(List<Employee> employees) {
        StringBuilder ret = new StringBuilder();
        for (Employee e : employees) {
            ret.append(e.getId());
            ret.append("-");
        }
        String s = ret.toString();
        if(s.length()==0)return s;
        return s.substring(0, s.length() - 1);
    }

    public Shift getShift(Date date, String type, String branch) throws Exception {
        if (!((type.equals("Morning")) | (type.equals("Evening")))) throw new Exception("Error:Ilegal shift type.");
        if (shiftsByDateAndBranch.containsKey(branch)) {
            Map<String, Shift[]> shiftsByDate = shiftsByDateAndBranch.get(branch);

            DateConvertor dateConvertor = new DateConvertor();
            String key = dateConvertor.dateToStringWithoutHour(date);

            if (shiftsByDate.containsKey(key)) {
                if (type.equals("Morning")) {
                    return shiftsByDate.get(key)[0];

                } else return shiftsByDate.get(key)[1];
            } else {
                Shift s = selectShift(date, type, branch, DATE_COLUMN_NAME, SHIFT_TYPE_COLUMN_NAME, BRANCH_COLUMN_NAME, SHIFT_TABLE_NAME);
                if (s == null) throw new Exception("Error:Shift does not exist.");
                Shift[] shifts = new Shift[2];
                if (type.equals("Morning")) shifts[0] = s;
                else shifts[1] = s;
                shiftsByDate.put(key, shifts);
                return s;
            }
        }
        else{
            Shift s = selectShift(date, type, branch, DATE_COLUMN_NAME, SHIFT_TYPE_COLUMN_NAME, BRANCH_COLUMN_NAME, SHIFT_TABLE_NAME);
            if (s == null) throw new Exception("Error:Shift does not exist.");
            Shift[] shifts = new Shift[2];
            if (type.equals("Morning")) shifts[0] = s;
            else shifts[1] = s;
            shiftsByDateAndBranch.put(branch,new ConcurrentHashMap<>());
            Map<String,Shift[]> shiftsByDate = shiftsByDateAndBranch.get(branch);

            DateConvertor dateConvertor = new DateConvertor();
            String key = dateConvertor.dateToStringWithoutHour(date);

            shiftsByDate.put(key, shifts);
            return s;
        }
    }
}
