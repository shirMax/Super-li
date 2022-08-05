package Backend.BusinessLayer.Deliveries;


import Backend.BusinessLayer.CallBacks.*;
import Backend.BusinessLayer.Deliveries.Enums.Area_Enum;
import Backend.BusinessLayer.Deliveries.Enums.DeliveryMode_Enum;
import Backend.BusinessLayer.Deliveries.Enums.License_Enum;
import Backend.BusinessLayer.Deliveries.Enums.SiteKind_Enum;
import Backend.BusinessLayer.Interfaces.CompleteDeliveryCallback;
import Backend.BusinessLayer.Suppliers.Contact;
import Backend.BusinessLayer.Tools.Pair;
import Backend.PersistenceLayer.*;
import Backend.PersistenceLayer.DeliveriesDal.*;
import Backend.BusinessLayer.Tools.DateConvertor;
import Backend.ServiceLayer.ObjectsDeliveries.SiteDocDataObj;
import sun.reflect.generics.reflectiveObjects.NotImplementedException;
//import jdk.internal.joptsimple.util.DateConverter;
//import sun.reflect.generics.reflectiveObjects.NotImplementedException;
//import jdk.jshell.spi.ExecutionControl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

public class DeliveryController {

    private HashMap<Integer, Delivery> _awaitDeliveries;
    private HashMap<Integer, Delivery> _OnWaysDeliveries;
    private HashMap<Integer, Delivery> _ArrivedDeliveries;
    private HashMap<Integer, Delivery> _PendingDeliveries;
    private SiteDocDAO siteDocDAO;
    private TransportController transportController;
    private CompleteDeliveryCallback completeDeliveryCallback;
    private UpdateOrderDateCallBack updateOrderDateCallBack;
    private UpdateOrderProductQuantityCallBack updateOrderProductQuantityCallBack;
    private HRManagerNotificationCallBack hrManagerNotificationCallBack;
    private HashMap<Delivery,List<Date>> pendingOrders;
    private CancelOrderCallBack cancelOrderCallBack;
    private PendingDeliveriesDAO pendingDeliveriesDAO;

    public void setCompleteDeliveryCallback(CompleteDeliveryCallback completeDeliveryCallback) {
        this.completeDeliveryCallback = completeDeliveryCallback;
    }

    public final int SHOULD_REMOVED = -1;
    public final int IGNORE_ARG = -1;
    public final int WITHOUT_ORDER = -1;
    public final int AWAIT_MODE = 0;
    public final int ONWAYS_MODE = 1;
    public final int REDESIGN_MODE = 2;
    public final int ARRIVED_MODE = 3;
    public final int PENDING_MODE = 4;
    private int deliveryIdCounter;
    public DateConvertor dateConverter;
    private CheckShiftAvailability checkShiftAvailability;
    private SwitchDriversCallBack switchDriversCallBack;
    private CheckStockKeeperCallBack checkStockKeeperCallBack;

    private DeliveryDAO deliveryDAO;
    private ProductsSiteDocDAO productsSiteDocDAO;

    public DeliveryController() {
        transportController = new TransportController();
        _awaitDeliveries = new HashMap<>();
        _OnWaysDeliveries = new HashMap<>();
        _ArrivedDeliveries = new HashMap<>();
        _PendingDeliveries = new HashMap<>();
    //    siteIdCounter = 0;
        deliveryIdCounter = 0;
        checkShiftAvailability = null;
        siteDocDAO = new SiteDocDAO(transportController.getTruckDAO()/*, transportController.getSiteDAO()*/);
        dateConverter = new DateConvertor();
        productsSiteDocDAO = new ProductsSiteDocDAO(siteDocDAO);
        pendingDeliveriesDAO = new PendingDeliveriesDAO();
        deliveryDAO = new DeliveryDAO(transportController.getTruckDAO(), siteDocDAO, productsSiteDocDAO);
    }


//    public void changeTruck(int deliveryID, int siteID, int truckNumber) throws Exception {
//        Delivery delivery = getDelivery(deliveryID);
//        if (delivery.getMode() == DeliveryMode_Enum.InTheWays || delivery.getMode() == DeliveryMode_Enum.Arrived) {
//            throw new Exception("you cannot change truck");
//        }
//        SiteDocument currentSiteDocument = delivery.getSite(siteID);
//        Truck requiredTruck = validateTruckNumber(truckNumber);
//        if (!requiredTruck.isAvailable()) {
//            throw new Exception("The truck" + requiredTruck.getLicense() + "is already in use");
//        }
//        validateTruckDriverLicense(requiredTruck, delivery.getDriver());
//
//        Truck oldTruck = delivery.getTruck();
//        //oldTruck.setAvailable(false);
//        delivery.setTruck(requiredTruck);
//        //requiredTruck.setAvailable(true);
//        if (delivery.getMode() == DeliveryMode_Enum.ReDesigning) {
//            currentSiteDocument.addComment("The truck with the vehicle number : " + oldTruck.getLicenseNumber() + " " +
//                    "has changed by the truck with the vehicle number: " + requiredTruck.getLicenseNumber() + "due to overWeight Problem");
//        }
//    }
//
//    public void changeDriver(int deliveryID, int siteID, int driverID) throws Exception {
//
//        Delivery delivery = getDelivery(deliveryID);
//        if (delivery.getMode() == DeliveryMode_Enum.InTheWays || delivery.getMode() == DeliveryMode_Enum.Arrived) {
//            throw new Exception("you cannot change truck");
//        }
//        SiteDocument currentSiteDocument = delivery.getSite(siteID);
//        Driver requiredDriver = validateDriver(driverID);
//        if (!requiredDriver.isAvailable()) {
//            throw new Exception("The driver" + requiredDriver.getLicense() + "is already in the ways");
//        }
//        Truck currentTruck = delivery.getTruck();
//        validateTruckDriverLicense(currentTruck, requiredDriver);
//
//        validateDriver(delivery.getDeliveryDate(),driverID);
//        //oldDriver.setAvailable(false);
//        delivery.setDriver(requiredDriver);
//        //requiredDriver.setAvailable(true);
//        if (delivery.getMode() == DeliveryMode_Enum.InTheWays) {
//            currentSiteDocument.addComment("The driver :" + oldDriver.getName() + "with id : " + oldDriver.getId()
//                    + "has been replaced with the driver : " + requiredDriver.getName() + "with the id number : " + requiredDriver.getId());
//        }
//
//    }

    public void changeTruckDriver(int deliveryID, int siteID, int truckNumber, int driverID) throws Exception {
        Delivery delivery = getDelivery(deliveryID);
        if (delivery.getMode() == DeliveryMode_Enum.InTheWays ||
                delivery.getMode() == DeliveryMode_Enum.Arrived) {
            throw new Exception("you cannot change truck");
        }

        Truck requiredTruck = validateTruckNumber(truckNumber);
//        validateDriver(delivery.getDeliveryDate(),driverID,requiredTruck.getLicense());
        boolean isDriverAvailable = true;
        if(driverID != delivery.getDriverID())
            isDriverAvailable = checkShiftAvailability.call(delivery.getDeliveryDate(), driverID, requiredTruck.getLicense());
        boolean isTruckAvailable = true;
        if(truckNumber != delivery.getTruck().getLicenseNumber())
            isDriverAvailable = requiredTruck.isAvailable();
        //validateTruckDriverLicense(requiredTruck, requiredDriver);
        SiteDocument currentSiteDocument = null;
        if (siteID != IGNORE_ARG) {
            currentSiteDocument = delivery.getSite(siteID);
        }
        if (!(isDriverAvailable && isTruckAvailable)) {
            throw new Exception("the driver or the truck is on a shipment right now");
        }
        int oldDriverID = delivery.getDriverID();
        Truck oldTruck = delivery.getTruck();

        if (!(delivery.getMode() == DeliveryMode_Enum.Invited)) {
            switchDriversCallBack.call(delivery.getDeliveryDate(), oldDriverID, driverID);
        }
//        Driver oldDriver = delivery.getDriver();
        delivery.setDriver(driverID);
        delivery.setTruck(requiredTruck);
        if (delivery.getMode() == DeliveryMode_Enum.ReDesigning &&
                currentSiteDocument != null) {
            setSiteDocTruckDriver(delivery, requiredTruck, driverID);
            currentSiteDocument.addComment("The driver with id : " + oldDriverID
                    + "has been replaced with the driver with the id number : " + driverID);
            currentSiteDocument.addComment("The truck with the vehicle number : " + oldTruck.getLicenseNumber() + " " +
                    "has changed by the truck with the vehicle number: " + requiredTruck.getLicenseNumber() + "due to overWeight Problem");
            delivery.setMode(DeliveryMode_Enum.InTheWays);
        }


//        currentSiteDocument.addComment("The driver :" + oldDriver.getName() + "with id : " + oldDriver.getId()
//                + "has been replaced with the driver : " + requiredDriver.getName() + "with the id number : " + requiredDriver.getId());
//        currentSiteDocument.addComment("The truck with the vehicle number : " + oldTruck.getLicenseNumber() + " " +
//                "has changed by the truck with the vehicle number: " + requiredTruck.getLicenseNumber() + "due to overWeight Problem");
//        delivery.setMode(DeliveryMode_Enum.InTheWays);
    }

    public void launchDelivery(int deliveryID) throws Exception {
        Delivery reqDelivery = getDelivery(deliveryID);
        if (reqDelivery.getSiteAmount() == 0)
            throw new Exception("there are no sites in the delivery");
        int deliveryDriver = reqDelivery.getDriverID();
        validateDriver(reqDelivery.getDeliveryDate(), deliveryDriver, reqDelivery.getTruck().getLicense());
        validateDeliveryDest(reqDelivery);
        Truck deliveryTruck = reqDelivery.getTruck();
        switchDriversCallBack.call(reqDelivery.getDeliveryDate(), reqDelivery.getDriverID(), IGNORE_ARG);
        setSiteDocTruckDriver(reqDelivery, deliveryTruck, deliveryDriver);
        transportController.setTruckAvailable(deliveryTruck.getLicenseNumber(), false);
        reqDelivery.setMode(DeliveryMode_Enum.InTheWays);
        // deliveryDAO.update(deliveryID,DeliveryMode_Enum.InTheWays.toString());
        _awaitDeliveries.remove(deliveryID);
        _OnWaysDeliveries.put(deliveryID, reqDelivery);
    }

    private void validateDeliveryDest (Delivery reqDelivery) throws Exception {
        boolean isDest = false;
        for(SiteDocument siteDocument : reqDelivery.getAllSite()) {
            if (siteDocument.getSiteKind() == SiteKind_Enum.DESTINATION) {
                Contact contact = validateStoreKeeper(reqDelivery.getDeliveryDate(), siteDocument.getAddress());
                siteDocument.setContact(contact);
                isDest = true;
            }
        }
        if(!isDest)
            throw new Exception("there is no destination in the delivery!");
    }

    /* private Delivery getDelivery(int deliveryID,int place) throws Exception {
         switch (place) {
             case 0:
                 if (!_awaitDeliveries.containsKey(deliveryID)) {
                     throw new Exception("there is no delivery with this id!");
                 }
                 return _awaitDeliveries.get(deliveryID);
             case 1:
                 if (!_OnWaysDeliveries.containsKey(deliveryID)) {
                     throw new Exception("there is no delivery with this id!");
                 }
                 return _OnWaysDeliveries.get(deliveryID);

             case 2:
                 if (!_ArrivedDeliveries.containsKey(deliveryID)) {
                     throw new Exception("there is no delivery with this id!");
                 }
                 return _ArrivedDeliveries.get(deliveryID);

         }
         throw new Exception("No exists delivery state entered");
     }
 */

//    private Driver validateDriver(int driverID) throws Exception {
//        return transportController.getByDriverID(driverID);
//    }


    public void addTruck(int licenseNumber, String licenseKind, int weight, int maxWeightAllowed) throws Exception {
        transportController.addTruck(licenseNumber, licenseKind, weight, maxWeightAllowed);
    }
    public Delivery getDelivery(int deliveryID) throws Exception {
        if (_awaitDeliveries.containsKey(deliveryID)) {
            return _awaitDeliveries.get(deliveryID);
        }
        if (_PendingDeliveries.containsKey(deliveryID)) {
            return _PendingDeliveries.get(deliveryID);
        }
        if (_OnWaysDeliveries.containsKey(deliveryID)) {
            return _OnWaysDeliveries.get(deliveryID);
        }
        if (_ArrivedDeliveries.containsKey(deliveryID)) {
            return _ArrivedDeliveries.get(deliveryID);
        }
        Delivery delivery = deliveryDAO.getDelivery(deliveryID);
        addDeliveryToMap(delivery);
        return delivery;
    }

    public void updateProductInSite(int deliveryID, int sourceID, int destinationID, int catNum, int quantity) throws Exception {
        Delivery delivery = getDelivery(deliveryID);
        if (quantity == SHOULD_REMOVED) {
            removeProductFromSite(deliveryID, sourceID, destinationID, catNum);
        } else {
            delivery.updateProductToSite(sourceID, destinationID, catNum, quantity);
        }
        updateOrderProductQuantityCallBack.call(delivery.getOrderID(),catNum,quantity);
    }

    public void removeProductFromSite(int deliveryID, int siteID, int destID, int catNum) throws Exception {
        Delivery delivery = getDelivery(deliveryID);
        delivery.removeProductToSite(siteID, destID, catNum);
        delivery.removeProductToSite(destID, siteID, catNum);
        //  delivery.moveToInTheWay();
        updateOrderProductQuantityCallBack.call(delivery.getOrderID(),catNum,0);

    }

    public void informSiteVisit(int deliveryID, int siteID, int weightTaken) throws Exception {
        Delivery delivery = getDelivery(deliveryID);
        delivery.informSiteVisit(siteID, weightTaken);
        if (delivery.getMode() == DeliveryMode_Enum.Arrived) {
            switchDriversCallBack.call(delivery.getDeliveryDate(), delivery.getDriverID(), IGNORE_ARG);
            transportController.setTruckAvailable(delivery.getTruck().getLicenseNumber(),true);
            deliveryDAO.update(deliveryID, DeliveryMode_Enum.Arrived.toString());
            _OnWaysDeliveries.remove(deliveryID);
            _ArrivedDeliveries.put(deliveryID, delivery);
            if(delivery.getOrderID() >=0){
                SiteDocument siteDocument = delivery.getSite(siteID);
                completeDeliveryCallback.completeDelivery(delivery.getOrderID(),siteDocument.getAddress());
                //completeDeliveryCallback.completeDelivery(delivery.getOrderID()); todo: add branch address to send
            }
        }
    }
/*        List<TruckDataObj> list = new ArrayList<>();
        for(Truck truck : trucks.values()) {
            list.add(new TruckDataObj(truck.getLicenseNumber(),
                    truck.getLicense(), truck.getWeight(), truck.getMaxWeight()));
        }
        if(list.size() == 0)
            throw new Exception("there are no available trucks");*/

    public Collection<Truck> getAllTrucks() throws Exception {
        return transportController.getAllTrucks();
    }
    /*        for(Driver driver : drivers.values()){
            if(driver.getLicense().canIDriveIt(license))
                list.add(new DriverDataObj(driver.getName(), driver.getLicense(), driver.getId()));
        }
        if(list.size() == 0)
            throw new Exception("there are no available drivers for this truck");*/

/*    public DeliveryDataObj getDelivery(String deliveryID) throws ExecutionControl.NotImplementedException {
        throw new ExecutionControl.NotImplementedException("");
    }*/

    public Collection<Delivery> getAllDeliveries(int kind) throws Exception {
        switch (kind) {
            case AWAIT_MODE:
                if (!_awaitDeliveries.values().isEmpty())
                    return _awaitDeliveries.values();
                return deliveryDAO.getAllDeliveries(DeliveryMode_Enum.Invited.toString());

            case ONWAYS_MODE:
//                List<Delivery> list1 = new ArrayList<>();
//                for(Delivery delivery : _OnWaysDeliveries.values()) {
//                    if (delivery.getMode() == DeliveryMode_Enum.InTheWays)
//                        list1.add(delivery);
//                }
//                if (list1.size() == 0)
//                    throw new Exception("there are no available deliveries");
//                return list1;
                return deliveryDAO.getAllDeliveries(DeliveryMode_Enum.InTheWays.toString());

            case PENDING_MODE:
//                List<Delivery> list1 = new ArrayList<>();
//                for(Delivery delivery : _OnWaysDeliveries.values()) {
//                    if (delivery.getMode() == DeliveryMode_Enum.InTheWays)
//                        list1.add(delivery);
//                }
//                if (list1.size() == 0)
//                    throw new Exception("there are no available deliveries");
//                return list1;
                return deliveryDAO.getAllPendingDeliveries(DeliveryMode_Enum.Pending.toString());


            case REDESIGN_MODE:
//                List<Delivery> list = new ArrayList<>();
//                for(Delivery delivery : _OnWaysDeliveries.values()) {
//                    if (delivery.getMode() == DeliveryMode_Enum.ReDesigning)
//                        list.add(delivery);
//                }
//                if (list.isEmpty())
//                    throw new Exception("there are no available deliveries");
//                return list;
                return deliveryDAO.getAllDeliveries(DeliveryMode_Enum.ReDesigning.toString());


            case ARRIVED_MODE:
/*
//                if (_ArrivedDeliveries.values().size() == 0)
//*/
//                if (_ArrivedDeliveries.values().isEmpty())
//
//                    throw new Exception("there are no available deliveries");
//                return _ArrivedDeliveries.values();

                if (!_ArrivedDeliveries.values().isEmpty())
                    return _ArrivedDeliveries.values();
                return deliveryDAO.getAllDeliveries(DeliveryMode_Enum.Invited.toString());

            default:
                Collection<Delivery> allDeliveries = deliveryDAO.getAllDeliveries();

                return allDeliveries;

        }
    }

    public void changeDate(int deliveryId, String newDate) throws Exception {
        Date deliveryDate = validateDate(newDate);
        Delivery delivery = getDelivery(deliveryId);
        if (delivery.getMode() == DeliveryMode_Enum.Invited) {
            delivery.setDeliveryDate(deliveryDate);
            updateOrderDateCallBack.call(delivery.getOrderID(),new java.sql.Date(deliveryDate.getTime()));
        }
        else throw new Exception("the delivery date  cant be changed!");
    }

    public void loadData() throws Exception {
//        try {
//            SiteDocument s = siteDocDAO.getSiteDoc(2);
//            s.addComment("dsdsa");
//        } catch (Exception exception) {
//            exception.printStackTrace();
//        }
        //deliveryDAO.loadAllSiteDocDelivery(-1, "",-1,"","SiteDocDeliveries");
/*
        transportController.addDriver("Yossi", "C1",123456789);
        transportController.addDriver("Michal", "C",246813579);
*/

        transportController.addTruck(1122233, "C", 360, 1400);
        transportController.addTruck(4567832, "C", 236, 998);

//        inviteDelivery("25/05/2023 08:00", 123456789, 1122233);
//        inviteDelivery("25/05/2023 18:00", 246813579, 4567832);
//
//        addSite("Osem", "Maze 3 TLV", "Center", "Elan", "0501231234");
//        addSite("Snif TLV", "Mapo 30 TLV", "Center", "Oded", "0503145837");
//        addSite("Coca Cola", "Bilo 3, Rehovot", "Center", "David", "0527563847");
//        addSite("Snif Rehovot", "Gamla 5, Rehovot", "Center", "Dvir", "0538263846");
//
//        addSiteToDelivery(1, "Osem", "SOURCE");
//        addSiteToDelivery(1, "Snif TLV", "DESTINATION");
//        addSiteToDelivery(2, "Coca Cola", "SOURCE");
//        addSiteToDelivery(2, "Snif Rehovot", "DESTINATION");
//
//        addProductToSite(1, 1, 2, 345, "Bamba", 30);
//        addProductToSite(1, 1, 2, 123, "Dubonim", 20);
//        addProductToSite(2, 3, 4, 847, "Cola 1.5 l", 15);
//        addProductToSite(2, 3, 4, 937, "Zero 0.5 l", 20);

        //launchDelivery(1);

    }

    public void setCheckShiftAvailability(CheckShiftAvailability checkShiftAvailability) {
        this.checkShiftAvailability = checkShiftAvailability;
    }

    public void setSwitchDrivers(SwitchDriversCallBack switchDriversCallBack) {
        this.switchDriversCallBack = switchDriversCallBack;
    }

    public void setCheckStockKeeper(CheckStockKeeperCallBack checkStockKeeperCallBack) {
        this.checkStockKeeperCallBack = checkStockKeeperCallBack;
    }

    private void addDeliveryToMap(Delivery delivery) {
        if (delivery.getMode() == DeliveryMode_Enum.Pending)
            _PendingDeliveries.put(delivery.getId(), delivery);
        if (delivery.getMode() == DeliveryMode_Enum.Invited)
            _awaitDeliveries.put(delivery.getId(), delivery);
        if (delivery.getMode() == DeliveryMode_Enum.InTheWays || delivery.getMode() == DeliveryMode_Enum.ReDesigning)
            _OnWaysDeliveries.put(delivery.getId(), delivery);
        if (delivery.getMode() == DeliveryMode_Enum.Arrived)
            _ArrivedDeliveries.put(delivery.getId(), delivery);
    }

    private Date convertDate(String date) {
        List<SimpleDateFormat> knownPatterns = new ArrayList<SimpleDateFormat>();
        SimpleDateFormat sd = new SimpleDateFormat("dd-MM-yyyy' 'HH:mm");
        SimpleDateFormat sd2 = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm");
        knownPatterns.add(sd);
        knownPatterns.add(sd2);
        sd.setLenient(false);
        sd2.setLenient(false);

        for (SimpleDateFormat pattern : knownPatterns) {
            try {
                Date d = new Date(pattern.parse(date).getTime());
                return d;
            } catch (Exception pe) {
            }
        }
        return null;
    }

    private Truck validateTruckNumber(int truckNumber) throws Exception {
        return transportController.getByTruckNumber(truckNumber);
    }

    private void validateDriver(Date deliveryDate, int driverID, License_Enum license) throws Exception {
        if (!checkShiftAvailability.call(deliveryDate, driverID, license))
            throw new Exception("driverID is not reachable");
    }

    private Contact validateStoreKeeper(Date deliveryDate, String address) throws Exception {
        Contact contact = checkStockKeeperCallBack.call(deliveryDate,address);
        if(contact == null)
            throw new Exception("there is no reachable storekeeper");
        return contact;
    }

    private void validateTruck(Truck truck) throws Exception {
        if (!truck.isAvailable()) {
            throw new Exception("The truck" + truck.getLicense() + "is already in use");
        }
    }

    private Date validateDate(String date) throws Exception {
        if (date == null || date.isEmpty()) {
            throw new Exception("Illegal date entered");
        }

        Date deliveryDateTime = convertDate(date);
        if (deliveryDateTime == null)
            throw new Exception("Invalid Date entered");
        Date currentTime = new Date();
        LocalDateTime localDateTime = LocalDateTime.ofInstant(currentTime.toInstant(), ZoneId.systemDefault());
        Date dateFromLocalDT = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        if (dateFromLocalDT.compareTo(deliveryDateTime) > 0) {
            throw new Exception("The date your entered has been passed");
        }

        return deliveryDateTime;
    }

    public void initial() {
        CreateDB createDBDeliveries = new CreateDBDeliveries();
        createDBDeliveries.createFileIfNotExists();
    }


    public int orderDelivery(java.sql.Date date, int orderID) throws Exception {
        //TODO: return new delivery with the nearest date
/*        List<String> myDatesString = new ArrayList<>();
        List<Date> myDates = new ArrayList<>();
        for(java.sql.Date date: dates) {
            myDatesString.add(dateConverter.dateToString(date));
            myDates.add(dateConverter.stringToDate(dateConverter.dateToString(date)));
        }*/
        Delivery delivery = deliveryDAO.addPendingDelivery(orderID, dateConverter.dateToString(date));
        //pendingOrders.put(delivery,myDates);
        return delivery.getId();
    }


    public int addSupplierToDelivery(int deliveryID, String name, String contactName, String contactPhone, Area_Enum area_enum, String address) throws Exception
    {
        if (name.length() == 0)
            throw new Exception("no empty parameters allowed");
        Delivery delivery = getDelivery(deliveryID);
        if (delivery.getMode() == DeliveryMode_Enum.Arrived || delivery.getMode() == DeliveryMode_Enum.InTheWays) {
            throw new Exception("add Site are not allowed in this Delivery Mode :" + delivery.getMode().toString());
        }
/*        SiteDocument siteDocument = siteDocDAO.addSiteDoc(SiteKind_Enum.SOURCE.toString(),
                delivery.getTruck().getWeight(), delivery.getDriverID(), delivery.getTruck().getLicenseNumber(),
                address,name,area_enum,contactName,contactPhone);*/
        SiteDocument siteDocument = siteDocDAO.addSiteDoc(SiteKind_Enum.SOURCE.toString(),
                address,name,area_enum,contactName,contactPhone);
        deliveryDAO.addSiteDocToDelivery(delivery.getId(), siteDocument.getSiteDocId());
        delivery.addSite(siteDocument);
        return siteDocument.getSiteDocId();
    }

    public int addBrunchToDelivery(int deliveryID, Area_Enum area_enum, String address) throws Exception
    {
        Delivery delivery = getDelivery(deliveryID);
        Contact contact = checkStockKeeperCallBack.call(delivery.getDeliveryDate(),address);
        if(contact == null) {
            contact = new Contact(0,null,null);
            //throw new Exception("there is no stock keeper in the branch!");
        }
        if (delivery.getMode() == DeliveryMode_Enum.Arrived || delivery.getMode() == DeliveryMode_Enum.InTheWays) {
            throw new Exception("add Site are not allowed in this Delivery Mode :" + delivery.getMode().toString());
        }
        SiteDocument siteDocument = siteDocDAO.addSiteDoc(SiteKind_Enum.DESTINATION.toString(),
                address,address,area_enum,contact.getName(),contact.getPhone());
        deliveryDAO.addSiteDocToDelivery(delivery.getId(), siteDocument.getSiteDocId());
        delivery.addSite(siteDocument);
        return siteDocument.getSiteDocId();
    }

    public void addProductToSite(int deliveryID, int itemID, String productName, int quantity) throws Exception {
        Delivery delivery = getDelivery(deliveryID);
        if (delivery.getMode() == DeliveryMode_Enum.Arrived || delivery.getMode() == DeliveryMode_Enum.InTheWays) {
            throw new Exception("adding products are not allowed in this Delivery Mode :" + delivery.getMode().toString());
        }
        List<SiteDocument> sites = delivery.getAllSite();
        if (sites.size() != 2 || sites.get(0).getSiteKind() != SiteKind_Enum.SOURCE ||
                sites.get(1).getSiteKind() != SiteKind_Enum.DESTINATION) {
            throw new Exception("adding products are not allowed in this delivery:" + delivery.getId());
        }
        delivery.addProductToSite(productsSiteDocDAO, sites.get(0).getSiteDocId(), sites.get(1).getSiteDocId(),
                itemID, productName, quantity);
        productsSiteDocDAO.addProductToSiteDocs(sites.get(0).getSiteDocId(), sites.get(1).getSiteDocId(),
                itemID, productName, quantity);
    }

    public void removeDelivery(int deliveryID) throws Exception {
        Delivery delivery = getDelivery(deliveryID);
        removeDeliveryCallback(deliveryID);
        cancelOrderCallBack.call(delivery.getOrderID());
    }

    public void removeDeliveryCallback(int deliveryID) throws Exception {
        Delivery delivery = getDelivery(deliveryID);
        if (delivery.getMode() == DeliveryMode_Enum.Arrived || delivery.getMode() == DeliveryMode_Enum.InTheWays) {
            throw new Exception("remove delivery are not allowed in this Delivery Mode :" + delivery.getMode().toString());
        }
        if(delivery.getMode() != DeliveryMode_Enum.Pending) {
            transportController.setTruckAvailable(delivery.getTruck().getLicenseNumber(),true);
            switchDriversCallBack.call(delivery.getDeliveryDate(),delivery.getDriverID(),IGNORE_ARG);
            _OnWaysDeliveries.remove(delivery);
        }
        else _PendingDeliveries.remove(delivery);
        delivery.setMode(DeliveryMode_Enum.Arrived);
        for(SiteDocument siteDocument : delivery.getAllSite()){
            if(!siteDocument.isVisited()){
                siteDocument.addComment("The delivery has canceled, and this site has not been visited.");
            }
            else siteDocument.setVisited(true);
        }
        _ArrivedDeliveries.put(deliveryID, delivery);
        //deliveryDAO.removeDelivery(deliveryID);
    }

    public void updateProductInSite(int deliveryID, int itemID, int quantity) throws Exception {
        Delivery delivery = getDelivery(deliveryID);
        if (delivery.getMode() == DeliveryMode_Enum.Arrived || delivery.getMode() == DeliveryMode_Enum.InTheWays) {
            throw new Exception("adding products are not allowed in this Delivery Mode :" + delivery.getMode().toString());
        }
        List<SiteDocument> sites = delivery.getAllSite();
        if (sites.size() != 2 || sites.get(0).getSiteKind() != SiteKind_Enum.SOURCE ||
                sites.get(1).getSiteKind() != SiteKind_Enum.DESTINATION) {
            throw new Exception("adding products are not allowed in this delivery:" + delivery.getId());
        }
        if (quantity == SHOULD_REMOVED) {
            removeProductFromSite(deliveryID, sites.get(0).getSiteDocId(),
                    sites.get(1).getSiteDocId(), itemID);
        } else {
            delivery.updateProductToSite(deliveryID, sites.get(0).getSiteDocId(),
                    sites.get(1).getSiteDocId(), itemID);
        }
    }

    public void setUpdateOrderDateCallBack(UpdateOrderDateCallBack updateOrderDateCallBack){
        this.updateOrderDateCallBack = updateOrderDateCallBack;
    }

    public void setUpdateOrderProductQuantityCallBack(UpdateOrderProductQuantityCallBack updateOrderProductQuantityCallBack){
        this.updateOrderProductQuantityCallBack = updateOrderProductQuantityCallBack;
    }

    public void setHRManagerNotificationCallBack(HRManagerNotificationCallBack hrManagerNotificationCallBack){
        this.hrManagerNotificationCallBack = hrManagerNotificationCallBack;
    }

    public void setCancelOrderCallBack(CancelOrderCallBack cancelOrder) {
        this.cancelOrderCallBack = cancelOrder;
    }

    public int bookDelivery(String date, int driverID, int licenseNumber, int deliveryID) throws Exception {
        Date deliveryDate = dateConverter.validateDate(date);
        Truck requiredTruck = validateTruckNumber(licenseNumber);
        validateDriver(deliveryDate, driverID, requiredTruck.getLicense());
        Delivery delivery = getDelivery(deliveryID);
        delivery.setTruck(requiredTruck);
        delivery.setDriver(driverID);
        delivery.setDeliveryDate(deliveryDate);
        delivery.setMode(DeliveryMode_Enum.Invited);
        setSiteDocTruckDriver(delivery, requiredTruck, driverID);
        _awaitDeliveries.put(_awaitDeliveries.size() + 1, delivery);
        updateOrderDateCallBack.call(delivery.getOrderID(),new java.sql.Date(dateConverter.stringToDate(date).getTime()));
        return delivery.getId();
    }

    private void setSiteDocTruckDriver(Delivery delivery, Truck requiredTruck, int driverID){
        for(SiteDocument siteDocument : delivery.getAllSite()){
            if(!siteDocument.isVisited()) {
                siteDocument.setTruck(requiredTruck);
                siteDocument.setDriver(driverID);
            }
        }
    }

    public void leaveHRMessage(String date, License_Enum license) {
        hrManagerNotificationCallBack.call(dateConverter.stringToDate(date),license);
    }

    public boolean isEmployeeAvailable(String name, String phone, Date date){
        for(Delivery delivery : _OnWaysDeliveries.values()){
            for(SiteDocument siteDocument : delivery.getAllSite()){
                if(!siteDocument.isVisited() && siteDocument.getContactName().equals(name) &&
                                                siteDocument.getContactPhone().equals(phone) &&
                dateConverter.dateToStringWithoutHour(date).equals(dateConverter.dateToStringWithoutHour(delivery.getDeliveryDate())))
                    return true;
            }
        }
        return false;
    }

/*
    public void addDriver(String driverName, String license, int id) throws Exception {
        transportController.addDriver(driverName, license, id);
    }
    public int inviteDelivery(String departureTime, int driverID, int truckNumber) throws Exception {
//        Date deliveryDate = dateConverter.validateDate(departureTime);
//        Driver requiredDriver = validateDriver(driverID);
//        Truck requiredTruck = validateTruckNumber(truckNumber);
//        Integer nextDeliveryID = deliveryIdCounter++;
        Date deliveryDate = dateConverter.validateDate(departureTime);
        Truck requiredTruck = validateTruckNumber(truckNumber);
        validateDriver(deliveryDate, driverID, requiredTruck.getLicense());
        Delivery delivery = deliveryDAO.addDelivery(departureTime, "Invited", truckNumber, driverID,WITHOUT_ORDER);
        _awaitDeliveries.put(_awaitDeliveries.size() + 1, delivery);
        return delivery.getId();
    }

    */
/*        if(licenseNumber == null || licenseNumber.length() == 0)
                throw new Exception("license number can not be empty");*/
    /*


    public int addSiteToDelivery(int deliveryID, String name, String siteKind) throws Exception {
        if (name.length() == 0)
            throw new Exception("no empty parameters allowed");
        Delivery delivery = getDelivery(deliveryID);
        if (delivery.getMode() == DeliveryMode_Enum.Arrived || delivery.getMode() == DeliveryMode_Enum.InTheWays) {
            throw new Exception("add Site are not allowed in this Delivery Mode :" + delivery.getMode().toString());
        }
        Site site = transportController.getSite(name);
        SiteDocument siteDocument = siteDocDAO.addSiteDoc(siteKind,
                delivery.getTruck().getWeight(), delivery.getDriverID(), delivery.getTruck().getLicenseNumber(),
                site.getAddress(),site.getName(),site.getArea(),site.getContactName(),site.getContactPhone());
        deliveryDAO.addSiteDocToDelivery(delivery.getId(), siteDocument.getSiteDocId());
        */
/*
        switch (siteKind) {
            case "SOURCE":
                siteDocument = new SiteDocument(site, siteKind,
                        siteIdCounter, delivery.getDriver(), delivery.getTruck());
                break;
            case "DESTINATION":
                siteDocument = new SiteDocument(site, siteKind,
                        siteIdCounter, delivery.getDriver(), delivery.getTruck());

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + siteKind);
        }*//*

        //   siteIdCounter++;
        delivery.addSite(siteDocument);
        return siteDocument.getSiteDocId();
    }


    public void removeSiteFromDelivery(int deliveryID, int siteID, int destID) throws Exception {
        Delivery delivery = getDelivery(deliveryID);
        if (SHOULD_REMOVED == destID) {
            delivery.removeSite(siteID);
        } else {
            delivery.removeSite(siteID, destID);
        }
    }

    public void addProductToSite(int deliveryID, int siteID, int secondSiteID, int catNum, String productName, int quantity) throws Exception {
        Delivery delivery = getDelivery(deliveryID);
*/
/*        if(delivery.getMode() == DeliveryMode_Enum.Arrived || delivery.getMode() == DeliveryMode_Enum.InTheWays){
            throw new Exception("adding products are not allowed in this Delivery Mode :" + delivery.getMode().toString());
        }*//*

        delivery.addProductToSite(productsSiteDocDAO, siteID, secondSiteID, catNum, productName, quantity);
        productsSiteDocDAO.addProductToSiteDocs(siteID, secondSiteID, catNum, productName, quantity);
    }

    private List<DeliveryDTO> createDemoData() {
        List<DeliveryDTO> list = new ArrayList<>();

        DriverDTO driverDTO1 = new DriverDTO("Yossi", License_Enum.C1, 123456789);
        DriverDTO driverDTO2 = new DriverDTO("Michal", License_Enum.C, 246813579);

        TruckDTO truckDTO1 = new TruckDTO(1122233, License_Enum.C, 360, 1400);
        TruckDTO truckDTO2 = new TruckDTO(4567832, License_Enum.C, 236, 998);

        ProductDTO productDTO1 = new ProductDTO("Bamba", 30, 345);
        ProductDTO productDTO2 = new ProductDTO("Dubonim", 20, 123);
        ProductDTO productDTO3 = new ProductDTO("Cola 1.5 l", 15, 847);
        ProductDTO productDTO4 = new ProductDTO("Zero 0.5 l", 20, 937);

        List<ProductDTO> prodList1 = new ArrayList<>();
        List<ProductDTO> prodList2 = new ArrayList<>();
        prodList1.add(productDTO1);
        prodList1.add(productDTO2);
        prodList2.add(productDTO3);
        prodList2.add(productDTO4);

        SiteDocDTO site1 = new SiteDocDTO("Maze 3 TLV", "Osem", "Elan",
                "0501231234", new ArrayList<>(), Area_Enum.Center, SiteKind_Enum.SOURCE,
                true, 363, 0, driverDTO1, truckDTO1, prodList1);

        SiteDocDTO site2 = new SiteDocDTO("Mapo 30 TLV", "Snif TLV", "Oded",
                "0503145837", new ArrayList<>(), Area_Enum.Center, SiteKind_Enum.DESTINATION,
                true, 0, 1, driverDTO1, truckDTO1, prodList1);

        SiteDocDTO site3 = new SiteDocDTO("Bilo 3, Rehovot", "Coca Cola", "David",
                "0527563847", new ArrayList<>(), Area_Enum.Center, SiteKind_Enum.SOURCE,
                true, 300, 2, driverDTO2, truckDTO2, prodList2);

        SiteDocDTO site4 = new SiteDocDTO("Gamla 5, Rehovot", "Snif Rehovot", "Dvir",
                "0538263846", new ArrayList<>(), Area_Enum.Center, SiteKind_Enum.DESTINATION,
                true, 0, 3, driverDTO2, truckDTO2, prodList2);

        List<SiteDocDTO> siteList1 = new ArrayList<>();
        List<SiteDocDTO> siteList2 = new ArrayList<>();

        siteList1.add(site1);
        siteList1.add(site2);
        siteList2.add(site3);
        siteList2.add(site4);

        DeliveryDTO deliveryDTO1 = new DeliveryDTO(0, null, driverDTO1, truckDTO1, siteList1,
                DeliveryMode_Enum.Arrived);
        DeliveryDTO deliveryDTO2 = new DeliveryDTO(1, null, driverDTO2, truckDTO2, siteList2,
                DeliveryMode_Enum.Arrived);
        list.add(deliveryDTO1);
        list.add(deliveryDTO2);
        return list;
    }

    public void addSite(String name, String address, String area, String contactName, String contactPhone) throws Exception {
        transportController.addSite(name, address, area, contactName, contactPhone);
    }

    public Collection<Site> getAllSites() throws Exception {
        return transportController.getAllSites();
    }

    public Collection<Driver> getAllDriversForTruck(License_Enum license) throws Exception {
        return transportController.getAllDriversForTruck(license);
    }

    public SiteDocument getSiteDoc(String siteID) throws Exception {
        int delID = Integer.parseInt(siteID.split("_")[0]);
        int sitID = Integer.parseInt(siteID.split("_")[1]);
        SiteDocument siteDocument = getDelivery(delID).getSite(sitID);
        if(siteDocument == null)
            throw new Exception("there is no site with this id!");
        return siteDocument;
    }

    private void validateTruckDriver(Truck truck, Driver driver) throws Exception {
        */
/*if (driver.getLicense().compareTo(truck.getLicense()) > 0)*//*

        validateTruckDriverLicense(truck, driver);
        if (!driver.isAvailable()) {
            throw new Exception("The driver" + driver.getName() + "is already in a shipment");
        }
        if (!truck.isAvailable()) {
            throw new Exception("The truck" + truck.getLicense() + "is already in use");
        }
    }
*/
/*
private void validateTruckDriverLicense(Truck truck, Driver driver) throws Exception {
    if (!driver.getLicense().canIDriveIt(truck.getLicense())) {
        throw new Exception("driver license do not fit to the truck license");
    }
}
*/


}