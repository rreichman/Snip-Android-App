package snip.androidapp;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by ranreichman on 8/3/16.
 */
public class ReactionManager
{
    static String REACTION_LOG_TAG = "Reaction Post";

    // TODO replace to strings, context needed
    public static void userLikedSnip(long snipID)
    {
        postReactionToServer(snipID, "like");
    }

    public static void userUnLikedSnip(long snipID)
    {
        postReactionToServer(snipID, "un_like");
    }

    public static void userDislikedSnip(long snipID)
    {
        postReactionToServer(snipID, "dislike");
    }

    public static void userUnDislikedSnip(long snipID)
    {
        postReactionToServer(snipID, "un_dislike");
    }

    public static void userSnoozedSnip(long snipID)
    {
        postReactionToServer(snipID, "snooze");
    }

    public static void reactionSuccess() {
        //Log.d(REACTION_LOG_TAG, "Success");
        Log.d("reaction", "success");
    }

    public static void reactionFailed(VolleyError error) {
        Log.d("reaction", "failed");
    }

    public static void postReactionToServer(long snipID, String reaction)
    {
        Context context = CustomVolleyRequestQueue.getInstance().getContext();
        HashMap<String,String> headers =
                SnipCollectionInformation.getInstance(context).getTokenForWebsiteAccessAsHashMap();

        VolleyInternetOperator.responseFunctionInterface responseFunction =
                new VolleyInternetOperator.responseFunctionInterface() {
                    @Override
                    public void apply(Context context, JSONObject response, JSONObject params)
                    {
                        reactionSuccess();
                    }
                };
        VolleyInternetOperator.errorFunctionInterface errorFunction =
                new VolleyInternetOperator.errorFunctionInterface() {
                    @Override
                    public void apply(Context context, VolleyError error, JSONObject params)
                    {
                        reactionFailed(error);
                    }
                };

        String baseAccessURL = context.getResources().getString(R.string.baseAccessURL);
        String reactionBaseUrl = context.getResources().getString(R.string.reactionBaseURL);
        String url = baseAccessURL + reactionBaseUrl;

        JSONObject params = new JSONObject();
        try {
            params.put("content_type", "snip");
            params.put("object_id", Long.toString(snipID));
            params.put("react", reaction);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        VolleyInternetOperator.accessWebsiteWithVolley(
                context, url, Request.Method.POST, params, headers,
                responseFunction, errorFunction);
    }


}
