package com.reactnativekalturaplayer.events

class KalturaPlayerEvents {

    companion object {
        @JvmField
        val LOAD_MEDIA_FAILED = "loadMediaFailed"
        @JvmField
        val LOAD_MEDIA_SUCCESS = "loadMediaSuccess"
        @JvmField
        val STATE_CHANGED = "stateChanged"
        @JvmField
        val CAN_PLAY = "canPlay" // Sent when enough data is available that the media can be played; at least for a couple of frames. This corresponds to the HAVE_ENOUGH_DATA readyState.
        @JvmField
        val DURATION_CHANGE = "durationChanged" //  The metadata has loaded or changed; indicating a change in duration of the media. This is sent; for example; when the media has loaded enough that the duration is known.
        @JvmField
        val ENDED = "ended" //  Sent when playback completes.
        @JvmField
        val ERROR = "error" //  Sent when an error occurs. The element"s error attribute contains more information. See Error handling for details.
        @JvmField
        val LOADED_METADATA = "loadedMetadata" //  The media"s metadata has finished loading; all attributes now contain as much useful information as they"re going to.
        @JvmField
        val PAUSE = "pause" //  Sent when playback is paused.
        @JvmField
        val PLAY = "play" //  Sent when playback of the media starts after having been paused; that is; when playback is resumed after a prior pause event.
        @JvmField
        val PLAYING = "playing" //  Sent when the media begins to play (either for the first time; after having been paused; or after ending and then restarting).
        @JvmField
        val SEEKED = "seeked" //  Sent when a seek operation completes.
        @JvmField
        val SEEKING = "seeking" //  Sent when a seek operation begins.
        @JvmField
        val TRACKS_AVAILABLE = "tracksAvailable" // Sent when track info is available.
        @JvmField
        val REPLAY = "replay" //Sent when replay happened.
        @JvmField
        val PLAYBACK_INFO_UPDATED = "playbackInfoUpdated" // Sent event that notify about changes in the playback parameters. When bitrate of the video or audio track changes or new media loaded. Holds the PlaybackInfo.java object with relevant data.
        @JvmField
        val VOLUME_CHANGED = "volumeChanged" // Sent when volume is changed.
        @JvmField
        val STOPPED = "stopped" // sent when stop player api is called
        @JvmField
        val METADATA_AVAILABLE = "metadataAvailable" // Sent when there is metadata available for this entry.
        @JvmField
        val EVENT_STREAM_CHANGED = "eventStreamChanged" //Send event streams received from manifest
        @JvmField
        val SOURCE_SELECTED = "sourceSelected" // Sent when the source was selected.
        @JvmField
        val PLAYHEAD_UPDATED = "playheadUpdated" //Send player position every 100 Milisec
        @JvmField
        val VIDEO_TRACK_CHANGED = "videoTrackChanged"
        @JvmField
        val AUDIO_TRACK_CHANGED = "audioTrackChanged"
        @JvmField
        val TEXT_TRACK_CHANGED = "textTrackChanged"
        @JvmField
        val IMAGE_TRACK_CHANGED = "imageTrackChanged"
        @JvmField
        val PLAYBACK_RATE_CHANGED = "playbackRateChanged"
        @JvmField
        val CONNECTION_ACQUIRED = "connectionAcquired"
        @JvmField
        val VIDEO_FRAMES_DROPPED = "videoFramesDropped" // Video frames were dropped; see PlayerEvent.VideoFramesDropped
        @JvmField
        val OUTPUT_BUFFER_COUNT_UPDATE = "outputBufferCountUpdate"
        @JvmField
        val BYTES_LOADED = "bytesLoaded" // Bytes were downloaded from the network
        @JvmField
        val SUBTITLE_STYLE_CHANGED = "subtitlesStyleChanged" // Subtitle style is changed.
        @JvmField
        val ASPECT_RATIO_RESIZE_MODE_CHANGED = "surfaceAspectRationSizeModeChanged" //Send when updating the Surface Vide Aspect Ratio size mode.
        @JvmField
        val LOAD_TIME_RANGES = "loadedTimeRanges"
        @JvmField
        val DRM_INITIALIZED = "drmInitialized"
        @JvmField
        val THUMBNAIL_INFO_RESPONSE = "thumbnailInfoResponse"
    }
}
