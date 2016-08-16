package snip.androidapp;

import android.content.Context;

import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.LevelStartEvent;
import com.crashlytics.android.answers.LoginEvent;

/**
 * Created by ranreichman on 8/3/16.
 */
public class LogUserActions
{
    // TODO:: think about more things to track and put inside code

    public static void logStartingActivity(int activityCode)
    {
        LevelStartEvent levelStartEvent = new LevelStartEvent();
        levelStartEvent.putLevelName(Integer.toString(activityCode));

        Answers.getInstance().logLevelStart(levelStartEvent);
    }

    public static void logStopActivity(int activityCode)
    {
        CustomEvent stopEvent = new CustomEvent("StopEvent");
        stopEvent.putCustomAttribute("ActivityCode", activityCode);
        Answers.getInstance().logCustom(stopEvent);
    }

    public static void logResumeActivity(int activityCode)
    {
        CustomEvent resumeEvent = new CustomEvent("ResumeEvent");
        resumeEvent.putCustomAttribute("ActivityCode", activityCode);
        Answers.getInstance().logCustom(resumeEvent);
    }

    public static void logPauseActivity(int activityCode)
    {
        CustomEvent pauseEvent = new CustomEvent("PauseEvent");
        pauseEvent.putCustomAttribute("ActivityCode", activityCode);
        Answers.getInstance().logCustom(pauseEvent);
    }

    public static void logServerError()
    {
        CustomEvent customEvent = new CustomEvent("ServerFailureEvent");
        Answers.getInstance().logCustom(customEvent);
    }

    public static void logContentView(
            Context context, String contentName, String contentID)
    {
        // TODO:: think about custom attributes
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName(contentName)
                .putContentId(contentID)
                .putCustomAttribute("Screen Orientation", context.getResources().getConfiguration().orientation));
    }
}
