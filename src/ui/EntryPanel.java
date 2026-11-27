package ui;

import model.VehicleType;
import service.ParkingService;

import javax.swing.*;
import java.awt.*;

public class EntryPanel extends JPanel {

    private final ParkingService service;

    // 콤보박스에 보일 한글 이름과 실제 enum 값 (같은 순서)
    private static final String[] TYPE_LABELS = {"일반 승용차", "경차", "전기차", "트럭"};
    private static final VehicleType[] TYPE_VALUES = {
            VehicleType.CAR, VehicleType.COMPACT, VehicleType.ELECTRIC, VehicleType.TRUCK};

    // 입차 입력
    private final JTextField plateField = new JTextField(10);
    private final JComboBox<String> typeBox = new JComboBox<>(TYPE_LABELS);
    private final JTextField loadField = new JTextField(6);
    private final JCheckBox dieselCheck = new JCheckBox("디젤");
    private final JTextField nameField = new JTextField(8);
    private final JTextField phoneField = new JTextField(11);
    private final JCheckBox memberCheck = new JCheckBox("회원");
    private final JButton enterButton = new JButton("입차");

    // 출차 입력
    private final JTextField exitPlateField = new JTextField(10);
    private final JButton exitButton = new JButton("출차");

    // 결과 표시
    private final JLabel resultLabel = new JLabel("차량 정보를 입력하세요.");

    public EntryPanel(ParkingService service) {


        this.service = service;

        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));   // 위에서 아래로 쌓기
        setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));

        add(fitHeight(createEntrySection()));
        add(Box.createVerticalStrut(12));
        add(fitHeight(createExitSection()));
        add(Box.createVerticalStrut(12));
        add(fitHeight(createResultSection()));
        add(Box.createVerticalGlue());

        // 차종을 바꿀 때마다 칸 상태 갱신
        typeBox.addActionListener(e -> updateFieldsByType());
        updateFieldsByType();
        add(Box.createVerticalGlue());                             // 남는 공간은 아래로
    }

    // 지금 선택된 차종 (화면의 한글 이름 -> enum)
    private VehicleType selectedType() {
        return TYPE_VALUES[typeBox.getSelectedIndex()];
    }

    // 입력 칸 동작 표
    private void updateFieldsByType() {
        VehicleType type = selectedType();

        boolean truck = (type == VehicleType.TRUCK);
        loadField.setEnabled(truck);
        if (!truck) loadField.setText("");

        if (type == VehicleType.CAR) {
            dieselCheck.setEnabled(true);       // 사용자가 고를 수 있음
            dieselCheck.setSelected(false);     // 이전 차종의 체크 상태 초기화
        } else {
            dieselCheck.setEnabled(false);      // 고정
            dieselCheck.setSelected(truck);     // 트럭이면 체크, 경차 전기차면 해제
        }
    }

    private JPanel createEntrySection() {
        JPanel section = new JPanel(new GridLayout(4, 1, 0, 6));   // 4줄
        section.setBorder(BorderFactory.createTitledBorder("입차"));

        section.add(row(new JLabel("차량번호"), plateField, new JLabel("차종"), typeBox));
        section.add(row(new JLabel("적재량(kg)"), loadField, dieselCheck));
        section.add(row(new JLabel("운전자"), nameField, new JLabel("연락처"), phoneField));
        section.add(row(memberCheck, enterButton));
        return section;
    }

    private JPanel createExitSection() {
        JPanel section = new JPanel(new GridLayout(1, 1));
        section.setBorder(BorderFactory.createTitledBorder("출차"));
        section.add(row(new JLabel("차량번호"), exitPlateField, exitButton));
        return section;
    }

    private JPanel createResultSection() {
        JPanel section = new JPanel(new GridLayout(1, 1));
        section.setBorder(BorderFactory.createTitledBorder("결과"));
        section.add(row(resultLabel));
        return section;
    }

    // BoxLayout은 섹션을 세로로 늘리므로, 높이를 내용만큼 고정
    private JPanel fitHeight(JPanel section) {
        section.setMaximumSize(new Dimension(Integer.MAX_VALUE, section.getPreferredSize().height));
        return section;
    }

    // 컴포넌트들을 왼쪽부터 한 줄로 나란히 놓는 도우미
    private JPanel row(JComponent... components) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        for (JComponent c : components) p.add(c);
        return p;
    }
}
