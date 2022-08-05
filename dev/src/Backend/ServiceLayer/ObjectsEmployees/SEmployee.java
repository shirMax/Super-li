package Backend.ServiceLayer.ObjectsEmployees;

import Backend.BusinessLayer.Employees.Employee;


import java.util.Date;
import java.util.List;

public class SEmployee {
    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public int getSalary() {
        return salary;
    }

    public int getBankAccount() {
        return bankAccount;
    }

    public List<String> getPositions() {
        return positions;
    }

    public Date getStartDate() {
        return startDate;
    }

    public Employee getEmployee(){return employee;}

    private String name;

    private int id;
    private int salary;
    private int bankAccount;
    private List<String> positions;
    private Date startDate;
    private Employee employee;
    public SEmployee(Employee e){
        employee=e;
        name = e.getName();
        id=e.getId();
        salary=e.getSalary();
        bankAccount=e.getBankAccount();
        startDate=e.getStartDate();
        positions=e.getPositions();

    }
    public String description(){
        return name+", "+"ID:"+id+","+positions;
    }

    @Override
    public String toString() {
        return
                "Name:'" + name + '\'' +
                ", ID:" + id +
                ",Positions:"+ positions+
                ", Salary:" + salary +
                ", Bank Account:" + bankAccount +
                ", Start Date:" + startDate+
                ", Phone Number:"+ employee.getPhoneNumber();
    }

    /**
     *
     * @param e first employee
     * @return if e1 is equal to e2
     */
    public boolean isEqual(SEmployee e){
        return e.getId()==this.getId()&
                e.getName().equals(this.getName())&
                e.getBankAccount()==this.getBankAccount()&
                e.getSalary()==this.getSalary();
    }


}
