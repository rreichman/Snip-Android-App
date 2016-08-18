package snip.androidapp;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ranreichman on 8/18/16.
 */
public class EndlessScrollerTests
{
    @Test
    public void testPassStubEndlessScroller()
    {
        // This is to make sure everything is working
        Assert.assertTrue(true);
    }

    @Test
    public void testOnScrolledLogic()
    {
        // Create a situation where the code should loadMore
        // See that it does and that it loads the correct amount of snips
        // Now code shouldn't loadMore
        // See that it doesn't
        Assert.fail();
    }

    @Test
    public void testLoadMore()
    {
        // Get to the starting position with 10 snips
        // Operate loadMore a random amount of times and check that 10*Random were loaded
        Assert.fail();
    }
}
