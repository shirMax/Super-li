package Frontend.PresentationLayer.Model;

import java.time.DayOfWeek;
import java.sql.Date;
import java.util.HashMap;
import java.util.List;

public class MSupplier {
    public enum TypeDelivery{Collect,Order,Const}
    public enum Payment{Cash,BankTransfer}
    private int supplierID;
    private int bankAccount;
    private TypeDelivery typeDelivery;
    private Payment payment;
    private String supplierName;
    private String supplierAddress;
    private String area;

    public MSupplier(int supplierID, int bankAccount, String typeDelivery, String payment, String supplierName,String supplierAddress, String area) {
        this.supplierID = supplierID;
        this.bankAccount = bankAccount;
        this.typeDelivery = TypeDelivery.valueOf(typeDelivery);
        this.payment = Payment.valueOf(payment);
        this.supplierName = supplierName;
        this.supplierAddress = supplierAddress;
        this.area = area;
    }

    public String addOrderConst(List<DayOfWeek> dayOfWeeks,HashMap<Integer,Integer> items,String address ) throws Exception {
        MOrderConst responseOrderConst = Controller.getInstance().addOrderConst(supplierID,dayOfWeeks, items,address);
        return "add order succeeded! the price is " + responseOrderConst.getPriceAfterDiscount() + " Shekel";
    }

    public String addOrderCollectOrder(Date current, HashMap<Integer,Integer> items,String address, Date possibleSupplyDates) throws Exception {
        MOrderCollect responseOrderCollect = Controller.getInstance().addOrderCollect(supplierID, current, items,address, possibleSupplyDates);
        return "add order succeeded! the price is " + responseOrderCollect.getPriceAfterDiscount() + " Shekel";
    }

    public List<MOrder> displaySupplierOrders() throws Exception {
        return Controller.getInstance().getOrdersFromSupplier(supplierID, typeDelivery);
    }
    public List<MOrder> displayNotArrivedSupplierOrders() throws Exception {
        return Controller.getInstance().getNotArrivedOrdersFromSupplier(supplierID);
    }

    public String addContact(int contactID, String contactName, String contactPhone) throws Exception {
        String addContactRes = Controller.getInstance().addContact(supplierID,contactID,contactName,contactPhone);
        return addContactRes;
    }

    public String removeContact(int contactID) throws Exception {
        String removeContactRes = Controller.getInstance().removeContact(supplierID,contactID);
        return removeContactRes;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public MContract getContract() throws Exception {
        return Controller.getInstance().getContract(supplierID);
    }

    public List<MContact> getContacts() throws Exception {
        return Controller.getInstance().getContacts(supplierID);
    }

    public MSupplier.TypeDelivery getTypeDelivery() {
        return typeDelivery;
    }

    public void checkIfConst() throws Exception {
        if(typeDelivery != TypeDelivery.Const)
            throw new Exception("the supplier does not supply in constant days");
    }

    @Override
    public String toString() {
        return "-" +
                "supplierID: " + supplierID +
                ", bankAccount: " + bankAccount +
                ", typeDelivery: " + typeDelivery +
                ", payment: " + payment +
                ", supplierAddress: " + supplierAddress +
                ", supplierName: '" + supplierName + '\'';
    }
}
