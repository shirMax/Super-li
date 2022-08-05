package Frontend.PresentationLayer.DeliveriesModule;

import Backend.ServiceLayer.ObjectsDeliveries.DeliveryDataObj;
import Backend.ServiceLayer.ObjectsDeliveries.SiteDocDataObj;
import Backend.ServiceLayer.Response;
import Backend.ServiceLayer.ResponseT;
import Backend.ServiceLayer.Service;

import java.util.List;
import java.util.Scanner;

public class VisitSiteManater  extends Manager {

    public VisitSiteManater(Scanner scanner, Service service) {
        super(scanner, service);
    }

    @Override
    public void act() {
        try {
            DeliveryDataObj deliveryObj = choose(service.getAllDeliveries(1), "delivery");
            if (deliveryObj == null)
                return;
            SiteDocDataObj siteDocDataObj = choose(new ResponseT<List<SiteDocDataObj>>(deliveryObj.getUnvisitedSites()), "site");
            if (siteDocDataObj == null)
                return;


            System.out.println("enter the weight of the truck \n" +
                    "if there is no weight to add enter 0");
            int weight = Integer.parseInt(scanner.nextLine());
            Response res = service.informSiteVisit(deliveryObj.getId(), siteDocDataObj.getID(), weight);
            if (res.isErrorOccurred())
                System.out.println(res.errorMessage);
            else System.out.println("the site has been successfully visited!");

/*        System.out.println("enter site id you are visiting in");
        input = scanner.nextLine();
        ResponseT<SiteDocDataObj> siteRes = service.getSiteDoc(input);
        if (siteRes.isErrorOccurred()) {
            System.out.println(siteRes.errorMessage);
            return;
        }
        SiteDocDataObj site = siteRes.Value;
        if(site == null){
            System.out.println("there is no site with this id!");
            return;
        }
        System.out.println(site.toString());
        int delID = Integer.parseInt(input.split("_")[0]);
        int siteID = Integer.parseInt(input.split("_")[1]);
        System.out.println("enter the weight of the truck \n" +
                "if there is no weight to add enter 0");
        int weight = Integer.parseInt(scanner.nextLine());
        Response res = service.informSiteVisit(delID, siteID, weight);
        if (res.isErrorOccurred())
            System.out.println(res.errorMessage);
        else System.out.println("the site has been successfully visited!");*/
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
