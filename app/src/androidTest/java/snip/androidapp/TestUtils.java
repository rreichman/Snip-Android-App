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
import static android.support.test.runner.lifecycle.Stage.RESUMED;

import org.junit.Assert;

import java.util.Collection;
import java.util.LinkedList;

/**
 * Created by ranreichman on 8/18/16.
 */
public class TestUtils
{
    public static void waitForAdapterInActivity(ActivityTestRule<MyActivity> activityRule)
    {
        int i = 0;
        int sleepTimeInMilliseconds = 300;
        int iterations = 50000 / sleepTimeInMilliseconds;
        while ((null == activityRule.getActivity().mAdapter) && (i < iterations))
        {
            safeSleep(sleepTimeInMilliseconds);
            i++;
        }
        if (i == iterations)
        {
            Assert.fail("Timeout exception");
        }
    }

    public static void waitForAdapterInActivityToChange(int originalSize, ActivityTestRule<MyActivity> activityRule)
    {
        int i = 0;
        int sleepTimeInMilliseconds = 300;
        int iterations = 50000 / sleepTimeInMilliseconds;
        while ((originalSize == activityRule.getActivity().mAdapter.getDataset().size()) && (i < iterations))
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

    static Activity currentActivity;

    public static Activity getActivityInstance()
    {
        InstrumentationRegistry.getInstrumentation().runOnMainSync(new Runnable() {
            public void run() {
                Collection resumedActivities = ActivityLifecycleMonitorRegistry.getInstance().getActivitiesInStage(RESUMED);
                if (resumedActivities.iterator().hasNext()){
                    currentActivity = (Activity)resumedActivities.iterator().next();
                }
            }
        });

        return currentActivity;
    }

    public static LinkedList<SnipData> getCurrentDataset()
    {
        return ((SnipHoldingActivity)TestUtils.getActivityInstance()).mAdapter.getDataset();
    }

    public static void swipeUpScreen()
    {
        Espresso.onView(ViewMatchers.withId(R.id.swipeContainer)).perform(ViewActions.swipeUp());
    }

    public static void swipeDownScreen()
    {
        Espresso.onView(ViewMatchers.withId(R.id.swipeContainer)).perform(ViewActions.swipeDown());
    }

    public static void enterSearchResultActivityAndRefresh(int id)
    {
        Espresso.onView(ViewMatchers.withId(id)).perform(ViewActions.click());
        TestUtils.swipeDownScreen();
        TestUtils.swipeDownScreen();
        // TODO:: this is problematic when coming from the snoozed, but will change with fragments anyway
        TestUtils.waitForActivityToAppear(SearchResultActivity.class);
    }

    public static void getToRefreshedItemList(ActivityTestRule<MyActivity> activityRule)
    {
        TestUtils.waitForAdapterInActivity(activityRule);

        // Make sure there are more than 10 snips in list
        swipeUpScreen();
        swipeUpScreen();

        int amountOfSnips = activityRule.getActivity().mAdapter.getItemCount();

        // Refresh the list
        swipeDownScreen();
        swipeDownScreen();
        swipeDownScreen();
        swipeDownScreen();

        TestUtils.waitForAdapterInActivityToChange(amountOfSnips, activityRule);
    }

    public static int waitForActivityToAppear(Class activityClass)
    {
        int timeWaitingForOpen = 0;
        while (activityClass != TestUtils.getActivityInstance().getClass())
        {
            final int SLEEP_TIME_BETWEEN_CHECKS = 10;
            TestUtils.safeSleep(SLEEP_TIME_BETWEEN_CHECKS);
            timeWaitingForOpen += SLEEP_TIME_BETWEEN_CHECKS;
        }

        return timeWaitingForOpen;
    }

    public static void swipeUpUntilThereAreNoNewSnips(ActivityTestRule<MyActivity> activityRule)
    {
        // TODO:: fix this later
        for (int i = 0; i < 50; i++)
        {
            TestUtils.swipeUpScreen();
        }

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
            Espresso.onView(ViewMatchers.withId(R.id.snip_recycler_view)).perform(
                    RecyclerViewActions.actionOnItemAtPosition(position, ViewActions.swipeRight()));
        }
        else if (R.id.snoozeButtonOnToolbar == id)
        {
            Espresso.onView(ViewMatchers.withId(R.id.snip_recycler_view)).perform(
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
}
