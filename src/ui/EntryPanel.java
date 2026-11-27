package ui;

import dto.EntryRequest;
import dto.EntryResult;
import dto.Receipt;
import exception.InvalidInputException;
import exception.ParkingException;
import model.VehicleType;
import service.ParkingService;

import javax.swing.*;
import java.awt.*;

public class EntryPanel extends JPanel {

    private final ParkingService service;

    // 입력 규칙
    private static final String PLATE_REGEX = "^\\d{2,3}[가-힣]\\d{4}$";
    private static final String PHONE_REGEX = "^01[016789]-?\\d{3,4}-?\\d{4}$";

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

        // 버튼 연결
        enterButton.addActionListener(e -> onEnter());
        exitButton.addActionListener(e -> onExit());
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

    // -- 입차 ----------------------------
    private void onEnter(){
        try {
            EntryRequest request = readEntryRequest();      // 1) 화면 입력 검증
            EntryResult result = service.enter(request);    // 2) 서비스 호출

            String msg = result.spotCode() + "에 배정되었습니다.";
            if(result.fallback()) msg += " (전용 구역이 가득 차 대체 구역 배정)";
            resultLabel.setText(msg);
            clearEntryFields();
        } catch(ParkingException e){                         // 3) 화면 검증 오류 + 백엔드 오류를 한 곳

            showError(e.getMessage());
        }
    }

    // 입력칸 -> EntryRequest. 규칙을 어기면 InvalidInputException
    private EntryRequest readEntryRequest() throws InvalidInputException {
        String plate = readPlate(plateField);
        VehicleType type = selectedType();

        int loadKg = 0;
        if(type == VehicleType.TRUCK){
            loadKg = readInt(loadField, "적재량", 0, 30000);
        }

        String name = nameField.getText().trim();
        if(name.isEmpty() || name.length() > 20){
            throw new InvalidInputException("운전자 이름은 1~20자로 입력하세요.");
        }
        if(name.contains(",")){
            throw new InvalidInputException("이름에 쉼표(,)를 쓸 수 없습니다.");
        }

        String phone = phoneField.getText().trim();
        if(!phone.isEmpty() && !phone.matches(PHONE_REGEX)){
            throw new InvalidInputException("연락처 형식이 올바르지 않습니다 (예: 010-1234-5678).");
        }

        return new EntryRequest(plate, type, dieselCheck.isSelected(),
                0, loadKg,
                name, phone.isEmpty() ? null : phone, memberCheck.isSelected());
    }

    // -- 출차 ----------------------------
    private void onExit(){
        try {
            String plate = readPlate(exitPlateField);
            Receipt r = service.exit(plate);

            resultLabel.setText(String.format("%s 출차 완료 ㆍ 주차 %d분 ㆍ 요금 %,d원",
                    r.plate(), r.minutes(), r.finalFee()));
            exitPlateField.setText("");

            // 임시 영수증 (12/01-04에 ReceiptDialog로 교체)
            JOptionPane.showMessageDialog(this,
                    String.format("차량번호: %s%n자리: %s%n주차 시간: %d분%n최종 요금: %,d원",
                            r.plate(), r.spotCode(), r.minutes(), r.finalFee()),
                    "영수증", JOptionPane.INFORMATION_MESSAGE);
        } catch(ParkingException e){
            showError(e.getMessage());
        }
    }

    // -- 공통 도우미 --------------------------
    private String readPlate(JTextField field) throws InvalidInputException{
        String plate = field.getText().replace(" ", "");    // 공백 제거
        if(!plate.matches(PLATE_REGEX)){
            throw new InvalidInputException("차량번호 형식이 올바르지 않습니다 (예: 12가3456).");
        }
        return plate;
    }
    private int readInt(JTextField field, String label, int min, int max) throws
            InvalidInputException{
        try{
            int value = Integer.parseInt(field.getText().trim());
            if(value < min || value > max) throw new NumberFormatException();
            return value;
        } catch(NumberFormatException e){
            throw new InvalidInputException(label + "은(는) " + min + "~" + max + " 사이 숫자로 입력하세요.");
        }
    }

    private void showError(String message){
        JOptionPane.showMessageDialog(this, message, "입력 오류", JOptionPane.WARNING_MESSAGE);
    }

    private void clearEntryFields(){
        plateField.setText("");
        loadField.setText("");
        nameField.setText("");
        phoneField.setText("");
        memberCheck.setSelected(false);
        typeBox.setSelectedIndex(0);        // 일반 승용차로 되돌리면 updateFieldsByType()도 자동 호출
    }

    // -- 화면 배치 ----------------------
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
