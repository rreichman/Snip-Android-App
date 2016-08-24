package snip.androidapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;

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
        CustomVolleyRequestQueue.getInstance(this.getApplicationContext());

        Fabric.with(this, new Crashlytics());
        logUserIntoCrashlytics();
        LogUserActions.logCreate(this, "CreateActivity", getActivityCode());

        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop()
    {
        LogUserActions.logStop(this, "StopActivity", getActivityCode());
        super.onStop();
    }

    @Override
    public void onPause()
    {
        LogUserActions.logPause(this, "PauseActivity", getActivityCode());
        super.onPause();
    }

    @Override
    public void onResume()
    {
        LogUserActions.logResume(this, "ResumeActivity", getActivityCode());
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        LogUserActions.logDestroy(this, "DestroyActivity", getActivityCode());
        super.onDestroy();
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
            emailOfUser = "defaultUser@mail.com";
        }

        return emailOfUser;
    }
}
