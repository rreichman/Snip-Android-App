package snip.androidapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.view.Gravity;

/**
 * Created by ranreichman on 8/31/16.
 */
public class ReadSnipStyling
{
    public int mDefMarginHorz;
    public int mDefMarginVert;
    public int mDefMarginHeadlineTop;
    public int mDefMarginImageTop;
    public int mDefGravity;
    public int mDefTextStyle;

    public ReadSnipStyling(Context context)
    {
        mDefMarginHorz = (int) context.getResources().getDimension(R.dimen.snip_text_margin_horz);
        mDefMarginVert = (int) context.getResources().getDimension(R.dimen.snip_text_margin_vert);
        mDefMarginHeadlineTop = (int) context.getResources().getDimension(R.dimen.snip_text_margin_headline_top);
        mDefMarginImageTop = (int) context.getResources().getDimension(R.dimen.snip_text_margin_image_top);
        mDefGravity = Gravity.RIGHT;
        mDefTextStyle = Typeface.NORMAL;
    }
}
