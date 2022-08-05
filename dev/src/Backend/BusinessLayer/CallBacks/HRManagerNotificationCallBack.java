package Backend.BusinessLayer.CallBacks;

import Backend.BusinessLayer.Deliveries.Enums.License_Enum;

import java.util.Date;

public interface HRManagerNotificationCallBack {
    public void call(Date date, License_Enum license_enum);
}
