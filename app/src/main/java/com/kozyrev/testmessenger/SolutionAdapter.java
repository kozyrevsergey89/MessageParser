package com.kozyrev.testmessenger;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

class SolutionAdapter extends RecyclerView.Adapter<SolutionAdapter.SolutionViewHolder> {

  private List<String> data;

  SolutionAdapter() {
    data = new ArrayList<>();
  }

  @Override public SolutionViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
    ViewGroup v = (ViewGroup) LayoutInflater.from(parent.getContext())
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

    SolutionViewHolder(ViewGroup v) {
      super(v);
      mTextView = (TextView) v.findViewById(R.id.text_content);
    }
  }

  void insertElement(String element){
    data.add(element);
    notifyItemInserted(getItemCount()-1);
  }
}
