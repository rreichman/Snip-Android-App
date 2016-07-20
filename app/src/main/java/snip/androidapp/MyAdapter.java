package snip.androidapp;

import android.media.Image;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by ranreichman on 7/19/16.
 */
public class MyAdapter extends RecyclerView.Adapter<ViewHolder>
{
    private LinkedList<SnipBox> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(LinkedList<SnipBox> myDataset)
    {
        mDataset = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.snip_card_view, parent, false);
        ViewHolder vh = new ViewHolder(v);

        //v.findViewById()
        // set the view's size, margins, paddings and layout parameters
        // ... TODO:: fill this

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        SnipBox currentSnip = mDataset.get(position);

        holder.mSnipTitle.setText(currentSnip.mSnipTitle);
        holder.mSnipText.setText(currentSnip.mSnipText);
        holder.mSnipAuthor.setText(currentSnip.mSnipAuthor);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return mDataset.size();
    }
}
