package snip.androidapp;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.support.v7.widget.helper.ItemTouchHelper;

import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;


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

    public void populateWithSnoozeSnips()
    {
        SnipCollectionInformation.getInstance().setShouldUseNewSnips(true);
        CollectSnipsFromInternet snipCollector =
                new CollectSnipsFromInternet(this);
        snipCollector.retrieveSnipsFromInternet(this, CollectSnipsFromInternet.getSnipsQuerySnoozed(this));
    }

    public void populateWithLikedSnips()
    {
        SnipCollectionInformation.getInstance().setShouldUseNewSnips(true);
        CollectSnipsFromInternet snipCollector =
                new CollectSnipsFromInternet(getApplicationContext());
        snipCollector.retrieveSnipsFromInternet(this, CollectSnipsFromInternet.getSnipsQueryLiked(this));
    }
}