package snip.androidapp;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.ShareCompat;
import android.util.Log;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created by ranreichman on 8/24/16.
 */
public class FragmentOperations
{
    public static Fragment getFragmentFromActivity(FragmentActivity activity, int id)
    {
        FragmentManager manager = activity.getSupportFragmentManager();
        Fragment fragment = manager.findFragmentById(R.id.fragmentPlaceholder);
        return fragment;
    }

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

    public static void addSnipToOtherFragment(
            FragmentActivity fragmentActivity, SnipData snipData, int targetFragmentCode)
    {
        // TODO:: this needs to not be null
        // TODO:: also, this needs to survive closing the app
        SnipHoldingFragment targetFragment = (SnipHoldingFragment)
                fragmentActivity.getSupportFragmentManager().findFragmentByTag(Integer.toString(targetFragmentCode));

        if (null != targetFragment)
        {
            targetFragment.mAdapter.getDataset().add(0, snipData);
            targetFragment.mAdapter.notifyDataSetChanged();
        }
        else
        {
            LinkedList<SnipData> currentListForFragment =
                    SnipTempManagement.getInstance(fragmentActivity).mSnipsToLoadInFragment.get(targetFragmentCode);
            currentListForFragment.add(0, snipData);
            SnipTempManagement.getInstance(fragmentActivity).mSnipsToLoadInFragment.put(targetFragmentCode, currentListForFragment);
        }
    }
}
