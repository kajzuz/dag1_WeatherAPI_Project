package smhi;

import smhi.Geometry;
import smhi.TimeSeries;

import java.util.ArrayList;
import java.util.Date;

public class WeatherSMHI {

    private Date approvedTime;
    private Date referenceTime;
    private Geometry geometry;
    private ArrayList<TimeSeries> timeSeries;

    public Date getApprovedTime() {
        return approvedTime;
    }

    public void setApprovedTime(Date approvedTime) {
        this.approvedTime = approvedTime;
    }

    public Date getReferenceTime() {
        return referenceTime;
    }

    public void setReferenceTime(Date referenceTime) {
        this.referenceTime = referenceTime;
    }

    public Geometry getGeometry() {
        return geometry;
    }

    public void setGeometry(Geometry geometry) {
        this.geometry = geometry;
    }

    public ArrayList<TimeSeries> getTimeSeries() {
        return timeSeries;
    }

    public void setTimeSeries(ArrayList<TimeSeries> timeSeries) {
        this.timeSeries = timeSeries;
    }



}
