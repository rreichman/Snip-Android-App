package snip.androidapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPropertyAnimatorListener;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.LinkedList;

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
                ReactionManager.userLikedSnip(mDataset.get(currentPositionInDataset).mID);

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
                CardView cardView = (CardView)view.findViewById(R.id.card_view);
                String headline = ((TextView)cardView.findViewById(R.id.headline)).getText().toString();

                Context context = view.getContext();
                Intent readsnipScreen = new Intent(context, ReadSnipActivity.class);
                final int currentPositionInDataset = viewHolder.getAdapterPosition();
                final long snipID = mDataset.get(currentPositionInDataset).mID;
                Bundle b = new Bundle();
                b.putLong("snipID", snipID);
                readsnipScreen.putExtras(b);
                context.startActivity(readsnipScreen);
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
        holder.mSnipAuthor.setText(currentSnip.mAuthor);
        holder.mSnipPublisher.setText(currentSnip.mPublisher);
        holder.mSnipThumbnail.setImageBitmap(currentSnip.mThumbnail);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return mDataset.size();
    }

    public void add(SnipData SnipData)
    {
        mDataset.addLast(SnipData);
    }

    public void add(LinkedList<SnipData> SnipDataList)
    {
        mDataset.addAll(SnipDataList);
    }
}
