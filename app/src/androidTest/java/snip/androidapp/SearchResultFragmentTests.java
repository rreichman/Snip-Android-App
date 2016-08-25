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

    @Test
    public void testLikeSnipsAndSeeThemInLikedScreenWithoutRefresh()
    {
        Assert.fail();
    }

    private void removeItemsFromMainScreenAndFindThemInOtherScreen(int id)
    {
        TestUtils.waitForAdapterInActivity(mActivityRule);

        TestUtils.enterSearchResultFragmentAndRefresh(id);
        TestUtils.swipeUpUntilThereAreNoNewSnips(mActivityRule);

        LinkedList<SnipData> searchResultSnipsDataset = TestUtils.getCurrentDataset();

        Espresso.pressBack();
        TestUtils.waitForFragmentToAppear(MainFragment.class);

        Random rand = new Random();
        int amountOfSnips = TestUtils.getFragmentAdapterFromActivity(mActivityRule.getActivity()).getItemCount();

        final int AMOUNT_OF_SNIPS_TO_LIKE = 2;
        int randomPositionToLike = 0;

        for (int i = 0; i < AMOUNT_OF_SNIPS_TO_LIKE; i++)
        {
            // TODO:: this is problematic because the screen only holds 4 snips and can't click on the 15th for instance
            //randomPositionToLike = rand.nextInt(amountOfSnips);
            TestUtils.swipeClickOrTapAccordingToId(id, randomPositionToLike);
        }

        TestUtils.enterSearchResultFragmentAndRefresh(id);
        TestUtils.swipeUpUntilThereAreNoNewSnips(mActivityRule);
        LinkedList<SnipData> newLikedSnipsDataset = TestUtils.getCurrentDataset();

        Assert.assertEquals(newLikedSnipsDataset.size() - AMOUNT_OF_SNIPS_TO_LIKE, searchResultSnipsDataset.size());
    }

    @Test
    public void testLikeSnipsAndSeeThemInLikedScreenAfterRefresh()
    {
        removeItemsFromMainScreenAndFindThemInOtherScreen(R.id.likeButtonOnToolbar);
    }

    @Test
    public void testSnoozeSnipsAndSeeThemInSnoozedScreenWithoutRefresh()
    {
        Assert.fail();
    }

    @Test
    public void testSnoozeSnipsAndSeeThemInSnoozedScreenAfterRefresh()
    {
        removeItemsFromMainScreenAndFindThemInOtherScreen(R.id.snoozeButtonOnToolbar);
    }
}
