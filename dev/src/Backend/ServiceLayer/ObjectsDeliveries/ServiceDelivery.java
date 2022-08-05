package Backend.ServiceLayer.ObjectsDeliveries;

import Backend.BusinessLayer.Deliveries.*;
import Backend.BusinessLayer.Deliveries.Enums.DeliveryMode_Enum;
import Backend.BusinessLayer.Deliveries.Enums.License_Enum;
import Backend.BusinessLayer.Tools.Pair;
import Backend.ServiceLayer.ObjectsEmployees.DriverDataObj;
import Backend.ServiceLayer.Response;
import Backend.ServiceLayer.ResponseT;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ServiceDelivery {
    private DeliveryController deliveryController;

    public ServiceDelivery() {
        this.deliveryController = new DeliveryController();
    }

    public Response updateProductQuantity(int deliveryID , int siteID , int destID, int catNum, int quantity){
        try {
            deliveryController.updateProductInSite(deliveryID, siteID,destID, catNum, quantity);
            return new Response();
        }
        catch (Exception e){
            return new Response(e.getMessage());
        }
    }

    public Response launchDelivery(int deliveryID){
        try {
            deliveryController.launchDelivery(deliveryID);
            return new Response();
        }
        catch (Exception e){
            return new Response(e.getMessage());
        }
    }

    public Response addTruck(int licenseNumber , String licenseKind , int weight , int maxWeightAllowed){
        try {
            deliveryController.addTruck(licenseNumber, licenseKind, weight, maxWeightAllowed);
            return new Response();
        }
        catch (Exception e){
            return new Response(e.getMessage());
        }
    }

    public Response informSiteVisit(int deliveryID , int siteID , int weightTaken){
        try {
            deliveryController.informSiteVisit(deliveryID, siteID, weightTaken);
            return new Response();
        }
        catch (Exception e){
            return new Response(e.getMessage());
        }
    }

    public Response changeTruckDriver(int deliveryID , int siteID , int truckNumber , int driverID){
        try {
            deliveryController.changeTruckDriver(deliveryID,siteID,truckNumber,driverID);
            return new Response();
        }
        catch (Exception e){
            return new Response(e.getMessage());
        }
    }

    public ResponseT<List<TruckDataObj>> getAllTrucks() {
        try {
            Collection<Truck> collection = deliveryController.getAllTrucks();
            List<TruckDataObj> list = new ArrayList<>();
            for(Truck truck : collection) {
                list.add(new TruckDataObj(truck));
            }
            return new ResponseT<List<TruckDataObj>>(list);
        }
        catch (Exception e){
            return new ResponseT<List<TruckDataObj>>(e.getMessage(),true);
        }
    }

    public ResponseT<List<DeliveryDataObj>> getAllDeliveires(int kind) {
        try {
            Collection<Delivery> collection = deliveryController.getAllDeliveries(kind);
            List<DeliveryDataObj> list = new ArrayList<>();
            for(Delivery delivery : collection) {
                list.add(new DeliveryDataObj(delivery));
            }
            return new ResponseT<List<DeliveryDataObj>>(list);
        }
        catch (Exception e){
            return new ResponseT<List<DeliveryDataObj>>(e.getMessage(),true);
        }
    }

    public Response changeDate(int deliveryId, String newDate) {
        try {
            deliveryController.changeDate(deliveryId, newDate);
            return new Response();
        }
        catch (Exception e){
            return new Response(e.getMessage());
        }
    }

    public Response loadData() {
        try {
            deliveryController.loadData();
            return new Response();
        }
        catch (Exception e){
            return new Response(e.getMessage());
        }
    }

    public DeliveryController getDeliveryController() {
        return deliveryController;
    }

    public Response initial() {
        try {
            deliveryController.initial();
            return new Response();
        }
        catch (Exception e){
            return new Response(e.getMessage());
        }
    }

    public ResponseT<Integer> bookDelivery(String date, int driverID, int licenseNumber, int deliveryID) {
        try {
            int id = deliveryController.bookDelivery(date, driverID, licenseNumber, deliveryID);
            return new ResponseT<Integer>(id);
        }
        catch (Exception e){
            return new ResponseT<Integer>(e.getMessage(),true);
        }    }

    public Response leaveHRMessage(String date, License_Enum license) {
        try {
            deliveryController.leaveHRMessage(date, license);
            return new Response();
        }
        catch (Exception e){
            return new Response(e.getMessage());
        }
    }

    public Response removeDelivery(int id) {
        try {
            deliveryController.removeDelivery(id);
            return new Response();
        }
        catch (Exception e){
            return new Response(e.getMessage());
        }
    }
/*

    public Response addSite(String name, String address, String area, String contactName, String contactPhone) {
        try {
            deliveryController.addSite(name, address, area, contactName, contactPhone);
            return new Response();
        }
        catch (Exception e){
            return new Response(e.getMessage());
        }
    }

    public ResponseT<List<SiteDataObj>> getAllSites() {
        try {
            Collection<Site> collection = deliveryController.getAllSites();
            List<SiteDataObj> list = new ArrayList<>();
            for(Site site : collection) {
                list.add(new SiteDataObj((site)));
            }
            return new ResponseT<List<SiteDataObj>>(list);
        }
        catch (Exception e){
            return new ResponseT<List<SiteDataObj>>(e.getMessage(),true);
        }
    }

    public ResponseT<Integer> inviteDelivery(String date, int driverID , int truckNumber){
        try {
            int id = deliveryController.inviteDelivery(date, driverID, truckNumber);
            return new ResponseT<Integer>(id);
        }
        catch (Exception e){
            return new ResponseT<Integer>(e.getMessage(),true);
        }    }

    public ResponseT<Integer> addSiteDoc(int deliveryID, String name ,String siteKind){
        try {
            int siteID = deliveryController.addSiteToDelivery(deliveryID, name, siteKind);
            return new ResponseT<>(siteID);
        }
        catch (Exception e){
            return new ResponseT<>(e.getMessage(),true);
        }
    }

    public Response addProductToSite(int deliveryID , int siteID , int destID , int catNum, String productName , int quantity){
        try {
            deliveryController.addProductToSite(deliveryID, siteID, destID, catNum, productName, quantity);
            return new Response();
        }
        catch (Exception e){
            return new Response(e.getMessage());
        }
    }

    public Response removeProductQuantity(int deliveryID , int siteID , int destID , int catNum){
        try {
            deliveryController.removeProductFromSite(deliveryID, siteID,destID, catNum);
            return new Response();
        }
        catch (Exception e){
            return new Response(e.getMessage());
        }
    }

    public Response provideSiteDocument(int deliveryID , int siteID){
        throw new UnsupportedOperationException();
    }

    public Response addDriver(String driverName , String license , int id){
        try {
            deliveryController.addDriver(driverName, license, id);
            return new Response();
        }
        catch (Exception e){
            return new Response(e.getMessage());
        }
    }

    public Response changeTruck(int deliveryID , int siteID , int truckNumber){
        throw new UnsupportedOperationException();
    }

    public Response cancelSiteVisit(int deliveryID , int siteID){
        throw new UnsupportedOperationException();
    }

    public ResponseT<List<DriverDataObj>> getAllDriversForTruck(License_Enum license) {
        try {
            Collection<Driver> collection = deliveryController.getAllDriversForTruck(license);
            List<DriverDataObj> list = new ArrayList<>();
            for(Driver driver : collection) {
                if (driver.getLicense().canIDriveIt(license))
                    list.add(new DriverDataObj(driver));
            }

            return new ResponseT<List<DriverDataObj>>(list);
        }
        catch (Exception e){
            return new ResponseT<List<DriverDataObj>>(e.getMessage(),true);
        }
    }

    public ResponseT<SiteDocDataObj> getSiteDoc(String siteID) {
        try {
            SiteDocument siteDocument = deliveryController.getSiteDoc(siteID);
            SiteDocDataObj siteDocDataObj = new SiteDocDataObj(siteDocument);
            return new ResponseT<SiteDocDataObj>(siteDocDataObj);
        }
        catch (Exception e){
            return new ResponseT<SiteDocDataObj>(e.getMessage(),true);
        }
    }

    public ResponseT<DeliveryDataObj> getDelivery(int deliveryID) {
        try {
            Delivery delivery = deliveryController.getDelivery(deliveryID);
            DeliveryDataObj deliveryDataObj = new DeliveryDataObj(delivery);
            return new ResponseT<DeliveryDataObj>(deliveryDataObj);
        }
        catch (Exception e){
            return new ResponseT<DeliveryDataObj>(e.getMessage(),true);
        }
    }

    public ResponseT<List<DeliveryDataObj>> getAllPendingDeliveires(int kind) {
        try {
            Collection<Delivery> collection = deliveryController.getAllDeliveries(kind);
            List<DeliveryDataObj> list = new ArrayList<>();
            for(Delivery delivery : collection) {
                list.add(new DeliveryDataObj(delivery));
            }
            return new ResponseT<List<DeliveryDataObj>>(list);
        }
        catch (Exception e){
            return new ResponseT<List<DeliveryDataObj>>(e.getMessage(),true);
        }
    }

    public Response removeSite(int deliveryID, int sourceID, int destID) {
        try {
            deliveryController.removeSiteFromDelivery(deliveryID, sourceID, destID);
            return new Response();
        } catch (Exception e) {
            return new Response(e.getMessage());
        }
    }
*/

}
