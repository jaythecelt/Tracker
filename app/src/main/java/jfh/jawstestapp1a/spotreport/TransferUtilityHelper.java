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

    private Context context;


    public TransferUtilityHelper (Context context) {
        this.context = context;
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

    private void makeTransferToast(TransferState state) {
        String message;
        switch (state) {
            case WAITING:
            case WAITING_FOR_NETWORK:
            case PAUSED:
                message = "Image transfer to cloud queued.";
                break;
            case IN_PROGRESS:
                message = "Image transfer in progress.";
                break;
            case CANCELED:
                message = "Image transfer canceled.";
                break;
            case COMPLETED:
                message = "Image transfer to cloud complete.";
                break;
            case FAILED:
                message = "Image transfer to cloud failed.";
                break;
            default:
                message = "Image transfer in unknown state";
        }
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    private void makeTransferToast(String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }


    public int uploadFile(String bucketName, String key, File file) {

        TransferUtility transferUtility =
                new TransferUtility(getS3Client(context.getApplicationContext()),
                                    context.getApplicationContext());

        TransferObserver observer = transferUtility.upload(
                            bucketName,                 /* The bucket to upload to */
                            key,                        /* The key for the uploaded object */
                            file );                       /* The file where the data to upload exists */


        observer.setTransferListener(new TransferListener(){
            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d(TAG, "onStateChanged() called with: id = [" + id + "], state = [" + state + "]");
                makeTransferToast(state); //Make toast on update
                Intent i = new Intent(Constants.INTENT_S3_TRANSFER_COMPLETE_ACTION);
                i.putExtra(Constants.INTENT_S3_ID, id);
                i.putExtra(Constants.INTENT_S3_TRANSFER_STATE, state.ordinal());
                context.sendBroadcast(i);

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
