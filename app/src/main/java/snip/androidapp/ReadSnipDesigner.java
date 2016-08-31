package snip.androidapp;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by ranreichman on 8/31/16.
 */
public class ReadSnipDesigner
{
    LinearLayout mLayout;
    ReadSnipStyling mStyling;
    SnipData mSnipData;
    private int mCurrentPictureCount = 0;
    private HashMap<Integer,String> mMappingBetweenPictureNumberAndUrl = new HashMap<>();

    public ReadSnipDesigner(Fragment fragment, LinearLayout layout, SnipData snipData)
    {
        mLayout = layout;
        mSnipData = snipData;
        mStyling = new ReadSnipStyling(fragment);
    }

    public static Spanned fromHtml(String htmlString)
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

    private void addTextDynamicallyToLayout(Activity activity, String text, boolean isLink, int styleId,
                                            int margin_horz, int margin_top, int margin_bottom, int align, int textStyle)
    {

        SnipTextView textView = new SnipTextView(new ContextThemeWrapper(activity, styleId), styleId);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        textView.setGravity(align);
        textView.setTypeface(textView.getTypeface(), textStyle);
        params.setMargins(margin_horz, margin_top, margin_horz, margin_bottom);

        Spanned spanned = ReadSnipDesigner.fromHtml(text);
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

    private void addPictureDynamicallyToLayout(Activity activity,
                                               String url,
                                               int styleId, int margin_horz, int margin_top, int margin_bottom)
    {
        ImageView imageViewStub = new ImageView(activity, null, styleId);
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

    private void handleImageInSnipBody(Activity activity, JSONObject bodyElem) throws JSONException
    {
        String imageURL = bodyElem.getString("url");
        String imageTitle = bodyElem.getString("caption");

        addPictureDynamicallyToLayout(activity, imageURL, R.style.SingleSnip_Image, 0,
                mStyling.mDefMarginImageTop, 0);
        if (!imageTitle.isEmpty()) {
            addTextDynamicallyToLayout(
                    activity, imageTitle, false,
                    R.style.SingleSnip_Text_ImageDesc,
                    mStyling.mDefMarginHorz,
                    mStyling.mDefMarginVert,
                    mStyling.mDefMarginVert,
                    Gravity.CENTER, mStyling.mDefTextStyle);
        }
    }

    private void handleParagraphInSnipBody(Activity activity, JSONObject bodyElem) throws JSONException
    {
        String cur_value = bodyElem.getString("value");
        addTextDynamicallyToLayout(activity, cur_value, false, R.style.SingleSnip_Text,
                mStyling.mDefMarginHorz, mStyling.mDefMarginVert,
                mStyling.mDefMarginVert, mStyling.mDefGravity, mStyling.mDefTextStyle);
    }

    private void parseSnipBodyAndCreateView(Activity activity)
    {
        try {
            JSONArray bodyElements = new JSONArray(mSnipData.mBody);
            for (int i = 0; i < bodyElements.length(); ++i) {
                JSONObject bodyElem = bodyElements.getJSONObject(i);
                String cur_type = bodyElem.getString("type");
                switch (cur_type) {
                    case "image":
                        handleImageInSnipBody(activity, bodyElem);
                        break;
                    case "paragraph":
                        handleParagraphInSnipBody(activity, bodyElem);
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void addSnipMetaDataToLayout(Activity activity)
    {
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
            addTextDynamicallyToLayout(activity, text, false, R.style.SingleSnip_Text_Author,
                    mStyling.mDefMarginHorz, 0, 0, mStyling.mDefGravity, mStyling.mDefTextStyle);
        }

        addTextDynamicallyToLayout(activity, TimeUtils.getDateDiff(mSnipData.mDate, new Date()), false,
                R.style.SingleSnip_Text_Author, mStyling.mDefMarginHorz, 0, 0,
                mStyling.mDefGravity, mStyling.mDefTextStyle);
    }

    public void buildSnipView(ReadSnipFragment fragment)
    {
        Log.d("started", "presenting snip");
        addTextDynamicallyToLayout(fragment.getActivity(), mSnipData.mHeadline, false, R.style.SingleSnip_Text_Headline,
                mStyling.mDefMarginHorz, mStyling.mDefMarginHeadlineTop, mStyling.mDefMarginVert,
                mStyling.mDefGravity, Typeface.BOLD);
        addSnipMetaDataToLayout(fragment.getActivity());

        parseSnipBodyAndCreateView(fragment.getActivity());

        String externalLinksTitle = fragment.getResources().getString(R.string.ExternalLinksTitle);
        addTextDynamicallyToLayout(fragment.getActivity(), externalLinksTitle, false, R.style.SingleSnip_Text,
                mStyling.mDefMarginHorz, mStyling.mDefMarginVert, mStyling.mDefMarginVert,
                mStyling.mDefGravity, Typeface.BOLD);

        for (int i = 0; i < mSnipData.mExternalLinks.mExternalLinks.size(); ++i)
        {
            ExternalLinkData cur_link = mSnipData.mExternalLinks.mExternalLinks.get(i);
            createLinkView(fragment.getActivity(), cur_link);
        }

        ReactionBarCreator.addReactionBarToLayout(fragment.getActivity(), mLayout, mSnipData.mID);

        PictureCacher pictureCacher = new PictureCacher(fragment);
        pictureCacher.execute();

        Log.d("finished", "presenting snip");
    }

    private void createLinkView(Activity activity, ExternalLinkData link)
    {
        String author = "";
        if (!link.mAuthor.isEmpty()) {
            author = " (" + link.mAuthor + ")";
        }
        String htmlLinkString = "<a href=\"" + link.mLink + "\">" + link.mTitle + author + "</a>";
        addTextDynamicallyToLayout(activity, htmlLinkString, true, R.style.SingleSnip_Text,
                mStyling.mDefMarginHorz, mStyling.mDefMarginVert, mStyling.mDefMarginVert,
                mStyling.mDefGravity, mStyling.mDefTextStyle);
    }

    public void displayPhotos()
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
