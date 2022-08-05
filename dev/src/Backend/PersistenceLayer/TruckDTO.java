package Backend.PersistenceLayer;

import Backend.BusinessLayer.Deliveries.Enums.License_Enum;

public class TruckDTO {
    private int licenseNumber;
    private License_Enum license;
    private int weight;
    private int maxWeight;

    public TruckDTO(int licenseNumber, License_Enum license, int weight, int maxWeight) {
        this.licenseNumber = licenseNumber;
        this.license = license;
        this.weight = weight;
        this.maxWeight = maxWeight;
    }

/*    public TruckDTO(Truck truck){
        this(truck.getLicenseNumber(), truck.getLicense(), truck.getWeight(), truck.getMaxWeight());
    }*/

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
}
