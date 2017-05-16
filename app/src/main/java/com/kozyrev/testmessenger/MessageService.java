package com.kozyrev.testmessenger;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.net.ConnectivityManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

public class MessageService extends IntentService {

  public static final String MESSAGE_SERVICE_PROGRESS = "MessageServiceProgress";
  private static final String ACTION_PARSE_MESSAGE = "com.kozyrev.testmessenger.action.PARSE_MESSAGE";
  private static final String EXTRA_TEXT_MESSAGE = "com.kozyrev.testmessenger.extra.TEXT_MESSAGE";
  private static final String TAG = MessageService.class.getSimpleName();

  private static Parser parser = new Parser();
  private Intent progressStickyIntent;

  public MessageService() {
    super("MessageService");
  }

  /**
   * Starts this service to perform action ACTION_PARSE_MESSAGE with the given parameters. If
   * the service is already performing a task this action will be queued.
   *
   * @see IntentService
   */
  public static void startActionParseMessage(Context context, String textMessage) {
    Intent intent = new Intent(context, MessageService.class);
    intent.setAction(ACTION_PARSE_MESSAGE);
    intent.putExtra(EXTRA_TEXT_MESSAGE, textMessage);
    context.startService(intent);
  }

  @Override public void onCreate() {
    super.onCreate();
    progressStickyIntent = new Intent(MESSAGE_SERVICE_PROGRESS);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    if (intent != null) {
      final String action = intent.getAction();
      if (ACTION_PARSE_MESSAGE.equals(action)) {
        sendStickyBroadcast(progressStickyIntent);
        final String textMessage = intent.getStringExtra(EXTRA_TEXT_MESSAGE);
        handleTextMessage(textMessage);
        removeStickyBroadcast(progressStickyIntent);
      }
    }
  }

  /**
   * Handle action ACTION_PARSE_MESSAGE in the provided background thread with the provided
   * parameters.
   */
  private void handleTextMessage(String textMessage) {
    JSONObject jsonObject = null;
    try {
      jsonObject = parser.parseJsonMessage(textMessage);
    } catch (JSONException e) {
      Log.e(TAG, "incorrect json", e);
    }
    Intent localIntent =
        new Intent(MainActivity.UI_MESSAGE_ACTION)
            .putExtra(MainActivity.UI_MESSAGE_TYPE, MainActivity.UI_OBJECT)
            .putExtra(MainActivity.UI_MESSAGE_JSON,
                (jsonObject != null) ? jsonObject.toString().replaceAll("\\\\/", "/") : null);
    LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    sendUserIsOfflineIfNeeded();
  }

  private void sendUserIsOfflineIfNeeded() {
    if (!isNetworkConnected()) {
      Intent localIntent =
          new Intent(MainActivity.UI_MESSAGE_ACTION).putExtra(MainActivity.UI_MESSAGE_TYPE, MainActivity.UI_OFFLINE);
      LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
  }

  private boolean isNetworkConnected() {
    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
    return cm.getActiveNetworkInfo() != null;
  }
}
