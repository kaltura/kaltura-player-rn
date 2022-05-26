package com.reactnativekalturaplayer.model;

import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.PKRequestConfig;
import com.kaltura.playkit.PKSubtitlePreference;
import com.kaltura.playkit.PKWakeMode;
import com.kaltura.playkit.player.ABRSettings;
import com.kaltura.playkit.player.AudioCodecSettings;
import com.kaltura.playkit.player.LoadControlBuffers;
import com.kaltura.playkit.player.MulticastSettings;
import com.kaltura.playkit.player.PKAspectRatioResizeMode;
import com.kaltura.playkit.player.PKLowLatencyConfig;
import com.kaltura.playkit.player.PKMaxVideoSize;
import com.kaltura.playkit.player.SubtitleStyleSettings;
import com.kaltura.playkit.player.VideoCodecSettings;
import com.kaltura.playkit.player.vr.VRSettings;
import com.kaltura.tvplayer.config.MediaEntryCacheConfig;

import java.util.List;

public class InitOptions {
    public String  serverUrl;
    public boolean autoplay = false;
    public boolean preload = true;
    public RegisteredPlugins plugins;
    public PKRequestConfig requestConfig;
    public boolean allowCrossProtocolRedirect = true;
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
    public SubtitleStyling subtitleStyling;
    public PKWakeMode wakeMode;
    public PKSubtitlePreference subtitlePreference;

    public LoadControlBuffers loadControlBuffers;
    public VRSettings vrSettings;
    public VideoCodecSettings videoCodecSettings;
    public AudioCodecSettings audioCodecSettings;

}
