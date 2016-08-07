package snip.androidapp;

import android.graphics.Bitmap;
import android.os.AsyncTask;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by ranihorev on 04/08/2016.
 */
public class ImageInternetAccessor extends AsyncTask<String,Void,Bitmap>
{
    protected Bitmap doInBackground(String... params)
    {
        return ImageLoader.getInstance().loadImageSync(params[0]);
    }
}
