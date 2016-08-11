package snip.androidapp;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.crashlytics.android.Crashlytics;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.LinkedList;

import butterknife.ButterKnife;
import io.fabric.sdk.android.Fabric;

/**
 * Created by ranreichman on 8/11/16.
 */
public abstract class SnipHoldingActivity extends AppCompatActivity
{
    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter<MyViewHolder> mAdapter;
    protected LinearLayoutManager mLayoutManager;
    protected SwipeRefreshLayout mSwipeContainer;
    // TODO:: what do i do with this when i load more snips and they aren't here? populate the list?
    // TODO:: Currently doesn't seem to be a bug. think why not! is it auto-populated?
    protected LinkedList<SnipData> mCollectedSnips;

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
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
    public void onRestoreInstanceState(Bundle savedInstanceState)
    {
        super.onRestoreInstanceState(savedInstanceState);
    }

    @Override
    public void onStop()
    {
        DataCacheManagement.saveAppInformationToFile(this, mCollectedSnips);
        super.onStop();
    }

    @Override
    public void onSaveInstanceState(Bundle outBundle)
    {
        DataCacheManagement.saveSnipDataToBundle(outBundle, mCollectedSnips);
        super.onSaveInstanceState(outBundle);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        initializeImportantStuff();

        if (null == SnipCollectionInformation.getInstance().getTokenForWebsiteAccess(this))
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, getResources().getInteger(R.integer.loginActivityCode));
        }
        else
        {
            try
            {
                mCollectedSnips = DataCacheManagement.retrieveSavedDataFromBundleOrFile(this, savedInstanceState);
                startActivityOperation();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }


    protected void initializeImportantStuff()
    {
        ButterKnife.bind(this);
        Fabric.with(this, new Crashlytics());
        // TODO:: make these do something real
        logUserIntoCrashlytics();
        LogUserActions.logContentView("Tweet", "Video", "1234");

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
        CustomVolleyRequestQueue.getInstance(this.getApplicationContext());
    }

    private void logUserIntoCrashlytics()
    {
        // TODO: Use the current user's information
        // You can call any combination of these three methods
        Crashlytics.setUserIdentifier("12345");
        Crashlytics.setUserEmail("testuser@snip.today");
        Crashlytics.setUserName("Test user");
    }

    protected void startUI()
    {
        setContentView(R.layout.my_activity);
        BaseToolbar activityToolbar = new BaseToolbar();
        activityToolbar.setupToolbar(this);
    }

    protected void setActivityVariables()
    {
        mRecyclerView = (RecyclerView) this.findViewById(R.id.snip_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(this));
        mSwipeContainer = (SwipeRefreshLayout)this.findViewById(R.id.swipeContainer);

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                SnipCollectionInformation.getInstance().setShouldRestartViewAfterCollection(true);
                deleteActivityCacheAndStartOver();
                Log.d("Refreshed!", "So refreshing!");
            }
        });
    }

    private void addPicturesToSnips()
    {
        for (int i = 0; i < mCollectedSnips.size(); i++)
        {
            if (null == mCollectedSnips.get(i).mThumbnail)
            {
                mCollectedSnips.get(i).mThumbnail =
                        SnipData.getBitmapFromUrl(mCollectedSnips.get(i).mThumbnailUrl);
            }
        }
    }

    protected void collectData()
    {
        // TODO:: is there a scenario where it's not null but empty and i still want to retrieve?
        if (null == mCollectedSnips)
        {
            CollectSnipsFromInternet snipCollector = new CollectSnipsFromInternet(getApplicationContext());
            //mCollectedSnips = snipCollector.retrieveSnipsFromInternet(this);
            snipCollector.retrieveSnipsFromInternet(this);
        }
        else
        {
            addPicturesToSnips();
            populateActivity();
        }
    }

    private void deleteActivityCacheAndStartOver()
    {
        mCollectedSnips = null;
        SnipCollectionInformation.getInstance().cleanLastSnipQuery();
        DataCacheManagement.deleteAppInformationFiles(this);
        startActivityOperation();
    }

    protected void populateActivity()
    {
        if ((null == mCollectedSnips) ||
                SnipCollectionInformation.getInstance().getShouldUseNewSnipsAndReset())
        {
            mCollectedSnips = SnipCollectionInformation.getInstance().getCollectedSnipsAndCleanList();
        }

        if ((null == mAdapter) ||
                SnipCollectionInformation.getInstance().getShouldRestartViewAfterCollectionAndReset())
        {
            startNewAdapter();
        }
        else
        {
            ((MyAdapter)mAdapter).mDataset = mCollectedSnips;
            mAdapter.notifyDataSetChanged();
        }
    }

    private void startNewAdapter()
    {
        mAdapter = new MyAdapter(mRecyclerView, mCollectedSnips, mLayoutManager);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback swipeTouchHelperCallback = getSwipeTouchHelperCallback();
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {});
    }

    private ItemTouchHelper.SimpleCallback getSwipeTouchHelperCallback()
    {
        return new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT)
        {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDirection)
            {
                int currentPositionInDataset = viewHolder.getAdapterPosition();
                if (ItemTouchHelper.LEFT == swipeDirection)
                {
                    ReactionManager.userDislikedSnip(mCollectedSnips.get(currentPositionInDataset).mID);
                    mCollectedSnips.remove(currentPositionInDataset);
                    mAdapter.notifyItemRemoved(currentPositionInDataset);
                    EndlessRecyclerOnScrollListener.onScrolledLogic(mRecyclerView, mLayoutManager);
                }

                if (ItemTouchHelper.RIGHT == swipeDirection)
                {
                    ReactionManager.userLikedSnip(mCollectedSnips.get(currentPositionInDataset).mID);
                    mCollectedSnips.remove(currentPositionInDataset);
                    mAdapter.notifyItemRemoved(currentPositionInDataset);
                    EndlessRecyclerOnScrollListener.onScrolledLogic(mRecyclerView, mLayoutManager);
                }

                mAdapter.notifyDataSetChanged();
            }

            @Override
            public boolean onMove(
                    RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder1, RecyclerView.ViewHolder viewHolder2)
            {
                return false;
            }

            @Override
            public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder)
            {
                getDefaultUIUtil().clearView(((MyViewHolder) viewHolder).mForeground);
            }

            @Override
            public void onChildDraw(
                    Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                    float dX, float dY, int actionState, boolean isCurrentlyActive)
            {
                final View foregroundView = ((MyViewHolder)viewHolder).mForeground;
                drawBackground(viewHolder, dX, actionState);
                getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
            }

            private void drawBackground(RecyclerView.ViewHolder viewHolder, float dX, int actionState)
            {
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE)
                {
                    Display display = getWindowManager().getDefaultDisplay();
                    Point screenSize = new Point();
                    display.getSize(screenSize);

                    final View backgroundViewRight = ((MyViewHolder) viewHolder).mSwipeBackgroundRight;
                    final View backgroundViewLeft = ((MyViewHolder) viewHolder).mSwipeBackgroundLeft;

                    // Swiping Right
                    if (dX > 0)
                    {
                        Log.d("right is", Integer.toString((int)Math.max(dX, 0)));
                        backgroundViewRight.setRight((int) Math.max(dX, 0));
                        backgroundViewRight.setLeft(0);

                        backgroundViewLeft.setRight(0);
                        backgroundViewLeft.setLeft(0);
                    }
                    // Swiping Left
                    else
                    {
                        // screenSize.x is the width of the screen
                        Log.d("left is", Integer.toString((int)(screenSize.x + dX)));
                        backgroundViewLeft.setRight(screenSize.x);
                        backgroundViewLeft.setLeft((int)(screenSize.x + dX));

                        backgroundViewRight.setRight(0);
                        backgroundViewRight.setLeft(0);
                    }
                }
            }
        };
    }

    protected void startActivityOperation()
    {
        startUI();
        setActivityVariables();
        collectData();
    }
}
