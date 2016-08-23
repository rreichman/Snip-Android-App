package snip.androidapp;

import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.*;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Random;
import java.util.regex.Matcher;

/**
 * Created by ranreichman on 8/18/16.
 */
@RunWith(AndroidJUnit4.class)
public class SpeedTests
{
    @Rule
    public ActivityTestRule<MyActivity> mActivityRule =
            new ActivityTestRule<>(MyActivity.class);

    // Check that things start fast enough (mostly ignore Internet connection)
    @Test
    public void testPassStubSpeedTests()
    {
        // This is to make sure everything is working
        Assert.assertTrue(true);
    }

    @Test
    public void testReadSnipOpenSpeed()
    {
        TestUtils.getToRefreshedItemList(mActivityRule);

        final int AMOUNT_OF_ITERATIONS = 15;
        int amountOfSnips = mActivityRule.getActivity().mAdapter.getItemCount();
        Random rand = new Random();
        int maxTimeWaitingForOpen = 0;

        for (int i = 0; i < AMOUNT_OF_ITERATIONS; i++)
        {
            Espresso.onView(ViewMatchers.withId(R.id.snip_recycler_view)).perform(
                    RecyclerViewActions.actionOnItemAtPosition(rand.nextInt(amountOfSnips), ViewActions.click()));

            int timeWaitingForOpen = TestUtils.waitForActivityToAppear(ReadSnipActivity.class);
            if (timeWaitingForOpen > maxTimeWaitingForOpen)
            {
                maxTimeWaitingForOpen = timeWaitingForOpen;
            }

            Espresso.pressBack();
        }

        Assert.assertTrue(maxTimeWaitingForOpen < 100);
    }

    @Test
    public void testFeedbackOpenSpeed()
    {
        TestUtils.getToRefreshedItemList(mActivityRule);
        TestUtils.clickButtonInMenu(R.string.feedbackMenuTitle);

        int openSpeed = TestUtils.waitForActivityToAppear(FeedbackActivity.class);

        Assert.assertTrue(openSpeed < 100);
    }

    @Test
    public void testLikedOpenSpeed()
    {
        TestUtils.getToRefreshedItemList(mActivityRule);

        Espresso.onView(ViewMatchers.withId(R.id.likeButtonOnToolbar)).perform(ViewActions.click());
        int openSpeed = TestUtils.waitForActivityToAppear(SearchResultActivity.class);

        Assert.assertTrue(openSpeed < 100);
    }

    @Test
    public void testSnoozedOpenSpeed()
    {
        TestUtils.getToRefreshedItemList(mActivityRule);

        Espresso.onView(ViewMatchers.withId(R.id.snoozeButtonOnToolbar)).perform(ViewActions.click());
        int openSpeed = TestUtils.waitForActivityToAppear(SearchResultActivity.class);

        Assert.assertTrue(openSpeed < 100);
    }
}
