package jfh.jawstestapp1a.Messages;


import jfh.jawstestapp1a.dao.SpotReport;

public class SpotReportSNSMessage extends BaseMessage {
    public static final String SPOT_REPORT_KEY = "spotReport";

    private SpotReport spotReport;

    public SpotReport getSpotReport() {
        return spotReport;
    }

    public void setSpotReport(SpotReport spotReport) {
        this.spotReport = spotReport;
    }
}
