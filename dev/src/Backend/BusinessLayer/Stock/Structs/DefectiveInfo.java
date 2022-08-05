package Backend.BusinessLayer.Stock.Structs;

import Backend.BusinessLayer.Stock.Interfaces.StatusEnum;

import java.util.List;

public class DefectiveInfo {

    public int productId;
    public String productName;
    public StatusEnum status;

    public DefectiveInfo(int productId, String productName, StatusEnum status) {
        this.productId = productId;
        this.productName = productName;
        this.status = status;
    }
}
