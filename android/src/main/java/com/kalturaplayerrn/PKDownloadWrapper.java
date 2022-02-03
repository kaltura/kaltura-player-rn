package com.kaltura.react_native_kplayer;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.google.gson.Gson;
import com.kaltura.playkit.PKLog;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.tvplayer.KalturaOttPlayer;
import com.kaltura.tvplayer.KalturaPlayer;
import com.kaltura.tvplayer.OfflineManager;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;

import com.kaltura.react_native_kplayer.model.MediaAsset;

public class PKDownloadWrapper {

    private static final PKLog log = PKLog.get("PKDownloadWrapper");
    public static ReactApplicationContext reactContext;

    private static OfflineManager manager;
    static ByteBuffer bridgeInstance;

    static void sendEvent(String name, String payload) {
        if (bridgeInstance == null) {
            log.e("bridgeInstance is null");
            return;
        }
//        nativeSendEvent(bridgeInstance, name, (payload != null ? payload : ""));
    }

//    private static native void nativeSendEvent(ByteBuffer instance, String name, String payload);



    // Called by PKDownloadWrapper
    static void setup(ByteBuffer instance, int partnerId, String serverUrl) throws IOException {
        log.d("Offline Manager setup partnerId: " + partnerId);

        bridgeInstance = instance;
        Context context = reactContext.getCurrentActivity();

        KalturaOttPlayer.initialize(context, partnerId, serverUrl);

        manager = OfflineManager.getInstance(reactContext.getCurrentActivity());
        manager.setKalturaParams(KalturaPlayer.Type.ott, partnerId);
        manager.start(() -> {
            log.d("Offline Manager started");
            sendEvent("offlineManagerStarted", null);
        });

        manager.setAssetStateListener(new OfflineManager.AssetStateListener() {
            @Override
            public void onAssetDownloadFailed(@NonNull String assetId, @NonNull Exception error) {
                log.e("onAssetDownloadFailed");
                String errorMessage = "";
                if (error != null && error.getMessage() != null) {
                    errorMessage = error.getMessage();
                }
                sendEvent("onAssetDownloadFailed", "{ " +
                                                                     "\"id\": \"" + assetId + "\", " +
                                                                     "\"errorMessage\": \"" + errorMessage + "\"" +
                                                                  " }");
            }

            @Override
            public void onAssetDownloadComplete(@NonNull String assetId) {
                log.d("onAssetDownloadComplete");
                sendEvent("onAssetDownloadComplete", getAssetIdJsonStr(assetId));
            }

            @Override
            public void onAssetDownloadPending(@NonNull String assetId) {
                log.d("onAssetDownloadPending");
                sendEvent("onAssetDownloadPending", getAssetIdJsonStr(assetId));
            }

            @Override
            public void onAssetDownloadPaused(@NonNull String assetId) {
                log.d("onAssetDownloadPaused");
                sendEvent("onAssetDownloadPaused", getAssetIdJsonStr(assetId));
            }

            @Override
            public void onRegistered(@NonNull String assetId, @NonNull OfflineManager.DrmStatus drmStatus) {
                long secondsLeft = -1;
                if (drmStatus != null) {
                    secondsLeft = drmStatus.currentRemainingTime;
                }
                log.d("onRegistered  secondsLeft = " + secondsLeft);
                sendEvent("onAssetRegistered", "{ " +
                                                                 "\"id\": \"" + assetId + "\", " +
                                                                 "\"currentRemainingTime\": " + secondsLeft +
                                                              " }");
            }

            @Override
            public void onRegisterError(@NonNull String assetId, @NonNull Exception error) {
                log.e("onRegisterError");
                String errorMessage = "";
                if (error != null && error.getMessage() != null) {
                    errorMessage = error.getMessage();
                }
                sendEvent("onAssetRegisterError", "{ " +
                                                                    "\"id\": \"" + assetId + "\", " +
                                                                    "\"errorMessage\": \"" + errorMessage + "\"" +
                                                                 " }");
            }

            @Override
            public void onStateChanged(@NonNull String assetId, @NonNull OfflineManager.AssetInfo onStateChanged) {
                log.d("onStateChanged " +  " :assetInfo " + onStateChanged.getState() + ":" + onStateChanged.getBytesDownloaded() + ":" + onStateChanged.getEstimatedSize());
                sendEvent("onAssetStateChanged", "{ " +
                                                                  "\"id\": \"" + assetId + "\", " +
                                                                  "\"downloadedState\": \"" + onStateChanged.getState().name() + "\", " +
                                                                  "\"bytesDownloaded\": " + (onStateChanged.getBytesDownloaded() / 1000) + ", " +
                                                                  "\"totalBytesEstimated\": " + (onStateChanged.getEstimatedSize() / 1000) +
                                                                " }");
            }

            @Override
            public void onAssetRemoved(@NonNull String assetId) {
                log.d("onAssetRemoved");
                sendEvent("onAssetRemoved", getAssetIdJsonStr(assetId));
            }
        });

        manager.setDownloadProgressListener((assetId, bytesDownloaded, totalBytesEstimated, percentDownloaded) -> {
            String progressPayload = assetId + ":" + (bytesDownloaded / 1000) + "/" + (totalBytesEstimated / 1000);
            log.d("[progress] " + progressPayload);
            sendEvent("onDownloadProgress", "{ " +
                                                              "\"id\": \"" + assetId + "\", " +
                                                              "\"bytesDownloaded\": " + (bytesDownloaded / 1000) + ", " +
                                                              "\"totalBytesEstimated\": " + (totalBytesEstimated / 1000) +
                                                          " }");
        });
    }

    private static String getAssetIdJsonStr(@NonNull String assetId) {
        return "{ \"id\": \"" + assetId + "\" }";
    }

    static PKMediaEntry getLocalPlaybackEntry(String assetId) {
        if (manager == null) {
            return null;
        }
        final OfflineManager.AssetInfo assetInfo = manager.getAssetInfo(assetId);
        if (assetInfo == null || assetInfo.getState() != OfflineManager.AssetDownloadState.completed) {
            return null;
        }

        try {
            return manager.getLocalPlaybackEntry(assetId);
        } catch (IOException e) {
            log.e("Error in getLocalPlaybackEntry", e);
            return null;
        }
    }

    public static void start(String assetId, String jsonDataStr) {
        log.d("Offline Manager start download assetId: " + assetId);

        final OfflineManager.SelectionPrefs prefs = new OfflineManager.SelectionPrefs();
        prefs.videoBitrate = 500_000;

        Gson gson = new Gson();
        MediaAsset mediaAsset = gson.fromJson(jsonDataStr, MediaAsset.class);
        if (mediaAsset == null) {
            return;
        }

        manager.prepareAsset(mediaAsset.buildOttMediaOptions(assetId, null), prefs, new OfflineManager.PrepareCallback() {
            @Override
            public void onPrepared(@NonNull String assetId, @NonNull OfflineManager.AssetInfo assetInfo, @Nullable Map<OfflineManager.TrackType, List<OfflineManager.Track>> selected) {
                manager.startAssetDownload(assetInfo);
            }

            @Override
            public void onPrepareError(@NonNull String assetId, @NonNull Exception error) {
                log.e("onPrepareError");
            }
        });
    }

    public static void pause(String assetId) {
        manager.pauseAssetDownload(assetId);
    }

    public static void resume(String assetId) {
        manager.resumeAssetDownload(assetId);
    }

    public static void remove(String assetId) {
        manager.removeAsset(assetId);
    }
}
