package Frontend.PresentationLayer.DeliveriesModule;


import Backend.BusinessLayer.Deliveries.Enums.SiteKind_Enum;
import Backend.ServiceLayer.ObjectsDeliveries.DeliveryDataObj;
import Backend.ServiceLayer.ObjectsDeliveries.SiteDocDataObj;
import Backend.ServiceLayer.Response;
import Backend.ServiceLayer.ResponseT;
import Backend.ServiceLayer.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
public class ProductsAdder extends Manager {
    DeliveryDataObj deliveryObj;
    int destinationID;
    int sourceID;

    public ProductsAdder(Scanner scanner, Service service) {
        super(scanner, service);
        deliveryObj = null;
        sourceID = 0;
        destinationID = 0;
    }

    @Override
    public void act() {
        try {
            System.out.println("choose an option:");
/*
        System.out.println("Press A to add a product");
*/
            System.out.println("Press B to remove a product");
            System.out.println("Press C to update a product quantity");
/*
        System.out.println("Press D to remove site");
*/

            input = scanner.nextLine();
            while (!"ABCD".contains(input)) {
                if (input.equals("exit"))
                    return;
                input = scanner.nextLine();
            }
            String siteOption = input;
            int productID;


            Response res;
            switch (siteOption) {
/*
            case "A":
                System.out.println("Enter product catalog number :");
                productID = Integer.parseInt(scanner.nextLine());
                System.out.println("Enter product name");
                String productName = scanner.nextLine();
                System.out.println("Enter product quantity");
                int productQuantity = Integer.parseInt(scanner.nextLine());
*/
/*
                res = service.addProductToSite(deliveryObj.getId(),sourceDataObj.getID(),destDataObj.getID(),productID,productName,productQuantity);
*//*

                res = service.addProductToSite(deliveryObj.getId(), sourceID, destinationID, productID, productName, productQuantity);

                break;
*/

                case "B":
                    System.out.println("Enter product catalog number :");
                    productID = Integer.parseInt(scanner.nextLine());
                    res = service.updateProductQuantity(deliveryObj.getId(), sourceID, destinationID, productID, -1);
                    break;

                case "C":
                    System.out.println("Enter product catalog number :");
                    productID = Integer.parseInt(scanner.nextLine());
                    System.out.println("Enter product quantity");
                    int productQuantity2 = Integer.parseInt(scanner.nextLine());
                    res = service.updateProductQuantity(deliveryObj.getId(), sourceID, destinationID, productID, productQuantity2);
                    break;
/*

            case "D":
                System.out.println("Press 1 to remove the whole source");
                System.out.println("Press 2 to remove the chosen destination from the source");
                while (!"12".contains(input))
                    input = scanner.nextLine();
                int removeOption = Integer.parseInt(input);
                if (removeOption == 1) {
                    res = service.removeSite(deliveryObj.getId(), sourceID, -1);
                } else {
                    res = service.removeSite(deliveryObj.getId(), sourceID, destinationID);
                }

                break;
*/

                default:
                    System.out.println("Undefined option have chosen: " + siteOption);
                    return;
            }
            if (res.isErrorOccurred()) {
                System.out.println(res.errorMessage);
            } else System.out.println("Succeed!");
        } catch (Exception e) {
            if (!input.equals("exit"))
                System.out.println("invalid input! try again");
            return;
        }
    }

    public void setDeliveryObj(DeliveryDataObj dobj) {
        deliveryObj = dobj;
    }

    public boolean presentOptions(boolean both) {
        int idNum = 1;
        if (both) {/*
            System.out.println("adding new product - choose delivery:");
            ResponseT<List<DeliveryDataObj>> allDeliveries = service.getAllDeliveries(0);
            if (allDeliveries.isErrorOccurred()) {
                System.out.println(allDeliveries.errorMessage);
                return false;
            }
            List<DeliveryDataObj> deliveries = allDeliveries.Value;
            idNum = 1;
            for (DeliveryDataObj deliveryObj : deliveries) {
                System.out.println(idNum + "." + deliveryObj.toString());
                idNum++;
            }
            input = scanner.nextLine();

            while (input.length() == 0 || Integer.valueOf(input) > idNum)
                input = scanner.nextLine();*/
            DeliveryDataObj deliveryObj = choose(service.getAllDeliveries(0), "delivery");
        }
        System.out.println("choose the the number of site you want");
        deliveryObj.getSites();
        List<SiteDocDataObj> allSitesByDelivery = deliveryObj.getSites();
        List<SiteDocDataObj> allSources = new ArrayList<>();
        List<SiteDocDataObj> allDestinations = new ArrayList<>();

        for (SiteDocDataObj siteObj : allSitesByDelivery) {
            if (siteObj.getSiteKind().equals(SiteKind_Enum.SOURCE)) {
                allSources.add(siteObj);
            }
            if (siteObj.getSiteKind().equals(SiteKind_Enum.DESTINATION)) {
                allDestinations.add(siteObj);
            }
        }
/*

        int idSite = 1;
        for(SiteDocDataObj siteObj : allSources){
            System.out.println(idSite+"."+siteObj.toString());
            idSite++;
        }
        if(idSite == 1){
            System.out.println("No sources added to the site");
            return false;
        }
        input = scanner.nextLine();

        while (input.length() == 0 || Integer.valueOf(input)>idSite)
            input = scanner.nextLine();
        sourceID = allSources.get(Integer.parseInt(input)-1).getID();
*/
        sourceID = choose(new ResponseT<List<SiteDocDataObj>>(allSources), "source site").getID();
/*

        idSite = 1;
        for(SiteDocDataObj siteObj : allDestinations){
            System.out.println(idSite+"."+siteObj.toString());
            idSite++;
        }
        if(idSite == 1){
            System.out.println("No destinations added to the site");
            return false;
        }
        input = scanner.nextLine();

        while (input.length() == 0 || Integer.valueOf(input)>idSite)
            input = scanner.nextLine();
        destinationID = allDestinations.get(Integer.parseInt(input)-1).getID();
*/
        destinationID = choose(new ResponseT<List<SiteDocDataObj>>(allSources), "source site").getID();

        return true;
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
