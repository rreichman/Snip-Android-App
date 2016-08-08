package snip.androidapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.StringTokenizer;

/**
 * Created by ranreichman on 7/19/16.
 */
public class MyAdapter extends RecyclerView.Adapter<ViewHolder>
{
    private RecyclerView mRecyclerView;
    private LinkedList<SnipData> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(RecyclerView recyclerView, LinkedList<SnipData> myDataset)
    {
        mRecyclerView = recyclerView;
        mDataset = myDataset;
    }

    private GestureDetector getGestureDetector(final ViewGroup parent, final View view, final ViewHolder viewHolder)
    {
        return new GestureDetector(parent.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTap(MotionEvent e) {
                final int currentPositionInDataset = viewHolder.getAdapterPosition();
                ReactionManager.userLikedSnip(view.getContext(), mDataset.get(currentPositionInDataset).mID);

                mDataset.remove(currentPositionInDataset);
                mRecyclerView.getAdapter().notifyItemRemoved(currentPositionInDataset);
                Log.d("TEST", "onDoubleTap");
                return super.onDoubleTap(e);
            }

            @Override
            public void onLongPress(MotionEvent e) { super.onLongPress(e); }

            @Override
            public boolean onSingleTapConfirmed(MotionEvent e)
            {
                final int currentPositionInDataset = viewHolder.getAdapterPosition();
                SnipData snipData = mDataset.get(currentPositionInDataset);

                Context context = view.getContext();
                Intent readsnipScreenIntent = new Intent(context, ReadSnipActivity.class);
                readsnipScreenIntent.putExtra(SnipData.getSnipDataString(), (Serializable) snipData);

                context.startActivity(readsnipScreenIntent);
                return super.onSingleTapConfirmed(e);
            }
        });
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType)
    {
        // create a new view
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snip_card_view, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

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
    public void onBindViewHolder(ViewHolder holder, int position)
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

    public void add(LinkedList<SnipData> SnipDataList)
    {
        mDataset.addAll(SnipDataList);
    }
}
