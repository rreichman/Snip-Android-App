package snip.androidapp;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.view.Menu;
import android.os.Bundle;
import android.content.res.Configuration;
import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;


// TODO:: In the CardView layout - To create a card with a shadow, use the card_view:cardElevation attribute
// TODO:: add Fabric to app
// TODO:: this file looks terrible. divide it to two or three files.

/**
 * Created by ranreichman on 7/19/16.
 */
public class MyActivity extends AppCompatActivity
{
    @Override
    public void onStop()
    {
        ActivityOperations.getInstance(this).saveAppInformationToFile();
        super.onStop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outBundle)
    {
        ActivityOperations.getInstance(this).saveSnipDataToBundle(outBundle);
        super.onSaveInstanceState(outBundle);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        ActivityOperations.getInstance(this).initalizeImportantStuff();

        try {
            ActivityOperations.getInstance(this).retrieveSavedDataFromBundleOrFile(savedInstanceState);
            ActivityOperations.getInstance(this).startActivityOperation();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        mAdapter.resetAdapter();
//         Checks the orientation of the screen
//        if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
//            SquareImageView imgView = (SquareImageView) findViewById(R.id.thumbnail);
//            LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) imgView.getLayoutParams();
//            lp.weight = 0.1f;
//        }
    }


    private void startUI() {
        setContentView(R.layout.my_activity);
        BaseToolbar activityToolbar = new BaseToolbar();
        activityToolbar.setupToolbar(this);
    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
}