package Backend.BusinessLayer.Deliveries;

import Backend.BusinessLayer.Deliveries.Enums.DeliveryMode_Enum;
import Backend.BusinessLayer.Tools.DateConvertor;
import Backend.PersistenceLayer.DeliveriesDal.DeliveryDAO;
import Backend.PersistenceLayer.DeliveriesDal.ProductsSiteDocDAO;
import Backend.PersistenceLayer.DeliveryDTO;
//import jdk.internal.joptsimple.util.DateConverter;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Delivery {
    public int getOrderID() {
        return orderID;
    }

    private int id;
    private Date deliveryDate;
    private int driverID;
    private Truck truck;
    private List<SiteDocument> siteDocuments;
    private DeliveryMode_Enum deliveryMode;
    private DeliveryDAO deliveryDAO;
    private static int WITHOUT_ORDER = -1;
    private int orderID;

    public Delivery(int id, Date date, int driverID , Truck truck) {
        this.orderID = WITHOUT_ORDER;
        this.id = id;
        this.deliveryDate = date;
        this.driverID= driverID;
        this.truck = truck;
        siteDocuments = new ArrayList<>();
        deliveryMode=DeliveryMode_Enum.Invited;
    }

    public Delivery(DeliveryDTO delivery) throws Exception {
        this.id = delivery.getId();
        this.deliveryDate = delivery.getDeliveryDate();
        this.driverID =delivery.getId();
        this.truck = new Truck(delivery.getTruck());
        this.siteDocuments = new ArrayList<>();
/*        for(SiteDTO site: delivery.getSites()) {
            if(site.getSiteKind() == SiteKind_Enum.SOURCE)
                this.sites.add(new Source()
        }*/
        this.deliveryMode = delivery.getMode();
    }

//    public Delivery(int deliveryID, String departureDate, String deliveryMode, Truck truck, int driverID) throws Exception {
//        this.id = deliveryID;
//        DateConvertor dateConverter = new DateConvertor();
//        this.deliveryDate = dateConverter.validateDate(departureDate);
//        this.driverID = driverID;
//        this.truck = truck;
//        siteDocuments = new ArrayList<>();
//        this.deliveryMode=DeliveryMode_Enum.valueOf(deliveryMode);
//    }

    public Delivery(int deliveryID, String departureDate, String deliveryMode, Truck truck,
                    int driverID ,DeliveryDAO deliveryDAO, int orderID) throws Exception {
        this.orderID = orderID;
        this.id = deliveryID;
        DateConvertor dateConverter = new DateConvertor();
        this.deliveryDate = dateConverter.validateDate(departureDate);

        this.driverID = driverID;
        this.truck = truck;
        siteDocuments = new ArrayList<>();
        this.deliveryMode=DeliveryMode_Enum.valueOf(deliveryMode);
        this.deliveryDAO = deliveryDAO;
    }

    public Delivery(int deliveryID,String date,DeliveryDAO deliveryDAO, int orderID) throws Exception {
        this.orderID = orderID;
        this.id = deliveryID;
        DateConvertor dateConverter = new DateConvertor();
        this.deliveryDate = dateConverter.stringToDate(date);
        siteDocuments = new ArrayList<>();
        this.deliveryMode=DeliveryMode_Enum.Pending;
        this.deliveryDAO = deliveryDAO;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public Date getDeliveryDate() {
        return deliveryDate;
    }

    public void setDeliveryDate(Date deliveryDate) throws Exception {
        this.deliveryDate = deliveryDate;
        deliveryDAO.updateDate(id,deliveryDate);
    }

    public int getDriverID() {
        return driverID;
    }

//    public void setDriver(Driver driver) {
//        if(deliveryMode==DeliveryMode_Enum.ReDesigning) {
//            this.driver.setAvailable(false);
//            this.driver = driver;
//            this.driver.setAvailable(true);
//        }
//        this.driver = driver;
//    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) throws Exception {
        if(deliveryMode==DeliveryMode_Enum.ReDesigning) {
            this.truck.setAvailable(true);
            deliveryDAO.updateTruckAvaillabillity(this.truck.getLicenseNumber(),true);
            this.truck= truck;
            this.truck.setAvailable(false);
        }
        this.truck = truck;
        deliveryDAO.updateTruck(id,truck.getLicenseNumber());
        //deliveryDAO.updateTruckAvaillabillity(truck.getLicenseNumber(),false);
    }

    public void addSite(SiteDocument siteDocument) throws Exception {
        if(deliveryMode == DeliveryMode_Enum.Arrived ||
                deliveryMode == DeliveryMode_Enum.InTheWays)
            throw new Exception("the delivery is not in edit state!");
        siteDocuments.add(siteDocument);
    }

    public void addSiteDoc(SiteDocument siteDocument){
        siteDocuments.add(siteDocument);
    }


    public int getSiteAmount(){return siteDocuments.size();}

    public void setMode(DeliveryMode_Enum newMode) throws Exception {
        deliveryMode = newMode;
        deliveryDAO.update(id,newMode.toString());
    }

    public SiteDocument getSite(int siteId) {
        for(SiteDocument siteDocument : siteDocuments) {
            if (siteDocument.getSiteDocId() == siteId)
                return siteDocument;
        }
        return null;
    }

    public DeliveryMode_Enum getMode() {
        return deliveryMode;
    }

    public void removeSite(int siteID) throws Exception {
        if(deliveryMode == DeliveryMode_Enum.Arrived ||
                deliveryMode == DeliveryMode_Enum.InTheWays)
            throw new Exception("the delivery is not in edit state!");
        SiteDocument siteDocument = getSite(siteID);
        if(siteDocument == null)
            throw new Exception("there is no such site with this id");
        if(deliveryMode == DeliveryMode_Enum.Invited) {
            siteDocument.removeAll();
            siteDocuments.remove(siteDocument);
        }

/*        if(deliveryMode == DeliveryMode_Enum.Invited) {
            sites.remove(site);

        }*/
        if(deliveryMode == DeliveryMode_Enum.ReDesigning)
            for(SiteDocument s : siteDocument.getProductsByDest().keySet()){
                removeSite(siteID,s.getSiteDocId());
            }
        siteDocument.cancel();
    }

    public void removeSite(int sourceID,int destID) throws Exception {
        if(deliveryMode == DeliveryMode_Enum.Arrived ||
                deliveryMode == DeliveryMode_Enum.InTheWays)
            throw new Exception("the delivery is not in edit state!");
        SiteDocument source = getSite(sourceID);
        SiteDocument dest =  getSite(destID);

        if(source == null)
            throw new Exception("there is no such site with this id");

        if(dest == null)
            throw new Exception("there is no such site with this id");
/*        if(deliveryMode == DeliveryMode_Enum.Invited) {
            sites.remove(site);
        }*/

        source.removeSite(dest);
        dest.removeSite(source);

        if(deliveryMode == DeliveryMode_Enum.ReDesigning)
            source.cancelSourceDest(dest);
        /*source.cancel();*/
    }

    public void informSiteVisit(int siteID, int weightTaken) throws Exception {
        if(deliveryMode != DeliveryMode_Enum.InTheWays)
            throw new Exception("the delivery is not in edit state!");
        SiteDocument siteDocument = getSite(siteID);
        siteDocument.setTruckWeight(weightTaken);
        if(weightTaken >= truck.getMaxWeight()) {
            setMode(DeliveryMode_Enum.ReDesigning);
            siteDocument.addComment("entered the weight: " + weightTaken + " and its overweight");
            throw new Exception("the truck weight is bigger then her maximum weight!");
        }
        setMode(DeliveryMode_Enum.InTheWays);
        siteDocument.setVisited(true);
        //    site.checkShouldRemoved();
        if(isDeliveryEnded()) {
            finishDelivery();
        }
        //    moveToInTheWay();
    }

    private void finishDelivery() {
        deliveryMode = DeliveryMode_Enum.Arrived;
        truck.setAvailable(true);
    }

    private boolean isDeliveryEnded(){
        for(SiteDocument siteDocument : siteDocuments){
            if(!siteDocument.isVisited())
                return false;
        }
        return true;
    }

/*    public void addProductToSite(int siteID, int catNum, String productName, int quantity) throws Exception {
        Site site = getSiteIfCanBeEdited(siteID);
       // site.addProduct(catNum, productName, quantity, weight);
        moveToInTheWay();
    }*/

    public void addProductToSite(ProductsSiteDocDAO productsSiteDocDAO, int sourceID, int destinationID,
                                 int catNum, String productName, int quantity) throws Exception {
        if(destinationID<sourceID){
            throw new Exception("destination site entered before source site");
        }
        SiteDocument source = getSiteIfCanBeEdited(sourceID);
        SiteDocument destination = getSiteIfCanBeEdited(destinationID);
        source.addProduct(productsSiteDocDAO, catNum, productName, quantity,destination);
        destination.addProduct(productsSiteDocDAO, catNum,productName,quantity,source);
        moveToInTheWay();

    }

    private SiteDocument getSiteIfCanBeEdited(int siteID) throws Exception {
        if(deliveryMode == DeliveryMode_Enum.Arrived ||
                deliveryMode == DeliveryMode_Enum.InTheWays)
            throw new Exception("the delivery is not in edit state!");
        SiteDocument siteDocument = getSite(siteID);
        if(siteDocument == null)
            throw new Exception("there is no site with this id!");
        return siteDocument;
    }

    private void moveToInTheWay() throws Exception {
        if(deliveryMode == DeliveryMode_Enum.ReDesigning)
            setMode(DeliveryMode_Enum.InTheWays);
    }

    public void updateProductToSite(int siteID,int destID, int catNum, int quantity) throws Exception {
        SiteDocument source = getSiteIfCanBeEdited(siteID);
        SiteDocument dest = getSiteIfCanBeEdited(destID);

        source.updateProductQuantity(catNum, quantity,dest);
        dest.updateProductQuantity(catNum,quantity,source);
        if(deliveryMode == DeliveryMode_Enum.ReDesigning){
            source.addComment("the product: " + catNum + " has been changed to " + quantity);
            dest.addComment("the product: " + catNum + " has been changed to " + quantity);
        }
        moveToInTheWay();
    }

    public void removeProductToSite(int siteID, int destID, int catNum) throws Exception {
        SiteDocument siteDocument = getSiteIfCanBeEdited(siteID);
        SiteDocument dest = getSiteIfCanBeEdited(destID);
        siteDocument.removeProduct(catNum,dest);
        if(deliveryMode == DeliveryMode_Enum.ReDesigning){
            siteDocument.addComment("the product: " + catNum + " has been removed from the site");
            //dest.addComment("the product: " + catNum + " has been removed from this site!");
        }
        //moveToInTheWay();
    }

    public List<SiteDocument> getAllSite() {
        return siteDocuments;
    }

    public void setDriver(int driverID) {
        this.driverID=driverID;
        deliveryDAO.updateDriver(id,driverID);
    }
}
