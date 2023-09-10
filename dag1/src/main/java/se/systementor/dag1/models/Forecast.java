package se.systementor.dag1.models;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import se.systementor.dag1.dataSource.DataSource;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.UUID;


@Entity
public class Forecast {


    @Id// Database generates the id for us
    @GeneratedValue(strategy = GenerationType.UUID)

    private UUID id;

    private LocalDateTime date;

    private int hour;

    private Float temperature;

    private LocalDateTime created; //is this needed?

    private Instant updated; //is this needed?

   // private Instant predictionDate;

    private float longitude; //is this needed?

    private float latitude; //is this needed?

    private boolean rainOrSnow; //is this needed?

    /*private int predictionTemperature;
    private int predictionHour;*/

    private DataSource dataSource;


    public boolean isRainOrSnow() {
        return rainOrSnow;
    }

    public void setRainOrSnow(boolean rainOrSnow) {
        this.rainOrSnow = rainOrSnow;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public Instant getUpdated() {
        return updated;
    }

    public void setUpdated(Instant updated) {
        this.updated = updated;
    }

    /*public Instant getPredictionDate() {
        return predictionDate;
    }

    public void setPredictionDate(Instant predictionDate) {
        this.predictionDate = predictionDate;
    }*/

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    public Float getTemperature() {
        return temperature;
    }

    public void setTemperature(Float temperature) {
        this.temperature = temperature;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }



    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }
}
