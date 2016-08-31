package snip.androidapp;

import android.app.Activity;
import android.content.Context;
import android.widget.RelativeLayout;

import com.wang.avi.AVLoadingIndicatorView;

/**
 * Created by ranreichman on 8/31/16.
 */
public class AnimationUtils
{
    public static void showHideLoadingAnimation(Context context, boolean toShow)
    {
        AVLoadingIndicatorView avi = (AVLoadingIndicatorView) ((Activity) context).findViewById(R.id.avi_fragment);
        if (null != avi)
        {
            if (toShow)
            {
                avi.setVisibility(avi.VISIBLE);
                avi.show();
            }
            else
            {
                avi.hide();
            }
        }
    }

    public static void moveLoadingAnimationToBottom(Context context)
    {
        try
        {
            AVLoadingIndicatorView avi = (AVLoadingIndicatorView) ((Activity) context).findViewById(R.id.avi_fragment);
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) avi.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            avi.setLayoutParams(params);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
