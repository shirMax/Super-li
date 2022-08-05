package Backend.BusinessLayer.Interfaces;

import Backend.BusinessLayer.Suppliers.OrderItem;

import java.util.List;

public interface CompleteOrderCallback {
    void completeOrder(int orderId , List<OrderItem> itemsApproved) throws Exception;
}
