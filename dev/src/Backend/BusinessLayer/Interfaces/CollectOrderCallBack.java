package Backend.BusinessLayer.Interfaces;

import Backend.BusinessLayer.Tools.Pair;

public interface CollectOrderCallBack {
    void makeCollectOrder(Pair<Integer,Integer> orderInfo,String address) throws Exception;
}
