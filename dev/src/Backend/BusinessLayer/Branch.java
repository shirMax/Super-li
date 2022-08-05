package Backend.BusinessLayer;

import Backend.BusinessLayer.Deliveries.Enums.Area_Enum;

public class Branch {
    private String branchName;
    private Area_Enum area;
    private String Address;


    public Branch(String branchName, Area_Enum area, String address) {
        this.branchName = branchName;
        this.area = area;
        Address = address;
    }

    public String getBranchName() {
        return branchName;
    }

    public void setBranchName(String branchName) {
        this.branchName = branchName;
    }

    public Area_Enum getArea() {
        return area;
    }

    public void setArea(Area_Enum area) {
        this.area = area;
    }

    public String getAddress() {
        return Address;
    }

    public void setAddress(String address) {
        Address = address;
    }
}
