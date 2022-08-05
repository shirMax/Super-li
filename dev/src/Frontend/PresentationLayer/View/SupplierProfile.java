package Frontend.PresentationLayer.View;

import Frontend.PresentationLayer.Model.MSupplier;
import Frontend.PresentationLayer.ViewModel.VMSupplierProfile;

import java.util.Scanner;

public class SupplierProfile implements Menu{
    private VMSupplierProfile vmSupplierProfile;
    public SupplierProfile(MSupplier mSupplier){
        vmSupplierProfile = new VMSupplierProfile(mSupplier);
    }

    @Override
    public Menu run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("---------supplier's profile---------");
        System.out.println("press 1 for contacts menu");
        System.out.println("press 2 for contract menu");
        System.out.println("press 3 for display the supplier's orders");
        System.out.println("press 4 for add order");
        System.out.println("press 5 for add items to order");
        System.out.println("press 6 for remove items to order");
        System.out.println("press 7 for suppliers main menu");
        System.out.println("****press -1 to exit****");
        String input = scanner.next();
        switch (input){
            case "1":
                return new ContactsMenu(vmSupplierProfile.getmSupplier());
            case "2":
                return new ContractMenu(vmSupplierProfile.getmSupplier());
            case "3":
                return vmSupplierProfile.displaySuppliersOrders();
            case "4":
                return vmSupplierProfile.addOrder();
            case "5": return vmSupplierProfile.addItemsToConstOrder();
            case "6": return vmSupplierProfile.removeItemsToConstOrder();
            case "7":
                return new MainMenu();
            case "-1": {
                MenuManager.stopRun = true;
                break;
            }
            default: System.out.println("invalid input");
        }
        return new SupplierProfile(vmSupplierProfile.getmSupplier());
    }
}

