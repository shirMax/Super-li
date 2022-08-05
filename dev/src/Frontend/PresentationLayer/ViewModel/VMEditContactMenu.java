package Frontend.PresentationLayer.ViewModel;

import Frontend.PresentationLayer.Model.MContact;
import Frontend.PresentationLayer.Model.MSupplier;
import Frontend.PresentationLayer.View.ContactsMenu;
import Frontend.PresentationLayer.View.EditContactMenu;
import Frontend.PresentationLayer.View.Menu;

import java.util.Scanner;

public class VMEditContactMenu {
    private MContact mContact;
    private MSupplier mSupplier;
    private Scanner scanner;

    public VMEditContactMenu(MContact mContact,MSupplier mSupplier){
        this.mContact = mContact;
        this.mSupplier = mSupplier;
        scanner = new Scanner(System.in);
    }

    public MSupplier getmSupplier() {
        return mSupplier;
    }

    public MContact getmContact() {
        return mContact;
    }

    public Menu editContactsName() {
        try {
            System.out.println("---------editing contact's name---------");
            System.out.println("please enter contact's new name");
            String name = scanner.nextLine();
            validateName(name);
            System.out.println(mContact.editContactsName(name,mSupplier.getSupplierID()));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new EditContactMenu(mSupplier, mContact);
    }

    public Menu editingContactsPhone() {
        try {
            System.out.println("---------editing contact's phone---------");
            System.out.println("please enter contact's new phone");
            String phone = scanner.nextLine();
            validatePhone(phone);
            System.out.println(mContact.editingContactsPhone(mSupplier.getSupplierID(),phone));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return new EditContactMenu(mSupplier, mContact);
    }

    private void validatePhone(String phone) throws Exception {
        if(phone.length()<=0)
            throw new Exception("phone is not valid");
    }
    private void validateName(String name) throws Exception {
        if(name.length()<=0)
            throw new Exception("name is not valid");
    }
    public Menu returnToContactMenu() {
        return new ContactsMenu(mSupplier);
    }
}
