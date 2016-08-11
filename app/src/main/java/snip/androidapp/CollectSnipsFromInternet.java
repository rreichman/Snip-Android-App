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
    private int mAmountOfSnipsCollectedInCurrentSession;

    LinkedList<SnipData> mSnipsFromBackend = new LinkedList<SnipData>();

    public CollectSnipsFromInternet(Context context)
    {
        mSnipsToCollect = context.getResources().getInteger(R.integer.numSnipsPerLoading);
        mAmountOfSnipsCollectedInCurrentSession = 0;
    }

    private static String getSnipsQuery(Context context)
    {
        final String baseQuery = "?im_width=600&im_height=600";
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

    public static String getSnipsQueryLiked(Context context)
    {
        final String baseQuery = "?im_width=600&im_height=600";
        String baseAccessUrl = context.getResources().getString(R.string.baseAccessURL);
        String snipsBaseUrl = context.getResources().getString(R.string.snipsBaseURL);
        String likedBaseUrl = context.getResources().getString(R.string.likedBaseURL);
        return baseAccessUrl + snipsBaseUrl + likedBaseUrl + baseQuery;
    }

    public static String getSnipsQuerySnoozed(Context context)
    {
        final String baseQuery = "?im_width=600&im_height=600";
        String baseAccessUrl = context.getResources().getString(R.string.baseAccessURL);
        String snipsBaseUrl = context.getResources().getString(R.string.snipsBaseURL);
        String snoozedBaseUrl = context.getResources().getString(R.string.snoozedBaseURL);
        return baseAccessUrl + snipsBaseUrl + snoozedBaseUrl + baseQuery;
    }

    public void retrieveSnipsFromInternet(final Context context)
    {
        retrieveSnipsFromInternet(context, null);
    }

    // If the queryFromServer is null then we use the default query
    public void retrieveSnipsFromInternet(final Context context, String queryFromServer)
    {
        JSONObject loginJsonParams = new JSONObject();

        HashMap<String,String> headers =
                SnipCollectionInformation.getInstance().getTokenForWebsiteAccessAsHashMap();

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

        if (null == queryFromServer) {
            VolleyInternetOperator.accessWebsiteWithVolley(
                    context, getSnipsQuery(context), Request.Method.GET, loginJsonParams, headers,
                    responseFunction, errorFunction);
        }
        else
        {
            VolleyInternetOperator.accessWebsiteWithVolley(
                    context, queryFromServer, Request.Method.GET, loginJsonParams, headers,
                    responseFunction, errorFunction);
        }
    }

    public void responseFunctionImplementation(
            Context context, JSONObject response, JSONObject params)
    {
        try {
            JSONArray jsonArray = response.getJSONArray("results");
            mSnipsFromBackend.addAll(SnipConversionUtils.convertJsonArrayToSnipList(jsonArray));
            int mLastSnipsCollection = jsonArray.length();
            mAmountOfSnipsCollectedInCurrentSession += mLastSnipsCollection;
            String fullNextRequest = response.getString("next");
            if (fullNextRequest.equals("null"))
            {
                SnipCollectionInformation.getInstance().setLastSnipQuery(fullNextRequest);
            }
            else
            {
                String[] splittedFullNextRequest = fullNextRequest.split("/");
                String nextQueryString = splittedFullNextRequest[splittedFullNextRequest.length - 1];
                SnipCollectionInformation.getInstance().setLastSnipQuery(nextQueryString);
            }

            if ((!fullNextRequest.equals("null")) && (mAmountOfSnipsCollectedInCurrentSession < mSnipsToCollect))
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

        if (SnipCollectionInformation.getInstance().mLock.isLocked())
        {
            SnipCollectionInformation.getInstance().mLock.unlock();
        }
    }

    public void errorFunctionImplementation(VolleyError error, JSONObject params)
    {
        // TODO:: implement
        Log.d("error", "function");
    }
}
