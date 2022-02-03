package com.kaltura.react_native_kplayer.model.tracks;

public class VideoTrack {
    public String type;
    public String id;
    public int width;
    public int height;
    public long bitrate;
    public boolean isSelected;
    public boolean isAdaptive;

    public VideoTrack(String id, int width, int height, long bitrate, boolean isSelected, boolean isAdaptive) {
        this.id = id;
        this.width = width;
        this.height = height;
        this.bitrate = bitrate;
        this.isSelected = isSelected;
        this.isAdaptive = isAdaptive;
    }
}
