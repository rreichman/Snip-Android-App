package snip.androidapp;

import android.content.Context;

import java.util.HashMap;
import java.util.LinkedList;

/**
 * Created by ranreichman on 8/29/16.
 */
public class SnipTempManagement
{
    private static SnipTempManagement mInstance = null;
    public HashMap<Integer, LinkedList<SnipData>> mSnipsToLoadInFragment;

    public SnipTempManagement(Context context)
    {
        mSnipsToLoadInFragment = new HashMap<>();
        mSnipsToLoadInFragment.put(context.getResources().getInteger(R.integer.fragmentCodeMain), new LinkedList<SnipData>());
        mSnipsToLoadInFragment.put(context.getResources().getInteger(R.integer.fragmentCodeLiked), new LinkedList<SnipData>());
        mSnipsToLoadInFragment.put(context.getResources().getInteger(R.integer.fragmentCodeSnoozed), new LinkedList<SnipData>());
    }

    public void clearSnipsToLoadInFragment(int fragmentCode)
    {
        mSnipsToLoadInFragment.get(fragmentCode).clear();
    }

    public static synchronized SnipTempManagement getInstance(Context context)
    {
        if (null == mInstance)
        {
            mInstance = new SnipTempManagement(context);
        }
        return mInstance;
    }
}
