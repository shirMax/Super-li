package Frontend.PresentationLayer.DeliveriesModule;

import Backend.ServiceLayer.ObjectsDeliveries.DeliveryDataObj;
import Backend.ServiceLayer.ObjectsDeliveries.Formalable;
import Backend.ServiceLayer.ObjectsDeliveries.SiteDocDataObj;
import Backend.ServiceLayer.ResponseT;
import Backend.ServiceLayer.Service;

import java.util.List;
import java.util.Scanner;

public class DocumentsManager extends Manager {

    public DocumentsManager(Scanner scanner, Service service) {
        super(scanner, service);
    }

    @Override
    public void act() {
        try {

            input = "y";
            while (!input.equals("n")) {
                System.out.println("press 1 to produce site document");
                System.out.println("press 2 to produce delivery document");
                System.out.println("press 9 to exit");
                input = scanner.nextLine();
                if (input.equals("9")) {
                    return;
                }
                if (input.equals("1")) {

                    DeliveryDataObj deliveryObj = choose(service.getAllDeliveries(9), "delivery");
                    if (deliveryObj == null)
                        return;
                    SiteDocDataObj siteDocDataObj = choose(new ResponseT<List<SiteDocDataObj>>(deliveryObj.getSites()), "site");
                    if (siteDocDataObj == null)
                        return;
                    System.out.println(siteDocDataObj.toString());
/*                System.out.println("enter side id");
                input = scanner.nextLine();
                ResponseT<SiteDocDataObj> res= service.getSiteDoc(input);
                if(res.isErrorOccurred()) {
                    System.out.println(res.errorMessage);
                    return;
                }
                System.out.println(res.Value.toString());*/
                } else if (input.equals("2")) {
                    System.out.println("press 0 for invited");
                    System.out.println("press 1 for on the way");
                    System.out.println("press 2 for redesigned");
                    System.out.println("press 3 for arrived");
                    System.out.println("press 9 for all deliveries");
                    input = scanner.nextLine();
                    ResponseT<List<DeliveryDataObj>> res = service.getAllDeliveries(Integer.parseInt(input));

                    if (res.isErrorOccurred()) {
                        System.out.println(res.errorMessage);
                        return;
                    }
                    for (DeliveryDataObj ddo : res.Value)
                        System.out.println(ddo.toString());
                }
            }
        } catch (Exception e) {
            if (!input.equals("exit"))
                System.out.println("invalid input! try again");
            return;
        }
    }

    private <T extends Formalable> T choose(ResponseT<List<T>> list, String name) {
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
            System.out.println(idNum + "." + obj.toFormal());
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
