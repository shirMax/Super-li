package Frontend.PresentationLayer.ViewModel;

import Frontend.PresentationLayer.Model.Controller;
import Frontend.PresentationLayer.Model.MOrder;
import Frontend.PresentationLayer.Model.MSupplier;
import Frontend.PresentationLayer.View.ContractMenu;
import Frontend.PresentationLayer.View.Menu;
import Frontend.PresentationLayer.View.SupplierProfile;
import Frontend.PresentationLayer.View.SuppliersMenu;
import sun.applet.resources.MsgAppletViewer;

import java.util.List;
import java.util.Scanner;

public class VMSupplierMenu {
    Scanner scanner;

    public VMSupplierMenu() {
        scanner = new Scanner(System.in);
    }

    public List<MSupplier> displayAllSuppliers() throws Exception {
        try {
            List<MSupplier> supplierList = Controller.getInstance().getSuppliers();
            if(supplierList.isEmpty())
                throw new Exception("There are no suppliers yet");
            for(MSupplier supplier : supplierList)
                System.out.println(supplier);
            return supplierList;
        }
        catch (Exception e) {
            throw e;
        }
    }
    public List<MSupplier> displayAllSuppliersSystem(){
        try {
            List<MSupplier> mSuppliers = Controller.getInstance().getSuppliers();
            for(MSupplier mSupplier : mSuppliers)
                System.out.println(mSupplier);
            return mSuppliers;
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return null;
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
    private void validateBank(int bankAccount) throws Exception {
        if(bankAccount<=0)
            throw new Exception("bank account is not valid");
    }

    public Menu addSupplier() {
        try{
            System.out.println("---------adding new supplier---------");
            System.out.println("please enter supplier's Id");
            int id = Integer.parseInt(scanner.nextLine());
            validateID(id);
            System.out.println("please enter supplier's name");
            String supplierName = scanner.nextLine();
            validateName(supplierName);
            System.out.println("please enter supplier's bank account");
            int bankAccount = Integer.parseInt(scanner.nextLine());
            validateBank(bankAccount);
            System.out.println("please enter supplier's payment : 1 for BankTransfer or 2 for Cash");
            int payment = Integer.parseInt(scanner.nextLine());
            String pay = "";
            switch (payment){
                case 1: {pay = "BankTransfer"; break;}
                case 2: {pay = "Cash";break;}
                default: System.out.println("there is no payment like this");
            }
            System.out.println("please enter supplier's type delivery : 1 for collect 2 for order and 3 for const delivery");
            int typeDelivery = Integer.parseInt(scanner.nextLine());
            String td = "";
            switch (typeDelivery){
                case 1: {td = "Collect"; break;}
                case 2: {td = "Order"; break;}
                case 3: {td = "Const"; break;}
                default: System.out.println("there is no type delivery like this");
            }
            System.out.println("please enter supplier's address");
            String supplierAddress = scanner.nextLine();
            System.out.println("please enter area delivery : 1 for North 2 for Center and 3 for South delivery");
            int area = Integer.parseInt(scanner.nextLine());
            String areaS = "";
            switch (area){
                case 1: {areaS = "North"; break;}
                case 2: {areaS = "Center"; break;}
                case 3: {areaS = "South"; break;}
                default: System.out.println("there is no area like this");
            }
            MSupplier mSupplier = new MSupplier(id,bankAccount,MSupplier.TypeDelivery.valueOf(td).toString(),MSupplier.Payment.valueOf(pay).toString(),supplierName,supplierAddress,areaS);
            System.out.println(Controller.getInstance().addSupplier(id,bankAccount,pay,td,supplierName,supplierAddress,areaS));
            return new ContractMenu(mSupplier);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return new SuppliersMenu();
        }
    }

    private void checkIfSupplierExists(int supplierID, List<MSupplier> mSuppliers) {
        for(MSupplier mSupplier : mSuppliers)
            if(mSupplier.getSupplierID() == supplierID)
                return;
        throw new IllegalArgumentException("supplier id does not exist!");
    }
    public Menu removeSupplier() {
        try {
            System.out.println("---------removing supplier---------");
            System.out.println("please enter supplier's Id that you want to remove:");
            List<MSupplier> mSuppliers = displayAllSuppliers();
            int supplierId = Integer.parseInt(scanner.nextLine());
            checkIfSupplierExists(supplierId, mSuppliers);
            System.out.println(Controller.getInstance().removeSupplier(supplierId));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return new SuppliersMenu();
    }

    public Menu enterSuppliersProfile() {
        try {
            System.out.println("choose a supplier that you want to enter his profile:");
            List<MSupplier> mSuppliers = displayAllSuppliers();
            int supplierID = Integer.parseInt(scanner.nextLine());
            checkIfSupplierExists(supplierID, mSuppliers);
            MSupplier mSupplier = Controller.getInstance().getSupplier(supplierID);
            return new SupplierProfile(mSupplier);
        }
        catch (Exception e){
            System.out.println(e.getMessage());
            return new SuppliersMenu();
        }
    }


}
