package Backend.ServiceLayer.ObjectsSupplier;




import Backend.BusinessLayer.Tools.Pair;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;

public class SOrderConst extends SOrder {
    private List<DayOfWeek> supplyConstantDays;

    public SOrderConst(int supplierID, int orderID, double priceAfterDiscount, List<DayOfWeek> supplyConstantDays, String superAddress) {
        super( supplierID,orderID,priceAfterDiscount,superAddress);
        this.supplyConstantDays = supplyConstantDays;
    }

    @Override
    public double getPriceAfterDiscount() {
        return super.getPriceAfterDiscount();
    }

    public List<DayOfWeek> getSupplyConstantDays() {
        return supplyConstantDays;
    }
}
