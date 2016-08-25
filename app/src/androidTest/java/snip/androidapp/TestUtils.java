package snip.androidapp;

import android.app.Activity;
import android.os.Parcelable;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.lifecycle.ActivityLifecycleMonitorRegistry;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.LinearLayoutManager;

import static android.support.test.runner.lifecycle.Stage.RESUMED;

import org.junit.Assert;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by ranreichman on 8/18/16.
 */
public class TestUtils
{
    public static void waitForAdapterInActivity(ActivityTestRule<MainActivity> activityRule)
    {
        int i = 0;
        int sleepTimeInMilliseconds = 300;
        int iterations = 50000 / sleepTimeInMilliseconds;
        while ((null == getFragmentAdapterFromActivity(activityRule.getActivity())) && (i < iterations))
        {
            safeSleep(sleepTimeInMilliseconds);
            i++;
        }
        if (i == iterations)
        {
            Assert.fail("Timeout exception");
        }
    }

    public static void waitForAdapterInActivityToChange(int originalSize, ActivityTestRule<MainActivity> activityRule)
    {
        int i = 0;
        int sleepTimeInMilliseconds = 300;
        int iterations = 50000 / sleepTimeInMilliseconds;
        while ((originalSize == getFragmentAdapterFromActivity(activityRule.getActivity()).getDataset().size()) &&
                (i < iterations))
        {
            safeSleep(sleepTimeInMilliseconds);
            i++;
        }
        if (i == iterations)
        {
            Assert.fail("Timeout exception");
        }
    }

    public static void safeSleep(int timeToSleepInMilliseconds)
    {
        try
        {
            Thread.sleep(timeToSleepInMilliseconds);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
    }

    public static int getDefaultAmountOfSnips()
    {
        return 10;
    }

    static FragmentActivity currentActivity;

    public static FragmentActivity getActivityInstance()
    {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()){
                    currentActivity = (FragmentActivity)resumedActivities.iterator().next();
                }
            }
        });

        return currentActivity;
    }

    public static Fragment getCurrentFragment()
    {
        return (GenericSnipFragment)
                FragmentOperations.getFragmentFromActivity(getActivityInstance(), R.id.fragmentPlaceholder);
    }

    public static LinkedList<SnipData> getCurrentDataset()
    {
        return ((SnipHoldingFragment)getCurrentFragment()).mAdapter.getDataset();
    }

    public static MyAdapter getCurrentAdapter()
    {
        return ((SnipHoldingFragment)getCurrentFragment()).mAdapter;
    }

    public static void swipeUpScreen()
    {
        Espresso.onView(ViewMatchers.withId(R.id.swipe_container_fragment)).perform(ViewActions.swipeUp());
    }

    public static void swipeDownScreen()
    {
        Espresso.onView(ViewMatchers.withId(R.id.swipe_container_fragment)).perform(ViewActions.swipeDown());
    }

    public static void enterSearchResultFragmentAndRefresh(int id)
    {
        Espresso.onView(ViewMatchers.withId(id)).perform(ViewActions.click());
        while (0 != getCurrentAdapter().mLinearLayoutManager.findFirstVisibleItemPosition())
        {
            TestUtils.swipeDownScreen();
        }

        TestUtils.swipeDownScreen();
        // TODO:: this is problematic when coming from the snoozed, but will change with fragments anyway
        TestUtils.waitForFragmentToAppear(SearchResultFragment.class);
    }

    public static void getToRefreshedItemList(ActivityTestRule<MainActivity> activityRule)
    {
        TestUtils.loginIfLoggedOut();
        TestUtils.waitForAdapterInActivity(activityRule);

        // Make sure there are more than 10 snips in list
        swipeUpScreen();
        swipeUpScreen();

        int amountOfSnips = getFragmentAdapterFromActivity(activityRule.getActivity()).getItemCount();

        // Refresh the list
        swipeDownScreen();
        swipeDownScreen();
        swipeDownScreen();
        swipeDownScreen();

        TestUtils.waitForAdapterInActivityToChange(amountOfSnips, activityRule);
    }

    public static int waitForActivityToAppear(Class fragmentClass)
    {
        int timeWaitingForOpen = 0;
        while (fragmentClass != TestUtils.getActivityInstance().getClass())
        {
            final int SLEEP_TIME_BETWEEN_CHECKS = 10;
            TestUtils.safeSleep(SLEEP_TIME_BETWEEN_CHECKS);
            timeWaitingForOpen += SLEEP_TIME_BETWEEN_CHECKS;
        }

        return timeWaitingForOpen;
    }

    public static int waitForFragmentToAppear(Class fragmentClass)
    {
        final int SLEEP_TIME_BETWEEN_CHECKS = 10;
        int timeWaitingForOpen = 0;

        while (null == TestUtils.getActivityInstance())
        {
            TestUtils.safeSleep(SLEEP_TIME_BETWEEN_CHECKS);
            timeWaitingForOpen += SLEEP_TIME_BETWEEN_CHECKS;
        }

        while (fragmentClass != TestUtils.getCurrentFragment().getClass())
        {
            TestUtils.safeSleep(SLEEP_TIME_BETWEEN_CHECKS);
            timeWaitingForOpen += SLEEP_TIME_BETWEEN_CHECKS;
        }

        return timeWaitingForOpen;
    }

    public static void swipeUpUntilThereAreNoNewSnips(ActivityTestRule<MainActivity> activityRule)
    {
        LinearLayoutManager linearLayoutManager = getCurrentAdapter().mLinearLayoutManager;
        int previousPosition = 0;
        int currentPosition = 0;
        do
        {
            previousPosition = currentPosition;
            TestUtils.swipeUpScreen();
            currentPosition = linearLayoutManager.findFirstVisibleItemPosition();
        }
        while (currentPosition != previousPosition);

        TestUtils.swipeUpScreen();

        // TODO:: fix this later
//        for (int i = 0; i < 30; i++)
//        {
//            TestUtils.swipeUpScreen();
//        }

        /*int likedSnipsDatasetSize = activityRule.getActivity().mAdapter.getItemCount();
        if (likedSnipsDatasetSize < 10)
        {
            return;
        }

        // Reach new snips and then talk
        for (int i = 0; i < likedSnipsDatasetSize / 2; i++)
        {
            TestUtils.swipeUpScreen();
        }

        likedSnipsDatasetSize = activityRule.getActivity().mAdapter.getItemCount();

        while (true)
        {
            // Swiping three times to make sure that i'm seeing new data
            TestUtils.swipeUpScreen();
            TestUtils.swipeUpScreen();
            TestUtils.swipeUpScreen();

            int currentActivityItemCount = activityRule.getActivity().mAdapter.getItemCount();
            if (currentActivityItemCount == likedSnipsDatasetSize)
            {
                break;
            }
            else
            {
                likedSnipsDatasetSize = currentActivityItemCount;
            }
        }*/
    }

    public static void swipeClickOrTapAccordingToId(int id, int position)
    {
        if (R.id.likeButtonOnToolbar == id)
        {
            Espresso.onView(ViewMatchers.withId(R.id.snip_recycler_view_fragment)).perform(
                    RecyclerViewActions.actionOnItemAtPosition(position, ViewActions.swipeRight()));
        }
        else if (R.id.snoozeButtonOnToolbar == id)
        {
            Espresso.onView(ViewMatchers.withId(R.id.snip_recycler_view_fragment)).perform(
                    RecyclerViewActions.actionOnItemAtPosition(position, ViewActions.doubleClick()));
        }
        else
        {
            Assert.fail();
        }
    }

    public static void clickButtonInMenu(int menuItemTextId)
    {
        Espresso.openActionBarOverflowOrOptionsMenu(InstrumentationRegistry.getInstrumentation().getTargetContext());
        Espresso.onView(ViewMatchers.withText(menuItemTextId)).perform(ViewActions.click());
    }

    public static MyAdapter getFragmentAdapterFromActivity(FragmentActivity activity)
    {
        SnipHoldingFragment fragment = (SnipHoldingFragment)
                FragmentOperations.getFragmentFromActivity(activity, R.id.fragmentPlaceholder);
        return fragment.mAdapter;
    }

    public static void loginIfLoggedOut()
    {
        // This is in case the current user isn't logged in
        if (LoginActivity.class == TestUtils.getActivityInstance().getClass())
        {
            loginUser();
        }
    }

    public static void loginUser()
    {
        Espresso.onView(ViewMatchers.withId(R.id.input_email_signin)).perform(
                ViewActions.typeText("ran.reichman@gmail.com"));
        Espresso.onView(ViewMatchers.withId(R.id.input_password_signin)).perform(
                ViewActions.typeText("Qwerty123"));
        Espresso.onView(ViewMatchers.withId(R.id.btn_login)).perform(ViewActions.click());
    }
}
