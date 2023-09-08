package se.systementor.dag1.openMeteo;

public class HourlyUnitsOpenMeteo {

    private String time;
    private String temperatureS_2m;
    private String relativehumidity_2m;
    private String windspeed_10m;

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getTemperatureS_2m() {
        return temperatureS_2m;
    }

    public void setTemperature_2m(String temperature_2m) {
        this.temperatureS_2m = temperature_2m;
    }

    public String getRelativehumidity_2m() {
        return relativehumidity_2m;
    }

    public void setRelativehumidity_2m(String relativehumidity_2m) {
        this.relativehumidity_2m = relativehumidity_2m;
    }

    public String getWindspeed_10m() {
        return windspeed_10m;
    }

    public void setWindspeed_10m(String windspeed_10m) {
        this.windspeed_10m = windspeed_10m;
    }
}
