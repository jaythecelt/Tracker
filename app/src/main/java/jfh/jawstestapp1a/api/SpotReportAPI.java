package jfh.jawstestapp1a.api;


import android.util.Log;

import com.amazonaws.AmazonClientException;
import com.amazonaws.http.HttpMethodName;
import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.api.CloudLogicAPI;

import com.amazonaws.mobile.api.idh9ydfojrbi.IRailClient;
import com.amazonaws.mobileconnectors.apigateway.ApiRequest;
import com.amazonaws.mobileconnectors.apigateway.ApiResponse;
import com.amazonaws.util.StringUtils;
import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

import jfh.jawstestapp1a.Messages.SpotReportSNSMessage;
import jfh.jawstestapp1a.common.Constants;
import jfh.jawstestapp1a.dao.SpotReport;

public class SpotReportAPI {
    private static final String TAG = SpotReportAPI.class.getSimpleName();

    public void postSpotReport(SpotReport spotReport) {
        final String method = Constants.POST;
        final String path = Constants.SR_API_PATH;

        SpotReportPostMsg msg = new SpotReportPostMsg();
        msg.setMsgType(Constants.POST_SR_MSG_TYPE);
        msg.setMsgVersion(Constants.POST_SR_MSG_VERSION);
        msg.setSpotReport(spotReport);
        Gson gson = new Gson();
        String body = gson.toJson(msg);
        Log.i(TAG, body);

        final Map<String, String> queryStringParameters = new HashMap<String, String>();
        final Map<String, String> headers = new HashMap<String, String>();

        final byte[] content = body.getBytes(StringUtils.UTF8);

        // Create an instance of the SDK client
        final AWSMobileClient mobileClient = AWSMobileClient.defaultMobileClient();
        final CloudLogicAPI client = mobileClient.createAPIClient(IRailClient.class);

        final ApiRequest request =
                new ApiRequest(client.getClass().getSimpleName())
                        .withPath(path)
                        .withHttpMethod(HttpMethodName.valueOf(method))
                        .withHeaders(headers)
                        .addHeader("Content-Type", "application/json")
                        .addHeader("Content-Length", String.valueOf(content.length))
                        .withParameters(queryStringParameters)
                        .withBody(content);

        // Make network call on background thread
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    // Invoke the API
                    final ApiResponse response = client.execute(request);
                    final int statusCode = response.getStatusCode();
                    final String statusText = response.getStatusText();
                    Log.d(TAG, "Response Status: " + statusCode + " " + statusText);

                    // TODO: Add handling for server response status codes (e.g., 404)

                } catch (final AmazonClientException exception) {
                    Log.e(TAG, exception.getMessage(), exception);

                    // TODO: Exception handling code here
                }
            }
        }).start();
    }

    public class SpotReportPostMsg {
        private String msgType;
        private String msgVersion;
        private SpotReport spotReport;

        public String getMsgType() {
            return msgType;
        }

        public void setMsgType(String msgType) {
            this.msgType = msgType;
        }

        public String getMsgVersion() {
            return msgVersion;
        }

        public void setMsgVersion(String msgVersion) {
            this.msgVersion = msgVersion;
        }

        public SpotReport getSpotReport() {
            return spotReport;
        }

        public void setSpotReport(SpotReport spotReport) {
            this.spotReport = spotReport;
        }

    }

}
