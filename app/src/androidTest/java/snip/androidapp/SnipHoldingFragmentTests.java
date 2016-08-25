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
public class SnipHoldingFragmentTests
{
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
        new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testPassStubSnipHoldingActivity()
    {
        // This is to make sure everything is working
        Assert.assertTrue(true);
    }

    @Test
    public void testMainFlow()
    {
        final int DEFAULT_AMOUNT_OF_SNIPS = TestUtils.getDefaultAmountOfSnips();

        TestUtils.waitForAdapterInActivity(mActivityRule);
        int currentSize = TestUtils.getFragmentAdapterFromActivity(mActivityRule.getActivity()).getDataset().size();
        if (currentSize > DEFAULT_AMOUNT_OF_SNIPS)
        {
            TestUtils.swipeDownScreen();
            TestUtils.safeSleep(2000);
            TestUtils.waitForAdapterInActivityToChange(currentSize, mActivityRule);
        }
        Assert.assertEquals(
                TestUtils.getFragmentAdapterFromActivity(mActivityRule.getActivity()).getDataset().size(),
                DEFAULT_AMOUNT_OF_SNIPS);

        TestUtils.swipeUpScreen();
        TestUtils.swipeUpScreen();

        TestUtils.safeSleep(3000);
        TestUtils.waitForAdapterInActivityToChange(DEFAULT_AMOUNT_OF_SNIPS, mActivityRule);
        Assert.assertEquals(
                TestUtils.getFragmentAdapterFromActivity(mActivityRule.getActivity()).getDataset().size(),
                DEFAULT_AMOUNT_OF_SNIPS * 2);

        TestUtils.swipeDownScreen();
        TestUtils.swipeDownScreen();
        TestUtils.swipeDownScreen();

        TestUtils.waitForAdapterInActivityToChange(DEFAULT_AMOUNT_OF_SNIPS * 2, mActivityRule);
        Assert.assertEquals(
                TestUtils.getFragmentAdapterFromActivity(mActivityRule.getActivity()).getDataset().size(),
                DEFAULT_AMOUNT_OF_SNIPS);

        TestUtils.swipeUpScreen();
        TestUtils.swipeUpScreen();

        TestUtils.waitForAdapterInActivityToChange(DEFAULT_AMOUNT_OF_SNIPS, mActivityRule);
        Assert.assertEquals(
                TestUtils.getFragmentAdapterFromActivity(mActivityRule.getActivity()).getDataset().size(),
                DEFAULT_AMOUNT_OF_SNIPS * 2);

        mActivityRule.getActivity().finish();
        TestUtils.safeSleep(3000);
        mActivityRule.launchActivity(null);

        TestUtils.waitForAdapterInActivity(mActivityRule);
        Assert.assertEquals(
                TestUtils.getFragmentAdapterFromActivity(mActivityRule.getActivity()).getDataset().size(),
                DEFAULT_AMOUNT_OF_SNIPS * 2);
    }
}
