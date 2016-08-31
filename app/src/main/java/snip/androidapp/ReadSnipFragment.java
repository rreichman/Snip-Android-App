package snip.androidapp;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by ranreichman on 8/24/16.
 */
public class ReadSnipFragment extends GenericSnipFragment implements OnTaskCompleted
{
    private View mRootView;
    private int mFragmentCodeOfCaller;

    private LinearLayout mLayout;
    public ReadSnipDesigner mSnipDesigner;

    //ViewPager mViewPager;
    //PagerAdapter mPagerAdapter;

    @Override
    public void onDestroyView()
    {
        // this code is based on http://stackoverflow.com/questions/11353075/how-can-i-maintain-fragment-state-when-added-to-the-back-stack
        if (null != mRootView.getParent())
        {
            ((ViewGroup)mRootView.getParent()).removeView(mRootView);
        }
        super.onDestroyView();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState)
    {
        if (null == mRootView)
        {
            mRootView = inflater.inflate(R.layout.clean_scrollable_fragment, parent, false);
        }

        return mRootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        Bundle bundle = getArguments();
        // If the layout isn't empty that means we're back here and the snip already exists.
        if ((null == bundle) || (null != mLayout))
        {
            return;
        }

        final SnipData snipData = (SnipData)bundle.getSerializable("snipData");
        mFragmentCodeOfCaller = bundle.getInt("fragmentCodeOfCaller");

        mLayout = (LinearLayout)getActivity().findViewById(R.id.clean_layout);
        mLayout.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeRight()
            {
                FragmentOperations.openOtherReadSnipFragment(getActivity(), true, snipData.mID, mFragmentCodeOfCaller);
            }
            public void onSwipeLeft()
            {
                FragmentOperations.openOtherReadSnipFragment(getActivity(),false, snipData.mID, mFragmentCodeOfCaller);
            }
        });

        mSnipDesigner = new ReadSnipDesigner(this, mLayout, snipData);
        mSnipDesigner.buildSnipView(this);

        //mViewPager = (ViewPager)getActivity().findViewById(R.id.pager);
        //mPagerAdapter = new PagerAdapter(getActivity().getSupportFragmentManager());
        //mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    public void onTaskCompleted()
    {
        mSnipDesigner.displayPhotos();
    }

    @Override
    protected int getFragmentCode()
    {
        return getResources().getInteger(R.integer.fragmentCodeReadSnip);
    }
}
