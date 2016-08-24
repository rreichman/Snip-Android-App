package snip.androidapp;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by ranreichman on 8/24/16.
 */
public abstract class GenericSnipFragment extends Fragment
{
    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        LogUserActions.logCreate(getActivity(), "CreateFragment", getFragmentCode());
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onStop()
    {
        LogUserActions.logStop(getActivity(), "StopFragment", getFragmentCode());
        super.onStop();
    }

    @Override
    public void onPause()
    {
        LogUserActions.logPause(getActivity(), "PauseFragment", getFragmentCode());
        super.onPause();
    }

    @Override
    public void onResume()
    {
        LogUserActions.logResume(getActivity(), "ResumeFragment", getFragmentCode());
        super.onResume();
    }

    @Override
    public void onDestroy()
    {
        LogUserActions.logDestroy(getActivity(), "DestroyFragment", getFragmentCode());
        super.onDestroy();
    }

    protected abstract int getFragmentCode();
}
