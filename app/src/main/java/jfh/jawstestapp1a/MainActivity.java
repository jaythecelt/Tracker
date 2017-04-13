package jfh.jawstestapp1a;

import android.*;
import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobilehelper.auth.IdentityManager;

import jfh.jawstestapp1a.common.Constants;
import jfh.jawstestapp1a.services.GPSService;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends Activity {
    private final static String TAG = MainActivity.class.getSimpleName();
    private final static int PERMISSION_REQUEST_CODE = 100;

    private String userName;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        boolean permissionsGranted = PermissionsChecker.check(this,
                                 new String []
                                         {android.Manifest.permission.ACCESS_FINE_LOCATION,
                                          android.Manifest.permission.ACCESS_COARSE_LOCATION,
                                          android.Manifest.permission.CAMERA,
                                          android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                          android.Manifest.permission.ACCESS_NETWORK_STATE
                                         },
                                 PERMISSION_REQUEST_CODE);

        //If the location permissions have been previously granted, start the GPS service//
        if (permissionsGranted) {
            Log.i(TAG, "onCreate: Permissions previously granted, start the GPS Service.");
            Intent i = new Intent(getApplicationContext(), GPSService.class);
            startService(i);
        } else {
            //TODO Handle if permissions are not granted.
        }

        final IdentityManager identityManager =
                AWSMobileClient.defaultMobileClient().getIdentityManager();
        userName = identityManager.getIdentityProfile().getUserName();

        //Clicking on the map image starts the map activity//
        Button btn = (Button) findViewById(R.id.map_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SitAwareMapActivity.class);
                intent.putExtra(Constants.INTENT_USER_NAME, userName);
                MainActivity.this.startActivity(intent);
            }
        });


        //Clicking on the map image starts the map activity//
        Button signOutBtn = (Button) findViewById(R.id.signout_btn);
        signOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                identityManager.signOut();
                Toast.makeText(getApplicationContext(), userName + " signed out.", Toast.LENGTH_SHORT).show();
            }
        });



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //Stop the GPS Service//
        Log.i(TAG, "onDestroy: Stopping GPS Service.");
        Intent i = new Intent(getApplicationContext(), GPSService.class);
        stopService(i);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           final String[] permissions,
                                           int[] grantResults) {
        boolean allGranted = true;
        for (int ix = 0; ix < grantResults.length; ix++) {
            allGranted = allGranted && (grantResults[ix] == PackageManager.PERMISSION_GRANTED);
        }
        if (!allGranted) {
            Log.i(TAG, "onRequestPermissionsResult: Some permissions denied.");
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) {
                    showMessageOKCancel("You need to allow access to the GPS permissions",
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(permissions, PERMISSION_REQUEST_CODE);
                                    }
                                }
                            });
                }
            }
            //Permissions not granted yet, so return
            return;
        }

        Log.i(TAG, "onRequestPermissionsResult: Permissions granted.");

        //Permissions granted, so start the GPS Service!
        //Start the GPS Service//
        Log.i(TAG, "onRequestPermissionsResult: Starting GPSService.");
        Intent i = new Intent(getApplicationContext(), GPSService.class);
        startService(i);

    }


    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }

}
