package com.kozyrev.testmessenger;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;

public class MainActivity extends AppCompatActivity {

  /**
   * Constants for intents communication
   */
  public static final String UI_MESSAGE_ACTION = "UI_MESSAGE_ACTION";
  public static final String UI_MESSAGE_TYPE = "UI_MESSAGE_TYPE";
  public static final String UI_OBJECT = "UI_OBJECT";
  public static final String UI_OFFLINE = "UI_OFFLINE";
  public static final String UI_MESSAGE_JSON = "UI_MESSAGE_JSON";

  private SolutionAdapter solutionAdapter;
  private ProgressBar progress;
  private View sendBtn;
  private Runnable stopProgressRunnable = new Runnable() {
    @Override public void run() {
      showProgress(false);
    }
  };
  private UiMessagesBroadcastReceiver uiMessagesBroadcastReceiver;
  private View.OnClickListener onClickListener;
  private RecyclerView recyclerView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    sendBtn = findViewById(R.id.chat_send);
    final EditText editText = (EditText) findViewById(R.id.chat_text);
    // for every non empty input we try to process it by sending in intent to MessageService
    onClickListener = new View.OnClickListener() {
      @Override public void onClick(final View view) {
        if (editText.getText().length() != 0) {
          showProgress(true);
          MessageService.startActionParseMessage(MainActivity.this, editText.getText().toString());
          editText.getText().clear();
        }
      }
    };
    sendBtn.setOnClickListener(onClickListener);
    recyclerView = (RecyclerView) findViewById(R.id.outputs_recycler_view);
    progress = (ProgressBar) findViewById(R.id.circle_progress);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    solutionAdapter = new SolutionAdapter();
    recyclerView.setAdapter(solutionAdapter);
    recyclerView.addItemDecoration(new VerticalSpaceItemDecoration(getResources().getDimensionPixelSize(R.dimen
        .space_decoration)));
  }

  @Override protected void onStart() {
    super.onStart();
    if (registerReceiver(null, new IntentFilter(MessageService.MESSAGE_SERVICE_PROGRESS)) != null){
      showProgress(true);
    } else {
      showProgress(false);
    }
    IntentFilter uiMessageIntentFilter = new IntentFilter(
        MainActivity.UI_MESSAGE_ACTION);
    uiMessagesBroadcastReceiver = new UiMessagesBroadcastReceiver(solutionAdapter, sendBtn, stopProgressRunnable, recyclerView);
    LocalBroadcastManager.getInstance(this).registerReceiver(
        uiMessagesBroadcastReceiver, uiMessageIntentFilter);
  }

  @Override protected void onStop() {
    super.onStop();
    LocalBroadcastManager.getInstance(this).unregisterReceiver(uiMessagesBroadcastReceiver);
  }

  private void showProgress(boolean showProgress) {
    if (showProgress) {
      UiUtils.showProgress(progress, sendBtn);
    } else {
      UiUtils.hideProgress(progress, sendBtn);
    }
  }

  private static class UiMessagesBroadcastReceiver extends BroadcastReceiver {

    private final SolutionAdapter solutionAdapter;
    private final View sendView;
    private final Runnable stopProgressRunnable;
    private final RecyclerView recyclerView;

    public UiMessagesBroadcastReceiver(SolutionAdapter solutionAdapter, View sendView, Runnable stopProgressRunnable,
        RecyclerView recyclerView) {
      super();
      this.solutionAdapter = solutionAdapter;
      this.sendView = sendView;
      this.stopProgressRunnable = stopProgressRunnable;
      this.recyclerView = recyclerView;
    }

    @Override public void onReceive(Context context, Intent intent) {
      String type = intent.getStringExtra(UI_MESSAGE_TYPE);
      if (UI_OFFLINE.equals(type)) {
        UiUtils.showWhiteSnackbar(sendView, context.getString(R.string.offline_label));
      } else if (UI_OBJECT.equals(type)) {
        sendView.postDelayed(stopProgressRunnable, context.getResources().getInteger(android.R.integer
            .config_shortAnimTime));
        if (!TextUtils.isEmpty(intent.getStringExtra(UI_MESSAGE_JSON))) {
          solutionAdapter.insertElement(intent.getStringExtra(UI_MESSAGE_JSON));
          recyclerView.smoothScrollToPosition(solutionAdapter.getItemCount()-1);
        }
      }
    }
  }
}
