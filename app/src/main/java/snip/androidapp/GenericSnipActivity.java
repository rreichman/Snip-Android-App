package snip.androidapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.crashlytics.android.Crashlytics;

import butterknife.BindString;
import io.fabric.sdk.android.Fabric;

/**
 * Created by ranreichman on 8/12/16.
 */
public abstract class GenericSnipActivity extends AppCompatActivity
{
    @BindString(R.string.userEmailFile) String userEmailFile;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        Fabric.with(this, new Crashlytics());
        // TODO:: make these do something real
        logUserIntoCrashlytics();
        LogUserActions.logStartingActivity(getActivityCode());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop()
    {
        LogUserActions.logStopActivity(getActivityCode());
        super.onStop();
    }

    @Override
    public void onPause()
    {
        LogUserActions.logPauseActivity(getActivityCode());
        super.onPause();
    }

    @Override
    public void onResume()
    {
        LogUserActions.logResumeActivity(getActivityCode());
        super.onResume();
    }

    protected abstract int getActivityCode();

    private void logUserIntoCrashlytics()
    {
        Crashlytics.setUserEmail(retrieveUserEmail());
    }

    private String retrieveUserEmail()
    {
        String emailOfUser = null;
        try
        {
            emailOfUser = (String)DataCacheManagement.retrieveObjectFromFile(this, userEmailFile);
            if (null == emailOfUser)
            {
                throw new Exception();
            }
        }
        catch (Exception e)
        {
            emailOfUser = retrieveUserEmailFromServer();
        }

        return emailOfUser;
    }

    private String retrieveUserEmailFromServer()
    {
        // TODO:: implement
        return "user@mail.com";
    }
}
