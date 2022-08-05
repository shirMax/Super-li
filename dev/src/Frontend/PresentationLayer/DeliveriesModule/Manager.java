package Frontend.PresentationLayer.DeliveriesModule;


import Backend.ServiceLayer.Service;

import java.util.Scanner;

public abstract class Manager {
    protected Scanner scanner;
    protected Service service;
    protected String input;

    public Manager(Scanner scanner, Service service) {
        this.scanner = scanner;
        this.service = service;
        this.input = "";
    }

    public abstract void act();

}
