package com.kozyrev.testmessenger;

import android.app.ActivityManager;
import android.content.Context;
import android.support.test.espresso.IdlingResource;

/**
 * IdlingResource to let Espresso know that app idle state depends on MessageService state
 * @see MessageService
 */
class MessageServiceIdlingResource implements IdlingResource {

  private final Context context;
  private ResourceCallback resourceCallback;

  MessageServiceIdlingResource(Context context) {
    this.context = context;
  }

  @Override
  public String getName() {
    return MessageServiceIdlingResource.class.getName();
  }

  @Override
  public boolean isIdleNow() {
    boolean idle = !isIntentServiceRunning();
    if (idle && resourceCallback != null) {
      resourceCallback.onTransitionToIdle();
    }
    return idle;
  }

  @Override
  public void registerIdleTransitionCallback(ResourceCallback resourceCallback) {
    this.resourceCallback = resourceCallback;
  }

  private boolean isIntentServiceRunning() {
    ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
    for (ActivityManager.RunningServiceInfo info : manager.getRunningServices(Integer.MAX_VALUE)) {
      if (MessageService.class.getName().equals(info.service.getClassName())) {
        return true;
      }
    }
    return false;
  }
}
