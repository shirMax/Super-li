package Backend.ServiceLayer;

import Backend.BusinessLayer.Deliveries.Enums.License_Enum;
import Backend.BusinessLayer.DeliveriesHandler;
import Backend.BusinessLayer.DeliveriesOrdersHandler;
import Backend.BusinessLayer.OrderHandler;
import Backend.ServiceLayer.ObjectsDeliveries.*;
import Backend.ServiceLayer.ObjectsEmployees.*;
import Frontend.PresentationLayer.Model.Controller;

import java.util.Collection;
import java.util.Date;
import java.util.List;

public class Service extends SuppliersAndStockService {
    private ServiceDelivery serviceDelivery;
    private ServiceEmployees serviceEmployees;
    private DeliveriesHandler deliveriesHandler;
    protected OrderHandler orderHandler;



    public Service() {
        serviceDelivery = new ServiceDelivery();
        serviceEmployees = new ServiceEmployees();
        deliveriesHandler = new DeliveriesHandler(serviceDelivery.getDeliveryController() ,
                serviceEmployees.getShiftController(), serviceEmployees.getEmployeeController());
        DeliveriesOrdersHandler deliveriesOrdersHandler = new DeliveriesOrdersHandler(
                serviceDelivery.getDeliveryController(), supplierService.getSupplierController());
        orderHandler = new OrderHandler(this.sms,supplierService.getSupplierController(),this.serviceDelivery.getDeliveryController());
    }

    public Response initial(){
        Result res = serviceEmployees.initial();
        if(res.isError())return new Response(res.getMessage());
        return serviceDelivery.initial();
    }

//    public ResponseT<Collection<DriverDataObj>> getAvailableDrivers (String shiftDate){
//        Result<Collection<DriverDataObj>> res = serviceEmployees.getAvailableDrivers(shiftDate);
//        if(res.isError())return new ResponseT<Collection<DriverDataObj>>(res.getMessage());
//        return new ResponseT<Collection<DriverDataObj>>(res.getValue());
//    }

//    public Result<Collection<DriverDataObj>> getAvailableDrivers(String shiftDate) {
//        return serviceEmployees.getAvailableDrivers(shiftDate);
//    }

    public ResponseT<List<DriverDataObj>> getAvailableDriverByLicense (String shiftDate , License_Enum license){
        Result<List<DriverDataObj>> res = serviceEmployees.getAvailableDriversByLicense(shiftDate,license);
        if(res.isError())return new ResponseT<List<DriverDataObj>>(res.getMessage(),true);
        return new ResponseT<List<DriverDataObj>>(res.getValue());
    }

    public ResponseT<List<DeliveryDataObj>> getAllDeliveries(int kind) {
        return serviceDelivery.getAllDeliveires(kind);
    }

    public Response updateProductQuantity(int deliveryID, int sourceID, int destinationID, int catNum, int quantity){
        return serviceDelivery.updateProductQuantity(deliveryID,sourceID,destinationID,catNum,quantity);
    }

    public Response launchDelivery(int deliveryID) {
        return serviceDelivery.launchDelivery(deliveryID);
    }

    public Response addTruck(int licenseNumber, String licenseKind, int weight, int maxWeightAllowed) {
        return serviceDelivery.addTruck(licenseNumber,licenseKind,weight,maxWeightAllowed);
    }

    public Response informSiteVisit(int deliveryID, int siteID, int weightTaken) {
        return serviceDelivery.informSiteVisit(deliveryID, siteID, weightTaken);
    }

    public Response changeTruckDriver(int deliveryID, int siteID, int truckNumber, int driverID) {
       return serviceDelivery.changeTruckDriver(deliveryID,siteID,truckNumber,driverID);
    }


    public ResponseT<List<TruckDataObj>> getAllTrucks(){
        return serviceDelivery.getAllTrucks();
    }


    public Response changeDate(int deliveryId, String newDate) {
        return serviceDelivery.changeDate(deliveryId, newDate);
    }

    public Response loadData() {
        serviceEmployees.init();
        return serviceDelivery.loadData();
    }



    public Result<Boolean> addConstraint(int _day,String _shift,int _id){
        return serviceEmployees.addConstraint(_day, _shift, _id);

    }
    public Result<List<SEmployee>> getEmployeesForShift(int _day, String _shift) {
        return serviceEmployees.getEmployeesForShift(_day, _shift);
    }

    public Result<List<SEmployee>> getAllEmployees(){
        return serviceEmployees.getAllEmployees();
    }

    public Result<Boolean> removeConstraint(int _day,String _shift,int _id) {
        return serviceEmployees.removeConstraint(_day, _shift, _id);
    }
    public Result<List<SShift>> getAllShifts(){
        return serviceEmployees.getAllShifts();
    }

    public Result checkExists(int id) {
        return serviceEmployees.checkExists(id);
    }

    public Result<SEmployee> removeEmployee(int id) {
        return serviceEmployees.removeEmployee(id);
    }

    public Result<SEmployee> addEmployee(String _name, int _id, int _salary, int _bankAcc, Date _startDate, String _phone) {
        return serviceEmployees.addEmployee(_name, _id, _salary, _bankAcc, _startDate, _phone);
    }
    public Result<Collection<String>> getAllHRNotifications(){
        return serviceEmployees.getAllHRNotifications();
    }


    public Result checkNotExists(int id) {
        return serviceEmployees.checkNotExists(id);
    }

    public Result<SEmployee> getEmployee(int id) {
        return serviceEmployees.getEmployee(id);
    }


    public Result<Boolean> addEmployeeToShift( int toAdd,Date _date,String _shiftType,String branch) {
        return serviceEmployees.addEmployeeToShift(toAdd, _date, _shiftType,branch);
    }

    public Result<Boolean> removeEmployeeFromShift( int toRemove,Date _date,String _shiftType,String branch) {
        return serviceEmployees.removeEmployeeFromShift(toRemove, _date, _shiftType,branch);
    }

    public Result<SShift> addShift(Date _date , String _type,String branch) {
        return serviceEmployees.addShift(_date, _type,branch);
    }

    public Result<SShift> removeShift(Date _date , String type) {
        return serviceEmployees.removeShift(_date, type);
    }

    public Result<SShift> getShift(Date _date  , String type,String branch) {
       return serviceEmployees.getShift(_date,type,branch);
    }

    public Result<Boolean> addPositionToEmployee(int _id,String _pos){
        return serviceEmployees.addPositionToEmployee(_id, _pos);
    }

    public Result<Boolean> removePositionToEmployee(int _id,String _pos){
        return serviceEmployees.removePositionToEmployee(_id, _pos);
    }

    public Result<Boolean> UpdateEmployeeSalary(int _id,int _newSalary){
        return serviceEmployees.UpdateEmployeeSalary(_id, _newSalary);
    }

    public Result<Boolean> UpdateEmployeeBankAccount(int _id,int _bankAcc){
        return serviceEmployees.UpdateEmployeeBankAccount(_id, _bankAcc);
    }

    public Result<Boolean> UpdateEmployeeName(int _id,String _newName){
        return serviceEmployees.UpdateEmployeeName(_id, _newName);
    }

    public Result<List<SEmployee>> getEmployeesByPosition(String _pos){
        return serviceEmployees.getEmployeesByPosition(_pos);
    }

    public Result<Boolean> updateEmployeeLicense(int id,String license){
        return serviceEmployees.updateEmployeeLicense(id, license);
    }

    public Result<List<DriverDataObj>> getAvailableDriversByLicense(String shiftDate, License_Enum license) {
        return serviceEmployees.getAvailableDriversByLicense(shiftDate, license);
    }

    public ResponseT<Integer> bookDelivery(String date, int driverID, int licenseNumber, int deliveryID) {
        return serviceDelivery.bookDelivery(date,driverID,licenseNumber,deliveryID);
    }

    public ResponseT<List<Integer>> getAllPendingOrders() {
        try{
            List<Integer> ordersIds = sms.getAllPendingOrders();
            return new ResponseT<>(ordersIds);
        }
        catch(Exception e) {
            return new ResponseT<>(e.getMessage(),true);
        }
    }

    public Response leaveHRMessage(String date, License_Enum license) {
        return serviceDelivery.leaveHRMessage(date, license);
    }

    public Response removeDelivery(int id) {
        return serviceDelivery.removeDelivery(id);
    }


/*    public ResponseT<List<DeliveryDataObj>> getAllPendingDeliveries(int kind) {
        return serviceDelivery.getAllPendingDeliveires(kind);
    }*/



//    public ResponseT<List<DriverDataObj>> getAllDriversForTruck(License_Enum license){
//        return serviceDelivery.getAllDriversForTruck(license);
//    }

/*    public ResponseT<Integer> inviteDelivery(String date, int driverID, int truckNumber) {
         return serviceDelivery.inviteDelivery(date,driverID,truckNumber);
    }*/

/*
    public ResponseT<Integer> addSiteDoc(int deliveryID,String name,String siteKind) {
        return serviceDelivery.addSiteDoc(deliveryID ,name ,siteKind);
     }
*/

/*
    public Response addProductToSite(int deliveryID , int siteID , int destID , int catNum, String productName , int quantity) {
        return serviceDelivery.addProductToSite(deliveryID,siteID,destID,catNum,productName,quantity);
    }
*/

/*
    public Response provideSiteDocument(int deliveryID, int siteID) {
        throw new UnsupportedOperationException();
    }
*/

/*
    public Response addDriver(String driverName, String license, int id) {
        return serviceDelivery.addDriver(driverName,license,id);
    }
*/


/*
    public Response changeTruck(int deliveryID, int siteID, int truckNumber) {
        throw new UnsupportedOperationException();
    }
    public Response changeDriver(int deliveryID, int siteID, int driverID) {
        throw new UnsupportedOperationException();
    }
*/
/*
    public Response cancelSiteVisit(int deliveryID, int siteID) {
        throw new UnsupportedOperationException();
    }
*/
/*
    public ResponseT<SiteDocDataObj> getSiteDoc(String siteID) {
        return serviceDelivery.getSiteDoc(siteID);
    }

    public ResponseT<DeliveryDataObj> getDelivery(int deliveryID) {
        return serviceDelivery.getDelivery(deliveryID);
    }

    public Response removeSite(int deliveryID, int sourceID, int destID) {
        return serviceDelivery.removeSite(deliveryID, sourceID, destID);
    }
*/
/*

    public Response addSite(String name, String address, String area, String contactName, String contactPhone) {
        return serviceDelivery.addSite(name, address, area, contactName, contactPhone);
    }

    public ResponseT<List<SiteDataObj>> getAllSites() {
        return serviceDelivery.getAllSites();
    }
*/
}

