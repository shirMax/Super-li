package Backend.BusinessLayer;

import Backend.BusinessLayer.Deliveries.DeliveryController;
import Backend.BusinessLayer.Stock.Objects.StockManagementSystem;
import Backend.BusinessLayer.Suppliers.OrderItem;
import Backend.BusinessLayer.Suppliers.SupplierController;
import Backend.BusinessLayer.Tools.Pair;
import Backend.PersistenceLayer.StockDAL.PendingOrdersDAO;

import java.util.List;

public class OrderHandler {

    private final StockManagementSystem sms;
    private final SupplierController sc;
    private final DeliveryController sd;
    private final PendingOrdersDAO pendingOrdersDAO = new PendingOrdersDAO();


    public OrderHandler(StockManagementSystem sms, SupplierController sc, DeliveryController serviceDelivery) {
        this.sms = sms;
        this.sc = sc;
        this.sd = serviceDelivery;
        sendCallBacks();
    }

    private void sendCallBacks(){
        sms.setCollectOrderCallBack(this::makeCollectOrder);
        sms.setGetOrderItemsCallback(this::getOrderItems);
        sms.setBranchOrdersCallBack((this::getBranchOrders));
        sms.setCompleteOrderCallback(this::completeOrderCallback);
        sc.setGetProductNameCallback(this::getProductName);
        sc.setCompleteDeliveryCallback(this::completeDelivery);
        sd.setCompleteDeliveryCallback(this::completeDelivery);
    }

    private List<Integer> getBranchOrders(String branchAddress) {
        return pendingOrdersDAO.getBranchPendingOrders(branchAddress);
    }

    // input: (orderId)
    //sendOrder deliveries , callback(orderID) => list<OrderItems> ,
    private void completeOrderCallback(int orderID, List<OrderItem> orderProducts) throws Exception {
        sc.completeOrderCallback(orderID,orderProducts);
        pendingOrdersDAO.removePendingOrder(orderID);
    }


    private void completeDelivery(int orderID,String branchAddress) {
        pendingOrdersDAO.addPendingOrder(orderID,branchAddress);
    }

    private List<OrderItem> getOrderItems(int orderID,String branch){
        return sc.getOrderItems(orderID,branch);
    }


    private  void makeCollectOrder(Pair<Integer,Integer> orderInfo, String address) throws Exception {
        sc.makeCollectOrder(orderInfo.getFirst(),orderInfo.getSecond(),address);
    }

    public String getProductName(int productId) throws Exception {
        return sms.getProductName(productId);
    }
}
