package snip.androidapp;

import android.app.Activity;

import android.content.Intent;
import android.graphics.Picture;
import android.os.Bundle;

import android.util.Log;
import android.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.Date;
import java.util.LinkedList;
import java.util.concurrent.ExecutionException;

// TODO:: In the CardView layout - To create a card with a shadow, use the card_view:cardElevation attribute
// TODO:: add Fabric to app

/**
 * Created by ranreichman on 7/19/16.
 */
public class MyActivity extends Activity
{
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter<ViewHolder> mAdapter;
    private LinearLayoutManager mLayoutManager;
    // TODO:: what do i do with this when i load more snips and they aren't here? populate the list?
    // TODO:: Currently doesn't seem to be a bug. think why not! is it auto-populated?
    private LinkedList<SnipData> mCollectedSnips;

    // Referenced this: http://stackoverflow.com/questions/26422948/how-to-do-swipe-to-delete-cardview-in-android-using-support-library
    private SwipeableRecyclerViewTouchListener getDefaultTouchListener()
    {
        return new SwipeableRecyclerViewTouchListener(mRecyclerView,
                new SwipeableRecyclerViewTouchListener.SwipeListener() {
                    @Override
                    public boolean canSwipeRight(int position)
                    {
                        return true;
                    }

                    @Override
                    public boolean canSwipeLeft(int position)
                    {
                        return true;
                    }

                    @Override
                    public void onDismissedBySwipeLeft(RecyclerView recyclerView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions)
                        {
                            // TODO:: mark as unlike
                            mCollectedSnips.remove(position);
                            mAdapter.notifyItemRemoved(position);
                        }
                        mAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onDismissedBySwipeRight(RecyclerView recyclerView, int[] reverseSortedPositions) {
                        for (int position : reverseSortedPositions)
                        {
                            // TODO:: mark as snooze
                            mCollectedSnips.remove(position);
                            mAdapter.notifyItemRemoved(position);
                        }
                        mAdapter.notifyDataSetChanged();
                    }
                });
    }

    public static LinkedList<SnipData> createRandomSnipDataset(int size, int numberToStartCountingAt)
    {
        LinkedList<SnipData> myDataset = new LinkedList<SnipData>();
        for (int i = 0; i < size; ++i)
        {
            int printedNumber = i + numberToStartCountingAt;
            String snipHeadline = "Headline" + printedNumber;
            String snipPublisher = "Snip" + printedNumber;
            String snipAuthor = "Author" + printedNumber;
            String snipSource = "Ynet" + printedNumber;

            String snipBody = "Body" + printedNumber;
            String snipWebsite = "www.ynet" + printedNumber + ".co.il";
            LinkedList<Pair<String,String>> links = new LinkedList<Pair<String,String>>();
            links.addLast(new Pair<String, String>(snipSource, snipWebsite));
            Picture fakePicture = new Picture();
            Date fakeDate = new Date();
            // TODO:: obviously important to change this later to real snip ID.
            long snipID = i + 1;
            SnipData currentSnipInformation =
                    new SnipData(snipHeadline, snipPublisher, snipAuthor, snipID, fakeDate, fakePicture,
                            snipBody,links, new SnipComments());

            myDataset.addLast(currentSnipInformation);
        }

        return myDataset;
    }

    private LinkedList<SnipData> getListOfSnips()
    {
        return createRandomSnipDataset(15, 1);
    }

    private void startActivity()
    {
        setContentView(R.layout.my_activity);

        mRecyclerView = (RecyclerView)findViewById(R.id.snip_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);

        try
        {
            AsyncInternetAccessor accessor = new AsyncInternetAccessor();
            accessor.execute().get();
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        catch (ExecutionException e)
        {
            e.printStackTrace();
        }


        mCollectedSnips = SnipCollectionInformation.getInstance().getCollectedSnipsAndCleanList();
        // specify an adapter
        mAdapter = new MyAdapter(MyActivity.this, mRecyclerView, mCollectedSnips);
        mRecyclerView.setAdapter(mAdapter);

        SwipeableRecyclerViewTouchListener swipeTouchListener = getDefaultTouchListener();

        mRecyclerView.addOnItemTouchListener(swipeTouchListener);

        mRecyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(mLayoutManager) {});
    }

    @Override
    public void onStop()
    {
        super.onStop();
    }

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
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        final Intent intent = getIntent();
        if (SnipCollectionInformation.getInstance().mLastSnipQuery.equals(""))
        {
            startActivity();
        }
    }
}