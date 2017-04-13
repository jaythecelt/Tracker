package jfh.jawstestapp1a.spotreport;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;

import jfh.jawstestapp1a.common.Constants;
import jfh.jawstestapp1a.utilities.Util;


public class TransferUtilityHelper {
    private static final String TAG = TransferUtilityHelper.class.getSimpleName();

    public static final int COMPLETE = 0;
    public static final int FAILED = -1;

    private Context context;
    private String intentAction = null;


    /**
     * If the intentAction is not null, the TransferUtilityHelper will broadcast whenever the
     * transfer state changes (using the intentAction parameter.  Otherwise no broadcast will occur.
     *
     * @param context
     * @param intentAction
     */

    public TransferUtilityHelper (Context context, String intentAction) {
        this.context = context;
        this.intentAction = intentAction;
    }

    /**
     * Gets an instance of a S3 client which is constructed using the given
     * Context.
     *
     * @param context An Context instance.
     * @return A default S3 client.
     */
    private AmazonS3Client getS3Client(Context context) {
        return new AmazonS3Client(Util.getCredProvider(context.getApplicationContext()));

    }

    private void makeTransferToast(TransferState state, String key) {
        String message;
        switch (state) {
            case WAITING:
            case WAITING_FOR_NETWORK:
            case PAUSED:
                message = "Image transfer to cloud queued. " + key;
                break;
            case IN_PROGRESS:
                message = "Image transfer in progress. " + key;
                break;
            case CANCELED:
                message = "Image transfer canceled. " + key;
                break;
            case COMPLETED:
                message = "Image transfer to cloud complete. " + key;
                break;
            case FAILED:
                message = "Image transfer to cloud failed. " + key;
                break;
            default:
                message = "Image transfer in unknown state. " + key;
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void makeTransferToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }



    public int uploadFile(String bucketName, final String key, File file) {

        TransferUtility transferUtility =
                new TransferUtility(getS3Client(context.getApplicationContext()),
                                    context.getApplicationContext());

        TransferObserver observer =
                transferUtility.upload( bucketName,      /* The bucket to upload to */
                                        key,             /* The key for the uploaded object */
                                        file );          /* The file where the data to upload exists */


        observer.setTransferListener(new TransferListener(){
            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d(TAG, "onStateChanged() called with: id = [" + id + "], state = [" + state + "]");

                makeTransferToast(state, key); //Make toast on update

                if (intentAction==null) return; //No notification needed if null

                if ((state.compareTo(TransferState.COMPLETED)==0) ||
                            state.compareTo(TransferState.FAILED)==0 ) {
                    Log.d(TAG, "onStateChanged: Sending broadcast...");
                    int status = (state.compareTo(TransferState.COMPLETED)==0) ? COMPLETE : FAILED;
                    Intent i = new Intent(intentAction);
                    i.putExtra(Constants.INTENT_S3_ID, id);
                    i.putExtra(Constants.INTENT_XFER_HELPER_STATUS, status);
                    context.sendBroadcast(i);
                }
            }
            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
            }
            @Override
            public void onError(int id, Exception ex) {
                Log.e(TAG, "onError: id:" + id, ex );
                makeTransferToast("Image transfer error: " + ex.getMessage());
            }
        });
        return observer.getId();
    }

}
