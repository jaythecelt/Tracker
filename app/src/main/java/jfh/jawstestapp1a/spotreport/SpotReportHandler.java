package jfh.jawstestapp1a.spotreport;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;

import jfh.jawstestapp1a.api.SpotReportAPI;
import jfh.jawstestapp1a.common.Constants;
import jfh.jawstestapp1a.dao.SpotReport;
import jfh.jawstestapp1a.dao.Track;
import jfh.jawstestapp1a.utilities.Util;

import static jfh.jawstestapp1a.common.Constants.INTENT_S3_TRANSFER_COMPLETE_ACTION;

public class SpotReportHandler {
    private static final String TAG = SpotReportHandler.class.getSimpleName();

    private static SpotReportHandler spotReportHandler = null;
    private static XferBroadcastReceiver xferBroadcastReceiver = null;

    private Context context = null;
    private Activity activity = null;

    private Uri imageURI = null;
    private File imageFile = null;
    private Track track = null;

    private SpotReport spotReport;

    private SpotReportHandler() {
        if (xferBroadcastReceiver == null)
            xferBroadcastReceiver = new XferBroadcastReceiver();
    }

    public static SpotReportHandler getInstance(@NonNull Context context, @NonNull Activity activity) {
        if (spotReportHandler == null) {
            spotReportHandler = new SpotReportHandler();
        }
        //TODO validate parameters
        spotReportHandler.context = context;
        spotReportHandler.activity = activity;

        return spotReportHandler;
    }


    /**
     *
     * @param track
     */
    public void createAndPost(String userID, Track track) {
        Log.d(TAG, "createAndPost() called with: track = [" + track + "]");

        spotReport = new SpotReport();
        if (imageURI == null) {
            //TODO handle when image is null
        }
        this.track = track;
        String tn = saveImageToS3();

        //While the full size image is being uploaded to S3, assemble the spot report.
        spotReport.setSrImageARN(Constants.SR_IMAGES_ARN);
        spotReport.setSrImageKey(imageFile.getName());
        spotReport.setSrLat(track.getTrackLat());
        spotReport.setSrLong(track.getTrackLong());
        spotReport.setSrStatus(track.getTrackStatus());
        spotReport.setSrTimestamp(track.getTrackTimestamp());
        spotReport.setSrThumbnail(tn);
        spotReport.setUserId(userID);
        spotReport.setTitle(userID + " " + track.getTrackTimestamp());
        spotReport.setSrDescription("description");
        Gson gson = new Gson();
        Log.d(TAG, "createAndPost: " + gson.toJson(spotReport));

        Log.d(TAG, "createAndPost: " + spotReport.toString());

        SpotReportAPI api = new SpotReportAPI();
        api.postSpotReport(spotReport);
    }

    public Uri getImageURI() {
        return imageURI;
    }

    public void setImage(Uri imageURI, File imageFile) {
        this.imageURI = imageURI;
        this.imageFile = imageFile;
    }

    public Track getTrack() {
        return track;
    }

    /**
     * Saves the image to S3 (previously set by the caller) and generates a thumbnail image.
     * @return Base64 encoded thumbnail image of the spot report image.
     */
    private String saveImageToS3() {
        Log.d(TAG, "saveImageToS3() called");
        String fileKey = imageFile.getName();

        //Make a thumbnail from the image
        Bitmap bigImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        Log.d(TAG, "saveImageToS3: Loaded bitmap image" + bigImage);
        Bitmap tnImage = ThumbnailUtils.extractThumbnail(bigImage, Constants.SR_THUMBNAIL_WIDTH, Constants.SR_THUMBNAIL_HEIGHT, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        //Show the thumbnail in a toast message.
        Util.showImageInToast(context, activity, tnImage);

        //Upload the full size image to S3 using the S3 TransferUtility
        TransferUtilityHelper tImage = new TransferUtilityHelper(context);
        int imageXferId = tImage.uploadFile(Constants.SR_IMAGE_BUCKET, fileKey, imageFile);

        //Convert the thumbnail to base64 to include in the spot report API call.
        String tnB64 = Util.BitMapToBase64JPG(tnImage);

        return tnB64;

    }


    public class XferBroadcastReceiver extends BroadcastReceiver {
        private final String TAG = XferBroadcastReceiver.class.getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case INTENT_S3_TRANSFER_COMPLETE_ACTION:

                    break;
                default:
                    Log.e(TAG, "onReceive: Unexpected action: " + intent.getAction());
            }


        }
    }
}