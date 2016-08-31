package snip.androidapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Point;
import android.os.Bundle;

import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.sql.Date;
import java.util.Calendar;
import java.util.LinkedList;

/**
 * Created by ranreichman on 8/23/16.
 */
public abstract class SnipHoldingFragment extends GenericSnipFragment
{
    protected RecyclerView mRecyclerView;
    protected MyAdapter mAdapter;
    protected LinearLayoutManager mLayoutManager;
    protected SwipeRefreshLayout mSwipeContainer;
    // This is in Unix time
    protected long mLastFragmentUpdateTime = 0;
    View mRootView = null;

    final long ONE_HOUR_IN_MILLISECONDS = 1000 * 60 * 60;

    private long getLastFragmentUpdateTime()
    {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        long preferencesUpdateTime = sharedPreferences.getLong(getLastUpdateString(), 0);
        return Math.max(preferencesUpdateTime, mLastFragmentUpdateTime);
    }

    private String getLastUpdateString()
    {
        final String LAST_UPDATE_TIME_FOR_FRAGMENT_STRING = "LastUpdateTimeForFragment";
        return LAST_UPDATE_TIME_FOR_FRAGMENT_STRING + Integer.toString(getFragmentCode());
    }

    @Override
    public void onResume()
    {
        super.onResume();

        if (null != mAdapter)
        {
            ((MyAdapter) mAdapter).removeIdsFromDataset(
                    SnipReactionsSingleton.getInstance().getIdsToRemoveFromDataset());

            mAdapter.notifyDataSetChanged();
            if (getLastFragmentUpdateTime() < System.currentTimeMillis() - ONE_HOUR_IN_MILLISECONDS)
            {
                deleteFragmentInformationAndStartOver();
            }
        }
        BaseToolbar.updateToolbarAccordingToFragment(getActivity(), getFragmentCode());
    }

    @Override
    public void onStop()
    {
        if (null != mAdapter)
        {
            DataCacheManagement.saveAppInformationToFile(getActivity(), mAdapter.getDataset(), getFragmentCode());
        }
        super.onStop();
    }

    @Override
    public void onDestroy()
    {
        if (null != mAdapter)
        {
            DataCacheManagement.saveAppInformationToFile(getActivity(), mAdapter.getDataset(), getFragmentCode());
        }
        super.onDestroy();
    }

    @Override
    public void onDestroyView()
    {
        // this code is based on http://stackoverflow.com/questions/11353075/how-can-i-maintain-fragment-state-when-added-to-the-back-stack
        if (null != mRootView.getParent())
        {
            ((ViewGroup)mRootView.getParent()).removeView(mRootView);
        }
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        if (null == mRootView)
        {
            mRootView = inflater.inflate(R.layout.snip_holding_fragment, parent, false);
        }

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        if (null == SnipCollectionInformation.getInstance(getActivity()).getTokenForWebsiteAccess(getActivity()))
        {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            getActivity().startActivityForResult(intent, getResources().getInteger(R.integer.activityCodeLogin));
        }
        else
        {
            operateAfterLogin(savedInstanceState);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outBundle)
    {
        if (null != mAdapter)
        {
            DataCacheManagement.saveSnipDataToBundle(getActivity(), outBundle, mAdapter.getDataset());
        }
        super.onSaveInstanceState(outBundle);
    }

    protected abstract int getFragmentCode();
    protected abstract String getBaseSnipsQueryForFragment();

    protected void operateAfterLogin(Bundle savedInstanceState)
    {
        try
        {
            Log.d("operate after", "login");
            LinkedList<SnipData> cachedSnips = new LinkedList<>();

            if (null == mAdapter)
            {
                if (!SnipCollectionInformation.getInstance(getActivity()).getShouldRestartViewAfterCollection())
                {
                    cachedSnips =
                            DataCacheManagement.retrieveSavedDataFromBundleOrFile(
                                    getActivity(), savedInstanceState, getFragmentCode());
                }
            }
            else
            {
                cachedSnips = (LinkedList<SnipData>)mAdapter.getDataset().clone();
                mAdapter.getDataset().clear();
            }

            startFragmentOperation(cachedSnips);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    protected void startFragmentOperation(LinkedList<SnipData> snipsToStartWith)
    {
        if (getLastFragmentUpdateTime() < System.currentTimeMillis() - ONE_HOUR_IN_MILLISECONDS)
        {
            deleteFragmentInformation();
        }

        // Only initialize the layout on the first time
        if (null == mLayoutManager) {
            setFragmentVariables();
        }
        if (null != snipsToStartWith) {
            if (snipsToStartWith.size() > 0) {
                populateFragment(snipsToStartWith);
                return;
            }
        }

        Log.d("collecting data", "in startFragmentOperation");
        collectData();
    }

    protected void setFragmentVariables()
    {
        mRecyclerView = (RecyclerView) getView().findViewById(R.id.snip_recycler_view_fragment);
        mLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.addItemDecoration(new SimpleDividerItemDecoration(getActivity()));
        mSwipeContainer = (SwipeRefreshLayout)getView().findViewById(R.id.swipe_container_fragment);

        mSwipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh()
            {
                onRefreshOperation();
            }
        });
    }

    protected void populateFragment(LinkedList<SnipData> addedSnips)
    {
        if (null != addedSnips)
        {
            if ((null == mAdapter) ||
                    SnipCollectionInformation.getInstance(getActivity()).getShouldRestartViewAfterCollectionAndReset())
            {
                startNewAdapter(addedSnips);
            }
            else
            {
                ((MyAdapter)mAdapter).addAll(getActivity(), addedSnips, true);
                SnipTempManagement.getInstance(getActivity()).clearSnipsToLoadInFragment(getFragmentCode());
                asyncNotifyDatasetChanged();
            }

            ((MyAdapter)mAdapter).addAll(getActivity(),
                    SnipTempManagement.getInstance(getActivity()).mSnipsToLoadInFragment.get(getFragmentCode()),
                    false);

            addPicturesToSnips();
        }
    }

    private void startNewAdapter(LinkedList<SnipData> snipsToStartWith)
    {
        mAdapter = new MyAdapter(
                getActivity(), mRecyclerView, snipsToStartWith, mLayoutManager,
                getBaseSnipsQueryForFragment(), getFragmentCode());
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper.SimpleCallback swipeTouchHelperCallback =
                getSwipeTouchHelperCallback(getFragmentCode());
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(swipeTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(
                mLayoutManager, getBaseSnipsQueryForFragment(), getFragmentCode()) {});

        // This is because the SwipeListener hangs for no reason in Fragments
        mSwipeContainer.setRefreshing(false);
        mSwipeContainer.destroyDrawingCache();
        mSwipeContainer.clearAnimation();
    }

    private ItemTouchHelper.SimpleCallback getSwipeTouchHelperCallback(int fragmentCode)
    {
        int callbackSwipeParam = ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
        if (getResources().getInteger(R.integer.fragmentCodeLiked) == fragmentCode)
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
                    ReactionManager.userDislikedSnip(mAdapter.getDataset().get(currentPositionInDataset).mID);
                    mAdapter.remove(currentPositionInDataset);
                    mAdapter.notifyItemRemoved(currentPositionInDataset);
                    EndlessRecyclerOnScrollListener.onScrolledLogic(
                            mRecyclerView, mLayoutManager, getBaseSnipsQueryForFragment(), getFragmentCode(), false);
                }

                if (ItemTouchHelper.RIGHT == swipeDirection)
                {
                    ReactionManager.userLikedSnip(mAdapter.getDataset().get(currentPositionInDataset).mID);
                    SnipData snipToLike = mAdapter.getDataset().get(currentPositionInDataset);

                    mAdapter.remove(currentPositionInDataset);
                    mAdapter.notifyItemRemoved(currentPositionInDataset);

                    FragmentOperations.addSnipToOtherFragment(
                            (FragmentActivity)getActivity(),
                            snipToLike,
                            getActivity().getResources().getInteger(R.integer.fragmentCodeLiked));
                    EndlessRecyclerOnScrollListener.onScrolledLogic(
                            mRecyclerView, mLayoutManager, getBaseSnipsQueryForFragment(), getFragmentCode(), false);
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
                    Display display = getActivity().getWindowManager().getDefaultDisplay();
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

    public void asyncNotifyDatasetChanged()
    {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    public void onRefreshOperation()
    {
        SnipCollectionInformation.getInstance(getActivity()).setShouldRestartViewAfterCollection(true);
        deleteFragmentInformationAndStartOver();
        Log.d("Refreshed!", "So refreshing!");
    }

    private void deleteFragmentInformation()
    {
        SnipCollectionInformation.getInstance(getActivity()).cleanLastSnipQuery(getFragmentCode());
        DataCacheManagement.deleteFragmentInformationFiles(getActivity(), getFragmentCode());
        if (null != mAdapter)
        {
            mAdapter.getDataset().clear();
            mAdapter.notifyDataSetChanged();
        }
    }

    private void deleteFragmentInformationAndStartOver()
    {
        deleteFragmentInformation();
        startFragmentOperation(null);
    }

    protected void collectData()
    {
        mLastFragmentUpdateTime = System.currentTimeMillis();
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
        sharedPreferences.edit().putLong(getLastUpdateString(), mLastFragmentUpdateTime).apply();

        CollectSnipsFromInternet snipCollector = new CollectSnipsFromInternet(
                getActivity(),
                getBaseSnipsQueryForFragment() +
                        SnipCollectionInformation.getInstance(getActivity()).getDimensionsQuery(),
                getFragmentCode(),
                true);
        snipCollector.retrieveSnipsFromInternet(getActivity());
    }

    private void addPicturesToSnips()
    {
        if (null != mAdapter)
        {
            if (null != mAdapter.getDataset()) {
                for (int i = 0; i < mAdapter.getDataset().size(); i++) {
                    if (null == mAdapter.getDataset().get(i).mThumbnail) {
                        mAdapter.getDataset().get(i).mThumbnail =
                                SnipData.getBitmapFromUrl(mAdapter.getDataset().get(i).mThumbnailUrl);
                    }
                }
            }
        }
    }
}
