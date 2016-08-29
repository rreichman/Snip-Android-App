package snip.androidapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.provider.ContactsContract;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.sql.Date;
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
    public HashMap<Integer,ReentrantLock> mLocks;
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
        mLocks = new HashMap<Integer, ReentrantLock>();
        mLocks.put(context.getResources().getInteger(R.integer.fragmentCodeMain), new ReentrantLock());
        mLocks.put(context.getResources().getInteger(R.integer.fragmentCodeLiked), new ReentrantLock());
        mLocks.put(context.getResources().getInteger(R.integer.fragmentCodeSnoozed), new ReentrantLock());

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

    private String getWebsiteTokenFromPreferences(Context context)
    {
        String userToken = context.getResources().getString(R.string.userTokenStringInPreferences);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String token = sharedPreferences.getString(userToken, null);
        return token;

        /*String tokenJsonAsString =
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

        return null;*/
    }

    public void setTokenForWebsiteAccess(String token)
    {
        mTokenForWebsiteAccess = token;
    }

    public void deleteTokenForWebsiteAccess(Context context)
    {
        mTokenForWebsiteAccess = null;
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPreferences.edit().remove(context.getResources().getString(R.string.userTokenStringInPreferences));
    }

    public String getTokenForWebsiteAccess(Context context)
    {
        if (null == mTokenForWebsiteAccess)
        {
            mTokenForWebsiteAccess = getWebsiteTokenFromPreferences(context);
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
