package snip.androidapp;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by ranreichman on 8/10/16.
 */
public class FeedbackActivity extends GenericSnipActivity
{
    EditText mFeedbackBox;

    public void postSuccess()
    {
        Log.d("feedback", "failed");
        Toast.makeText(getApplicationContext(), "Feedback sent. Thanks!", Toast.LENGTH_LONG).show();
        mFeedbackBox.setText("");
    }

    public void postFailed(VolleyError error)
    {
        Log.d("reaction", "failed");
        Toast.makeText(getApplicationContext(), "Failed to send feedback :( Are you connected to the Internet?", Toast.LENGTH_SHORT).show();
    }

    private void sendFeedbackTextToServer(String feedbackText)
    {
        try
        {
            // Do i really need this?
            CustomVolleyRequestQueue.getInstance(this.getApplicationContext());
            HashMap<String, String> headers =
                    SnipCollectionInformation.getInstance(this).getTokenForWebsiteAccessAsHashMap();

            VolleyInternetOperator.responseFunctionInterface responseFunction =
                    new VolleyInternetOperator.responseFunctionInterface() {
                        @Override
                        public void apply(Context context, JSONObject response, JSONObject params) {
                            postSuccess();
                        }
                    };
            VolleyInternetOperator.errorFunctionInterface errorFunction =
                    new VolleyInternetOperator.errorFunctionInterface() {
                        @Override
                        public void apply(Context context, VolleyError error, JSONObject params) {
                            postFailed(error);
                        }
                    };

            Context context = CustomVolleyRequestQueue.getInstance().getContext();
            String baseAccessURL = context.getResources().getString(R.string.baseAccessURL);
            String feedbackBaseUrl = context.getResources().getString(R.string.feedbackBaseUrl);
            String url = baseAccessURL + feedbackBaseUrl;

            JSONObject params = new JSONObject();
            try {
                params.put("feedback", feedbackText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            VolleyInternetOperator.accessWebsiteWithVolley(
                    context, url, Request.Method.POST, params, headers,
                    responseFunction, errorFunction);
        }
        catch (Exception e)
        {
            Log.d("exception", "in post");
            Toast.makeText(getApplicationContext(), "Failed to send feedback :( Please try again", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        MenuHandler.handleItemSelectedInMenu(this, item);
        return super.onOptionsItemSelected(item);
    }

    public int getActivityCode()
    {
        return getResources().getInteger(R.integer.activityCodeFeedback);
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.feedback_activity);
        BaseToolbar activityToolbar = new BaseToolbar();
        activityToolbar.setupToolbar(this);
        LogUserActions.logStartingActivity(this, getResources().getInteger(R.integer.activityCodeFeedback));

        final Button sendFeedbackButton = (Button)findViewById(R.id.sendFeedbackButton);
        mFeedbackBox = (EditText)findViewById(R.id.feedbackText);

        sendFeedbackButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                String feedbackText = mFeedbackBox.getText().toString();
                if (!feedbackText.equals(""))
                {
                    sendFeedbackTextToServer(feedbackText);
                }
            }
        });
    }
}
