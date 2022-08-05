package Backend.PersistenceLayer;

import Backend.BusinessLayer.Deliveries.Enums.DeliveryMode_Enum;

import java.util.Date;
import java.util.List;

public class DeliveryDTO {
    private int id;
    private Date deliveryDate;
    private DriverDTO driver;
    private TruckDTO truck;
    private List<SiteDocDTO> sites;
    private DeliveryMode_Enum deliveryMode;


    public DeliveryDTO(int id, Date deliveryDate, DriverDTO driver,
                       TruckDTO truck, List<SiteDocDTO> sites, DeliveryMode_Enum deliveryMode) {
        this.id = id;
        this.deliveryDate = deliveryDate;
        this.driver = driver;
        this.truck = truck;
        this.sites = sites;
        this.deliveryMode = deliveryMode;
    }

/*    public DeliveryDTO(Delivery delivery) {
        this.id = delivery.getId();
        this.deliveryDate = delivery.getDeliveryDate();
        this.driver = new DriverDTO(delivery.getDriver());
        this.truck = new TruckDTO(delivery.getTruck());
        this.sites = new ArrayList<>();
        for(Site site: delivery.getAllSite())
            this.sites.add(new SiteDTO(site));
        this.deliveryMode = delivery.getMode();
    }*/

    public int getId() {
        return id;
    }

    public String toString(){
        return "delivery id: " + id +
                " date: " + deliveryDate.toString() +
                " truck: " + truck.toString() +
                " driver: " + driver.toString() +
                " sites: " + sites.toString() + "\n";
    }

    public List<SiteDocDTO> getSites() {
        return sites;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public DriverDTO getDriver() {
        return driver;
    }

    public TruckDTO getTruck() {
        return truck;
    }

    public DeliveryMode_Enum getDeliveryMode() {
        return deliveryMode;
    }

    public DeliveryMode_Enum getMode() {
        return deliveryMode;
    }
}
