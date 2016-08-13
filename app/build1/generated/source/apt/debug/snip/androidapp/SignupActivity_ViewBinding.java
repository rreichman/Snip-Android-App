// Generated code from Butter Knife. Do not modify!
package snip.androidapp;

import android.content.res.Resources;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import java.lang.IllegalStateException;
import java.lang.Object;
import java.lang.Override;

public class SignupActivity_ViewBinding<T extends SignupActivity> implements Unbinder {
  protected T target;

  public SignupActivity_ViewBinding(T target, Finder finder, Object source, Resources res) {
    this.target = target;

    target._emailText = finder.findRequiredViewAsType(source, R.id.input_email_signup, "field '_emailText'", EditText.class);
    target._passwordText1 = finder.findRequiredViewAsType(source, R.id.input_password_signup1, "field '_passwordText1'", EditText.class);
    target._passwordText2 = finder.findRequiredViewAsType(source, R.id.input_password_signup2, "field '_passwordText2'", EditText.class);
    target._signupButton = finder.findRequiredViewAsType(source, R.id.btn_signup, "field '_signupButton'", Button.class);
    target._loginLink = finder.findRequiredViewAsType(source, R.id.link_login, "field '_loginLink'", TextView.class);

    target.baseAccessURL = res.getString(R.string.baseAccessURL);
    target.signUpURL = res.getString(R.string.signupURL);
    target.emailField = res.getString(R.string.emailField);
    target.signupProgressBarText = res.getString(R.string.signupProgressBar);
    target.passField = res.getString(R.string.passField);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target._emailText = null;
    target._passwordText1 = null;
    target._passwordText2 = null;
    target._signupButton = null;
    target._loginLink = null;

    this.target = null;
  }
}
