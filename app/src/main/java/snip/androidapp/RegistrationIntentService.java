package snip.androidapp;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;


/**
 * Created by ranihorev on 24/08/2016.
 */
public class RegistrationIntentService extends IntentService {

    // abbreviated tag name
    private static final String TAG = "RegIntentService";
    public static final String FCM_TOKEN = "FCMToken";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

        String token = sharedPreferences.getString(FCM_TOKEN, null);
        if (null == token) {
            token = FirebaseInstanceId.getInstance().getToken();
            sharedPreferences.edit().putString(FCM_TOKEN, token).commit();
        }
        Log.d("Token is", token);
        NotificationUtils.sendRegistrationToServer(this, token, false);
    }
}