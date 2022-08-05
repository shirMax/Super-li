package Backend.BusinessLayer.Stock.Interfaces;

public interface PeriodicStockSubject {
    void AddPeriodicStockObserver(PeriodicStockObserver observer);
    void RemovePeriodicStockObserver(PeriodicStockObserver observer);
    void NotifyPeriodicStockObservers();
}
