package snip.androidapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.view.View;


/**
 * Created by ranihorev on 03/08/2016.
 */
public class SimpleDividerItemDecoration extends RecyclerView.ItemDecoration {
    private LayerDrawable mDivider;

    public SimpleDividerItemDecoration(Context context) {
        mDivider = (LayerDrawable) ContextCompat.getDrawable(context,R.drawable.line_divider);
    }

    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int div_margin_prct = parent.getContext().getResources().getInteger(R.integer.div_margin_prct);
        int parentWidth = parent.getWidth();
        int paddingWidth = parentWidth * div_margin_prct / 100;
        int leftDivider_left = parent.getPaddingLeft();
        int leftDivider_right = leftDivider_left + paddingWidth;
        int rightDivider_right = parentWidth - parent.getPaddingRight();
        int rightDivider_left   = rightDivider_right - paddingWidth;

        Drawable leftDivider = mDivider.findDrawableByLayerId(R.id.leftDivider);
        Drawable gradDivider = mDivider.findDrawableByLayerId(R.id.gradientDivider);
        Drawable rightDivider = mDivider.findDrawableByLayerId(R.id.rightDivider);

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            leftDivider.setBounds(leftDivider_left, top, leftDivider_right, bottom);
            gradDivider.setBounds(leftDivider_right, top, rightDivider_left, bottom);
            rightDivider.setBounds(rightDivider_left, top, rightDivider_right, bottom);
            mDivider.draw(c);
        }
    }
}
