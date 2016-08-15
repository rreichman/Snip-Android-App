package snip.androidapp;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by ranreichman on 8/11/16.
 */
public class SearchResultActivity extends SnipHoldingActivity
{
    // Is it liked or snoozed
    public int mSearchResultType;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        //mSearchResultType = savedInstanceState.getInt()
        Intent intent = getIntent();
        mSearchResultType = intent.getIntExtra("param", 0);
        super.onCreate(savedInstanceState);
    }

    public String getSnipsQueryLiked()
    {
        String baseAccessUrl = getResources().getString(R.string.baseAccessURL);
        String snipsBaseUrl = getResources().getString(R.string.snipsBaseURL);
        String likedBaseUrl = getResources().getString(R.string.likedBaseURL);
        return baseAccessUrl + snipsBaseUrl + likedBaseUrl;
    }

    public String getSnipsQuerySnoozed()
    {
        String baseAccessUrl = getResources().getString(R.string.baseAccessURL);
        String snipsBaseUrl = getResources().getString(R.string.snipsBaseURL);
        String snoozedBaseUrl = getResources().getString(R.string.snoozedBaseURL);
        return baseAccessUrl + snipsBaseUrl + snoozedBaseUrl;
    }

    public String getBaseSnipsQueryForActivity()
    {
        if (getResources().getInteger(R.integer.activityCodeSnoozed) == mSearchResultType)
        {
            return getSnipsQuerySnoozed();
        }
        else if (getResources().getInteger(R.integer.activityCodeLiked) == mSearchResultType)
        {
            return getSnipsQueryLiked();
        }
        else
        {
            return null;
        }
    }

    public int getActivityCode()
    {
        return mSearchResultType;
    }
}
