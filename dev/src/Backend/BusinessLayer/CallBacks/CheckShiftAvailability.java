package Backend.BusinessLayer.CallBacks;

import Backend.BusinessLayer.Deliveries.Enums.License_Enum;

import java.util.Date;

public interface CheckShiftAvailability {

    public boolean call(Date date, int employeeId, License_Enum license);
}
