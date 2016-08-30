package snip.androidapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.graphics.Typeface;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by ranreichman on 8/24/16.
 */
public class ReadSnipFragment extends GenericSnipFragment implements OnTaskCompleted
{
    View mRootView;
    int mCurrentPictureCount = 0;
    HashMap<Integer,String> mMappingBetweenPictureNumberAndUrl = new HashMap<>();
    int mFragmentCodeOfCaller;

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
        if (null == bundle)
        {
            return;
        }

        mSnipData = (SnipData)bundle.getSerializable("snipData");
        mFragmentCodeOfCaller = bundle.getInt("fragmentCodeOfCaller");

        mLayout = (LinearLayout)getActivity().findViewById(R.id.clean_layout);
        mLayout.setOnTouchListener(new OnSwipeTouchListener(getActivity()) {
            public void onSwipeRight()
            {
                FragmentOperations.openOtherReadSnipFragment(getActivity(), true, mSnipData.mID, mFragmentCodeOfCaller);
                //Toast.makeText(getActivity(), "right", Toast.LENGTH_SHORT).show();
            }
            public void onSwipeLeft()
            {
                FragmentOperations.openOtherReadSnipFragment(getActivity(),false, mSnipData.mID, mFragmentCodeOfCaller);
                //Toast.makeText(getActivity(), "left", Toast.LENGTH_SHORT).show();
            }
        });

        mDefMarginHorz = (int) getResources().getDimension(R.dimen.snip_text_margin_horz);
        mDefMarginVert = (int) getResources().getDimension(R.dimen.snip_text_margin_vert);
        mDefMarginHeadlineTop = (int) getResources().getDimension(R.dimen.snip_text_margin_headline_top);
        mDefMarginImageTop = (int) getResources().getDimension(R.dimen.snip_text_margin_image_top);
        mDefGravity = Gravity.RIGHT;
        mDefTextStyle = Typeface.NORMAL;
        buildSnipView();

        //mViewPager = (ViewPager)getActivity().findViewById(R.id.pager);
        //mPagerAdapter = new PagerAdapter(getActivity().getSupportFragmentManager());
        //mViewPager.setAdapter(mPagerAdapter);
    }

    @Override
    protected int getFragmentCode()
    {
        return getResources().getInteger(R.integer.fragmentCodeReadSnip);
    }

    protected SnipData mSnipData;
    protected LinearLayout mLayout;
    protected int mDefMarginHorz;
    protected int mDefMarginVert;
    protected int mDefGravity;
    protected int mDefTextStyle;
    protected int mDefMarginHeadlineTop;
    protected int mDefMarginImageTop;

    private static Spanned fromHtml(String htmlString)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
        {
            return Html.fromHtml(htmlString,Html.FROM_HTML_MODE_COMPACT);
        }
        else
        {
            return Html.fromHtml(htmlString);
        }
    }


    private String getDateDiff(Date startDate, Date endDate) {

        //milliseconds
        long different = endDate.getTime() - startDate.getTime();

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;
        long weeksInMilli = daysInMilli * 7;

        long elapsedWeeks = different / weeksInMilli;
        if (elapsedWeeks > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, ''yy"); // Set your date format
            return sdf.format(startDate);
        }

        String timeDiffStr = "";

        long elapsedDays = different / daysInMilli;
        if (elapsedDays > 0) {
            return Long.toString(elapsedDays) + " day" + isTimePlural(elapsedDays);
        }

        long elapsedHours = different / hoursInMilli;
        if (elapsedHours > 0) {
            String prefix = "hr";
            return Long.toString(elapsedHours) + " hr" + isTimePlural(elapsedHours);
        }

        long elapsedMinutes = different / minutesInMilli;
        return Long.toString(elapsedMinutes) + " min" + isTimePlural(elapsedMinutes);
    }

    private String isTimePlural(long elapsedTime) {
        if (elapsedTime > 1) {
            return "s";
        }
        return "";
    }

    private void addTextDynamicallyToLayout(String text, boolean isLink, int styleId,
                                            int margin_horz, int margin_top, int margin_bottom, int align, int textStyle)
    {

        SnipTextView textView = new SnipTextView(new ContextThemeWrapper(getActivity(), styleId), styleId);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        textView.setGravity(align);
        textView.setTypeface(textView.getTypeface(), textStyle);
        params.setMargins(margin_horz, margin_top, margin_horz, margin_bottom);

        Spanned spanned = fromHtml(text);
        if (isLink)
        {
            textView.setMovementMethod(LinkMovementMethod.getInstance());
            textView.setText(spanned);
        }
        else {
            textView.setText(spanned.toString().trim());
        }
        textView.setLayoutParams(params);
        mLayout.addView(textView, params);
    }

    private void addPictureDynamicallyToLayout(String url, int styleId, int margin_horz, int margin_top, int margin_bottom)
    {
        ImageView imageViewStub = new ImageView(getActivity(), null, styleId);
        imageViewStub.setId(mCurrentPictureCount);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin_horz, margin_top, margin_horz, margin_bottom);
        imageViewStub.setAdjustViewBounds(true);
        imageViewStub.setScaleType(ImageView.ScaleType.FIT_XY);

        mMappingBetweenPictureNumberAndUrl.put(mCurrentPictureCount, url);
        mCurrentPictureCount++;

        mLayout.addView(imageViewStub, params);
    }

    public void addPicturesToLayout()
    {
        for (int i = 0; i < mCurrentPictureCount; i++)
        {
            Log.d("getting pic", Integer.toString(i));
            Bitmap curImage = SnipData.getBitmapFromUrl(mMappingBetweenPictureNumberAndUrl.get(i));
        }
    }

    private void parseSnipBodyAndCreateView() {
        try {
            JSONArray bodyElements = new JSONArray(mSnipData.mBody);
            for (int i = 0; i < bodyElements.length(); ++i) {
                JSONObject bodyElem = bodyElements.getJSONObject(i);
                String cur_type = bodyElem.getString("type");
                switch (cur_type) {
                    case "image":
                        String imageURL = bodyElem.getString("url");
                        String imageTitle = bodyElem.getString("caption");

                        addPictureDynamicallyToLayout(imageURL, R.style.SingleSnip_Image, 0, mDefMarginImageTop, 0);
                        if (!imageTitle.isEmpty()) {
                            addTextDynamicallyToLayout(imageTitle, false, R.style.SingleSnip_Text_ImageDesc,
                                    mDefMarginHorz, mDefMarginVert, mDefMarginVert, Gravity.CENTER, mDefTextStyle);
                        }
                        break;
                    case "paragraph":
                        String cur_value = bodyElem.getString("value");
                        addTextDynamicallyToLayout(cur_value, false, R.style.SingleSnip_Text,
                                mDefMarginHorz, mDefMarginVert, mDefMarginVert, mDefGravity, mDefTextStyle);
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void createLinkView(ExternalLinkData link) {
        String author = "";
        if (!link.mAuthor.isEmpty()) {
            author = " (" + link.mAuthor + ")";
        }
        String htmlLinkString = "<a href=\"" + link.mLink + "\">" + link.mTitle + author + "</a>";
        addTextDynamicallyToLayout(htmlLinkString, true, R.style.SingleSnip_Text,
                mDefMarginHorz, mDefMarginVert, mDefMarginVert, mDefGravity, mDefTextStyle);

    }


    private void addSnipMetaDataToLayout() {
        String text = "";
        if ((!mSnipData.mPublisher.isEmpty()) && (!mSnipData.mAuthor.isEmpty())) {
            text += mSnipData.mPublisher + ", " + mSnipData.mAuthor;
        }
        else if (!mSnipData.mAuthor.isEmpty()) {
            text += mSnipData.mAuthor;
        }
        else if (!mSnipData.mPublisher.isEmpty()) {
            text += mSnipData.mPublisher;
        }
        if (!text.isEmpty()) {
            addTextDynamicallyToLayout(text, false, R.style.SingleSnip_Text_Author,
                    mDefMarginHorz, 0, 0, mDefGravity, mDefTextStyle);
        }

        addTextDynamicallyToLayout(getDateDiff(mSnipData.mDate, new Date()), false,
                R.style.SingleSnip_Text_Author, mDefMarginHorz, 0, 0, mDefGravity, mDefTextStyle);
    }

    private void buildSnipView()
    {
        Log.d("started", "presenting snip");
        addTextDynamicallyToLayout(mSnipData.mHeadline, false, R.style.SingleSnip_Text_Headline,
                mDefMarginHorz, mDefMarginHeadlineTop, mDefMarginVert, mDefGravity, Typeface.BOLD);
        addSnipMetaDataToLayout();

        parseSnipBodyAndCreateView();

        String externalLinksTitle = getResources().getString(R.string.ExternalLinksTitle);
        addTextDynamicallyToLayout(externalLinksTitle, false, R.style.SingleSnip_Text,
                mDefMarginHorz, mDefMarginVert, mDefMarginVert, mDefGravity, Typeface.BOLD);

        for (int i = 0; i < mSnipData.mExternalLinks.mExternalLinks.size(); ++i) {
            ExternalLinkData cur_link = mSnipData.mExternalLinks.mExternalLinks.get(i);
            createLinkView(cur_link);
        }

        ReactionBarCreator.addReactionBarToLayout(getActivity(), mLayout, mSnipData.mID);

        PictureCacher pictureCacher = new PictureCacher(this);
        pictureCacher.execute();

        Log.d("finished", "presenting snip");
    }

    @Override
    public void onTaskCompleted()
    {
        for (int i = 0; i < mCurrentPictureCount; i++)
        {
            Log.d("after caching pics", Integer.toString(i));
            ImageView imageView = (ImageView)mLayout.findViewById(i);
            Bitmap curImage = SnipData.getBitmapFromUrl(mMappingBetweenPictureNumberAndUrl.get(i));
            imageView.setImageBitmap(curImage);
        }
    }
}
