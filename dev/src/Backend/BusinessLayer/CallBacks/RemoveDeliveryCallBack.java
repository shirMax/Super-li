package Backend.BusinessLayer.CallBacks;

import Backend.BusinessLayer.Deliveries.Delivery;

public interface RemoveDeliveryCallBack {

    public void call(int deliveryID) throws Exception;

}
