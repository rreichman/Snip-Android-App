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
        SnipCollectionInformation.getInstance().mSnipsCollectedByNonUIThread =
                CollectDataFromInternet.collectSnipsFromBackend(
                        SnipCollectionInformation.getInstance().mAmountOfSnipsPerLoad);
        Log.d("Accessed snips, size: ",
                Integer.toString(SnipCollectionInformation.getInstance().mSnipsCollectedByNonUIThread.size()));
        return null;
    }
}
