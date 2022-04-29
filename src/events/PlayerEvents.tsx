export default {
    // TODO: All the "" string needs to be added on Native android layer
    // TODO: Generate it using some script

    STATE_CHANGED : "stateChanged",
    CAN_PLAY : "canPlay",   // Sent when enough data is available that the media can be played, at least for a couple of frames. This corresponds to the HAVE_ENOUGH_DATA readyState.
    DURATION_CHANGE : "durationChanged",   //  The metadata has loaded or changed, indicating a change in duration of the media. This is sent, for example, when the media has loaded enough that the duration is known.
    ENDED : "ended",   //  Sent when playback completes.
    ERROR : "error",   //  Sent when an error occurs. The element's error attribute contains more information. See Error handling for details.
    LOADED_METADATA : "loadedMetadata",   //  The media's metadata has finished loading; all attributes now contain as much useful information as they're going to.
    PAUSE : "pause",   //  Sent when playback is paused.
    PLAY : "play",    //  Sent when playback of the media starts after having been paused; that is, when playback is resumed after a prior pause event.
    PLAYING : "playing",   //  Sent when the media begins to play (either for the first time, after having been paused, or after ending and then restarting).
    SEEKED : "seeked",   //  Sent when a seek operation completes.
    SEEKING : "seeking",   //  Sent when a seek operation begins.
    TRACKS_AVAILABLE : "tracksAvailable", // Sent when track info is available.
    REPLAY : "replay", //Sent when replay happened.
    PLAYBACK_INFO_UPDATED : "playbackInfoUpdated", // Sent event that notify about changes in the playback parameters. When bitrate of the video or audio track changes or new media loaded. Holds the PlaybackInfo.java object with relevant data.
    VOLUME_CHANGED : "", // Sent when volume is changed.
    STOPPED : "stopped", // sent when stop player api is called
    METADATA_AVAILABLE : "", // Sent when there is metadata available for this entry.
    SOURCE_SELECTED : "", // Sent when the source was selected.
    PLAYHEAD_UPDATED : "", //Send player position every 100 Milisec
    VIDEO_TRACK_CHANGED : "videoTrackChanged",
    AUDIO_TRACK_CHANGED : "audioTrackChanged",
    TEXT_TRACK_CHANGED : "textTrackChanged",
    IMAGE_TRACK_CHANGED : "imageTrackChanged",
    PLAYBACK_RATE_CHANGED : "",
    CONNECTION_ACQUIRED : "",
    VIDEO_FRAMES_DROPPED : "",   // Video frames were dropped, see PlayerEvent.VideoFramesDropped
    OUTPUT_BUFFER_COUNT_UPDATE : "",
    BYTES_LOADED : "",  // Bytes were downloaded from the network
    SUBTITLE_STYLE_CHANGED : "subtitlesStyleChanged",  // Subtitle style is changed.
    ASPECT_RATIO_RESIZE_MODE_CHANGED : "surfaceAspectRationSizeModeChanged", //Send when updating the Surface Vide Aspect Ratio size mode.
    TIME_UPDATE : "timeUpdate",
    LOAD_TIME_RANGES : "loadedTimeRanges"
}