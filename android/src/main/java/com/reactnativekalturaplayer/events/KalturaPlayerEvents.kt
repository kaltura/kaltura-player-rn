package com.reactnativekalturaplayer.events

class KalturaPlayerEvents {

    companion object {
        const val STATE_CHANGED = "stateChanged"
        const val CAN_PLAY = "canPlay" // Sent when enough data is available that the media can be played; at least for a couple of frames. This corresponds to the HAVE_ENOUGH_DATA readyState.
        const val DURATION_CHANGE = "durationChanged" //  The metadata has loaded or changed; indicating a change in duration of the media. This is sent; for example; when the media has loaded enough that the duration is known.
        const val ENDED = "ended" //  Sent when playback completes.
        const val ERROR = "error" //  Sent when an error occurs. The element"s error attribute contains more information. See Error handling for details.
        const val LOADED_METADATA = "loadedMetadata" //  The media"s metadata has finished loading; all attributes now contain as much useful information as they"re going to.
        const val PAUSE = "pause" //  Sent when playback is paused.
        const val PLAY = "play" //  Sent when playback of the media starts after having been paused; that is; when playback is resumed after a prior pause event.
        const val PLAYING = "playing" //  Sent when the media begins to play (either for the first time; after having been paused; or after ending and then restarting).
        const val SEEKED = "seeked" //  Sent when a seek operation completes.
        const val SEEKING = "seeking" //  Sent when a seek operation begins.
        const val TRACKS_AVAILABLE = "tracksAvailable" // Sent when track info is available.
        const val REPLAY = "replay" //Sent when replay happened.
        const val PLAYBACK_INFO_UPDATED = "playbackInfoUpdated" // Sent event that notify about changes in the playback parameters. When bitrate of the video or audio track changes or new media loaded. Holds the PlaybackInfo.java object with relevant data.
        const val VOLUME_CHANGED = "volumeChanged" // Sent when volume is changed.
        const val STOPPED = "stopped" // sent when stop player api is called
        const val METADATA_AVAILABLE = "metadataAvailable" // Sent when there is metadata available for this entry.
        const val EVENT_STREAM_CHANGED = "eventStreamChanged" //Send event streams received from manifest
        const val SOURCE_SELECTED = "sourceSelected" // Sent when the source was selected.
        const val PLAYHEAD_UPDATED = "playheadUpdated" //Send player position every 100 Milisec
        const val VIDEO_TRACK_CHANGED = "videoTrackChanged"
        const val AUDIO_TRACK_CHANGED = "audioTrackChanged"
        const val TEXT_TRACK_CHANGED = "textTrackChanged"
        const val IMAGE_TRACK_CHANGED = "imageTrackChanged"
        const val PLAYBACK_RATE_CHANGED = "playbackRateChanged"
        const val CONNECTION_ACQUIRED = "connectionAcquired"
        const val VIDEO_FRAMES_DROPPED = "videoFramesDropped" // Video frames were dropped; see PlayerEvent.VideoFramesDropped
        const val OUTPUT_BUFFER_COUNT_UPDATE = "outputBufferCountUpdate"
        const val BYTES_LOADED = "bytesLoaded" // Bytes were downloaded from the network
        const val SUBTITLE_STYLE_CHANGED = "subtitlesStyleChanged" // Subtitle style is changed.
        const val ASPECT_RATIO_RESIZE_MODE_CHANGED = "surfaceAspectRationSizeModeChanged" //Send when updating the Surface Vide Aspect Ratio size mode.
        const val LOAD_TIME_RANGES = "loadedTimeRanges"
        const val DRM_INITIALIZED = "drmInitialized"
        const val THUMBNAIL_INFO_RESPONSE = "thumbnailInfoResponse"
    }
}
