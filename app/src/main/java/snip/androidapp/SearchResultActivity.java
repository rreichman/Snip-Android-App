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

    public void operateAfterLogin(Bundle savedInstanceState)
    {
        try
        {
            startActivityOperation();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public String getSnipsQueryLiked()
    {
        final String baseQuery = "?im_width=600&im_height=600";
        String baseAccessUrl = getResources().getString(R.string.baseAccessURL);
        String snipsBaseUrl = getResources().getString(R.string.snipsBaseURL);
        String likedBaseUrl = getResources().getString(R.string.likedBaseURL);
        return baseAccessUrl + snipsBaseUrl + likedBaseUrl + baseQuery;
    }

    public String getSnipsQuerySnoozed()
    {
        final String baseQuery = "?im_width=600&im_height=600";
        String baseAccessUrl = getResources().getString(R.string.baseAccessURL);
        String snipsBaseUrl = getResources().getString(R.string.snipsBaseURL);
        String snoozedBaseUrl = getResources().getString(R.string.snoozedBaseURL);
        return baseAccessUrl + snipsBaseUrl + snoozedBaseUrl + baseQuery;
    }

    public String getSnipsQueryForActivity()
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
