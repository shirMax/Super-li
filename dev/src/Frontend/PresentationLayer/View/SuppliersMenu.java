package Frontend.PresentationLayer.View;

import Frontend.PresentationLayer.ViewModel.VMOrderMenu;
import Frontend.PresentationLayer.ViewModel.VMSupplierMenu;

import java.util.Scanner;

public class SuppliersMenu implements Menu {
    private VMSupplierMenu vmSupplierMenu;
    private VMOrderMenu vmOrderMenu;

    public SuppliersMenu() {
        vmSupplierMenu = new VMSupplierMenu();
        vmOrderMenu = new VMOrderMenu();

    }

    @Override
    public Menu run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("---------suppliers menu---------");
        System.out.println("press 1 for add new supplier");
        System.out.println("press 2 for remove supplier");
        System.out.println("press 3 for enter supplier's profile");
        System.out.println("press 4 for display all suppliers");
        System.out.println("press 5 for orders menu");
        System.out.println("press 6 for suppliers main menu");
        System.out.println("****press -1 to exit****");
        String input = scanner.next();
        switch (input) {
            case "1":
                return vmSupplierMenu.addSupplier();
            case "2":
                return vmSupplierMenu.removeSupplier();
            case "3":
                return vmSupplierMenu.enterSuppliersProfile();
            case "4": {
                vmSupplierMenu.displayAllSuppliersSystem();
                break;
            }
            case "5":
                return new OrderMenu();
            case "6": return new MainMenu();
            case "-1": {
                MenuManager.stopRun = true;
                break;
            }
            default: System.out.println("invalid input");
        }
        return this;
    }
}
