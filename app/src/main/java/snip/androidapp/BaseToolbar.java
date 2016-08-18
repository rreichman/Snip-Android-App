package snip.androidapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

/**
 * Created by ranihorev on 03/08/2016.
 */
public class BaseToolbar
{
    private void openSearchResultActivity(final AppCompatActivity currentActivity, int param)
    {

        Intent searchScreenIntent = new Intent(currentActivity, SearchResultActivity.class);
        // TODO:: change this later
        searchScreenIntent.putExtra("param", param);
        currentActivity.startActivityForResult(searchScreenIntent, param);

    }

    public void setupToolbar(final AppCompatActivity currentActivity)
    {
        android.support.v7.widget.Toolbar myToolbar =
                (android.support.v7.widget.Toolbar) currentActivity.findViewById(R.id.toolbar);

        if (myToolbar != null)
        {
            currentActivity.setSupportActionBar(myToolbar);
            currentActivity.getSupportActionBar().setDisplayUseLogoEnabled(true);
            currentActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            //getSupportActionBar().setHomeButtonEnabled(true);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }

        int activityType = -1;
        if (SearchResultActivity.class == currentActivity.getClass())
        {
            activityType = ((SearchResultActivity)currentActivity).mSearchResultType;
            if (currentActivity.getResources().getInteger(R.integer.activityCodeSnoozed) == activityType) {
                ImageView curImage = (ImageView) currentActivity.findViewById(R.id.snoozeButtonOnToolbar);
                curImage.setImageResource(R.drawable.snooze_blue);
            }
            else if (currentActivity.getResources().getInteger(R.integer.activityCodeLiked) == activityType) {
                ImageView curImage = (ImageView) currentActivity.findViewById(R.id.likeButtonOnToolbar);
                curImage.setImageResource(R.drawable.heart_icon_full);
            }
        }

        final int activityTypeFinal = activityType;
        currentActivity.findViewById(R.id.snoozeButtonOnToolbar).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("clicked", "snooze");
                        if (activityTypeFinal != view.getResources().getInteger(R.integer.activityCodeSnoozed)) {
                            openSearchResultActivity(currentActivity,
                                    currentActivity.getResources().getInteger(R.integer.activityCodeSnoozed));
                        }
                        else {
                            currentActivity.finish();
                        }
                    }
                }
        );

        currentActivity.findViewById(R.id.likeButtonOnToolbar).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("clicked", "liked");
                        if (activityTypeFinal != view.getResources().getInteger(R.integer.activityCodeLiked)) {
                            openSearchResultActivity(currentActivity,
                                    currentActivity.getResources().getInteger(R.integer.activityCodeLiked));
                        }
                        else {
                            currentActivity.finish();
                        }
                    }
                }
        );
    }

}
