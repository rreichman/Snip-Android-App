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
        Bitmap result = null;
        final int ATTEMPTS_TO_SUCCEED = 3;
        for (int i = 0; i < ATTEMPTS_TO_SUCCEED; ++i)
        {
            try
            {
                result = ImageLoader.getInstance().loadImageSync(params[0]);
                break;
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }

        return result;
    }
}
