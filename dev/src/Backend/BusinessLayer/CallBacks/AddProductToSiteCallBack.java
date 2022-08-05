package Backend.BusinessLayer.CallBacks;

import Backend.BusinessLayer.Deliveries.Delivery;

public interface AddProductToSiteCallBack {

    public void call(int deliveryID, int itemID, String productName, int quantity) throws Exception;

}
