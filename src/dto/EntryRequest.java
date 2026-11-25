package dto;

import model.VehicleType;

public record EntryRequest(
        String plate, VehicleType type, boolean diesel,
        int weightKg, int loadKg,
        String driverName, String phone, boolean member) {

}