package jfh.jawstestapp1a.common;


public class Constants {

    //AWS Configuration
    public final static String TRACK_API_PATH  = "/tracker";
    public static final String SR_API_PATH     = "/spot";
    public static final String SR_IMAGE_BUCKET = "trackerapp-sr-images";
    public static final String SR_IMAGES_ARN   = "arn:aws:s3:::trackerapp-sr-images";

    //APIs
    public final static String POST = "POST";
    public final static String GET  = "GET";

    //Message Types
    public final static String POST_TRACK_MSG_TYPE    = "tracker_location";
    public final static String POST_TRACK_MSG_VERSION = "0.1";
    public final static String POST_SR_MSG_TYPE       = "spot_report_post";
    public final static String POST_SR_MSG_VERSION    = "0.1";


    public static final String SNS_TRACK_NOTIFY       = "track_notify";
    public static final String SNS_SPOT_REPORT_NOTIFY = "spot_report_notify";

    //Activity data
    public static final String INTENT_USER_NAME       = "user_name";


    //Location - broadcast from GPSService
    public static final String INTENT_GPS_UPDATE_ACTION = "location_update";  //From GPSService
    public static final String INTENT_GPS_LAT           = "location_lat";
    public static final String INTENT_GPS_LONG          = "location_long";
    public static final String INTENT_GPS_TIME          = "location_time";
    public static final String INTENT_GPS_ACCURACY      = "location_accuracy" ;
    public static final String INTENT_GPS_ALTITUDE      = "location_altitude" ;
    public static final String INTENT_GPS_BEARING       = "location_bearing" ;
    public static final String INTENT_GPS_SPEED         = "location_speed" ;

    //Track Broadcast - broadcast from PushListenerService
    public static final String INTENT_TRACK_RCVD_ACTION = "track_rcvd"; //from PushListenerService
    public static final String INTENT_TRACK_JSON        = "track_json";
    public static final String INTENT_SR_RCVD_ACTION    = "spotReport_rcvd";
    public static final String INTENT_SR_JSON           = "spotReport_json";

    //S3 File Transfer
    public static final String INTENT_S3_TRANSFER_COMPLETE_ACTION = "upload_complete";
    public static final String INTENT_S3_ID                       = "s3_xfer_id";
    public static final String INTENT_S3_TRANSFER_STATE           = "s3_transfer_state";


    //Spot Report
    public static final int SR_THUMBNAIL_WIDTH  = 768;
    public static final int SR_THUMBNAIL_HEIGHT = 432;

    //Invalid Default Values
    public static final long INVALID_LONG_VALUE     = -1;
    public static final double INVALID_DOUBLE_VALUE = -1;
    public static final float INVALID_FLOAT_VALUE   = -1.0f;



}
