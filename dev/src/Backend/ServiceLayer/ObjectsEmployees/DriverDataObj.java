package Backend.ServiceLayer.ObjectsEmployees;

import Backend.BusinessLayer.Deliveries.Driver;
import Backend.BusinessLayer.Deliveries.Enums.License_Enum;
import Backend.BusinessLayer.Employees.Employee;

public class DriverDataObj {
    private String name;
    private License_Enum license;
    private int id;

    public DriverDataObj(String name, License_Enum license, int id) {
        this.name = name;
        this.license = license;
        this.id = id;
    }

    public DriverDataObj(Driver driver) {
        this(driver.getName(), driver.getLicense(), driver.getId());
    }
    public DriverDataObj(Employee employee){
        this(employee.getName(), License_Enum.valueOf(employee.getLicense()), employee.getId());
    }
    public DriverDataObj(int driverID) {
        driverID=driverID;
    }

    public String getName() {
        return name;
    }

    public License_Enum getLicense() {
        return license;
    }

    public int getId() {
        return id;
    }

    public String toString(){
        return "driver name: " + name + ", license kind: " + license.toString() +
                ", id: " + id + "\n";
    }
}
