package Backend.BusinessLayer.Interfaces;

import Backend.BusinessLayer.Suppliers.OrderItem;

import java.util.List;

public interface GetOrderItemsCallback {
    List<OrderItem> getOrderItems(int orderID,String branch);
}
