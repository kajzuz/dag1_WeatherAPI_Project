package se.systementor.dag1.dto;

import java.time.LocalDateTime;

public class ForcastAverageTempDTO {

    private LocalDateTime date;

    private int hour;

    private float averageTemp;


    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
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
