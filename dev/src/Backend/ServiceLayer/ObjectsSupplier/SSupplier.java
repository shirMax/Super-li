package Backend.ServiceLayer.ObjectsSupplier;
import Backend.BusinessLayer.Deliveries.Enums.Area_Enum;
import Backend.BusinessLayer.Suppliers.*;
import Backend.BusinessLayer.Suppliers.Supplier.typeDelivery;
import Backend.BusinessLayer.Suppliers.Supplier.payment;


import java.util.HashMap;


public class SSupplier {
    private int supplierID;
    private int bankAccount;
    private typeDelivery typeDelivery;
    private payment payment;
    private String supplierName;
    private String supplierAddress;
    private Area_Enum area;

    public SSupplier(int supplierID, int bankAccount, typeDelivery typeDelivery, payment payment,String supplierName,String supplierAddress, Area_Enum area_enum) {
        this.supplierID = supplierID;
        this.bankAccount = bankAccount;
        this.typeDelivery = typeDelivery;
        this.payment = payment;
        this.supplierName = supplierName;
        this.supplierAddress = supplierAddress;
        this.area = area_enum;
    }

    public Area_Enum getArea() {
        return area;
    }

    public int getSupplierID() {
        return supplierID;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public typeDelivery getTypeDelivery() {
        return typeDelivery;
    }

    public Supplier.payment getPayment() {
        return payment;
    }

    public int getBankAccount() {
        return bankAccount;
    }

    public String getSupplierAddress() {
        return supplierAddress;
    }
}
