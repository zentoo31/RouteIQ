package com.zentoodevs.login.repository.models;

public class Bus {
    private String nameShort;
    private String distance;
    private String duration;
    private int color;

    private String durationAll;
    private String distanceAll;

    public Bus(String nameShort, String distance, String duration,int color, String durationAll,String distanceAll){
        this.nameShort = nameShort;
        this.distance = distance;
        this.duration = duration;
        this.color = color;
        this.durationAll = durationAll;
        this.distanceAll = distanceAll;
    }

    public String getDistance() {
        return distance;
    }

    public String getDuration() {
        return duration;
    }

    public String getNameShort() {
        return nameShort;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

    public void setNameShort(String nameShort) {
        this.nameShort = nameShort;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    public String getDistanceAll() {
        return distanceAll;
    }

    public String getDurationAll() {
        return durationAll;
    }

    public void setDistanceAll(String distanceAll) {
        this.distanceAll = distanceAll;
    }

    public void setDurationAll(String durationAll) {
        this.durationAll = durationAll;
    }
}
