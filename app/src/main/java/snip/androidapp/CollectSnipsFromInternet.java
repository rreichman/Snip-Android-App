package snip.androidapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.wang.avi.AVLoadingIndicatorView;

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
    private int mActivityCode;
    public String mBasicQuery;
    public boolean mShowAnimation;

    LinkedList<SnipData> mSnipsFromBackend = new LinkedList<SnipData>();

    public CollectSnipsFromInternet(Context context, String basicQuery, int activityCode, boolean showAnimation)
    {
        mSnipsToCollect = context.getResources().getInteger(R.integer.numSnipsPerLoading);
        mAmountOfSnipsCollectedInCurrentSession = 0;
        mBasicQuery = basicQuery;
        mActivityCode = activityCode;
        mShowAnimation = showAnimation;
    }

    public void retrieveSnipsFromInternet(final Context context)
    {
        retrieveSnipsFromInternet(context, null, mShowAnimation);
    }

    private void showHideLoadingAnimation(Context context, boolean toShow) {
        AVLoadingIndicatorView avi = (AVLoadingIndicatorView) ((Activity) context).findViewById(R.id.avi);
        if (null != avi) {
            if (toShow) {
                avi.show();
            } else {
                avi.hide();
            }
        }
    }
    // If the queryFromServer is null then we use the default query
    public void retrieveSnipsFromInternet(
            final Context context, String queryFromServer, boolean showAnimation)
    {
        showHideLoadingAnimation(context, showAnimation);
        JSONObject loginJsonParams = new JSONObject();

        HashMap<String,String> headers =
                SnipCollectionInformation.getInstance().getTokenForWebsiteAccessAsHashMap();

        VolleyInternetOperator.responseFunctionInterface responseFunction =
                new VolleyInternetOperator.responseFunctionInterface() {
                    @Override
                    public void apply(Context context, JSONObject response, JSONObject params)
                    {
                        showHideLoadingAnimation(context, false);
                        responseFunctionImplementation(context, response, params);
                    }
                };
        VolleyInternetOperator.errorFunctionInterface errorFunction =
                new VolleyInternetOperator.errorFunctionInterface() {
                    @Override
                    public void apply(Context context, VolleyError error, JSONObject params)
                    {
                        showHideLoadingAnimation(context, false);
                        errorFunctionImplementation(context, error, params);
                    }
                };

        if (null == queryFromServer)
        {
            Log.d("gettings snips", "here");
            Log.d("basic query is", mBasicQuery);
            VolleyInternetOperator.accessWebsiteWithVolley(
                    context,
                    mBasicQuery + SnipCollectionInformation.getInstance().getLastSnipQueryForActivity(mActivityCode),
                    Request.Method.GET,
                    loginJsonParams,
                    headers,
                    responseFunction, errorFunction);
        }
        else
        {
            Log.d("retrieving snips", queryFromServer);
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
                SnipCollectionInformation.getInstance().setLastSnipQuery(mActivityCode, fullNextRequest);
            }
            else
            {
                String[] splittedFullNextRequest = fullNextRequest.split("/");
                String nextQueryString = splittedFullNextRequest[splittedFullNextRequest.length - 1];
                SnipCollectionInformation.getInstance().setLastSnipQuery(mActivityCode, nextQueryString);
            }

            if ((!fullNextRequest.equals("null")) && (mAmountOfSnipsCollectedInCurrentSession < mSnipsToCollect))
            {
                retrieveSnipsFromInternet(context, mBasicQuery, mShowAnimation);
            }
            else
            {
                //SnipCollectionInformation.getInstance().setCollectedSnips(mSnipsFromBackend);
                Log.d("setting collected snips", Integer.toString(mSnipsFromBackend.size()));
                ((SnipHoldingActivity)context).populateActivity(mSnipsFromBackend);
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

    public void errorFunctionImplementation(Context context, VolleyError error, JSONObject params)
    {
        try
        {
            String errorString = VolleyInternetOperator.parseNetworkErrorResponse(error);
            checkUserPermissionStartLoginActivity(context, error.networkResponse.statusCode);
            Log.d("error", "Error collecting Snips");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    private void checkUserPermissionStartLoginActivity(Context context, int errorCode) {
        if (errorCode == 403) {
            Intent intent = new Intent(context, LoginActivity.class);
            // TODO check if it's ok with reichman
            ((MyActivity) context).startActivityForResult(intent, context.getResources().getInteger(R.integer.activityCodeLogin));
        }
    }


}
