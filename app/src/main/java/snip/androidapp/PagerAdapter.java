package snip.androidapp;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.LinkedList;

/**
 * Created by ranreichman on 8/30/16.
 */
public class PagerAdapter extends FragmentStatePagerAdapter
{
    LinkedList<Fragment> mFragmentList;

    public PagerAdapter(FragmentManager fm)
    {
        super(fm);
        mFragmentList = new LinkedList<>();
    }

    @Override
    public Fragment getItem(int position)
    {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount()
    {
        return mFragmentList.size();
    }
}
