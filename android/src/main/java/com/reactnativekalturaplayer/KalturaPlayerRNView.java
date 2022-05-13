package com.reactnativekalturaplayer;

import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.WritableNativeArray;
import com.facebook.react.bridge.WritableNativeMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.uimanager.ThemedReactContext;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.kaltura.netkit.utils.NKLog;
import com.kaltura.playkit.PKDrmParams;
import com.kaltura.playkit.PKError;
import com.kaltura.playkit.PKLog;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PKPlugin;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PKRequestConfig;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.ads.PKAdErrorType;
import com.kaltura.playkit.player.LoadControlBuffers;
import com.kaltura.playkit.player.MediaSupport;
import com.kaltura.playkit.player.PKHttpClientManager;
import com.kaltura.playkit.player.PKPlayerErrorType;
import com.kaltura.playkit.player.PKTracks;
import com.kaltura.playkit.player.thumbnail.ThumbnailInfo;
import com.kaltura.playkit.plugins.ads.AdCuePoints;
import com.kaltura.playkit.plugins.ads.AdEvent;
import com.kaltura.playkit.plugins.broadpeak.BroadpeakConfig;
import com.kaltura.playkit.plugins.broadpeak.BroadpeakEvent;
import com.kaltura.playkit.plugins.ima.IMAConfig;
import com.kaltura.playkit.plugins.imadai.IMADAIConfig;
import com.kaltura.playkit.plugins.kava.KavaAnalyticsConfig;
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsConfig;
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsEvent;
import com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig;
import com.kaltura.playkit.utils.Consts;
import com.kaltura.tvplayer.KalturaBasicPlayer;
import com.kaltura.tvplayer.KalturaOttPlayer;
import com.kaltura.tvplayer.KalturaOvpPlayer;
import com.kaltura.tvplayer.KalturaPlayer;
import com.kaltura.tvplayer.OTTMediaOptions;
import com.kaltura.tvplayer.OVPMediaOptions;
import com.kaltura.tvplayer.PlayerInitOptions;
import com.npaw.youbora.lib6.YouboraLog;
import com.reactnativekalturaplayer.events.KalturaPlayerAdEvents;
import com.reactnativekalturaplayer.events.KalturaPlayerEvents;
import com.reactnativekalturaplayer.model.BasicMediaAsset;
import com.reactnativekalturaplayer.model.InitOptions;
import com.reactnativekalturaplayer.model.MediaAsset;
import com.reactnativekalturaplayer.model.PlayerPluginUtilsKt;
import com.reactnativekalturaplayer.model.PlayerPlugins;
import com.reactnativekalturaplayer.model.UpdatePluginConfigJson;
import com.reactnativekalturaplayer.model.tracks.AudioTrack;
import com.reactnativekalturaplayer.model.tracks.ImageTrack;
import com.reactnativekalturaplayer.model.tracks.TextTrack;
import com.reactnativekalturaplayer.model.tracks.TracksInfo;
import com.reactnativekalturaplayer.model.tracks.VideoTrack;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class KalturaPlayerRNView extends FrameLayout {

   private final PKLog log = PKLog.get(KalturaPlayerRNView.class.getSimpleName());
   private final ThemedReactContext context;

   private KalturaPlayer player;
   private KalturaPlayer.Type playerType;

   private int partnerId;
   private String assetId;
   private String mediaAsset;

   private final Gson gson = new Gson();
   private long reportedDuration = Consts.TIME_UNSET;
   private boolean playerViewAdded;
   private static final String YOUBORA_ACCOUNT_CODE = "accountCode";

   public KalturaPlayerRNView(@NonNull ThemedReactContext context) {
      super(context);
      this.context = context;
      // addActivityLifeCycleListeners(context);// Intentionally commented out because RN FE is handling it
   }

   protected void setPartnerId(int partnerId) {
      this.partnerId = partnerId;
   }

   protected void setPlayerType(String playerType) {
      this.playerType = getKalturaPlayerType(playerType);
   }

   protected void setAssetId(String assetId) {
      this.assetId = assetId;
   }

   protected void createPlayerInstance(String initOptions) {
      if ((partnerId > 0 && !TextUtils.isEmpty(initOptions)) || playerType == KalturaPlayer.Type.basic) {
         if (playerType == KalturaPlayer.Type.basic) {
            createKalturaBasicPlayer(initOptions);
         } else if (!TextUtils.isEmpty(initOptions) &&
                 (playerType == KalturaPlayer.Type.ott || playerType == KalturaPlayer.Type.ovp)){
            createKalturaOttOvpPlayer(partnerId, initOptions);
         } else {
            //TODO: Fix log message
            log.e("Player can not be created.");
         }
      } else {
         log.e("PartnerId is not valid.");
      }
   }

   protected void setMediaAsset(String mediaAsset) {
      this.mediaAsset = mediaAsset;
   }

   protected void load(boolean autoPlay) {
      load(assetId, mediaAsset);
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

   protected void onApplicationResumed() {
      if (player != null) {
         requestLayout(); // TODO: This does not fix the problem while coming to foreground. Look for another solution
         player.onApplicationResumed();
      }
   }

   protected void onApplicationPaused() {
      if (player != null) {
         player.onApplicationPaused();
      }
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
            destroy();
         }
      });
   }

   private void createKalturaBasicPlayer(String initOptions) {
      log.d("Creating Basic Player instance.");
      InitOptions initOptionsModel = getParsedJson(initOptions, InitOptions.class);
      PlayerInitOptions playerInitOptions = new PlayerInitOptions();
      if (initOptionsModel == null) {
         playerInitOptions.setAutoPlay(true);
         playerInitOptions.setPKRequestConfig(new PKRequestConfig(true));
      } else {
         setCommonPlayerInitOptions(playerInitOptions, initOptionsModel);
         PKPluginConfigs pkPluginConfigs = createPluginConfigs(initOptionsModel);
         playerInitOptions.setPluginConfigs(pkPluginConfigs);
      }

      if (player == null) {
         player = KalturaBasicPlayer.create(context, playerInitOptions);
      }
      initDrm(context);
      addPlayerViewToRNView(player);
   }

   private void addPlayerViewToRNView(KalturaPlayer kalturaPlayer) {
      if (!playerViewAdded && kalturaPlayer != null) {
         kalturaPlayer.setPlayerView(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
         addView(kalturaPlayer.getPlayerView());
         playerViewAdded = true;
      }
   }

   private PKMediaEntry createMediaEntry(String url, BasicMediaAsset basicMediaAsset) {
      log.d("createMediaEntry URL is: " + url);

      //Create media entry.
      PKMediaEntry mediaEntry = new PKMediaEntry();
      //Set id for the entry.
      mediaEntry.setId(basicMediaAsset.getId());
      mediaEntry.setName(basicMediaAsset.getName());
      mediaEntry.setDuration(basicMediaAsset.getDuration());
      mediaEntry.setMediaType(basicMediaAsset.getMediaEntryType());
      mediaEntry.setIsVRMediaType(basicMediaAsset.isVRMediaType());

      if (basicMediaAsset.getExternalSubtitleList() != null && !basicMediaAsset.getExternalSubtitleList().isEmpty()) {
         mediaEntry.setExternalSubtitleList(basicMediaAsset.getExternalSubtitleList());
      }

      if (basicMediaAsset.getMetadata() != null && !basicMediaAsset.getMetadata().isEmpty()) {
         mediaEntry.setMetadata(basicMediaAsset.getMetadata());
      }

      if (!TextUtils.isEmpty(basicMediaAsset.getExternalVttThumbnailUrl())) {
         mediaEntry.setExternalVttThumbnailUrl(basicMediaAsset.getExternalVttThumbnailUrl());
      }

      List<PKMediaSource> mediaSources = createMediaSources(url, basicMediaAsset);
      mediaEntry.setSources(mediaSources);

      return mediaEntry;
   }

   /**
    * Create list of {@link PKMediaSource}.
    * @return - the list of sources.
    */
   private List<PKMediaSource> createMediaSources(String url, BasicMediaAsset basicMediaAsset) {
      log.d("createMediaSources URL is: " + url);
      //Create new PKMediaSource instance.
      PKMediaSource mediaSource = new PKMediaSource();
      //Set the id.
      mediaSource.setId("basicPlayerTestSource");
      //Set the content url.
      mediaSource.setUrl(url);
      //Set the format of the source.
      mediaSource.setMediaFormat(basicMediaAsset.getMediaFormat());
      // Set the DRM Params if available
      setDrmParams(basicMediaAsset.getDrmData(), mediaSource);

      return Collections.singletonList(mediaSource);
   }

   private void setDrmParams(List<PKDrmParams> pkDrmParamsList, PKMediaSource mediaSource) {
      if (mediaSource != null && pkDrmParamsList != null && !pkDrmParamsList.isEmpty()) {
         mediaSource.setDrmData(pkDrmParamsList);
      }
   }

   private void createKalturaOttOvpPlayer(int partnerId, String playerInitOptionsJson) {
      log.d("createKalturaOttOvpPlayer:" + partnerId + ", \n initOptions: \n " + playerInitOptionsJson);

      InitOptions initOptionsModel = getParsedJson(playerInitOptionsJson, InitOptions.class);
      if (initOptionsModel == null || TextUtils.isEmpty(initOptionsModel.serverUrl) || playerType == null || playerType == KalturaPlayer.Type.basic) {
         // TODO : write log message
         return;
      }

      // load the player and put it in the main frame
      if (playerType == KalturaPlayer.Type.ott) {
         KalturaOttPlayer.initialize(context, partnerId, initOptionsModel.serverUrl);
      } else {
         KalturaOvpPlayer.initialize(context, partnerId, initOptionsModel.serverUrl);
      }

      if (initOptionsModel.warmupUrls != null && !initOptionsModel.warmupUrls.isEmpty()) {
         PKHttpClientManager.setHttpProvider("okhttp");
         PKHttpClientManager.warmUp((initOptionsModel.warmupUrls).toArray((new String[0])));
      }

      PlayerInitOptions playerInitOptions = new PlayerInitOptions(partnerId);

      playerInitOptions.setKs(initOptionsModel.ks);
      playerInitOptions.setMediaEntryCacheConfig(initOptionsModel.mediaEntryCacheConfig);
      setCommonPlayerInitOptions(playerInitOptions, initOptionsModel);

      PKPluginConfigs pkPluginConfigs = createPluginConfigs(initOptionsModel);
      playerInitOptions.setPluginConfigs(pkPluginConfigs);

      //playerInitOptions.setVideoCodecSettings(appPlayerInitConfig.videoCodecSettings)
      //playerInitOptions.setAudioCodecSettings(appPlayerInitConfig.audioCodecSettings)

      if (player == null && playerType == KalturaPlayer.Type.ott) {
         player = KalturaOttPlayer.create(context, playerInitOptions);
      }

      if (player == null && playerType == KalturaPlayer.Type.ovp) {
         player = KalturaOvpPlayer.create(context, playerInitOptions);
      }
      initDrm(context);
      addPlayerViewToRNView(player);
   }

   /**
    * PlayerInitOptions which can be used for
    * OVP, OTT and Basic Player types
    *
    * @param initOptions PlayerInitOptions
    * @param initOptionsModel InitOptions model passed by FE apps
    */
   private void setCommonPlayerInitOptions(PlayerInitOptions initOptions, InitOptions initOptionsModel) {
      initOptions.setAutoPlay(initOptionsModel.autoplay);
      initOptions.setPreload(initOptionsModel.preload);
      if (initOptionsModel.requestConfig != null) {
         initOptions.setPKRequestConfig(initOptionsModel.requestConfig);
      } else {
         initOptions.setAllowCrossProtocolEnabled(initOptionsModel.allowCrossProtocolRedirect);
      }
      initOptions.setReferrer(initOptionsModel.referrer);
      initOptions.setPKLowLatencyConfig(initOptionsModel.lowLatencyConfig);
      initOptions.setAbrSettings(initOptionsModel.abrSettings);
      initOptions.setPreferredMediaFormat(initOptionsModel.preferredMediaFormat);
      initOptions.setSecureSurface(initOptionsModel.secureSurface);
      initOptions.setAspectRatioResizeMode(initOptionsModel.aspectRatioResizeMode);
      initOptions.setAllowClearLead(initOptionsModel.allowClearLead);
      initOptions.setEnableDecoderFallback(initOptionsModel.enableDecoderFallback);
      initOptions.setAdAutoPlayOnResume(initOptionsModel.adAutoPlayOnResume);
      initOptions.setIsVideoViewHidden(initOptionsModel.isVideoViewHidden);
      initOptions.forceSinglePlayerEngine(initOptionsModel.forceSinglePlayerEngine);
      initOptions.setTunneledAudioPlayback(initOptionsModel.isTunneledAudioPlayback);
      initOptions.setMaxAudioBitrate(initOptionsModel.maxAudioBitrate);
      initOptions.setMaxAudioChannelCount(initOptionsModel.maxAudioChannelCount);
      initOptions.setMaxVideoBitrate(initOptionsModel.maxVideoBitrate);
      initOptions.setMaxVideoSize(initOptionsModel.maxVideoSize);
      initOptions.setHandleAudioBecomingNoisy(initOptionsModel.handleAudioBecomingNoisyEnabled);
      initOptions.setHandleAudioFocus(initOptionsModel.handleAudioFocus);
      initOptions.setMulticastSettings(initOptionsModel.multicastSettings);

      if (initOptionsModel.networkSettings != null && initOptionsModel.networkSettings.preferredForwardBufferDuration > 0) {
         initOptions.setLoadControlBuffers(new LoadControlBuffers().setMaxPlayerBufferMs(initOptionsModel.networkSettings.preferredForwardBufferDuration));
      }

      if (initOptionsModel.trackSelection != null && initOptionsModel.trackSelection.audioLanguage != null && initOptionsModel.trackSelection.audioMode != null) {
         initOptions.setAudioLanguage(initOptionsModel.trackSelection.audioLanguage, initOptionsModel.trackSelection.audioMode);
      }
      if (initOptionsModel.trackSelection != null && initOptionsModel.trackSelection.textLanguage != null && initOptionsModel.trackSelection.textMode != null) {
         initOptions.setTextLanguage(initOptionsModel.trackSelection.textLanguage, initOptionsModel.trackSelection.textMode);
      }
   }

   /**
    * Create `PKPluginConfigs` object for `PlayerInitOptions`
    *
    * @param initOptions class which contains all the configuration for PlayerInitOptions
    * @return `PKPluginConfig` object
    */
   @NonNull
   private PKPluginConfigs createPluginConfigs(InitOptions initOptions) {
      PKPluginConfigs pkPluginConfigs = new PKPluginConfigs();

      if (initOptions.plugins != null) {
         if (initOptions.plugins.getIma() != null) {
            createPlugin(PlayerPlugins.ima, pkPluginConfigs, initOptions.plugins.getIma());
         }

         if (initOptions.plugins.getImadai() != null) {
            createPlugin(PlayerPlugins.imadai, pkPluginConfigs, initOptions.plugins.getImadai());
         }

         if (initOptions.plugins.getYoubora() != null) {
            JsonObject youboraConfigJson = initOptions.plugins.getYoubora();
            if (youboraConfigJson.has(YOUBORA_ACCOUNT_CODE) && youboraConfigJson.get(YOUBORA_ACCOUNT_CODE) != null) {
               createPlugin(PlayerPlugins.youbora, pkPluginConfigs, initOptions.plugins.getYoubora());
            }
         }

         if (initOptions.plugins.getKava() != null) {
            createPlugin(PlayerPlugins.kava, pkPluginConfigs, initOptions.plugins.getKava());
         }

         if (initOptions.plugins.getOttAnalytics() != null) {
            createPlugin(PlayerPlugins.ottAnalytics, pkPluginConfigs, initOptions.plugins.getOttAnalytics());
         }

         if (initOptions.plugins.getBroadpeak() != null) {
            createPlugin(PlayerPlugins.broadpeak, pkPluginConfigs, initOptions.plugins.getBroadpeak());
         }
      }

      return pkPluginConfigs;
   }

   public void load(String assetId, String mediaAssetJson) {
      log.d("load assetId: " + assetId +
              "\n player type: " + playerType +
              "\n , mediaAssetJson:" + mediaAssetJson);

      if (player == null) {
         log.e("Player instance is null while loading the media. Hence returning.");
         return;
      }

      if (playerType == KalturaPlayer.Type.basic || isBasicPlaybackRequired(mediaAssetJson)) {
         BasicMediaAsset basicMediaAsset = getParsedJson(mediaAssetJson, BasicMediaAsset.class);
         if (basicMediaAsset == null || basicMediaAsset.getMediaFormat() == null) {
            log.e("Invalid Media Asset for player type " + playerType + " \n and media asset is " + basicMediaAsset);
            return;
         }
         PKMediaEntry mediaEntry = createMediaEntry(assetId, basicMediaAsset);
         player.setMedia(mediaEntry);
      } else if (playerType == KalturaPlayer.Type.ott || playerType == KalturaPlayer.Type.ovp) {
         MediaAsset mediaAsset = getParsedJson(mediaAssetJson, MediaAsset.class);
         if (mediaAsset == null || player == null) {
            log.e("Invalid Media Asset for player type " + playerType + " \n and media asset is " + mediaAsset);
            return;
         }

         if (playerType == KalturaPlayer.Type.ott) {
            OTTMediaOptions ottMediaOptions = mediaAsset.buildOttMediaOptions(assetId, player.getKS());
            player.loadMedia(ottMediaOptions, (mediaOptions, entry, error) -> {
               if (error != null) {
                  log.e("ott media load error: " + error.getName() + " " + error.getCode() + " " + error.getMessage());
                  sendPlayerEvent(KalturaPlayerEvents.LOAD_MEDIA_FAILED, gson.toJson(error));
               } else {
                  log.d("ott media load success name = " + entry.getName() + " initialVolume = " + mediaAsset.getInitialVolume());
                  sendPlayerEvent(KalturaPlayerEvents.LOAD_MEDIA_SUCCESS, gson.toJson(entry));

                  if (mediaAsset.getInitialVolume() >= 0 && mediaAsset.getInitialVolume() < 1.0) {
                     player.setVolume(mediaAsset.getInitialVolume());
                  }
               }
            });
         } else {
            OVPMediaOptions ovpMediaOptions = mediaAsset.buildOvpMediaOptions(assetId, "", player.getKS());
            player.loadMedia(ovpMediaOptions, (mediaOptions, entry, error) -> {
               if (error != null) {
                  log.e("ovp media load error: " + error.getName() + " " + error.getCode() + " " + error.getMessage());
                  sendPlayerEvent(KalturaPlayerEvents.LOAD_MEDIA_FAILED, gson.toJson(error));
               } else {
                  log.d("ovp media load success name = " + entry.getName() + " initialVolume = " + mediaAsset.getInitialVolume());
                  sendPlayerEvent(KalturaPlayerEvents.LOAD_MEDIA_SUCCESS, gson.toJson(entry));

                  if (mediaAsset.getInitialVolume() >= 0 && mediaAsset.getInitialVolume() < 1.0) {
                     player.setVolume(mediaAsset.getInitialVolume());
                  }
               }
            });
         }
      } else {
         log.e("No Player type defined hence can not load the media. PlayerType " + playerType);
      }
   }

   /**
    * This method checks if the Player type is OVP/OTT
    * but still app wants to play a media using URL or DRM license URL
    * instead of using our backend.
    *
    * @return `true` if basic playback required
    */
   protected boolean isBasicPlaybackRequired(String basicMediaAsset) {
      if (basicMediaAsset != null && playerType != null && playerType != KalturaPlayer.Type.basic) {
         BasicMediaAsset mediaAsset = getParsedJson(basicMediaAsset, BasicMediaAsset.class);
         return mediaAsset != null && mediaAsset.getMediaFormat() != null;
      }
      return false;
   }

   protected void updatePluginConfigs(String pluginConfigJson) {
      log.e("configurePluginConfigs");
      if (TextUtils.isEmpty(pluginConfigJson)) {
         log.e("pluginConfigJson is empty hence returning from here.");
         return;
      }

      UpdatePluginConfigJson pluginConfig = getParsedJson(pluginConfigJson, UpdatePluginConfigJson.class);

      if (pluginConfig != null &&
              !TextUtils.isEmpty(pluginConfig.getPluginName()) &&
              pluginConfig.getPluginConfig() != null) {

         String pluginName = pluginConfig.getPluginName();

         if (TextUtils.equals(pluginName, PlayerPlugins.ima.name())) {
            IMAConfig imaConfig = getParsedJson(pluginConfig.getPluginConfig().toString(), IMAConfig.class);
            if (imaConfig != null) {
               updatePlugin(PlayerPlugins.ima, imaConfig);
            }
         } else if (TextUtils.equals(pluginName, PlayerPlugins.imadai.name())) {
            IMADAIConfig imadaiConfig = getParsedJson(pluginConfig.getPluginConfig().toString(), IMADAIConfig.class);
            if (imadaiConfig != null) {
               updatePlugin(PlayerPlugins.imadai, imadaiConfig);
            }
         } else if (TextUtils.equals(pluginName, PlayerPlugins.youbora.name())) {
            YouboraConfig youboraConfig = getParsedJson(pluginConfig.getPluginConfig().toString(), YouboraConfig.class);
            if (youboraConfig != null) {
               updatePlugin(PlayerPlugins.youbora, youboraConfig);
            }
         } else if (TextUtils.equals(pluginName, PlayerPlugins.kava.name())) {
            KavaAnalyticsConfig kavaAnalyticsConfig = getParsedJson(pluginConfig.getPluginConfig().toString(), KavaAnalyticsConfig.class);
            if (kavaAnalyticsConfig != null) {
               updatePlugin(PlayerPlugins.kava, kavaAnalyticsConfig);
            }
         } else if (TextUtils.equals(pluginName, PlayerPlugins.ottAnalytics.name())) {
            PhoenixAnalyticsConfig phoenixAnalyticsConfig = getParsedJson(pluginConfig.getPluginConfig().toString(), PhoenixAnalyticsConfig.class);
            if (phoenixAnalyticsConfig != null) {
               updatePlugin(PlayerPlugins.ottAnalytics, phoenixAnalyticsConfig);
            }
         } else if (TextUtils.equals(pluginName, PlayerPlugins.broadpeak.name())) {
            BroadpeakConfig broadpeakConfig = getParsedJson(pluginConfig.getPluginConfig().toString(), BroadpeakConfig.class);
            if (broadpeakConfig != null) {
               updatePlugin(PlayerPlugins.broadpeak, broadpeakConfig);
            }
         } else {
            log.w("No Plugin can be registered PluginName is: " + pluginName);
         }
      } else {
         log.e("Plugin config or Plugin Name is not valid.");
      }
   }

   private void initDrm(ThemedReactContext context) {
      context.runOnNativeModulesQueueThread(() -> MediaSupport.initializeDrm(context, (pkDeviceSupportInfo, provisionError) -> {
         if (pkDeviceSupportInfo.isProvisionPerformed()) {
            if (provisionError != null) {
               log.e("DRM Provisioning failed" + provisionError);
            } else {
               log.d("DRM Provisioning succeeded");
            }
         }
         log.d("DRM initialized; supported: " + pkDeviceSupportInfo.getSupportedDrmSchemes() + " isHardwareDrmSupported = " + pkDeviceSupportInfo.isHardwareDrmSupported());
         Gson gson = new Gson();
         sendPlayerEvent("drmInitialized", gson.toJson(pkDeviceSupportInfo));
      }));
   }

   public void play() {
      log.d("play");
      if (player != null && !player.isPlaying()) {
         player.play();
      }
   }

   public void pause() {
      log.d("pause");
      if (player != null && player.isPlaying()) {
         player.pause();
      }
   }

   public void replay() {
      log.d("replay");
      if (player != null) {
         player.replay();
      }
   }

   public void seekTo(float position) {
      long posMS = (long) (position * Consts.MILLISECONDS_MULTIPLIER);
      log.d("seekTo:" + posMS);
      if (player != null) {
         player.seekTo(posMS);
      }
   }

   public void changeTrack(String uniqueId) {
      log.d("changeTrack:" + uniqueId);
      if (player != null) {
         player.changeTrack(uniqueId);
      }
   }

   public void changePlaybackRate(float playbackRate) {
      log.d("changePlaybackRate:" + playbackRate);
      if (player != null) {
         player.setPlaybackRate(playbackRate);
      }
   }

   protected void destroy() {
      if (player != null) {
         player.destroy();
      }
      player = null;
      playerViewAdded = false;
   }

   public void stop() {
      log.d("stop");
      if (player != null) {
         player.stop();
      }
   }

   public void setAutoplay(boolean autoplay) {
      log.d("setAutoplay: " + autoplay);
      if (player != null) {
         player.setAutoPlay(autoplay);
      }
   }

   public void setKS(String ks) {
      log.d("setKS: " + ks);
      if (player != null) {
         player.setKS(ks);
      }
   }

   public void setZIndex(float index) {
      log.d("setZIndex: " + index);
      if (player != null && player.getPlayerView() != null) {
         player.getPlayerView().setZ(index);
      }
   }

   //NOT ADDED YET AS PROPS
   public void setFrame(int playerViewWidth, int playerViewHeight, int playerViewPosX, int playerViewPosY) {
      log.d("setFrame " + playerViewWidth + "/" + playerViewHeight + " " + playerViewPosX + "/" + playerViewPosY);

      if (player != null && player.getPlayerView() != null) {

         ViewGroup.LayoutParams layoutParams = player.getPlayerView().getLayoutParams();

         if (layoutParams != null) {
            layoutParams.width = playerViewWidth >= 0 ? playerViewWidth : MATCH_PARENT;
            layoutParams.height = playerViewHeight >= 0 ? playerViewHeight : MATCH_PARENT;
            player.getPlayerView().setLayoutParams(layoutParams);
         }

         if (playerViewPosX > 0) {
            player.getPlayerView().setTranslationX(playerViewPosX);
         } else {
            player.getPlayerView().setTranslationX(0);
         }

         if (playerViewPosY > 0) {
            player.getPlayerView().setTranslationY(playerViewPosY);
         } else {
            player.getPlayerView().setTranslationY(0);
         }
      }
   }

   public void setVolume(float volume) {
      log.d("setVolume: " + volume);
      if (volume < 0) {
         volume = 0f;
      } else if (volume > 1) {
         volume = 1.0f;
      }

      if (player != null) {
         float finalPlayerVolume = volume;

         player.setVolume(finalPlayerVolume);

      }
   }

   //NOT ADDED YET AS PROPS
   public void setPlayerVisibility(boolean isVisible, float volume) {
      log.d("setPlayerVisibility: " + isVisible + " volume = " + volume);
      if (player != null && player.getPlayerView() != null) {
         if (isVisible) {
            player.getPlayerView().setVisibility(View.VISIBLE);
         } else {
            player.getPlayerView().setVisibility(View.INVISIBLE);
         }
         player.setVolume(volume);

      }
   }

   //NOT ADDED YET AS PROPS
   public void requestThumbnailInfo(float positionMs) {
      log.d("requestThumbnailInfo:" + positionMs);
      if (player != null) {
         ThumbnailInfo thumbnailInfo = player.getThumbnailInfo((long) positionMs);
         if (thumbnailInfo != null && positionMs >= 0) {
            String thumbnailInfoJson = "{ \"position\": " + positionMs + ", \"thumbnailInfo\": " + gson.toJson(thumbnailInfo) + " }";
            sendPlayerEvent("thumbnailInfoResponse", thumbnailInfoJson);
         } else {
            log.e("requestThumbnailInfo: thumbnailInfo is null or position is invalid");
         }
      }
   }

   //NOT ADDED YET AS PROPS
   public void setLogLevel(String logLevel) {
      log.d("setLogLevel: " + logLevel);
      if (TextUtils.isEmpty(logLevel)) {
         return;
      }
      logLevel = logLevel.toUpperCase();

      switch (logLevel) {
         case "VERBOSE":
            PKLog.setGlobalLevel(PKLog.Level.verbose);
            NKLog.setGlobalLevel(NKLog.Level.verbose);
            YouboraLog.setDebugLevel(YouboraLog.Level.VERBOSE);
            break;
         case "DEBUG":
            PKLog.setGlobalLevel(PKLog.Level.debug);
            NKLog.setGlobalLevel(NKLog.Level.debug);
            YouboraLog.setDebugLevel(YouboraLog.Level.DEBUG);
            break;
         case "WARN":
            PKLog.setGlobalLevel(PKLog.Level.warn);
            NKLog.setGlobalLevel(NKLog.Level.warn);
            YouboraLog.setDebugLevel(YouboraLog.Level.WARNING);
            break;
         case "INFO":
            PKLog.setGlobalLevel(PKLog.Level.info);
            NKLog.setGlobalLevel(NKLog.Level.info);
            YouboraLog.setDebugLevel(YouboraLog.Level.NOTICE);
            break;
         case "ERROR":
            PKLog.setGlobalLevel(PKLog.Level.error);
            NKLog.setGlobalLevel(NKLog.Level.error);
            YouboraLog.setDebugLevel(YouboraLog.Level.ERROR);
            break;
         case "OFF":
         default:
            PKLog.setGlobalLevel(PKLog.Level.off);
            NKLog.setGlobalLevel(NKLog.Level.off);
            YouboraLog.setDebugLevel(YouboraLog.Level.SILENT);
      }
   }

   protected void removePlayerListeners() {
      if (player != null) {
         player.removeListeners(this);
         log.d("Player listeners are removed.");
      }
   }

   protected void addKalturaPlayerListeners() {
      log.d("addKalturaPlayerListeners");

      if (player == null) {
         log.d("Player is null. Not able to add the Kaltura Player Listeners hence returning.");
         return;
      }

      log.d("Player listeners are added.");

      player.addListener(context, PlayerEvent.canPlay, event -> sendPlayerEvent(KalturaPlayerEvents.CAN_PLAY));

      player.addListener(context, PlayerEvent.playing, event -> sendPlayerEvent(KalturaPlayerEvents.PLAYING));

      player.addListener(context, PlayerEvent.play, event -> sendPlayerEvent(KalturaPlayerEvents.PLAY));

      player.addListener(context, PlayerEvent.pause, event -> sendPlayerEvent(KalturaPlayerEvents.PAUSE));

      player.addListener(context, PlayerEvent.ended, event -> sendPlayerEvent(KalturaPlayerEvents.ENDED));

      player.addListener(context, PlayerEvent.stopped, event -> sendPlayerEvent(KalturaPlayerEvents.STOPPED));

      player.addListener(context, PlayerEvent.durationChanged, event -> {
         reportedDuration = event.duration;
         String durationJson = "{ \"duration\": " + (event.duration / Consts.MILLISECONDS_MULTIPLIER_FLOAT) + " }";
         sendPlayerEvent(KalturaPlayerEvents.DURATION_CHANGE, durationJson);
      });

      player.addListener(context, PlayerEvent.playheadUpdated, event -> {
         String timeUpdatePayload = "\"position\": " + (event.position / Consts.MILLISECONDS_MULTIPLIER_FLOAT) +
                 ", \"bufferPosition\": " + (event.bufferPosition / Consts.MILLISECONDS_MULTIPLIER_FLOAT);

         if (player != null && player.isLive() && player.getCurrentProgramTime() > 0) {
            timeUpdatePayload = "{ " + timeUpdatePayload +
                    ", \"currentProgramTime\": " + player.getCurrentProgramTime() + " }";
         } else {
            timeUpdatePayload = "{ " + timeUpdatePayload + " }";
         }

         sendPlayerEvent(KalturaPlayerEvents.PLAYHEAD_UPDATED, timeUpdatePayload);
         if (reportedDuration != event.duration && event.duration > 0) {
            reportedDuration = event.duration;
            if (player != null && player.getMediaEntry() != null && player.getMediaEntry().getMediaType() != PKMediaEntry.MediaEntryType.Vod /*|| player.isLive()*/) {
               sendPlayerEvent(KalturaPlayerEvents.LOAD_TIME_RANGES, "{\"timeRanges\": [ { \"start\": " + 0 +
                       ", \"end\": " + (event.duration / Consts.MILLISECONDS_MULTIPLIER_FLOAT) +
                       " } ] }");
            }
         }
      });

      player.addListener(context, PlayerEvent.stateChanged, event -> {
         sendPlayerEvent(KalturaPlayerEvents.STATE_CHANGED, "{ \"newState\": \"" + event.newState.name() + "\" }");
      });

      player.addListener(context, PlayerEvent.tracksAvailable, event -> {
         sendPlayerEvent(KalturaPlayerEvents.TRACKS_AVAILABLE, gson.toJson(getTracksInfo(event.tracksInfo)));
      });

      player.addListener(context, PlayerEvent.loadedMetadata, event -> {
         sendPlayerEvent(KalturaPlayerEvents.LOADED_METADATA);
      });

      player.addListener(context, PlayerEvent.replay, event -> {
         sendPlayerEvent(KalturaPlayerEvents.REPLAY);
      });

      player.addListener(context, PlayerEvent.volumeChanged, event -> {
         sendPlayerEvent(KalturaPlayerEvents.VOLUME_CHANGED , createJSONForEventPayload("volume", event.volume));
      });

      player.addListener(context, PlayerEvent.surfaceAspectRationSizeModeChanged, event -> {
         sendPlayerEvent(KalturaPlayerEvents.ASPECT_RATIO_RESIZE_MODE_CHANGED , createJSONForEventPayload("surfaceAspectRationSizeModeChanged", event.resizeMode.name()));
      });

      player.addListener(context, PlayerEvent.subtitlesStyleChanged, event -> {
         sendPlayerEvent(KalturaPlayerEvents.SUBTITLE_STYLE_CHANGED , createJSONForEventPayload("subtitlesStyleChanged" , event.styleName));
      });

      player.addListener(context, PlayerEvent.videoTrackChanged, event -> {
         final com.kaltura.playkit.player.VideoTrack newTrack = event.newTrack;
         VideoTrack videoTrack = new VideoTrack(newTrack.getUniqueId(), newTrack.getWidth(), newTrack.getHeight(), newTrack.getBitrate(), true, newTrack.isAdaptive());
         sendPlayerEvent(KalturaPlayerEvents.VIDEO_TRACK_CHANGED, gson.toJson(videoTrack));
      });

      player.addListener(context, PlayerEvent.audioTrackChanged, event -> {
         final com.kaltura.playkit.player.AudioTrack newTrack = event.newTrack;
         AudioTrack audioTrack = new AudioTrack(newTrack.getUniqueId(), newTrack.getBitrate(), newTrack.getLanguage(), newTrack.getLabel(), newTrack.getChannelCount(), true);
         sendPlayerEvent(KalturaPlayerEvents.AUDIO_TRACK_CHANGED, gson.toJson(audioTrack));
      });

      player.addListener(context, PlayerEvent.textTrackChanged, event -> {
         final com.kaltura.playkit.player.TextTrack newTrack = event.newTrack;
         TextTrack textTrack = new TextTrack(newTrack.getUniqueId(), newTrack.getLanguage(), newTrack.getLabel(), true);
         sendPlayerEvent(KalturaPlayerEvents.TEXT_TRACK_CHANGED, gson.toJson(textTrack));
      });

      player.addListener(context, PlayerEvent.imageTrackChanged, event -> {
         final com.kaltura.playkit.player.ImageTrack newImageTrack = event.newTrack;

         ImageTrack imageTrack = new ImageTrack(newImageTrack.getUniqueId(), newImageTrack.getLabel(),
                 newImageTrack.getBitrate(),
                 newImageTrack.getWidth(),
                 newImageTrack.getHeight(),
                 newImageTrack.getCols(),
                 newImageTrack.getRows(),
                 newImageTrack.getDuration(),
                 newImageTrack.getUrl(),
                 true);

         sendPlayerEvent(KalturaPlayerEvents.IMAGE_TRACK_CHANGED, gson.toJson(imageTrack));
      });

      player.addListener(context, PlayerEvent.playbackInfoUpdated, event -> {
         long videoBitrate = (event.playbackInfo.getVideoBitrate() > 0) ? event.playbackInfo.getVideoBitrate() : 0;
         long audioBitrate = (event.playbackInfo.getAudioBitrate() > 0) ? event.playbackInfo.getAudioBitrate() : 0;
         long totalBitrate = (videoBitrate + audioBitrate);
         sendPlayerEvent(KalturaPlayerEvents.PLAYBACK_INFO_UPDATED, "{ \"videoBitrate\": " + event.playbackInfo.getVideoBitrate() +
                 ", \"audioBitrate\": " + event.playbackInfo.getAudioBitrate() +
                 ", \"totalBitrate\": " + totalBitrate +
                 " }");
      });

      player.addListener(context, PlayerEvent.seeking, event -> sendPlayerEvent(KalturaPlayerEvents.SEEKING, "{ \"targetPosition\": " + (event.targetPosition / Consts.MILLISECONDS_MULTIPLIER_FLOAT) + " }"));

      player.addListener(context, PlayerEvent.seeked, event -> sendPlayerEvent(KalturaPlayerEvents.SEEKED));

      player.addListener(context, PlayerEvent.error, event -> {
         if (event.error.isFatal()) {
            log.e("error event : " + getErrorJson(event.error));
            sendPlayerEvent(KalturaPlayerEvents.ERROR, getErrorJson(event.error));
         }
      });

      player.addListener(context, PhoenixAnalyticsEvent.bookmarkError, event -> {
         sendPlayerEvent("bookmarkError", "{ \"errorMessage\": \"" + event.errorMessage + "\" " +
                 ", \"errorCode\": \"" + event.errorCode + "\" " +
                 ", \"errorType\": \"" + event.type + "\" " +
                 " }");
      });
      player.addListener(context, PhoenixAnalyticsEvent.concurrencyError, event -> {
         sendPlayerEvent("concurrencyError", "{ \"errorMessage\": \"" + event.errorMessage + "\" " +
                 ", \"errorCode\": \"" + event.errorCode + "\" " +
                 ", \"errorType\": \"" + event.type + "\" " +
                 " }");
      });

      player.addListener(context, BroadpeakEvent.error, event -> {
         sendPlayerEvent("broadpeakError", "{ \"errorMessage\": \"" + event.errorMessage + "\" " +
                 ", \"errorCode\": \"" + event.errorCode + "\" " +
                 ", \"errorType\": \"" + event.type + "\" " +
                 " }");
      });

      player.addListener(context, AdEvent.adProgress, event -> {
         sendPlayerEvent(KalturaPlayerAdEvents.AD_PROGRESS, "{ \"currentAdPosition\": " + (event.currentAdPosition / Consts.MILLISECONDS_MULTIPLIER_FLOAT) + " }");
      });

      player.addListener(context, AdEvent.cuepointsChanged, event -> sendPlayerEvent(KalturaPlayerAdEvents.CUEPOINTS_CHANGED, getCuePointsJson(event.cuePoints)));

      player.addListener(context, AdEvent.started, event -> sendPlayerEvent(KalturaPlayerAdEvents.STARTED));

      player.addListener(context, AdEvent.completed, event -> sendPlayerEvent(KalturaPlayerAdEvents.COMPLETED));

      player.addListener(context, AdEvent.paused, event -> sendPlayerEvent(KalturaPlayerAdEvents.PAUSED));

      player.addListener(context, AdEvent.resumed, event -> sendPlayerEvent(KalturaPlayerAdEvents.RESUMED));

      player.addListener(context, AdEvent.adBufferStart, event -> sendPlayerEvent(KalturaPlayerAdEvents.AD_BUFFER_START));

      player.addListener(context, AdEvent.adClickedEvent, event -> sendPlayerEvent(KalturaPlayerAdEvents.CLICKED, "{ \"clickThruUrl\": \"" + event.clickThruUrl + "\" }"));

      player.addListener(context, AdEvent.skipped, event -> sendPlayerEvent(KalturaPlayerAdEvents.SKIPPED));

      player.addListener(context, AdEvent.adRequested, event -> sendPlayerEvent(KalturaPlayerAdEvents.AD_REQUESTED, "{ \"adTagUrl\": \"" + event.adTagUrl + "\" }"));

      player.addListener(context, AdEvent.contentPauseRequested, event -> sendPlayerEvent(KalturaPlayerAdEvents.CONTENT_RESUME_REQUESTED));

      player.addListener(context, AdEvent.contentResumeRequested, event -> sendPlayerEvent(KalturaPlayerAdEvents.CONTENT_PAUSE_REQUESTED));

      player.addListener(context, AdEvent.allAdsCompleted, event -> sendPlayerEvent(KalturaPlayerAdEvents.ALL_ADS_COMPLETED));

      player.addListener(context, AdEvent.error, event -> {
         if (event.error.isFatal()) {
            sendPlayerEvent(KalturaPlayerAdEvents.ERROR, getErrorJson(event.error));
         }
      });
   }

   private TracksInfo getTracksInfo(PKTracks pkTracksInfo) {
      List<VideoTrack> videoTracksInfo = new ArrayList<>();
      List<AudioTrack> audioTracksInfo = new ArrayList<>();
      List<TextTrack> textTracksInfo = new ArrayList<>();
      List<ImageTrack> imageTracksInfo = new ArrayList<>();

      int videoTrackIndex = 0;
      for (com.kaltura.playkit.player.VideoTrack videoTrack : pkTracksInfo.getVideoTracks()) {
         videoTracksInfo.add(new VideoTrack(videoTrack.getUniqueId(),
                 videoTrack.getWidth(),
                 videoTrack.getHeight(),
                 videoTrack.getBitrate(),
                 pkTracksInfo.getDefaultVideoTrackIndex() == videoTrackIndex,
                 videoTrack.isAdaptive()));
         videoTrackIndex++;
      }

      int audioTrackIndex = 0;
      for (com.kaltura.playkit.player.AudioTrack audioTrack : pkTracksInfo.getAudioTracks()) {
         audioTracksInfo.add(new AudioTrack(audioTrack.getUniqueId(),
                 audioTrack.getBitrate(),
                 audioTrack.getLanguage(),
                 audioTrack.getLabel(),
                 audioTrack.getChannelCount(),
                 pkTracksInfo.getDefaultAudioTrackIndex() == audioTrackIndex));
         audioTrackIndex++;
      }

      int textTrackIndex = 0;
      for (com.kaltura.playkit.player.TextTrack textTrack : pkTracksInfo.getTextTracks()) {
         textTracksInfo.add(new TextTrack(textTrack.getUniqueId(),
                 textTrack.getLanguage(),
                 textTrack.getLabel(),
                 pkTracksInfo.getDefaultTextTrackIndex() == textTrackIndex));
         textTrackIndex++;
      }

      int imageTrackIndex = 0;
      for (com.kaltura.playkit.player.ImageTrack imageTrack : pkTracksInfo.getImageTracks()) {

         imageTracksInfo.add(new ImageTrack(imageTrack.getUniqueId(),
                 imageTrack.getLabel(),
                 imageTrack.getBitrate(),
                 imageTrack.getWidth(),
                 imageTrack.getHeight(),
                 imageTrack.getCols(),
                 imageTrack.getRows(),
                 imageTrack.getDuration(),
                 imageTrack.getUrl(),
                 imageTrackIndex == 0));
         imageTrackIndex++;
      }

      TracksInfo tracksInfo = new TracksInfo();
      tracksInfo.setVideoTracks(videoTracksInfo);
      tracksInfo.setAudioTracks(audioTracksInfo);
      tracksInfo.setTextTracks(textTracksInfo);
      tracksInfo.setImageTracks(imageTracksInfo);

      return tracksInfo;
   }

   private String getErrorJson(PKError error) {
      String errorCause = (error.exception != null) ? error.exception.getCause() + "" : "";

      JsonObject errorJson = new JsonObject();
      errorJson.addProperty("errorType", error.errorType.name());
      if (error.errorType instanceof PKPlayerErrorType) {
         errorJson.addProperty("errorCode", String.valueOf(((PKPlayerErrorType) error.errorType).errorCode));
      } else if (error.errorType instanceof PKAdErrorType) {
         errorJson.addProperty("errorCode", String.valueOf(((PKAdErrorType) error.errorType).errorCode));
      } else {
         errorJson.addProperty("errorCode", String.valueOf(((PKPlayerErrorType) PKPlayerErrorType.UNEXPECTED).errorCode));
      }
      errorJson.addProperty("errorSeverity", error.severity.name());
      errorJson.addProperty("errorMessage", error.message);
      errorJson.addProperty("errorCause", errorCause);

      return gson.toJson(errorJson);
   }

   private String getCuePointsJson(AdCuePoints adCuePoints) {

      if (adCuePoints == null) {
         return null;
      }

      StringBuilder cuePointsList = new StringBuilder("[ ");
      List<Long> adCuePointsArray = adCuePoints.getAdCuePoints();
      for (int i = 0; i < adCuePointsArray.size(); i++) {
         cuePointsList.append(adCuePointsArray.get(i));
         if (i + 1 != adCuePointsArray.size()) {
            cuePointsList.append(", ");
         }
      }
      cuePointsList.append(" ]");

      return "{ " +
              "\"adPluginName\": \"" + adCuePoints.getAdPluginName() +
              "\"," +
              "\"cuePoints\": " + cuePointsList +
              ", " +
              "\"hasPreRoll\": " + adCuePoints.hasPreRoll() +
              ", " +
              "\"hasMidRoll\": " + adCuePoints.hasMidRoll() +
              "," +
              "\"hasPostRoll\": " + adCuePoints.hasPostRoll() +
              " }";
   }

   /**
    * Method to register and set the plugin config for the first time.
    * Method can not be used to update the plugin config
    *
    * @param pluginName `PlayerPlugins` enum
    * @param pluginConfigs `PKPluginConfigs` object for the `PlayerInitOptions`
    * @param pluginConfigJson plugin configuration json
    */
   private void createPlugin(PlayerPlugins pluginName,
                             PKPluginConfigs pluginConfigs,
                             JsonObject pluginConfigJson) {

      PlayKitManager.registerPlugins(context, PlayerPluginUtilsKt.getPluginFactory(pluginName));

      if (pluginConfigJson == null || pluginConfigJson.size() == 0) {
         log.w("Plugins' config Json is not valid hence returning" + PlayerPluginUtilsKt.getPluginClass(pluginName));
         return;
      }

      String strPluginJson = pluginConfigJson.toString();
      if (pluginConfigs != null && !TextUtils.isEmpty(strPluginJson)) {
         Object pluginConfig = getParsedJson(strPluginJson, PlayerPluginUtilsKt.getPluginClass(pluginName));
         if (pluginConfig != null) {
            pluginConfigs.setPluginConfig(PlayerPluginUtilsKt.getPluginFactory(pluginName).getName(), pluginConfig);
         } else {
            log.e("Invalid configuration for " + PlayerPluginUtilsKt.getPluginClass(pluginName).getSimpleName());
         }
      } else {
         log.e("Can not create the plugin " + PlayerPluginUtilsKt.getPluginClass(pluginName).getSimpleName() + " \n " +
                 "pluginConfig is: " + pluginConfigs + " imaConfig json is: " + pluginConfigJson);
      }
   }

   /**
    * Method to update the plugin config on Player
    *
    * @param pluginName `PlayerPlugins` enum
    * @param updatePluginConfig updated plugin configuration json
    */
   private void updatePlugin(PlayerPlugins pluginName, Object updatePluginConfig) {
      PKPlugin.Factory pluginFactory = PlayerPluginUtilsKt.getPluginFactory(pluginName);
      if (player != null && updatePluginConfig != null && !TextUtils.isEmpty(pluginFactory.getName())) {
         player.updatePluginConfig(pluginFactory.getName(), updatePluginConfig);
      }
   }

   private KalturaPlayer.Type getKalturaPlayerType(String playerType) {
      if (TextUtils.equals(playerType, KalturaPlayer.Type.basic.name())) {
         return KalturaPlayer.Type.basic;
      } else if (TextUtils.equals(playerType, KalturaPlayer.Type.ovp.name())) {
         return KalturaPlayer.Type.ovp;
      }
      return KalturaPlayer.Type.ott;
   }

   private String createJSONForEventPayload(String key, Object value) {
      return "{ \" " + key + "\": " + value + "\" }";
   }

   @Nullable
   private <T> T getParsedJson(String parsableJson, Class<T> parsingClass) {
      if (TextUtils.isEmpty(parsableJson)) {
         log.e("getParsedJson parsable Json is empty.");
         return null;
      }

      try {
         return gson.fromJson(parsableJson, parsingClass);
      } catch (JsonSyntaxException exception) {
         log.e("JsonSyntaxException while parsing " + parsingClass.getSimpleName() + "\n and the exception is \n" +
                 exception.getMessage());
      }

      return null;
   }

   /** Device Event Emitter for React Native to android event communication **/

   private DeviceEventManagerModule.RCTDeviceEventEmitter emitter() {
      return context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
   }

   private void sendPlayerEvent(String event) {
      WritableMap params = Arguments.createMap();
      emitter().emit(event, params);
   }

   //   private void sendPlayerEvent(String event, String payload) {
//      WritableMap params = Arguments.createMap();
//      if (!TextUtils.isEmpty(payload)) {
//         params.putString("payload", payload);
//      }
//      //log.d("sendPlayerEvent Event Name: " + event + "Payload is: " + payload) ;
//      emitter().emit(event, params);
//   }
//
   private void sendPlayerEvent(String event, @Nullable String payloadString) {
      WritableMap eventPayloadMap = convertStringToWritableMap(payloadString);
      if (eventPayloadMap == null) {
         log.e("Event payload is null hence returning event is: " + event);
         return;
      }

      emitter().emit(event, eventPayloadMap);
   }

   @Nullable
   private WritableMap convertStringToWritableMap(String payload) {
      //log.v("convertStringToJson");

      if (TextUtils.isEmpty(payload)) {
         log.e("Payload string is invalid " + payload);
         return null;
      }

      try {
         JSONObject jsonObject = new JSONObject(payload);
         return convertJsonToMap(jsonObject);
      } catch (JSONException e) {
         log.e("Payload string can not be converted to JSON object " + payload);
      }

      return null;
   }

   @Nullable
   private WritableMap convertJsonToMap(JSONObject jsonObject) {
      //log.v("convertJsonToMap");

      WritableMap map = new WritableNativeMap();
      Iterator<String> iterator = jsonObject.keys();

      while (iterator.hasNext()) {
         String key = iterator.next();
         Object value = null;
         try {
            value = jsonObject.get(key);
         } catch (JSONException e) {
            log.e("Exception while parsing Json object : " + key + " Exception is : " + e.getMessage());
         }

         if (value == null) {
            log.e("Exception while parsing Json object value is null, hence returning null.");
            return null;
         }

         if (value instanceof JSONObject) {
            map.putMap(key, convertJsonToMap((JSONObject) value));
         } else if (value instanceof JSONArray) {
            try {
               map.putArray(key, convertJsonToArray((JSONArray) value));
            } catch (JSONException e) {
               log.e("Exception while parsing Json Array key: " + key + "value is: " + value + "Exception is : " + e.getMessage());
            }
         } else if (value instanceof  Boolean) {
            map.putBoolean(key, (Boolean) value);
         } else if (value instanceof  Integer) {
            map.putInt(key, (Integer) value);
         } else if (value instanceof  Double) {
            map.putDouble(key, (Double) value);
         } else if (value instanceof String)  {
            map.putString(key, (String) value);
         } else {
            map.putString(key, value.toString());
         }
      }
      return map;
   }

   @NonNull
   private WritableArray convertJsonToArray(JSONArray jsonArray) throws JSONException {
      WritableArray array = new WritableNativeArray();

      for (int i = 0; i < jsonArray.length(); i++) {
         Object value = jsonArray.get(i);
         if (value instanceof JSONObject) {
            array.pushMap(convertJsonToMap((JSONObject) value));
         } else if (value instanceof  JSONArray) {
            array.pushArray(convertJsonToArray((JSONArray) value));
         } else if (value instanceof  Boolean) {
            array.pushBoolean((Boolean) value);
         } else if (value instanceof  Integer) {
            array.pushInt((Integer) value);
         } else if (value instanceof  Double) {
            array.pushDouble((Double) value);
         } else if (value instanceof String)  {
            array.pushString((String) value);
         } else {
            array.pushString(value.toString());
         }
      }
      return array;
   }
}

