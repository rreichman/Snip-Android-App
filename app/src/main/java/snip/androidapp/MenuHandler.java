package snip.androidapp;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;

/**
 * Created by ranreichman on 8/11/16.
 */
public class MenuHandler
{
    public static void handleItemSelectedInMenu(Activity activity, MenuItem item)
    {
        Log.d("Menu item - ", Integer.toString(item.getItemId()));
        switch (item.getItemId())
        {
            case R.id.feedbackMenu:
                openFeedbackMenu(activity);
                break;
            case R.id.logoutMenu:
                logoutAndOpenLoginActivity(activity);
                break;
        }
    }

    private static void openFeedbackMenu(Activity activity)
    {
        Intent feedbackIntent = new Intent(activity, FeedbackActivity.class);
        activity.startActivityForResult(feedbackIntent,activity.getResources().getInteger(R.integer.activityCodeFeedback));
    }

    private static void logoutAndOpenLoginActivity(Activity activity)
    {
        logoutUser(activity);
        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivityForResult(intent, activity.getResources().getInteger(R.integer.activityCodeLogin));
    }

    private static void logoutUser(Activity activity)
    {
        SnipCollectionInformation.getInstance(activity).deleteTokenForWebsiteAccess(activity);
        SnipCollectionInformation.getInstance(activity).setShouldRestartViewAfterCollection(true);
        DataCacheManagement.deleteAllInformationFiles(activity);
        LogUserActions.logUserLogout(activity);
    }
}
