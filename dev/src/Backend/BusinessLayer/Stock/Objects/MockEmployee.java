package Backend.BusinessLayer.Stock.Objects;

import Backend.BusinessLayer.Stock.Interfaces.DefectivesObserver;
import Backend.BusinessLayer.Stock.Interfaces.MinimumQuantityObserver;
import Backend.BusinessLayer.Stock.Interfaces.PeriodicStockObserver;
import Backend.BusinessLayer.Stock.Interfaces.ShortageObserver;

public class MockEmployee implements DefectivesObserver, MinimumQuantityObserver, PeriodicStockObserver, ShortageObserver {

    public MockEmployee() {}

    @Override
    public void GetDefectivesReport(String report) {
        System.out.println("\nemployee got defectives report");
    }

    @Override
    public void GetMinimumQuantityReport(String msg) {
        System.out.printf("\nemployee got minimum quantity alert: %s\n",msg);
    }

    @Override
    public void GetPeriodicStockReport(String report) {
        System.out.println("\nemployee got periodic stock report");
    }

    @Override
    public void GetShortageReport(String report) {
        System.out.println("\nemployee got shortage report");
    }
}
