package com.kalturaplayerrn;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
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

        Button button = new Button(context);
        button.setText("MyButton");
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("KalturaPlayerModule", "Button Clicked");
            }
        });

        getCurrentActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //ViewGroup.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
                //getCurrentActivity().addContentView(button, layoutParams);
                loadBasicPlayer(url);            }
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
        //Set media entry type. It could be Live,Vod or Unknown.
        //In this sample we use Vod.
        mediaEntry.setMediaType(PKMediaEntry.MediaEntryType.Vod);

        //Create list that contains at least 1 media source.
        //Each media entry can contain a couple of different media sources.
        //All of them represent the same content, the difference is in it format.
        //For example same entry can contain PKMediaSource with dash and another
        // PKMediaSource can be with hls. The player will decide by itself which source is
        // preferred for playback.
        List<PKMediaSource> mediaSources = createMediaSources(url);

        //Set media sources to the entry.
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
}