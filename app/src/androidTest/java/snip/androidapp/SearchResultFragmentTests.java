package snip.androidapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

import java.util.LinkedList;
import java.util.Random;

/**
 * Created by ranreichman on 8/18/16.
 */
public class SearchResultFragmentTests
{
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testPassStubSearchResultActivity()
    {
        // This is to make sure everything is working
        Assert.assertTrue(true);
    }

    private void removeItemsFromMainScreenAndFindThemInOtherScreen(int id, boolean shouldRefresh)
    {
        TestUtils.waitForAdapterInActivity(mActivityRule);

        TestUtils.enterSearchResultFragmentAndRefresh(id);
        TestUtils.swipeUpUntilThereAreNoNewSnips(mActivityRule);

        int originalSize = TestUtils.getCurrentDataset().size();

        Espresso.pressBack();
        TestUtils.waitForFragmentToAppear(MainFragment.class);

        Random rand = new Random();
        int amountOfSnips = TestUtils.getFragmentAdapterFromActivity(mActivityRule.getActivity()).getItemCount();

        final int AMOUNT_OF_SNIPS_TO_LIKE_SNOOZE = 2;
        int randomPositionToLikeSnooze = 0;

        for (int i = 0; i < AMOUNT_OF_SNIPS_TO_LIKE_SNOOZE; i++)
        {
            // TODO:: this is problematic because the screen only holds 4 snips and can't click on the 15th for instance
            //randomPositionToLike = rand.nextInt(amountOfSnips);
            TestUtils.swipeClickOrTapAccordingToId(id, randomPositionToLikeSnooze);
            TestUtils.safeSleep(500);
        }

        if (shouldRefresh) {
            TestUtils.enterSearchResultFragmentAndRefresh(id);
        }
        else
        {
            Espresso.onView(ViewMatchers.withId(id)).perform(ViewActions.click());
            // TODO:: this is problematic when coming from the snoozed
            TestUtils.waitForFragmentToAppear(SearchResultFragment.class);
        }
        TestUtils.swipeUpUntilThereAreNoNewSnips(mActivityRule);
        LinkedList<SnipData> newSearchResultSnipsDataset = TestUtils.getCurrentDataset();

        Assert.assertEquals(newSearchResultSnipsDataset.size() - AMOUNT_OF_SNIPS_TO_LIKE_SNOOZE, originalSize);
    }

    @Test
    public void testLikeSnipsAndSeeThemInLikedScreenWithoutRefresh()
    {
        removeItemsFromMainScreenAndFindThemInOtherScreen(R.id.likeButtonOnToolbar, false);
    }

    @Test
    public void testLikeSnipsAndSeeThemInLikedScreenAfterRefresh()
    {
        removeItemsFromMainScreenAndFindThemInOtherScreen(R.id.likeButtonOnToolbar, true);
    }

    @Test
    public void testSnoozeSnipsAndSeeThemInSnoozedScreenWithoutRefresh()
    {
        removeItemsFromMainScreenAndFindThemInOtherScreen(R.id.snoozeButtonOnToolbar, false);
    }

    @Test
    public void testSnoozeSnipsAndSeeThemInSnoozedScreenAfterRefresh()
    {
        removeItemsFromMainScreenAndFindThemInOtherScreen(R.id.snoozeButtonOnToolbar, true);
    }
}
