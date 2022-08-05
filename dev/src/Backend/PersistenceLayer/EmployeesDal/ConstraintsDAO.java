package Backend.PersistenceLayer.EmployeesDal;

import Backend.BusinessLayer.Employees.Employee;
import Backend.PersistenceLayer.DalController;

import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ConstraintsDAO extends DalController {
    private List<Integer>[] constraints;
    private boolean[] loaded;
    final String CONSTRAINTS_TABLE_NAME = "Constraints";
    final String SHIFT_ID_COLUMN_NAME = "ShiftID";
    final String EMPLOYEE_ID_COLUMN_NAME = "EmployeeID";
    public ConstraintsDAO(){
        constraints = new List[14];
        for(List l : constraints) l = new ArrayList<Integer>();
        loaded = new boolean[14];
        for(boolean b : loaded) b=false;
    }
    public List<Integer> getEmployeeIDsForShift(int shift) throws Exception {
        if(!loaded[shift])loadShiftConstraints(shift);
        return constraints[shift];
    }

    private void loadShiftConstraints(int shift) throws Exception {
        String sql = "SELECT "+EMPLOYEE_ID_COLUMN_NAME+" FROM " +CONSTRAINTS_TABLE_NAME+" WHERE "+SHIFT_ID_COLUMN_NAME+"="+shift;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            List<Integer> ids = new ArrayList<>();
            while(rs.next())
                ids.add(rs.getInt(1));
            constraints[shift]=ids;
            loaded[shift]=true;
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }
    }
    public void addConstraintToArr(int shift,int employeeID) throws Exception {
        try {
            if(!loaded[shift])loadShiftConstraints(shift);
            constraints[shift].add(employeeID);
        }catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }
    public void removeConstraintFromArr(int shift,int employeeID) throws Exception {
        try {
            if(!loaded[shift])loadShiftConstraints(shift);
            int index = constraints[shift].indexOf(employeeID);
            if(index!=-1)constraints[shift].remove(index);

        }catch (Exception e){
        throw new Exception(e.getMessage());
    }
    }
    public void addConstraint(int shift,int employeeID) throws Exception {
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}) VALUES(?,?)",
                CONSTRAINTS_TABLE_NAME,SHIFT_ID_COLUMN_NAME,EMPLOYEE_ID_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,shift);
            pstmt.setInt(2,employeeID);
            pstmt.executeUpdate();
            addConstraintToArr(shift, employeeID);
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }
    }
    public void removeConstraint(int shift,int employeeID) throws Exception {
        String sql = MessageFormat.format("DELETE FROM {0} WHERE {1}=? AND {2}=?",CONSTRAINTS_TABLE_NAME,SHIFT_ID_COLUMN_NAME,EMPLOYEE_ID_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,shift);
            pstmt.setInt(2,employeeID);
            pstmt.executeUpdate();
            removeConstraintFromArr(shift, employeeID);
        } catch (SQLException e) {
            throw new Exception(e.getMessage());
        }
    }


}
