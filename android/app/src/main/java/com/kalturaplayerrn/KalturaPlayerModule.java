package com.kalturaplayerrn;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.kaltura.netkit.utils.NKLog;
import com.kaltura.playkit.PKError;
import com.kaltura.playkit.PKEvent;
import com.kaltura.playkit.PKLog;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PKPluginConfigs;
import com.kaltura.playkit.PlayKitManager;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.playkit.ads.PKAdErrorType;
import com.kaltura.playkit.player.LoadControlBuffers;
import com.kaltura.playkit.player.PKHttpClientManager;
import com.kaltura.playkit.player.PKPlayerErrorType;
import com.kaltura.playkit.player.PKTracks;
import com.kaltura.playkit.player.thumbnail.ThumbnailInfo;
import com.kaltura.playkit.plugins.ads.AdCuePoints;
import com.kaltura.playkit.plugins.ads.AdEvent;
import com.kaltura.playkit.plugins.broadpeak.BroadpeakConfig;
import com.kaltura.playkit.plugins.broadpeak.BroadpeakEvent;
import com.kaltura.playkit.plugins.broadpeak.BroadpeakPlugin;
import com.kaltura.playkit.plugins.ima.IMAConfig;
import com.kaltura.playkit.plugins.ima.IMAPlugin;
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsConfig;
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsEvent;
import com.kaltura.playkit.plugins.ott.PhoenixAnalyticsPlugin;
import com.kaltura.playkit.plugins.youbora.YouboraPlugin;
import com.kaltura.playkit.plugins.youbora.pluginconfig.YouboraConfig;
import com.kaltura.playkit.utils.Consts;
import com.kaltura.tvplayer.KalturaBasicPlayer;
import com.kaltura.tvplayer.KalturaOttPlayer;
import com.kaltura.tvplayer.KalturaPlayer;
import com.kaltura.tvplayer.PlayerInitOptions;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;
import com.kalturaplayerrn.model.InitOptions;
import com.kalturaplayerrn.model.MediaAsset;
import com.kalturaplayerrn.model.NetworkSettings;
import com.kalturaplayerrn.model.WrapperIMAConfig;
import com.kalturaplayerrn.model.WrapperPhoenixAnalyticsConfig;
import com.kalturaplayerrn.model.tracks.AudioTrack;
import com.kalturaplayerrn.model.tracks.TextTrack;
import com.kalturaplayerrn.model.tracks.TracksInfo;
import com.kalturaplayerrn.model.tracks.VideoTrack;
import com.kalturaplayerrn.model.tracks.ImageTrack;
import com.npaw.youbora.lib6.YouboraLog;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;


public class KalturaPlayerModule extends ReactContextBaseJavaModule {

    private static final PKLog log = PKLog.get("KalturaPlayerModule");
    private static final String YOUBORA_ACCOUNT_CODE = "accountCode";

    ReactApplicationContext context;
    private KalturaPlayer player;   // singleton player
    private long reportedDuration = Consts.TIME_UNSET;
    private boolean initialized;


    KalturaPlayerModule(ReactApplicationContext context) {
        super(context);
        this.context = context;
    }

    @NotNull
    @Override
    public String getName() {
        return "KalturaPlayerModule";
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void createKalturaPlayerEvent(String url, String location) {
        Log.d("KalturaPlayerModule", "Create event called with name: " + url
                + " and location: " + location);
        Toast.makeText(getReactApplicationContext(), url + " " + location, Toast.LENGTH_LONG).show();

        getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                loadBasicPlayer(url);
            }
        });
    }

    public void loadBasicPlayer(String url) {
        PKMediaEntry mediaEntry = createMediaEntry(url);

        PlayerInitOptions playerInitOptions = new PlayerInitOptions();
        playerInitOptions.setAutoPlay(true);

        KalturaPlayer player = KalturaBasicPlayer.create(context, playerInitOptions);
        player.setPlayerView(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        getCurrentActivity().addContentView(player.getPlayerView(), new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        player.addListener(this, PlayerEvent.tracksAvailable, event -> {
            log.d("Event tracksAvailable tracksInfo.getVideoTracks().size() " + (event.tracksInfo.getVideoTracks().size()));
            WritableMap params = Arguments.createMap();
            params.putString("eventProperty1", "someValue1");
            params.putString("eventProperty2", "someValue2");
            sendPlayerEvent("EventReminder", params);
        });

        player.setMedia(mediaEntry);
    }

    private PKMediaEntry createMediaEntry(String url) {
        //Create media entry.
        PKMediaEntry mediaEntry = new PKMediaEntry();

        //Set id for the entry.
        mediaEntry.setId("testEntry");
        mediaEntry.setName("testEntryName");
        mediaEntry.setDuration(881000);
        mediaEntry.setMediaType(PKMediaEntry.MediaEntryType.Vod);
        List<PKMediaSource> mediaSources = createMediaSources(url);
        mediaEntry.setSources(mediaSources);

        return mediaEntry;
    }

    /**
     * Create list of {@link PKMediaSource}.
     *
     * @return - the list of sources.
     */
    private List<PKMediaSource> createMediaSources(String url) {

        //Create new PKMediaSource instance.
        PKMediaSource mediaSource = new PKMediaSource();

        //Set the id.
        mediaSource.setId("testSource");

        //Set the content url. In our case it will be link to hls source(.m3u8).
        mediaSource.setUrl(url);

        //Set the format of the source. In our case it will be hls in case of mpd/wvm formats you have to to call mediaSource.setDrmData method as well
        mediaSource.setMediaFormat(PKMediaFormat.hls);

        return Collections.singletonList(mediaSource);
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void setup(int partnerId, String jsonOptionsStr) {

        log.d("setup:" + partnerId  + ", " + jsonOptionsStr);

        if (initialized) {
            log.d("*** initialized:" + partnerId  + ", " + jsonOptionsStr);
            return;
        }

        Gson gson = new Gson();
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
        initOptions.setHandleAudioFocus(initOptionsModel.handleAudioFocus);
        initOptions.setMulticastSettings(initOptionsModel.multicastSettings);
        initOptions.setMediaEntryCacheConfig(initOptionsModel.mediaEntryCacheConfig);

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


        if (context != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player = KalturaOttPlayer.create(context, initOptions);
                    player.setPlayerView(MATCH_PARENT, MATCH_PARENT);
                    getCurrentActivity().addContentView(player.getPlayerView(), new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
                    addKalturaPlayerListeners();
                    sendPlayerEvent("playerInitialized");
                }
            });

            initialized = true;
        } else {
            initialized = false;
        }
    }

    ////////////////////////////////////////////////////////////////////

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void load(String assetId, String jsonOptionsStr) {

        log.d("load assetId: " + assetId + ", jsonOptionsStr:" + jsonOptionsStr);

        Gson gson = new Gson();
        MediaAsset mediaAsset = gson.fromJson(jsonOptionsStr, MediaAsset.class);
        if (mediaAsset == null || player == null) {
            return;
        }

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

//            final PKMediaEntry localPlaybackEntry = PKDownloadWrapper.getLocalPlaybackEntry(assetId);
//            if (localPlaybackEntry != null) {
//                player.setMedia(localPlaybackEntry);
//            }

        getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                player.loadMedia(mediaAsset.buildOttMediaOptions(assetId, player.getKS()), (mediaOptions, entry, error) -> {
                    if (error != null) {
                        log.e("ott media load error: " + error.getName() + " " + error.getCode() + " " + error.getMessage());
                        sendPlayerEvent("loadMediaFailed", gson.toJson(error));
                    } else {
                        log.d("ott media load success name = " + entry.getName() + " initialVolume = " + mediaAsset.getInitialVolume());
                        sendPlayerEvent("loadMediaSuccess", gson.toJson(entry));

                        if (mediaAsset.getInitialVolume() >= 0 && mediaAsset.getInitialVolume() < 1.0) {
                            player.setVolume(mediaAsset.getInitialVolume());
                        }
                    }
                });
            }
        });
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void prepare() {

        log.d("prepare");
        reportedDuration = Consts.TIME_UNSET;
        if (player != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player.prepare();
                }
            });

        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void play() {

        log.d("play");
        if (player != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player.play();
                }
            });
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void pause() {

        log.d("pause");
        if (player != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player.pause();
                }
            });
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void replay() {

        log.d("replay");
        if (player != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player.replay();
                }
            });
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void seekTo(float position) {

        long posMS = (long)(position * Consts.MILLISECONDS_MULTIPLIER);
        log.d("seekTo:" + posMS);
        if (player != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player.seekTo(posMS);
                }
            });
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void changeTrack(String uniqueId) {

        log.d("changeTrack:" + uniqueId);
        if (player != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player.changeTrack(uniqueId);
                }
            });
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void changePlaybackRate(float playbackRate) {

        log.d("changePlaybackRate:" + playbackRate);
        if (player != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player.setPlaybackRate(playbackRate);
                }
            });
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void stop() {

        log.d("stop");
        if (player != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player.stop();
                }
            });
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void destroy() {

        log.d("destroy");
//        if (lifecycleListener != null) {
//            activity.removeLifecycleListener(lifecycleListener);
//            lifecycleListener = null;
//        }
//
//            if (player != null) {
//                player.removeListeners(self);
//                player.pause();
//                player.destroy();
//                player = null;
//            }
//
//
//        activity = null;
//        mainHandler = null;
//        bridgeInstance = null;
//        initialized = false;
//        reportedDuration = Consts.TIME_UNSET;
//        self = null;
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void setAutoplay(boolean autoplay) {

        log.d("setAutoplay: " + autoplay);
        if (player != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player.setAutoPlay(autoplay);
                }
            });
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void setKS(String ks) {

        log.d("setKS: " + ks);
        if (player != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player.setKS(ks);
                }
            });
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void setZIndex(float index) {

        log.d("setZIndex: " + index);
        if (player != null && player.getPlayerView() != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player.getPlayerView().setZ(index);
                }
            });
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void setFrame(int playerViewWidth, int playerViewHeight, int playerViewPosX, int playerViewPosY) {

        log.d("setFrame " + playerViewWidth + "/" + playerViewHeight + " " + playerViewPosX + "/" + playerViewPosY);

        if (player != null && player.getPlayerView() != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
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
            });
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void setVolume(float volume) {

        log.d("setVolume: " + volume);
        if (volume < 0) {
            volume = 0f;
        } else if (volume > 1) {
            volume = 1.0f;
        }

        if (player != null) {
            float finalPlayerVolume = volume;
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player.setVolume(finalPlayerVolume);
                }
            });
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public  void setPlayerVisibility(boolean isVisible, float volume) {

        log.d("setPlayerVisibility: " + isVisible + " volume = " + volume);
        if (player != null && player.getPlayerView() != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (isVisible) {
                        player.getPlayerView().setVisibility(View.VISIBLE);
                        player.setVolume(volume);
                    } else {

                        player.getPlayerView().setVisibility(View.INVISIBLE);
                        player.setVolume(volume);
                    }
                }
            });
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
    public void requestThumbnailInfo(float positionMs) {

        log.d("requestThumbnailInfo:" + positionMs);
        if (player != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ThumbnailInfo thumbnailInfo = player.getThumbnailInfo((long) positionMs);
                    if (thumbnailInfo != null && positionMs >= 0) {
                        String thumbnailInfoJson = "{ \"position\": " + positionMs + ", \"thumbnailInfo\": " + new Gson().toJson(thumbnailInfo) + " }";
                        //sendPlayerEvent("thumbnailInfoResponse", thumbnailInfoJson);
                    } else {
                        log.e("requestThumbnailInfo: thumbnailInfo is null or position is invalid");
                    }
                }
            });
        }
    }

    @ReactMethod(isBlockingSynchronousMethod = true)
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

    //////////////////////////////////////////////////////////////////////

    private void addKalturaPlayerListeners() {
        log.d("addKalturaPlayerListeners");

        if (player == null) {
            return;
        }

        player.addListener(context, PlayerEvent.canPlay, event -> sendPlayerEvent("canPlay"));
        player.addListener(context, PlayerEvent.playing, event -> sendPlayerEvent("playing"));
        player.addListener(context, PlayerEvent.play, event -> sendPlayerEvent("play"));
        player.addListener(context, PlayerEvent.pause, event -> sendPlayerEvent("pause"));
        player.addListener(context, PlayerEvent.ended, event -> sendPlayerEvent("ended"));
        player.addListener(context, PlayerEvent.stopped, event -> sendPlayerEvent("stopped"));
        player.addListener(context, PlayerEvent.durationChanged, event -> {
            reportedDuration = event.duration;
            sendPlayerEvent("durationChanged", "{ \"duration\": " + (event.duration / Consts.MILLISECONDS_MULTIPLIER_FLOAT) + " }");
        });
        player.addListener(context, PlayerEvent.playheadUpdated, new PKEvent.Listener<PlayerEvent.PlayheadUpdated>() {
            @Override
            public void onEvent(PlayerEvent.PlayheadUpdated event) {

                String timeUpdatePayload = "\"position\": " + (event.position / Consts.MILLISECONDS_MULTIPLIER_FLOAT) +
                        ", \"bufferPosition\": " + (event.bufferPosition / Consts.MILLISECONDS_MULTIPLIER_FLOAT);

                if (player != null && player.isLive() &&  player.getCurrentProgramTime() > 0) {
                    timeUpdatePayload = "{ " + timeUpdatePayload +
                            ", \"currentProgramTime\": " + player.getCurrentProgramTime() +
                            " }";
                } else {
                    timeUpdatePayload = "{ " + timeUpdatePayload  +
                            " }";
                }

                sendPlayerEvent("timeUpdate", timeUpdatePayload);
                if (reportedDuration != event.duration && event.duration > 0) {
                    reportedDuration = event.duration;
                    if (player != null && player.getMediaEntry() != null && player.getMediaEntry().getMediaType() != PKMediaEntry.MediaEntryType.Vod /*|| player.isLive()*/) {
                        sendPlayerEvent("loadedTimeRanges", "{\"timeRanges\": [ { \"start\": " + 0 +
                                ", \"end\": " + (event.duration / Consts.MILLISECONDS_MULTIPLIER_FLOAT) +
                                " } ] }");
                    }
                }
            }
        });
        player.addListener(context, PlayerEvent.stateChanged, event -> sendPlayerEvent("stateChanged", "{ \"newState\": \"" + event.newState.name() + "\" }"));
        player.addListener(context, PlayerEvent.tracksAvailable, event -> {
            Gson gson = new Gson();
            sendPlayerEvent("tracksAvailable", gson.toJson(getTracksInfo(event.tracksInfo)));
        });

        player.addListener(context, PlayerEvent.videoTrackChanged, event -> {
            final com.kaltura.playkit.player.VideoTrack newTrack = event.newTrack;
            VideoTrack videoTrack = new VideoTrack(newTrack.getUniqueId(), newTrack.getWidth(), newTrack.getHeight(), newTrack.getBitrate(), true, newTrack.isAdaptive());
            Gson gson = new Gson();
            sendPlayerEvent("videoTrackChanged", gson.toJson(videoTrack));
        });

        player.addListener(context, PlayerEvent.audioTrackChanged, event -> {
            final com.kaltura.playkit.player.AudioTrack newTrack = event.newTrack;
            AudioTrack audioTrack = new AudioTrack(newTrack.getUniqueId(), newTrack.getBitrate(), newTrack.getLanguage(), newTrack.getLabel(), newTrack.getChannelCount(), true);
            Gson gson = new Gson();
            sendPlayerEvent("audioTrackChanged", gson.toJson(audioTrack));
        });

        player.addListener(context, PlayerEvent.textTrackChanged, event -> {
            final com.kaltura.playkit.player.TextTrack newTrack = event.newTrack;
            TextTrack textTrack = new TextTrack(newTrack.getUniqueId(), newTrack.getLanguage(), newTrack.getLabel(), true);
            Gson gson = new Gson();
            sendPlayerEvent("textTrackChanged", gson.toJson(textTrack));
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

            sendPlayerEvent("imageTrackChanged", new Gson().toJson(imageTrack));
        });

        player.addListener(context, PlayerEvent.playbackInfoUpdated, event -> {
            long videoBitrate = (event.playbackInfo.getVideoBitrate() > 0) ? event.playbackInfo.getVideoBitrate() : 0;
            long audioBitrate = (event.playbackInfo.getAudioBitrate() > 0) ? event.playbackInfo.getAudioBitrate() : 0;
            long totalBitrate = (videoBitrate + audioBitrate);
            sendPlayerEvent("playbackInfoUpdated", "{ \"videoBitrate\": " + event.playbackInfo.getVideoBitrate() +
                    ", \"audioBitrate\": " + event.playbackInfo.getAudioBitrate() +
                    ", \"totalBitrate\": " + totalBitrate  +
                    " }");
        });

        player.addListener(context, PlayerEvent.seeking, event -> sendPlayerEvent("seeking", "{ \"targetPosition\": " + (event.targetPosition / Consts.MILLISECONDS_MULTIPLIER_FLOAT) + " }"));
        player.addListener(context, PlayerEvent.seeked, event -> sendPlayerEvent("seeked", Arguments.createMap()));
        player.addListener(context, PlayerEvent.error, event -> {
            if (event.error.isFatal()) {
                log.e("error event : " + getErrorJson(event.error));
                sendPlayerEvent("error", getErrorJson(event.error));
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

        player.addListener(context, AdEvent.adProgress, event -> sendPlayerEvent("adProgress", "{ \"currentAdPosition\": " + (event.currentAdPosition / Consts.MILLISECONDS_MULTIPLIER_FLOAT) + " }"));
        player.addListener(context, AdEvent.cuepointsChanged, event -> sendPlayerEvent("adCuepointsChanged", getCuePointsJson(event.cuePoints)));
        player.addListener(context, AdEvent.started, event -> sendPlayerEvent("adStarted"));
        player.addListener(context, AdEvent.completed, event -> sendPlayerEvent("adCompleted"));
        player.addListener(context, AdEvent.paused, event -> sendPlayerEvent("adPaused"));
        player.addListener(context, AdEvent.resumed, event -> sendPlayerEvent("adResumed"));
        player.addListener(context, AdEvent.adBufferStart, event -> sendPlayerEvent("adBufferStart", ""));
        player.addListener(context, AdEvent.adClickedEvent, event -> sendPlayerEvent("adClicked", "{ \"clickThruUrl\": \"" + event.clickThruUrl + "\" }"));
        player.addListener(context, AdEvent.skipped, event -> sendPlayerEvent("adSkipped"));
        player.addListener(context, AdEvent.adRequested, event -> sendPlayerEvent("adRequested", "{ \"adTagUrl\": \"" +  event.adTagUrl + "\" }"));
        player.addListener(context, AdEvent.contentPauseRequested, event -> sendPlayerEvent("adContentPauseRequested"));
        player.addListener(context, AdEvent.contentResumeRequested, event -> sendPlayerEvent("adContentResumeRequested"));
        player.addListener(context, AdEvent.allAdsCompleted, event -> sendPlayerEvent("allAdsCompleted"));
        player.addListener(context, AdEvent.error, event -> {
            if (event.error.isFatal()) {
                sendPlayerEvent("adError", getErrorJson(event.error));
            }
        });
    }

    private TracksInfo getTracksInfo(PKTracks pkTracksInfo) {
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

        return new Gson().toJson(errorJson);
    }

    private String getCuePointsJson(AdCuePoints adCuePoints) {

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

    private void updateIMAPlugin(WrapperIMAConfig wrapperIMAConfig) {
        if (player != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player.updatePluginConfig(IMAPlugin.factory.getName(), getIMAConfig(wrapperIMAConfig));                }
            });

        }
    }

    private void updateYouboraPlugin(YouboraConfig youboraConfig) {
        if (player != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player.updatePluginConfig(YouboraPlugin.factory.getName(), youboraConfig);
                }
            });
        }
    }

    private void updatePhoenixAnalyticsPlugin(WrapperPhoenixAnalyticsConfig wrapperPhoenixAnalyticsConfig) {
        if (player != null && wrapperPhoenixAnalyticsConfig != null) {
            getCurrentActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    player.updatePluginConfig(PhoenixAnalyticsPlugin.factory.getName(), wrapperPhoenixAnalyticsConfig.toJson());                }
            });
        }
    }

    private void createIMAPlugin(PKPluginConfigs pluginConfigs, JsonObject imaConfigJson) {

        PlayKitManager.registerPlugins(context, IMAPlugin.factory);
        if (pluginConfigs != null) {
            if (imaConfigJson == null) {
                pluginConfigs.setPluginConfig(IMAPlugin.factory.getName(), new IMAConfig());
            } else {
                pluginConfigs.setPluginConfig(IMAPlugin.factory.getName(), imaConfigJson);
            }
        }
    }

    private IMAConfig getIMAConfig(WrapperIMAConfig wrapperIMAConfig) {
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

    private void createYouboraPlugin(PKPluginConfigs pluginConfigs, YouboraConfig youboraConfig) {

        PlayKitManager.registerPlugins(context, YouboraPlugin.factory);
        if (pluginConfigs != null) {
            pluginConfigs.setPluginConfig(YouboraPlugin.factory.getName(), youboraConfig);
        }
    }

    private void createBroadpeakPlugin(PKPluginConfigs pluginConfigs, BroadpeakConfig broadpeakConfig) {
        PlayKitManager.registerPlugins(context, BroadpeakPlugin.factory);
        if (pluginConfigs != null) {
            pluginConfigs.setPluginConfig(BroadpeakPlugin.factory.getName(), broadpeakConfig);
        }
    }

    private void createPhoenixAnalyticsPlugin(PKPluginConfigs pluginConfigs, JsonObject phoenixAnalyticsConfigJson) {
        PlayKitManager.registerPlugins(context, PhoenixAnalyticsPlugin.factory);
        if (pluginConfigs != null) {
            if (phoenixAnalyticsConfigJson == null) {
                pluginConfigs.setPluginConfig(PhoenixAnalyticsPlugin.factory.getName(), new PhoenixAnalyticsConfig(-1, "", "", Consts.DEFAULT_ANALYTICS_TIMER_INTERVAL_HIGH_SEC));
            } else {
                pluginConfigs.setPluginConfig(PhoenixAnalyticsPlugin.factory.getName(), phoenixAnalyticsConfigJson);
            }
        }
    }

    ////////////////////////////////////////////////////////////////////////

    private DeviceEventManagerModule.RCTDeviceEventEmitter emitter() {
        return context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
    }

    private void sendPlayerEvent(String event) {
        WritableMap params = Arguments.createMap();
        emitter().emit(event, params);
    }

    private void sendPlayerEvent(String event, String payload) {
        WritableMap params = Arguments.createMap();
        if (!TextUtils.isEmpty(payload)) {
            params.putString("payload", payload);
        }
        emitter().emit(event, params);
    }

    private void sendPlayerEvent(String event, @NonNull WritableMap params) {
        emitter().emit(event, params);
    }
}