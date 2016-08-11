package snip.androidapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

/**
 * Created by ranihorev on 03/08/2016.
 */
public class BaseToolbar
{
    private void openSearchResultActivity(final AppCompatActivity currentActivity, int param)
    {
        /*if (MyActivity.class == currentActivity.getClass())
        {
            if (param == currentActivity.getResources().getInteger(R.integer.activityResultCollectSnoozed))
            {
                ((MyActivity)currentActivity).populateWithSnoozeSnips();
                return;
            }
            else if (param == currentActivity.getResources().getInteger(R.integer.activityResultCollectLiked))
            {
                ((MyActivity)currentActivity).populateWithLikedSnips();
                return;
            }
        }*/

        Intent searchScreenIntent = new Intent(currentActivity, SearchResultActivity.class);
        // TODO:: change this later
        searchScreenIntent.putExtra("param", param);
        currentActivity.startActivityForResult(searchScreenIntent, param);

        //((Activity)context).startActivityForResult(readsnipScreenIntent,0);
        //currentActivity.setResult(param);
        //currentActivity.finish();
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

        currentActivity.findViewById(R.id.snoozeButtonOnToolbar).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("clicked", "snooze");

                        openSearchResultActivity(
                                currentActivity,
                                currentActivity.getResources().getInteger(R.integer.activityResultCollectSnoozed));
                    }
                }
        );

        currentActivity.findViewById(R.id.likeButtonOnToolbar).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("clicked", "liked");
                        openSearchResultActivity(
                                currentActivity,
                                currentActivity.getResources().getInteger(R.integer.activityResultCollectLiked));
                    }
                }
        );

        // TODO:: add the
        /*currentActivity.findViewById(R.id.snipLogoOnToolbar).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.d("clicked", "liked");

                    }
                }
        );*/
    }

}
