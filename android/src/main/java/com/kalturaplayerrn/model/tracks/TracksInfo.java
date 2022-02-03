package com.kaltura.react_native_kplayer.model.tracks;

import java.util.ArrayList;
import java.util.List;

public class TracksInfo {
    public String type;
    List<VideoTrack> video = new ArrayList<>();
    List<TextTrack> text = new ArrayList<>();
    List<AudioTrack> audio = new ArrayList<>();
    List<ImageTrack> image = new ArrayList<>();


    public TracksInfo setVideoTracks(List<VideoTrack> video) {
        this.video = video;
        return this;
    }

    public TracksInfo setTextTracks(List<TextTrack> text) {
        this.text = text;
        return this;
    }

    public TracksInfo setAudioTracks(List<AudioTrack> audio) {
        this.audio = audio;
        return this;
    }

    public TracksInfo setImageTracks(List<ImageTrack> image) {
        this.image = image;
        return this;
    }
}
