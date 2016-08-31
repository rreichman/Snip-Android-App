package snip.androidapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by ranihorev on 25/08/2016.
 */
public class NotificationUtils {

    public static final String TAG = "NotificationUtils";
    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";

    public static void sendRegistrationToServer(Context context, String token, Boolean forceUpdate) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        if (forceUpdate || (!sharedPreferences.getBoolean(SENT_TOKEN_TO_SERVER, false))) {
            sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, false).commit();

            HashMap<String,String> headers =
                    SnipCollectionInformation.getInstance(context).getTokenForWebsiteAccessAsHashMap();

            VolleyInternetOperator.responseFunctionInterface responseFunction =
                    new VolleyInternetOperator.responseFunctionInterface() {
                        @Override
                        public void apply(Context context, JSONObject response, JSONObject params)
                        {
                            updateTokenToServerStatus(context, true);
                        }
                    };
            VolleyInternetOperator.errorFunctionInterface errorFunction =
                    new VolleyInternetOperator.errorFunctionInterface() {
                        @Override
                        public void apply(Context context, VolleyError error, JSONObject params)
                        {
                            updateTokenToServerStatus(context, false);
                        }
                    };

            String baseAccessURL = context.getResources().getString(R.string.baseAccessURL);
            String notificationBaseUrl = context.getResources().getString(R.string.notificationTokenBaseURL);
            String url = baseAccessURL + notificationBaseUrl;

            JSONObject params = new JSONObject();
            try {
                params.put("notification_token", token);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            VolleyInternetOperator.accessWebsiteWithVolley(
                    context, url, Request.Method.POST, params, headers,
                    responseFunction, errorFunction);
        }


    }

    private static void updateTokenToServerStatus(Context context, boolean status) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().putBoolean(SENT_TOKEN_TO_SERVER, status).commit();
    }



    private static void showHideLoadingAnimation(Context context, boolean toShow) {
        // TODO fix this
//        AVLoadingIndicatorView avi = (AVLoadingIndicatorView)context.findViewById(R.id.avi_fragment);
//        if (null != avi) {
//            if (toShow) {
//                avi.setVisibility(avi.VISIBLE);
//                avi.show();
//            } else {
//                avi.hide();
//            }
//        }
    }

    // If the queryFromServer is null then we use the default query
    public static void retrieveSnipFromInternet(final Context context, String query, boolean showAnimation)
    {
        // TODO refactor this code
        showHideLoadingAnimation(context, showAnimation);
        JSONObject loginJsonParams = new JSONObject();

        HashMap<String,String> headers =
                SnipCollectionInformation.getInstance(context).getTokenForWebsiteAccessAsHashMap();

        VolleyInternetOperator.responseFunctionInterface responseFunction =
                new VolleyInternetOperator.responseFunctionInterface() {
                    @Override
                    public void apply(Context context, JSONObject response, JSONObject params)
                    {
                        showHideLoadingAnimation(context, false);
                        snipResponseFunctionImplementation(context, response, params);
                    }
                };
        VolleyInternetOperator.errorFunctionInterface errorFunction =
                new VolleyInternetOperator.errorFunctionInterface() {
                    @Override
                    public void apply(Context context, VolleyError error, JSONObject params)
                    {
                        showHideLoadingAnimation(context, false);
                        snipErrorFunctionImplementation(context, error, params);
                    }
                };


        Log.d(TAG, query);
        VolleyInternetOperator.accessWebsiteWithVolley(
                context, query, Request.Method.GET, loginJsonParams, headers,
                responseFunction, errorFunction);

    }

    public static void snipResponseFunctionImplementation(
            Context context, JSONObject response, JSONObject params)
    {

        Log.d(TAG, "Received snip from server");
        SnipData snipData = SnipConversionUtils.convertJsonObjectToSnipData(response);

        Bundle bundledSnipData = new Bundle();
        bundledSnipData.putSerializable("snipData", snipData);
        // TODO: load snip fragment
        FragmentOperations.openFragment((FragmentActivity) context,
                -1,
                context.getResources().getInteger(R.integer.fragmentCodeReadSnip),
                Long.toString(snipData.mID),
                bundledSnipData);

    }

    public static void snipErrorFunctionImplementation(Context context, VolleyError error, JSONObject params) {
        // TODO handle failure
        Log.d(TAG, "Failed");
    }

    public static void showSnip(Context context, String snip_id) {
        // TODO Check user logged in
        String baseAccessURL = context.getResources().getString(R.string.baseAccessURL);
        String snipBaseURL = context.getResources().getString(R.string.snipsBaseURL);
        String url = baseAccessURL + snipBaseURL + snip_id + SnipCollectionInformation.getInstance(context).getDimensionsQuery();

        retrieveSnipFromInternet(context, url, true);
    }
}


