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
    public static final String INTENT_USER_NAME       = "jfh.jawstestapp1a.user_name";

    //Location - broadcast from GPSService
    public static final String INTENT_GPS_UPDATE_ACTION = "jfh.jawstestapp1a.location_update";  //From GPSService
    public static final String INTENT_GPS_LAT           = "jfh.jawstestapp1a.location_lat";
    public static final String INTENT_GPS_LONG          = "jfh.jawstestapp1a.location_long";
    public static final String INTENT_GPS_TIME          = "jfh.jawstestapp1a.location_time";
    public static final String INTENT_GPS_ACCURACY      = "jfh.jawstestapp1a.location_accuracy" ;
    public static final String INTENT_GPS_ALTITUDE      = "jfh.jawstestapp1a.location_altitude" ;
    public static final String INTENT_GPS_BEARING       = "jfh.jawstestapp1a.location_bearing" ;
    public static final String INTENT_GPS_SPEED         = "jfh.jawstestapp1a.location_speed" ;

    //Track Broadcast - broadcast from PushListenerService
    public static final String INTENT_TRACK_RCVD_ACTION = "jfh.jawstestapp1a.track_rcvd"; //from PushListenerService
    public static final String INTENT_TRACK_JSON        = "jfh.jawstestapp1a.track_json";
    public static final String INTENT_SR_RCVD_ACTION    = "jfh.jawstestapp1a.spotReport_rcvd";
    public static final String INTENT_SR_JSON           = "jfh.jawstestapp1a.spotReport_json";

    //S3 File Transfer - Passed to TransferUtilityHelper to generate a broadcast when complete.
    public static final String INTENT_IMAGE_UPLOAD_COMPLETE_ACTION               = "jfh.jawstestapp1a.image_upload_complete";
    public static final String INTENT_IMAGE_DOWNLOAD_COMPLETE_ACTION             = "jfh.jawstestapp1a.image_download_complete";
    public static final String INTENT_IMAGE_THUMBNAIL_UPLOAD_COMPLETE_ACTION     = "jfh.jawstestapp1a.image_thumbnail_upload_complete";
    public static final String INTENT_IMAGE_THUMBNAIL_DOWNLOAD_COMPLETE_ACTION   = "jfh.jawstestapp1a.image_thumbnail_download_complete";


    public static final String INTENT_S3_TRANSFER_COMPLETE_ACTION = "jfh.jawstestapp1a.upload_complete";
    public static final String INTENT_S3_ID                       = "jfh.jawstestapp1a.s3_xfer_id";
    public static final String INTENT_XFER_HELPER_STATUS = "jfh.jawstestapp1a.s3_transfer_state";


    //Spot Report
    public static final int SR_THUMBNAIL_WIDTH  = 768;
    public static final int SR_THUMBNAIL_HEIGHT = 432;

    //Invalid Default Values
    public static final long INVALID_LONG_VALUE     = -1;
    public static final double INVALID_DOUBLE_VALUE = -1;
    public static final float INVALID_FLOAT_VALUE   = -1.0f;



}
