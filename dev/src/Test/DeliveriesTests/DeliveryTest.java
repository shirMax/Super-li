package DeliveriesTests;

import Backend.BusinessLayer.Deliveries.Delivery;
import Backend.BusinessLayer.Deliveries.Enums.Area_Enum;
import Backend.BusinessLayer.Deliveries.Enums.DeliveryMode_Enum;
import Backend.BusinessLayer.Deliveries.SiteDocument;
import Backend.BusinessLayer.Deliveries.Truck;
import Backend.BusinessLayer.Tools.DateConvertor;
import Backend.PersistenceLayer.DeliveriesDal.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class DeliveryTest {

    static CreateDBDeliveries db;
    static Delivery delivery;
    //static Site site;
    static TruckDAO truckDAO;
    //static SiteDAO siteDAO;
    static SiteDocDAO siteDocDAO;
    static ProductsSiteDocDAO productsSiteDocDAO;
    static DeliveryDAO deliveryDAO;

    @BeforeEach
    void setUp() {
        db = new CreateDBDeliveries();
        db.createFileIfNotExists();
        Truck t1;
        truckDAO = new TruckDAO();
        siteDocDAO = new SiteDocDAO(truckDAO);
        productsSiteDocDAO = new ProductsSiteDocDAO(siteDocDAO);
        deliveryDAO = new DeliveryDAO(truckDAO, siteDocDAO, productsSiteDocDAO);
        try {
            t1 = new Truck(2, "B", 10, 15);
            //site = new Site("mapo 3, TLV", "gery", "oded", "050", "Center");
            delivery = new Delivery(0, "20/05/2030 10:00", "Invited", t1, 1, deliveryDAO,-1);
        } catch (Exception exception) {
        }
    }

    @AfterEach
    void tearDown() {
        db.clearDB();
    }

    @Test
    void addSite_success() {
        SiteDocument siteDoc;
        try {
            siteDoc = new SiteDocument(siteDocDAO,"SOURCE",1,delivery.getDriverID(),
                    delivery.getTruck(),"address","name", Area_Enum.Center,
                    "david","050");
            delivery.addSite(siteDoc);
            assertTrue(siteDoc.equals(delivery.getSite(1)));
        } catch (Exception exception) {
            assertTrue(false);
        }
    }


    @Test
    void addSite_fail() {
        SiteDocument siteDoc;
        try {
//            siteDoc = new SiteDocument(siteDocDAO,site,"SOURCE",1,
//                    delivery.getDriverID(),delivery.getTruck());
            siteDoc = new SiteDocument(siteDocDAO,"SOURCE",1,delivery.getDriverID(),
                    delivery.getTruck(),"address","name", Area_Enum.Center,
                    "david","050");
            delivery.setMode(DeliveryMode_Enum.Arrived);
            delivery.addSite(siteDoc);
            assertTrue(false);
        } catch (Exception exception) {
            assertTrue(true);
        }
    }

    @Test
    void removeSite_success() {
        SiteDocument siteDoc;
        try {
//            siteDoc = new SiteDocument(siteDocDAO,site,"SOURCE",1,
//                    delivery.getDriverID(),delivery.getTruck());
            siteDoc = new SiteDocument(siteDocDAO,"SOURCE",1,delivery.getDriverID(),
                    delivery.getTruck(),"address","name", Area_Enum.Center,
                    "david","050");
            delivery.addSite(siteDoc);
            delivery.removeSite(1);
            assertTrue(delivery.getSite(1) == null);
        } catch (Exception exception) {
            assertTrue(false);
        }
    }
    @Test
    void removeSite_fail() {
        SiteDocument siteDoc;
        try {
            delivery.removeSite(1);
            assertTrue(false);
        } catch (Exception exception) {
            assertTrue(true);
        }
    }


    @Test
    void launchDelivery() {
        try {
            delivery.setMode(DeliveryMode_Enum.InTheWays);
            assertTrue(delivery.getMode() == DeliveryMode_Enum.InTheWays);
        } catch (Exception exception) {
            assertTrue(false);
        }

    }

    @Test
    void informSiteVisit_success() {
        SiteDocument siteDoc;
        try {
//            siteDoc = new SiteDocument(siteDocDAO,"SOURCE",1, site.getAddress(),
//                    site.getName(),site.getArea().toString(),"dav","050");
            siteDoc = new SiteDocument(siteDocDAO,"SOURCE",1,delivery.getDriverID(),
                    delivery.getTruck(),"address","name", Area_Enum.Center,
                    "david","050");
            delivery.addSite(siteDoc);
            delivery.setMode(DeliveryMode_Enum.InTheWays);
            delivery.informSiteVisit(1,2);
            boolean arrived = delivery.getMode() == DeliveryMode_Enum.Arrived;
            boolean visited = siteDoc.isVisited();
            assertTrue(arrived && visited);
        } catch (Exception exception) {
            assertTrue(false);
        }

    }

    @Test
    void informSiteVisit_failSite() {
        SiteDocument siteDoc;
        try {
//            siteDoc = new SiteDocument(siteDocDAO,"SOURCE",1, site.getAddress(),
//                    site.getName(),site.getArea().toString(),"dav","050");
            siteDoc = new SiteDocument(siteDocDAO,"SOURCE",1,delivery.getDriverID(),
                    delivery.getTruck(),"address","name", Area_Enum.Center,
                    "david","050");
            delivery.addSite(siteDoc);
            delivery.setMode(DeliveryMode_Enum.InTheWays);
            delivery.informSiteVisit(3,2);
            assertTrue(false);
        } catch (Exception exception) {
            assertTrue(true);
        }

    }
    @Test
    void informSiteVisit_Redesign() {
        SiteDocument siteDoc;
        try {
//            siteDoc = new SiteDocument(siteDocDAO,"SOURCE",1, site.getAddress(),
//                    site.getName(),site.getArea().toString(),"dav","050");
            siteDoc = new SiteDocument(siteDocDAO,"SOURCE",1,delivery.getDriverID(),
                    delivery.getTruck(),"address","name", Area_Enum.Center,
                    "david","050");
            delivery.addSite(siteDoc);
            delivery.setMode(DeliveryMode_Enum.InTheWays);
            delivery.informSiteVisit(1,222222);
            assertTrue(false);
        } catch (Exception exception) {
            assertTrue(delivery.getMode() == DeliveryMode_Enum.ReDesigning);
        }

    }

    @Test
    void informSiteVisit_failMode() {
        SiteDocument siteDoc;
        try {
//            siteDoc = new SiteDocument(siteDocDAO,"SOURCE",1, site.getAddress(),
//                    site.getName(),site.getArea().toString(),"dav","050");
            siteDoc = new SiteDocument(siteDocDAO,"SOURCE",1,delivery.getDriverID(),
                    delivery.getTruck(),"address","name", Area_Enum.Center,
                    "david","050");
            delivery.addSite(siteDoc);
            delivery.setMode(DeliveryMode_Enum.Arrived);
            delivery.informSiteVisit(1,2);
            assertTrue(false);
        } catch (Exception exception) {
            assertTrue(true);
        }

    }

    @Test
    void setTruck_success() {
        try {
            Truck t2 = new Truck(2,"B",34,45);
            delivery.setTruck(t2);
            assertTrue(delivery.getTruck().equals(t2));
        } catch (Exception exception) {
            assertTrue(false);
        }
    }
    @Test
    void setTruck_fail() {
        try {
            Truck t2 = new Truck(2,"B",34,45);
            delivery.setMode(DeliveryMode_Enum.Arrived);
            delivery.setTruck(t2);
            assertTrue(delivery.getTruck().equals(t2));
        } catch (Exception exception) {
            assertTrue(false);
        }
    }
}
