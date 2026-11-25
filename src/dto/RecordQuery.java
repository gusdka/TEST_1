package dto;

import model.VehicleType;
import java.time.LocalDate;

public record RecordQuery(
        String plateContains, LocalDate from, LocalDate to, VehicleType type) {}