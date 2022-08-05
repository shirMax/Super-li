package Frontend.PresentationLayer.View;

import Frontend.PresentationLayer.Model.MSupplier;
import Frontend.PresentationLayer.ViewModel.VMContractMenu;

import java.util.Scanner;

public class ContractMenu implements Menu {
    private VMContractMenu vmContractMenu;
    private Scanner scanner;

    public ContractMenu(MSupplier mSupplier){
        vmContractMenu = new VMContractMenu(mSupplier);
        scanner = new Scanner(System.in);
    }
    @Override
    public Menu run() {
        System.out.println("---------contract menu---------");
        System.out.println("press 1 for add item");
        System.out.println("press 2 for remove item");
        System.out.println("press 3 for add discount");
        System.out.println("press 4 for display contract");
        System.out.println("press 5 for suppliers main menu");
        System.out.println("press 6 for supplier menu");
        System.out.println("****press -1 to exit****");
        String input = scanner.next();
        switch (input) {
            case "1":
                return vmContractMenu.addItemToTheContract();
            case "2":
                return vmContractMenu.removeItemFromTheContract();
            case "3":
                return vmContractMenu.addDiscountToTheContract();
            case "4":
                return vmContractMenu.displayingContractsDetails();
            case "5":
                return new MainMenu();
            case "6":
                return vmContractMenu.returnToSupplierMenu();
            case "-1":
            {MenuManager.stopRun = true; break;}
            default: System.out.println("invalid input");
        }
        return this;
    }
}
