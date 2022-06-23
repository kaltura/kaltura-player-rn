package com.reactnativekalturaplayer;

import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;

public class KalturaPlayerRNView extends FrameLayout {

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
}

