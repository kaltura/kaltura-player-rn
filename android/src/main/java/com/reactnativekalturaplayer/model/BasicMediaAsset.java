package com.reactnativekalturaplayer.model;

import com.kaltura.playkit.PKMediaEntry;
import com.kaltura.playkit.PKMediaFormat;

public class BasicMediaAsset {
   String id; // Id is only for PKMediaEntry only
   String name;
   long duration;
   PKMediaEntry.MediaEntryType mediaEntryType;
   PKMediaFormat mediaFormat;

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
}
