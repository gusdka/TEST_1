package model;

import java.time.LocalDateTime;

public class ParkingRecord {
    private final int id;
    private final String plate;
    private final VehicleType type;
    private final String spotCode;
    private final LocalDateTime inTime;
    private LocalDateTime outTime;  // 출차 전이면 null
    private int fee;

    public ParkingRecord(int id, String plate, VehicleType type, String spotCode, LocalDateTime inTime){
        this.id = id;
        this.plate = plate;
        this.type = type;
        this.spotCode = spotCode;
        this.inTime = inTime;
    }

    public int getId(){
        return id;
    }
    public String getPlate(){
        return plate;
    }
    public VehicleType getType(){
        return type;
    }
    public String getSpotCode(){
        return spotCode;
    }
    public LocalDateTime getInTime(){
        return inTime;
    }
    public LocalDateTime getOutTime() {
        return outTime;
    }
    public int getFee() {
        return fee;
    }
    public boolean isParking() {
        return outTime == null;
    }

    // TODO(백엔드): 출차 처리 메서드
}
