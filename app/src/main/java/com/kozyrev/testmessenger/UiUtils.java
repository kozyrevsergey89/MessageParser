package com.kozyrev.testmessenger;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;
import java.lang.ref.WeakReference;

@SuppressWarnings({ "UtilityClass", "WeakerAccess" }) public final class UiUtils {

  private UiUtils() {
  }

  public static void showWhiteSnackbar(@NonNull View view, CharSequence text) {
    createWhiteSnackBar(view, text).show();
  }

  public static void showProgress(View progressView, View viewToHide) {
    if (progressView != null && viewToHide != null) {
      fadeOut(viewToHide);
      fadeIn(progressView);
    }
  }

  public static void hideProgress(View progressView, View viewToShow) {
    if (progressView != null && viewToShow != null) {
      fadeOut(progressView);
      fadeIn(viewToShow);
    }
  }

  public static void fadeOut(View viewToHide) {
    int shortAnimTime = viewToHide.getResources().getInteger(android.R.integer.config_shortAnimTime);
    fadeOut(viewToHide, shortAnimTime);
  }

  public static void fadeOut(View viewToHide, int animTime) {
    if (viewToHide.getVisibility() == View.GONE) {
      return;
    }
    viewToHide.setVisibility(View.VISIBLE);
    ObjectAnimator fadeOut = ObjectAnimator.ofFloat(viewToHide, "alpha", 1f, 0f);
    fadeOut.setDuration(animTime);
    fadeOut.addListener(new InOutAnimationListenerAdapter(false, viewToHide));
    fadeOut.start();
  }

  public static void fadeIn(View viewToShow) {
    int shortAnimTime = viewToShow.getResources().getInteger(android.R.integer.config_shortAnimTime);
    fadeIn(viewToShow, shortAnimTime);
  }

  public static void fadeIn(View viewToShow, int animTime) {
    if (viewToShow.getVisibility() == View.VISIBLE) {
      return;
    }
    viewToShow.setVisibility(View.VISIBLE);
    ObjectAnimator fadeIn = ObjectAnimator.ofFloat(viewToShow, "alpha", 0f, 1f);
    fadeIn.addListener(new InOutAnimationListenerAdapter(true, viewToShow));
    fadeIn.setDuration(animTime);
    fadeIn.start();
  }

  private static Snackbar createWhiteSnackBar(@NonNull View view, CharSequence text) {
    Snackbar snack = Snackbar.make(view, text, Snackbar.LENGTH_LONG);
    ((TextView) snack.getView()
        .findViewById(android.support.design.R.id.snackbar_text)).setTextColor(Color.WHITE);
    return snack;
  }

  private static final class InOutAnimationListenerAdapter extends AnimatorListenerAdapter {
    private final boolean mShouldShow;
    private final WeakReference<View> mViewRef;

    InOutAnimationListenerAdapter(boolean shouldShow, View view) {
      super();
      this.mShouldShow = shouldShow;
      this.mViewRef = new WeakReference<>(view);
    }

    @Override public void onAnimationEnd(Animator animation) {
      final View view = mViewRef.get();
      if (view != null) {
        view.setVisibility(mShouldShow ? View.VISIBLE : View.GONE);
      }
    }
  }
}
