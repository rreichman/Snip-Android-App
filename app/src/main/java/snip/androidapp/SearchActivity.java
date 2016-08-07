package snip.androidapp;

import android.app.Activity;

import android.os.Bundle;

/**
 * Created by ranreichman on 8/7/16.
 */
public class SearchActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // TODO:: this class should include a search widget
        asyncPopulateRecommendations();
    }

    private void asyncPopulateRecommendations()
    {
        // TODO:: get the recommendations from the server
        // TODO:: present them on the screen
    }
}
