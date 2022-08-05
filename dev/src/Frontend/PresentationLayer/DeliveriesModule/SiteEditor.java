/*
package Frontend.PresentationLayer.DeliveriesModule;

import Backend.ServiceLayer.ObjectsDeliveries.DeliveryDataObj;
import Backend.ServiceLayer.ObjectsDeliveries.SiteDataObj;
import Backend.ServiceLayer.ResponseT;
import Backend.ServiceLayer.Service;

import java.util.List;
import java.util.Scanner;

public class SiteEditor extends Manager {
    DeliveryDataObj deliveryObj;
    public SiteEditor(Scanner scanner, Service service) {
        super(scanner, service);
        deliveryObj = null;
    }

    @Override
    public void act() {
//        System.out.println("enter site name");
//        String siteName=scanner.nextLine();
//
//        System.out.println("enter site address");
//        String siteAddress = scanner.nextLine();
//
//        System.out.println("enter contact person name");
//        String contactPerson=scanner.nextLine();
//
//        System.out.println("enter site contact person phone");
//        String contactPhone = scanner.nextLine();

        String siteName = choseSite();

        System.out.println("choose site kind : choose 1 for Source and 2 for Destination");
        input = scanner.nextLine();
        int siteKind = 0;
        String kindS = null;
        while (!"12".contains(input)){
            input = scanner.nextLine();
        }
        siteKind = Integer.parseInt(input);

        if(siteKind == 1)
             kindS = "SOURCE";
        else if(siteKind == 2) kindS = "DESTINATION";

//        System.out.println("enter area");
//        String areaKind = scanner.nextLine();
        ResponseT<Integer> response = service.addSiteDoc(deliveryObj.getId(),siteName,kindS);
        if (response.isErrorOccurred())
            System.out.println(response.errorMessage);
        else System.out.println("site number: " +deliveryObj.getId()+"_" +response.Value + " added successfully");

    }

    public void presentOptions() {
        System.out.println("adding new site");
        System.out.println("choose delivery you want to add a site to");
        ResponseT<List<DeliveryDataObj>>  allDeliveries = service.getAllDeliveries(0);
        if (allDeliveries.isErrorOccurred()){
            System.out.println(allDeliveries.errorMessage);
            return;
        }
        List<DeliveryDataObj> deliveries = allDeliveries.Value;
        int idNum = 1;
        for(DeliveryDataObj deliveryObj : deliveries){
            System.out.println(idNum + "." + deliveryObj.toString());
            idNum++;
        }
        input = scanner.nextLine();

        while (input.length() == 0 || Integer.valueOf(input)>idNum)
            input = scanner.nextLine();
         deliveryObj = deliveries.get(Integer.valueOf(input)-1);
    }

    public String choseSite(){
        System.out.println("choose site");
        ResponseT<List<SiteDataObj>> sitesRes = service.getAllSites();
        if (sitesRes.isErrorOccurred()){
            System.out.println(sitesRes.errorMessage);
            return "";
        }
        List<SiteDataObj> sites = sitesRes.Value;
        int idNum = 1;
        for(SiteDataObj siteDataObj : sites){
            System.out.println(idNum + "." + siteDataObj.toString());
            idNum++;
        }
        input = scanner.nextLine();

        while (input.length() == 0 || Integer.valueOf(input)>idNum)
            input = scanner.nextLine();
        return sites.get(Integer.valueOf(input)-1).getName();
    }
}
*/
