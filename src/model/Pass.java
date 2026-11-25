package model;

import java.time.LocalDate;

public class Pass {
    private final String plate;
    private LocalDate start;
    private LocalDate end;

    public Pass(String plate, LocalDate start, LocalDate end){
        this.plate = plate;
        this.start = start;
        this.end = end;
    }

    public String getPlate() {
        return plate;
    }
    public LocalDate getStart(){
        return start;
    }

    public LocalDate getEnd(){
        return end;
    }

    // TODO(백엔드): isValidOn(LocalData), extend(int months)
}
