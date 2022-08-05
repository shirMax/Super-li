package Backend.BusinessLayer.CallBacks;

import Backend.BusinessLayer.Deliveries.Delivery;
import Backend.BusinessLayer.Deliveries.Enums.Area_Enum;
import Backend.BusinessLayer.Deliveries.Enums.DeliveryMode_Enum;
import Backend.BusinessLayer.Deliveries.SiteDocument;

public interface AddSupplierToDeliveryCallBack {

    public int call(int deliveryID, String name, String contactName, String contactPhone, Area_Enum area_enum, String address) throws Exception;

}
