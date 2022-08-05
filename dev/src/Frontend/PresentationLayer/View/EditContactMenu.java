package Frontend.PresentationLayer.View;

import Frontend.PresentationLayer.Model.MContact;
import Frontend.PresentationLayer.Model.MSupplier;
import Frontend.PresentationLayer.ViewModel.VMEditContactMenu;

import java.util.Scanner;

public class EditContactMenu implements Menu{
    VMEditContactMenu vmEditContactMenu;
    private Scanner scanner;
    public EditContactMenu(MSupplier mSupplier, MContact mContact){
        vmEditContactMenu = new VMEditContactMenu(mContact,mSupplier);
        scanner = new Scanner(System.in);
    }
    @Override
    public Menu run() {
        System.out.println("---------editing contact's details---------");
        System.out.println("press 1 for edit contact's name");
        System.out.println("press 2 for edit contact's phone");
        System.out.println("press 3 for main menu");
        System.out.println("press 4 for contact menu");
        System.out.println("****press -1 to exit****");
        String input = scanner.next();
        switch (input){
            case "1":
                return vmEditContactMenu.editContactsName();
            case "2":
                return vmEditContactMenu.editingContactsPhone();
            case "3":
                return new MainMenu();
            case "-1": {MenuManager.stopRun = true; break;}
            case "4":
                return vmEditContactMenu.returnToContactMenu();
            default: System.out.println("invalid input");
        }
        return this;
    }
}
