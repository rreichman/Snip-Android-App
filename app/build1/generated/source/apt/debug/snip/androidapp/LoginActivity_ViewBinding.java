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

public class LoginActivity_ViewBinding<T extends LoginActivity> implements Unbinder {
  protected T target;

  public LoginActivity_ViewBinding(T target, Finder finder, Object source, Resources res) {
    this.target = target;

    GenericSnipActivity_ViewBinder.bindToTarget(target, res);

    target._emailText = finder.findRequiredViewAsType(source, R.id.input_email_signin, "field '_emailText'", EditText.class);
    target._passwordText = finder.findRequiredViewAsType(source, R.id.input_password_signin, "field '_passwordText'", EditText.class);
    target._loginButton = finder.findRequiredViewAsType(source, R.id.btn_login, "field '_loginButton'", Button.class);
    target._signupLink = finder.findRequiredViewAsType(source, R.id.link_signup, "field '_signupLink'", TextView.class);

    target.baseAccessURL = res.getString(R.string.baseAccessURL);
    target.signInURL = res.getString(R.string.signInURL);
    target.emailField = res.getString(R.string.emailField);
    target.signInProgressBarText = res.getString(R.string.signInProgressBar);
    target.passField = res.getString(R.string.passField);
    target.tokenField = res.getString(R.string.tokenField);
    target.userTokenFile = res.getString(R.string.userTokenFile);
    target.userEmailFile = res.getString(R.string.userEmailFile);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target._emailText = null;
    target._passwordText = null;
    target._loginButton = null;
    target._signupLink = null;

    this.target = null;
  }
}
