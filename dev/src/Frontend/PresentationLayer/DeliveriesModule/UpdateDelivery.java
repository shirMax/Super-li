package Frontend.PresentationLayer.DeliveriesModule;

import Backend.BusinessLayer.Tools.DateConvertor;
import Backend.ServiceLayer.ObjectsDeliveries.DeliveryDataObj;
import Backend.ServiceLayer.ObjectsEmployees.DriverDataObj;
import Backend.ServiceLayer.ObjectsDeliveries.SiteDocDataObj;
import Backend.ServiceLayer.ObjectsDeliveries.TruckDataObj;
import Backend.ServiceLayer.Response;
import Backend.ServiceLayer.ResponseT;
import Backend.ServiceLayer.Service;

import java.util.List;
import java.util.Scanner;

public class UpdateDelivery extends Manager {
    DeliveryDataObj deliveryObj;

    public UpdateDelivery(Scanner scanner, Service service) {
        super(scanner, service);
        deliveryObj = null;
    }

    @Override
    public void act() {

    }

    public void setDeliveryObj(DeliveryDataObj dobj) {
        deliveryObj = dobj;
    }

    public void manageDriverTrucks(boolean mode) {
        try {
            int siteID = -1;
            if (mode) {
                siteID = choose(new ResponseT<List<SiteDocDataObj>>(deliveryObj.getSites()), "site").getID();
/*
            System.out.println("choose the SiteID you want:");
            deliveryObj.getSites();
            List<SiteDocDataObj> allSitesByDelivery = deliveryObj.getSites();

            int idSite = 1;
            for(SiteDocDataObj siteObj : allSitesByDelivery){
                System.out.println(siteObj.toString());
                idSite++;

            }
            if(idSite == 1){
                System.out.println("No sites added to the delivery");
                return;
            }
            input = scanner.nextLine();

            while (input.length() == 0 || Integer.valueOf(input)>idSite)
                input = scanner.nextLine();
            siteID = Integer.parseInt(input);
*/
            }
/*
        ResponseT<List<TruckDataObj>> trucksRes= service.getAllTrucks();
        if(trucksRes.isErrorOccurred()) {
            System.out.println(trucksRes.errorMessage);
            return;
        }
        List<TruckDataObj> trucks = trucksRes.Value;
        System.out.println("choose truck:");
        for(int i=0; i<trucks.size(); i++)
            System.out.println("press " + i + " for " + trucks.get(i).toString());
        input = scanner.nextLine();
        while (input.length() == 0 || Integer.valueOf(input)>=trucks.size())
            input = scanner.nextLine();
        TruckDataObj truck = trucks.get(Integer.valueOf(input));*/
            TruckDataObj truck = choose(service.getAllTrucks(), "truck");
            DateConvertor dateConvertor = new DateConvertor();
            String date = dateConvertor.dateToString(deliveryObj.getDeliveryDate());
/*        ResponseT<List<DriverDataObj>> driverRes= service.getAvailableDriverByLicense(date, truck.getLicense());
        if(driverRes.isErrorOccurred()) {
            System.out.println(driverRes.errorMessage);
            return;
        }
        List<DriverDataObj> drivers = driverRes.Value;
        System.out.println("choose driver:");
        for(int i=0; i<drivers.size(); i++)
            System.out.println("press " + i + " for " + drivers.get(i).toString());
        input = scanner.nextLine();
        while (input.length() == 0 || Integer.valueOf(input)>=drivers.size())
            input = scanner.nextLine();
        DriverDataObj driver = drivers.get(Integer.valueOf(input));*/
            DriverDataObj driver = choose(service.getAvailableDriverByLicense(date, truck.getLicense()), "driver");

            Response response = service.changeTruckDriver(deliveryObj.getId(), siteID, truck.getLicenseNumber(), driver.getId());
            if (response.isErrorOccurred())
                System.out.println(response.errorMessage);
        } catch (Exception e) {
            if (!input.equals("exit"))
                System.out.println("invalid input! try again");
            return;
        }
    }

    private <T> T choose(ResponseT<List<T>> list, String name) {
        System.out.println("choose " + name + ":");
        if (list.isErrorOccurred()) {
            System.out.println(list.errorMessage);
            return null;
        }
        List<T> objList = list.Value;
        if (objList == null || objList.size() == 0) {
            System.out.println("there are no available " + name + "s");
            return null;
        }
        int idNum = 1;
        for (T obj : objList) {
            System.out.println(idNum + "." + obj.toString());
            idNum++;
        }
        input = scanner.nextLine();

        while (input.length() == 0 || Integer.valueOf(input) >= idNum || Integer.valueOf(input) < 1)
            input = scanner.nextLine();
        if (input.equals("exit"))
            return null;
        T obj = objList.get(Integer.parseInt(input) - 1);
        return obj;
    }

}
