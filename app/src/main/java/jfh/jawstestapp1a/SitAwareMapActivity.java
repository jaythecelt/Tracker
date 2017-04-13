package jfh.jawstestapp1a;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jfh.jawstestapp1a.api.TrackerAPI;
import jfh.jawstestapp1a.common.Constants;
import jfh.jawstestapp1a.dao.SpotReport;
import jfh.jawstestapp1a.dao.Track;
import jfh.jawstestapp1a.spotreport.SpotReportHandler;
import jfh.jawstestapp1a.utilities.Util;

import static jfh.jawstestapp1a.common.Constants.INTENT_GPS_UPDATE_ACTION;
import static jfh.jawstestapp1a.common.Constants.INTENT_SR_JSON;
import static jfh.jawstestapp1a.common.Constants.INTENT_SR_RCVD_ACTION;
import static jfh.jawstestapp1a.common.Constants.INTENT_TRACK_JSON;
import static jfh.jawstestapp1a.common.Constants.INTENT_TRACK_RCVD_ACTION;

public class SitAwareMapActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = SitAwareMapActivity.class.getSimpleName();

    private String userName;

    private GoogleMap map;
    private Marker myMarker = null;
    private Polyline myLine = null;
    private LatLng oldLoc = null;
    private LatLng initialLocation = null;

    private Track myCurrentTrack = null;
    private Uri myLatestImageURI = null;

    protected SpotReportHandler spotReportHandler;

    //List of icons available to be assigned
    protected List<Integer> availableIcons =
            new ArrayList<>(Arrays.asList(R.mipmap.ic_aqua_dot, R.mipmap.ic_yellow_dot, R.mipmap.ic_drkgreen_dot, R.mipmap.ic_red_dot));
    //Associates users and icons
    protected Map<String, Integer> iconMap = new HashMap<>();

    //Associates users and their current marker
    protected Map<String, Marker> markerMap = new HashMap<>();

    //Options for markers
    protected boolean showNames = false;



    private MapBroadcastReceiver mapBroadcastReceiver = null;

    @Override
    protected void onResume() {
        super.onResume();
        if (mapBroadcastReceiver == null) {
            mapBroadcastReceiver = new MapBroadcastReceiver();
            IntentFilter filter = new IntentFilter(INTENT_GPS_UPDATE_ACTION);
            filter.addAction(Constants.INTENT_TRACK_RCVD_ACTION);
            registerReceiver(mapBroadcastReceiver, filter);
        }
    }

    @Override
    protected void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sit_aware_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        userName = getIntent().getStringExtra(Constants.INTENT_USER_NAME);

        spotReportHandler = new SpotReportHandler(getApplicationContext(), this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.map_menu, menu);
        // return true so that the menu pop up is opened
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.camera_menu_item:
                dispatchTakePictureIntent();
                return true;
            case R.id.show_names_menu_item:
                showNames = !showNames;
                for (Map.Entry<String, Marker> entry : markerMap.entrySet()) {
                    if (showNames) entry.getValue().showInfoWindow();
                    else entry.getValue().hideInfoWindow();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onMapReady (GoogleMap googleMap){
        map = googleMap;

        map.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        //noinspection MissingPermission
        map.setMyLocationEnabled(true);


//        LatLng home = new LatLng(40.75055587, -80.39055334);
//        map.moveCamera(CameraUpdateFactory.newLatLngZoom(home, 15));
//
//        LatLng poi = new LatLng(40.750886, -80.3901275);
//        LatLng camera = new LatLng(40.750695, -80.390965);
//
//        Marker poiMarker = map.addMarker(new MarkerOptions().position(poi).snippet("Target").anchor(0.5f, 0.5f)
//                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_target)));
//
////        Marker camMarker = map.addMarker(new MarkerOptions().position(camera).snippet("Camera"));
//
//        PolylineOptions lineOptions = new PolylineOptions().add(camera).add(poi).width(10).color(Color.LTGRAY).geodesic(true);
//        Polyline line = map.addPolyline(lineOptions);
//        line.setStartCap(new RoundCap());
//        line.setEndCap(new RoundCap());

    }


    @Override
    protected void onDestroy () {
        super.onDestroy();
        if (mapBroadcastReceiver != null) {
            Log.i(TAG, "onDestroy: unregister broadcast receiver.");
            unregisterReceiver(mapBroadcastReceiver);
        }
        if (spotReportHandler!=null) {
            spotReportHandler.cleanup();
        }
    }


    /**
     * Called by the MapBroadcastReceiver whenever a new TRACK is received.
     * This method takes the track data (in the extras parameter) and updates the map.
     * @param extras
     */
    protected void handleTrackReceived (Bundle extras) {
        String trackJson = extras.getString(INTENT_TRACK_JSON);
        Gson gson = new Gson();
        Track track = gson.fromJson(trackJson, Track.class);
        Log.d(TAG, "handleTrackReceived: track: " + track.getUserId() + " " + track.getTrackLat() + " " + track.getTrackLong());

        String trackUserId = track.getUserId();
        if (userName.equals(trackUserId)) {
            return; //No updates for current user since that's handles by the blue dot/arrow built in to the map.
        }

        //Now have a new track ... display it on the map.//
        LatLng loc = new LatLng(track.getTrackLat(), track.getTrackLong());

        if (!markerMap.containsKey(trackUserId)) { //Is this the first time we've heard from this user?
            Log.i(TAG, "handleTrackReceived: New User: " + trackUserId);
            //Grab the next available icon
            int iconId;
            if (availableIcons.size() > 0) {
                iconId = availableIcons.get(0);
                availableIcons.remove(0); //Remove the icon since it's taken.
            } else {
                iconId = R.mipmap.ic_blue_dot; //default if all taken
            }

            Drawable d = ContextCompat.getDrawable(this, iconId);
            BitmapDescriptor icon = Util.getMarkerIconFromDrawable(d);
            MarkerOptions markerOptions = new MarkerOptions().position(loc)
                    .title(trackUserId)
                    .anchor(0.5f, 0.5f)
                    .icon(icon);

            Marker marker = map.addMarker(markerOptions);
            if (showNames) marker.showInfoWindow();
            markerMap.put(trackUserId, marker);

        } else { //We've received updates from this user before
            Log.i(TAG, "handleTrackReceived: Existing User: " + trackUserId);
            Log.i(TAG, "handleTrackReceived: Updating the location.");
            markerMap.get(trackUserId).setPosition(loc);
        }
    }


    /**
     * Called by the MapBroadcastReceiver whenever the GPSService detects a new GPS location.
     * This method takes the GPS data (in the extras parameter) and POSTs the new track to the API.
     *
     * @param extras
     */
    protected void handleGPSUpdate (Bundle extras) {
        String loc_time = Util.getTimestamp(extras.getLong(Constants.INTENT_GPS_TIME, Constants.INVALID_LONG_VALUE));
        double latitude = extras.getDouble(Constants.INTENT_GPS_LAT, Constants.INVALID_DOUBLE_VALUE);
        double longitude = extras.getDouble(Constants.INTENT_GPS_LONG, Constants.INVALID_DOUBLE_VALUE);
        float accuracy = extras.getFloat(Constants.INTENT_GPS_ACCURACY, Constants.INVALID_FLOAT_VALUE);

        Log.i(TAG, "handleGPSUpdate: " +  latitude +
                " " + longitude +
                " " + loc_time  +
                " " + accuracy);

        Track track = new Track();
        track.setUserId(userName);
        track.setTrackStatus(0);
        track.setTrackTimestamp(loc_time);
        track.setTrackLat(latitude);
        track.setTrackLong(longitude);
        TrackerAPI api = new TrackerAPI();
        api.postTrack(track);

        //Update the current track//
        myCurrentTrack = track;

        //Move camera on the first GPS update.
        if (initialLocation==null) {
           initialLocation = new LatLng(latitude, longitude);
           map.moveCamera(CameraUpdateFactory.newLatLngZoom(initialLocation, 15));
        }

    }


    protected void handleSpotReportReceived (Bundle extras) {
        String srJson = extras.getString(INTENT_SR_JSON);
        Gson gson = new Gson();
        SpotReport sr = gson.fromJson(srJson, SpotReport.class);
        Log.d(TAG, "handleSpotReportReceived: spotReport: " + sr.getUserId() + " " + sr.getSrLat() + " " + sr.getSrLong());

        String srUserId = sr.getUserId();
        if (userName.equals(srUserId)) {
            return; //No updates for current user
        }
        addSpotReportToMap(sr);
        Toast.makeText(this, "Spot report received.", Toast.LENGTH_SHORT).show();
    }




    private static final int REQUEST_IMAGE_CAPTURE = 1;

    /**
     * Starts the activity to take a picture.  Call back is onActivityResult with
     * requestCode = REQUEST_IMAGE_CAPTURE
     *
     * Note:  We are using getUriForFile(Context, String, File) which returns a
     *        content:// URI. For more recent apps targeting Android 7.0 (API level 24) and higher,
     *        passing a file:// URI across a package boundary causes a FileUriExposedException.
     *        Therefore, we are using a more generic way of storing images using a FileProvider.
     *        Fileprovider has to be configured in the manifest.  Also the path name must be in a
     *        resource file res/xml/file_paths.xml.
     *
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) == null) { //Ensure camera activity exists
            Log.e(TAG, "dispatchTakePictureIntent: camera activity does not exist.");
            return;
        }

        //Create the files for the image and for the thumbnail.
        File[] imageFiles = null;

        try {
            //imageFiles[0] is the full size image, imageFiles[1] is the thumbnail.
            imageFiles = Util.createImageFiles(this, "spot", "bmp");
        } catch (IOException e) {
            Toast.makeText(this, "Cannot create image file.", Toast.LENGTH_LONG).show();
            Log.e(TAG, "dispatchTakePictureIntent: Cannot create image file", e );
        }
        if (imageFiles[0]==null) return;

        //The spotReportHandler takes care of creating and saving the spot report, including
        // creating a thubmnail from the full size image and uploading them to the cloud.
        spotReportHandler.setImage(imageFiles[0], imageFiles[1]);

        Log.d(TAG, "dispatchTakePictureIntent: Image path = " + imageFiles[0].getAbsolutePath());
        //URI for full size image.
        Uri imageURI = FileProvider.getUriForFile(this,
                                                  "jfh.jawstestapp1a.fileprovider",
                                                  imageFiles[0]);

        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageURI);
        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
    }

    /**
     * Callback from activity completion.
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");
        if (resultCode!=RESULT_OK) {
            Log.e(TAG, "onActivityResult() error: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "]");
            return;
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE ) {
            //Note: data is null from an image capture.
            //Got the image, now assemble the spot report
            spotReportHandler.createAndPost(userName, myCurrentTrack);
            //Add the spot report to the map
            addSpotReportToMap(spotReportHandler.getSpotReport());


        }
    }

    private void addSpotReportToMap(SpotReport spotReport) {
        if (spotReport==null) return;
        LatLng loc = new LatLng(spotReport.getSrLat(), spotReport.getSrLong());

        Marker srMarker = map.addMarker(new MarkerOptions().position(loc).snippet(spotReport.getTitle()).anchor(0.5f, 0.5f)
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_target)));

    }


    public class MapBroadcastReceiver extends BroadcastReceiver {
        private final String TAG = MapBroadcastReceiver.class.getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            switch (intent.getAction()) {
                case INTENT_GPS_UPDATE_ACTION:
                    handleGPSUpdate(intent.getExtras());
                    break;
                case INTENT_TRACK_RCVD_ACTION:
                    handleTrackReceived(intent.getExtras());
                    break;
                case INTENT_SR_RCVD_ACTION:
                    handleSpotReportReceived(intent.getExtras());
                    break;
                default:
                    Log.e(TAG, "onReceive: Unexpected action: " + intent.getAction() );
            }




//            LatLng loc = new LatLng(latitude, longitude);
//            if (myMarker!=null) myMarker.remove(); //Remove the previously set marker
//            if (myLine!=null) myLine.remove();
//
//            Drawable d = ContextCompat.getDrawable(context, R.mipmap.ic_blue_dot);
////                    d.setColorFilter(new
////                            PorterDuffColorFilter(0xffff00, PorterDuff.Mode.MULTIPLY));
//            BitmapDescriptor icon = Util.getMarkerIconFromDrawable(d);
//
//
//
//
//
//                    MarkerOptions markerOptions = new MarkerOptions().position(loc)
//                                                        .snippet(" " + loc.latitude + " " + loc.longitude + " " + accuracy)
//                                                        .anchor(0.5f, 0.5f)
//                                                        .icon(icon)
//                                                    ;
//
//                    myMarker = map.addMarker(markerOptions);
//                    Log.i(TAG, "=== Add new marker at: " + loc.latitude + " " + loc.longitude);
//                    map.moveCamera(CameraUpdateFactory.newLatLng(loc));
//
//
//                    if (oldLoc!=null) {
//                        PolylineOptions lineOptions = new PolylineOptions().add(loc).add(oldLoc).width(10).color(Color.LTGRAY).geodesic(true);
//                        myLine = map.addPolyline(lineOptions);
//                        myLine.setStartCap(new RoundCap());
//                        myLine.setEndCap(new RoundCap());
//
//                    }
//
//                    oldLoc = loc;
//


        }
    }

}

