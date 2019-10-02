package com.example.sistemas.myapplication;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class MyLocation {
    private Date fecha;
    private Double latitud;
    private double longitud;

    public MyLocation() {

    }

    public Date getFecha() {
        return fecha;
    }

    public void setFecha(Date fecha) {
        this.fecha = fecha;
    }

    public Double getLatitud() {
        return latitud;
    }

    public void setLatitud(Double latitud) {
        this.latitud = latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public JSONObject toJSON () {
        JSONObject obj = new JSONObject();
        try {
            obj.put("latitud", getLatitud());
            obj.put("longitud", getLongitud());
            obj.put("date", getFecha());
        }
        catch (JSONException e) {
        e.printStackTrace();
        }
        return obj;
    }
}
