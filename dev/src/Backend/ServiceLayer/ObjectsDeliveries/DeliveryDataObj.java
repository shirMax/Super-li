package Backend.ServiceLayer.ObjectsDeliveries;

import Backend.BusinessLayer.Deliveries.Delivery;
import Backend.BusinessLayer.Deliveries.Enums.DeliveryMode_Enum;
import Backend.BusinessLayer.Deliveries.SiteDocument;
import Backend.ServiceLayer.ObjectsEmployees.DriverDataObj;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

public class DeliveryDataObj implements Formalable{
    private int id;
    private Date deliveryDate;
    private int driverID;
    private int orderID;
    private TruckDataObj truck;
    private List<SiteDocDataObj> sites;

    private DeliveryMode_Enum deliveryMode;

    public DeliveryDataObj(int id, Date deliveryDate, int driverID, TruckDataObj truck,
                           List<SiteDocDataObj> sites, DeliveryMode_Enum deliveryMode, int orderID) {
        this.id = id;
        this.deliveryDate = deliveryDate;
        this.driverID = driverID;
        this.truck = truck;
        this.sites = sites;
        this.deliveryMode = deliveryMode;
        this.orderID = orderID;
    }
    public DeliveryDataObj(int id, Date deliveryDate, int orderID) {
        this.id = id;
        this.deliveryDate = deliveryDate;
        this.driverID = -1;
        this.truck = null;
        this.sites = null;
        this.deliveryMode = DeliveryMode_Enum.Pending;
        this.orderID = orderID;
    }

    public DeliveryDataObj(Delivery delivery) {
        this.id = delivery.getId();
        this.deliveryDate = delivery.getDeliveryDate();
        this.sites = new ArrayList<>();
        this.orderID = delivery.getOrderID();
        for (SiteDocument siteDocument : delivery.getAllSite())
            this.sites.add(new SiteDocDataObj(siteDocument));
        this.deliveryMode = DeliveryMode_Enum.Pending;

        if (delivery.getMode() != DeliveryMode_Enum.Pending) {
            this.deliveryMode = delivery.getMode();
            this.driverID = delivery.getDriverID();
            this.truck = new TruckDataObj(delivery.getTruck());
        }
    }

    public int getId() {
        return id;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public String toString() {
        if(deliveryMode == DeliveryMode_Enum.Pending)
            return toFormal();
        String s = "delivery id: " + id +
                " date: " + deliveryDate.toString() +
                " truck: " + truck.toFormal() +
                " driver: " + driverID + " \n";
        if (sites.size() != 0)
            s += "sites: \n"
                    + "[ \n" + sites
                    .stream()
                    .reduce("", (acc, curr) -> acc + "    " + curr.toFormal(), String::join)
                    + "] \n";
        return s;
    }

    public String toFormal() {
        String s = "delivery id: " + id +
                " date: " + deliveryDate.toString() +
                " order: " + orderID;
        return s;
    }

    public List<SiteDocDataObj> getSites() {
        return sites;
    }

    public List<SiteDocDataObj> getUnvisitedSites() {
        List<SiteDocDataObj> list = new ArrayList<>();
        for(SiteDocDataObj siteDocument : sites)
            if(!siteDocument.isVisited()) {
                list.add(siteDocument);
                return list;
            }
        return list;
    }
}
