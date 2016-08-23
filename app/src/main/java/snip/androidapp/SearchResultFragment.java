package snip.androidapp;

import android.os.Bundle;
import android.view.View;

/**
 * Created by ranreichman on 8/23/16.
 */
public class SearchResultFragment extends SnipHoldingFragment
{
    // Is it liked or snoozed
    public int mSearchResultType;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        mSearchResultType = getArguments().getInt("param");
        super.onViewCreated(view,savedInstanceState);
    }

    @Override
    protected int getFragmentCode()
    {
        return mSearchResultType;
    }

    @Override
    protected String getBaseSnipsQueryForFragment()
    {
        if (getResources().getInteger(R.integer.fragmentCodeSnoozed) == mSearchResultType)
        {
            return getSnipsQuerySnoozed();
        }
        else if (getResources().getInteger(R.integer.fragmentCodeLiked) == mSearchResultType)
        {
            return getSnipsQueryLiked();
        }
        else
        {
            return null;
        }
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
}
