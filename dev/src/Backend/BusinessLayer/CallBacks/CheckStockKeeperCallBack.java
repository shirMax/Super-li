package Backend.BusinessLayer.CallBacks;

import Backend.BusinessLayer.Deliveries.Enums.License_Enum;
import Backend.BusinessLayer.Suppliers.Contact;

import java.util.Date;

public interface CheckStockKeeperCallBack {

    public Contact call(Date date, String brunchAddress);

}
