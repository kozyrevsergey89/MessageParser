package com.kozyrev.testmessenger;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.view.View;

class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {

  private final int verticalSpaceHeight;

  VerticalSpaceItemDecoration(int verticalSpaceHeight) {
    this.verticalSpaceHeight = verticalSpaceHeight;
  }

  @Override
  public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
      RecyclerView.State state) {
    outRect.bottom = verticalSpaceHeight;
  }
}
