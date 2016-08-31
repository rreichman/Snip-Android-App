package snip.androidapp;

import android.app.Activity;
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

    public static void openFragment(
            final FragmentActivity currentActivity, int fragmentCodeToOpen)
    {
        Bundle b = new Bundle();
        b.putInt("param", fragmentCodeToOpen);

        openFragment(currentActivity, -1, fragmentCodeToOpen, Integer.toString(fragmentCodeToOpen), b);
    }

    public static void openFragment(final FragmentActivity currentActivity,
                                    int fragmentCodeOfCaller,
                                    int fragmentCodeToOpen,
                                    String fragmentTag,
                                    Bundle passedData)
    {
        Log.d("opened new", "fragment");
        FragmentManager fragmentManager = currentActivity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment fragment = fragmentManager.findFragmentByTag(fragmentTag);

        if (null == fragment)
        {
            if (fragmentCodeToOpen == currentActivity.getResources().getInteger(R.integer.fragmentCodeReadSnip))
            {
                fragment = new ReadSnipFragment();
                passedData.putInt("fragmentCodeOfCaller", fragmentCodeOfCaller);
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
        fragmentManager.executePendingTransactions();
        BaseToolbar.updateToolbarAccordingToFragment(currentActivity, fragmentCodeToOpen);
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

    private static int getCurrentPositionOfSnipInAdapter(FragmentActivity activity, long snipId, int originalFragmentCode)
    {
        final int POSITION_NOT_FOUND = -1;
        int position = POSITION_NOT_FOUND;

        try
        {
            SnipHoldingFragment fragment = (SnipHoldingFragment)
                    activity.getSupportFragmentManager().findFragmentByTag(Integer.toString(originalFragmentCode));
            if (null == fragment)
            {
                return position;
            }

            for (int i = 0; i < fragment.mAdapter.getDataset().size(); i++)
            {
                if (snipId == fragment.mAdapter.getDataset().get(i).mID)
                {
                    position = i;
                    break;
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return position;
    }

    public static String getReadSnipFragmentTag(int originalFragmentCode, long snipId)
    {
        return Integer.toString(originalFragmentCode) + "-" + Long.toString(snipId);
    }

    private static int getPositionOfNextSnip(
            int adapterSize, boolean swipedRight, int currentPositionOfSnip)
    {
        if (swipedRight)
        {
            if (currentPositionOfSnip == adapterSize - 1)
            {
                return 0;
            }
            else
            {
                return currentPositionOfSnip + 1;
            }
        }
        else
        {
            if (currentPositionOfSnip == 0)
            {
                return adapterSize - 1;
            }
            else
            {
                return currentPositionOfSnip - 1;
            }
        }
    }

    private static SnipData getDataOfNextSnip(
            FragmentActivity activity, int fragmentCodeOfCaller, boolean swipedRight, int currentPositionOfSnip)
    {
        SnipHoldingFragment fragment = (SnipHoldingFragment)
                activity.getSupportFragmentManager().findFragmentByTag(Integer.toString(fragmentCodeOfCaller));
        if (null != fragment)
        {
            int adapterSize = fragment.mAdapter.getItemCount();
            int nextPosition = getPositionOfNextSnip(adapterSize, swipedRight, currentPositionOfSnip);

            if (adapterSize - currentPositionOfSnip < EndlessRecyclerOnScrollListener.getVisibleThreshold())
            {
                EndlessRecyclerOnScrollListener.loadMore(
                        fragment.mRecyclerView,
                        fragment.getBaseSnipsQueryForFragment(),
                        fragment.getFragmentCode(),
                        false);
            }

            return fragment.mAdapter.getDataset().get(nextPosition);
        }
        else
        {
            return null;
        }
    }

    public static void openOtherReadSnipFragment(
            FragmentActivity activity, boolean swipedRight, long snipId, int fragmentCodeOfCaller)
    {
        try
        {
            int currentPositionOfSnip = getCurrentPositionOfSnipInAdapter(activity, snipId, fragmentCodeOfCaller);
            if (-1 == currentPositionOfSnip)
            {
                return;
            }

            SnipData snipData = getDataOfNextSnip(activity, fragmentCodeOfCaller, swipedRight, currentPositionOfSnip);
            Bundle b = new Bundle();
            b.putSerializable("snipData", snipData);
            openFragment(
                    activity,
                    fragmentCodeOfCaller,
                    activity.getResources().getInteger(R.integer.fragmentCodeReadSnip),
                    getReadSnipFragmentTag(fragmentCodeOfCaller, snipData.mID),
                    b);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            LogUserActions.logAppError(activity, "OpeningOtherReadSnip");
        }
    }
}
