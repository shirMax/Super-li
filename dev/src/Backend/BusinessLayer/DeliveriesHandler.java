package Backend.BusinessLayer;

import Backend.BusinessLayer.Deliveries.DeliveryController;
import Backend.BusinessLayer.Deliveries.Enums.License_Enum;
import Backend.BusinessLayer.Employees.EmployeeController;
import Backend.BusinessLayer.Employees.Shift;
import Backend.BusinessLayer.Employees.ShiftController;

import java.util.Date;

public class DeliveriesHandler {
    private DeliveryController deliveryController;
    private ShiftController shiftController;
    private EmployeeController employeeController;

    public DeliveriesHandler(DeliveryController deliveryController, ShiftController shiftController,
                             EmployeeController employeeController) {
        this.deliveryController=deliveryController;
        this.shiftController=shiftController;
        this.employeeController = employeeController;
        deliveryController.setCheckShiftAvailability((Date date, int employeeId, License_Enum license) -> shiftController.CheckShiftAvailability(date, employeeId,license));
        deliveryController.setSwitchDrivers((Date date, int employeeID1 , int employeeID2) -> shiftController.SwitchDrivers(date,employeeID1,employeeID2));
        deliveryController.setCheckStockKeeper((Date date, String brunchAddress) -> shiftController.CheckStockKeeper(date, brunchAddress));
        deliveryController.setHRManagerNotificationCallBack(shiftController::hRManagerNotification);
        employeeController.setGetShiftsCallBack(shiftController::getAllShifts);
        shiftController.setIsEmployeeAvailableCallBack(deliveryController::isEmployeeAvailable);
    }

}
