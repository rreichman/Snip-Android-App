// Generated code from Butter Knife. Do not modify!
package snip.androidapp;

import android.content.res.Resources;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import butterknife.internal.ViewBinder;
import java.lang.Object;
import java.lang.Override;

public final class LoginActivity_ViewBinder implements ViewBinder<LoginActivity> {
  @Override
  public Unbinder bind(Finder finder, LoginActivity target, Object source) {
    Resources res = finder.getContext(source).getResources();
    return new LoginActivity_ViewBinding<>(target, finder, source, res);
  }
}
