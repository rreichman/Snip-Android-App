package snip.androidapp;

/**
 * Created by ranihorev on 07/08/2016.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;
import com.crashlytics.android.answers.Answers;
import com.crashlytics.android.answers.LoginEvent;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.BindView;

public class LoginActivity extends GenericSnipActivity
{
    private static final String TAG = "LoginActivity";
    private ProgressDialog mProgressDialog;

    @BindView(R.id.input_email_signin) EditText _emailText;
    @BindView(R.id.input_password_signin) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;
    @BindString(R.string.baseAccessURL) String baseAccessURL;
    @BindString(R.string.signInURL) String signInURL;
    @BindString(R.string.emailField) String emailField;
    @BindString(R.string.signInProgressBar) String signInProgressBarText;
    @BindString(R.string.passField) String passField;
    @BindString(R.string.tokenField) String tokenField;
    @BindString(R.string.userTokenStringInPreferences) String userTokenStringInPreferences;
    @BindString(R.string.userEmailStringInPreferences) String userEmailStringInPreferences;

    public int getActivityCode()
    {
        return getResources().getInteger(R.integer.activityCodeLogin);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        
        _loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                login();
            }
        });
        _signupLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), SignupActivity.class);
                startActivityForResult(intent, getResources().getInteger(R.integer.activityCodeSignup));
            }
        });
    }

    private JSONObject getTokenJson(JSONObject response, JSONObject loginParams) {
        JSONObject tokenParams = new JSONObject();
        try {
            tokenParams.put(emailField, loginParams.getString(emailField));
            tokenParams.put(tokenField, response.getString(tokenField));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return tokenParams;
    }

    private void onLoginSuccess(Context context, JSONObject response, JSONObject loginParams)
    {
        try
        {
            Answers.getInstance().logLogin(new LoginEvent());
            JSONObject tokenJson = getTokenJson(response, loginParams);
            _loginButton.setEnabled(true);
            mProgressDialog.hide();

            SnipCollectionInformation.getInstance(context).setTokenForWebsiteAccess(response.getString(tokenField));
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);

            sharedPreferences.edit().putString(userTokenStringInPreferences, tokenJson.getString(tokenField)).commit();
            sharedPreferences.edit().putString(userEmailStringInPreferences, tokenJson.getString(emailField)).commit();
        }
        catch (JSONException e)
        {
            Log.d("Login", e.toString());
        }
        setResult(Activity.RESULT_OK, null);
        finish();
    }

    private void onLoginFailed(VolleyError error, JSONObject params)
    {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
        mProgressDialog.hide();
    }

    public void onValidateFailed()
    {
        Toast.makeText(getBaseContext(), "Validation failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    private void sendLoginRequest(final JSONObject loginJsonParams)
    {
        mProgressDialog = RegistrationUtils.showProgressDialog(this, R.style.TempAppTheme_Dark_Dialog, signInProgressBarText);
        int requestMethod = Request.Method.POST;
        VolleyInternetOperator.responseFunctionInterface loginSuccessFunction =
                new VolleyInternetOperator.responseFunctionInterface() {
                    @Override
                    public void apply(Context context, JSONObject response, JSONObject params)
                    {
                        onLoginSuccess(context, response, params);
                    }
                };
        VolleyInternetOperator.errorFunctionInterface loginFailedFunction =
                new VolleyInternetOperator.errorFunctionInterface() {
                    @Override
                    public void apply(Context context, VolleyError error, JSONObject params)
                    {
                        onLoginFailed(error, params);
                    }
                };
        VolleyInternetOperator.accessWebsiteWithVolley(this, baseAccessURL + signInURL,
                requestMethod, loginJsonParams, null, loginSuccessFunction, loginFailedFunction);
    }

    public void login() {
        Log.d(TAG, "Login");
        if (!validate()) {
            onValidateFailed();
            return;
        }
        _loginButton.setEnabled(false);
        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();
        JSONObject loginJsonParams = new JSONObject();
        try {
            loginJsonParams.put(emailField, email);
            loginJsonParams.put(passField, password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendLoginRequest(loginJsonParams);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == getResources().getInteger(R.integer.activityCodeSignup)) {
            if (resultCode == RESULT_OK) {
                String email = data.getStringExtra(emailField);
                String pass = data.getStringExtra(passField);
                _emailText.setText(email);
                _passwordText.setText(pass);
            }
        }
    }

    @Override
    public void onBackPressed() {
        // disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public boolean validate() {
        boolean valid = true;
        if (!RegistrationUtils.validateEmail(_emailText)) {valid = false;}
        if (!RegistrationUtils.validatePassword(_passwordText)) {valid = false;}
        return valid;
    }

}