package Backend.BusinessLayer.CallBacks;


import Backend.BusinessLayer.Deliveries.Enums.Area_Enum;

public interface AddBrunchToDeliveryCallBack {

    public int call(int deliveryID, Area_Enum area_enum, String address) throws Exception;

}
