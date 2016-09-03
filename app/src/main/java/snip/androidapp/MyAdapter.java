package snip.androidapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;

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
    public int mFragmentCode;

    private int mCount = 0;

    public MyAdapter(
            Context context, RecyclerView recyclerView, LinkedList<SnipData> dataset,
            LinearLayoutManager linearLayoutManager, String defaultQuery, int fragmentCode)
    {
        mRecyclerView = recyclerView;
        mDataset = new LinkedList<SnipData>();
        if (null != dataset)
        {
            addAll(context, dataset, true);
        }
        mLinearLayoutManager = linearLayoutManager;
        mDefaultQuery = defaultQuery;
        mFragmentCode = fragmentCode;
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

    public void addAll(Context context, LinkedList<SnipData> snips, boolean atEnd)
    {
        Log.d("Adapter size", Integer.toString(mDataset.size()));
        Log.d("adding snips to list", Integer.toString(snips.size()));

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
                    LogUserActions.logAppError(context, "Identical IDs in dataset");
                    notMultiple = false;
                    break;
                }
            }
            if (notMultiple)
            {
                if (atEnd)
                {
                    mDataset.add(snips.get(i));
                }
                else
                {
                    mDataset.add(0, snips.get(i));
                }
            }
        }

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

                if (SNOOZE_SCREEN_CODE != mFragmentCode)
                {
                    try
                    {
                        final int currentPositionInDataset = viewHolder.getAdapterPosition();
                        ReactionManager.userSnoozedSnip(mDataset.get(currentPositionInDataset).mID);

                        final MyViewHolder cardHolder = (MyViewHolder) mRecyclerView.findViewHolderForAdapterPosition(currentPositionInDataset);
                        Animation snoozeAnimation =
                                AnimationUtils.loadAnimation(parent.getContext(), R.anim.pulse_fade_in);

                        final int DURATION_OF_ANIMATION_IN_MS = 300;
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
                                SnipData snipToSnooze = mDataset.get(currentPositionInDataset);

                                mDataset.remove(currentPositionInDataset);
                                mRecyclerView.getAdapter().notifyItemRemoved(currentPositionInDataset);
                                FragmentOperations.addSnipToOtherFragment(
                                        (FragmentActivity)parent.getContext(),
                                        snipToSnooze,
                                        parent.getContext().getResources().getInteger(R.integer.fragmentCodeSnoozed));
                                EndlessRecyclerOnScrollListener.onScrolledLogic(
                                        mRecyclerView, mLinearLayoutManager, mDefaultQuery, mFragmentCode, false);
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
                Context context = view.getContext();

                FragmentOperations.openFragmentInPosition(
                        (FragmentActivity)context,
                        context.getResources().getInteger(R.integer.fragmentCodePager),
                        currentPositionInDataset);

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
        String authorPublisher = "";

        if (!currentSnip.mPublisher.equals("סניפ"))
        {
            authorPublisher += currentSnip.mPublisher;
        }
        if (!currentSnip.mAuthor.isEmpty())
        {
            authorPublisher += currentSnip.mAuthor;
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
