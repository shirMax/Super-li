package Backend.BusinessLayer.Employees;



import java.util.*;

public class Shift {
    private String shiftType;
    private int dayInWeek;
    private Date date;
    private List<Employee> employees;
    private List<Integer> dEmployees;
    private String branch;

    public Shift(Date _date,String _type,String _branch){
        shiftType=_type;
        dayInWeek= _date.getDay();
        date=_date;
        employees=new ArrayList<Employee>();
        branch=_branch;

    }

    public boolean hasShiftManager(){
        for(Employee e : employees){
            if(e.hasPosition(Positions.shiftManager()))return true;
        }
        return false;
    }
    public void setDataEmployees(String dataEmployees){
        List<String> s = Arrays.asList(dataEmployees.split("-"));
        List<Integer> ids = new ArrayList<>();
            for (String str : s) {
                   if(!str.equals("")) ids.add(Integer.valueOf(str));
            }
        dEmployees=ids;
    }

    public boolean addEmployee(Employee e){
        return employees.add(e);
    }
    public boolean removeEmployee(Employee e){
        return employees.remove(e);
    }

    public Date getDate() {
        return date;
    }

    public int getDayInWeek() {
        return dayInWeek;
    }

    public List<Employee> getEmployees() {
        return employees;
    }


    public boolean hasEmployee(int id){
        for(Employee e : employees){
            if(e.getId()==id)return true;
        }
        return false;
    }
    public String getType() {
        return shiftType;
    }


    public List<Integer> getDataEmployees() {
        return dEmployees;
    }

    public void setEmployees(List<Employee> employees) {
        this.employees=employees;
    }

    public boolean hasPosition(String pos) {
        for(Employee e :employees){
            if(e.hasPosition(pos))return true;
        }
        return false;
    }

    public String getBranch() {
        return branch;
    }
}
