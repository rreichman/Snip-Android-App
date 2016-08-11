package snip.androidapp;

import android.app.Activity;
import android.content.Intent;
import android.view.MenuItem;

/**
 * Created by ranreichman on 8/11/16.
 */
public class MenuHandler
{
    public static void handleItemSelectedInMenu(Activity activity, MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.feedbackMenu:
                openFeedbackMenu(activity);
        }
    }

    private static void openFeedbackMenu(Activity activity)
    {
        Intent feedbackIntent = new Intent(activity, FeedbackActivity.class);
        activity.startActivityForResult(feedbackIntent,0);
    }
}
