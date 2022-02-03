package com.kalturaplayerrn;

import android.content.Context;
import android.widget.FrameLayout;

public class PlayerView extends FrameLayout {
  public PlayerView(Context context) {
    super(context);
  }

  @Override
  protected void onAttachedToWindow() {
    super.onAttachedToWindow();

    // addView(PKPlayerWrapper.player.getPlayerView());
  }

  @Override
  protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();

    // removeView(PKPlayerWrapper.player.getPlayerView());
  }
}
