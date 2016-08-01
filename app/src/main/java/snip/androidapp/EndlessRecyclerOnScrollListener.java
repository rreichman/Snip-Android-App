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

    public EndlessRecyclerOnScrollListener(LinearLayoutManager linearLayoutManager) {
        this.mLinearLayoutManager = linearLayoutManager;
    }

    // TODO:: do i need to remove duplicates here? If i ask for 12 snips and then more are added, do i receive a snip twice?
    private void loadMore(RecyclerView view)
    {
        Log.d("Loading More", "");
        if (!SnipCollectionInformation.getInstance().mLock.isLocked())
        {
            Log.d("Not locked", "");

            if (SnipCollectionInformation.getInstance().mSnipsCollectedByNonUIThread.size() > 0)
            {
                MyAdapter adapter = (MyAdapter) view.getAdapter();
                LinkedList<SnipData> collectedSnips =
                        SnipCollectionInformation.getInstance().getCollectedSnipsAndCleanList();
                adapter.add(collectedSnips);
                adapter.notifyDataSetChanged();
            }

            if (!SnipCollectionInformation.getInstance().mLastSnipQuery.equals("null"))
            {
                Log.d("collecting data", "");
                AsyncInternetAccessor accessor = new AsyncInternetAccessor();
                accessor.execute();
                Log.d("finished collecting", "data");
            }
        }
        else
        {
            Log.d("locked", "");
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
