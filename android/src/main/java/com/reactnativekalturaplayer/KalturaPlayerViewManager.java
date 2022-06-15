package com.reactnativekalturaplayer;

import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.kaltura.playkit.PKLog;

public class KalturaPlayerViewManager extends ViewGroupManager<KalturaPlayerRNView> {

   private PKLog log = PKLog.get(KalturaPlayerViewManager.class.getSimpleName());
   private static final String PLAYER_CLASS = "KalturaPlayerView";
   private KalturaPlayerRNView kalturaPlayerRNView;

   private static final String PROP_PLAYER_TYPE = "playerType";

   public KalturaPlayerViewManager(ReactApplicationContext reactContext) {
      kalturaPlayerRNView = new KalturaPlayerRNView(reactContext);
   }

   @NonNull
   @Override
   public String getName() {
      return PLAYER_CLASS;
   }

   @NonNull
   @Override
   protected KalturaPlayerRNView createViewInstance(@NonNull ThemedReactContext reactContext) {
      return kalturaPlayerRNView;
   }

   @Override
   public void onDropViewInstance(@NonNull KalturaPlayerRNView view) {
      // Cleanup player resources
      super.onDropViewInstance(view);
      //view.destroy();
      log.d("onDropViewInstance");
   }

   @NonNull
   public KalturaPlayerRNView getKalturaPlayerView() { // <-- returns the View instance
      return kalturaPlayerRNView;
   }

   @ReactProp(name = PROP_PLAYER_TYPE)
   public void setPlayerType(KalturaPlayerRNView kalturaPlayerRNView, String playerType) {
      log.d("setPlayerType playerType " + playerType);
      if (!TextUtils.isEmpty(playerType)) {
         kalturaPlayerRNView.setPlayerType(playerType);
      }
   }
}
