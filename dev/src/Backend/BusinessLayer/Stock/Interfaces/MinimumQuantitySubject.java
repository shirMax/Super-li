package Backend.BusinessLayer.Stock.Interfaces;

public interface MinimumQuantitySubject {
    void AddMinimumQuantityObserver(MinimumQuantityObserver observer);
    void RemoveMinimumQuantityObserver(MinimumQuantityObserver observer);
    void NotifyMinimumQuantityObservers();
}
