package Backend.BusinessLayer.CallBacks;

import Backend.BusinessLayer.Deliveries.Enums.Area_Enum;

import java.sql.Date;

public interface UpdateOrderDateCallBack {
    public void call(int orderID, Date date) throws Exception;

}
