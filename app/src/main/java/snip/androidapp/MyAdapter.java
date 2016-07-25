package snip.androidapp;

import android.content.Context;
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
    private LinkedList<SnipBox> mDataset;

    // Provide a suitable constructor (depends on the kind of dataset)
    public MyAdapter(Context context, LinkedList<SnipBox> myDataset)
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
                //Object ob = cardView.getLayoutParams();
                int height = cardView.getHeight();
                int newHeight = 300;
                if (height > 200)
                {
                    // TODO:: make this relative
                    newHeight = 126;
                }

                //RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)cardView.getLayoutParams();
                //cardView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, newHeight));
                //cardView.setCardBackgroundColor(R.color.snipCardColor);

                //mDataset.addLast(new SnipBox("a", "b", "c", new HashMap<String, String>()));
                int currentPositionInDataset = viewHolder.getAdapterPosition();
                mDataset.get(currentPositionInDataset).mSnipSource =
                        "New Source! " + Integer.toString(currentPositionInDataset + 1);
                //mDataset.get(getAdapterPosition())
                notifyDataSetChanged();
            }
        });

        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position)
    {
        SnipBox currentSnip = mDataset.get(position);

        holder.mSnipTitle.setText(currentSnip.mSnipTitle);
        holder.mSnipSource.setText(currentSnip.mSnipSource);
        holder.mSnipAuthor.setText(currentSnip.mSnipAuthor);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount()
    {
        return mDataset.size();
    }

    public void add(SnipBox snipBox)
    {
        mDataset.addLast(snipBox);
    }

    public void add(LinkedList<SnipBox> snipBoxList)
    {
        mDataset.addAll(snipBoxList);
    }
}
