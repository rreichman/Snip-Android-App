package snip.androidapp;

import android.content.Context;
import android.widget.TextView;

/**
 * Created by ranihorev on 04/08/2016.
 */
public class SnipTextView extends TextView {
    public SnipTextView(Context context, int styleAtt) {
        super(context, null, styleAtt);
    }

    public SnipTextView(Context context) {
        super(context, null, R.style.SingleSnip_Text);
    }
}
