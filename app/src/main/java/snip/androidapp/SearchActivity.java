package snip.androidapp;

import android.app.Activity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by ranreichman on 8/7/16.
 */
public class SearchActivity extends Activity
{
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // TODO:: this class should include a search widget

    }

    private void getFixedCategories() {

    }

    private void getCategories() {

    }

    public static void searchReactionSuccess(JSONObject response) {
        //Log.d(REACTION_LOG_TAG, "Success");
        Log.d("reaction", "success");
    }

    public static void categoriesReactionSuccess(JSONObject response) {
        //Log.d(REACTION_LOG_TAG, "Success");
        Log.d("reaction", "success");
    }

    public static void reactionFailed(VolleyError error) {
        Log.d("reaction", "failed");
    }


    public static void ServerCategoriesRequest(boolean getFixedCategories)
    {
        Context context = CustomVolleyRequestQueue.getInstance().getContext();
        HashMap<String,String> headers =
                SnipCollectionInformation.getInstance(context).getTokenForWebsiteAccessAsHashMap();

        VolleyInternetOperator.responseFunctionInterface responseFunction =
                new VolleyInternetOperator.responseFunctionInterface() {
                    @Override
                    public void apply(Context context, JSONObject response, JSONObject params)
                    {
                        categoriesReactionSuccess(response);
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
        String categoriesBaseUrl = context.getResources().getString(R.string.categeoriesBaseURL);
        if (getFixedCategories) {
            categoriesBaseUrl += context.getResources().getString(R.string.categeoriesFixedURL);
        }
        String url = baseAccessURL + categoriesBaseUrl;

        /*VolleyInternetOperator.accessWebsiteWithVolley(
                context, url, Request.Method.GET, null, headers,
                responseFunction, errorFunction);*/
    }


    public static void ServerSearchRequest(String searchString) {
        Context context = CustomVolleyRequestQueue.getInstance().getContext();
        HashMap<String,String> headers =
                SnipCollectionInformation.getInstance(context).getTokenForWebsiteAccessAsHashMap();

        VolleyInternetOperator.responseFunctionInterface responseFunction =
                new VolleyInternetOperator.responseFunctionInterface() {
                    @Override
                    public void apply(Context context, JSONObject response, JSONObject params)
                    {
                        searchReactionSuccess(response);
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

        String searchBaseQuery = "?content=";
        String baseAccessURL = context.getResources().getString(R.string.baseAccessURL);
        String searchBaseUrl = context.getResources().getString(R.string.searchBaseURL);
        String url = baseAccessURL + searchBaseUrl + searchBaseQuery + searchString;

        /*VolleyInternetOperator.accessWebsiteWithVolley(
                context, url, Request.Method.GET, null, headers,
                responseFunction, errorFunction);*/
    }




}
