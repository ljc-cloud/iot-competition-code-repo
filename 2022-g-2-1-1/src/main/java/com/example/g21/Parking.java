package com.example.g21;

public class Parking {
    private int id;
    private String openTime;
    private String closeTime;
    private long continueTime;

    public Parking(int id, String openTime, String closeTime) {
        this.id = id;
        this.openTime = openTime;
        this.closeTime = closeTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOpenTime() {
        return openTime;
    }

    public void setOpenTime(String openTime) {
        this.openTime = openTime;
    }

    public String getCloseTime() {
        return closeTime;
    }

    public void setCloseTime(String closeTime) {
        this.closeTime = closeTime;
    }

    public long getContinueTime() {
        return continueTime;
    }

    public void setContinueTime(long continueTime) {
        this.continueTime = continueTime;
    }
}
