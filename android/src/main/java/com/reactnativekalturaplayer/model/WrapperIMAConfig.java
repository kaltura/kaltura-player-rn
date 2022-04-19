package com.reactnativekalturaplayer.model;

import java.util.ArrayList;
import java.util.List;

public class WrapperIMAConfig {
    private String adTagUrl;
    private String adTagResponse;
    private boolean alwaysStartWithPreroll;
    private boolean enableDebugMode;
    private int videoBitrate = 600; // in KB
    private int adLoadTimeOut = 8; // in sec
    private List<String> videoMimeTypes = new ArrayList<>();

    public WrapperIMAConfig () {
        videoMimeTypes.add("video/mp4");
        videoMimeTypes.add("application/x-mpegURL");
    }
    public String getAdTagUrl() {
        return adTagUrl;
    }

    public String getAdTagResponse() {
        return adTagResponse;
    }

    public boolean isAlwaysStartWithPreroll() {
        return alwaysStartWithPreroll;
    }

    public boolean isEnableDebugMode() {
        return enableDebugMode;
    }

    public int getVideoBitrate() {
        return videoBitrate;
    }

    public int getAdLoadTimeOut() {
        return adLoadTimeOut;
    }

    public List<String> getVideoMimeTypes() {
        return videoMimeTypes;
    }
}
