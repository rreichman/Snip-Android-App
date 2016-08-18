package snip.androidapp;

import android.app.Activity;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.support.test.rule.ActivityTestRule;
import android.support.v4.widget.SwipeRefreshLayout;

import java.util.LinkedList;

/**
 * Created by ranreichman on 8/18/16.
 */
@RunWith(AndroidJUnit4.class)
public class SnipHoldingActivityTests
{
    @Rule
    public ActivityTestRule<MyActivity> mActivityRule =
        new ActivityTestRule<>(MyActivity.class);

    /*@Test
    public void testPassStubSnipHoldingActivity()
    {
        // This is to make sure everything is working
        Assert.assertTrue(true);
    }*/

    @Test
    public void testMainFlow()
    {
        TestUtils.waitForAdapterInActivity(mActivityRule);
        int currentSize = mActivityRule.getActivity().mAdapter.getDataset().size();
        if (currentSize > 10)
        {
            Espresso.onView(ViewMatchers.withId(R.id.swipeContainer)).perform(ViewActions.swipeDown());
            TestUtils.safeSleep(2000);
            TestUtils.waitForAdapterInActivityToChange(currentSize, mActivityRule);
        }
        Assert.assertEquals(mActivityRule.getActivity().mAdapter.getDataset().size(), 10);

        Espresso.onView(ViewMatchers.withId(R.id.swipeContainer)).perform(ViewActions.swipeUp());
        Espresso.onView(ViewMatchers.withId(R.id.swipeContainer)).perform(ViewActions.swipeUp());

        TestUtils.safeSleep(3000);
        TestUtils.waitForAdapterInActivityToChange(10, mActivityRule);
        Assert.assertEquals(mActivityRule.getActivity().mAdapter.getDataset().size(), 20);

        Espresso.onView(ViewMatchers.withId(R.id.swipeContainer)).perform(ViewActions.swipeDown());
        Espresso.onView(ViewMatchers.withId(R.id.swipeContainer)).perform(ViewActions.swipeDown());
        Espresso.onView(ViewMatchers.withId(R.id.swipeContainer)).perform(ViewActions.swipeDown());

        TestUtils.waitForAdapterInActivityToChange(20, mActivityRule);
        Assert.assertEquals(mActivityRule.getActivity().mAdapter.getDataset().size(), 10);

        Espresso.onView(ViewMatchers.withId(R.id.swipeContainer)).perform(ViewActions.swipeUp());
        Espresso.onView(ViewMatchers.withId(R.id.swipeContainer)).perform(ViewActions.swipeUp());

        TestUtils.waitForAdapterInActivityToChange(10, mActivityRule);
        Assert.assertEquals(mActivityRule.getActivity().mAdapter.getDataset().size(), 20);

        mActivityRule.getActivity().finish();
        TestUtils.safeSleep(3000);
        mActivityRule.launchActivity(null);

        TestUtils.waitForAdapterInActivity(mActivityRule);
        Assert.assertEquals(mActivityRule.getActivity().mAdapter.getDataset().size(), 20);
    }
}
