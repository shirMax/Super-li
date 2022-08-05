package Backend.ServiceLayer.ObjectsDeliveries;

import Backend.BusinessLayer.Deliveries.Enums.License_Enum;
import Backend.BusinessLayer.Deliveries.Truck;

public class TruckDataObj {
    private int licenseNumber;
    private License_Enum license;
    private int weight;
    private int maxWeight;

    public TruckDataObj(int licenseNumber, License_Enum license, int weight, int maxWeight) {
        this.licenseNumber = licenseNumber;
        this.license = license;
        this.weight = weight;
        this.maxWeight = maxWeight;
    }

    public TruckDataObj(Truck truck){
        this(truck.getLicenseNumber(), truck.getLicense(), truck.getWeight(), truck.getMaxWeight());
    }

    public int getLicenseNumber() {
        return licenseNumber;
    }

    public License_Enum getLicense() {
        return license;
    }

    public int getWeight() {
        return weight;
    }

    public int getMaxWeight() {
        return maxWeight;
    }

    public String toString(){
        return "license number: " + licenseNumber + ", license kind: " + license.toString() +
                ", weight: " + weight + ", " + ", maximum weight: " + maxWeight + "\n";
    }

    public String toFormal(){
        return "license number: " + licenseNumber;
    }
}
