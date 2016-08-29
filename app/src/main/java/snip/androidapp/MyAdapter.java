package snip.androidapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

/**
 * Created by ranreichman on 7/19/16.
 */
public class MyAdapter extends RecyclerView.Adapter<MyViewHolder>
{
    private RecyclerView mRecyclerView;
    private LinkedList<SnipData> mDataset;
    public LinearLayoutManager mLinearLayoutManager;
    private String mDefaultQuery;
    public int mActivityCode;

    public MyAdapter(
            Context context, RecyclerView recyclerView, LinkedList<SnipData> dataset,
            LinearLayoutManager linearLayoutManager, String defaultQuery, int activityCode)
    {
        mRecyclerView = recyclerView;
        mDataset = new LinkedList<SnipData>();
        if (null != dataset)
        {
            addAll(context, dataset);
        }
        mLinearLayoutManager = linearLayoutManager;
        mDefaultQuery = defaultQuery;
        mActivityCode = activityCode;
    }

    public void remove(int id)
    {
        Log.d("Adapter size", Integer.toString(mDataset.size()));
        Log.d("removing snip from list", Integer.toString(id));
        mDataset.remove(id);
    }

    public LinkedList<SnipData> getDataset()
    {
        return mDataset;
    }

    private void addUniqueSnipsToDataset(LinkedList<SnipData> snips)
    {
        for (int i = 0; i < snips.size(); i++)
        {
            boolean notMultiple = true;
            for (int j = 0; j < mDataset.size(); j++)
            {
                if (mDataset.get(j).mID == snips.get(i).mID)
                {
                    Log.d("bug!!!!!!!!!!!!!", "two identical IDs in dataset");
                    Log.d("bug!!!!!!!!!!!!!", "two identical IDs in dataset");
                    Log.d("bug!!!!!!!!!!!!!", "two identical IDs in dataset");
                    Log.d("bug!!!!!!!!!!!!!", "two identical IDs in dataset");
                    Log.d("headline is", snips.get(i).mHeadline);
                    Log.d("bug!!!!!!!!!!!!!", "two identical IDs in dataset");
                    Log.d("bug!!!!!!!!!!!!!", "two identical IDs in dataset");
                    Log.d("bug!!!!!!!!!!!!!", "two identical IDs in dataset");
                    Log.d("bug!!!!!!!!!!!!!", "two identical IDs in dataset");
                    notMultiple = false;
                    break;
                }
            }
            if (notMultiple)
            {
                mDataset.add(snips.get(i));
            }
        }
    }

    public void addAll(Context context, LinkedList<SnipData> snips)
    {
        Log.d("Adapter size", Integer.toString(mDataset.size()));
        Log.d("adding snips to list", Integer.toString(snips.size()));
        addUniqueSnipsToDataset(snips);
        Log.d("Adapter size", Integer.toString(mDataset.size()));
    }

    public void removeIdsFromDataset(Set<Long> ids)
    {
        Log.d("Adapter size", Integer.toString(mDataset.size()));
        Log.d("removing snips to list", Integer.toString(ids.size()));
        LinkedList<SnipData> newDataset = new LinkedList<SnipData>();
        ListIterator<SnipData> iter = (ListIterator)mDataset.iterator();
        while (iter.hasNext())
        {
            SnipData currentSnip = iter.next();
            if (!ids.contains(currentSnip.mID))
            {
                newDataset.add(currentSnip);
            }
        }
        mDataset = newDataset;
        Log.d("New adapter size", Integer.toString(mDataset.size()));
    }

    private GestureDetector getGestureDetector(final ViewGroup parent, final View view, final MyViewHolder viewHolder)
    {
        return new GestureDetector(parent.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e)
            {
                final int SNOOZE_SCREEN_CODE =
                        parent.getContext().getResources().getInteger(R.integer.fragmentCodeSnoozed);

                if (SNOOZE_SCREEN_CODE != mActivityCode)
                {
                    try
                    {
                        final int currentPositionInDataset = viewHolder.getAdapterPosition();
                        ReactionManager.userSnoozedSnip(mDataset.get(currentPositionInDataset).mID);

                        final MyViewHolder cardHolder = (MyViewHolder) mRecyclerView.findViewHolderForAdapterPosition(currentPositionInDataset);
                        Animation snoozeAnimation =
                                AnimationUtils.loadAnimation(parent.getContext(), R.anim.pulse_fade_in);

                        final int DURATION_OF_ANIMATION_IN_MS = 400;
                        snoozeAnimation.setDuration(DURATION_OF_ANIMATION_IN_MS);
                        snoozeAnimation.setAnimationListener(new Animation.AnimationListener() {
                            @Override
                            public void onAnimationStart(Animation animation) {
                                cardHolder.mHeartImage.bringToFront();
                                cardHolder.mHeartImage.setVisibility(View.VISIBLE);
                            }

                            @Override
                            public void onAnimationEnd(Animation animation) {
                                cardHolder.mHeartImage.setVisibility(View.GONE);
                                mDataset.remove(currentPositionInDataset);
                                mRecyclerView.getAdapter().notifyItemRemoved(currentPositionInDataset);
                                EndlessRecyclerOnScrollListener.onScrolledLogic(
                                        mRecyclerView, mLinearLayoutManager, mDefaultQuery, mActivityCode, false);
                            }

                            @Override
                            public void onAnimationRepeat(Animation animation) {}
                        });
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                cardHolder.mHeartImage.setVisibility(View.GONE);
                            }
                        }, snoozeAnimation.getDuration());
                        cardHolder.mHeartImage.startAnimation(snoozeAnimation);
                    }
                    catch (IndexOutOfBoundsException e1)
                    {
                        e1.printStackTrace();
                    }
                    catch (NullPointerException e2)
                    {
                        e2.printStackTrace();
                    }
                }

                Log.d("TEST", "onDoubleTap");
                return super.onDoubleTap(e);
            }

            @Override
            public void onLongPress(MotionEvent e) { super.onLongPress(e); }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e)
            {
                final int currentPositionInDataset = viewHolder.getAdapterPosition();
                try
                {
                    SnipData snipData = mDataset.get(currentPositionInDataset);
                    Context context = view.getContext();
                    Bundle bundledSnipData = new Bundle();
                    bundledSnipData.putSerializable("snipData", snipData);

                    FragmentOperations.openFragment(
                            (AppCompatActivity)context,
                            context.getResources().getInteger(R.integer.fragmentCodeReadSnip),
                            Long.toString(snipData.mID),
                            bundledSnipData);
                }
                catch (IndexOutOfBoundsException e1)
                {
                    e1.printStackTrace();
                }
                catch (NullPointerException e2)
                {
                    e2.printStackTrace();
                }

                return super.onSingleTapConfirmed(e);
            }
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(final ViewGroup parent, int viewType)
    {
        // create a new view
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snip_card_view, parent, false);
        final MyViewHolder viewHolder = new MyViewHolder(view);

        view.setOnTouchListener(new View.OnTouchListener() {
            private GestureDetector gestureDetector = getGestureDetector(parent, view, viewHolder);

            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                return gestureDetector.onTouchEvent(event);
            }});

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position)
    {
        SnipData currentSnip = mDataset.get(position);
        holder.mSnipHeadline.setText(currentSnip.mHeadline);
        String authorPublisher = currentSnip.mPublisher;
        if (!currentSnip.mAuthor.isEmpty()) {
            authorPublisher += ", " + currentSnip.mAuthor;
        }
        holder.mSnipPublisher.setText(authorPublisher);

        if (null == currentSnip.mThumbnail)
        {
            holder.mSnipThumbnail.setImageBitmap(currentSnip.mThumbnail);
        }
        else
        {
            holder.mSnipThumbnail.setImageBitmap(SnipData.getBitmapFromUrl(currentSnip.mThumbnailUrl));
        }

        holder.mSnipPublishAge.setText(DatetimeUtils.getDateDiff(currentSnip.mDate, new Date()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return mDataset.size();
    }

}
