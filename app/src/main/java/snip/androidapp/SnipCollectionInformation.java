package snip.androidapp;

import android.app.Activity;
import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReentrantLock;

import butterknife.BindInt;
import butterknife.BindString;

/**
 * Created by ranreichman on 7/31/16.
 */
public class SnipCollectionInformation
{
    private static SnipCollectionInformation mInstance = null;

    private HashMap<Integer,String> mLastSnipQueryPerFragment;
    public ReentrantLock mLock;
    private String mTokenForWebsiteAccess;
    private boolean mShouldRestartViewAfterCollection;

    private void initializeLastQueries(Context context)
    {
        mLastSnipQueryPerFragment = new HashMap<Integer, String>();
        mLastSnipQueryPerFragment.put(context.getResources().getInteger(R.integer.fragmentCodeMain),"");
        mLastSnipQueryPerFragment.put(context.getResources().getInteger(R.integer.fragmentCodeLiked),"");
        mLastSnipQueryPerFragment.put(context.getResources().getInteger(R.integer.fragmentCodeSnoozed),"");
    }

    public String getDimensionsQuery()
    {
        return "?im_width=600&im_height=600";
    }

    protected SnipCollectionInformation(Context context)
    {
        initializeLastQueries(context);
        mLock = new ReentrantLock();
        mTokenForWebsiteAccess = null;
        mShouldRestartViewAfterCollection = false;
    }

    public String getLastSnipQueryForFragment(int fragmentCode)
    {
        Log.d("snipQueryForActivity" + Integer.toString(fragmentCode),
                mLastSnipQueryPerFragment.get(fragmentCode));
        return mLastSnipQueryPerFragment.get(fragmentCode);
    }

    public boolean getShouldRestartViewAfterCollectionAndReset()
    {
        boolean returnValue = mShouldRestartViewAfterCollection;
        mShouldRestartViewAfterCollection = false;
        return returnValue;
    }

    public void setShouldRestartViewAfterCollection(boolean value)
    {
        mShouldRestartViewAfterCollection = value;
    }

    private String getWebsiteTokenFromFile(Context context)
    {
        String userTokenFile = context.getResources().getString(R.string.userTokenFile);
        String tokenJsonAsString =
                (String)DataCacheManagement.retrieveObjectFromFile(context, userTokenFile);
        try
        {
            if (null != tokenJsonAsString)
            {
                JSONObject tokenJson = new JSONObject(tokenJsonAsString);
                return tokenJson.getString(context.getResources().getString(R.string.tokenField));
            }
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }

        return null;
    }

    public void setTokenForWebsiteAccess(String token)
    {
        mTokenForWebsiteAccess = token;
    }

    public void deleteTokenForWebsiteAccess(Context context)
    {
        mTokenForWebsiteAccess = null;
        String userTokenFile = context.getResources().getString(R.string.userTokenFile);
        DataCacheManagement.deleteFileOnDisk(
                DataCacheManagement.getFullPathOfFile(context, userTokenFile));
    }

    public String getTokenForWebsiteAccess(Context context)
    {
        if (null == mTokenForWebsiteAccess)
        {
            mTokenForWebsiteAccess = getWebsiteTokenFromFile(context);
        }

        return mTokenForWebsiteAccess;
    }

    public HashMap<String, String> getTokenForWebsiteAccessAsHashMap()
    {
        HashMap<String,String> tokenAsHashmap = new HashMap<String, String>();
        tokenAsHashmap.put("Authorization", "Token " + getTokenForWebsiteAccess(CustomVolleyRequestQueue.getInstance().getContext()));
        return tokenAsHashmap;
    }

    public void cleanLastSnipQuery(int fragmentCode) { mLastSnipQueryPerFragment.put(fragmentCode,""); }

    public void setLastSnipQuery(int fragmentCode, String lastSnipQuery)
    {
        if (null != lastSnipQuery)
        {
            mLastSnipQueryPerFragment.put(fragmentCode, lastSnipQuery);
        }
        else
        {
            mLastSnipQueryPerFragment.put(fragmentCode, "");
        }
    }

    public static synchronized SnipCollectionInformation getInstance(Context context)
    {
        if (null == mInstance)
        {
            mInstance = new SnipCollectionInformation(context);
        }
        return mInstance;
    }
}
