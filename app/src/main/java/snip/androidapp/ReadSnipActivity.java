package snip.androidapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.view.ContextThemeWrapper;
import android.text.Html;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.support.v7.app.AppCompatActivity;

import com.nostra13.universalimageloader.core.ImageLoader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by ranreichman on 7/25/16.
 */
public class ReadSnipActivity extends AppCompatActivity
{
    protected SnipData mSnipData;

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
        long WeekInMilli = hoursInMilli * 24;

        long elapsedWeeks = different / daysInMilli;
        if (elapsedWeeks > 0) {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM d, ''yy"); // Set your date format
            return sdf.format(startDate);
        }

        String timeDiffStr = "";

        long elapsedDays = different / daysInMilli;
        if (elapsedDays > 0) {
            return Long.toString(elapsedDays) + "D";
        }

        long elapsedHours = different / hoursInMilli;
        if (elapsedHours > 0) {
            return (Long.toString(elapsedHours) + "H");
        }

        long elapsedMinutes = different / minutesInMilli;
        return Long.toString(elapsedMinutes) + "m";


    }


    private void addTextDynamicallyToLayout(LinearLayout layout, String text, boolean isLink, int styleId)
    {

        SnipTextView textView = new SnipTextView(new ContextThemeWrapper(this, styleId), styleId);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT);

        textView.setGravity(Gravity.RIGHT);

        int margin_horz = (int) getResources().getDimension(R.dimen.snip_text_margin_horz);
        int margin_vert = (int) getResources().getDimension(R.dimen.snip_text_margin_vert);
        params.setMargins(margin_horz, margin_vert, margin_horz, margin_vert);

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
        layout.addView(textView, params);
    }

    private void addPictureDynamicallyToLayout(LinearLayout layout, String url, int styleId)
    {
        ImageView imageView = new ImageView(this, null, styleId);
        Bitmap cur_image = SnipData.getBitmapFromUrl(url);
        imageView.setImageBitmap(cur_image);

        int screen_height = getResources().getDisplayMetrics().heightPixels;
        int image_height = cur_image.getHeight();
        double SCREEN_IMAGE_RATIO = 0.75;
        if (image_height > (screen_height * SCREEN_IMAGE_RATIO )) {
            image_height = (int) (screen_height * SCREEN_IMAGE_RATIO );
        }
        LinearLayout.LayoutParams params =
                new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        imageView.setAdjustViewBounds(true);
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        layout.addView(imageView, params);
    }

    private void parseSnipBodyAndCreateView(LinearLayout layout) {
        try {
            JSONArray bodyElements = new JSONArray(mSnipData.mBody);
            for (int i = 0; i < bodyElements.length(); ++i) {
                JSONObject bodyElem = bodyElements.getJSONObject(i);
                String cur_type = bodyElem.getString("type");
                switch (cur_type) {
                    case "image":
                        String imageURL = bodyElem.getString("url");
                        String imageTitle = bodyElem.getString("title");
                        addPictureDynamicallyToLayout(layout, imageURL, R.style.SingleSnip_Image);
                        addTextDynamicallyToLayout(layout, imageTitle, false, R.style.SingleSnip_Text_ImageDesc);
                        break;
                    case "paragraph":
                        String cur_value = bodyElem.getString("value");
                        addTextDynamicallyToLayout(layout, cur_value, false, R.style.SingleSnip_Text);
                        break;
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    private void createLinkView(LinearLayout layout, ExternalLinkData link) {
        String htmlLinkString = "<a href=\"" + link.mLink + "\">" + link.mTitle + " (" + link.mAuthor + ")" + "</a>";
        addTextDynamicallyToLayout(layout, htmlLinkString, true, R.style.SingleSnip_Text);
    }

    private void addReactionBarToLayout(LinearLayout layout) {
        Button button = new Button(this);
        button.setText("LIKE");
        layout.addView(button);
    }

    private void buildSnipView(LinearLayout layout) {
        addTextDynamicallyToLayout(layout, mSnipData.mHeadline, false, R.style.SingleSnip_Text_Headline);
        addTextDynamicallyToLayout(layout, mSnipData.mAuthor, false, R.style.SingleSnip_Text_Author);
        addTextDynamicallyToLayout(layout, mSnipData.mPublisher, false, R.style.SingleSnip_Text_Author);
        addTextDynamicallyToLayout(layout, getDateDiff(mSnipData.mDate, new Date()), false, R.style.SingleSnip_Text_Author);
        parseSnipBodyAndCreateView(layout);
        for (int i = 0; i < mSnipData.mExternalLinks.mExternalLinks.size(); ++i) {
            ExternalLinkData cur_link = mSnipData.mExternalLinks.mExternalLinks.get(i);
            createLinkView(layout, cur_link);
        }
        addReactionBarToLayout(layout);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        mSnipData = (SnipData)getIntent().getSerializableExtra(SnipData.getSnipDataString());

        super.onCreate(savedInstanceState);
        setContentView(R.layout.clean_scrollable_activity);

        BaseToolbar activityToolbar = new BaseToolbar();
        activityToolbar.setupToolbar(this);

        LinearLayout layout = (LinearLayout)findViewById(R.id.clean_layout);
        buildSnipView(layout);

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