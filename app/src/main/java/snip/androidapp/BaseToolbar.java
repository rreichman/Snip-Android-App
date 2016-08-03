package snip.androidapp;

import android.app.Activity;
import android.support.v7.app.AppCompatActivity;
/**
 * Created by ranihorev on 03/08/2016.
 */
public class BaseToolbar {
    public void setupToolbar(AppCompatActivity curActivity) {
        android.support.v7.widget.Toolbar myToolbar = (android.support.v7.widget.Toolbar) curActivity.findViewById(R.id.toolbar);
        if (myToolbar != null) {
            curActivity.setSupportActionBar(myToolbar);
            curActivity.getSupportActionBar().setLogo(R.drawable.sniplogosmall);
            curActivity.getSupportActionBar().setDisplayUseLogoEnabled(true);
            curActivity.getSupportActionBar().setDisplayShowTitleEnabled(false);
            //getSupportActionBar().setHomeButtonEnabled(true);
            //getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        }
    }

}
