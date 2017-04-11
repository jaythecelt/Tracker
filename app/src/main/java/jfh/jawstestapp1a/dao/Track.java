package jfh.jawstestapp1a.dao;



public class Track {

    private String userId;
    private String trackTimestamp;
    private int trackStatus;
    private double trackLat;
    private double trackLong;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getTrackTimestamp() {
        return trackTimestamp;
    }

    public void setTrackTimestamp(String trackTimestamp) {
        this.trackTimestamp = trackTimestamp;
    }

    public int getTrackStatus() {
        return trackStatus;
    }

    public void setTrackStatus(int trackStatus) {
        this.trackStatus = trackStatus;
    }

    public double getTrackLat() {
        return trackLat;
    }

    public void setTrackLat(double trackLat) {
        this.trackLat = trackLat;
    }

    public double getTrackLong() {
        return trackLong;
    }

    public void setTrackLong(double trackLong) {
        this.trackLong = trackLong;
    }


    @Override
    public String toString() {
        return "Track{" +
                "userId='" + userId + '\'' +
                ", trackTimestamp='" + trackTimestamp + '\'' +
                ", trackStatus=" + trackStatus +
                ", trackLat=" + trackLat +
                ", trackLong=" + trackLong +
                '}';
    }
}
