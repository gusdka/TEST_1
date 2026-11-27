package ui;

import dto.*;
import exception.DuplicateVehicleException;
import exception.ParkingException;
import exception.ParkingFullException;
import exception.VehicleNotFoundException;
import model.ParkingRecord;
import model.Pass;
import model.SpotType;
import model.VehicleType;
import service.ParkingService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.EnumMap;

public class MockParkingService implements ParkingService {
    private static final List<String> FLOORS = List.of("B1", "1F", "2F");
    private static final char[] ZONES = {'A', 'B', 'C', 'D'};
    private static final int SPOTS_PER_ZONE = 6;

    // 가짜 주차장: 72칸
    private final List<SpotStatus> spots = new ArrayList<>();

    public MockParkingService() {
        for (String floor : FLOORS) {
            for (char zone : ZONES) {
                for (int n = 1; n <= SPOTS_PER_ZONE; n++) {
                    String code = String.format("%s-%c-%02d", floor, zone, n);
                    spots.add(new SpotStatus(code, floor, zone, n,
                            spotTypeOf(floor, zone), null, null, null));
                }
            }
        }
        // 미리 세워 둔 차 몇 대
        occupy("1F-A-01", "12가3456", VehicleType.CAR, LocalDateTime.now().minusHours(2));
        occupy("1F-C-02", "45다5678", VehicleType.ELECTRIC, LocalDateTime.now().minusMinutes(40));
        occupy("B1-D-01", "80버1234", VehicleType.TRUCK, LocalDateTime.now().minusHours(5));
    }

    // SRS 7-4 레이아웃: 1F-C 전기차, 1F-D 경차, B1-D 대형, 나머지 일반
    private SpotType spotTypeOf(String floor, char zone){
        if(floor.equals("1F") && zone == 'C') return SpotType.EV;
        if(floor.equals("1F") && zone == 'D') return SpotType.COMPACT;
        if(floor.equals("B1") && zone == 'D') return SpotType.LARGE;
        return SpotType.GENERAL;
    }

    // record는 값을 못 바꾸므로, 같은 칸을 새 record로 갈아 끼운다
    private void occupy(String code, String plate, VehicleType type, LocalDateTime time){
        for(int i = 0; i < spots.size(); i++){
            SpotStatus s = spots.get(i);
            if(s.spotCode().equals(code)){
                spots.set(i, new SpotStatus(s.spotCode(), s.floor(), s.zone(), s.number(),
                        s.type(), plate, type, time));
                return ;
            }
        }
    }

    private void release(SpotStatus s){
        spots.set(spots.indexOf(s), new SpotStatus(s.spotCode(), s.floor(), s.zone(), s.number(),
                s.type(), null, null, null));
    }

    private Optional<SpotStatus> findByPlate(String plate){
        return spots.stream().filter(s -> plate.equals(s.plate())).findFirst();
    }

    // -- 입출차 --------------------------------
    @Override
    public EntryResult enter(EntryRequest request) throws ParkingException {
        if(findByPlate(request.plate()).isPresent()){
            throw new DuplicateVehicleException("이미 주차 중인 차량입니다: " + request.plate());
        }

        // Mock이라 단순 switch. 실제 배정 규칙(다형성)은 백엔드 ParkingLot이 담당
        List<SpotType> order = switch(request.type()){
            case TRUCK -> List.of(SpotType.LARGE);
            case COMPACT -> List.of(SpotType.COMPACT, SpotType.GENERAL);
            case ELECTRIC -> List.of(SpotType.EV, SpotType.GENERAL);
            case CAR -> List.of(SpotType.GENERAL);
        };
        for(SpotType want : order){
            for(SpotStatus s : spots){
                if(!s.occupied() && s.type() == want){
                    LocalDateTime now = LocalDateTime.now();
                    occupy(s.spotCode(), request.plate(), request.type(), now);
                    boolean fallback = want != order.get(0);
                    return new EntryResult(request.plate(), s.spotCode(), want, fallback, now);
                }
            }
        }
        throw new ParkingFullException("배정할 수 있는 빈 주차면이 없습니다.");
    }

    @Override
    public Receipt exit(String plate) throws ParkingException {
        SpotStatus s = findByPlate(plate)
                .orElseThrow(() -> new VehicleNotFoundException("주차 중이 아닌 차량입니다: " + plate));
        LocalDateTime out = LocalDateTime.now();
        long minutes = Duration.between(s.entryTime(), out).toMinutes();
        int fee = mockFee(minutes);
        release(s);
        return new Receipt(plate, s.spotCode(), s.entryTime(), out, minutes,
                fee, 0, List.of(), 0, null, false, fee);
    }

    @Override
    public int estimateFee(String plate) throws ParkingException {
        SpotStatus s = findByPlate(plate)
                .orElseThrow(() -> new VehicleNotFoundException("주차 중이 아닌 차량입니다: " + plate));
        return mockFee(Duration.between(s.entryTime(), LocalDateTime.now()). toMinutes());
    }

    // 가짜 요금: 10분당 500원 (실제 규칙은 백엔드 FeeCalculator)
    private int mockFee(long minutes){
        return (int) ((minutes + 9) / 10) * 500;
    }

    @Override
    public List<String> getFloors() {
        return FLOORS;
    }

    @Override
    public List<SpotStatus> getFloorStatus(String floor) {
        return  spots.stream().filter(s -> s.floor().equals(floor)).toList();
    }

    @Override
    public Map<SpotType, Integer> getEmptyCounts() {
        Map<SpotType, Integer> counts = new EnumMap<>(SpotType.class);
        for(SpotType t : SpotType.values()) counts.put(t, 0);
        for(SpotStatus s : spots){
            if(!s.occupied()) counts.merge(s.type(), 1, Integer::sum);
        }
        return counts;
    }

    // -- 정기권 (나중 화면용, 지금은 최소한) ----
    private final List<Pass> passes = new ArrayList<>();

    @Override
    public Pass registerPass(String plate, LocalDate start, int months) throws ParkingException {
        Pass p = new Pass(plate, start, start.plusMonths(months).minusDays(1));
        passes.add(p);
        return p;
    }

    @Override
    public Pass extendPass(String plate, int months) throws ParkingException {
        throw new ParkingException("Mock에서는 연장을 지원하지 않습니다.");
    }

    @Override
    public List<Pass> getPasses() {
        return passes;
    }

    @Override
    public List<Pass> getExpiringPasses(int withinDays) {
        return List.of();
    }

    // -- 기록, 통계 (나중 화면용, 빈 값) -------

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
        return new int[24];
    }
}
