package snip.androidapp;

import android.content.Context;
import android.net.SSLCertificateSocketFactory;
import android.os.Build;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.Volley;
import com.crashlytics.android.answers.Answers;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.net.SocketFactory;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by ranreichman on 8/7/16.
 */
public class VolleyInternetOperator {
    public interface responseFunctionInterface {
        void apply(Context context, JSONObject response, JSONObject params);
    }

    public interface errorFunctionInterface {
        void apply(Context context, VolleyError error, JSONObject params);
    }

    public static String parseNetworkErrorResponse(VolleyError volleyError) {
        if (volleyError.networkResponse != null && volleyError.networkResponse.data != null) {
            return new String(volleyError.networkResponse.data);
        }
        return null;
    }

    public static void accessWebsiteWithVolley(
            final Context context, String url, int requestMethod,
            final JSONObject params, final HashMap<String, String> additionalHeaders,
            final responseFunctionInterface responseFunction,
            final errorFunctionInterface errorFunction)
    {
        Log.d("accessing website", url);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
        {
            HttpsTrustManager.allowAllSSL();
        }
        RequestQueue queue = CustomVolleyRequestQueue.getInstance().getRequestQueue();

        final CustomJSONObjectRequest jsonRequest =
                new CustomJSONObjectRequest(requestMethod, url, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("got", "response");
                                responseFunction.apply(context, response, params);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                try
                                {
                                    try
                                    {
                                        Log.d("error", parseNetworkErrorResponse(error));
                                    }
                                    catch (NullPointerException e)
                                    {
                                        e.printStackTrace();
                                    }
                                    errorFunction.apply(context, error, params);
                                }
                                catch (Exception e)
                                {
                                    e.printStackTrace();
                                }
                            }
                        })
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        if (null != additionalHeaders)
                        {
                            Iterator it = additionalHeaders.entrySet().iterator();
                            while (it.hasNext()) {
                                HashMap.Entry<String, String> pair = (Map.Entry<String, String>) it.next();
                                headers.put(pair.getKey(), pair.getValue());
                            }
                        }
                        return headers;
                    }
                };
        ;
        final int RETRY_ATTEMPTS_IN_VOLLEY = 3;
        jsonRequest.setRetryPolicy(new DefaultRetryPolicy(0, RETRY_ATTEMPTS_IN_VOLLEY, 0));

        final String REQUEST_TAG = "MainVolleyActivity";
        jsonRequest.setTag(REQUEST_TAG);

        queue.add(jsonRequest);
    }
}
