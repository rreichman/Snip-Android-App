package snip.androidapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
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
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Set;

/**
 * Created by ranreichman on 7/19/16.
 */
public class MyAdapter extends RecyclerView.Adapter<MyViewHolder>
{
    private RecyclerView mRecyclerView;
    public LinkedList<SnipData> mDataset;
    private LinearLayoutManager mLinearLayoutManager;
    private String mDefaultQuery;
    public int mActivityCode;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(
            RecyclerView recyclerView, LinkedList<SnipData> dataset,
            LinearLayoutManager linearLayoutManager, String defaultQuery, int activityCode)
    {
        mRecyclerView = recyclerView;
        if (null != dataset)
        {
            mDataset = (LinkedList<SnipData>)dataset.clone();
        }
        else
        {
            mDataset = new LinkedList<SnipData>();
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

    private void validateThatAddedDataIsntMultiple(LinkedList<SnipData> snips)
    {
        for (int i = 0; i < mDataset.size(); i++)
        {
            for (int j = 0; j < snips.size(); j++)
            {
                if (mDataset.get(i).mID == snips.get(j).mID)
                {
                    Log.d("bug!!!!!!!!!!!!!", "two identical IDs in dataset");
                    Log.d("bug!!!!!!!!!!!!!", "two identical IDs in dataset");
                    Log.d("bug!!!!!!!!!!!!!", "two identical IDs in dataset");
                    Log.d("bug!!!!!!!!!!!!!", "two identical IDs in dataset");
                    Log.d("headline is", snips.get(j).mHeadline);
                    Log.d("bug!!!!!!!!!!!!!", "two identical IDs in dataset");
                    Log.d("bug!!!!!!!!!!!!!", "two identical IDs in dataset");
                    Log.d("bug!!!!!!!!!!!!!", "two identical IDs in dataset");
                    Log.d("bug!!!!!!!!!!!!!", "two identical IDs in dataset");
                }
            }
        }
    }

    public void addAll(LinkedList<SnipData> snips)
    {
        Log.d("Adapter size", Integer.toString(mDataset.size()));
        Log.d("adding snips to list", Integer.toString(snips.size()));
        validateThatAddedDataIsntMultiple(snips);
        mDataset.addAll(snips);
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
                        parent.getContext().getResources().getInteger(R.integer.activityCodeSnoozed);

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
                    Intent readsnipScreenIntent = new Intent(context, ReadSnipActivity.class);
                    readsnipScreenIntent.putExtra(SnipData.getSnipDataString(), (Serializable) snipData);
                    ((Activity)context).startActivityForResult(readsnipScreenIntent, context.getResources().getInteger(R.integer.activityCodeReadSnip));
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

    // Create new views (invoked by the layout manager)
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
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return mDataset.size();
    }

}
