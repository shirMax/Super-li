package Backend.PersistenceLayer;

import Backend.BusinessLayer.Deliveries.Enums.Area_Enum;
import Backend.BusinessLayer.Deliveries.Enums.SiteKind_Enum;


import java.util.ArrayList;
import java.util.List;

public class SiteDocDTO {
    private String address;
    private String name;
    private String contactName;
    private String contactPhone;
    private ArrayList<String> comments;
    private Area_Enum area;
    private SiteKind_Enum siteKind;
    private boolean visited;
    private int truckWeight;
    private int siteDocId;
    private DriverDTO driver;
    private TruckDTO truck;
    private List<ProductDTO> products;

/*
    public SiteDTO(Site site) {
        this.address = site.getAddress();
        this.name = site.getName();
        this.contactName = site.getContactName();
        this.contactPhone = site.getContactPhone();
        this.comments = new ArrayList<>();
        for (String s : site.getComments())
            this.comments.add(s);
        this.area = site.getArea();
        this.siteKind = site.getSiteKind();
        this.visited = site.isVisited();
        this.truckWeight = site.getTruckWeight();
        this.siteId = site.getSiteId();
        this.driver = new DriverDTO(site.getDriver());
        this.truck = new TruckDTO(site.getTruck());
        this.products = new ArrayList<>();
        for (Product product : site.getAllSiteProducts().values())
            this.products.add(new ProductDTO(product));
    }
*/

    public SiteDocDTO(String address, String name, String contactName, String contactPhone,
                      ArrayList<String> comments, Area_Enum area, SiteKind_Enum siteKind,
                      boolean visited, int truckWeight, int siteDocId, DriverDTO driver,
                      TruckDTO truck, List<ProductDTO> products) {
        this.address = address;
        this.name = name;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.comments = comments;
        this.area = area;
        this.siteKind = siteKind;
        this.visited = visited;
        this.truckWeight = truckWeight;
        this.siteDocId = siteDocId;
        this.driver = driver;
        this.truck = truck;
        this.products = products;
    }

    public SiteKind_Enum getSiteKind() {
        return siteKind;
    }

    public String toString(){
        return "site id: " + siteDocId +
                " address: " + address +
                " name: " + name +
                " contact name: " + contactName +
                " contact phone: " + contactPhone +
                " area: " + area.toString() +
                " site kind: " + siteKind.toString() +
                " truck weight: " + truckWeight +
                " truck: " + truck.toString() +
                " driver: " + driver.toString() +
                " comments: " + comments.toString() + "\n" +
                " products: " + products.toString() + "\n";
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

    public ArrayList<String> getComments() {
        return comments;
    }

    public Area_Enum getArea() {
        return area;
    }

    public boolean isVisited() {
        return visited;
    }

    public int getTruckWeight() {
        return truckWeight;
    }

    public int getSiteDocId() {
        return siteDocId;
    }

    public DriverDTO getDriver() {
        return driver;
    }

    public TruckDTO getTruck() {
        return truck;
    }

    public List<ProductDTO> getProducts() {
        return products;
    }
}
