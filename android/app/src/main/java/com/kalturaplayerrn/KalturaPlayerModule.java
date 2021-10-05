package com.kalturaplayerrn;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.google.gson.Gson;
import com.kaltura.playkit.PKDrmParams;
import com.kaltura.playkit.PKMediaConfig;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKMediaSource;
import com.kaltura.playkit.PlayerEvent;
import com.kaltura.tvplayer.KalturaBasicPlayer;
import com.kaltura.tvplayer.KalturaPlayer;
import com.kaltura.tvplayer.PlayerInitOptions;

import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.bridge.Arguments;


public class KalturaPlayerModule extends ReactContextBaseJavaModule {
    ReactApplicationContext context;

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
            Log.d("KalturaPlayerModule", "Event tracksAvailable tracksInfo.getVideoTracks().size() " + (event.tracksInfo.getVideoTracks().size()));
            WritableMap params = Arguments.createMap();
            params.putString("eventProperty1", "someValue1");
            params.putString("eventProperty2", "someValue2");
            sendEvent("EventReminder", params);
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



    private DeviceEventManagerModule.RCTDeviceEventEmitter emitter() {
        return context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class);
    }

    private void sendEvent(String event, @Nullable WritableMap payload) {
        emitter().emit(event, payload);
    }
}