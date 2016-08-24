package snip.androidapp;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.Menu;
import android.view.MenuItem;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import butterknife.ButterKnife;

/**
 * Created by ranreichman on 8/23/16.
 */
public class MainActivity extends GenericSnipActivity
{
    int mCurrentFragmentCode;

    public int getActivityCode()
    {
        return getResources().getInteger(R.integer.activityCodeMain);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //DataCacheManagement.deleteAllInformationFiles(this);
        // TODO:: verify this
        mCurrentFragmentCode = getResources().getInteger(R.integer.fragmentCodeMain);
        startUI();
        initializeImportantStuff();
        addFragmentToScreen();
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

    private void startUI()
    {
        setContentView(R.layout.main_activity);
        BaseToolbar.setupToolbar(this);
        BaseToolbar.updateToolbarAccordingToFragment(this, mCurrentFragmentCode);
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
