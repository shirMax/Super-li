package Frontend.PresentationLayer;

import Frontend.PresentationLayer.DeliveriesModule.DeliveryMainConsole;
import Frontend.PresentationLayer.Employees.EmployeesConsole;
import Backend.ServiceLayer.Service;

import java.util.Scanner;

public class Console {
    private static Scanner scanner;
    private static Service service;

    public static void main(String[] args) throws Exception {
        scanner = new Scanner(System.in);
        service = new Service();
        service.initial();
        EmployeesConsole employeesConsole =new EmployeesConsole(scanner, service);
        DeliveryMainConsole deliveryMainConsole = new DeliveryMainConsole(scanner, service);
        System.out.println("do you want to load data? [y/n]");
        String load = scanner.nextLine();
        while (!"yn".contains(load)){
            load = scanner.nextLine();
        }
        if(load.equals("y"))
            service.loadData();
        String input = "";
        while (!input.equals("exit")){
            System.out.println("enter 1 for deliveries system");
            System.out.println("enter 2 for employee and shift system");
            input = scanner.nextLine();
            if(input.equals("1"))
                deliveryMainConsole.run();
            else if(input.equals("2"))
                employeesConsole.run();
        }
    }
}

