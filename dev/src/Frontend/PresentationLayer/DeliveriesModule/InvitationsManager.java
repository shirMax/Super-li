package Frontend.PresentationLayer.DeliveriesModule;

import Backend.BusinessLayer.Deliveries.Enums.License_Enum;
import Backend.ServiceLayer.ObjectsDeliveries.DeliveryDataObj;
import Backend.ServiceLayer.ObjectsEmployees.DriverDataObj;
import Backend.ServiceLayer.ObjectsDeliveries.TruckDataObj;
import Backend.ServiceLayer.Response;
import Backend.ServiceLayer.ResponseT;
import Backend.ServiceLayer.Service;

import java.util.List;
import java.util.Scanner;

public class InvitationsManager extends Manager {

    public InvitationsManager(Scanner scanner, Service service) {
        super(scanner, service);
    }

    @Override
    public void act() {
        try {
            input = "y";
            while (!input.equals("exit")) {
                //System.out.println("press 1 to add a new delivery");
                System.out.println("press 1 to book pending delivery");
                System.out.println("press 2 to launch delivery");
                System.out.println("press 3 to manage invitation");

                System.out.println("**reminder: press exit to return to previous menu");
                input = scanner.nextLine();


                if (input.equals("1")) {
                    pendingDelivery();
/*
                inviteDelivery();
*/
                } else if (input.equals("2")) {
                    launchDelivery();
                } else if (input.equals("3")) {
                    ManageUpdates manageUpdates = new ManageUpdates(scanner, service);
                    manageUpdates.act(0);
                }


            }
        } catch (Exception e) {
            if (!input.equals("exit"))
                System.out.println("invalid input! try again");
            return;
        }
    }
/*

    private void inviteDelivery() {
        System.out.println("adding new delivery");
        ResponseT<List<TruckDataObj>> trucksRes = service.getAllTrucks();
        if (trucksRes.isErrorOccurred()) {
            System.out.println(trucksRes.errorMessage);
            return;
        }
        List<TruckDataObj> trucks = trucksRes.Value;
        System.out.println("choose truck:");
        for (int i = 0; i < trucks.size(); i++)
            System.out.println("press " + i + " for " + trucks.get(i).toString());
        input = scanner.nextLine();
        while (input.length() == 0 || Integer.valueOf(input) >= trucks.size())
            input = scanner.nextLine();
        TruckDataObj truck = trucks.get(Integer.valueOf(input));

*/
/*

            ResponseT<List<DriverDataObj>> driverRes = service.getAllDriversForTruck(
                    truck.getLicense());
            if (driverRes.errorOccured) {
                System.out.println(driverRes.errorMessage);
                return;
            }
            List<DriverDataObj> drivers = driverRes.Value;
            System.out.println("choose driver:");
            for (int i = 0; i < drivers.size(); i++)
                System.out.println("press " + i + " for " + drivers.get(i).toString());
            input = scanner.nextLine();
            while (input.length() == 0 || Integer.valueOf(input) >= drivers.size())
                input = scanner.nextLine();
            DriverDataObj driver = drivers.get(Integer.valueOf(input));
*//*


        System.out.println("enter delivery time. for example: 08/07/2022 13:30");
        String date = scanner.nextLine();


        ResponseT<List<DriverDataObj>> driverRes = service.getAvailableDriverByLicense(date, truck.getLicense());
        if (driverRes.isErrorOccurred()) {
            System.out.println(driverRes.errorMessage);
            return;
        }
        List<DriverDataObj> drivers = driverRes.Value;
        System.out.println("choose driver:");
        for (int i = 0; i < drivers.size(); i++)
            System.out.println("press " + i + " for " + drivers.get(i).toString());
        input = scanner.nextLine();
        while (input.length() == 0 || Integer.valueOf(input) >= drivers.size())
            input = scanner.nextLine();
        DriverDataObj driver = drivers.get(Integer.valueOf(input));

        ResponseT<Integer> response = service.inviteDelivery(date, driver.getId(), truck.getLicenseNumber());
        if (response.isErrorOccurred())
            System.out.println(response.errorMessage);
        else System.out.println("delivery number: " + response.Value + " added successfully");

        System.out.println("do you want to add another one? [y/n]");
        input = scanner.nextLine();
    }

    private void dateDelivery() {
        System.out.println("enter delivery id");
        int delId = Integer.parseInt(scanner.nextLine());
        System.out.println("enter delivery new date");
        String delDate = scanner.nextLine();
        Response response = service.changeDate(delId, delDate);
        if (response.isErrorOccurred())
            System.out.println(response.errorMessage);
        else System.out.println("delivery number: " + delId + " date changes successfully");
    }
*/

    private void launchDelivery() {
        DeliveryDataObj deliveryObj = choose(service.getAllDeliveries(0), "delivery");
        Response response = service.launchDelivery(deliveryObj.getId());
        if (response.isErrorOccurred())
            System.out.println(response.errorMessage);
        else System.out.println("delivery number: " + deliveryObj.getId() + " lunched successfully");
    }

    private void pendingDelivery() {
        try {
            DeliveryDataObj deliveryObj = choose(service.getAllDeliveries(4), "delivery");
            if (deliveryObj == null)
                return;
            TruckDataObj truck = choose(service.getAllTrucks(), "truck");
            if (truck == null)
                return;
            System.out.println("enter delivery time. for example: 08/07/2022 13:30");
            String date = scanner.nextLine();
            DriverDataObj driver = choose(service.getAvailableDriverByLicense(date, truck.getLicense()), "driver");
            if (driver == null) {
                leaveMessage(date, truck.getLicense());
                return;
            }
            ResponseT<Integer> response = service.bookDelivery(date, driver.getId(), truck.getLicenseNumber(), deliveryObj.getId());
            if (response.isErrorOccurred())
                System.out.println(response.errorMessage);
            else System.out.println("delivery number: " + response.Value + " added successfully");

            System.out.println("do you want to add another one? [y/n]");
            input = scanner.nextLine();
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

    private void leaveMessage(String date, License_Enum license) {
        System.out.println("do you want to leave a message to the HR manager " +
                "to add suitable employees to shift in this date? [y/n]");
        input = scanner.nextLine();
        while (!input.equals("y") && !input.equals("n"))
            input = scanner.nextLine();
        if (input.equals("y"))
            service.leaveHRMessage(date, license);
    }

}
