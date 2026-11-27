package ui;

import service.ParkingService;

import javax.swing.*;

public class MainFrame extends JFrame{
    private final ParkingService service; // 화면이 아는 유일한 백엔드 창구

    public MainFrame(ParkingService service){
        this.service = service;

        setTitle("주차장 관리 시스템");
        setSize(1000, 700);
        setLocationRelativeTo(null);                // 화면 가운데에 띄우기
        setDefaultCloseOperation(EXIT_ON_CLOSE);    // x 누르면 프로그램 종료

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("입출차", new JPanel());   // 다음 단계: EntryPanel로 교체
        tabs.addTab("현황 맵", new JPanel());
        tabs.addTab("정기권", new JPanel());
        tabs.addTab("기록 조회", new JPanel());
        tabs.addTab("통계", new JPanel());

        add(tabs);
    }

    public static void main(String[] args) {
        // 12/04 통합 때 이 줄의 MockParkingService만 실제 구현체로 바꾼다
        ParkingService service = new MockParkingService();

        SwingUtilities.invokeLater(() -> new MainFrame(service).setVisible(true));
    }
}
