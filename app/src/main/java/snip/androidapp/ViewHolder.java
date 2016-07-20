package snip.androidapp;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

/**
 * Created by ranreichman on 7/20/16.
 */
// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public class ViewHolder extends RecyclerView.ViewHolder
{
    // each data item is just a string in this case
    protected TextView mSnipTitle;
    //public Image mSnipThumbnail;
    protected TextView mSnipText;
    protected TextView mSnipAuthor;
    //public TextView mSnipComments;
    //public HashMap<String,String> mExternalLinks;

    public ViewHolder(View view)
    {
        super(view);

        this.mSnipTitle = (TextView)view.findViewById(R.id.headline);
        this.mSnipAuthor = (TextView)view.findViewById(R.id.author);
        this.mSnipText = (TextView)view.findViewById(R.id.source);

        view.setClickable(true);
    }
}