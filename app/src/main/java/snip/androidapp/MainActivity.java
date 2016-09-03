package snip.androidapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import butterknife.ButterKnife;

/**
 * Created by ranreichman on 8/23/16.
 */
public class MainActivity extends GenericSnipActivity
{
    boolean mShouldRestartFragment = false;

    // TODO:: expand this to liked and snoozed
//    ViewPager mPager;
//    ViewPagerAdapter mPagerAdapter;

    public int getActivityCode()
    {
        return getResources().getInteger(R.integer.activityCodeMain);
    }

    private void launch_notification_intent() {
        Intent intent = new Intent(this, RegistrationIntentService.class);
        // TODO:: Think if it's ok to start the service again and again
        startService(intent);
    }

    private void handle_intent() {
        if (getIntent().getExtras() != null) {
            int notification_request_code = getResources().getInteger(R.integer.notificationRequestCode);
            int request_code = getIntent().getIntExtra("RequestCode", -1);
            if (notification_request_code == request_code) {
                String snip_id = getIntent().getStringExtra("snip_id");
                if (null != snip_id) {
                    NotificationUtils.showSnip(this, snip_id);
                }
            }

        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        startUI(getResources().getInteger(R.integer.fragmentCodeMain));
        initializeImportantStuff();
        addFragmentToScreen();
        launch_notification_intent();
        if (null != SnipCollectionInformation.getInstance(this).getTokenForWebsiteAccess(this))
        {
            handle_intent();
        }

        /*if (null == mPagerAdapter)
        {
            mPagerAdapter = new ScreenSlidePagerAdapter(
                    getSupportFragmentManager(), this, mCurrentFragmentCode);
        }*/
        /*mPager = (ViewPager) findViewById(R.id.pager);
        mPagerAdapter = new ScreenSlidePagerAdapter(getSupportFragmentManager());
        mPager.setAdapter(mPagerAdapter);
        mPager.setVisibility(View.INVISIBLE);*/
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == getResources().getInteger(R.integer.activityCodeLogin))
        {
            if (resultCode == Activity.RESULT_OK)
            {
                // We want to do this in onResume and not in onActivityResult because otherwise Android
                // gets very mad. See http://www.androiddesignpatterns.com/2013/08/fragment-transaction-commit-state-loss.html
                mShouldRestartFragment = true;
            }
        }
    }

    @Override
    public void onResume()
    {
        if (mShouldRestartFragment)
        {
            addFragmentToScreen();
            mShouldRestartFragment = false;
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        MenuHandler.handleItemSelectedInMenu(this, item);
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed()
    {
        FragmentManager manager = getSupportFragmentManager();
        int count = manager.getBackStackEntryCount();

        if(count==0)
        {
            super.onBackPressed();
        }
        else
        {
            manager.popBackStack();
        }
    }

    private void startUI(int currentFragmentCode)
    {
        setContentView(R.layout.main_activity);
        BaseToolbar.setupToolbar(this);
        BaseToolbar.updateToolbarAccordingToFragment(this, currentFragmentCode);
    }

    private void addFragmentToScreen()
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        String fragmentTag = Integer.toString(getResources().getInteger(R.integer.fragmentCodeMain));
        ft.replace(R.id.fragmentPlaceholder, new MainFragment(), fragmentTag);
        ft.commit();
    }

    protected void initializeImportantStuff()
    {
        ButterKnife.bind(this);

        DisplayImageOptions displayImageOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .build();

        // TODO:: think if you want the caches to depend on the device..
        ImageLoaderConfiguration imageLoaderConfiguration =
                new ImageLoaderConfiguration.Builder(this)
                        .diskCacheSize(104857600)
                        .memoryCacheSize(41943040)
                        .threadPoolSize(10)
                        .defaultDisplayImageOptions(displayImageOptions)
                        .build();

        ImageLoader.getInstance().init(imageLoaderConfiguration);
    }
}
