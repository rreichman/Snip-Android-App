package snip.androidapp;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.ContentViewEvent;
import com.crashlytics.android.answers.CustomEvent;
import com.crashlytics.android.answers.LevelStartEvent;
import com.crashlytics.android.answers.LoginEvent;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by ranreichman on 8/3/16.
 */
public class LogUserActions
{
    // TODO:: think about more things to track and put inside code

    public static void logStartingActivity(Context context, int activityCode)
    {
        LevelStartEvent levelStartEvent = new LevelStartEvent();
        levelStartEvent.putLevelName(Integer.toString(activityCode));

        Answers.getInstance().logLevelStart(levelStartEvent);
        sendLogToServer(context, "StartActivity", "", activityCode);
    }

    public static void logStopActivity(Context context, int activityCode)
    {
        CustomEvent stopEvent = new CustomEvent("StopEvent");
        stopEvent.putCustomAttribute("ActivityCode", activityCode);
        Answers.getInstance().logCustom(stopEvent);
        sendLogToServer(context, "StopActivity", "", activityCode);
    }

    public static void logResumeActivity(Context context, int activityCode)
    {
        CustomEvent resumeEvent = new CustomEvent("ResumeEvent");
        resumeEvent.putCustomAttribute("ActivityCode", activityCode);
        Answers.getInstance().logCustom(resumeEvent);
        sendLogToServer(context, "ResumeActivity", "", activityCode);
    }

    public static void logPauseActivity(Context context, int activityCode)
    {
        CustomEvent pauseEvent = new CustomEvent("PauseEvent");
        pauseEvent.putCustomAttribute("ActivityCode", activityCode);
        Answers.getInstance().logCustom(pauseEvent);
        sendLogToServer(context, "PauseActivity", "", activityCode);
    }

    public static void logServerError(Context context)
    {
        CustomEvent customEvent = new CustomEvent("ServerFailureEvent");
        Answers.getInstance().logCustom(customEvent);
        sendLogToServer(context, "ServerError", "", 0);
    }

    public static void logContentView(
            Context context, String contentName, String contentID)
    {
        // TODO:: think about custom attributes
        Answers.getInstance().logContentView(new ContentViewEvent()
                .putContentName(contentName)
                .putContentId(contentID)
                .putCustomAttribute("Screen Orientation", context.getResources().getConfiguration().orientation));
        sendLogToServer(context, "ContentView", contentName, new Integer(contentID));
    }

    public static void logUserLike(Context context, int snipID)
    {
        sendLogToServer(context, "PostLike", "", snipID);
    }

    public static void logUserDislike(Context context, int snipID)
    {
        sendLogToServer(context, "PostDislike", "", snipID);
    }

    public static void responseFunctionImplementation(
            Context context, JSONObject response, JSONObject params)
    {
        Log.d("success!", "good");
    }

    public static void errorFunctionImplementation(Context context, VolleyError error, JSONObject params)
    {
        Log.d("error!", "bad");
        String errorStr = VolleyInternetOperator.parseNetworkErrorResponse(error);
    }

    private static void sendLogToServer(Context context, String eventName, String strParam, int intParam)
    {
        try
        {
            JSONObject params = new JSONObject();
            params.put("name", eventName);
            String strParamNew = strParam;
            if (strParam.equals(""))
            {
                strParamNew = "null";
            }
            params.put("param_str_1", strParamNew);
            params.put("param_int_1", intParam);
            sendLogToServer(context, params);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
    }

    private static void sendLogToServer(Context context, JSONObject params)
    {
        try
        {
            String url = context.getResources().getString(R.string.baseAccessURL) +
                    context.getResources().getString(R.string.eventPostUrl);
            HashMap<String, String> headers =
                    SnipCollectionInformation.getInstance().getTokenForWebsiteAccessAsHashMap();

            VolleyInternetOperator.responseFunctionInterface responseFunction =
                    new VolleyInternetOperator.responseFunctionInterface() {
                        @Override
                        public void apply(Context context, JSONObject response, JSONObject params) {
                            responseFunctionImplementation(context, response, params);
                        }
                    };
            VolleyInternetOperator.errorFunctionInterface errorFunction =
                    new VolleyInternetOperator.errorFunctionInterface() {
                        @Override
                        public void apply(Context context, VolleyError error, JSONObject params) {
                            errorFunctionImplementation(context, error, params);
                        }
                    };

            VolleyInternetOperator.accessWebsiteWithVolley(context, url, Request.Method.POST,
                    params, headers, responseFunction, errorFunction);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
