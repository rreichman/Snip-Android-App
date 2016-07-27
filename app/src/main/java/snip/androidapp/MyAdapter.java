package snip.androidapp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;

/**
 * Created by ranreichman on 7/19/16.
 */
public class MyAdapter extends RecyclerView.Adapter<ViewHolder>
{
    private Context mContext;
    private LinkedList<SnipData> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, LinkedList<SnipData> myDataset)
    {
        mContext = context;
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.snip_card_view, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.mLayout.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Log.d("In onClick", "1");
                CardView cardView = (CardView)v.findViewById(R.id.card_view);
                String headline = ((TextView)cardView.findViewById(R.id.headline)).getText().toString();

                Context context = v.getContext();
                Intent readsnipScreen = new Intent(context, ReadSnipActivity.class);
                final int currentPositionInDataset = viewHolder.getAdapterPosition();
                final long snipID = mDataset.get(currentPositionInDataset).mID;
                Bundle b = new Bundle();
                b.putLong("snipID", snipID);
                readsnipScreen.putExtras(b);
                context.startActivity(readsnipScreen);
            }
        });

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        SnipData currentSnip = mDataset.get(position);

        holder.mSnipTitle.setText(currentSnip.mHeadline);
        //holder.mSnipSource.setText(currentSnip.mSource);
        holder.mSnipAuthor.setText(currentSnip.mAuthor);
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
