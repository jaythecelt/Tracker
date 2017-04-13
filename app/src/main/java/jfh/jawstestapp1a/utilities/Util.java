package jfh.jawstestapp1a.utilities;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import jfh.jawstestapp1a.R;

/*
/*
 * Handles helper functions used throughout the app.
 */


public class Util {
    private static final String TAG = Util.class.getSimpleName();

    // S3 Client instance and TransferUtility instance
    private static AmazonS3Client s3Client = null;



    // "Z" to indicate UTC, no timezone offset
    public final static String ISO_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.S'Z'";

    private static DateFormat dateFormat;

    static {
        dateFormat = new SimpleDateFormat(ISO_DATE_FORMAT);
        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        dateFormat.setTimeZone(timeZone);
    }


    //---Timestamp Methods ---//

    public static String getTimeNow() {
        String now = dateFormat.format(new Date());
        return now;
    }

    public static String getTimestamp(long utcEpochMs) {
        String date = dateFormat.format(utcEpochMs);
        return date;
    }

    public static String getTimestamp(String epochMillisec) {

        long ms = Long.parseLong(epochMillisec);
        return getTimestamp(ms);
    }


    //---Map Marker Utils---//
    public static BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }


    //---Photo Utils---//

    /**
     * Creates an image file with a unique name.
     * The filename is pre_yyyyMMdd_HHmmss.ext
     * The file is created in the external dir in DIRECTORY_PICTURES
     *
     * @param context
     * @param pre
     * @param ext
     * @return  images[0] = full size image file
     *          images[1] = thumbnail file
     * @throws IOException
     */
    public static File[] createImageFiles(@NonNull Context context, @NonNull String pre, String ext) throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = pre + "_" + timeStamp;
        String thumbnailFileName = pre + "_tn_" + timeStamp;

//        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File [] images = new File[2];
        images[0] = File.createTempFile(   imageFileName,   /* prefix */
                                            "." + ext,      /* suffix */
                                            storageDir      /* directory */
                                         );
        images[1] = File.createTempFile( thumbnailFileName, /* prefix */
                                            "." + ext,      /* suffix */
                                            storageDir      /* directory */
                                        );
        return images;
    }


    public static void saveImageToExternalStorage(@NonNull Context context, @NonNull File file, @NonNull Bitmap bitmap) {
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        // Tell the media scanner about the new file so that it is
        // immediately available to the user.
        MediaScannerConnection.scanFile(context, new String[] { file.toString() }, null,
                new MediaScannerConnection.OnScanCompletedListener() {
                    public void onScanCompleted(String path, Uri uri) {
                    }
                });
    }


    /**
     * Gets the aws credentials provider
     * @param context
     * @return
     */
    public static AWSCredentialsProvider getCredProvider(Context context) {
        final AWSMobileClient mobileClient = AWSMobileClient.defaultMobileClient();
        AWSCredentialsProvider credentialsProvider = mobileClient.getIdentityManager().getCredentialsProvider();

        return credentialsProvider;
    }

    /**
     * Shows a bitmap in a custom Toast.  Used the layout thumbnail_toast.
     * @param thumbnailBitmap
     */
    public static void showImageInToast(Context context, Activity activity, Bitmap thumbnailBitmap) {
        // Show the thumbnail in the app...
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = inflater.inflate(R.layout.thumbnail_toast,
                (ViewGroup) activity.findViewById(R.id.custom_thumbnail_toast_layout_id));
        // Set the image and message
        ImageView image = (ImageView) layout.findViewById(R.id.toast_thumbnail_imageview);
        image.setImageBitmap(thumbnailBitmap);
        TextView text = (TextView) layout.findViewById(R.id.toast_thumbnail_text);
        text.setText("");
        //Show the toast
        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(Toast.LENGTH_LONG);
        toast.setView(layout);
        toast.show();
    }


    /**
     * Convert a bitmap to a base64 encoded JPEG.
     * @param bitmap
     * @return
     */
    public static String BitMapToBase64JPG(Bitmap bitmap){
        ByteArrayOutputStream ByteStream = new  ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ByteStream);
        byte [] b = ByteStream.toByteArray();
        String temp = Base64.encodeToString(b, Base64.DEFAULT);
        return temp;
    }

    /**
     * Convert a base64 encoded JPEG to a bitmap.
     * @param encodedString
     * @return
     */
    public static Bitmap Base64JPGToBitMap(String encodedString){
        try{
            byte [] encodeByte = Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap = BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        }catch(Exception e){
            Log.e(TAG, "Base64JPGToBitMap: Error:", e );
            return null;
        }
    }




}
