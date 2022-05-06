package com.reactnativekalturaplayer.model;

import com.kaltura.playkit.PKDrmParams;
import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;
import com.kaltura.playkit.player.PKExternalSubtitle;

import java.util.List;
import java.util.Map;

public class BasicMediaAsset {
   String id; // Id is only for PKMediaEntry only
   String name;
   long duration;
   PKMediaEntry.MediaEntryType mediaEntryType;
   PKMediaFormat mediaFormat;
   boolean isVRMediaType;
   List<PKDrmParams> drmData; // For PKMediaSource
   Map<String, String> metadata;
   List<PKExternalSubtitle> externalSubtitleList;
   String externalVttThumbnailUrl;

   public String getId() {
      return id;
   }

   public String getName() {
      return name;
   }

   public long getDuration() {
      return duration;
   }

   public PKMediaEntry.MediaEntryType getMediaEntryType() {
      return mediaEntryType;
   }

   public PKMediaFormat getMediaFormat() {
      return mediaFormat;
   }

   public boolean isVRMediaType() {
      return isVRMediaType;
   }

   public List<PKDrmParams> getDrmData() {
      return drmData;
   }

   public Map<String, String> getMetadata() {
      return metadata;
   }

   public List<PKExternalSubtitle> getExternalSubtitleList() {
      return externalSubtitleList;
   }

   public String getExternalVttThumbnailUrl() {
      return externalVttThumbnailUrl;
   }
}
