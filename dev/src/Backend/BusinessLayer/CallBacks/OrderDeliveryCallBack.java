package Backend.BusinessLayer.CallBacks;

import Backend.BusinessLayer.Deliveries.Delivery;

import java.sql.Date;
import java.util.List;

public interface OrderDeliveryCallBack {
    public int call(Date dates, int orderID) throws Exception;
}
