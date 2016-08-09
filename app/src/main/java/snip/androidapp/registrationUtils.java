package snip.androidapp;

import android.app.ProgressDialog;
import android.content.Context;
import android.widget.EditText;

/**
 * Created by ranihorev on 09/08/2016.
 */
public class RegistrationUtils {

    public static boolean validatePassword(EditText passEditText) {
        int min_chars = 6;
        int max_chars = 20;
        String password = passEditText.getText().toString();
        if (password.isEmpty() || password.length() < min_chars || password.length() > max_chars) {
            passEditText.setError("Between " + Integer.toString(min_chars) + " and " +
                    Integer.toString(max_chars) + " alphanumeric characters");
            return false;
        } else {
            passEditText.setError(null);
            return true;
        }
    }
    public static boolean validateEmail(EditText emailEditText) {
        String email = emailEditText.getText().toString();
        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.setError("Enter a valid email address");
            return false;
        } else {
            emailEditText.setError(null);
            return true;
        }
    }

    public static ProgressDialog showProgressDialog(Context context, int themeId, String text) {
        ProgressDialog mProgressDialog = new ProgressDialog(context, themeId);
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setMessage(text);
        mProgressDialog.show();
        return mProgressDialog;
    }
}
