package dto;

import java.time.LocalDateTime;
import java.util.List;

public record Receipt(
        String plate, String spotCode,
        LocalDateTime inTime, LocalDateTime outTime, long minutes,
        int baseFee,
        int surchargeAmount, List<String> surchargeReasons,
        int discountAmount, String discountReason,
        boolean passApplied,
        int finalFee) {}