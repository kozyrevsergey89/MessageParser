package com.kozyrev.testmessenger;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {

  private static final String TAG = MainActivity.class.getSimpleName();
  private static Parser parser = new Parser();

  private List<String> terms = new ArrayList<>();
  private SolutionAdapter solutionAdapter;
  private RecyclerView recyclerView;
  private ProgressBar progress;
  private Handler workerHandler;
  private Handler uiHandler;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    View sendBtn = findViewById(R.id.chat_send);
    final EditText editText = (EditText) findViewById(R.id.chat_text);
    sendBtn.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(final View view) {
        if (editText.getText().length() != 0) {
          showProgress(true);
          Message message = workerHandler.obtainMessage();
          message.obj = editText.getText().toString();
          workerHandler.sendMessage(message);
          editText.getText().clear();
        }
      }
    });
    recyclerView = (RecyclerView) findViewById(R.id.outputs_recycler_view);
    progress = (ProgressBar) findViewById(R.id.circle_progress);
    recyclerView.setLayoutManager(new LinearLayoutManager(this));
    solutionAdapter = new SolutionAdapter(terms);
    recyclerView.setAdapter(solutionAdapter);
    recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

    HandlerThread workerThread = new HandlerThread("WorkerThread");
    workerThread.start();
    uiHandler = new Handler(new Handler.Callback() {
      @Override public boolean handleMessage(Message msg) {
        showProgress(false);
        String objectOfInterest = (String) msg.obj;
        if (objectOfInterest != null) {
          terms.add(0, objectOfInterest);
          solutionAdapter.notifyItemInserted(0);
        }
        return true;
      }
    });
    workerHandler = new Handler(workerThread.getLooper(), new Handler.Callback() {
      @Override public boolean handleMessage(Message msg) {
        JSONObject jsonObject = null;
        try {
          jsonObject = parser.parseJsonMessage((String) msg.obj);
        } catch (JSONException e) {
          Log.e(TAG, "incorrect json", e);
        }
        Message uiMessage = uiHandler.obtainMessage();
        uiMessage.obj = (jsonObject != null) ? jsonObject.toString().replaceAll("\\\\/", "/") : null;
        uiHandler.sendMessage(uiMessage);
        return true;
      }
    });
  }

  private void showProgress(boolean showProgress) {
    if (showProgress) {
      progress.setVisibility(View.VISIBLE);
      recyclerView.setVisibility(View.GONE);
    } else {
      progress.setVisibility(View.GONE);
      recyclerView.setVisibility(View.VISIBLE);
    }
  }

  private static class SolutionAdapter extends RecyclerView.Adapter<SolutionAdapter.SolutionViewHolder> {

    private List<String> data;

    private SolutionAdapter(final List<String> data) {
      this.data = data;
    }

    @Override public SolutionViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
      TextView v = (TextView) LayoutInflater.from(parent.getContext())
        .inflate(R.layout.text_view_item, parent, false);
      return new SolutionViewHolder(v);
    }

    @Override public void onBindViewHolder(final SolutionViewHolder holder, final int position) {
      holder.mTextView.setText(data.get(position));
    }

    @Override public int getItemCount() {
      return (data != null) ? data.size() : 0;
    }

    static class SolutionViewHolder extends RecyclerView.ViewHolder {
      TextView mTextView;

      SolutionViewHolder(TextView v) {
        super(v);
        mTextView = v;
      }
    }
  }
}
