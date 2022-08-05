package Frontend.PresentationLayer.DeliveriesModule;

import Backend.BusinessLayer.Deliveries.Enums.License_Enum;
import Backend.BusinessLayer.Tools.Pair;
import Backend.ServiceLayer.ObjectsDeliveries.DeliveryDataObj;
import Backend.ServiceLayer.ObjectsDeliveries.TruckDataObj;
import Backend.ServiceLayer.ObjectsEmployees.DriverDataObj;
import Backend.ServiceLayer.Response;
import Backend.ServiceLayer.ResponseT;
import Backend.ServiceLayer.Service;

import java.util.List;
import java.util.Locale;
import java.util.Scanner;

public class PendingManager extends Manager {

    public PendingManager(Scanner scanner, Service service) {
        super(scanner, service);
    }

    @Override
    public void act() {
        input = "y";
        while (!input.equals("exit")) {
            System.out.println("press 1 to see pending deliveries");

            System.out.println("**reminder: press exit to return to previous menu");
            input = scanner.nextLine();


            if (input.equals("1")) {
                pendingDelivery();
            }
        }
    }


    private void pendingDelivery(){
        try {
            DeliveryDataObj deliveryObj = choose(service.getAllDeliveries(4), "delivery");
            if(deliveryObj == null)
                return;
            TruckDataObj truck = choose(service.getAllTrucks(), "truck");
            if(truck == null)
                return;
            System.out.println("enter delivery time. for example: 08/07/2022 13:30");
            String date = scanner.nextLine();
            DriverDataObj driver = choose(service.getAvailableDriverByLicense(date, truck.getLicense()), "driver");
            if(driver == null) {
                leaveMessage(date, truck.getLicense());
                return;
            }
            ResponseT<Integer> response = service.bookDelivery(date, driver.getId(), truck.getLicenseNumber(), deliveryObj.getId());
            if (response.isErrorOccurred())
                System.out.println(response.errorMessage);
            else System.out.println("delivery number: " + response.Value + " added successfully");

            System.out.println("do you want to add another one? [y/n]");
            input = scanner.nextLine();
        }
        catch (Exception e){
            if(!input.equals("exit"))
                System.out.println("invalid input! try again");
            return;
        }
    }

    private <T> T choose(ResponseT<List<T>> list, String name){
        System.out.println("choose " + name +":");
        if (list.isErrorOccurred()) {
            System.out.println(list.errorMessage);
            return null;
        }
        List<T> objList = list.Value;
        if(objList == null || objList.size() == 0){
            System.out.println("there are no available " + name + "s");
            return null;
        }
        int idNum = 1;
        for (T obj : objList) {
            System.out.println(idNum + "." + obj.toString());
            idNum++;
        }
        input = scanner.nextLine();

        while (input.length() == 0 || Integer.valueOf(input) >= idNum || Integer.valueOf(input)<1)
            input = scanner.nextLine();
        if (input.equals("exit"))
            return null;
        T obj = objList.get(Integer.parseInt(input) - 1);
        return obj;
    }

    private void leaveMessage(String date, License_Enum license){
        System.out.println("do you want to leave a message to the HR manager " +
                "to add suitable employees to shift in this date? [y/n]");
        input = scanner.nextLine();
        while (!input.equals("y") && !input.equals("n"))
            input = scanner.nextLine();
        if(input.equals("y"))
            service.leaveHRMessage(date, license);
    }

/*
    private int stringToNum(String input){
        try {
            if(input.equals("exit")){
                return -1;
            }
            int ret = Integer.parseInt(input);
            return ret;
        }
        catch (Exception e) {
            System.out.println("wrong field");
            return -1;
        }
    }
*/
}
