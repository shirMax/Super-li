package Backend.PersistenceLayer.DeliveriesDal;

import Backend.BusinessLayer.Deliveries.Truck;
import Backend.PersistenceLayer.DalController;

import java.sql.*;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

public class TruckDAO extends DalController {
    private HashMap<Integer,Truck> truckMap;
    final String TRUCKS_TABLE_NAME = "Trucks";
    final String TRUCK_ID_COLUMN_NAME = "LicenseNumber";
    final String LICENSE_KIND_COLUMN_NAME = "LicenseKind";
    final String WEIGHT_COLUMN_NAME = "Weight";
    final String MAX_WEIGHT_COLUMN_NAME = "MaxWeight";
    final String AVAILABLE_COLUMN_NAME = "Available";
    private boolean allLoaded;
    private int currTruck = 0;

    public TruckDAO() {
        this.truckMap = new HashMap<>();
        allLoaded = false;
    }

    public void addTruck(int licenseNumber, String licenseKind, int weight, int maxWeight , boolean isAvailable) throws Exception {
       Truck truck = selectTruck(licenseNumber,TRUCK_ID_COLUMN_NAME,TRUCKS_TABLE_NAME);
        if (truck == null) {
            try {
                insert(licenseNumber, licenseKind, weight, maxWeight, isAvailable);
                addTruckToIDMap(licenseNumber,licenseKind,weight,maxWeight,isAvailable);

            } catch (Exception e) {
                throw new Exception(e.getMessage());
            }
        }
    }

    public Truck getTruck(int licenseNumber) throws Exception {
        if(truckMap.containsKey(licenseNumber)){
            return truckMap.get(licenseNumber);
        }

        Truck truck = selectTruck(licenseNumber,TRUCK_ID_COLUMN_NAME,TRUCKS_TABLE_NAME);
        if(truck == null)
            throw new Exception("No such truck with the given licenseNumber : " +licenseNumber);
        addTruckToIDMap(truck);
        return truck;
    }

    public Collection<Truck> getAllTrucks() throws Exception {
        if(allLoaded)
            return truckMap.values();

        Collection<Truck> colTrucks = selectAllTrucks(TRUCKS_TABLE_NAME);
        if(colTrucks.isEmpty())
            throw new Exception("no trucks");
        addAllTruckToIDMap(colTrucks);
        allLoaded=true;
        return colTrucks;

    }

    private void addAllTruckToIDMap(Collection<Truck> colTrucks) {
        for (Truck truck : colTrucks){
            addTruckToIDMap(truck);
        }
    }

    private void addTruckToIDMap(Truck truck) {
        if (!truckMap.containsKey(truck.getLicenseNumber())){
            truckMap.put(truck.getLicenseNumber(),truck);
        }
    }

    private void addTruckToIDMap(int licenseNumber, String licenseKind, int weight, int maxWeight, boolean isAvailable) throws Exception {
        if(!truckMap.containsKey(licenseNumber)){
            Truck truck = new Truck(licenseNumber,licenseKind,weight,maxWeight,isAvailable);
            truckMap.put(licenseNumber,truck);
        }
    }

    private Truck selectTruck(int id1,String columnName1, String tableName) throws Exception {
        String sql = "SELECT * FROM " +tableName+ " WHERE "+ columnName1+"="+id1;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if(rs.next())
                return ConvertReaderToTruck(rs);
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }

    private Collection<Truck> selectAllTrucks(String tableName) throws Exception {
        String sql = "SELECT * FROM " +tableName;
        try (Connection conn = this.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            Collection<Truck> ctrucks = new ArrayList<>();
            while(rs.next())
                  ctrucks.add(ConvertReaderToTruck(rs));
            return ctrucks;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
        return null;
    }


    private Truck ConvertReaderToTruck(ResultSet rs) throws Exception {
        Truck result = null;
        try {
            result = new Truck(rs.getInt(1), rs.getString(2),
                    rs.getInt(3),rs.getInt(4),rs.getBoolean(5));
        }
        catch (SQLException throwables) {
            throwables.printStackTrace();
        } catch (Exception exception) {
            throw exception;
        }
        return result;

    }


    private void insert(int licenseNumber,String licenseKind , int weight, int maxWeight , boolean isAvailable) {
        String sql = MessageFormat.format("INSERT INTO {0} ({1}, {2}, {3}, {4}, {5}) VALUES(?,?,?,?,?)",
                TRUCKS_TABLE_NAME,TRUCK_ID_COLUMN_NAME, LICENSE_KIND_COLUMN_NAME, WEIGHT_COLUMN_NAME,
                MAX_WEIGHT_COLUMN_NAME,AVAILABLE_COLUMN_NAME);
        try (Connection conn = this.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1,licenseNumber);
            pstmt.setString(2,licenseKind);
            pstmt.setInt(3,weight);
            pstmt.setInt(4,maxWeight);
            pstmt.setBoolean(5,isAvailable);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }


    public void updateAvailabillity(int licenseNumber, boolean b) throws Exception {
        super.update(TRUCKS_TABLE_NAME,AVAILABLE_COLUMN_NAME,b,TRUCK_ID_COLUMN_NAME,licenseNumber);
    }

    public Truck getNextTruck(){
        int ret = currTruck;
        currTruck = (currTruck+1)%truckMap.size();
        for(Truck truck : truckMap.values()){
            if(ret == 0)
                return truck;
            ret--;
        }
        throw new IllegalArgumentException("there are no trucks");
    }
}
