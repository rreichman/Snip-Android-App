package snip.androidapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by ranreichman on 8/7/16.
 */
public class VolleyInternetOperator
{
    public interface responseFunctionInterface
    {
        void apply(JSONObject response, JSONObject params);
    }

    public interface errorFunctionInterface
    {
        void apply(VolleyError error, JSONObject params);
    }

    public static void accessWebsiteWithVolley(
            Context context, String url, int requestMethod,
            final JSONObject params, final HashMap<String, String> additionalHeaders,
            final responseFunctionInterface responseFunction, final errorFunctionInterface errorFunction)
    {
        RequestQueue queue = CustomVolleyRequestQueue.getInstance(context).getRequestQueue();

        final CustomJSONObjectRequest jsonRequest =
                new CustomJSONObjectRequest(requestMethod, url, params,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                Log.d("got", "response");
                                responseFunction.apply(response, params);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.d("error", error.getMessage());
                                errorFunction.apply(error, params);
                            }
                        })
                {
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        HashMap<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        Iterator it = additionalHeaders.entrySet().iterator();
                        while (it.hasNext())
                        {
                            HashMap.Entry<String,String> pair = (Map.Entry<String,String>)it.next();
                            headers.put(pair.getKey(), pair.getValue());
                        }
                        return headers;
                    }
                };
        ;

        final String REQUEST_TAG = "MainVolleyActivity";
        jsonRequest.setTag(REQUEST_TAG);

        queue.add(jsonRequest);
    }
}
