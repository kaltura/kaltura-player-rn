package com.kaltura.react_native_kplayer.model.tracks;

public class ImageTrack {
    public String type;
    public String id;
    public String label;
    public long bitrate;
    public float width;
    public float height;
    public int cols;
    public int rows;
    public long duration;
    public String url;
    public boolean isSelected;


    public ImageTrack(String id,
               String label,
               long bitrate,
               float width,
               float height,
               int cols,
               int rows,
               long duration,
               String url,
               boolean isSelected

    ) {
        this.id = id;
        this.label = label;
        this.bitrate = bitrate;
        this.width = width;
        this.height = height;
        this.cols = cols;
        this.rows = rows;
        this.duration = duration;
        this.url = url;
        this.isSelected = isSelected;
    }
}
