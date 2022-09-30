package com.reactnativekalturaplayer.model.tracks;

public class AudioTrack {
    public String id;
    public long bitrate;
    public String language;
    public String label;
    public int channelCount;
    public boolean isSelected;

    public AudioTrack(String id, long bitrate, String language, String label, int channelCount, boolean isSelected) {
        this.id = id;
        this.bitrate = bitrate;
        this.label = label;
        this.language = language;
        this.channelCount = channelCount;
        this.isSelected = isSelected;
    }
}
