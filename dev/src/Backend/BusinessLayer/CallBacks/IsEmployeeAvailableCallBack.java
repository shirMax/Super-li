package Backend.BusinessLayer.CallBacks;


import java.util.Date;

public interface IsEmployeeAvailableCallBack {
    public boolean call(String name, String phone, Date date) throws Exception;

}
