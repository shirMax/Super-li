
package TestsSuppliers.Backend.BusinessLayer;

import Backend.BusinessLayer.Suppliers.Supplier;
import Backend.PersistenceLayer.BranchesDAO;
import Backend.PersistenceLayer.SuppliersDAL.ContactDAO;
import Backend.PersistenceLayer.SuppliersDAL.ItemDAO;
import Backend.PersistenceLayer.SuppliersDAL.OrderDAO;
import Backend.PersistenceLayer.SuppliersDAL.SupplierDAO;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

import java.time.DayOfWeek;
import java.util.*;

class SupplierTest {

    private static OrderDAO od;
    private static ItemDAO itd;
    private static ContactDAO cd;
    private static BranchesDAO bd;
    private static SupplierDAO sd;
    private static Supplier s1;
    private static Supplier s2;

    @BeforeEach
    public void setUp() throws Exception {
        od = new OrderDAO();
        itd = new ItemDAO();
        cd = new ContactDAO();
        bd = new BranchesDAO();
        sd = new SupplierDAO(cd,itd,od,bd);
        sd.insertSupplier(6666,45665,"Cash","Const","Tnuva","Rager 51","North");
        sd.insertSupplier(7777,54785,"Cash","Collect","Osem","Hashalom 51", "North");
        s1 = sd.getSupplier(6666);
        s2 = sd.getSupplier(7777);
        bd.addBranch("modiin","North");
    }

    @AfterEach
    public void tearDown() throws Exception {
        sd.removeSupplier(6666);
        sd.removeSupplier(7777);
        bd.remove("modiin","BranchAddress","Branches");
    }

    @Test
    void addOrderConst() throws Exception {
        try {
            int sizeBefore= s1.getOrdersConst().size();
            List<DayOfWeek> supplyConstantDays = new LinkedList<>();
            HashMap<Integer, Integer> items = new HashMap<>();
            supplyConstantDays.add(DayOfWeek.SUNDAY);
            s1.addItem( 100, 2, "milk", 4.9);
            items.put(100, 5);
            s1.addOrderConst(supplyConstantDays, items, "modiin",100000);
            assertEquals(s1.getOrdersConst().size(),sizeBefore+1);
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }



    @Test
    void calculatePrice() throws Exception {
        try {
            List<DayOfWeek> days = new LinkedList<>();
            days.add(DayOfWeek.SUNDAY);
            s1.addItem( 100, 2, "milk", 4.9);
            s1.addItem( 200, 65, "cheese", 14.0);
            s1.addItem( 300, 32, "koteg", 22.9);
            s1.addItem( 400, 78, "batter", 8.9);
            s1.addItem( 500, 53, "ygurt", 5.9);
            s1.addDiscount(100, 20, 10);
            s1.addDiscount(100, 100, 20);
            s1.addDiscount(400, 100, 20);
            HashMap<Integer, Integer> items = new HashMap<>();
            items.put(100, 300);
            items.put(200, 100);
            items.put(300, 10);
            items.put(400, 50);
            s1.addOrderConst(days, items, "modiin", 100002);
            assertEquals(s1.getOrdersConst().get(s1.getOrdersConst().size()-1).getPriceAfterDiscount(),3250);
        }
        catch (Exception e)
        {
            fail(e.getMessage());
        }
    }

    @Test
    void addItem() throws Exception {
        try {
            int sizeBefore = s1.getContract().getItems().size();
            s1.addItem( 100, 2, "Bamba", 9.90);
            assertEquals(s1.getContract().getItems().size(),sizeBefore+1);
            s1.addItem( 200, 6, "Bisli", 9.90);
            assertEquals(s1.getContract().getItems().size(),sizeBefore+2);
        }
        catch (Exception e){
            fail(e.getMessage());
        }
        finally {
            s1.removeItem(100);
            s1.removeItem(200);
        }
    }

    @Test
    void removeItem() throws Exception {
        try {
            s1.addItem( 100, 2, "Bamba", 9.90);
            s1.addItem( 200, 6, "Bisli", 9.90);
            int sizeBefore = s1.getContract().getItems().size();
            s1.removeItem(100);
            assertEquals(s1.getContract().getItems().size(), sizeBefore-1);
            s1.removeItem(200);
            assertEquals(s1.getContract().getItems().size(), sizeBefore-2);
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }


    @Test
    void addContact() throws Exception {
        try {
            int size = s1.getContacts().size();
            s1.addContact(12345, "shir", "05797979");
            assertEquals(s1.getContacts().size(),size+1);
            s1.addContact(12495, "shira", "04797979");
            assertEquals(s1.getContacts().size(),size+2);
        }
        catch (Exception e){
            fail(e.getMessage());
        }
        finally {
            s1.removeContact(12345);
            s1.removeContact(12495);
        }
    }

    @Test
    void removeContact() throws Exception {
        try {
            int size = s1.getContacts().size();
            s1.addContact(12345, "shir", "05797979");
            s1.addContact(12495, "shira", "04797979");
            s1.removeContact(12345);
            assertEquals(s1.getContacts().size(), size + 1);
        } catch (Exception e) {
            fail(e.getMessage());
        } finally {
            s1.removeContact(12495);
        }
    }


    @Test
    void setContactName() throws Exception {
        try {
            s1.addContact(12345, "shir", "05797979");
            s1.setContactName(12345,"Niv");
            assertEquals(s1.getContacts().get(s1.getContacts().size()-1).getName(),"Niv");
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }


    @Test
    void removeItemsToConstOrder() {
        try {
            List<DayOfWeek> supplyConstantDays = new LinkedList<>();
            HashMap<Integer, Integer> items = new HashMap<>();
            supplyConstantDays.add(DayOfWeek.SATURDAY);
            s1.addItem( 100, 2, "milk", 4.9);
            s1.addItem( 200, 3, "cheese", 4.5);
            items.put(100, 5);
            items.put(200, 10);
            HashMap<Integer, Integer> itemsRemove = new HashMap<>();
            itemsRemove.put(200,10);
            s1.addOrderConst(supplyConstantDays, items, "modiin",100000);
            s1.removeItemsToConstOrder(100000,itemsRemove);
            assertEquals(s1.getOrderPrice(100000),24.5);
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    void getItemsForSupplier() {
        try {
            s1.addItem(100, 2, "milk", 4.9);
            s1.addItem(200, 65, "cheese", 14.0);
            s1.addItem(300, 32, "koteg", 22.9);
            s1.addItem(400, 78, "batter", 8.9);
            s1.addItem(500, 53, "ygurt", 5.9);
            int sizeItems = s1.getItemsForSupplier().size();
            assertEquals(sizeItems,5);

        } catch (Exception e) {
            fail(e);
        }
    }
}




