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
    public ActivityTestRule<MyActivity> mActivityRule =
            new ActivityTestRule<>(MyActivity.class);

    @Test
    public void testPassStubLoginActivity()
    {
        // This is to make sure everything is working
        Assert.assertTrue(true);
    }

    private void loginUser()
    {
        Espresso.onView(ViewMatchers.withId(R.id.input_email_signin)).perform(
                ViewActions.typeText("ran.reichman@gmail.com"));
        Espresso.onView(ViewMatchers.withId(R.id.input_password_signin)).perform(
                ViewActions.typeText("Qwerty123"));
        Espresso.onView(ViewMatchers.withId(R.id.btn_login)).perform(ViewActions.click());
    }

    @Test
    public void testLogoutAndThenLogin()
    {
        // This is in case the current user isn't logged in
        if (LoginActivity.class == TestUtils.getActivityInstance().getClass())
        {
            loginUser();
        }

        TestUtils.waitForAdapterInActivity(mActivityRule);

        Assert.assertEquals(MyActivity.class, TestUtils.getActivityInstance().getClass());

        TestUtils.clickButtonInMenu(R.string.logoutMenuTitle);

        Assert.assertEquals(LoginActivity.class, TestUtils.getActivityInstance().getClass());

        loginUser();

        TestUtils.waitForAdapterInActivity(mActivityRule);

        Assert.assertEquals(MyActivity.class, TestUtils.getActivityInstance().getClass());
    }
}
