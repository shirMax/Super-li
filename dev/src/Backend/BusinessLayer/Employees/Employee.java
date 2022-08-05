package Backend.BusinessLayer.Employees;



import Backend.BusinessLayer.Deliveries.Enums.License_Enum;

import java.util.ArrayList;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Employee {
    private String name;
    private final int id;
    private int salary;
    private int bankAccount;
    private List<String> positions;
    private Date startDate;
    private String license;
    private String phoneNumber;
    private boolean isAvailable;

    public Employee(String _name, int _id,int _salary, int _bankAcc, Date _startDate,String _phone) throws Exception {
        if(_salary<0)throw new Exception("Salary can't be negative");
        this.name=_name;
        this.id=_id;
        this.positions = new ArrayList<>();
        this.salary=_salary;
        this.bankAccount=_bankAcc;
        this.startDate=_startDate;
        this.license="none";
        this.phoneNumber=_phone;
        this.isAvailable=true;
    }



    public boolean isAvailable() {
        return isAvailable;
    }


    public void setAvailable(boolean available) {
        isAvailable = available;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public void setPositions(String pos) throws Exception {
        positions = unparsePositions(pos);
    }

    public boolean  addPosition(String _pos){
        return positions.add(_pos);
    }
    public List<String> getPositions(){return positions;}

    public boolean removePosition(String _pos){
        return positions.remove(_pos);
    }
    public String getName() {
        return name;
    }

    public Date getStartDate() {
        return startDate;
    }

    public int getBankAccount() {
        return bankAccount;
    }

    public int getId() {
        return id;
    }

    public int getSalary() {
        return salary;
    }

    public void setBankAccount(int bankAccount) {
        this.bankAccount = bankAccount;
    }



    public void setName(String name) {
        this.name = name;
    }

    public void setSalary(int _salary) throws Exception {
        if(_salary<0)throw new Exception("Salary can't be negative");
        this.salary = _salary;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public boolean hasPosition(String _pos){
        return positions.contains(_pos);
    }
    private List<String> unparsePositions(String positions) throws Exception {
        List<String> s = Arrays.asList(positions.split("-"));
        if(s.get(0).equals(""))return new ArrayList<String>();
        for(String pos : s){
            if(!Positions.getPositions().contains(pos))throw new Exception("Ilegal Position");
        }
        return s;
    }


    public String getLicense(){
        return license;
    }
    @Override
    public String toString() {
        return "Employee{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", salary=" + salary +
                ", bankAccount=" + bankAccount +
                ", positions=" + positions +
                ", startDate=" + startDate +
                '}';
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }
}
