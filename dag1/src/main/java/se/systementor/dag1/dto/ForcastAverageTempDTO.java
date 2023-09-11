package se.systementor.dag1.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class ForcastAverageTempDTO {

    private LocalDate date;

    private int hour;

    private float averageTemp;


    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public float getAverageTemp() {
        return averageTemp;
    }

    public void setAverageTemp(float averageTemp) {
        this.averageTemp = averageTemp;
    }
}
