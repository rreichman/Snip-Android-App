package snip.androidapp;

import android.os.AsyncTask;
import android.support.annotation.IntegerRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ranreichman on 7/24/16.
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager mLinearLayoutManager;

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    // TODO:: do i need to remove duplicates here? If i ask for 12 snips and then more are added, do i receive a snip twice?
    private void loadMore(RecyclerView view)
    {
        ReentrantLock lock = new ReentrantLock();
        Log.d("Loading More", "");
        if (!SnipCollectionInformation.getInstance().mIsCurrentlyLoading)
        {
            Log.d("Not Currently Loading", "Setting to true");
            SnipCollectionInformation.getInstance().setCurrentlyLoading(true);

            if ("null" != SnipCollectionInformation.getInstance().mLastSnipQuery)
            {
                Log.d("Snip query not null", SnipCollectionInformation.getInstance().mLastSnipQuery);
                MyAdapter adapter = (MyAdapter) view.getAdapter();
                adapter.add(SnipCollectionInformation.getInstance().mSnipsCollectedByNonUIThread);
                adapter.notifyDataSetChanged();
                Log.d("Just added list of size",
                        Integer.toString(SnipCollectionInformation.getInstance().mSnipsCollectedByNonUIThread.size()));
                // TODO:: think if this is a good solution
                //SnipCollectionInformation.getInstance().mFinishedCollectingSnips = false;
                Log.d("Set Finished Collecting", "flag to false");
            }
            else
            {
                Log.d("Finished collecting", "snips is false");
                if (!SnipCollectionInformation.getInstance().mNoMoreSnipsForNow)
                {
                    Log.d("Got snips for now", "");
                    AsyncInternetAccessor accessor = new AsyncInternetAccessor();
                    accessor.execute();
                }
                else
                {
                    Log.d("don't have snips for", "now");
                }
            }

            SnipCollectionInformation.getInstance().setCurrentlyLoading(false);
        }
    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy)
    {
        super.onScrolled(recyclerView, dx, dy);
        Log.d("OnScrolled", "");

        // Used this file as reference:
        // https://github.com/Harrison1/RecyclerViewActivity/blob/master/app/src/main/java/com/harrisonmcguire/recyclerview/EndlessRecyclerOnScrollListener.java

        int visibleItemCount = recyclerView.getChildCount();
        int totalItemCount = mLinearLayoutManager.getItemCount();
        int firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();
        final int visibleThreshold = 4;

        if (totalItemCount - visibleItemCount <= firstVisibleItem + visibleThreshold)
        {
            loadMore(recyclerView);
        }
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState)
    {
        super.onScrollStateChanged(recyclerView, newState);
        Log.d("OnScrollStateChanged", "");
    }
}
