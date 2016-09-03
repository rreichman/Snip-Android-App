package snip.androidapp;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;

import java.util.HashMap;

public class ScreenSlidePagerAdapter extends PagerAdapter implements OnTaskCompleted{
    MainActivity mMainActivity;
    //ReadSnipDesigner mSnipDesigner;
    HashMap<Long,ReadSnipDesigner> mSnipDesigners;
    int mPreviousCount = 0;
    ViewPager mViewPager;
    int mSourceFragmentCode;

    public ScreenSlidePagerAdapter(Context context, ViewPager viewPager, int sourceFragmentCode)
    {
        mMainActivity = (MainActivity) context;
        mViewPager = viewPager;
        mSnipDesigners = new HashMap<>();
        mSourceFragmentCode = sourceFragmentCode;
    }

    private SnipData getSnipDataFromPosition(int position)
    {
        SnipHoldingFragment fragment = (SnipHoldingFragment)
                mMainActivity.getSupportFragmentManager().findFragmentByTag(
                        Integer.toString(mSourceFragmentCode));
        if (null != fragment)
        {
            int adapterSize = fragment.mAdapter.getItemCount();

            if (adapterSize - position < EndlessRecyclerOnScrollListener.getVisibleThreshold())
            {
                EndlessRecyclerOnScrollListener.loadMore(
                        fragment.mRecyclerView,
                        fragment.getBaseSnipsQueryForFragment(),
                        fragment.getFragmentCode(),
                        false);
            }

            return fragment.mAdapter.getDataset().get(position);
        }

        return null;
    }

    @Override
    public Object instantiateItem(ViewGroup collection, int position)
    {
        LayoutInflater inflater = LayoutInflater.from(mMainActivity);
        ViewGroup outsideLayout = (ViewGroup) inflater.inflate(R.layout.clean_scrollable_fragment, collection, false);
        LinearLayout layout = (LinearLayout) outsideLayout.findViewById(R.id.clean_layout);
        SnipData snipData = getSnipDataFromPosition(position);
        layout.setTag(Long.toString(snipData.mID));

        ReadSnipDesigner snipDesigner = new ReadSnipDesigner(mMainActivity, layout, snipData);
        snipDesigner.buildSnipView(mMainActivity, this, snipData.mID);
        mSnipDesigners.put(snipData.mID, snipDesigner);

        collection.addView(outsideLayout);
        return outsideLayout;
    }

    @Override
    public void destroyItem(ViewGroup collection, int position, Object view)
    {
        collection.removeView((View) view);
    }

    @Override
    public int getCount()
    {
        SnipHoldingFragment fragment = (SnipHoldingFragment)
            mMainActivity.getSupportFragmentManager().findFragmentByTag(Integer.toString(mSourceFragmentCode));
        if (null != fragment)
        {
            int count = fragment.mAdapter.getItemCount();
            if (count != mPreviousCount)
            {
                mPreviousCount = count;
                notifyDataSetChanged();
            }
            return fragment.mAdapter.getItemCount();
        }

        return 0;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public void onTaskCompleted(Long snipID)
    {
        try
        {
            View view = mViewPager.findViewWithTag(Long.toString(snipID));
            //mSnipDesigner.displayPhotos((LinearLayout) view);
            mSnipDesigners.get(snipID).displayPhotos((LinearLayout)view);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Crashlytics.logException(e);
        }
    }
}