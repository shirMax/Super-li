package Backend.BusinessLayer.Stock.Objects;

import Backend.BusinessLayer.Stock.Interfaces.*;

import java.util.ArrayList;
import java.util.List;

public class Reporter implements DefectivesSubject, PeriodicStockSubject, ShortageSubject, MinimumQuantitySubject,Runnable {

    private boolean shouldTerminate;
    List<DefectivesObserver> defectivesObservers;
    List<PeriodicStockObserver> periodicStockObservers;
    List<ShortageObserver> shortageObservers;
    List<MinimumQuantityObserver> minimumQuantityObservers;
    List<String> defectivesReports;
    List<String> periodicStockReports;
    List<String> shortageReports;
    List<String> minQuantityReports;

    protected Reporter(){
        shouldTerminate = false;
        defectivesObservers = new ArrayList<>();
        minimumQuantityObservers = new ArrayList<>();
        periodicStockObservers = new ArrayList<>();
        shortageObservers = new ArrayList<>();
        defectivesReports = new ArrayList<>();
        periodicStockReports = new ArrayList<>();
        shortageReports = new ArrayList<>();
        minQuantityReports = new ArrayList<>();
    }

    public synchronized void sendDefectivesReport(String report){
        defectivesReports.add(report);
        this.notifyAll();
    }

    public synchronized void sendPeriodicStockReport(String report){
        periodicStockReports.add(report);
        this.notifyAll();
    }

    public synchronized void sendShortageReport(String report){
        shortageReports.add(report);
        this.notifyAll();
    }

    public synchronized void sendMinimumQuantityAlert(String msg){
        minQuantityReports.add(msg);
        this.notifyAll();
    }

    @Override
    public synchronized void AddDefectivesObserver(DefectivesObserver observer) {
        defectivesObservers.add(observer);
    }

    @Override
    public synchronized void RemoveDefectivesObserver(DefectivesObserver observer) {
        defectivesObservers.remove(observer);
    }

    @Override
    public synchronized void NotifyDefectivesObservers() {
        for(String report : defectivesReports)
            for(DefectivesObserver observer : defectivesObservers)
                observer.GetDefectivesReport(report);
        defectivesReports.clear();
    }

    @Override
    public synchronized void AddMinimumQuantityObserver(MinimumQuantityObserver observer) {
        minimumQuantityObservers.add(observer);
    }

    @Override
    public synchronized void RemoveMinimumQuantityObserver(MinimumQuantityObserver observer) {
        minimumQuantityObservers.remove(observer);
    }

    @Override
    public synchronized void NotifyMinimumQuantityObservers() {
        for(String report : minQuantityReports)
            for(MinimumQuantityObserver observer : minimumQuantityObservers)
                observer.GetMinimumQuantityReport(report);
        minQuantityReports.clear();
    }

    @Override
    public synchronized void AddPeriodicStockObserver(PeriodicStockObserver observer) {
        periodicStockObservers.add(observer);
    }

    @Override
    public synchronized void RemovePeriodicStockObserver(PeriodicStockObserver observer) {
        periodicStockObservers.remove(observer);
    }

    @Override
    public synchronized void NotifyPeriodicStockObservers() {
        for(String report : periodicStockReports)
            for(PeriodicStockObserver observer: periodicStockObservers)
                observer.GetPeriodicStockReport(report);
        periodicStockReports.clear();
    }

    @Override
    public synchronized void AddShortageObserver(ShortageObserver observer) {
        shortageObservers.add(observer);
    }

    @Override
    public synchronized void RemoveShortageObserver(ShortageObserver observer) {
        shortageObservers.remove(observer);
    }

    @Override
    public synchronized void NotifyShortageObservers() {
        for(String report : shortageReports)
            for(ShortageObserver observer : shortageObservers)
                observer.GetShortageReport(report);
        shortageReports.clear();
    }

    public synchronized void Terminate(){
        shouldTerminate = true;
        notifyAll();
    }

    @Override
    public void run() {
        while(!shouldTerminate){
            synchronized (this){
                while (listsEmpty() && !shouldTerminate) {
                    try {this.wait();} catch (InterruptedException e) {
                        if(shouldTerminate) break;
                    }
                }
                NotifyShortageObservers();
                NotifyDefectivesObservers();
                NotifyMinimumQuantityObservers();
                NotifyPeriodicStockObservers();
            }
        }
    }

    private boolean listsEmpty(){
        return defectivesReports.isEmpty() && shortageReports.isEmpty() && periodicStockReports.isEmpty() && minQuantityReports.isEmpty();
    }
}
