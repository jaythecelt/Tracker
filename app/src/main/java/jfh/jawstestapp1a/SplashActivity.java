package jfh.jawstestapp1a;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.amazonaws.mobile.AWSMobileClient;
import com.amazonaws.mobile.push.PushManager;
import com.amazonaws.mobilehelper.auth.DefaultSignInResultHandler;
import com.amazonaws.mobilehelper.auth.IdentityManager;
import com.amazonaws.mobilehelper.auth.IdentityProvider;
import com.amazonaws.mobilehelper.auth.StartupAuthErrorDetails;
import com.amazonaws.mobilehelper.auth.StartupAuthResult;
import com.amazonaws.mobilehelper.auth.StartupAuthResultHandler;
import com.amazonaws.mobilehelper.auth.signin.CognitoUserPoolsSignInProvider;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;


public class SplashActivity extends Activity {
    private final static String TAG = SplashActivity.class.getSimpleName();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    private RegisterDeviceTask registerDeviceTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        AWSMobileClient.initializeMobileClientIfNecessary(getApplicationContext());
        final IdentityManager identityManager =
                AWSMobileClient.defaultMobileClient().getIdentityManager();

        identityManager.addIdentityProvider(CognitoUserPoolsSignInProvider.class);
        registerDeviceTask = new RegisterDeviceTask(this);

        final StartupAuthResultHandler authResultHandler = new StartupAuthResultHandler() {
            @Override
            public void onComplete(final StartupAuthResult authResult) {
                Log.i(TAG, "StartupAuthResultHandler:onComplete: ");
                final IdentityManager identityManager = authResult.getIdentityManager();
                if (authResult.isUserSignedIn()) {
                    final IdentityProvider provider = identityManager.getCurrentIdentityProvider();
                    // If we were signed in previously with a provider indicate that to the user with a toast.
                    Toast.makeText(SplashActivity.this, String.format("Signed in with %s",
                            provider.getDisplayName()), Toast.LENGTH_LONG).show();
                } else {
                    // Either the user has never signed in with a provider before or refresh failed with a previously
                    // signed in provider.
                    // Optionally, you may want to check if refresh failed for the previously signed in provider.
                    final StartupAuthErrorDetails errors = authResult.getErrorDetails();
                    if (errors.didErrorOccurRefreshingProvider()) {
                        Log.w(TAG, String.format(
                                "Credentials for Previously signed-in provider could not be refreshed."),
                                errors.getProviderRefreshException());
                    }
                    doMandatorySignIn(identityManager);
                    return;
                }

                registerDeviceTask.execute(null, null);
                goMain(SplashActivity.this);
            }
        };
        identityManager.doStartupAuth(this, authResultHandler, 2000);

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

    }

    private void doMandatorySignIn(final IdentityManager identityManager) {
        Log.i(TAG, "doMandatorySignIn: ");
        Log.i(TAG, "Calling identityManager.signInOrSignUp ");
        identityManager.signInOrSignUp(SplashActivity.this, new DefaultSignInResultHandler() {

            @Override
            public void onSuccess(final Activity callingActivity, final IdentityProvider provider) {
                Log.i(TAG, "identityManager.signInOrSignUp onSuccess ");
                if (provider != null) {
                    Log.d(TAG, String.format("User sign-in with %s provider succeeded",
                            provider.getDisplayName()));

                    Toast.makeText(callingActivity, String.format(
                            callingActivity.getString(R.string.sign_in_succeeded_message_format),
                            provider.getDisplayName()), Toast.LENGTH_LONG).show();
                    goMain(SplashActivity.this);
                }
            }

            @Override
            public boolean onCancel(final Activity callingActivity) {
                Log.i(TAG, "identityManager.signInOrSignUp onCancel ");
                // User abandoned sign in flow.
                final boolean shouldFinishSignInActivity = false;
                return shouldFinishSignInActivity;
            }
        });

    }

    /**
     * Go to the main activity.
     */
    private void goMain(final Activity callingActivity) {
        Log.i(TAG, "Going to main activity!");
        callingActivity.startActivity(new Intent(callingActivity, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));
        callingActivity.finish();
    }



//-------------------------------------------------------------------------//
    /**
     * Registers the application with GCM servers asynchronously.
     * This cannot be done in the UI thread.
     */
    private class RegisterDeviceTask extends AsyncTask <Void, Void, Boolean> {
        private final String TAG = RegisterDeviceTask.class.getSimpleName();
        private Activity activity;

        public RegisterDeviceTask (Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            Log.i(TAG, "doInBackground");
            boolean success = false;
            //--SNS Push--//
            //Register device to receive SNS push notifications
            final PushManager pushManager = AWSMobileClient.defaultMobileClient().getPushManager();
            Log.i(TAG, "Got pushManager");
            pushManager.registerDevice();
            Log.i(TAG, "Registered Device.");
            if (pushManager.isRegistered()) { // if registration succeeded.
                pushManager.setPushEnabled(true);
                Log.i(TAG, "pushManager.getDefaultTopic(): " +  pushManager.getDefaultTopic());
                pushManager.subscribeToTopic(pushManager.getDefaultTopic());
                success = true;
                Log.i(TAG, "Device registered with SNS!" + pushManager.getEndpointArn());
            } else { //ErrorMessage
                Log.e(TAG, "Device not registered with SNS!" + pushManager.getEndpointArn());
            }

            Log.i(TAG, "doInBackground success: " + success);
            return success;
        }

        @Override
        protected void onPostExecute(Boolean success) {
            Log.i(TAG, "onPostExecute success: " + success);
            if (!success) {
                Toast.makeText(activity, "Cannot register device with SNS.", Toast.LENGTH_LONG).show();
            }
        }
    }

}