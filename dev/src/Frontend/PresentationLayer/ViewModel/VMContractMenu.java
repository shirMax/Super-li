package Frontend.PresentationLayer.ViewModel;

import Frontend.PresentationLayer.Model.*;
import Frontend.PresentationLayer.View.ContractMenu;
import Frontend.PresentationLayer.View.Menu;
import Frontend.PresentationLayer.View.SupplierProfile;

import java.util.List;
import java.util.Scanner;

public class VMContractMenu implements Menu {
    private MSupplier mSupplier;
    private MContract mContract;
    private Scanner scanner;
    public VMContractMenu(MSupplier mSupplier) {
        try {
            this.mSupplier = mSupplier;
            this.mContract = mSupplier.getContract();
            scanner = new Scanner(System.in);
        }
        catch (Exception e){System.out.println(e.getMessage());}
    }

    @Override
    public Menu run() {
        return null;
    }

    public Menu addItemToTheContract() {
        try{
            System.out.println("---------add item---------");
            System.out.println("please enter item id");
            int itemid = Integer.parseInt(scanner.nextLine());
            validateID(itemid);
            System.out.println("please enter catalog id");
            int catalogid = Integer.parseInt(scanner.nextLine());
            validateID(catalogid);
            System.out.println("please enter price");
            double price = Double.parseDouble(scanner.nextLine());
            validatePrice(price);
            System.out.println(mContract.addItemToTheContract(mSupplier.getSupplierID(), itemid, catalogid, price));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return new ContractMenu(mSupplier);
    }
    private void validatePrice(double price) throws Exception {
        if(price<=0)
            throw new Exception("invalid price");
    }
    private void validateID(int id) throws Exception {
        if(id<=0)
            throw new Exception("id is not valid");
    }
    private void validateDiscount(int discount) throws Exception {
        if(discount<=0)
            throw new Exception("discount is not valid");
    }
    private void validateAmount(int amount) throws Exception {
        if(amount<=0)
            throw new Exception("amount is not valid");
    }
    private void validateName(String name) throws Exception {
        if(name.length()<=0)
            throw new Exception("name is not valid");
    }
    public List<MItem> displayingItemsPerSupplier() throws Exception {
        try {
            List<MItem> mitems = Controller.getInstance().getItemsForSupplier(mSupplier.getSupplierID());
            if(mitems.size() == 0)
                throw new Exception("there are no items in the system yet");
            for (MItem item : mitems)
                System.out.println(item);
            return mitems;
        }
        catch (Exception e) {
            throw e;
        }
    }

    public Menu removeItemFromTheContract() {
        try{
            System.out.println("---------remove item---------");
            System.out.println("please enter item's Id that you want to remove");
            List<MItem> mItems = displayingItemsPerSupplier();
            int itemID = Integer.parseInt(scanner.nextLine());
            checkIfItemExists(itemID, mItems);
            System.out.println(mContract.removeItemFromTheContract(mSupplier.getSupplierID(),itemID));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return new ContractMenu(mSupplier);
    }

    public Menu addDiscountToTheContract() {
        try{
            System.out.println("---------add discount for the contract---------");
            System.out.println("please enter item id from the items:");
            List<MItem> mItems = displayingItemsPerSupplier();
            int itemID = Integer.parseInt(scanner.nextLine());
            checkIfItemExists(itemID, mItems);
            System.out.println("please enter amount");
            int amount = Integer.parseInt(scanner.nextLine());
            validateAmount(amount);
            System.out.println("please enter discount");
            int discount = Integer.parseInt(scanner.nextLine());
            validateDiscount(discount);
            System.out.println(mContract.addDiscountToTheContract(mSupplier.getSupplierID(),itemID,amount,discount));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return new ContractMenu(mSupplier);
    }

    private void checkIfItemExists(int itemID, List<MItem> mItems) {
        for(MItem mItem : mItems)
            if(mItem.getItemID() == itemID)
                return;
        throw new IllegalArgumentException("item id does not exist!");
    }

    public Menu displayingContractsDetails() {
        try{
            displayingItemsPerSupplier();
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return new ContractMenu(mSupplier);
    }

    public Menu returnToSupplierMenu() {
        return new SupplierProfile(mSupplier);
    }
}
