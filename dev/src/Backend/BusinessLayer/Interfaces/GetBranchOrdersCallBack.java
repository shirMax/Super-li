package Backend.BusinessLayer.Interfaces;

import java.util.List;

public interface GetBranchOrdersCallBack {
    List<Integer> getBranchOrders(String branchAddress);
}
