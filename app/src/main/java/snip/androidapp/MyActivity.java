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
        if (requestCode == getResources().getInteger(R.integer.activityCodeLogin))
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

    public String getBaseSnipsQueryForActivity()
    {
        String lastRequestURL =
                SnipCollectionInformation.getInstance().getLastSnipQueryForActivity(getActivityCode());
        String baseAccessUrl = getResources().getString(R.string.baseAccessURL);
        String snipsBaseUrl = getResources().getString(R.string.snipsBaseURL);
        String newSnipsBaseUrl = getResources().getString(R.string.newSnipsBaseURL);
        String fullRequestURL = baseAccessUrl + snipsBaseUrl + newSnipsBaseUrl;
        /*if (lastRequestURL.isEmpty() || lastRequestURL.equals("null")) {
            fullRequestURL += SnipCollectionInformation.getInstance().getDimensionsQuery();
        }
        else {
            fullRequestURL += lastRequestURL;
        }*/
        return fullRequestURL;
    }

    public int getActivityCode()
    {
        return getResources().getInteger(R.integer.activityCodeMyActivity);
    }
}