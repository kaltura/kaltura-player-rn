package com.kaltura.react_native_kplayer.model.tracks;

public class TextTrack {
    public String type;
    public String id;
    public String language;
    public String label;
    public boolean isSelected;

    public TextTrack(String id, String language, String label, boolean isSelected) {
        this.id = id;
        this.language = language;
        this.label = label;
        this.isSelected = isSelected;
    }
}
