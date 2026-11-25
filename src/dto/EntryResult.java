package dto;

import model.SpotType;
import java.time.LocalDateTime;

public record EntryResult(
        String plate, String spotCode, SpotType spotType,
        boolean fallback,               // 대체 주차면 배정 여부
        LocalDateTime entryTime) {}