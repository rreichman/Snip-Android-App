package snip.androidapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Created by ranihorev on 07/08/2016.
 */
public class ServerRequests {

    String mBaseURL;
    String mReqContType = "application/json; charset=utf-8";

    public ServerRequests(String baseURL) {
        mBaseURL = baseURL;
    }

    public interface responseFuncInterface {
        void apply(JSONObject response, JSONObject params);
    }

    public void sendRequest(Context context, int methodType, String reqURL, final JSONObject params,
                            final responseFuncInterface successFunc, final responseFuncInterface errorFunc) {

        RequestQueue queue = Volley.newRequestQueue(context);
//        InternetRequestSingleton.getInstance().getRequestQueue();

        JsonObjectRequest LoginReq = new JsonObjectRequest(methodType, mBaseURL + reqURL, params,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        successFunc.apply(response, params);
                    }
                }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.d("Server Request Failed", error.toString());
                if (null != errorFunc) {
                    errorFunc.apply(null, params);
                }
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", mReqContType);
                return headers;
            }
        };
        queue.add(LoginReq);
        queue.stop();
    }
}
