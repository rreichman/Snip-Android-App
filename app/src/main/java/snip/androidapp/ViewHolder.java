package snip.androidapp;

import android.content.Context;
import android.graphics.Picture;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
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
    public RelativeLayout mForeground;
    public RelativeLayout mSwipeBackgroundRight;
    public RelativeLayout mSwipeBackgroundLeft;
    public SnipTextView mSnipHeadline;
    public ImageView mSnipThumbnail;
    public SnipTextView mSnipPublisher;

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
        this.mForeground = (RelativeLayout)view.findViewById(R.id.clipForeground);
        this.mSwipeBackgroundLeft = (RelativeLayout)view.findViewById(R.id.clipSwipeBackgroundLeft);
        this.mSwipeBackgroundRight = (RelativeLayout)view.findViewById(R.id.clipSwipeBackgroundRight);
        this.mSnipHeadline = (SnipTextView) view.findViewById(R.id.headline);
        this.mSnipPublisher = (SnipTextView) view.findViewById(R.id.publisher);
        this.mSnipThumbnail = (ImageView)view.findViewById(R.id.thumbnail);

        view.setClickable(true);
    }
}