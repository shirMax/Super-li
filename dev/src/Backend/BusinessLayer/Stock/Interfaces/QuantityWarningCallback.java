package Backend.BusinessLayer.Stock.Interfaces;

import Backend.BusinessLayer.Stock.Structs.StockQuantityInfo;

public interface QuantityWarningCallback {
    public void call(StockQuantityInfo info) throws Exception;
}
