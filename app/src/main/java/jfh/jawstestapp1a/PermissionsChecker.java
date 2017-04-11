package jfh.jawstestapp1a;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.Arrays;


public class PermissionsChecker {
    private static final String TAG = PermissionsChecker.class.getSimpleName();

    /**
     * Checks for the permissions given in the parameters.  If permissions have been previously
     * granted, returns true.  If not all permissions are granted, the permissions are requested,
     * results are returned in the callback onRequestPermissionsResult (implemented by the
     * calling activity).
     *
     * @param activity
     * @param permissions
     * @param requestCode
     * @return
     */

    public static boolean check(@NonNull final Activity activity,
                                         final String [] permissions,
                                         final int requestCode)
    {
        boolean allGranted = true;
        for(String p : Arrays.asList(permissions)) {

            boolean granted = (ContextCompat.checkSelfPermission(activity, p) == PackageManager.PERMISSION_GRANTED);
            allGranted = allGranted && granted;
            Log.d(TAG, "Permission " + p + ((granted) ? " granted" : " denied"));
        }
        if (!allGranted) {
            Log.i(TAG, "checkPermissions: Not all permissions granted ... requesting...");
            ActivityCompat.requestPermissions(activity, permissions, requestCode); //Triggers callback on activity
        }
        return allGranted;
    }
}

