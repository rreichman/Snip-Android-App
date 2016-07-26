package snip.androidapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by ranreichman on 7/20/16.
 */
// Provide a reference to the views for each data item
// Complex data items may need more than one view per item, and
// you provide access to all the views for a data item in a view holder
public class ViewHolder extends RecyclerView.ViewHolder
{
    protected RelativeLayout mLayout;
    protected TextView mSnipTitle;
    //public Image mSnipThumbnail;
    protected TextView mSnipSource;
    protected TextView mSnipAuthor;

    private DisplayMetrics getScreenDimensions(Context context)
    {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics dimension = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(dimension);
        return dimension;
    }

    public ViewHolder(View view)
    {
        super(view);
        this.mLayout = (RelativeLayout)view.findViewById(R.id.recLayout);
        this.mSnipTitle = (TextView)view.findViewById(R.id.headline);
        this.mSnipAuthor = (TextView)view.findViewById(R.id.author);
        this.mSnipSource = (TextView)view.findViewById(R.id.source);

        view.setClickable(true);
    }
}