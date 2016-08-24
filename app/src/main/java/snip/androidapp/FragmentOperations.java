package snip.androidapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.util.Log;

/**
 * Created by ranreichman on 8/24/16.
 */
public class FragmentOperations
{
    public static void openFragment(final FragmentActivity currentActivity, int fragmentCode)
    {
        Bundle b = new Bundle();
        b.putInt("param", fragmentCode);

        openFragment(currentActivity, fragmentCode, Integer.toString(fragmentCode), b);
    }

    public static void openFragment(final FragmentActivity currentActivity,
                                    int fragmentCode,
                                    String fragmentTag,
                                    Bundle passedData)
    {
        Log.d("opened new", "fragment");
        FragmentManager fragmentManager = currentActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);

        if (null == fragment)
        {
            if (fragmentCode == currentActivity.getResources().getInteger(R.integer.fragmentCodeReadSnip))
            {
                fragment = new ReadSnipFragment();
            }
            else
            {
                fragment = new SearchResultFragment();
            }

            fragment.setArguments(passedData);
        }

        fragmentTransaction.replace(R.id.fragmentPlaceholder, fragment, fragmentTag);
        fragmentTransaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
        BaseToolbar.updateToolbarAccordingToFragment(currentActivity, fragmentCode);
        Log.d("finished new","fragment");
    }
}
