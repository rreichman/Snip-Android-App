package snip.androidapp;

import java.util.LinkedList;

/**
 * Created by ranreichman on 7/31/16.
 */
public class SnipCollectionInformation
{
    private static SnipCollectionInformation mInstance = null;

    public String mLastSnipQuery;
    public boolean mIsCurrentlyLoading;
    // This is when i collected all the snips from scrolling and don't want to try anymore
    public boolean mNoMoreSnipsForNow;
    public boolean mFinishedCollectingSnips;
    public int mAmountOfSnipsPerLoad;
    public LinkedList<SnipData> mSnipsCollectedByNonUIThread;

    protected SnipCollectionInformation()
    {
        mLastSnipQuery = "";
        mIsCurrentlyLoading = false;
        mNoMoreSnipsForNow = false;
        mFinishedCollectingSnips = false;
        mAmountOfSnipsPerLoad = 11;
    }

    public void setCurrentlyLoading(boolean isCurrentlyLoading)
    {
        mIsCurrentlyLoading = isCurrentlyLoading;
    }

    // This is needed because of the AsyncTask input method (expects an array of strings)
    // Starts as a blank string for the first query, and then becomes ?limit=3&offset=3\
    public String[] getLastSnipQueryAsArray()
    {
        return new String[] {mLastSnipQuery};
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
