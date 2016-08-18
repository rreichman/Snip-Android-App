package snip.androidapp;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by ranreichman on 8/18/16.
 */
public class LoginActivityTests {
    @Test
    public void testPassStubLoginActivity() {
        // This is to make sure everything is working
        Assert.assertTrue(true);
    }

    @Test
    public void testFailedLoginFlow() {
        // Try to login with a non-existing user
        // See that the user fails
        Assert.fail();
    }

    @Test
    public void testSuccessfulLoginFlow()
    {
        // Try to login with an existing user
        // See that the user succeeds
        Assert.fail();
    }

    @Test
    public void testSuccessfulSignupFlow()
    {
        // Operate the whole signup process, including the verification mail (how to do this?)
        // Sign up with user and password
        // Get link to click from server
        // "Click" link in app
        // Sign up
        // Send Server to delete the user
        Assert.fail();
    }
}
