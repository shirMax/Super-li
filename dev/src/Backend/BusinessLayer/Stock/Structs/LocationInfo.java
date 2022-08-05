package Backend.BusinessLayer.Stock.Structs;

public class LocationInfo {
    public int wareHouseShelfId;
    public int wareHouseAmount;
    public int storeShelfId;
    public int storeAmount;

    public LocationInfo(int wareHouseShelfId, int wareHouseAmount, int storeShelfId, int storeAmount) {
        this.wareHouseShelfId = wareHouseShelfId;
        this.wareHouseAmount = wareHouseAmount;
        this.storeShelfId = storeShelfId;
        this.storeAmount = storeAmount;
    }
}
