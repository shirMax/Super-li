package Backend.PersistenceLayer;

import Backend.BusinessLayer.Deliveries.Enums.License_Enum;

public class DriverDTO {
    private String name;
    private License_Enum license;
    private int id;

    public DriverDTO(String name, License_Enum license, int id) {
        this.name = name;
        this.license = license;
        this.id = id;
    }

/*
    public DriverDTO(Driver driver) {
        this(driver.getName(), driver.getLicense(), driver.getId());
    }
*/

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
