package Frontend.PresentationLayer.Model;




import java.time.DayOfWeek;
import java.util.List;

public class MOrderConst extends MOrder{
    private List<DayOfWeek> supplyConstantDays;

    public MOrderConst(int supplierID, int orderID, double priceAfterDiscount, List<DayOfWeek> supplyConstantDays, String superAddress) {
        super(supplierID,orderID,priceAfterDiscount,superAddress);
        this.supplyConstantDays = supplyConstantDays;
    }

    @Override
    public String toString() {
        return "{" +
                "supplierID=" + getSupplierID() +
                ", OrderID=" + getOrderID() +
                ", priceAfterDiscount=" + getPriceAfterDiscount() +
                ", superAddress= "+getAddress()+
                " supplyConstantDays=" + supplyConstantDays +
                '}';
    }
}
