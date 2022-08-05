package Backend.BusinessLayer.Stock.Interfaces;

public interface ShortageSubject {
    void AddShortageObserver(ShortageObserver observer);
    void RemoveShortageObserver(ShortageObserver observer);
    void NotifyShortageObservers();
}
