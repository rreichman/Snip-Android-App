package snip.androidapp;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.LinkedList;

import butterknife.ButterKnife;

/**
 * Created by ranreichman on 8/11/16.
 */
public abstract class SnipHoldingActivity extends GenericSnipActivity
{
    protected RecyclerView mRecyclerView;
    protected MyAdapter mAdapter;
    protected LinearLayoutManager mLayoutManager;
    protected SwipeRefreshLayout mSwipeContainer;

    @Override
    public void onPause()
    {
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (null != mAdapter) {
            ((MyAdapter) mAdapter).removeIdsFromDataset(
                    SnipReactionsSingleton.getInstance().getIdsToRemoveFromDataset());
            // Here not using asyncNotifyDatasetChanged on purpose because i want the user to wait
            // TODO:: think if this is true
            mAdapter.notifyDataSetChanged();
            SnipReactionsSingleton.getInstance().mSnipsReactions.clear();
        }
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
        if (null != mAdapter)
        {
            DataCacheManagement.saveAppInformationToFile(this, mAdapter.mDataset, getActivityCode());
        }
        super.onStop();
    }



    @Override
    public void onSaveInstanceState(Bundle outBundle) {
        if (null != mAdapter)
        {
            DataCacheManagement.saveSnipDataToBundle(outBundle, mAdapter.mDataset);
        }
        super.onSaveInstanceState(outBundle);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        LogUserActions.logStartingActivity(this, getActivityCode());
        initializeImportantStuff();

        if (null == SnipCollectionInformation.getInstance().getTokenForWebsiteAccess(this))
        {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivityForResult(intent, getResources().getInteger(R.integer.activityCodeLogin));
        }
        else
        {
            operateAfterLogin(savedInstanceState);
        }
    }

    protected void operateAfterLogin(Bundle savedInstanceState)
    {
        try
        {
            Log.d("operate after", "login");
            LinkedList<SnipData> cachedSnips =
                    DataCacheManagement.retrieveSavedDataFromBundleOrFile(
                            this, savedInstanceState, getActivityCode());
            startActivityOperation(cachedSnips);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected abstract String getBaseSnipsQueryForActivity();

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
        CustomVolleyRequestQueue.getInstance(this.getApplicationContext());
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
        if (null != mAdapter)
        {
            if (null != mAdapter.mDataset) {
                for (int i = 0; i < mAdapter.mDataset.size(); i++) {
                    if (null == mAdapter.mDataset.get(i).mThumbnail) {
                        mAdapter.mDataset.get(i).mThumbnail =
                                SnipData.getBitmapFromUrl(mAdapter.mDataset.get(i).mThumbnailUrl);
                    }
                }
            }
        }
    }

    protected void collectData()
    {
        CollectSnipsFromInternet snipCollector = new CollectSnipsFromInternet(
                getApplicationContext(),
                getBaseSnipsQueryForActivity() +
                        SnipCollectionInformation.getInstance().getDimensionsQuery(),
                getActivityCode(),
                true);
        snipCollector.retrieveSnipsFromInternet(this);
    }

    private void deleteActivityCacheAndStartOver()
    {
        SnipCollectionInformation.getInstance().cleanLastSnipQuery(getActivityCode());
        DataCacheManagement.deleteActivityInformationFiles(this, getActivityCode());
        startActivityOperation(null);
    }

    public void asyncNotifyDatasetChanged()
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    protected void populateActivity(LinkedList<SnipData> addedSnips)
    {
        if (null != addedSnips)
        {
            if ((null == mAdapter) ||
                    SnipCollectionInformation.getInstance().getShouldRestartViewAfterCollectionAndReset())
            {
                startNewAdapter(addedSnips);
            }
            else
            {
                ((MyAdapter)mAdapter).addAll(addedSnips);
                asyncNotifyDatasetChanged();
            }
            addPicturesToSnips();
        }
    }

    private void startNewAdapter(LinkedList<SnipData> snipsToStartWith)
    {
        mAdapter = new MyAdapter(
                mRecyclerView, snipsToStartWith, mLayoutManager,
                getBaseSnipsQueryForActivity(), getActivityCode());
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback swipeTouchHelperCallback =
                getSwipeTouchHelperCallback(getActivityCode());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(
                mLayoutManager, getBaseSnipsQueryForActivity(), getActivityCode()) {});
    }

    private ItemTouchHelper.SimpleCallback getSwipeTouchHelperCallback(int activityCode)
    {
        int callbackSwipeParam = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        if (getResources().getInteger(R.integer.activityCodeLiked) == activityCode)
        {
            callbackSwipeParam = ItemTouchHelper.LEFT;
        }

        return new ItemTouchHelper.SimpleCallback(0, callbackSwipeParam)
        {
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDirection)
            {
                int currentPositionInDataset = viewHolder.getAdapterPosition();
                if (ItemTouchHelper.LEFT == swipeDirection)
                {
                    ReactionManager.userDislikedSnip(mAdapter.mDataset.get(currentPositionInDataset).mID);
                    mAdapter.remove(currentPositionInDataset);
                    mAdapter.notifyItemRemoved(currentPositionInDataset);
                    EndlessRecyclerOnScrollListener.onScrolledLogic(
                            mRecyclerView, mLayoutManager, getBaseSnipsQueryForActivity(), getActivityCode(), false);
                }

                if (ItemTouchHelper.RIGHT == swipeDirection)
                {
                    ReactionManager.userLikedSnip(mAdapter.mDataset.get(currentPositionInDataset).mID);
                    mAdapter.remove(currentPositionInDataset);
                    mAdapter.notifyItemRemoved(currentPositionInDataset);
                    EndlessRecyclerOnScrollListener.onScrolledLogic(
                            mRecyclerView, mLayoutManager, getBaseSnipsQueryForActivity(), getActivityCode(), false);
                }

                asyncNotifyDatasetChanged();
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

    protected void startActivityOperation(LinkedList<SnipData> snipsToStartWith)
    {
        startUI();
        setActivityVariables();
        if (null != snipsToStartWith)
        {
            populateActivity(snipsToStartWith);
        }
        else
        {
            collectData();
        }
    }
}
