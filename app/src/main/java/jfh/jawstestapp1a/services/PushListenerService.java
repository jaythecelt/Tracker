package jfh.jawstestapp1a.services;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import com.google.android.gms.gcm.GcmListenerService;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import jfh.jawstestapp1a.Messages.BaseMessage;
import jfh.jawstestapp1a.Messages.ErrorMessage;
import jfh.jawstestapp1a.Messages.SpotReportSNSMessage;
import jfh.jawstestapp1a.Messages.TrackSNSMessage;
import jfh.jawstestapp1a.common.Constants;

import static jfh.jawstestapp1a.common.Constants.INTENT_SR_JSON;
import static jfh.jawstestapp1a.common.Constants.INTENT_SR_RCVD_ACTION;
import static jfh.jawstestapp1a.common.Constants.INTENT_TRACK_RCVD_ACTION;
import static jfh.jawstestapp1a.common.Constants.INTENT_TRACK_JSON;

/** Listens to GCM notifications. */
public class PushListenerService extends GcmListenerService {
    private static final String TAG = PushListenerService.class.getSimpleName();

    public static final String TRACK_UPDATE_ACTION = "track_update_action";



    /**
     * Helper method to extract SNS message from bundle.
     *
     * @param data bundle
     * @return message string from SNS push notification
     */
    public static String getMessage(final Bundle data) {
        // If a push notification is sent as plain text, then the message appears in "default".
        // Otherwise it's in the "message" for JSON format.
        return data.containsKey("default") ? data.getString("default") : data.getString(
                "message", "");
    }

    /**
     * Called when an SNS message is received.
     *
     * @param from SenderID of the sender.
     * @param data Bundle containing message data
     */
    @Override
    public void onMessageReceived(final String from, final Bundle data) {
        Log.v(TAG, "Received Push message: " + data.toString());
        String snsMessage = getMessage(data);

        //Determine what message is sent.
        JsonParser parser = new JsonParser();
        JsonObject msgJO = parser.parse(snsMessage).getAsJsonObject();
        if (!msgJO.has(BaseMessage.MSG_TYPE_KEY)) {
            Log.e(TAG, "onMessageReceived: Invalid message from SNS: " + snsMessage);
            return;
        }
        if (msgJO.has(ErrorMessage.ERROR_KEY)) {
            Log.e(TAG, "onMessageReceived: Error message from SNS: " + snsMessage);
            return;
        }

        String msgType = msgJO.get(BaseMessage.MSG_TYPE_KEY).getAsString();
        switch (msgType) {
            case Constants.SNS_TRACK_NOTIFY:
                //This message type is a TrackSNSMessage; therefore it contains 'track' json
                //Extract the 'track' json from the message and pass it in the intent.
                JsonObject trackJO = msgJO.getAsJsonObject(TrackSNSMessage.TRACK_KEY);
                Log.d(TAG, "onMessageReceived: Track object: " + trackJO.toString());
                broadcastTrack(trackJO.toString()); //Pass the JSON string representing a Track object
                break;

            case Constants.SNS_SPOT_REPORT_NOTIFY:
                JsonObject spotJO = msgJO.getAsJsonObject(SpotReportSNSMessage.SPOT_REPORT_KEY);
                Log.d(TAG, "onMessageReceived: SpotReport object: " + spotJO.toString());
                broadcastSpotReport(spotJO.toString());
                break;

            default:
                Log.e(TAG, "onMessageReceived: Unexpected message type: " + msgType);
                return;
        }


    }


    private void broadcastTrack(String trackJson) {
        Intent i = new Intent(INTENT_TRACK_RCVD_ACTION);
        i.putExtra(INTENT_TRACK_JSON, trackJson);
        sendBroadcast(i);
    }

    private void broadcastSpotReport(String spotReportJson) {
        Intent i = new Intent(INTENT_SR_RCVD_ACTION);
        i.putExtra(INTENT_SR_JSON, spotReportJson);
        sendBroadcast(i);

    }

}
