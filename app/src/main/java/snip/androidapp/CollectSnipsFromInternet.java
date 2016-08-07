package snip.androidapp;

import android.content.Context;

import org.json.JSONObject;

import java.util.LinkedList;

/**
 * Created by ranreichman on 8/7/16.
 */
public class CollectSnipsFromInternet
{
    String mBaseAccessURL;
    String mGetSnipsBaseURL;
    String mTokenField;

    public CollectSnipsFromInternet(String baseAccessURL, String getSnipsBaseURL, String tokenField)
    {
        mBaseAccessURL = baseAccessURL;
        mGetSnipsBaseURL = getSnipsBaseURL;
        mTokenField = tokenField;
    }

    public LinkedList<SnipData> retrieveSnipsFromInternet(Context context)
    {
        collectSnips(context, null);
        return SnipCollectionInformation.getInstance().getCollectedSnipsAndCleanList();
    }

    private void collectSnips(Context context, JSONObject tokenParams)
    {

    }
}
