package snip.androidapp;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ranihorev on 04/08/2016.
 */
public class SnipTextView extends TextView {

    final String baseFontsFolder = "fonts/";

    public SnipTextView(Context context, int styleAtt) {
        super(context, null, styleAtt);
        setFont();
    }


    public SnipTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFont();
    }

    public SnipTextView(Context context) {
        super(context);
        setFont();
    }

    private void setFont(String fontName) {
        Typeface fontType = Typeface.createFromAsset(this.getContext().getAssets(), baseFontsFolder + fontName);
        this.setTypeface(fontType);
    }

    private void setFont() {
        setFont(this.getResources().getString(R.string.basicFont));
    }


}
