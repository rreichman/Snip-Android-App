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

    protected SnipCollectionInformation()
    {
        mLastSnipQuery = "";
        mLock = new ReentrantLock();
        mSnipsCollectedByNonUIThread = new LinkedList<SnipData>();
        mTokenForWebsiteAccess = null;
    }

    private String getWebsiteTokenFromFile(Context context)
    {
        String userTokenFile = context.getResources().getString(R.string.userTokenFile);
        JSONObject tokenJson =
                (JSONObject) DataCacheManagement.retrieveObjectFromFile(context, userTokenFile);

        if (null != tokenJson)
        {
            try
            {
                return tokenJson.getString(context.getResources().getString(R.string.tokenField));
            }
            catch (JSONException e)
            {
                e.printStackTrace();
            }
        }

        return null;
    }

    public void setTokenForWebsiteAccess(String token)
    {
        mTokenForWebsiteAccess = token;
    }

    public String getTokenForWebsiteAccess(Context context)
    {
        if (null == mTokenForWebsiteAccess)
        {
            mTokenForWebsiteAccess = getWebsiteTokenFromFile(context);
        }

        return mTokenForWebsiteAccess;
    }

    public HashMap<String, String> getTokenForWebsiteAccessAsHashMap(Context context)
    {
        HashMap<String,String> tokenAsHashmap = new HashMap<String, String>();
        // TODO:: make this great again
        //tokenAsHashmap.put("Authorization", "Token " + getTokenForWebsiteAccess(context));
        tokenAsHashmap.put("Authorization", "Token " + "ce53a666b61b6ea2a1950ead117bba3fa27b0f62");
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
