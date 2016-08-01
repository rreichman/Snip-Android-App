package snip.androidapp;

import android.os.AsyncTask;
import android.util.Log;

/**
 * Created by ranreichman on 7/31/16.
 */
public class AsyncInternetAccessor extends AsyncTask<Void,Void,Void>
{
    protected Void doInBackground(Void... params)
    {
        Log.d("Locking", "");
        if ((!SnipCollectionInformation.getInstance().mLock.isLocked()) &&
                (!SnipCollectionInformation.getInstance().mLastSnipQuery.equals("null")))
        {
            SnipCollectionInformation.getInstance().mLock.lock();

            SnipCollectionInformation.getInstance().mSnipsCollectedByNonUIThread =
                    CollectDataFromInternet.collectSnipsFromBackend(
                            SnipCollectionInformation.getInstance().mAmountOfSnipsPerLoad);
            Log.d("Accessed snips, size: ",
                    Integer.toString(SnipCollectionInformation.getInstance().mSnipsCollectedByNonUIThread.size()));

            Log.d("Unlocking", "");
            SnipCollectionInformation.getInstance().mLock.unlock();
        }

        return null;
    }
}
