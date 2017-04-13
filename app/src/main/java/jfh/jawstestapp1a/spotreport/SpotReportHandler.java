package jfh.jawstestapp1a.spotreport;


import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import jfh.jawstestapp1a.api.SpotReportAPI;
import jfh.jawstestapp1a.common.Constants;
import jfh.jawstestapp1a.dao.SpotReport;
import jfh.jawstestapp1a.dao.Track;
import jfh.jawstestapp1a.utilities.Util;

import static jfh.jawstestapp1a.common.Constants.INTENT_IMAGE_THUMBNAIL_UPLOAD_COMPLETE_ACTION;
import static jfh.jawstestapp1a.common.Constants.INTENT_XFER_HELPER_STATUS;

public class SpotReportHandler {
    private static final String TAG = SpotReportHandler.class.getSimpleName();

    private static SpotReportHandler spotReportHandler = null;
    private static XferBroadcastReceiver xferBroadcastReceiver = null;

    private Context context = null;
    private Activity activity = null;

    private File imageFile = null;
    private File thumbnailFile = null;

    private Track track = null;
    private SpotReport spotReport;

    public SpotReportHandler(@NonNull Context context, @NonNull Activity activity) {
        if (xferBroadcastReceiver == null) xferBroadcastReceiver = new XferBroadcastReceiver();

        IntentFilter filter = new IntentFilter(INTENT_IMAGE_THUMBNAIL_UPLOAD_COMPLETE_ACTION);
        context.registerReceiver(xferBroadcastReceiver, filter);

        this.context = context;
        this.activity = activity;
    }

    public void cleanup() {
        if (xferBroadcastReceiver != null) {
            Log.i(TAG, "unregister broadcast receiver.");
            context.unregisterReceiver(xferBroadcastReceiver);
        }
    }

    /**
     *
     * @param track
     */
    public void createAndPost(String userID, Track track) {
        Log.d(TAG, "createAndPost() called with: track = [" + track + "]");

        spotReport = new SpotReport();
        if (imageFile == null) {
            Log.e(TAG, "createAndPost: imageFile is NULL!");
            //TODO handle when image is null
        }
        this.track = track;

        //Assemble the spot report.
        spotReport.setSrImageARN(Constants.SR_IMAGES_ARN);
        spotReport.setSrImageKey(imageFile.getName());
        spotReport.setSrLat(track.getTrackLat());
        spotReport.setSrLong(track.getTrackLong());
        spotReport.setSrStatus(track.getTrackStatus());
        spotReport.setSrTimestamp(track.getTrackTimestamp());
        spotReport.setSrThumbnail(thumbnailFile.getName());
        spotReport.setUserId(userID);
        spotReport.setTitle(userID + " " + track.getTrackTimestamp());
        spotReport.setSrDescription("GPS: " + track.getTrackLat() + " " + track.getTrackLong());

        //Uploads the images (full size and thumbnail) to S3.
        //Send the spot report to the cloud when the thumbnail has finished
        // uploading (in XferBroadcastReceiver)
        uploadImagesToS3();
    }

    public void setImage(File imageFile, File thumbnailFile) {
        this.imageFile = imageFile;
        this.thumbnailFile = thumbnailFile;
    }

    public SpotReport getSpotReport() {
        return spotReport;
    }


    /**
     * Saves the image to S3 (previously set by the caller) and generates a thumbnail image.
     * @return Base64 encoded thumbnail image of the spot report image.
     */
    private void uploadImagesToS3() {
        Log.d(TAG, "uploadImagesToS3() called");
        String fileKey = imageFile.getName();
        String thumbnailKey = thumbnailFile.getName();

        //Make a thumbnail from the image
        Bitmap bigImage = BitmapFactory.decodeFile(imageFile.getAbsolutePath());
        Bitmap tnImage = ThumbnailUtils.extractThumbnail(bigImage, Constants.SR_THUMBNAIL_WIDTH, Constants.SR_THUMBNAIL_HEIGHT, ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

        //Save tnImage
        try {
            FileOutputStream fOut = new FileOutputStream(thumbnailFile);
            tnImage.compress(Bitmap.CompressFormat.PNG, 100, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            Log.e(TAG, "uploadImagesToS3: " + e.getMessage(), e);
        }

        //Show the thumbnail in a toast message.
        Util.showImageInToast(context, activity, tnImage);

        //Upload the full size image to S3 using the S3 TransferUtility
        TransferUtilityHelper imgXferHelper = new TransferUtilityHelper(context, null);
        int imgXferId = imgXferHelper.uploadFile(Constants.SR_IMAGE_BUCKET, fileKey, imageFile);

        //Upload the thumbnail image to S3 using the S3 TransferUtility
        TransferUtilityHelper tnXferHelper = new TransferUtilityHelper(context, Constants.INTENT_IMAGE_THUMBNAIL_UPLOAD_COMPLETE_ACTION);
        int tnXferId = tnXferHelper.uploadFile(Constants.SR_IMAGE_BUCKET, thumbnailKey, thumbnailFile);
    }


    public class XferBroadcastReceiver extends BroadcastReceiver {
        private final String TAG = XferBroadcastReceiver.class.getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case INTENT_IMAGE_THUMBNAIL_UPLOAD_COMPLETE_ACTION:
                    int status =  intent.getIntExtra(INTENT_XFER_HELPER_STATUS, TransferUtilityHelper.FAILED);
                    Log.d(TAG, "onReceive: Thumbnail upload complete with status: " + status);
                    Log.d(TAG, "onReceive: Sending spot report");

                    Gson gson = new Gson();
                    Log.d(TAG, "POSTing spot report: " + gson.toJson(spotReport));
                    SpotReportAPI api = new SpotReportAPI();
                    api.postSpotReport(spotReport);
                    break;
                default:
                    Log.e(TAG, "onReceive: Unexpected action: " + intent.getAction());
            }


        }
    }
}