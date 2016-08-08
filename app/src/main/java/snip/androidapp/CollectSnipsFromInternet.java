package snip.androidapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by ranreichman on 8/7/16.
 */
public class CollectSnipsFromInternet
{
    private int mSnipsToCollect;
    private int mSnipsCollectedInCurrentSession;

    LinkedList<SnipData> mSnipsFromBackend = new LinkedList<SnipData>();

    public CollectSnipsFromInternet(Context context)
    {
        mSnipsToCollect = context.getResources().getInteger(R.integer.numSnipsPerLoading);
        mSnipsCollectedInCurrentSession = 0;
    }

    private String getSnipsQuery(Context context)
    {
        final String baseQuery = ""; // "?im_width=600&im_height=600";
        String lastRequestURL = SnipCollectionInformation.getInstance().getLastSnipQuery();
        String baseAccessUrl = context.getResources().getString(R.string.baseAccessURL);
        String snipsBaseUrl = context.getResources().getString(R.string.snipsBaseURL);
        String fullRequestURL = baseAccessUrl + snipsBaseUrl;
        if (lastRequestURL.isEmpty()) {
            fullRequestURL += baseQuery;
        }
        else {
            fullRequestURL += lastRequestURL;
        }
        return fullRequestURL;
    }

    public void retrieveSnipsFromInternet(final Context context)
    {
        JSONObject loginJsonParams = new JSONObject();

        HashMap<String,String> headers =
                SnipCollectionInformation.getInstance().getTokenForWebsiteAccessAsHashMap(context);

        VolleyInternetOperator.responseFunctionInterface responseFunction =
                new VolleyInternetOperator.responseFunctionInterface() {
                    @Override
                    public void apply(Context context, JSONObject response, JSONObject params)
                    {
                        responseFunctionImplementation(context, response, params);
                    }
                };
        VolleyInternetOperator.errorFunctionInterface errorFunction =
                new VolleyInternetOperator.errorFunctionInterface() {
                    @Override
                    public void apply(VolleyError error, JSONObject params)
                    {
                        errorFunctionImplementation(error, params);
                    }
                };

        VolleyInternetOperator.accessWebsiteWithVolley(
                context, getSnipsQuery(context), Request.Method.GET, loginJsonParams, headers,
                responseFunction, errorFunction);
    }

    public void responseFunctionImplementation(Context context, JSONObject response, JSONObject params)
    {
        try {
            JSONArray jsonArray = response.getJSONArray("results");
            mSnipsFromBackend.addAll(SnipConversionUtils.convertJsonArrayToSnipList(jsonArray));
            int mLastSnipsCollection = jsonArray.length();
            mSnipsCollectedInCurrentSession += mLastSnipsCollection;
            String fullNextRequest = response.getString("next");
            if (fullNextRequest.equals("null")) {
                SnipCollectionInformation.getInstance().setLastSnipQuery(fullNextRequest);
                return;
            }
            String[] splittedFullNextRequest = fullNextRequest.split("/");
            String nextQueryString = "/" + splittedFullNextRequest[splittedFullNextRequest.length - 1];
            SnipCollectionInformation.getInstance().setLastSnipQuery(nextQueryString);

            if ((mLastSnipsCollection != 0) && (mSnipsCollectedInCurrentSession < mSnipsToCollect))
            {
                retrieveSnipsFromInternet(context);
            }
            else
            {
                SnipCollectionInformation.getInstance().setCollectedSnips(mSnipsFromBackend);
                ((MyActivity)context).populateActivity();
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    public void errorFunctionImplementation(VolleyError error, JSONObject params)
    {
        // TODO:: implement
        Log.d("error", "function");
    }
}
