package snip.androidapp;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.HashMap;

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
        this.mSnipTitle = (TextView)view.findViewById(R.id.headline);
        this.mSnipAuthor = (TextView)view.findViewById(R.id.author);
        this.mSnipText = (TextView)view.findViewById(R.id.source);

        view.setClickable(true);

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

                //RecyclerView recyclerView = (RecyclerView)v.findViewById(R.id.snip_recycler_view);
                //MyAdapter adapter0 = (MyAdapter)cardView.getAdapter();
                //MyAdapter adapter = (MyAdapter)recyclerView.getAdapter();
                //adapter.add(new SnipBox("titleeee", "texttt", "authorrr", new HashMap<String,String>()));
                //adapter.notifyDataSetChanged();
                //CardView.LayoutParams layoutParams = (CardView.LayoutParams)cardView.getLayoutParams();
                //layoutParams.height = 300;
                //cardView.setMinimumHeight(300);


                /*AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(v.getContext());
                dlgAlert.setMessage("This is an alert with no consequence");
                dlgAlert.setTitle("App Title");
                dlgAlert.setPositiveButton("OK", null);
                dlgAlert.setCancelable(true);
                dlgAlert.create().show();*/

            }
        }
        );
    }
}