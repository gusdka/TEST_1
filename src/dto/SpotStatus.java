package dto;

import model.SpotType;
import model.VehicleType;
import java.time.LocalDateTime;

public record SpotStatus(
        String spotCode, String floor, char zone, int number,
        SpotType type,
        String plate,                   // 빈 자리면 null
        VehicleType vehicleType,        // 빈 자리면 null
        LocalDateTime entryTime) {      // 빈 자리면 null

    public boolean occupied() { return plate != null; }
}