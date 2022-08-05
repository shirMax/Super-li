package Backend.BusinessLayer.CallBacks;

import Backend.BusinessLayer.Deliveries.Delivery;

public interface UpdateProductInSiteCallBack {

    public void call(int deliveryID, int itemID, int quantity) throws Exception;

}
