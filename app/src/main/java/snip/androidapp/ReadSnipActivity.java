package snip.androidapp;

import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by ranreichman on 7/25/16.
 */
public class ReadSnipActivity extends GenericSnipActivity
{
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


    private void addTextDynamicallyToLayout(String text, boolean isLink, int styleId,
                                            int margin_horz, int margin_top, int margin_bottom, int align, int textStyle)
    {

        SnipTextView textView = new SnipTextView(new ContextThemeWrapper(this, styleId), styleId);
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

    private void fitImageHeightToScreen(ImageView curImage) {
                int screen_height = getResources().getDisplayMetrics().heightPixels;
        int image_height = curImage.getHeight();
        double SCREEN_IMAGE_RATIO = 0.75;
        if (image_height > (screen_height * SCREEN_IMAGE_RATIO )) {
            image_height = (int) (screen_height * SCREEN_IMAGE_RATIO );
        }
    }

    private void addPictureDynamicallyToLayout(String url, int styleId, int margin_horz, int margin_top, int margin_bottom)
    {
        ImageView imageView = new ImageView(this, null, styleId);
        Bitmap curImage = SnipData.getBitmapFromUrl(url);
        imageView.setImageBitmap(curImage);
//        fitImageHeightToScreen(ImageView curImage);
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(margin_horz, margin_top, margin_horz, margin_bottom);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        mLayout.addView(imageView, params);
    }

    private void parseSnipBodyAndCreateView() {
        try {
            JSONArray bodyElements = new JSONArray(mSnipData.mBody);
            for (int i = 0; i < bodyElements.length(); ++i)
            {
                Log.d("Adding element", Integer.toString(i));
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

        addTextDynamicallyToLayout(DatetimeUtils.getDateDiff(mSnipData.mDate, new Date()), false,
                R.style.SingleSnip_Text_Author, mDefMarginHorz, 0, 0, mDefGravity, mDefTextStyle);
    }

    private void buildSnipView() {
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
        ReactionBarCreator.addReactionBarToLayout(this, mLayout, mSnipData.mID);
    }

    public int getActivityCode()
    {
        //return getResources().getInteger(R.integer.activityCodeReadSnip);
        return 0;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        mSnipData = (SnipData)getIntent().getSerializableExtra(SnipData.getSnipDataString());
        super.onCreate(savedInstanceState);
        setContentView(R.layout.clean_scrollable_fragment);
        BaseToolbar activityToolbar = new BaseToolbar();
        activityToolbar.setupToolbar(this);

        LogUserActions.logContentView(this, "ReadSnip", Long.toString(mSnipData.mID));

        mLayout = (LinearLayout)findViewById(R.id.clean_layout);
        mDefMarginHorz = (int) getResources().getDimension(R.dimen.snip_text_margin_horz);
        mDefMarginVert = (int) getResources().getDimension(R.dimen.snip_text_margin_vert);
        mDefMarginHeadlineTop = (int) getResources().getDimension(R.dimen.snip_text_margin_headline_top);
        mDefMarginImageTop = (int) getResources().getDimension(R.dimen.snip_text_margin_image_top);
        mDefGravity = Gravity.RIGHT;
        mDefTextStyle = Typeface.NORMAL;
        buildSnipView();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }
}