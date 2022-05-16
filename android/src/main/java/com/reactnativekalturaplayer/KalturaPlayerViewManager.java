package com.reactnativekalturaplayer;

import android.annotation.SuppressLint;
import android.text.TextUtils;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.uimanager.ThemedReactContext;
import com.facebook.react.uimanager.ViewGroupManager;
import com.facebook.react.uimanager.annotations.ReactProp;
import com.kaltura.playkit.PKLog;
import com.npaw.youbora.lib6.YouboraLog;

public class KalturaPlayerViewManager extends ViewGroupManager<KalturaPlayerRNView> {

   private ReactApplicationContext context;
   private static final String PLAYER_CLASS = "KalturaPlayerView";
   private PKLog log = PKLog.get(KalturaPlayerRNView.class.getSimpleName());

   private static final String PROP_PARTNER_ID = "partnerId";
   private static final String PROP_PLAYER_TYPE = "playerType";
   private static final String PROP_PLAYER_INIT_OPTIONS = "playerInitOptions";
   private static final String PROP_ASSET_ID = "assetId";
   private static final String PROP_MEDIA_ASSET = "mediaAsset";
   private static final String PROP_LOAD = "load";
   private static final String PROP_UPDATE_PLUGIN_CONFIG = "updatePluginConfig";
   private static final String PROP_ADD_LISTENERS = "addListeners";
   private static final String PROP_REMOVE_LISTENERS = "removeListeners";
   private static final String PROP_ON_APPLICATION_PAUSE = "onApplicationPaused";
   private static final String PROP_ON_APPLICATION_RESUME = "onApplicationResumed";
   private static final String PROP_ON_DESTROY = "onDestroy";
   private static final String PROP_PLAY = "play";
   private static final String PROP_PAUSE = "pause";
   private static final String PROP_REPLAY = "replay";
   private static final String PROP_SEEK = "seek";
   private static final String PROP_CHANGE_TRACK = "changeTrack";
   private static final String PROP_PLAYBACK_RATE = "playbackRate";
   private static final String PROP_STOP = "stop";
   private static final String PROP_SET_AUTO_PLAY = "autoPlay";
   private static final String PROP_SET_KS = "ks";
   private static final String PROP_SET_Z_INDEX = "zIndex";
   private static final String PROP_SET_VOLUME = "volume";

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
   }

   @Override
   public void onDropViewInstance(@NonNull KalturaPlayerRNView view) {
      // Cleanup player resources
      super.onDropViewInstance(view);
      if (view != null) {
         view.destroy();
      }
   }

   @ReactProp(name = PROP_PARTNER_ID)
   public void setPartnerId(KalturaPlayerRNView kalturaPlayerRNView, int partnerId) {
      log.d("setPartnerId partnerId " + partnerId);
      kalturaPlayerRNView.setPartnerId(partnerId);
   }

   @ReactProp(name = PROP_PLAYER_TYPE)
   public void setPlayerType(KalturaPlayerRNView kalturaPlayerRNView, String playerType) {
      log.d("setPlayerType playerType " + playerType);
      if (!TextUtils.isEmpty(playerType)) {
         kalturaPlayerRNView.setPlayerType(playerType);
      }
   }

   @ReactProp(name = PROP_PLAYER_INIT_OPTIONS)
   public void setPlayerInitOptions(KalturaPlayerRNView kalturaPlayerRNView, String playerInitOptions) {
      log.d("setPlayerInitOptions playerInitOptions Json is " + playerInitOptions);
      kalturaPlayerRNView.createPlayerInstance(playerInitOptions);
   }

   @ReactProp(name = PROP_ASSET_ID)
   public void setAssetId(KalturaPlayerRNView kalturaPlayerRNView, String assetId) {
      log.d("setAssetId assetId " + assetId);
      if (!TextUtils.isEmpty(assetId)) {
         kalturaPlayerRNView.setAssetId(assetId);
      } else {
         log.d("AssetId is invalid which is " + assetId);
      }
   }

   @ReactProp(name = PROP_MEDIA_ASSET)
   public void setMediaAsset(KalturaPlayerRNView kalturaPlayerRNView, String mediaAsset) {
      log.d("loadMedia mediaAsset Json is " + mediaAsset);
      if (!TextUtils.isEmpty(mediaAsset)) {
         kalturaPlayerRNView.setMediaAsset(mediaAsset);
      } else {
         log.d("mediaAsset is invalid which is " + mediaAsset);
      }
   }

   @ReactProp(name = PROP_LOAD)
   public void load(KalturaPlayerRNView kalturaPlayerRNView, boolean autoPlay) {
      log.d("preparePlayer autoPlay " + autoPlay);
      YouboraLog.setDebugLevel(YouboraLog.Level.VERBOSE);
      kalturaPlayerRNView.load(true);
   }

   @ReactProp(name = PROP_UPDATE_PLUGIN_CONFIG)
   public void updatePluginConfig(KalturaPlayerRNView kalturaPlayerRNView, String updatedPluginConfig) {
      log.d("updatePluginConfig " + updatedPluginConfig);
      kalturaPlayerRNView.updatePluginConfigs(updatedPluginConfig);
   }

   @ReactProp(name = PROP_ADD_LISTENERS)
   public void addPlayerListeners(KalturaPlayerRNView kalturaPlayerRNView, boolean autoPlay) {
      log.d("addPlayerListeners");
      kalturaPlayerRNView.addKalturaPlayerListeners();
   }

   @ReactProp(name = PROP_REMOVE_LISTENERS)
   public void removePlayerListeners(KalturaPlayerRNView kalturaPlayerRNView, boolean autoPlay) {
      log.d("removePlayerListeners");
      kalturaPlayerRNView.removePlayerListeners();
   }

   @ReactProp(name = PROP_ON_APPLICATION_PAUSE)
   public void onApplicationPaused(KalturaPlayerRNView kalturaPlayerRNView, boolean isPause) {
      log.d("onApplicationPaused");
      kalturaPlayerRNView.onApplicationPaused();
   }

   @ReactProp(name = PROP_ON_APPLICATION_RESUME)
   public void onApplicationResumed(KalturaPlayerRNView kalturaPlayerRNView, boolean isResume) {
      log.d("onApplicationResumed");
      kalturaPlayerRNView.onApplicationResumed();
   }

   @ReactProp(name = PROP_ON_DESTROY)
   public void onApplicationDestroyed(KalturaPlayerRNView kalturaPlayerRNView, boolean isDestroyed) {
      log.d("onApplicationDestroyed");
      kalturaPlayerRNView.destroy();
   }

   @ReactProp(name = PROP_PLAY)
   public void play(KalturaPlayerRNView kalturaPlayerRNView, boolean isPlay) {
      log.d("play " + isPlay);
      if (isPlay) {
         kalturaPlayerRNView.play();
      }
   }

   @ReactProp(name = PROP_PAUSE)
   public void pause(KalturaPlayerRNView kalturaPlayerRNView, boolean isPause) {
      log.d("pause " + isPause);
      if (isPause) {
         kalturaPlayerRNView.pause();
      }
   }

   @ReactProp(name = PROP_STOP)
   public void stop(KalturaPlayerRNView kalturaPlayerRNView, boolean isStop) {
      log.d("stop " + isStop);
      if (isStop) {
         kalturaPlayerRNView.stop();
      }
   }

   @ReactProp(name = PROP_REPLAY)
   public void replay(KalturaPlayerRNView kalturaPlayerRNView, boolean isReplay) {
      log.d("replay " + isReplay);
      if (isReplay) {
         kalturaPlayerRNView.replay();
      }
   }

   @ReactProp(name = PROP_SEEK)
   public void seekTo(KalturaPlayerRNView kalturaPlayerRNView, float position) {
      log.d("seekTo " + position);
      if (position > 0f) {
         kalturaPlayerRNView.seekTo(position);
      } else {
         log.d("Invalid seek position which is " + position);
      }
   }

   @ReactProp(name = PROP_CHANGE_TRACK)
   public void changeTrack(KalturaPlayerRNView kalturaPlayerRNView, String trackId) {
      log.d("changeTrack " + trackId);
      if (!TextUtils.isEmpty(trackId)) {
         kalturaPlayerRNView.changeTrack(trackId);
      } else {
         log.d("Invalid trackId for changeTrack which is " + trackId);
      }
   }

   @ReactProp(name = PROP_PLAYBACK_RATE)
   public void setPlaybackRate(KalturaPlayerRNView kalturaPlayerRNView, float rate) {
      log.d("setPlaybackRate " + rate);
      if (rate > 0f) {
         kalturaPlayerRNView.changePlaybackRate(rate);
      } else {
         log.d("Invalid playback rate which is " + rate);
      }
   }

   @ReactProp(name = PROP_SET_AUTO_PLAY)
   public void setAutoPlay(KalturaPlayerRNView kalturaPlayerRNView, boolean isAutoPlay) {
      log.d("setAutoPlay " + isAutoPlay);
      if (isAutoPlay) {
         kalturaPlayerRNView.setAutoplay(isAutoPlay);
      }
   }

   @ReactProp(name = PROP_SET_KS)
   public void setKs(KalturaPlayerRNView kalturaPlayerRNView, String ks) {
      log.d("setKs " + ks);
      if (!TextUtils.isEmpty(ks)) {
         kalturaPlayerRNView.setKS(ks);
      } else {
         log.d("Invalid KS which is " + ks);
      }
   }

   @ReactProp(name = PROP_SET_Z_INDEX)
   public void setZIndex(KalturaPlayerRNView kalturaPlayerRNView, float index) {
      log.d("setZIndex " + index);
      if (index > 0f) {
         kalturaPlayerRNView.setZIndex(index);
      } else {
         log.d("Invalid Z Index which is " + index);
      }
   }

   @ReactProp(name = PROP_SET_VOLUME)
   public void setVolume(KalturaPlayerRNView kalturaPlayerRNView, float volume) {
      log.d("setVolume " + volume);
      if (volume > 0f) {
         kalturaPlayerRNView.setZIndex(volume);
      } else {
         log.d("Invalid Volume which is " + volume);
      }
   }
}

