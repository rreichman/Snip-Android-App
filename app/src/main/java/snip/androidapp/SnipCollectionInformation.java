package snip.androidapp;

import java.util.LinkedList;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ranreichman on 7/31/16.
 */
public class SnipCollectionInformation
{
    private static SnipCollectionInformation mInstance = null;

    public String mLastSnipQuery;
    public boolean mFinishedCollectingSnips;
    public int mAmountOfSnipsPerLoad;
    public LinkedList<SnipData> mSnipsCollectedByNonUIThread;
    public ReentrantLock mLock;

    protected SnipCollectionInformation()
    {
        mLastSnipQuery = "";
        mFinishedCollectingSnips = false;
        mAmountOfSnipsPerLoad = 11;
        mLock = new ReentrantLock();
    }

    public LinkedList<SnipData> getCollectedSnipsAndCleanList()
    {
        LinkedList<SnipData> clonedList = (LinkedList<SnipData>) mSnipsCollectedByNonUIThread.clone();
        mSnipsCollectedByNonUIThread.clear();
        return clonedList;
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
