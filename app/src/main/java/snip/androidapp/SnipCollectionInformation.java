package snip.androidapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;
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
    private String mTokenKey;

    protected SnipCollectionInformation() {
        mLastSnipQuery = "";
        mLock = new ReentrantLock();
        mSnipsCollectedByNonUIThread = new LinkedList<SnipData>();
    }


    public int getAmountOfSnipsPerLoad() {
        return 10;
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

    public void setTokenKey(String token){
        mTokenKey = token;
    }

    public String getTokenKey() {
        return mTokenKey;
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
