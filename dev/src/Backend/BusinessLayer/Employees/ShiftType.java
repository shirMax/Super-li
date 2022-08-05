package Backend.BusinessLayer.Employees;

public class ShiftType{
    private static String morning="Morning";
    private static String evening = "Evening";
    public static String morning(){return morning;}
    public static String evening(){return evening;}
    public static boolean isShiftType(String type){
        return type.equals(morning)|type.equals(evening);
    }
}
