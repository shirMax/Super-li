package Backend.BusinessLayer.Stock.Interfaces;

public interface DefectivesSubject {

    void AddDefectivesObserver(DefectivesObserver observer);
    void RemoveDefectivesObserver(DefectivesObserver observer);
    void  NotifyDefectivesObservers();
}
