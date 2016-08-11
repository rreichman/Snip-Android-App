package snip.androidapp;

/**
 * Created by ranihorev on 07/08/2016.
 */
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindString;
import butterknife.ButterKnife;
import butterknife.BindView;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SignupActivity";
    private ProgressDialog mProgressDialog;

    @BindView(R.id.input_email_signup) EditText _emailText;
    @BindView(R.id.input_password_signup1) EditText _passwordText1;
    @BindView(R.id.input_password_signup2) EditText _passwordText2;
    @BindView(R.id.btn_signup) Button _signupButton;
    @BindView(R.id.link_login) TextView _loginLink;
    @BindString(R.string.baseAccessURL) String baseAccessURL;
    @BindString(R.string.signupURL) String signUpURL;
    @BindString(R.string.emailField) String emailField;
    @BindString(R.string.signupProgressBar) String signupProgressBarText;
    @BindString(R.string.passField) String passField;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _signupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signup();
            }
        });

        _loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Finish the registration screen and return to the Login activity
                finish();
            }
        });
    }

    public void signup() {
        Log.d(TAG, "Signup");

        if (!validate()) {
            onSignupFailed(null);
            return;
        }

        _signupButton.setEnabled(false);

        String email = _emailText.getText().toString().trim();
        String password1 = _passwordText1.getText().toString().trim();
        String password2 = _passwordText2.getText().toString().trim();
        JSONObject signupJsonParams = new JSONObject();
        try {
            signupJsonParams.put(emailField, email);
            signupJsonParams.put(passField + "1", password1);
            signupJsonParams.put(passField + "2", password2);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendSignupRequest(signupJsonParams);

    }

    private void sendSignupRequest(final JSONObject signupJsonParams)
    {
        mProgressDialog = RegistrationUtils.showProgressDialog(this, R.style.TempAppTheme_Dark_Dialog, signupProgressBarText);
        int requestMethod = Request.Method.POST;
        VolleyInternetOperator.responseFunctionInterface successFun =
                new VolleyInternetOperator.responseFunctionInterface() {
                    @Override
                    public void apply(Context context, JSONObject response, JSONObject params)
                    {
                        onSignupSuccess();
                    }
                };
        VolleyInternetOperator.errorFunctionInterface failedFun =
                new VolleyInternetOperator.errorFunctionInterface() {
                    @Override
                    public void apply(Context context, VolleyError error, JSONObject params)
                    {
                        onSignupFailed(error);
                    }
                };
        VolleyInternetOperator.accessWebsiteWithVolley(this, baseAccessURL + signUpURL,
                requestMethod, signupJsonParams, null, successFun, failedFun);
    }



    public void onSignupSuccess() {
        Toast.makeText(getBaseContext(), "Thanks for signing up. Please check your email for confirmation!", Toast.LENGTH_LONG).show();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(emailField,_emailText.getText().toString());
        returnIntent.putExtra(passField,_passwordText1.getText().toString());
        setResult(RESULT_OK,returnIntent);
        finish();
    }

    public void onSignupFailed(VolleyError error) {
        if (null != error) {
            Log.d("signUp error", VolleyInternetOperator.parseNetworkErrorResponse(error));
        }
        Toast.makeText(getBaseContext(), "Signup failed", Toast.LENGTH_LONG).show();
        if (null != mProgressDialog) {
            mProgressDialog.hide();
        }
        _signupButton.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;
        if (!RegistrationUtils.validateEmail(_emailText)) {valid = false;}
        if (!RegistrationUtils.validatePassword(_passwordText1)) {valid = false;}
        if (!RegistrationUtils.validatePassword(_passwordText2)) {valid = false;}
        return valid;
    }


}