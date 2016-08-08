package snip.androidapp;

/**
 * Created by ranihorev on 07/08/2016.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.BindView;

public class LoginActivity extends AppCompatActivity
{
    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private ProgressDialog mProgressDialog;

    @BindView(R.id.input_email) EditText _emailText;
    @BindView(R.id.input_password) EditText _passwordText;
    @BindView(R.id.btn_login) Button _loginButton;
    @BindView(R.id.link_signup) TextView _signupLink;
    @BindString(R.string.baseAccessURL) String baseAccessURL;
    @BindString(R.string.signInURL) String signInURL;
    @BindString(R.string.emailField) String emailField;
    @BindString(R.string.passField) String passField;
    @BindString(R.string.tokenField) String tokenField;
    @BindString(R.string.userTokenFile) String userTokenFile;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
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
                startActivityForResult(intent, REQUEST_SIGNUP);
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

    private ProgressDialog showProgressDialog() {
        mProgressDialog = new ProgressDialog(LoginActivity.this,
                R.style.TempAppTheme_Dark_Dialog);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage("Authenticating...");
        mProgressDialog.show();
        return mProgressDialog;
    }

    private void onLoginSuccess(Context context, JSONObject response, JSONObject loginParams)
    {
        Intent returnIntent = new Intent();

        try
        {
            JSONObject tokenJson = getTokenJson(response, loginParams);
            _loginButton.setEnabled(true);
            mProgressDialog.hide();

            SnipCollectionInformation.getInstance().setTokenForWebsiteAccess(response.getString(tokenField));
            DataCacheManagement.saveObjectToFile(getBaseContext(), tokenJson, userTokenFile);
        }
        catch (JSONException e)
        {
            Log.d("Login", e.toString());
        }
        setResult(Activity.RESULT_OK, returnIntent);
        finish();
    }

    private void onLoginFailed(VolleyError error, JSONObject params)
    {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
        mProgressDialog.hide();
    }

    public void onValidateFaild()
    {
        Toast.makeText(getBaseContext(), "Validation failed", Toast.LENGTH_LONG).show();
        _loginButton.setEnabled(true);
    }

    private void sendLoginRequest(final JSONObject loginJsonParams)
    {
        showProgressDialog();
        int requestMethod = Request.Method.POST;
        VolleyInternetOperator.responseFunctionInterface loginSuccessFun =
                new VolleyInternetOperator.responseFunctionInterface() {
                    @Override
                    public void apply(Context context, JSONObject response, JSONObject params)
                    {
                        onLoginSuccess(context, response, params);
                    }
                };
        VolleyInternetOperator.errorFunctionInterface loginFailedFun =
                new VolleyInternetOperator.errorFunctionInterface() {
                    @Override
                    public void apply(VolleyError error, JSONObject params)
                    {
                        onLoginFailed(error, params);
                    }
                };
        VolleyInternetOperator.accessWebsiteWithVolley(this, baseAccessURL + signInURL,
                requestMethod, loginJsonParams, null, loginSuccessFun, loginFailedFun);
    }

    public void login() {
        Log.d(TAG, "Login");
        if (!validate()) {
            onValidateFaild();
            return;
        }
        _loginButton.setEnabled(false);
        String email = _emailText.getText().toString().trim();
        String password = _passwordText.getText().toString().trim();
        JSONObject loginJsonParams = new JSONObject();
        try {
            loginJsonParams.put("email", email);
            loginJsonParams.put("password", password);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendLoginRequest(loginJsonParams);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {
                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
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
        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _emailText.setError("enter a valid email address");
            valid = false;
        } else {
            _emailText.setError(null);
        }
        if (password.isEmpty() || password.length() < 4 || password.length() > 10) {
            _passwordText.setError("between 4 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordText.setError(null);
        }
        return valid;
    }
}