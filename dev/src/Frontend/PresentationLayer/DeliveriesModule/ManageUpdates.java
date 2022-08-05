package Frontend.PresentationLayer.DeliveriesModule;


import Backend.ServiceLayer.ObjectsDeliveries.DeliveryDataObj;
import Backend.ServiceLayer.Response;
import Backend.ServiceLayer.ResponseT;
import Backend.ServiceLayer.Service;

import java.util.List;
import java.util.Scanner;

public class ManageUpdates extends Manager {
    DeliveryDataObj deliveryObj;
    boolean reDesignMode;

    public ManageUpdates(Scanner scanner, Service service) {
        super(scanner, service);
        deliveryObj = null;
        reDesignMode = false;
    }

    public void setMode(int x) {
        if (x == 2)
            reDesignMode = true;
    }

    public void act(int mode) {
        setMode(mode);
        deliveryObj = choose(service.getAllDeliveries(mode), "delivery");
        act();
    }

    @Override
    public void act() {
        try {
            System.out.println("choose an option:");
            System.out.println("Press A to manage truck and drivers");
/*
        System.out.println("Press B to add sites");
*/
            System.out.println("Press B to remove delivery");
            System.out.println("Press C to manage products and remove sites");
            if (!reDesignMode)
                System.out.println("Press D to update delivery date");
            System.out.println("**reminder: press exit to return to previous menu");

            input = scanner.nextLine();
            while (!"ABC".contains(input)) {
                if (input.equals("exit"))
                    return;
                input = scanner.nextLine();
            }
            String siteOption = input;

            switch (siteOption) {
                case "A":
                    UpdateDelivery updateDelivery = new UpdateDelivery(scanner, service);
                    updateDelivery.setDeliveryObj(deliveryObj);
                    updateDelivery.manageDriverTrucks(reDesignMode);
                    break;

                case "B":
                    removeDelivery();
/*                SiteEditor siteEditor = new SiteEditor(scanner, service);
                siteEditor.presentOptions();
                siteEditor.act();*/
                    break;

                case "C":
                    ProductsAdder productsAdder = new ProductsAdder(scanner, service);
                    productsAdder.setDeliveryObj(deliveryObj);
                    if (productsAdder.presentOptions(false))
                        productsAdder.act();
                    System.out.println("*** Notify a visit once again to free the delivery back to the ways ***");
                    break;

                case "D":
                    if (!reDesignMode) {
                        dateDelivery();
                    }
                    break;
/*
            case "E":
                removeDelivery();
                break;
                */
            }
        } catch (Exception e) {
            if (!input.equals("exit"))
                System.out.println("invalid input! try again");
            return;
        }
    }

    private void removeDelivery() {
        Response res = service.removeDelivery(deliveryObj.getId());
        if (res.isErrorOccurred())
            System.out.println(res.errorMessage);
        else System.out.println("delivery number: " + deliveryObj.getId() + " removed successfully");
    }

    private void dateDelivery() {
        System.out.println("enter delivery new date");
        String delDate = scanner.nextLine();
        Response response = service.changeDate(deliveryObj.getId(), delDate);
        if (response.isErrorOccurred())
            System.out.println(response.errorMessage);
        else System.out.println("delivery number: " + deliveryObj.getId() + " date changes successfully");
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
