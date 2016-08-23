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
        AVLoadingIndicatorView avi = (AVLoadingIndicatorView) ((Activity) context).findViewById(R.id.avi_fragment);
        if (null != avi) {
            if (toShow) {
                avi.setVisibility(avi.VISIBLE);
                avi.show();
            } else {
                avi.hide();
            }
        }
    }

    private void moveLoadingAnimationToBottom(Context context) {
        try
        {
            AVLoadingIndicatorView avi = (AVLoadingIndicatorView) ((Activity) context).findViewById(R.id.avi_fragment);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) avi.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            avi.setLayoutParams(params);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // If the queryFromServer is null then we use the default query
    public void retrieveSnipsFromInternet(
            final Context context, String queryFromServer, boolean showAnimation)
    {
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
                    mBasicQuery + SnipCollectionInformation.getInstance(context).getLastSnipQueryForFragment(mActivityCode),
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
                SnipCollectionInformation.getInstance(context).setLastSnipQuery(mActivityCode, fullNextRequest);
            }
            else
            {
                String[] splittedFullNextRequest = fullNextRequest.split("/");
                String nextQueryString = splittedFullNextRequest[splittedFullNextRequest.length - 1];
                SnipCollectionInformation.getInstance(context).setLastSnipQuery(mActivityCode, nextQueryString);
            }

            if ((!fullNextRequest.equals("null")) && (mAmountOfSnipsCollectedInCurrentSession < mSnipsToCollect))
            {
                retrieveSnipsFromInternet(context, mBasicQuery, mShowAnimation);
            }
            else
            {
                //SnipCollectionInformation.getInstance().setCollectedSnips(mSnipsFromBackend);
                Log.d("setting collected snips", Integer.toString(mSnipsFromBackend.size()));
                FragmentManager manager = ((MainActivity)context).getSupportFragmentManager();
                SnipHoldingFragment fragment = (SnipHoldingFragment)manager.findFragmentById(R.id.fragmentPlaceholder);
                fragment.populateFragment(mSnipsFromBackend);
  //              SnipHoldingFragment fragment = (SnipHoldingFragment)
//                        ((MainActivity)context).getFragmentManager().findFragmentById(R.id.fragmentPlaceholder);
                //SnipHoldingFragment f;
                //f.populateFragment(mSnipsFromBackend);
                //Fragment fragment = ((MainActivity)context).findViewById(R.id.fragmentPlaceholder);
                //((Fragment)context).populat(mSnipsFromBackend);
            }

            moveLoadingAnimationToBottom(context);

        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        if (SnipCollectionInformation.getInstance(context).mLock.isLocked())
        {
            SnipCollectionInformation.getInstance(context).mLock.unlock();
        }
    }

    private boolean isInternetAvailable()
    {
        try
        {
            InetAddress ipAddr = InetAddress.getByName("google.com"); //You can replace it with your name
            InetAddress ipAddr2 = InetAddress.getByName("twitter.com");
            return (!ipAddr.equals("") && !ipAddr2.equals(""));
        }
        catch (Exception e) {
            return false;
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
                if (isInternetAvailable())
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

        if (SnipCollectionInformation.getInstance(context).mLock.isLocked())
        {
            SnipCollectionInformation.getInstance(context).mLock.unlock();
        }
    }

    private void checkUserPermissionStartLoginActivity(Context context, int errorCode) {
        if (errorCode == 403)
        {
            Intent intent = new Intent(context, LoginActivity.class);
            ((MyActivity) context).startActivityForResult(intent, context.getResources().getInteger(R.integer.activityCodeLogin));
        }
    }


}
