package jfh.jawstestapp1a.dao;


public class SpotReport {

    private String userId;
    private String srTimestamp;
    private String title;
    private String srDescription;
    private int    srStatus;
    private double srLat;
    private double srLong;
    private String srThumbnail;
    private String srImageARN;
    private String srImageKey;

    public String getSrImageKey() {
        return srImageKey;
    }

    public void setSrImageKey(String srImageKey) {
        this.srImageKey = srImageKey;
    }

    public String getSrImageARN() {
        return srImageARN;
    }

    public void setSrImageARN(String srImageARN) {
        this.srImageARN = srImageARN;
    }

    public String getSrThumbnail() {
        return srThumbnail;
    }

    public void setSrThumbnail(String srThumbnail) {
        this.srThumbnail = srThumbnail;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSrTimestamp() {
        return srTimestamp;
    }

    public void setSrTimestamp(String srTimestamp) {
        this.srTimestamp = srTimestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSrDescription() {
        return srDescription;
    }

    public void setSrDescription(String srDescription) {
        this.srDescription = srDescription;
    }

    public int getSrStatus() {
        return srStatus;
    }

    public void setSrStatus(int srStatus) {
        this.srStatus = srStatus;
    }

    public double getSrLat() {
        return srLat;
    }

    public void setSrLat(double srLat) {
        this.srLat = srLat;
    }

    public double getSrLong() {
        return srLong;
    }

    public void setSrLong(double srLong) {
        this.srLong = srLong;
    }

    @Override
    public String toString() {
        return "SpotReport{" +
                "userId='" + userId + '\'' +
                ", srTimestamp='" + srTimestamp + '\'' +
                ", title='" + title + '\'' +
                ", srDescription='" + srDescription + '\'' +
                ", srStatus=" + srStatus +
                ", srLat=" + srLat +
                ", srLong=" + srLong +
                ", srThumbnail='" + srThumbnail.substring(0, 10) + "..." + '\'' +
                ", srImageARN='" + srImageARN + '\'' +
                ", srImageKey='" + srImageKey + '\'' +
                '}';
    }
}
