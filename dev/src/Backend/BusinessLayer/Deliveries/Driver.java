package Backend.BusinessLayer.Deliveries;

import Backend.BusinessLayer.Deliveries.Enums.License_Enum;
import Backend.PersistenceLayer.DriverDTO;

public class Driver {
    private String name;
    private License_Enum license;
    private int id;
    private boolean available;

    public Driver(String name, String license, int id) throws Exception {
        this.name = name;
        try {
            this.license = License_Enum.valueOf(license);
        }
        catch (Exception e){
            throw new Exception("Wrong license entered!");
        }
        this.id = id;
        available = true;
    }

    public Driver(DriverDTO driver) throws Exception {
        this(driver.getName(), driver.getLicense().toString(), driver.getId());
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public License_Enum getLicense() {
        return license;
    }

    public void setLicense(License_Enum license) {
        this.license = license;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
