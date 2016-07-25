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
    // each data item is just a string in this case
    protected TextView mSnipTitle;
    //public Image mSnipThumbnail;
    protected TextView mSnipSource;
    protected TextView mSnipAuthor;
    //public TextView mSnipComments;
    //public HashMap<String,String> mExternalLinks;

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

        /*view.onScroll(new View.OnScrollChangeListener()
        {

        });

        view.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
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

                RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams)cardView.getLayoutParams();
                cardView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, newHeight));
                cardView.setCardBackgroundColor(R.color.snipCardColor);

            }
        }
        );*/
    }
}