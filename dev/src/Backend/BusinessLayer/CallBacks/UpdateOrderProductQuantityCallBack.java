package Backend.BusinessLayer.CallBacks;

import java.sql.Date;

public interface UpdateOrderProductQuantityCallBack {
    public void call(int orderID, int itemNum, int quantity) throws Exception;

}
