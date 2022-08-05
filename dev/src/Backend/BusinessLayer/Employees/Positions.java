package Backend.BusinessLayer.Employees;

import java.util.ArrayList;
import java.util.List;

public class Positions {
    private static String cashier = "Cashier";
    private static String mover ="Mover";
    private static String stockKeeper ="Stock Keeper";
    private static String shiftManager ="Shift Manager";
    private static String hrManager = "HR Manager";
    private static String driver = "Driver";

    public static List<String> getPositions(){
        List<String> ret = new ArrayList<>();
        ret.add(cashier);ret.add(mover);ret.add(stockKeeper);ret.add(shiftManager);ret.add(hrManager);ret.add(driver);
        return ret;
    }
    public static String cashier(){return cashier;}
    public static String mover(){return mover;}
    public static String stockKeeper(){return stockKeeper;}
    public static String shiftManager(){return shiftManager;}
    public static String hrManager(){return hrManager;}
    public static String driver(){return driver;}

}
