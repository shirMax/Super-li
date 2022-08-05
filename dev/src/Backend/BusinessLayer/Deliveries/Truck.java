package Backend.BusinessLayer.Deliveries;

import Backend.BusinessLayer.Deliveries.Enums.License_Enum;
import Backend.PersistenceLayer.TruckDTO;

public class Truck {
    private int licenseNumber;
    private License_Enum license;
    private int weight;
    private int maxWeight;
    private boolean available;

    public Truck(int licenseNumber, String license, int weight, int maxWeight) throws Exception {
        this.licenseNumber = licenseNumber;
        try {
            this.license = License_Enum.valueOf(license);
        }
        catch (Exception e){
            throw new Exception("Wrong license entered!");
        }
        this.weight = weight;
        this.maxWeight = maxWeight;
        available = true;
    }

    public Truck(int licenseNumber, String license, int weight, int maxWeight,boolean available) throws Exception {
        this.licenseNumber = licenseNumber;
        try {
            this.license = License_Enum.valueOf(license);
        }
        catch (Exception e){
            throw new Exception("Wrong license entered!");
        }
        this.weight = weight;
        this.maxWeight = maxWeight;
        this.available = available;
    }

    public Truck(TruckDTO truck) throws Exception {
        this(truck.getLicenseNumber(), truck.getLicense().toString(), truck.getWeight(), truck.getMaxWeight());
    }

    public int getLicenseNumber() {
        return licenseNumber;
    }

    public void setLicenseNumber(int licenseNumber) {
        this.licenseNumber = licenseNumber;
    }

    public License_Enum getLicense() {
        return license;
    }

    public void setLicense(License_Enum license) {
        this.license = license;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public void setMaxWeight(int maxWeight) {
        this.maxWeight = maxWeight;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}
