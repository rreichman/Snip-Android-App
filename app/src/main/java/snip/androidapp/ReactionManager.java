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
//    private static void showToastReaction(Context context) {
//        Toast toast = Toast.makeText(context, "Toast text", Toast.LENGTH_SHORT);
//        View view = toast.getView();
//        view.setBackgroundColor(Color.TRANSPARENT);
//        toast.setView(view);
//        toast.show();
//    }

    static String REACTION_LOG_TAG = "Reaction Post";

    private static void postReactionToServer(String reaction)
    {
        // TODO:: implement
    }

    public static void userLikedSnip(Context context, long snipID)
    {
        // TODO:: implement
//        ImageView myImageView =(ImageView)((Activity)context).findViewById(R.id.heart);
//        Animation myFadeInAnimation = AnimationUtils.loadAnimation(context, R.anim.fadein);
//        myImageView.startAnimation(myFadeInAnimation);
        postReactionToServer(snipID, "like");
    }

    public static void userDislikedSnip(long snipID)
    {
        postReactionToServer(snipID, "dislike");
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

    public static void postReactionToServer(long snipID, String reaction) {

        HashMap<String,String> headers =
                SnipCollectionInformation.getInstance().getTokenForWebsiteAccessAsHashMap();

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
                    public void apply(VolleyError error, JSONObject params)
                    {
                        reactionFailed(error);
                    }
                };

        Context context = CustomVolleyRequestQueue.getInstance().getContext();
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
