package com.kalturaplayerrn.model;

import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKRequestConfig;
import com.kaltura.playkit.player.ABRSettings;
import com.kaltura.playkit.player.MulticastSettings;
import com.kaltura.playkit.player.PKAspectRatioResizeMode;
import com.kaltura.playkit.player.PKLowLatencyConfig;
import com.kaltura.playkit.player.PKMaxVideoSize;
import com.kaltura.tvplayer.config.MediaEntryCacheConfig;

import java.util.List;

public class InitOptions {
    public String  serverUrl;
    public boolean autoplay = false;
    public boolean preload = true;
    public PKRequestConfig requestConfig;
    public boolean allowCrossProtocolRedirect = true;
    public RegisteredPlugins plugins;
    public List<String> warmupUrls;
    public String ks;
    public String referrer;
    public ABRSettings abrSettings;
    public NetworkSettings networkSettings;
    public TrackSelection trackSelection;
    public PKMediaFormat preferredMediaFormat;
    public PKLowLatencyConfig lowLatencyConfig;
    public Boolean allowClearLead;
    public Boolean enableDecoderFallback;
    public Boolean secureSurface;
    public Boolean adAutoPlayOnResume;
    public Boolean isVideoViewHidden;
    public Boolean forceSinglePlayerEngine;
    public PKAspectRatioResizeMode aspectRatioResizeMode;
    public Boolean isTunneledAudioPlayback;
    public Boolean handleAudioBecomingNoisyEnabled;
    public Boolean handleAudioFocus;
    public PKMaxVideoSize maxVideoSize;
    public Integer maxVideoBitrate;
    public Integer maxAudioBitrate;
    public Integer maxAudioChannelCount;
    public MulticastSettings multicastSettings;
    public MediaEntryCacheConfig mediaEntryCacheConfig;
}
