package snip.androidapp;

import android.os.AsyncTask;
import android.support.annotation.IntegerRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;

import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ranreichman on 7/24/16.
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager mLinearLayoutManager;
    private String mBaseQuery;
    private int mActivityCode;

    public EndlessRecyclerOnScrollListener(
            LinearLayoutManager linearLayoutManager, String baseQuery, int activityCode) {
        mLinearLayoutManager = linearLayoutManager;
        mBaseQuery = baseQuery;
        mActivityCode = activityCode;
    }

    private static void loadMore(RecyclerView view, String baseQuery, int activityCode)
    {
        if (!SnipCollectionInformation.getInstance().mLock.isLocked())
        {
            SnipCollectionInformation.getInstance().mLock.lock();
            if (SnipCollectionInformation.getInstance().getAmountOfCollectedSnips() > 0)
            {
                MyAdapter adapter = (MyAdapter) view.getAdapter();
                LinkedList<SnipData> collectedSnips =
                        SnipCollectionInformation.getInstance().getCollectedSnipsAndCleanList();
                adapter.add(collectedSnips);
                adapter.notifyDataSetChanged();
            }

            if (!SnipCollectionInformation.getInstance().getLastSnipQueryForActivity(activityCode).equals("null"))
            {
                CollectSnipsFromInternet collectSnipsFromInternet =
                        new CollectSnipsFromInternet(
                                view.getContext(),
                                baseQuery,
                                activityCode);
                collectSnipsFromInternet.retrieveSnipsFromInternet(view.getContext());
            }
        }
    }

    public static void onScrolledLogic(
            RecyclerView recyclerView, LinearLayoutManager linearLayoutManager,
            String baseQuery, int activityCode)
    {
        try
        {
            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = linearLayoutManager.getItemCount();
            int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();
            final int visibleThreshold = 4;

            if (totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold) {
                loadMore(recyclerView, baseQuery, activityCode);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // Used this file as reference:
    // https://github.com/Harrison1/RecyclerViewActivity/blob/master/app/src/main/java/com/harrisonmcguire/recyclerview/EndlessRecyclerOnScrollListener.java
    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy)
    {
        super.onScrolled(recyclerView, dx, dy);
        onScrolledLogic(recyclerView, mLinearLayoutManager, mBaseQuery, mActivityCode);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState)
    {
        super.onScrollStateChanged(recyclerView, newState);
        Log.d("OnScrollStateChanged", "");
    }
}
