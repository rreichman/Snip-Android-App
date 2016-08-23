package snip.androidapp;

import android.os.Bundle;
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
    BaseToolbar mBaseToolbar;
    int mCurrentFragmentCode;

    public int getActivityCode()
    {
        return getResources().getInteger(R.integer.activityCodeMain);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
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

    private void startUI()
    {
        setContentView(R.layout.main_activity);
        mBaseToolbar = new BaseToolbar();
        mBaseToolbar.setupToolbar(this);
        mBaseToolbar.updateToolbarAccordingToFragment(this, mCurrentFragmentCode);
    }

    private void addFragmentToScreen()
    {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragmentPlaceholder, new MainFragment());
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
