package com.reactnativekalturaplayer;

import android.annotation.SuppressLint;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.kaltura.playkit.PKLog;

public class KalturaPlayerViewManager extends ViewGroupManager<KalturaPlayerRNView> {

   private ReactApplicationContext context;
   private static final String PLAYER_CLASS = "KalturaPlayerView";
   private PKLog log = PKLog.get(KalturaPlayerRNView.class.getSimpleName());

   private static final String PROP_PARTNER_ID = "partnerId";
   private static final String PROP_BASE_URL = "baseUrl";
   private static final String PROP_KS = "ks";
   private static final String PROP_ASSET_ID = "assetId";
   private static final String PROP_FORMAT = "format";
   private static final String PROP_PREPARE = "prepare";
   private static final String PROP_PLAY_PAUSE = "playPause";

   public KalturaPlayerViewManager(ReactApplicationContext reactContext) {
      context = reactContext;
   }

   @NonNull
   @Override
   public String getName() {
      return PLAYER_CLASS;
   }

   @SuppressLint("ResourceType")
   @NonNull
   @Override
   protected KalturaPlayerRNView createViewInstance(@NonNull ThemedReactContext reactContext) {
      return new KalturaPlayerRNView(reactContext);
      // DUMMY Frame layout for testing
//      FrameLayout frameLayout = new FrameLayout(context);
//      frameLayout.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//      frameLayout.setBackgroundColor(context.getResources().getColor(android.R.color.darker_gray));
//      frameLayout.setId(240689);
   }

   @Override
   public void onDropViewInstance(@NonNull KalturaPlayerRNView view) {
      // Cleanup player resources
      super.onDropViewInstance(view);
      if (view != null) {
         view.onApplicationDestroy();
      }
   }

   @ReactProp(name = PROP_PARTNER_ID)
   public void setPartnerId(KalturaPlayerRNView kalturaPlayerRNView, int partnerId) {
      log.d("setPartnerId partnerId " + partnerId);
      kalturaPlayerRNView.setPartnerId(partnerId);
   }

   @ReactProp(name = PROP_BASE_URL)
   public void setBaseUrl(KalturaPlayerRNView kalturaPlayerRNView, String baseUrl) {
      log.d("setBaseUrl baseUrl " + baseUrl);
      kalturaPlayerRNView.setBaseUrl(baseUrl);
   }

   @ReactProp(name = PROP_ASSET_ID)
   public void setAssetId(KalturaPlayerRNView kalturaPlayerRNView, String assetId) {
      log.d("setAssetId assetId " + assetId);
      kalturaPlayerRNView.setAssetId(assetId);
   }

   @ReactProp(name = PROP_KS)
   public void setKs(KalturaPlayerRNView kalturaPlayerRNView, String ks) {
      log.d("setKS ks " + ks);
      kalturaPlayerRNView.setKs(ks);
   }

   @ReactProp(name = PROP_PREPARE)
   public void preparePlayer(KalturaPlayerRNView kalturaPlayerRNView, boolean autoPlay) {
      log.d("preparePlayer autoPlay " + autoPlay);
      kalturaPlayerRNView.prepare(true);
   }

   @ReactProp(name = PROP_PLAY_PAUSE)
   public void pauseOrPlay(KalturaPlayerRNView kalturaPlayerRNView, boolean isPlay) {
      log.d("pauseOrPlay isPlay " + isPlay);
      kalturaPlayerRNView.pauseOrPlayPlayer(isPlay);
   }
}

