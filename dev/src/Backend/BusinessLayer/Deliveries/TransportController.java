package Backend.BusinessLayer.Deliveries;

import Backend.BusinessLayer.Deliveries.Enums.License_Enum;
import Backend.PersistenceLayer.DeliveriesDal.TruckDAO;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TransportController {
    private HashMap<Integer, Driver> drivers;
    private TruckDAO truckDAO;
//    private SiteDAO siteDAO;
//    private HashMap<Integer, Truck> trucks;
    //private HashMap<String, Site> sites;

    public TransportController() {
        drivers = new HashMap<>();
        truckDAO = new TruckDAO();
//        siteDAO = new SiteDAO();
//        trucks = new HashMap<>();
        //sites = new HashMap<>();
    }


    public Truck getByTruckNumber(int truckNumber) throws Exception {
        // "a truck with the vehicle number : " + truckNumber + " is not exists in the company "
        return truckDAO.getTruck(truckNumber);

//        if (!trucks.containsKey(truckNumber)) {
//            throw new Exception("a truck with the vehicle number : " + truckNumber + " is not exists in the company ");
//        }
//        return trucks.get(truckNumber);
    }

    public void addTruck(int licenseNumber, String licenseKind, int weight, int maxWeightAllowed) throws Exception {
        checkTruck(licenseNumber, licenseKind, weight, maxWeightAllowed);
        truckDAO.addTruck(licenseNumber,licenseKind,weight,maxWeightAllowed,true);
    }

    private void checkTruck(int licenseNumber, String licenseKind, int weight, int maxWeightAllowed) throws Exception {
        if (licenseKind == null || licenseKind.length() == 0)
            throw new Exception("license can not be empty");
        try { License_Enum.valueOf(licenseKind);}
        catch (Exception e){ throw new Exception("license is illigle"); }
        if (maxWeightAllowed <= weight)
            throw new Exception("truck weight can not be bigger then her max weight");
    }

    public Collection<Truck> getAllTrucks() throws Exception {
        Collection<Truck> cTrucks = truckDAO.getAllTrucks();
        if(cTrucks.size() == 0 )
            throw new Exception("No Trucks");
        return cTrucks;
    }

    public TruckDAO getTruckDAO() {
        return this.truckDAO;
    }

    public void setTruckAvailable(int licenseNumber, boolean availability) throws Exception {
        truckDAO.updateAvailabillity(licenseNumber,availability);
        truckDAO.getTruck(licenseNumber).setAvailable(availability);
    }


/*
    public Driver getByDriverID(int driverID) throws Exception {
        if (!drivers.containsKey(driverID))
            throw new Exception("a driver with the given ID number is not exists in the company");
        return drivers.get(driverID);
    }

    public SiteDAO getSiteDAO() {
        return this.siteDAO;
    }

    private void checkDriver(String driverName, String license, int id) throws Exception {
        if (license == null || license.length() == 0)
        throw new Exception("license can not be empty");
        if (driverName == null || driverName.length() == 0)
            throw new Exception("driverName can not be empty");
    }
    public Site getSite(String name) throws Exception {
        return  siteDAO.getSite(name);
//        if (!sites.containsKey(name)) {
//            throw new Exception("a site with the name : " + name + " is not exists in the company ");
//        }
//        return sites.get(name);
    }


    public void addDriver(String driverName, String license, int id) throws Exception {
        if (drivers.containsKey(id))
            throw new Exception("id already exist!");
        checkDriver(driverName, license, id);
        Driver driver = new Driver(driverName, license, id);
        drivers.put(id, driver);
    }

    public Collection<Driver> getAllDriversForTruck(License_Enum license) throws Exception {
        Collection<Driver> list = new ArrayList<>();
        for (Driver driver : drivers.values()) {
            if (driver.getLicense().canIDriveIt(license))
                list.add(driver);
        }
        if (list.size() == 0)
            throw new Exception("there are no available drivers for this truck");
        return list;
    }

    public void addSite(String name, String address, String area, String contactName, String contactPhone) throws Exception {
        siteDAO.addSite(name,address,contactName,contactPhone,area);
//        if (sites.containsKey(name))
//            throw new Exception("site name already exist!");
    }

    public Collection<Site> getAllSites() throws Exception {
        return siteDAO.getAllSites();
//        if (sites.values().size() == 0)
//            throw new Exception("there are no sites");
//        return sites.values();
    }

    public Truck getNextTruck() {
        return truckDAO.getNextTruck();
    }
*/

}
