package service;

import dto.*;
import exception.ParkingException;
import model.ParkingRecord;
import model.Pass;
import model.SpotType;
import model.VehicleType;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class ParkingServiceImpl implements ParkingService{
    @Override
    public EntryResult enter(EntryRequest request) throws ParkingException {
        return null;
    }

    @Override
    public Receipt exit(String plate) throws ParkingException {
        return null;
    }

    @Override
    public int estimateFee(String plate) throws ParkingException {
        return 0;
    }

    @Override
    public List<String> getFloors() {
        return List.of();
    }

    @Override
    public List<SpotStatus> getFloorStatus(String floor) {
        return List.of();
    }

    @Override
    public Map<SpotType, Integer> getEmptyCounts() {
        return Map.of();
    }

    @Override
    public Pass registerPass(String plate, LocalDate start, int months) throws ParkingException {
        return null;
    }

    @Override
    public Pass extendPass(String plate, int months) throws ParkingException {
        return null;
    }

    @Override
    public List<Pass> getPasses() {
        return List.of();
    }

    @Override
    public List<Pass> getExpiringPasses(int withinDays) {
        return List.of();
    }

    @Override
    public List<ParkingRecord> searchRecords(RecordQuery query) {
        return List.of();
    }

    @Override
    public Map<LocalDate, Integer> getDailySales(LocalDate from, LocalDate to) {
        return Map.of();
    }

    @Override
    public Map<VehicleType, Integer> getSalesByType(LocalDate from, LocalDate to) {
        return Map.of();
    }

    @Override
    public int[] getHourlyEntries(LocalDate date) {
        return new int[0];
    }
}
