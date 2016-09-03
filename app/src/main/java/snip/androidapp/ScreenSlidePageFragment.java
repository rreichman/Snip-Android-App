package snip.androidapp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by ranreichman on 9/1/16.
 */
public class ScreenSlidePageFragment extends GenericSnipFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_screen_slide_page, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        final ViewPager viewPager = (ViewPager) getActivity().findViewById(R.id.pagerSlide);
        if (null == viewPager.getAdapter())
        {
            Bundle bundle = getArguments();
            int position = bundle.getInt("position");
            int sourceFragmentCode = bundle.getInt("fragmentCodeOfCaller");
            viewPager.setAdapter(new ScreenSlidePagerAdapter(getActivity(), viewPager, sourceFragmentCode));
            viewPager.setCurrentItem(position);
        }

        BaseToolbar.updateToolbarAccordingToFragment(getActivity(), getFragmentCode());
    }

    public int getFragmentCode()
    {
        return getActivity().getResources().getInteger(R.integer.fragmentCodePager);
    }
}
