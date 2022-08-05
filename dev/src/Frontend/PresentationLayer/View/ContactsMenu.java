package Frontend.PresentationLayer.View;

import Frontend.PresentationLayer.Model.MSupplier;
import Frontend.PresentationLayer.ViewModel.VMContactsMenu;

import java.util.Scanner;

public class
ContactsMenu implements Menu{
    private VMContactsMenu vmContactsMenu;

    public ContactsMenu(MSupplier mSupplier) {
        vmContactsMenu = new VMContactsMenu(mSupplier);
    }

    @Override
    public Menu run() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("---------contacts menu---------");
        System.out.println("press 1 for add contact");
        System.out.println("press 2 for remove contact");
        System.out.println("press 3 for edit contact's details");
        System.out.println("press 4 for suppliers main menu");
        System.out.println("press 5 for supplier profile");
        System.out.println("****press -1 to exit****");
        String input = scanner.next();
        switch (input){
            case "1":
                return vmContactsMenu.addContact();
            case "2":
                return vmContactsMenu.removeContact();
            case "3":
                return vmContactsMenu.editContactDetails();
            case "4":
                return new MainMenu();
            case "5": return new SupplierProfile(vmContactsMenu.getmSupplier());
            case "-1": {MenuManager.stopRun = true; break;}
            default: System.out.println("invalid input");
        }
        return this;
    }
}
