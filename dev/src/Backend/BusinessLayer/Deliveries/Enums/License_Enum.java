package Backend.BusinessLayer.Deliveries.Enums;

public enum License_Enum {
    B(1),
    C(2),
    C1(3),
    CE(4);

    private int id;

    License_Enum(int id) {
        this.id = id;
    }

    public boolean canIDriveIt(License_Enum l){
        return l.id <= this.id;
    }

}

