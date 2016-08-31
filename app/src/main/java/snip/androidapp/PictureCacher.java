package snip.androidapp;

import android.graphics.Picture;
import android.os.AsyncTask;

/**
 * Created by ranreichman on 8/29/16.
 */
public class PictureCacher extends AsyncTask<Void, Void, Void>
{
    private OnTaskCompleted mListener;

    public PictureCacher(OnTaskCompleted listener)
    {
        mListener = listener;
    }

    @Override
    protected Void doInBackground(Void... voids)
    {
        ((ReadSnipFragment)mListener).mSnipDesigner.addPicturesToLayout();
        return null;
    }

    @Override
    protected void onPostExecute(Void o)
    {
        // your stuff
        mListener.onTaskCompleted();
    }
}
