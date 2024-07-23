package com.zentoodevs.login.repository.models;

import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

public class Ruta {
    private List<Bus> buses;
    private List<PolylineOptions> polylines;

    public Ruta(List<Bus> buses, List<PolylineOptions> polylines) {
        this.buses = buses;
        this.polylines = polylines;
    }
    public Ruta(){
    }

    public List<Bus> getBuses() {
        return buses;
    }

    public void setBuses(List<Bus> buses) {
        this.buses = buses;
    }
    public List<PolylineOptions> getPolylines() {
        return polylines;
    }
}