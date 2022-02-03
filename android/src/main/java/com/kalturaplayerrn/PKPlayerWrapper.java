package com.kalturaplayerrn;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.facebook.react.bridge.LifecycleEventListener;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.modules.core.DeviceEventManagerModule;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class PKPlayerWrapper {
  private static final PKLog log = PKLog.get("PKPlayerWrapper");
  private static final String YOUBORA_ACCOUNT_CODE = "accountCode";

  public static KalturaPlayer player;   // singleton player
  // public static ViewGroup parentView;
  public static ReactApplicationContext reactContext;
  private static LifecycleEventListener lifecycleListener;
  private static Handler mainHandler = new Handler(Looper.getMainLooper());
  private static Class self = PKPlayerWrapper.class;
  private static Gson gson = new Gson();

  private static void runOnUiThread(Runnable runnable) {
    mainHandler.post(runnable);
  }

  private static boolean initialized;
  private static long reportedDuration = Consts.TIME_UNSET;
  static ByteBuffer bridgeInstance;

  static void sendEvent(String name, String payload) {
    if (payload == null)
      payload = "";
    // send via bridge
    reactContext
      .getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
      .emit(name, payload);

    // send via JSI (doesn't work fro now)
    // nativeSendEvent(name, payload);
  }

  private static native void nativeSendEvent(String name, String payload);


  @SuppressWarnings("unused") // Called from C++
  public static void setup(int partnerId, String jsonOptionsStr) {

    log.d("setup:" + partnerId  + ", " + jsonOptionsStr);

    if (initialized) {
      log.d("*** initialized:" + partnerId  + ", " + jsonOptionsStr);
      return;
    }

    if (self == null) {
      self = PKPlayerWrapper.class;
    }

    if (mainHandler == null) {
      mainHandler = new Handler(Looper.getMainLooper());
    }

    if (reactContext.getCurrentActivity() == null) {
      log.e("*** reactContext.getCurrentActivity() == null");
      return;
    }

    Context context = reactContext.getCurrentActivity();
    reactContext.addLifecycleEventListener(getLifeCycleListener());

    InitOptions initOptionsModel = gson.fromJson(jsonOptionsStr, InitOptions.class);
    if (initOptionsModel == null) {
      return;
    }

    if (initOptionsModel.warmupUrls != null && !initOptionsModel.warmupUrls.isEmpty()) {
      PKHttpClientManager.setHttpProvider("okhttp");
      PKHttpClientManager.warmUp((initOptionsModel.warmupUrls).toArray((new String[0])));
    }

    // load the player and put it in the main frame
    KalturaOttPlayer.initialize(context, partnerId, initOptionsModel.serverUrl);
    PKPluginConfigs pluginConfigs = new PKPluginConfigs();
    if (initOptionsModel.plugins != null) {
      if (initOptionsModel.plugins.ima != null) {
        createIMAPlugin(pluginConfigs, initOptionsModel.plugins.ima); //DEFAULT
      }

      if (initOptionsModel.plugins.youbora != null) {
        JsonObject youboraConfigJson = initOptionsModel.plugins.youbora;
        if (youboraConfigJson.has(YOUBORA_ACCOUNT_CODE) && youboraConfigJson.get(YOUBORA_ACCOUNT_CODE) != null) {
          YouboraConfig youboraConfig = gson.fromJson(youboraConfigJson, YouboraConfig.class);
          if (youboraConfig != null) {
            createYouboraPlugin(pluginConfigs, youboraConfig);
          }
        }
      }

      if (initOptionsModel.plugins.ottAnalytics != null) {
        createPhoenixAnalyticsPlugin(pluginConfigs, initOptionsModel.plugins.ottAnalytics); //DEFAULT
      }

      if (initOptionsModel.plugins.broadpeak != null) {
        JsonObject broadpeakJsonObject = initOptionsModel.plugins.broadpeak;

        BroadpeakConfig broadpeakConfig = gson.fromJson(broadpeakJsonObject.toString(), BroadpeakConfig.class);
        createBroadpeakPlugin(pluginConfigs, broadpeakConfig);
      }
    }

    final PlayerInitOptions initOptions = new PlayerInitOptions(partnerId);
    initOptions.setAutoPlay(initOptionsModel.autoplay);
    initOptions.setPreload(initOptionsModel.preload);
    if (initOptionsModel.requestConfig != null) {
      initOptions.setPKRequestConfig(initOptionsModel.requestConfig);
    } else {
      initOptions.setAllowCrossProtocolEnabled(initOptionsModel.allowCrossProtocolRedirect);
    }
    initOptions.setKs(initOptionsModel.ks);
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
    initOptions.setMulticastSettings(initOptionsModel.multicastSettings);
    initOptions.setMediaEntryCacheConfig(initOptionsModel.mediaEntryCacheConfig);
    initOptions.setHandleAudioFocus(true);

    NetworkSettings networkSettings = initOptionsModel.networkSettings;
    if (initOptionsModel.networkSettings != null && initOptionsModel.networkSettings.preferredForwardBufferDuration > 0) {
      initOptions.setLoadControlBuffers(new LoadControlBuffers().setMaxPlayerBufferMs(initOptionsModel.networkSettings.preferredForwardBufferDuration));
    }

    if (initOptionsModel.trackSelection != null && initOptionsModel.trackSelection.audioLanguage != null && initOptionsModel.trackSelection.audioMode != null) {
      initOptions.setAudioLanguage(initOptionsModel.trackSelection.audioLanguage, initOptionsModel.trackSelection.audioMode);
    }
    if (initOptionsModel.trackSelection != null && initOptionsModel.trackSelection.textLanguage != null && initOptionsModel.trackSelection.textMode != null) {
      initOptions.setTextLanguage(initOptionsModel.trackSelection.textLanguage, initOptionsModel.trackSelection.textMode);
    }

    //initOptions.setVideoCodecSettings(appPlayerInitConfig.videoCodecSettings)
    //initOptions.setAudioCodecSettings(appPlayerInitConfig.audioCodecSettings)
    initOptions.setPluginConfigs(pluginConfigs);

    runOnUiThread(() -> {

      if (reactContext.getCurrentActivity() != null) {
        player = KalturaOttPlayer.create(context, initOptions);
        player.setPlayerView(MATCH_PARENT, MATCH_PARENT);
        ViewGroup parent = (ViewGroup)reactContext.getCurrentActivity().getWindow().getDecorView().getRootView();
        parent.addView(player.getPlayerView(), 0);
        setZIndex(0);
        addKalturaPlayerListeners();
        initialized = true;
      } else {
        initialized = false;
      }
    });
  }

  private static LifecycleEventListener getLifeCycleListener() {
    if (lifecycleListener == null) {
      lifecycleListener = new LifecycleEventListener() {
        @Override
        public void onHostResume() {
          log.d("addLifecycleListener onHostResume");
          if (player != null) {
            player.onApplicationResumed();
          }
        }

        @Override
        public void onHostPause() {
          log.d("addLifecycleListener onHostPause");
          if (player != null) {
            player.onApplicationPaused();
          }
        }

        @Override
        public void onHostDestroy() {
        }
      };
    }
    return lifecycleListener;
  }

  private static void addKalturaPlayerListeners() {
    log.d("addKalturaPlayerListeners");

    if (player == null) {
      return;
    }

    player.addListener(self, PlayerEvent.canPlay, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"canPlay\" }"));
    player.addListener(self, PlayerEvent.playing, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"playing\" }"));
    player.addListener(self, PlayerEvent.play, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"play\" }"));
    player.addListener(self, PlayerEvent.pause, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"pause\" }"));
    player.addListener(self, PlayerEvent.ended, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"ended\" }"));
    player.addListener(self, PlayerEvent.stopped, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"stopped\" }"));
    player.addListener(self, PlayerEvent.durationChanged, event -> {
      reportedDuration = event.duration;
      sendPlayerEvent("KPlayerEvent", "{ \"type\": \"durationChanged\", \"duration\": " + (event.duration / Consts.MILLISECONDS_MULTIPLIER_FLOAT) + " }");
    });
    player.addListener(self, PlayerEvent.playheadUpdated, new PKEvent.Listener<PlayerEvent.PlayheadUpdated>() {
      @Override
      public void onEvent(PlayerEvent.PlayheadUpdated event) {

        String timeUpdatePayload = "\"type\": \"timeUpdate\", \"position\": " + (event.position / Consts.MILLISECONDS_MULTIPLIER_FLOAT) +
          ", \"bufferPosition\": " + (event.bufferPosition / Consts.MILLISECONDS_MULTIPLIER_FLOAT);

        if (player != null && player.isLive() &&  player.getCurrentProgramTime() > 0) {
          timeUpdatePayload = "{ " + timeUpdatePayload +
            ", \"currentProgramTime\": " + player.getCurrentProgramTime() +
            " }";
        } else {
          timeUpdatePayload = "{ " + timeUpdatePayload  +
            " }";
        }

        sendPlayerEvent("KPlayerEvent", timeUpdatePayload);
        if (reportedDuration != event.duration && event.duration > 0) {
          reportedDuration = event.duration;
          if (player != null && player.getMediaEntry() != null && player.getMediaEntry().getMediaType() != PKMediaEntry.MediaEntryType.Vod /*|| player.isLive()*/) {
            sendPlayerEvent("KPlayerEvent", "{ \"type\": \"loadedTimeRanges\", \"timeRanges\": [ { \"start\": " + 0 +
              ", \"end\": " + (event.duration / Consts.MILLISECONDS_MULTIPLIER_FLOAT) +
              " } ] }");
          }
        }
      }
    });
    player.addListener(self, PlayerEvent.stateChanged, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"stateChanged\", \"newState\": \"" + event.newState.name() + "\" }"));
    player.addListener(self, PlayerEvent.tracksAvailable, event -> {
      TracksInfo info = getTracksInfo(event.tracksInfo);
      info.type = "tracksAvailable";
      sendPlayerEvent("KPlayerEvent", gson.toJson(info));
    });

    player.addListener(self, PlayerEvent.videoTrackChanged, event -> {
      final com.kaltura.playkit.player.VideoTrack newTrack = event.newTrack;
      VideoTrack videoTrack = new VideoTrack(newTrack.getUniqueId(), newTrack.getWidth(), newTrack.getHeight(), newTrack.getBitrate(), true, newTrack.isAdaptive());
      videoTrack.type = "videoTrackChanged";
      sendPlayerEvent("KPlayerEvent", gson.toJson(videoTrack));
    });

    player.addListener(self, PlayerEvent.audioTrackChanged, event -> {
      final com.kaltura.playkit.player.AudioTrack newTrack = event.newTrack;
      AudioTrack audioTrack = new AudioTrack(newTrack.getUniqueId(), newTrack.getBitrate(), newTrack.getLanguage(), newTrack.getLabel(), newTrack.getChannelCount(), true);
      audioTrack.type = "audioTrackChanged";
      sendPlayerEvent("KPlayerEvent", gson.toJson(audioTrack));
    });

    player.addListener(self, PlayerEvent.textTrackChanged, event -> {
      final com.kaltura.playkit.player.TextTrack newTrack = event.newTrack;
      TextTrack textTrack = new TextTrack(newTrack.getUniqueId(), newTrack.getLanguage(), newTrack.getLabel(), true);
      textTrack.type = "textTrackChanged";
      sendPlayerEvent("KPlayerEvent", gson.toJson(textTrack));
    });

    player.addListener(self, PlayerEvent.imageTrackChanged, event -> {
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
      imageTrack.type = "imageTrackChanged";

      sendPlayerEvent("KPlayerEvent", gson.toJson(imageTrack));
    });

    player.addListener(self, PlayerEvent.playbackInfoUpdated, event -> {
      long videoBitrate = (event.playbackInfo.getVideoBitrate() > 0) ? event.playbackInfo.getVideoBitrate() : 0;
      long audioBitrate = (event.playbackInfo.getAudioBitrate() > 0) ? event.playbackInfo.getAudioBitrate() : 0;
      long totalBitrate = (videoBitrate + audioBitrate);
      sendPlayerEvent("KPlayerEvent", "{ \"type\": \"playbackInfoUpdated\", \"videoBitrate\": " + event.playbackInfo.getVideoBitrate() +
        ", \"audioBitrate\": " + event.playbackInfo.getAudioBitrate() +
        ", \"totalBitrate\": " + totalBitrate  +
        " }");
    });

    player.addListener(self, PlayerEvent.seeking, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"seeking\", \"targetPosition\": " + (event.targetPosition / Consts.MILLISECONDS_MULTIPLIER_FLOAT) + " }"));
    player.addListener(self, PlayerEvent.seeked, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"seeked\" }"));
    player.addListener(self, PlayerEvent.error, event -> {
      if (event.error.isFatal()) {
        log.e("error event : " + getErrorJson(event.error, "error"));
        sendPlayerEvent("KPlayerEvent", getErrorJson(event.error, "error"));
      }
    });

    player.addListener(self, PhoenixAnalyticsEvent.bookmarkError, event -> {
      sendPlayerEvent("KPlayerEvent", "{ \"type\": \"bookmarkError\", \"errorMessage\": \"" + event.errorMessage + "\" " +
        ", \"errorCode\": \"" + event.errorCode + "\" " +
        ", \"errorType\": \"" + event.type + "\" " +
        " }");
    });
    player.addListener(self, PhoenixAnalyticsEvent.concurrencyError, event -> {
      sendPlayerEvent("KPlayerEvent", "{ \"type\": \"concurrencyError\", \"errorMessage\": \"" + event.errorMessage + "\" " +
        ", \"errorCode\": \"" + event.errorCode + "\" " +
        ", \"errorType\": \"" + event.type + "\" " +
        " }");
    });

    player.addListener(self, BroadpeakEvent.error, event -> {
      sendPlayerEvent("KPlayerEvent", "{ \"type\": \"broadpeakError\", \"errorMessage\": \"" + event.errorMessage + "\" " +
        ", \"errorCode\": \"" + event.errorCode + "\" " +
        ", \"errorType\": \"" + event.type + "\" " +
        " }");
    });

    player.addListener(self, AdEvent.adProgress, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"adProgress\", \"currentAdPosition\": " + (event.currentAdPosition / Consts.MILLISECONDS_MULTIPLIER_FLOAT) + " }"));
    player.addListener(self, AdEvent.cuepointsChanged, event -> sendPlayerEvent("KPlayerEvent", getCuePointsJson(event.cuePoints)));
    player.addListener(self, AdEvent.started, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"adStarted\" }"));
    player.addListener(self, AdEvent.completed, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"adCompleted\" }"));
    player.addListener(self, AdEvent.paused, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"adPaused\" }"));
    player.addListener(self, AdEvent.resumed, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"adResumed\" }"));
    player.addListener(self, AdEvent.adBufferStart, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"adBufferStart\" }"));
    player.addListener(self, AdEvent.adClickedEvent, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"adClicked\", \"clickThruUrl\": \"" + event.clickThruUrl + "\" }"));
    player.addListener(self, AdEvent.skipped, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"adSkipped\" }"));
    player.addListener(self, AdEvent.adRequested, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"adRequested\", \"adTagUrl\": \"" +  event.adTagUrl + "\" }"));
    player.addListener(self, AdEvent.contentPauseRequested, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"adContentPauseRequested\" }"));
    player.addListener(self, AdEvent.contentResumeRequested, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"adContentResumeRequested\" }"));
    player.addListener(self, AdEvent.allAdsCompleted, event -> sendPlayerEvent("KPlayerEvent", "{ \"type\": \"allAdsCompleted\" }"));
    player.addListener(self, AdEvent.error, event -> {
      if (event.error.isFatal()) {
        sendPlayerEvent("KPlayerEvent", getErrorJson(event.error, "adError"));
      }
    });
  }

  private static TracksInfo getTracksInfo(PKTracks pkTracksInfo) {
    List<VideoTrack> videoTracksInfo = new ArrayList<>();
    List<AudioTrack> audioTracksInfo = new ArrayList<>();
    List<TextTrack>  textTracksInfo  = new ArrayList<>();
    List<ImageTrack> imageTracksInfo  = new ArrayList<>();


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
        (imageTrackIndex == 0) ? true : false));
      imageTrackIndex++;
    }

    TracksInfo tracksInfo = new TracksInfo();
    tracksInfo.setVideoTracks(videoTracksInfo);
    tracksInfo.setAudioTracks(audioTracksInfo);
    tracksInfo.setTextTracks(textTracksInfo);
    tracksInfo.setImageTracks(imageTracksInfo);

    return tracksInfo;
  }

  private static String getErrorJson(PKError error, String type) {
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
    errorJson.addProperty("type", type);

    return gson.toJson(errorJson);
  }

  private static String getCuePointsJson(AdCuePoints adCuePoints) {

    if (adCuePoints == null) {
      return null;
    }

    StringBuilder cuePointsList = new StringBuilder("[ ");
    List<Long> adCuePointsArray = adCuePoints.getAdCuePoints();
    for (int i = 0; i < adCuePointsArray.size() ; i++) {
      cuePointsList.append(adCuePointsArray.get(i));
      if (i + 1 != adCuePointsArray.size()) {
        cuePointsList.append(", ");
      }
    }
    cuePointsList.append(" ]");

    return "{ type: 'adCuepointsChanged', " +
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

  @SuppressWarnings("unused") // Called from C++
  public static void load(String assetId, String jsonOptionsStr) {

    log.d("load assetId: " + assetId + ", jsonOptionsStr:" + jsonOptionsStr);

    MediaAsset mediaAsset = gson.fromJson(jsonOptionsStr, MediaAsset.class);

    if (mediaAsset == null || player == null) {
      return;
    }

    runOnUiThread(() -> {

      if (mediaAsset.getPlugins() != null) {
        if (mediaAsset.getPlugins().ima != null) {
          updateIMAPlugin(mediaAsset.getPlugins().ima);
        }

        if (mediaAsset.getPlugins().youbora != null) {
          updateYouboraPlugin(mediaAsset.getPlugins().youbora);
        }

        if (mediaAsset.getPlugins().ottAnalytics != null) {
          updatePhoenixAnalyticsPlugin(mediaAsset.getPlugins().ottAnalytics);
        }
      }

      final PKMediaEntry localPlaybackEntry = PKDownloadWrapper.getLocalPlaybackEntry(assetId);
      if (localPlaybackEntry != null) {
        player.setMedia(localPlaybackEntry);
      } else {
        player.loadMedia(mediaAsset.buildOttMediaOptions(assetId, player.getKS()), (mediaOptions, entry, error) -> {
          if (error != null) {
            JsonElement errorTree = gson.toJsonTree(error);
            errorTree.getAsJsonObject().addProperty("type", "loadMediaFailed");
            log.e("ott media load error: " + error.getName() + " " + error.getCode() + " " + error.getMessage());
            sendPlayerEvent("KPlayerEvent", gson.toJson(errorTree));
          } else {
            JsonElement entryTree = gson.toJsonTree(entry);
            entryTree.getAsJsonObject().addProperty("type", "loadMediaSuccess");
            log.d("ott media load success name = " + entry.getName());
            sendPlayerEvent("KPlayerEvent", gson.toJson(entryTree));
          }
        });
      }
    });
  }

  private static void updateIMAPlugin(WrapperIMAConfig wrapperIMAConfig) {
    if (player != null) {
      runOnUiThread(() -> player.updatePluginConfig(IMAPlugin.factory.getName(), getIMAConfig(wrapperIMAConfig)));
    }
  }

  private static void updateYouboraPlugin(YouboraConfig youboraConfig) {
    if (player != null) {
      runOnUiThread(() -> player.updatePluginConfig(YouboraPlugin.factory.getName(), youboraConfig));
    }
  }

  private static void updatePhoenixAnalyticsPlugin(WrapperPhoenixAnalyticsConfig wrapperPhoenixAnalyticsConfig) {
    if (player != null && wrapperPhoenixAnalyticsConfig != null) {
      runOnUiThread(() -> player.updatePluginConfig(PhoenixAnalyticsPlugin.factory.getName(), wrapperPhoenixAnalyticsConfig.toJson()));
    }
  }

  private static void createIMAPlugin(PKPluginConfigs pluginConfigs, JsonObject imaConfigJson) {

    PlayKitManager.registerPlugins(reactContext.getCurrentActivity(), IMAPlugin.factory);
    if (pluginConfigs != null) {
      if (imaConfigJson == null) {
        pluginConfigs.setPluginConfig(IMAPlugin.factory.getName(), new IMAConfig());
      } else {
        pluginConfigs.setPluginConfig(IMAPlugin.factory.getName(), imaConfigJson);
      }
    }
  }

  private static IMAConfig getIMAConfig(WrapperIMAConfig wrapperIMAConfig) {
    IMAConfig imaConfig = new IMAConfig();

    if (wrapperIMAConfig != null) {
      String adTagUrl = (wrapperIMAConfig.getAdTagUrl() != null) ? wrapperIMAConfig.getAdTagUrl() : "";
      if (!TextUtils.isEmpty(wrapperIMAConfig.getAdTagResponse()) && TextUtils.isEmpty(adTagUrl)) {
        imaConfig.setAdTagResponse(wrapperIMAConfig.getAdTagResponse());
      } else {
        imaConfig.setAdTagUrl(adTagUrl);
      }
      imaConfig.setVideoBitrate(wrapperIMAConfig.getVideoBitrate());
      imaConfig.enableDebugMode(wrapperIMAConfig.isEnableDebugMode());
      imaConfig.setVideoMimeTypes(wrapperIMAConfig.getVideoMimeTypes());
      imaConfig.setAdLoadTimeOut(wrapperIMAConfig.getAdLoadTimeOut());
    }
    return imaConfig;
  }

  private static void createYouboraPlugin(PKPluginConfigs pluginConfigs, YouboraConfig youboraConfig) {

    PlayKitManager.registerPlugins(reactContext.getCurrentActivity(), YouboraPlugin.factory);
    if (pluginConfigs != null) {
      pluginConfigs.setPluginConfig(YouboraPlugin.factory.getName(), youboraConfig);
    }
  }

  private static void createBroadpeakPlugin(PKPluginConfigs pluginConfigs, BroadpeakConfig broadpeakConfig) {
    PlayKitManager.registerPlugins(reactContext.getCurrentActivity(), BroadpeakPlugin.factory);
    if (pluginConfigs != null) {
      pluginConfigs.setPluginConfig(BroadpeakPlugin.factory.getName(), broadpeakConfig);
    }
  }

  private static void createPhoenixAnalyticsPlugin(PKPluginConfigs pluginConfigs, JsonObject phoenixAnalyticsConfigJson) {
    PlayKitManager.registerPlugins(reactContext.getCurrentActivity(), PhoenixAnalyticsPlugin.factory);
    if (pluginConfigs != null) {
      if (phoenixAnalyticsConfigJson == null) {
        pluginConfigs.setPluginConfig(PhoenixAnalyticsPlugin.factory.getName(), new PhoenixAnalyticsConfig(-1, "", "", Consts.DEFAULT_ANALYTICS_TIMER_INTERVAL_HIGH_SEC));
      } else {
        pluginConfigs.setPluginConfig(PhoenixAnalyticsPlugin.factory.getName(), phoenixAnalyticsConfigJson);
      }
    }
  }

  @SuppressWarnings("unused") // Called from C++
  public static void prepare() {
    log.d("prepare");
    reportedDuration = Consts.TIME_UNSET;
    if (player != null) {
      runOnUiThread(() -> player.prepare());
    }
  }

  @SuppressWarnings("unused") // Called from C++
  public static void play() {
    log.d("play");
    if (player != null) {
      runOnUiThread(() -> player.play());
    }
  }

  @SuppressWarnings("unused") // Called from C++
  public static void pause() {
    log.d("pause");
    if (player != null) {
      runOnUiThread(() -> player.pause());
    }
  }

  @SuppressWarnings("unused") // Called from C++
  public static void replay() {
    log.d("replay");
    if (player != null) {
      runOnUiThread(() -> player.replay());
    }
  }

  @SuppressWarnings("unused") // Called from C++
  public static void seekTo(double position) {
    long posMS = (long)(position * Consts.MILLISECONDS_MULTIPLIER);
    log.d("seekTo:" + posMS);
    if (player != null) {
      runOnUiThread(() -> player.seekTo(posMS));
    }
  }

  @SuppressWarnings("unused") // Called from C++
  public static void changeTrack(String uniqueId) {
    log.d("changeTrack:" + uniqueId);
    if (player != null) {
      runOnUiThread(() -> player.changeTrack(uniqueId));
    }
  }

  @SuppressWarnings("unused") // Called from C++
  public static void changePlaybackRate(double playbackRate) {

    log.d("changePlaybackRate:" + playbackRate);
    if (player != null) {
      runOnUiThread(() -> player.setPlaybackRate((float) playbackRate));
    }
  }

  @SuppressWarnings("unused") // Called from C++
  public static void stop() {
    log.d("stop");
    if (player != null) {
      runOnUiThread(() -> player.stop());
    }
  }

  @SuppressWarnings("unused") // Called from C++
  public static void destroy() {
    log.d("destroy");
//        if (lifecycleListener != null) {
//            activity.removeLifecycleListener(lifecycleListener);
//            lifecycleListener = null;
//        }

    runOnUiThread(() -> {
      if (player != null) {
        player.removeListeners(self);
        player.pause();
        player.destroy();
        player = null;
      }
    });

    mainHandler = null;
    bridgeInstance = null;
    initialized = false;
    reportedDuration = Consts.TIME_UNSET;
    self = null;
  }

  @SuppressWarnings("unused") // Called from C++
  public static void setAutoplay(boolean autoplay) {

    log.d("setAutoplay: " + autoplay);
    if (player != null) {
      runOnUiThread(() -> player.setAutoPlay(autoplay));
    }
  }

  @SuppressWarnings("unused") // Called from C++
  public static void setKS(String ks) {

    log.d("setKS: " + ks);
    if (player != null) {
      runOnUiThread(() -> player.setKS(ks));
    }
  }

  @SuppressWarnings("unused") // Called from C++
  public static void setZIndex(double index) {

    log.d("setZIndex: " + index);
    if (player != null && player.getPlayerView() != null) {
      runOnUiThread(() -> player.getPlayerView().setZ((float) index));
    }
  }

  @SuppressWarnings("unused") // Called from C++
  public static void setFrame(int playerViewWidth, int playerViewHeight, int playerViewPosX, int playerViewPosY) {

    log.d("setFrame " + playerViewWidth + "/" + playerViewHeight + " " + playerViewPosX + "/" + playerViewPosY);

    if (player != null && player.getPlayerView() != null) {
      runOnUiThread(() -> {
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
      });
    }
  }

  @SuppressWarnings("unused") // Called from C++
  public static void setVolume(double volume) {

    log.d("setVolume: " + volume);
    if (volume < 0) {
      volume = 0f;
    } else if (volume > 1) {
      volume = 1.0f;
    }

    if (player != null) {
      float finalPlayerVolume = (float) volume;
      runOnUiThread(() -> player.setVolume(finalPlayerVolume));
    }
  }

  @SuppressWarnings("unused") // Called from C++
  public static void setPlayerVisibility(boolean isVisible) {

    log.d("setPlayerVisibility: " + isVisible);
    if (player != null && player.getPlayerView() != null) {
      if (isVisible) {
        runOnUiThread(() -> player.getPlayerView().setVisibility(View.VISIBLE));
      } else {
        runOnUiThread(() -> player.getPlayerView().setVisibility(View.INVISIBLE));
      }
    }
  }

  @SuppressWarnings("unused") // Called from C++
  public static void requestThumbnailInfo(double positionMs) {

    log.d("requestThumbnailInfo:" + positionMs);

    if (player != null) {
      runOnUiThread(() -> {
        ThumbnailInfo thumbnailInfo = player.getThumbnailInfo((long) positionMs);
        if (thumbnailInfo != null && positionMs >= 0) {
          String thumbnailInfoJson = "{ type: 'thumbnailInfoResponse', position: " + positionMs + ", thumbnailInfo: " + gson.toJson(thumbnailInfo) + " }";
          sendPlayerEvent("KPlayerEvent", thumbnailInfoJson);
        } else {
          log.e("requestThumbnailInfo: thumbnailInfo is null or position is invalid");
        }
      });
    }
  }

  @SuppressWarnings("unused") // Called from C++
  public static void setLogLevel(String logLevel) {

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

  static void sendPlayerEvent(String name, String payload) {
    sendEvent(name, payload);
  }

  static void sendPlayerEvent(String name) {
    sendPlayerEvent(name, null);
  }
}
