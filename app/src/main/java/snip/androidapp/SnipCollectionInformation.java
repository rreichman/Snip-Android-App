package snip.androidapp;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.StringTokenizer;
import java.util.concurrent.locks.ReentrantLock;

import butterknife.BindInt;

/**
 * Created by ranreichman on 7/31/16.
 */
public class SnipCollectionInformation
{
    private static SnipCollectionInformation mInstance = null;

    private String mLastSnipQuery;
    public LinkedList<SnipData> mSnipsCollectedByNonUIThread;
    public ReentrantLock mLock;
    private String mTokenForWebsiteAccess;
    private boolean mShouldRestartViewAfterCollection;
    // TODO:: this is a hack, needs to be changed
    private boolean mShouldUseNewSnips;

    protected SnipCollectionInformation()
    {
        mLastSnipQuery = "";
        mLock = new ReentrantLock();
        mSnipsCollectedByNonUIThread = new LinkedList<SnipData>();
        mTokenForWebsiteAccess = null;
        mShouldRestartViewAfterCollection = false;
    }

    public boolean getShouldUseNewSnipsAndReset()
    {
        boolean returnValue = mShouldUseNewSnips;
        mShouldUseNewSnips = false;
        return returnValue;
    }

    public void setShouldUseNewSnips(boolean value)
    {
        mShouldUseNewSnips = value;
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
        DataCacheManagement.deleteFileOnDisk(userTokenFile);
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

    public String getLastSnipQuery() {
        return mLastSnipQuery;
    }

    public void cleanLastSnipQuery() { mLastSnipQuery = ""; }

    public void setLastSnipQuery(String lastSnipQuery)
    {
        if (null != lastSnipQuery)
        {
            mLastSnipQuery = lastSnipQuery;
        }
        else
        {
            mLastSnipQuery = "";
        }
    }

    public LinkedList<SnipData> getCollectedSnipsAndCleanList()
    {
        LinkedList<SnipData> clonedList = (LinkedList<SnipData>) mSnipsCollectedByNonUIThread.clone();
        mSnipsCollectedByNonUIThread.clear();
        return clonedList;
    }

    public void setCollectedSnips(LinkedList<SnipData> snipData)
    {
        mSnipsCollectedByNonUIThread = snipData;
    }

    public int getAmountOfCollectedSnips()
    {
        if (null != mSnipsCollectedByNonUIThread)
        {
            return mSnipsCollectedByNonUIThread.size();
        }

        return 0;
    }

    public static synchronized SnipCollectionInformation getInstance()
    {
        if (null == mInstance)
        {
            mInstance = new SnipCollectionInformation();
        }
        return mInstance;
    }
}
