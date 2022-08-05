package Backend.BusinessLayer.Suppliers;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class OrderConst extends Order {
    private List<DayOfWeek> supplyConstantDays;

    public OrderConst(int orderID, int supplierID, double priceAfterDiscount, List<DayOfWeek> supplyConstantDays, String superAddress) {
        super(supplierID, orderID, priceAfterDiscount, superAddress);
        this.supplyConstantDays = supplyConstantDays;
    }
    public OrderConst(int supplierID, int orderID, List<DayOfWeek> supplyConstantDays, String superAddress, HashMap<Item, Integer> itemAndAmount, HashMap<Integer,HashMap<Integer,Integer>> itemToAmountAndDiscount) {
        super(supplierID, orderID, superAddress, itemAndAmount, itemToAmountAndDiscount);
        this.supplyConstantDays = supplyConstantDays;
    }

    public List<DayOfWeek> getSupplyConstantDays() {
        return supplyConstantDays;
    }


    public void checkDays() throws Exception {
        LocalDate localDate = LocalDate.now();
        int currentDayOfWeek = localDate.getDayOfWeek().getValue();
        for(DayOfWeek dw : supplyConstantDays)
            if(currentDayOfWeek+1 == dw.getValue() || currentDayOfWeek == dw.getValue())
                throw new Exception("can't update order day before the supply date!");
    }
    public boolean isSuppliedToday() {
        int today = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)-1;
        if(today==0)
            today = 7;
        DayOfWeek day = DayOfWeek.of(today);
        return supplyConstantDays.contains(day);
    }

}
