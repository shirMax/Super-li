/*
package Backend.ServiceLayer.ObjectsDeliveries;

import Backend.BusinessLayer.Deliveries.Enums.Area_Enum;

public class SiteDataObj implements Formalable {

    private String address;
    private String name;
    private String contactName;
    private String contactPhone;
    private Area_Enum area;

    public SiteDataObj(String address, String name, String contactName, String contactPhone, Area_Enum area) {
        this.address = address;
        this.name = name;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.area = area;
    }

    public SiteDataObj(Site site) {
        this.address = site.getAddress();
        this.name = site.getName();
        this.contactName = site.getContactName();
        this.contactPhone = site.getContactPhone();
        this.area = site.getArea();
    }

    public String getAddress() {
        return address;
    }

    public String getName() {
        return name;
    }

    public String getContactName() {
        return contactName;
    }

    public String getContactPhone() {
        return contactPhone;
    }

    public Area_Enum getArea() {
        return area;
    }

    public String toString(){
        return " address: " + address +
                " name: " + name +
                " contact name: " + contactName +
                " contact phone: " + contactPhone +
                " area: " + area.toString();
    }

    public String toFormal(){
        return " address: " + address +
                " name: " + name;
    }
}
*/
