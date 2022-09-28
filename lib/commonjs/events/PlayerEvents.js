"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.PlayerEvents = void 0;
const PlayerEvents = {
  // TODO: All the "" string needs to be added on Native android layer
  // TODO: Generate it using some script
  // TODO: Add pending events
  STATE_CHANGED: 'stateChanged',
  CAN_PLAY: 'canPlay',
  // Sent when enough data is available that the media can be played, at least for a couple of frames. This corresponds to the HAVE_ENOUGH_DATA readyState.
  DURATION_CHANGE: 'durationChanged',
  //  The metadata has loaded or changed, indicating a change in duration of the media. This is sent, for example, when the media has loaded enough that the duration is known.
  ENDED: 'ended',
  //  Sent when playback completes.
  ERROR: 'error',
  //  Sent when an error occurs. The element's error attribute contains more information. See Error handling for details.
  LOADED_METADATA: 'loadedMetadata',
  //  The media's metadata has finished loading; all attributes now contain as much useful information as they're going to.
  PAUSE: 'pause',
  //  Sent when playback is paused.
  PLAY: 'play',
  //  Sent when playback of the media starts after having been paused; that is, when playback is resumed after a prior pause event.
  PLAYING: 'playing',
  //  Sent when the media begins to play (either for the first time, after having been paused, or after ending and then restarting).
  SEEKED: 'seeked',
  //  Sent when a seek operation completes.
  SEEKING: 'seeking',
  //  Sent when a seek operation begins.
  TRACKS_AVAILABLE: 'tracksAvailable',
  // Sent when track info is available.
  REPLAY: 'replay',
  //Sent when replay happened.
  PLAYBACK_INFO_UPDATED: 'playbackInfoUpdated',
  // Sent event that notify about changes in the playback parameters. When bitrate of the video or audio track changes or new media loaded. Holds the PlaybackInfo.java object with relevant data.
  VOLUME_CHANGED: 'volumeChanged',
  // Sent when volume is changed.
  STOPPED: 'stopped',
  // sent when stop player api is called
  METADATA_AVAILABLE: 'metadataAvailable',
  // Sent when there is metadata available for this entry.
  EVENT_STREAM_CHANGED: 'eventStreamChanged',
  //Send event streams received from manifest
  SOURCE_SELECTED: 'sourceSelected',
  // Sent when the source was selected.
  PLAYHEAD_UPDATED: 'playheadUpdated',
  //Send player position every 100 Milisec
  VIDEO_TRACK_CHANGED: 'videoTrackChanged',
  AUDIO_TRACK_CHANGED: 'audioTrackChanged',
  TEXT_TRACK_CHANGED: 'textTrackChanged',
  IMAGE_TRACK_CHANGED: 'imageTrackChanged',
  PLAYBACK_RATE_CHANGED: 'playbackRateChanged',
  CONNECTION_ACQUIRED: 'connectionAcquired',
  VIDEO_FRAMES_DROPPED: 'videoFramesDropped',
  // Video frames were dropped, see PlayerEvent.VideoFramesDropped
  OUTPUT_BUFFER_COUNT_UPDATE: 'outputBufferCountUpdate',
  BYTES_LOADED: 'bytesLoaded',
  // Bytes were downloaded from the network
  SUBTITLE_STYLE_CHANGED: 'subtitlesStyleChanged',
  // Subtitle style is changed.
  ASPECT_RATIO_RESIZE_MODE_CHANGED: 'surfaceAspectRationSizeModeChanged',
  //Send when updating the Surface Vide Aspect Ratio size mode.
  LOAD_TIME_RANGES: 'loadedTimeRanges',
  DRM_INITIALIZED: 'drmInitialized'
};
exports.PlayerEvents = PlayerEvents;
//# sourceMappingURL=PlayerEvents.js.map