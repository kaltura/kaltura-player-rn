package com.reactnativekalturaplayer;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.uimanager.ThemedReactContext;
import com.google.gson.Gson;
import com.kaltura.playkit.PKLog;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.player.LoadControlBuffers;
import com.kaltura.playkit.player.PKHttpClientManager;
import com.kaltura.playkit.providers.api.phoenix.APIDefines;
import com.kaltura.playkit.providers.ott.OTTMediaAsset;
import com.kaltura.playkit.providers.ott.PhoenixMediaProvider;
import com.kaltura.tvplayer.KalturaOttPlayer;
import com.kaltura.tvplayer.KalturaPlayer;
import com.kaltura.tvplayer.OTTMediaOptions;
import com.kaltura.tvplayer.PlayerInitOptions;
import com.reactnativekalturaplayer.model.InitOptions;
import com.reactnativekalturaplayer.model.MediaAsset;
import com.reactnativekalturaplayer.model.NetworkSettings;

import java.util.Collections;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

public class KalturaPlayerRNView extends FrameLayout {

   private final PKLog log = PKLog.get(KalturaPlayerRNView.class.getSimpleName());
   private final ThemedReactContext context;

   private KalturaPlayer player;

   private int partnerId;
   private String baseUrl;
   private String ks;
   private String assetId;

   public KalturaPlayerRNView(@NonNull ThemedReactContext context) {
      super(context);
      this.context = context;
   }

   protected void setPartnerId(int partnerId) {
      this.partnerId = partnerId;
   }

   protected void setBaseUrl(String baseUrl) {
      this.baseUrl = baseUrl;
   }

   protected void setKs(String ks) {
      this.ks = ks;
   }

   protected void setAssetId(String assetId) {
      this.assetId = assetId;
   }

   protected void setFormat(String format) {

   }

   protected void prepare(boolean autoPlay) {
      setup(partnerId);
      load(assetId);
   }

   private void setup(int partnerId) {
      log.d("setup partnerId: " + partnerId);

//      Gson gson = new Gson();
//      InitOptions initOptionsModel = gson.fromJson(options, InitOptions.class);
//      if (initOptionsModel == null) {
//         return null;
//      }

//      if (initOptionsModel.warmupUrls != null && !initOptionsModel.warmupUrls.isEmpty()) {
//         PKHttpClientManager.setHttpProvider("okhttp");
//         PKHttpClientManager.warmUp((initOptionsModel.warmupUrls).toArray((new String[0])));
//      }

      // load the player and put it in the main frame
      KalturaOttPlayer.initialize(context, partnerId, baseUrl);
      PKPluginConfigs pluginConfigs = new PKPluginConfigs();
//      if (initOptionsModel.plugins != null) {
//         if (initOptionsModel.plugins.ima != null) {
//            createIMAPlugin(pluginConfigs, initOptionsModel.plugins.ima); //DEFAULT
//         }
//
//         if (initOptionsModel.plugins.youbora != null) {
//            JsonObject youboraConfigJson = initOptionsModel.plugins.youbora;
//            if (youboraConfigJson.has(YOUBORA_ACCOUNT_CODE) && youboraConfigJson.get(YOUBORA_ACCOUNT_CODE) != null) {
//               YouboraConfig youboraConfig = gson.fromJson(youboraConfigJson, YouboraConfig.class);
//               if (youboraConfig != null) {
//                  createYouboraPlugin(pluginConfigs, youboraConfig);
//               }
//            }
//         }
//
//         if (initOptionsModel.plugins.ottAnalytics != null) {
//            createPhoenixAnalyticsPlugin(pluginConfigs, initOptionsModel.plugins.ottAnalytics); //DEFAULT
//         }
//
//         if (initOptionsModel.plugins.broadpeak != null) {
//            JsonObject broadpeakJsonObject = initOptionsModel.plugins.broadpeak;
//
//            BroadpeakConfig broadpeakConfig = gson.fromJson(broadpeakJsonObject.toString(), BroadpeakConfig.class);
//            createBroadpeakPlugin(pluginConfigs, broadpeakConfig);
//         }
//      }

      final PlayerInitOptions initOptions = new PlayerInitOptions(partnerId);
      initOptions.setAutoPlay(true);
      initOptions.setPreload(true);
      initOptions.setAllowCrossProtocolEnabled(true);
//      if (initOptionsModel.requestConfig != null) {
//         initOptions.setPKRequestConfig(initOptionsModel.requestConfig);
//      } else {
//         initOptions.setAllowCrossProtocolEnabled(initOptionsModel.allowCrossProtocolRedirect);
//      }
//      initOptions.setKs(initOptionsModel.ks);
//      initOptions.setReferrer(initOptionsModel.referrer);
//      initOptions.setPKLowLatencyConfig(initOptionsModel.lowLatencyConfig);
//      initOptions.setAbrSettings(initOptionsModel.abrSettings);
//      initOptions.setPreferredMediaFormat(initOptionsModel.preferredMediaFormat);
      initOptions.setSecureSurface(true);
//      initOptions.setAspectRatioResizeMode(initOptionsModel.aspectRatioResizeMode);
      initOptions.setAllowClearLead(true);
      initOptions.setEnableDecoderFallback(true);
//      initOptions.setAdAutoPlayOnResume(initOptionsModel.adAutoPlayOnResume);
//      initOptions.setIsVideoViewHidden(initOptionsModel.isVideoViewHidden);
//      initOptions.forceSinglePlayerEngine(initOptionsModel.forceSinglePlayerEngine);
//      initOptions.setTunneledAudioPlayback(initOptionsModel.isTunneledAudioPlayback);
//      initOptions.setMaxAudioBitrate(initOptionsModel.maxAudioBitrate);
//      initOptions.setMaxAudioChannelCount(initOptionsModel.maxAudioChannelCount);
//      initOptions.setMaxVideoBitrate(initOptionsModel.maxVideoBitrate);
//      initOptions.setMaxVideoSize(initOptionsModel.maxVideoSize);
//      initOptions.setHandleAudioBecomingNoisy(initOptionsModel.handleAudioBecomingNoisyEnabled);
//      initOptions.setHandleAudioFocus(initOptionsModel.handleAudioFocus);
//      initOptions.setMulticastSettings(initOptionsModel.multicastSettings);
//      initOptions.setMediaEntryCacheConfig(initOptionsModel.mediaEntryCacheConfig);

//      NetworkSettings networkSettings = initOptionsModel.networkSettings;
//      if (initOptionsModel.networkSettings != null && initOptionsModel.networkSettings.preferredForwardBufferDuration > 0) {
//         initOptions.setLoadControlBuffers(new LoadControlBuffers().setMaxPlayerBufferMs(initOptionsModel.networkSettings.preferredForwardBufferDuration));
//      }
//
//      if (initOptionsModel.trackSelection != null && initOptionsModel.trackSelection.audioLanguage != null && initOptionsModel.trackSelection.audioMode != null) {
//         initOptions.setAudioLanguage(initOptionsModel.trackSelection.audioLanguage, initOptionsModel.trackSelection.audioMode);
//      }
//      if (initOptionsModel.trackSelection != null && initOptionsModel.trackSelection.textLanguage != null && initOptionsModel.trackSelection.textMode != null) {
//         initOptions.setTextLanguage(initOptionsModel.trackSelection.textLanguage, initOptionsModel.trackSelection.textMode);
//      }

      //initOptions.setVideoCodecSettings(appPlayerInitConfig.videoCodecSettings)
      //initOptions.setAudioCodecSettings(appPlayerInitConfig.audioCodecSettings)
      initOptions.setPluginConfigs(pluginConfigs);

      player = KalturaOttPlayer.create(context, initOptions);
   }

   private void load(String assetId) {
      log.d("load assetId: " + assetId);

//      Gson gson = new Gson();
//      MediaAsset mediaAsset = gson.fromJson(options, MediaAsset.class);
//      if (mediaAsset == null) {
//         log.d("Media Asset is null hence returning");
//         return;
//      }

      if (player == null) {
         log.d("Player is null hence returning");
         return;
      }

      player.setPlayerView(MATCH_PARENT, MATCH_PARENT);
      addView(player.getPlayerView());

      player.loadMedia(buildOttMediaOptions(assetId), (mediaOptions, entry, error) -> {
         if (error != null) {
            log.e("ott media load error: " + error.getName() + " " + error.getCode() + " " + error.getMessage());
         }
      });
      addActivityLifeCycleListeners(context);
   }

   @Override
   public void requestLayout() {
      super.requestLayout();
      // This view relies on a measure + layout pass happening after it calls requestLayout().
      // https://github.com/facebook/react-native/issues/17968#issuecomment-721958427
      post(measureAndLayout);
   }

   private final Runnable measureAndLayout = new Runnable() {
      @Override
      public void run() {
         measure(
                 MeasureSpec.makeMeasureSpec(getWidth(), MeasureSpec.EXACTLY),
                 MeasureSpec.makeMeasureSpec(getHeight(), MeasureSpec.EXACTLY));
         layout(getLeft(), getTop(), getRight(), getBottom());
      }
   };

   public OTTMediaOptions buildOttMediaOptions(String assetId) {

      OTTMediaAsset ottMediaAsset = new OTTMediaAsset();
      ottMediaAsset.setAssetId(assetId);
      ottMediaAsset.setAssetType(APIDefines.KalturaAssetType.Media);
      ottMediaAsset.setContextType(APIDefines.PlaybackContextType.Playback);
      ottMediaAsset.setAssetReferenceType(APIDefines.AssetReferenceType.Media);
      ottMediaAsset.setProtocol(PhoenixMediaProvider.HttpProtocol.Http);
      ottMediaAsset.setKs(null);
      ottMediaAsset.setFormats(Collections.singletonList("Mobile_Main"));
      OTTMediaOptions ottMediaOptions = new OTTMediaOptions(ottMediaAsset);
      return ottMediaOptions;
   }

   protected void onApplicationResumed() {
      if (player != null) {
         player.onApplicationResumed();
      }
   }

   protected void onApplicationPaused() {
      if (player != null) {
         player.onApplicationPaused();
      }
   }

   protected void onApplicationDestroy() {
      if (player != null) {
         player.destroy();
      }
      player = null;
   }

   private void addActivityLifeCycleListeners(ThemedReactContext context) {
      log.d("addActivityLifeCycleListeners");
      if (context == null) {
         log.d("Context is null hence returning.");
         return;
      }

      this.context.addLifecycleEventListener(new LifecycleEventListener() {
         @Override
         public void onHostResume() {
            log.d("Activity resume");
            onApplicationResumed();
         }

         @Override
         public void onHostPause() {
            log.d("Activity pause");
            onApplicationPaused();
         }

         @Override
         public void onHostDestroy() {
            log.d("Activity destroyed");
            onApplicationDestroy();
         }
      });
   }

}

