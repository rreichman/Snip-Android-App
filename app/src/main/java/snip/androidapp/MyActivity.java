package snip.androidapp;

import android.content.Intent;
import android.os.Bundle;


// TODO:: In the CardView layout - To create a card with a shadow, use the card_view:cardElevation attribute
// TODO:: add Fabric to app
// TODO:: this file looks terrible. divide it to two or three files.

/**
 * Created by ranreichman on 7/19/16.
 */
public class MyActivity extends SnipHoldingActivity
{
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == getResources().getInteger(R.integer.loginActivityCode))
        {
            if (resultCode == MyActivity.RESULT_OK)
            {
                startActivityOperation();
            }
        }
    }

    public void operateAfterLogin(Bundle savedInstanceState)
    {
        try
        {
            mCollectedSnips =
                    mDataCacheManagement.retrieveSavedDataFromBundleOrFile(this, savedInstanceState);
            startActivityOperation();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getSnipsQueryForActivity()
    {
        final String baseQuery = "?im_width=600&im_height=600";
        String lastRequestURL = SnipCollectionInformation.getInstance().getLastSnipQuery();
        String baseAccessUrl = getResources().getString(R.string.baseAccessURL);
        String snipsBaseUrl = getResources().getString(R.string.snipsBaseURL);
        String fullRequestURL = baseAccessUrl + snipsBaseUrl;
        if (lastRequestURL.isEmpty()) {
            fullRequestURL += baseQuery;
        }
        else {
            fullRequestURL += lastRequestURL;
        }
        return fullRequestURL;
    }

//    public void populateWithSnoozeSnips()
//    {
//        SnipCollectionInformation.getInstance().setShouldUseNewSnips(true);
//        CollectSnipsFromInternet collectSnipsFromInternet =
//                new CollectSnipsFromInternet(this);
//        collectSnipsFromInternet.retrieveSnipsFromInternet(
//                this,
//                CollectSnipsFromInternet.getSnipsQuerySnoozed(this));
//    }
//
//    public void populateWithLikedSnips()
//    {
//        SnipCollectionInformation.getInstance().setShouldUseNewSnips(true);
//        CollectSnipsFromInternet collectSnipsFromInternet =
//                new CollectSnipsFromInternet(getApplicationContext());
//        collectSnipsFromInternet.retrieveSnipsFromInternet(
//                this,
//                CollectSnipsFromInternet.getSnipsQueryLiked(this));
//    }
}