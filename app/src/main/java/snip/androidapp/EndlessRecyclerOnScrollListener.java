package snip.androidapp;

import android.os.AsyncTask;
import android.support.annotation.IntegerRes;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.util.LinkedList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by ranreichman on 7/24/16.
 */
public abstract class EndlessRecyclerOnScrollListener extends RecyclerView.OnScrollListener {
    private LinearLayoutManager mLinearLayoutManager;
    private String mBaseQuery;
    private int mFragmentCode;

    public EndlessRecyclerOnScrollListener(
            LinearLayoutManager linearLayoutManager, String baseQuery, int fragmentCode) {
        mLinearLayoutManager = linearLayoutManager;
        mBaseQuery = baseQuery;
        mFragmentCode = fragmentCode;
    }

    public static void loadMore(
            RecyclerView view, String baseQuery, int fragmentCode, boolean showAnimation)
    {
        if (!SnipCollectionInformation.getInstance(view.getContext()).
                getLastSnipQueryForFragment(fragmentCode).equals("null"))
        {
            CollectSnipsFromInternet collectSnipsFromInternet =
                    new CollectSnipsFromInternet(
                            view.getContext(),
                            baseQuery,
                            fragmentCode,
                            showAnimation);
            Log.d("loading more", "endless");
            collectSnipsFromInternet.retrieveSnipsFromInternet(view.getContext());
        }
        else
        {
            // TODO:: Add a card that says "no more snips here" (probably with some nice pic)
            //Toast.makeText(view.getContext(), "No more snips here...", Toast.LENGTH_SHORT).show();
            SnipCollectionInformation.getInstance(view.getContext()).mLocks.get(fragmentCode).unlock();
        }
    }

    public static int getVisibleThreshold()
    {
        return 4;
    }

    public static void onScrolledLogic(
            RecyclerView recyclerView, LinearLayoutManager linearLayoutManager,
            String baseQuery, int fragmentCode, boolean showAnimation)
    {
        try
        {
            int visibleItemCount = recyclerView.getChildCount();
            int totalItemCount = recyclerView.getAdapter().getItemCount();
            int firstVisibleItem = linearLayoutManager.findFirstVisibleItemPosition();

            if (totalItemCount - visibleItemCount <= firstVisibleItem + getVisibleThreshold()) {
                loadMore(recyclerView, baseQuery, fragmentCode, showAnimation);
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
        onScrolledLogic(recyclerView, mLinearLayoutManager, mBaseQuery, mFragmentCode, true);
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState)
    {
        super.onScrollStateChanged(recyclerView, newState);
        Log.d("OnScrollStateChanged", "");
    }
}
