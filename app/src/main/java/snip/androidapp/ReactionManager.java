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

    final String REACTION_LOG_TAG = "Reaction Post";

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
        postReactionToServer(context, snipID, "like");
    }

    public static void userDislikedSnip(Context context, long snipID)
    {
        postReactionToServer(context, snipID, "dislike");
    }

    public static void userSnoozedSnip(Context context, long snipID)
    {
        postReactionToServer(context, snipID, "snooze");
    }

    private static void postReactionToServer(Context context, long snipID, String reaction) {

        JSONObject loginJsonParams = new JSONObject();
        HashMap<String,String> headers =
                SnipCollectionInformation.getInstance().getTokenForWebsiteAccessAsHashMap(context);

        VolleyInternetOperator.responseFunctionInterface responseFunction =
                new VolleyInternetOperator.responseFunctionInterface() {
                    @Override
                    public void apply(Context context, JSONObject response, JSONObject params)
                    {
                        Log.d(REACTION_LOG_TAG, "Success");
                    }
                };
        VolleyInternetOperator.errorFunctionInterface errorFunction =
                new VolleyInternetOperator.errorFunctionInterface() {
                    @Override
                    public void apply(VolleyError error, JSONObject params)
                    {
                        Log.d(REACTION_LOG_TAG, "Fail");
                    }
                };
        String baseAccessURL = context.getResources().getString(R.string.baseAccessURL);
        String reactionBaseUrl = context.getResources().getString(R.string.snipsBaseURL);
        String query = "/?content_type=snip&content_id=" + Long.toString(snipID) + "&react=" + reaction;

        String url = baseAccessURL + reactionBaseUrl + query;

        VolleyInternetOperator.accessWebsiteWithVolley(
                context, url, Request.Method.POST, loginJsonParams, headers,
                responseFunction, errorFunction);
    }


}
