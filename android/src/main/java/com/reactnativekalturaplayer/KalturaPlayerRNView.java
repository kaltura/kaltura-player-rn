package com.reactnativekalturaplayer;

import android.text.TextUtils;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.kaltura.playkit.PKLog;
import com.kaltura.tvplayer.KalturaPlayer;

public class KalturaPlayerRNView extends FrameLayout {

   private final PKLog log = PKLog.get(KalturaPlayerRNView.class.getSimpleName());

   private KalturaPlayer.Type playerType;

   public KalturaPlayerRNView(@NonNull ReactApplicationContext context) {
      super(context);
   }

   @Override
   public void requestLayout() {
      super.requestLayout();
      reMeasureAndReLayout();
   }

   public void reMeasureAndReLayout() {
      post(measureAndLayout);
   }

   private final Runnable measureAndLayout = () -> {
      // This view relies on a measure + layout pass happening after it calls requestLayout().
      // https://github.com/facebook/react-native/issues/17968#issuecomment-721958427
      measure(
              MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
              MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
      layout(getLeft(), getTop(), getRight(), getBottom());
   };

   protected void setPlayerType(String playerType) {
      this.playerType = getKalturaPlayerType(playerType);
   }

   public KalturaPlayer.Type getPlayerType() {
      return this.playerType;
   }

   private KalturaPlayer.Type getKalturaPlayerType(String playerType) {
      if (TextUtils.equals(playerType, KalturaPlayer.Type.basic.name())) {
         return KalturaPlayer.Type.basic;
      } else if (TextUtils.equals(playerType, KalturaPlayer.Type.ovp.name())) {
         return KalturaPlayer.Type.ovp;
      }
      return KalturaPlayer.Type.ott;
   }
}

