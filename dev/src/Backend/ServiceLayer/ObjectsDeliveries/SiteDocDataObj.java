package Backend.ServiceLayer.ObjectsDeliveries;

import Backend.BusinessLayer.Deliveries.Enums.Area_Enum;
import Backend.BusinessLayer.Deliveries.Enums.SiteKind_Enum;
import Backend.BusinessLayer.Deliveries.Product;
import Backend.BusinessLayer.Deliveries.SiteDocument;
import Backend.ServiceLayer.ObjectsEmployees.DriverDataObj;

import java.util.ArrayList;
import java.util.List;

public class SiteDocDataObj implements Formalable {
    //private SiteDataObj siteDataObj;
    private ArrayList<String> comments;
    private SiteKind_Enum siteKind;
    private boolean visited;
    private int truckWeight;
    private int siteId;
    private int driverID;
    private TruckDataObj truck;
    private List<ProductDataObj> products;
    private String address;
    private String name;
    private String contactName;
    private String contactPhone;
    private Area_Enum area;

    public SiteDocDataObj(SiteDocument siteDocument) {
/*        this.siteDataObj = new SiteDataObj(siteDocument.getAddress(),siteDocument.getName(),
                siteDocument.getContactName(),siteDocument.getContactPhone(),siteDocument.getArea());*/
        this.comments = new ArrayList<>();
        for (String s : siteDocument.getComments())
            this.comments.add(s);
        this.siteKind = siteDocument.getSiteKind();
        this.visited = siteDocument.isVisited();
        this.truckWeight = siteDocument.getTruckWeight();
        this.siteId = siteDocument.getSiteDocId();
        this.driverID = siteDocument.getDriver();
        this.truck = (siteDocument.getTruck() == null) ? null : new TruckDataObj(siteDocument.getTruck());
        this.products = new ArrayList<>();
        for (Product product : siteDocument.getAllSiteProducts().values())
            this.products.add(new ProductDataObj(product));
        this.address = siteDocument.getAddress();
        this.name = siteDocument.getName();
        this.contactName = siteDocument.getContactName();
        this.contactPhone = siteDocument.getContactPhone();
        this.area = siteDocument.getArea();
    }

    public SiteDocDataObj(ArrayList<String> comments, SiteKind_Enum siteKind, boolean visited, int truckWeight,
                          int siteId, int driverID, TruckDataObj truck, List<ProductDataObj> products) {
        this.comments = comments;
        this.siteKind = siteKind;
        this.visited = visited;
        this.truckWeight = truckWeight;
        this.siteId = siteId;
        this.driverID = driverID;
        this.truck = truck;
        this.products = products;
    }

    public boolean isVisited() {
        return visited;
    }

    public SiteKind_Enum getSiteKind() {
        return siteKind;
    }

    public String toString() {
        String s = "siteDoc id: " + siteId +
                " address: " + address +
                " name: " + name +
                " contact name: " + contactName +
                " contact phone: " + contactPhone +
                " area: " + area.toString() +
                " site kind: " + siteKind.toString();
        if(truck != null)
            s = s +
                " truck weight: " + truckWeight +
                " truck: " + truck.toFormal() +
                " driver: " + driverID + "\n";
        if (comments.size() != 0)
            s += " \n" + "comments: \n"
                    + "[ \n" + comments
                    .stream()
                    .reduce("", (acc, curr) -> acc + "    " + curr + "\n", String::join)
                    + "] \n";
        if (products.size() != 0)
            s += "products: \n"
                    + "[ \n" + products
                    .stream()
                    .reduce("", (acc, curr) -> acc + "    " + curr.toString(), String::join)
                    + "] \n";
        return s;
    }

    public String toFormal(){
        return "siteDoc id: " + siteId +
                " address: " + address +
                " name: " + name +
                " site kind: " + siteKind.toString() + "\n";
    }



    public int getID() {
        return siteId;
    }
}
