package Frontend.PresentationLayer.DeliveriesModule;


import Backend.PersistenceLayer.CreateDB;
import Backend.PersistenceLayer.DeliveriesDal.CreateDBDeliveries;
import Backend.ServiceLayer.ObjectsDeliveries.ServiceDelivery;
import Backend.ServiceLayer.Service;

import java.util.Scanner;

public class DeliveryMainConsole {
    private Scanner scanner;
    private Service service;

    public DeliveryMainConsole(Scanner scanner, Service service) {
        this.service = service;
        this.scanner = scanner;
    }

    public void run() {
        String input = "";
        try {

/*        CreateDB createDBDeliveries = new CreateDBDeliveries();
        createDBDeliveries.createFileIfNotExists();*/
            //Service service = new Service();
            //ServiceDelivery serviceDelivery = new ServiceDelivery();
            PoolManager poolManager = new PoolManager(scanner, service);
            InvitationsManager invitationsManager = new InvitationsManager(scanner, service);
            DocumentsManager documentsManager = new DocumentsManager(scanner, service);
            VisitSiteManater visitSiteManater = new VisitSiteManater(scanner, service);
            PendingManager pendingManager = new PendingManager(scanner, service);


            while (!input.equals("exit")) {
                System.out.println("press 1 to arrange an invitations");
                System.out.println("press 2 to arrange trucks");
//            System.out.println("press 2 to arrange trucks/* and sites pool*/");
                System.out.println("press 3 to update a visit");
                System.out.println("press 4 to arrange a documents");
                System.out.println("press 5 to arrange an redesigning deliveries");
                //System.out.println("press 6 to arrange an pending deliveries");
                input = scanner.nextLine();
                if (input.equals("1")) {
                    invitationsManager.act();
                } else if (input.equals("2")) {
                    poolManager.act();
                } else if (input.equals("3")) {
                    visitSiteManater.act();
                } else if (input.equals("4")) {
                    documentsManager.act();
                } else if (input.equals("5")) {
                    ManageUpdates manageUpdates = new ManageUpdates(scanner, service);
                    manageUpdates.act(2);
                }/* else if (input.equals("6")) {
                    pendingManager.act();
                }*/

            }
        } catch (Exception e) {
            if (!input.equals("exit"))
                System.out.println("invalid input! try again");
            return;
        }
    }

    public void docRun() {
        String input = "";
        DocumentsManager documentsManager = new DocumentsManager(scanner, service);
        documentsManager.act();
    }


}
