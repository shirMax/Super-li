package Backend.BusinessLayer;

import Backend.BusinessLayer.Deliveries.DeliveryController;
import Backend.BusinessLayer.Deliveries.Enums.Area_Enum;
import Backend.BusinessLayer.Deliveries.Enums.License_Enum;
import Backend.BusinessLayer.Employees.ShiftController;
import Backend.BusinessLayer.Suppliers.SupplierController;

import java.sql.Date;

public class DeliveriesOrdersHandler {

    private DeliveryController deliveryController;
    private SupplierController supplierController;

    public DeliveriesOrdersHandler(DeliveryController deliveryController, SupplierController supplierController) {
        this.deliveryController=deliveryController;
        this.supplierController=supplierController;
        supplierController.setAddBrunchToDeliveryCallBack((int deliveryID, Area_Enum area_enum, String address)->
                deliveryController.addBrunchToDelivery(deliveryID, area_enum, address));
        supplierController.setAddProductToSiteCallBack(deliveryController::addProductToSite);
        supplierController.setAddSupplierToDeliveryCallBack((int deliveryID, String name, String contactName, String contactPhone, Area_Enum area_enum, String address)->
                deliveryController.addSupplierToDelivery(deliveryID,name,contactName,contactPhone,area_enum,address));
        supplierController.setOrderDeliveryCallBack(deliveryController::orderDelivery);
        supplierController.setRemoveDeliveryCallBack(deliveryController::removeDeliveryCallback);
        supplierController.setUpdateProductInSiteCallBack(deliveryController::updateProductInSite);
        deliveryController.setUpdateOrderDateCallBack((int orderID, Date date)->
                supplierController.updateOrderDate(orderID, date));
        deliveryController.setUpdateOrderProductQuantityCallBack((int orderID, int itemNum, int quantity)->
                supplierController.updateOrderProductQuantity(orderID, itemNum, quantity));
        deliveryController.setCancelOrderCallBack(supplierController::cancelOrderCallback);
    }
}
