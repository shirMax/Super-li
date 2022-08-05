package Frontend.PresentationLayer.DeliveriesModule;

import Backend.ServiceLayer.Response;
import Backend.ServiceLayer.Service;

import java.util.Scanner;

public class PoolManager extends Manager {

    public PoolManager(Scanner scanner, Service service) {
        super(scanner, service);
    }

    @Override
    public void act() {
        input = "y";
        try {
            while (!input.equals("n")) {
                System.out.println("press 1 to add new truck");
                //System.out.println("press 2 to add new site");
                System.out.println("**reminder: press exit to return to previous menu");
                input = scanner.nextLine();
                if (input.equals("exit"))
                    return;
//            if (input.equals("1")) {
//                System.out.println("adding new driver");
//                System.out.println("enter driver name");
//                String name = scanner.nextLine();
//                System.out.println("enter driver id");
//                int id = Integer.valueOf(scanner.nextLine());
//                System.out.println("enter driver license");
//                String license = scanner.nextLine();
//                Response response = service.addDriver(name, license, id);
//                if (response.errorOccured)
//                    System.out.println(response.errorMessage);
//                else System.out.println("driver " + name + " added successfully");
//            }

                if (input.equals("1")) {
                    System.out.println("adding new truck");/*
                    System.out.println("enter truck number");
                        int num = Integer.valueOf(scanner.nextLine());*/
                    int num = checkField("truck number");
                    int weight = checkField("truck weight");
                    int maxWeight = checkField("truck max weight");
/*

                    System.out.println("enter truck weight");
                    int weight = Integer.valueOf(scanner.nextLine());
                    System.out.println("enter truck max weight");
                    int maxWeight = Integer.valueOf(scanner.nextLine());
*/
                    System.out.println("enter truck license");
                    String license = scanner.nextLine();
                    Response response = service.addTruck(num, license, weight, maxWeight);
                    if (response.isErrorOccurred())
                        System.out.println(response.errorMessage);
                    else System.out.println("truck " + num + " added successfully");

                }
                /*
                else if (input.equals("2")) {
                    System.out.println("adding new site");
                    System.out.println("enter site name");
                    String name = scanner.nextLine();
                    System.out.println("enter site address");
                    String address = scanner.nextLine();
                    System.out.println("enter site area");
                    String area = scanner.nextLine();
                    System.out.println("enter site contact name");
                    String contactName = scanner.nextLine();
                    System.out.println("enter site contact phone");
                    String contactPhone = scanner.nextLine();
                    Response response = service.addSite(name, address, area, contactName, contactPhone);
                    if (response.isErrorOccurred())
                        System.out.println(response.errorMessage);
                    else System.out.println("site " + name + " added successfully");
                }
                */
                else return;
                System.out.println("do you want to add another one? [y/n]");
                input = scanner.nextLine();
            }
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private int checkField(String name) throws Exception {
        int num;
        System.out.println("enter " + name);
        String input = "";
        while (!input.contains("exit")) {
            try {
                input = scanner.nextLine();
                num = Integer.valueOf(input);
                if (num < 0)
                    throw new Exception();
                return num;
            } catch (Exception e) {
                System.out.println("you should enter non negative number. try again");
            }
        }
        throw new Exception();
    }
}
