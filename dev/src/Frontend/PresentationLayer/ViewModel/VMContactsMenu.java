package Frontend.PresentationLayer.ViewModel;

import Frontend.PresentationLayer.Model.MContact;
import Frontend.PresentationLayer.Model.MItem;
import Frontend.PresentationLayer.Model.MSupplier;
import Frontend.PresentationLayer.View.ContactsMenu;
import Frontend.PresentationLayer.View.EditContactMenu;
import Frontend.PresentationLayer.View.Menu;

import javax.naming.ldap.HasControls;
import java.util.List;
import java.util.Scanner;

public class VMContactsMenu {
    private MSupplier mSupplier;
    private Scanner scanner;
    public VMContactsMenu(MSupplier mSupplier){
        this.mSupplier = mSupplier;
        scanner = new Scanner(System.in);
    }

    public Menu addContact() {
        try{
            System.out.println("---------adding new contact---------");
            System.out.println("please enter contact's Id");
            int contactId = Integer.parseInt(scanner.nextLine());
            validateID(contactId);
            System.out.println("please enter contact's name");
            String contactName = scanner.nextLine();
            validateName(contactName);
            System.out.println("please enter contact's phone");
            String contactPhone = scanner.nextLine();
            validatePhone(contactPhone);
            System.out.println(mSupplier.addContact(contactId,contactName,contactPhone));
        }
        catch (Exception e){
            System.out.println(e.getMessage());
        }
        return new ContactsMenu(mSupplier);
    }
    public List<MContact> displayContacts() throws Exception {
        List<MContact> mContactList = mSupplier.getContacts();
        if(mContactList.isEmpty())
            throw new Exception("there are no contacts");
        for(MContact mContact : mContactList)
            System.out.println(mContact);
        return mContactList;
    }
    private void validateID(int id) throws Exception {
        if(id<=0)
            throw new Exception("id is not valid");
    }
    private void validatePhone(String phone) throws Exception {
        if(phone.length()<=0)
            throw new Exception("phone is not valid");
    }
    private void validateName(String name) throws Exception {
        if(name.length()<=0)
            throw new Exception("name is not valid");
    }
    public Menu removeContact() {
        try {
            System.out.println("---------removing contact---------");
            System.out.println("please enter contact's Id that you want to remove:");
            List<MContact> mContactList = displayContacts();
            int contactId = Integer.parseInt(scanner.nextLine());
            checkIfIdInList(contactId, mContactList);
            System.out.println(mSupplier.removeContact(contactId));
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ContactsMenu(mSupplier);
    }

    private void checkIfIdInList(int id, List<MContact> contacts) {
        for(MContact contact : contacts)
            if(id == contact.getContactID())
                return;
        throw new IllegalArgumentException("id does not exist");
    }

    public Menu editContactDetails() {
        try{
            System.out.println("---------editing contact's details---------");
            System.out.println("please enter contact's Id that you want to edit:");
            List<MContact> mContactList = displayContacts();
            int id = Integer.parseInt(scanner.nextLine());
            for(MContact mContact : mContactList)
                if(id == mContact.getContactID())
                  return new EditContactMenu(mSupplier, mContact);
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
        return new ContactsMenu(mSupplier);
    }

    public MSupplier getmSupplier() {
        return mSupplier;
    }
}
