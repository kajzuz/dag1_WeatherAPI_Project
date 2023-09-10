package se.systementor.dag1.smhi;


import java.util.ArrayList;
import java.util.Date;

//@JsonIgnoreProperties(ignoreUnknown = true)
public class TimeSeries {

    private Date validTime;
    private ArrayList<Parameter> parameters;

    public Date getValidTime() {
        return validTime;
    }

    public void setValidTime(Date validTime) {
        this.validTime = validTime;
    }

    public ArrayList<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(ArrayList<Parameter> parameters) {
        this.parameters = parameters;
    }


}



