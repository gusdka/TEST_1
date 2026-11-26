package service;

import dto.*;
import exception.ParkingException;
import model.Pass;
import model.ParkingRecord;
import model.SpotType;
import model.VehicleType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface ParkingService {

    // 입출차
    EntryResult enter(EntryRequest request) throws ParkingException;
    Receipt exit(String plate) throws ParkingException;
    int estimateFee(String plate) throws ParkingException;

    // 현황
    List<String> getFloors();
    List<SpotStatus> getFloorStatus(String floor);
    Map<SpotType, Integer> getEmptyCounts();

    // 정기권
    Pass registerPass(String plate, LocalDate start, int months) throws ParkingException;
    Pass extendPass(String plate, int months) throws ParkingException;
    List<Pass> getPasses();
    List<Pass> getExpiringPasses(int withinDays);

    // 기록
    List<ParkingRecord> searchRecords(RecordQuery query);

    // 통계
    Map<LocalDate, Integer> getDailySales(LocalDate from, LocalDate to);
    Map<VehicleType, Integer> getSalesByType(LocalDate from, LocalDate to);
    int[] getHourlyEntries(LocalDate date);
}