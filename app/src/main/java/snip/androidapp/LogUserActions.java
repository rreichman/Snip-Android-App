package snip.androidapp;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;

/**
 * Created by ranreichman on 8/3/16.
 */
public class LogUserActions
{
    // TODO:: think about more things to track and put inside code

    public static void logContentView(
            String contentName, String contentType, String contentID)
    {
        // TODO:: think about custom attributes
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName(contentName)
                .putContentType(contentType)
                .putContentId(contentID)
                .putCustomAttribute("Favorites Count", 20)
                .putCustomAttribute("Screen Orientation", "Landscape"));

    }
}
