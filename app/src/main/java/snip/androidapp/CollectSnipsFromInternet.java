package snip.androidapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import android.support.v4.app.FragmentManager;

import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by ranreichman on 8/7/16.
 */
public class CollectSnipsFromInternet
{
    private int mSnipsToCollect;
    private int mAmountOfSnipsCollectedInCurrentSession;
    private int mFragmentCode;
    public String mBasicQuery;
    public boolean mShowAnimation;

    LinkedList<SnipData> mSnipsFromBackend = new LinkedList<SnipData>();

    public CollectSnipsFromInternet(Context context, String basicQuery, int fragmentCode, boolean showAnimation)
    {
        mSnipsToCollect = context.getResources().getInteger(R.integer.numSnipsPerLoading);
        mAmountOfSnipsCollectedInCurrentSession = 0;
        mBasicQuery = basicQuery;
        mFragmentCode = fragmentCode;
        mShowAnimation = showAnimation;
    }

    public void retrieveSnipsFromInternet(final Context context)
    {
        retrieveSnipsFromInternet(context, null, mShowAnimation);
    }

    // TODO:: fix this function
    // If the queryFromServer is null then we use the default query
    public void retrieveSnipsFromInternet(
            final Context context, String queryFromServer, boolean showAnimation)
    {
        if (SnipCollectionInformation.getInstance(context).mLocks.get(mFragmentCode).isLocked())
        {
            Log.d("no retrieve", "locked");
            return;
        }
        Log.d("retreive", "not locked, locking");

        SnipCollectionInformation.getInstance(context).mLocks.get(mFragmentCode).lock();
        try
        {
            // TODO:: check this
            //showHideLoadingAnimation(context, showAnimation);
            JSONObject loginJsonParams = new JSONObject();

            HashMap<String, String> headers =
                    SnipCollectionInformation.getInstance(context).getTokenForWebsiteAccessAsHashMap();

            VolleyInternetOperator.responseFunctionInterface responseFunction =
                    new VolleyInternetOperator.responseFunctionInterface() {
                        @Override
                        public void apply(Context context, JSONObject response, JSONObject params) {
                            AnimationUtils.showHideLoadingAnimation(context, false);
                            responseFunctionImplementation(context, response, params);
                        }
                    };
            VolleyInternetOperator.errorFunctionInterface errorFunction =
                    new VolleyInternetOperator.errorFunctionInterface() {
                        @Override
                        public void apply(Context context, VolleyError error, JSONObject params) {
                            AnimationUtils.showHideLoadingAnimation(context, false);
                            errorFunctionImplementation(context, error, params);
                        }
                    };

            if (null == queryFromServer) {
                Log.d("gettings snips", "here");
                Log.d("basic query is", mBasicQuery);
                VolleyInternetOperator.accessWebsiteWithVolley(
                        context,
                        mBasicQuery + SnipCollectionInformation.getInstance(context).getLastSnipQueryForFragment(mFragmentCode),
                        Request.Method.GET,
                        loginJsonParams,
                        headers,
                        responseFunction, errorFunction);
            } else {
                Log.d("retrieving snips", queryFromServer);
                VolleyInternetOperator.accessWebsiteWithVolley(
                        context, queryFromServer, Request.Method.GET, loginJsonParams, headers,
                        responseFunction, errorFunction);
            }
        }
        catch (Exception e)
        {
            if (SnipCollectionInformation.getInstance(context).mLocks.get(mFragmentCode).isLocked())
            {
                SnipCollectionInformation.getInstance(context).mLocks.get(mFragmentCode).unlock();
            }
        }
        Log.d("End of retrieve snips", "end");
    }

    // TODO:: fix this function
    public void responseFunctionImplementation(
            Context context, JSONObject response, JSONObject params)
    {
        Log.d("Starting", "response");
        try {
            JSONArray jsonArray = response.getJSONArray("results");
            mSnipsFromBackend.addAll(SnipConversionUtils.convertJsonArrayToSnipList(jsonArray));
            Log.d("Added snips", "from backend");
            int mLastSnipsCollection = jsonArray.length();
            mAmountOfSnipsCollectedInCurrentSession += mLastSnipsCollection;
            String fullNextRequest = response.getString("next");
            if (fullNextRequest.equals("null"))
            {
                SnipCollectionInformation.getInstance(context).setLastSnipQuery(mFragmentCode, fullNextRequest);
            }
            else
            {
                String[] splittedFullNextRequest = fullNextRequest.split("/");
                String nextQueryString = splittedFullNextRequest[splittedFullNextRequest.length - 1];
                SnipCollectionInformation.getInstance(context).setLastSnipQuery(mFragmentCode, nextQueryString);
            }

            if ((!fullNextRequest.equals("null")) && (mAmountOfSnipsCollectedInCurrentSession < mSnipsToCollect))
            {
                retrieveSnipsFromInternet(context, mBasicQuery, mShowAnimation);
            }
            else
            {
                Log.d("setting collected snips", Integer.toString(mSnipsFromBackend.size()));
                SnipHoldingFragment fragment =
                        (SnipHoldingFragment)((MainActivity)context).getSupportFragmentManager().
                                findFragmentByTag(Integer.toString(mFragmentCode));

                if (null != fragment)
                {
                    fragment.populateFragment(mSnipsFromBackend);
                }
                else
                {
                    SnipTempManagement.getInstance(context).
                            mSnipsToLoadInFragment.get(mFragmentCode).addAll(mSnipsFromBackend);
                }
            }

            AnimationUtils.moveLoadingAnimationToBottom(context);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        Log.d("should unlock?", "Checking");
        if (SnipCollectionInformation.getInstance(context).mLocks.get(mFragmentCode).isLocked())
        {
            Log.d("unlocking", "Checked");
            SnipCollectionInformation.getInstance(context).mLocks.get(mFragmentCode).unlock();
        }
    }

    public void errorFunctionImplementation(Context context, VolleyError error, JSONObject params)
    {
        try
        {
            String errorString = VolleyInternetOperator.parseNetworkErrorResponse(error);
            if (error instanceof TimeoutError)
            {
                Toast.makeText(context, "Unable to load snips. Is your internet connection stable?", Toast.LENGTH_LONG).show();
            }
            else if (error instanceof NoConnectionError)
            {
                String toastText = "Unable to load snips. Are you connected to the Internet?";
                if (InternetUtils.isInternetAvailable())
                {
                    toastText = "We seem to be having server problems. Sorry about that";
                    LogUserActions.logServerError(context);
                }
                Toast.makeText(context, toastText, Toast.LENGTH_LONG).show();
            }
            checkUserPermissionStartLoginActivity(context, error.networkResponse.statusCode);
            Log.d("error", "Error collecting Snips");
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        Log.d("should unlock?", "Checking");
        if (SnipCollectionInformation.getInstance(context).mLocks.get(mFragmentCode).isLocked())
        {
            Log.d("unlocking", "Checked");
            SnipCollectionInformation.getInstance(context).mLocks.get(mFragmentCode).lock();
        }
    }

    private void checkUserPermissionStartLoginActivity(Context context, int errorCode) {
        final int FORBIDDEN_ERROR_IN_WEBSITE = 403;
        if (errorCode == FORBIDDEN_ERROR_IN_WEBSITE)
        {
            Intent intent = new Intent(context, LoginActivity.class);
            ((MainActivity) context).startActivityForResult(intent, context.getResources().getInteger(R.integer.activityCodeLogin));
        }
        else
        {
            LogUserActions.logAppError(context, "PermissionError", errorCode);
        }
    }


}
