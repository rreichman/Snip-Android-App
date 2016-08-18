package snip.androidapp;

import android.support.test.rule.ActivityTestRule;

import org.junit.Assert;

import java.util.concurrent.TimeoutException;

/**
 * Created by ranreichman on 8/18/16.
 */
public class TestUtils
{
    public static void waitForAdapterInActivity(ActivityTestRule<MyActivity> activity)
    {
        int i = 0;
        int sleepTimeInMilliseconds = 300;
        int iterations = 50000 / sleepTimeInMilliseconds;
        while ((null == activity.getActivity().mAdapter) && (i < iterations))
        {
            safeSleep(sleepTimeInMilliseconds);
            i++;
        }
        if (i == iterations)
        {
            Assert.fail("Timeout exception");
        }
    }

    public static void waitForAdapterInActivityToChange(int originalSize, ActivityTestRule<MyActivity> activity)
    {
        int i = 0;
        int sleepTimeInMilliseconds = 300;
        int iterations = 50000 / sleepTimeInMilliseconds;
        while ((originalSize == activity.getActivity().mAdapter.getDataset().size()) && (i < iterations))
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
}
