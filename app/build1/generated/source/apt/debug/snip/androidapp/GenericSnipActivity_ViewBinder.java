// Generated code from Butter Knife. Do not modify!
package snip.androidapp;

import android.content.res.Resources;
import butterknife.Unbinder;
import butterknife.internal.Finder;
import butterknife.internal.ViewBinder;
import java.lang.Object;
import java.lang.Override;

public final class GenericSnipActivity_ViewBinder implements ViewBinder<GenericSnipActivity> {
  @Override
  public Unbinder bind(Finder finder, GenericSnipActivity target, Object source) {
    Resources res = finder.getContext(source).getResources();
    bindToTarget(target, res);
    return Unbinder.EMPTY;
  }

  public static void bindToTarget(GenericSnipActivity target, Resources res) {
    target.userEmailFile = res.getString(R.string.userEmailFile);
  }
}
