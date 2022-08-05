package Backend.BusinessLayer.Deliveries;

import Backend.BusinessLayer.Deliveries.Enums.Area_Enum;
import Backend.BusinessLayer.Deliveries.Enums.SiteKind_Enum;
import Backend.BusinessLayer.Suppliers.Contact;
import Backend.PersistenceLayer.DeliveriesDal.ProductsSiteDocDAO;
import Backend.PersistenceLayer.DeliveriesDal.SiteDocDAO;
import Backend.PersistenceLayer.ProductDTO;
import Backend.PersistenceLayer.SiteDocDTO;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SiteDocument {
    //private Site site;
    private int siteDocId;
    private ArrayList<String> comments;
    private SiteKind_Enum siteKind;
    private boolean visited;
    private int truckWeight;
    private int driverID;
    private Truck truck;
    protected HashMap<Integer, Product> productMap;
    private HashMap<SiteDocument, HashMap<Integer, Product>> productsByDest;
    private SiteDocDAO siteDocDAO;
    private String address;
    private String name;
    private String contactName;
    private String contactPhone;
    private Area_Enum area;

    public SiteDocument(SiteDocDAO siteDocDAO, /*Site site,*/ String siteKind, int siteDocId, int driverID,
                        Truck truck, String address, String name, Area_Enum area,
                        String contactName, String contactPhone) throws Exception {
        this.siteDocDAO = siteDocDAO;
        //this.site = site;
        try {
            this.siteKind = SiteKind_Enum.valueOf(siteKind);
        } catch (Exception e) {
            throw new Exception("Wrong site kind entered!");
        }
        this.siteDocId = siteDocId;
        this.driverID = driverID;
        this.truck = truck;
        this.productMap = new HashMap<>();
        this.comments = new ArrayList<>();
        this.productsByDest = new HashMap<>();
        this.address = address;
        this.name = name;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.area = area;
        visited=false;
    }
    public SiteDocument(SiteDocDAO siteDocDAO, /*Site site,*/ String siteKind, int siteDocId, int driverID,
                        Truck truck , boolean visited, String address, String name, String area,
                        String contactName, String contactPhone) throws Exception {
        this.siteDocDAO = siteDocDAO;
        //this.site = site;
        try {
            this.siteKind = SiteKind_Enum.valueOf(siteKind);
        } catch (Exception e) {
            throw new Exception("Wrong site kind entered!");
        }
        this.siteDocId = siteDocId;
        this.driverID = driverID;
        this.truck = truck;
        this.productMap = new HashMap<>();
        this.comments = new ArrayList<>();
        this.productsByDest = new HashMap<>();
        this.visited=visited;
        this.address = address;
        this.name = name;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        try {
            this.area = Area_Enum.valueOf(area);
        } catch (Exception e) {
            throw new Exception("Wrong site kind entered!");
        }    }

    public SiteDocument(SiteDocDAO siteDocDAO, String siteKind, int siteDocId, String address,
                        String name, String area, String contactName, String contactPhone) throws Exception {
        this.siteDocDAO = siteDocDAO;
        try {
            this.siteKind = SiteKind_Enum.valueOf(siteKind);
        } catch (Exception e) {
            throw new Exception("Wrong site kind entered!");
        }
        this.siteDocId = siteDocId;
        this.productMap = new HashMap<>();
        this.comments = new ArrayList<>();
        this.productsByDest = new HashMap<>();
        this.visited=false;
        this.address = address;
        this.name = name;
        this.contactName = contactName;
        this.contactPhone = contactPhone;

        try {
            this.area = Area_Enum.valueOf(area);
        } catch (Exception e) {
            throw new Exception("Wrong area entered!");
        }    }


    public HashMap<SiteDocument, HashMap<Integer, Product>> getProductsByDest() {
        return productsByDest;
    }

    public SiteDocument(/*Site site,*/ ArrayList<String> comments, SiteKind_Enum siteKind, boolean visited,
                        int truckWeight, /*int siteId,*/ int driverID, Truck truck, List<Product> products,
                        String address, String name, Area_Enum area, String contactName, String contactPhone) {
        //this.site = site;
        this.comments = comments;
        this.siteKind = siteKind;
        this.visited = visited;
        this.truckWeight = truckWeight;
        this.siteDocId = siteDocId;
        this.driverID = driverID;
        this.truck = truck;
        this.address = address;
        this.name = name;
        this.contactName = contactName;
        this.contactPhone = contactPhone;
        this.area = area;
        //this.products = products;
        this.productMap = new HashMap<>();
        for (Product product : products) {
            this.productMap.put(product.getCatNum(), product);
        }
        this.productsByDest = new HashMap<>();
    }

//    public SiteDocument(SiteDocDTO site) throws Exception {
//        this.address = site.getAddress();
//        this.name = site.getName();
//        this.contactName = site.getContactName();
//        this.contactPhone = site.getContactPhone();
//        this.comments = new ArrayList<>();
//        for (String s : site.getComments())
//            this.comments.add(s);
//        this.area = site.getArea();
//        this.siteKind = site.getSiteKind();
//        this.visited = site.isVisited();
//        this.truckWeight = site.getTruckWeight();
//        this.siteDocId = site.getSiteDocId();
//        this.driver = new Driver(site.getDriver());
//        this.truck = new Truck(site.getTruck());
//        this.productMap = new HashMap<>();
//        for (ProductDTO product : site.getProducts())
//            this.productMap.put(product.getCatNum(), new Product(product));
//
//    }

    public ArrayList<String> getComments() {
        return comments;
    }

    public boolean isVisited() {
        return visited;
    }

    public void setVisited(boolean visited) throws Exception {
        siteDocDAO.updateVisit(visited, siteDocId);
        this.visited = visited;
    }

    public void setContact(Contact contact) throws Exception {
        siteDocDAO.updateContact(contact, siteDocId);
        this.contactPhone = contact.getPhone();
        this.contactName = contact.getName();

    }

    public int getTruckWeight() {
        return truckWeight;
    }

    public void setTruckWeight(int truckWeight) {
        siteDocDAO.updateTruckWeight(truckWeight, siteDocId);
        this.truckWeight = truckWeight;
    }

    public int getSiteDocId() {
        return siteDocId;
    }

    public void setSiteId(int siteId) {
        this.siteDocId = siteId;
    }

    public int getDriver() {
        return driverID;
    }

    public void setDriver(int driverID) {
        siteDocDAO.updateDriver(driverID, siteDocId);
        this.driverID = driverID;
    }

    public Truck getTruck() {
        return truck;
    }

    public void setTruck(Truck truck) {
        siteDocDAO.updateTruck(truck.getLicenseNumber(), siteDocId);
        this.truck = truck;
    }

    public SiteKind_Enum getSiteKind() {
        return siteKind;
    }

    public void setSiteKind(SiteKind_Enum siteKind) {
        this.siteKind = siteKind;
    }

    public HashMap<Integer, Product> getAllSiteProducts() {
        return productMap;
    }

/*    public Site getSite() {
        return site;
    }*/
    /*
    public HashMap<Integer, Product> getProductMap() {
        return productMap;
    }

    public void setProductMap(HashMap<Integer, Product> productMap) {
        this.productMap = productMap;
    }*/

    public void addProduct(ProductsSiteDocDAO productsSiteDocDAO, int catNum, String productName,
                           int quantity, SiteDocument destination) throws Exception {
        // CHECK ID - WHO BEFORE ?

        if (!productsByDest.containsKey(destination)) {
            productsByDest.put(destination, new HashMap<>());
        }
        HashMap<Integer, Product> _destProducts = productsByDest.get(destination);
        if (_destProducts.containsKey(catNum))
            throw new Exception("a product with the given category number already exists");
        Product product = new Product(productsSiteDocDAO, catNum, productName, quantity, siteDocId, destination.siteDocId);
        _destProducts.put(catNum, product);
        addProduct(productsSiteDocDAO, catNum, productName, quantity, destination.siteDocId);


    }


    private void addProduct(ProductsSiteDocDAO productsSiteDocDAO, int catNum, String productName, int quantity, int destinationID) throws Exception {
        if (!productMap.containsKey(catNum)) {
            Product product = new Product(productsSiteDocDAO, catNum, productName, quantity, siteDocId, destinationID);
            productMap.put(catNum, product);
        } else {
            Product product = productMap.get(catNum);
            product.setQuantity(product.getQuantity() + quantity);
        }
    }

    public void updateProductQuantity(int catNum, int quantity, SiteDocument destination) throws Exception {
        if (quantity < 0)
            throw new Exception("quantity must be a positive number");
        if (!productsByDest.containsKey(destination)) {
            throw new Exception("cannot update quantity - there is no such destination for this site");
        }
        HashMap<Integer, Product> productsMap = productsByDest.get(destination);
        if (!productsMap.containsKey(catNum))
            throw new Exception("a product with the given category number isnt exists");
        Product reqProduct = productsMap.get(catNum);
        int currentQuantity = reqProduct.getQuantity();
        reqProduct.setQuantity(quantity);

        updateProductQuantity(catNum, quantity - currentQuantity);

    }

    public void updateProductQuantity(int catNum, int quantity) throws Exception {
        if (!productMap.containsKey(catNum))
            throw new Exception("this product is not in the site list");
        Product product = productMap.get(catNum);
        product.setQuantity(product.getQuantity() + quantity);
        if (product.isEmpty()) {
            productMap.remove(catNum);
        }
    }

    public void addComment(String s) throws Exception {
        comments.add(s);
        siteDocDAO.addComment(siteDocId, s);
    }

    public void addCommentFromDB(String s) throws Exception {
        comments.add(s);
    }

    public void cancel() {
        visited = true;
        comments.add("site: " + /*site.getName()*/ name + " with id :" + siteDocId + " has been canceled during redesigning \n");

    }

    public void removeProduct(int catNum) throws Exception {
        if (!productMap.containsKey(catNum))
            throw new Exception("this product is not in the site list");
        productMap.remove(catNum);
    }

    public void removeProduct(int catNum, SiteDocument dest) throws Exception {
        if (!productsByDest.containsKey(dest)) {
            throw new Exception("No such destination to the given site");
        }
        HashMap<Integer, Product> productsMap = productsByDest.get(dest);
        if (!productsMap.containsKey(catNum))
            throw new Exception("a product with the given category number isnt exists");
        int currentAmount = productsByDest.get(dest).get(catNum).getQuantity();
        productsByDest.remove(dest);
        updateProductQuantity(catNum, -currentAmount);
        /* ??? validate if dest is empty ?? validateDest(productsMap); */


    }

/*
    public abstract void checkShouldRemoved();
*/

    public void removeAll() {
        for (SiteDocument dest : productsByDest.keySet()) {
            //productsByDest.remove(dest);
            dest.removeSite(this);
        }

        productsByDest.clear();
        productMap.clear();

    }

    public void removeSite(SiteDocument s) {
        List<Integer> shouldRemoved = new ArrayList<>();
        if (productsByDest.containsKey(s)) {
            HashMap<Integer, Product> productsBySource = productsByDest.get(s);
            for (Product p : productMap.values()) {
                if (productsBySource.containsKey(p.getCatNum()))
                    p.setQuantity(p.getQuantity() - productsBySource.get(p.getCatNum()).getQuantity());
                if (p.isEmpty())
                    shouldRemoved.add(p.getCatNum());
            }
            clear(shouldRemoved);
            productsByDest.remove(s);
        }
    }

    public void cancelSourceDest(SiteDocument dest) {
        comments.add("The source " + /*site.getName()*/ name + " products that appointed to destination : " + /*dest.site.getName()*/ dest.name + " has been canceled during redesigning \n");
        comments.add("Destination: " + /*dest.site.getName()*/ dest.name + " with id" + dest.getSiteDocId() + " has been canceled during redesigning \n");
    }

    private void clear(List<Integer> shouldRemoved) {
        for (Integer x : shouldRemoved) {
            productMap.remove(x);
        }
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

}
