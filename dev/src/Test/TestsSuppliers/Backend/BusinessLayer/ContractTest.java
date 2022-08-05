
package TestsSuppliers.Backend.BusinessLayer;

import Backend.BusinessLayer.Suppliers.Supplier;
import Backend.PersistenceLayer.BranchesDAO;
import Backend.PersistenceLayer.SuppliersDAL.ContactDAO;
import Backend.PersistenceLayer.SuppliersDAL.ItemDAO;
import Backend.PersistenceLayer.SuppliersDAL.OrderDAO;
import Backend.PersistenceLayer.SuppliersDAL.SupplierDAO;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class ContractTest {
    private static OrderDAO od;
    private static ItemDAO itd;
    private static BranchesDAO bd;
    private static ContactDAO cd;
    private static SupplierDAO sd;
    private static Supplier s;

    @BeforeEach
    public void setUp() throws Exception {
        od = new OrderDAO();
        itd = new ItemDAO();
        cd = new ContactDAO();
        bd = new BranchesDAO();
        sd = new SupplierDAO(cd,itd,od,bd);
        sd.insertSupplier(6666,45665,"Cash","Const","Tnuva","Rager 51","North");
        s = sd.getSupplier(6666);
        s.addItem(100, 2,"milk", 4.9);
        s.addItem(200, 65,"cheese", 14.0);
        s.addItem( 300, 32,"koteg", 22.9);
        s.addItem(400, 78,"batter", 8.9);
        s.addItem( 500, 53,"ygurt", 5.9);
        s.addDiscount(100,20,10);
        s.addDiscount(400,100,20);
    }

    @AfterEach
    public void tearDown() throws Exception {
        sd.removeSupplier(6666);
    }

    @Test
    void addItem_Success() {
        try{
            int sizeBefore = s.getItemsForSupplier().size();
            s.addItem( 900, 3,"milkTnuva", 6.2);
            assertEquals(s.getItemsForSupplier().size(),sizeBefore+1);
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    void addItem_fail() {
        try{
            int sizeBefore = s.getItemsForSupplier().size();
            s.addItem(100, 78,"batter", 8.9);
            assertEquals(s.getItemsForSupplier().size(),sizeBefore);
            fail("Expected exception was not thrown");
        }
        catch (Exception e){
            assertNotNull(e);
        }
    }

    @Test
    void removeItem_Success() throws Exception {
        try{
            int sizeBefore = s.getItemsForSupplier().size();
            s.removeItem(100);
            assertEquals(s.getItemsForSupplier().size(),sizeBefore-1);
           // s.addItem(1, 2,"milk", 4.9);
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }

    @Test
    void removeItem_fail() {
        try{
            int sizeBefore = s.getItemsForSupplier().size();
            s.removeItem(111);
            assertEquals(s.getItemsForSupplier().size(),sizeBefore);
            fail("Expected exception was not thrown");
        }
        catch (Exception e){
            assertNotNull(e);
        }
    }

    @Test
    void addDiscount_Success() {
        try{
            int sizeBefore = s.getContract().getDiscount().size();
            s.addDiscount(100,300,20);
            assertEquals(s.getContract().getDiscount().size(),sizeBefore);
        }
        catch (Exception e){
            fail(e.getMessage());
        }
    }

}

