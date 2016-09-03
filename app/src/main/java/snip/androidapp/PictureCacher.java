package snip.androidapp;

import android.graphics.Picture;
import android.os.AsyncTask;
import android.view.View;
import android.widget.LinearLayout;

/**
 * Created by ranreichman on 8/29/16.
 */
public class PictureCacher extends AsyncTask<Void, Void, Void>
{
    private OnTaskCompleted mListener;
    private Long mSnipID;

    public PictureCacher(OnTaskCompleted listener, Long snipID)
    {
        mListener = listener;
        mSnipID = snipID;
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        try {
            ((ScreenSlidePagerAdapter) mListener).mSnipDesigners.get(mSnipID).addPicturesToLayout();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void o)
    {
        mListener.onTaskCompleted(mSnipID);
    }
}
