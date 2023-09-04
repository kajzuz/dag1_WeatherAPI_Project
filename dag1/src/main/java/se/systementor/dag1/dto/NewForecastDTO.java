package se.systementor.dag1.dto;

import java.time.LocalDateTime;

public class NewForecastDTO { //Limit data for db, it's ok to have them as public but we prefer private. Min and maxValue add

    private LocalDateTime date;

    private int hour;

    private float temperature;

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

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }
}
