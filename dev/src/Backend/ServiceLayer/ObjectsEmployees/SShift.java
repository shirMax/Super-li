package Backend.ServiceLayer.ObjectsEmployees;

import Backend.BusinessLayer.Employees.Employee;
import Backend.BusinessLayer.Employees.Shift;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SShift {
    private String shift_type;
    private int dayInWeek;
    private Date date;
    private List<SEmployee> employeeList;
    private Shift shift;
    private String description;
    public SShift(Shift shift){
        this.shift=shift;
        this.dayInWeek= shift.getDayInWeek();
        this.shift_type= shift.getType();
        date = shift.getDate();
        employeeList = new ArrayList<SEmployee>();
        for(Employee e : shift.getEmployees())
            employeeList.add(new SEmployee(e));
        description= "Branch: "+shift.getBranch()+" , Shift date:"+date+" day "+dayInWeek+" "+shift_type+" :\n    employees:";
        for(SEmployee e : employeeList){
            description+="      ";
            description+= e.description();
            description+= "\n";
        }
    }

    public Shift getShift(){
        return shift;
    }
    @Override
    public String toString() {
        return description;
    }

    public List<SEmployee> getEmployeeList() {
        return employeeList;
    }
    public boolean isEqual(SShift sShift){
        return this.shift_type.equals(sShift.shift_type)&
                this.dayInWeek== sShift.dayInWeek&
                this.date.equals(sShift.date);
    }
}
