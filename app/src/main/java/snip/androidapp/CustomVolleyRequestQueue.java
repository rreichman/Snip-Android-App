package snip.androidapp;

import android.content.Context;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;

/**
 * Custom implementation of Volley Request Queue
 */
public class CustomVolleyRequestQueue {

    private static CustomVolleyRequestQueue mInstance;
    private static Context mCtx;
    private RequestQueue mRequestQueue;

    private CustomVolleyRequestQueue(Context context) {
        mCtx = context;
        mRequestQueue = getRequestQueue();
    }


    private static void init(Context context){
        mInstance = new CustomVolleyRequestQueue(context);
    }

    public static synchronized CustomVolleyRequestQueue getInstance() {
        if (mInstance == null) {
            try {
                throw new Exception("RequestQueue not initialized");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return mInstance;
    }

    public static synchronized  CustomVolleyRequestQueue getInstance(Context context)
    {
        if (mInstance == null) {
            init(context);
        }
        return mInstance;
    }

    public Context getContext()
    {
        return mCtx;
    }

    public RequestQueue getRequestQueue() {
        if (mRequestQueue == null) {
            Cache cache = new DiskBasedCache(mCtx.getCacheDir(), 10 * 1024 * 1024);
            Network network = new BasicNetwork(new HurlStack());
            mRequestQueue = new RequestQueue(cache, network);
            // Don't forget to start the volley request queue
            mRequestQueue.start();
        }
        return mRequestQueue;
    }
}