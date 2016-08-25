package snip.androidapp;

import android.support.test.espresso.Espresso;
import android.support.test.espresso.action.ViewActions;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;
import android.view.View;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;

/**
 * Created by ranreichman on 8/18/16.
 */
public class LogoutTests
{
    @Rule
    public ActivityTestRule<MainActivity> mActivityRule =
            new ActivityTestRule<>(MainActivity.class);

    @Test
    public void testPassStubLoginActivity()
    {
        // This is to make sure everything is working
        Assert.assertTrue(true);
    }

    @Test
    public void testLogoutAndThenLogin()
    {
        TestUtils.loginIfLoggedOut();

        TestUtils.waitForAdapterInActivity(mActivityRule);

        Assert.assertEquals(MainActivity.class, TestUtils.getActivityInstance().getClass());

        TestUtils.clickButtonInMenu(R.string.logoutMenuTitle);

        Assert.assertEquals(LoginActivity.class, TestUtils.getActivityInstance().getClass());

        TestUtils.loginUser();

        TestUtils.waitForAdapterInActivity(mActivityRule);

        Assert.assertEquals(MainActivity.class, TestUtils.getActivityInstance().getClass());
    }
}
