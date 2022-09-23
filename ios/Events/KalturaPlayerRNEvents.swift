//
//  KalturaPlayerRNEvents.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 30/06/2022.
//

enum KalturaPlayerRNEvents: String, CaseIterable {
    
    case stateChanged
    
    /** Sent when enough data is available that the media can be played; at least for a couple of frames. This corresponds to the HAVE_ENOUGH_DATA readyState.
     **/
    case canPlay
    
    /** The metadata has loaded or changed; indicating a change in duration of the media. This is sent; for example; when the media has loaded enough that the duration is known.
     **/
    case durationChanged
    
    /** Sent when playback completes.
     **/
    case ended
    
    /** Sent when an error occurs. The element"s error attribute contains more information. See Error handling for details.
     **/
    case error
    
    /** The media"s metadata has finished loading; all attributes now contain as much useful information as they"re going to.
     */
    case loadedMetadata
    
    /** Sent when playback is paused.
     */
    case pause
    
    /** Sent when playback of the media starts after having been paused; that is; when playback is resumed after a prior pause event.
     */
    case play
    
    /** Sent when the media begins to play (either for the first time; after having been paused; or after ending and then restarting).
     */
    case playing
    
    /** Sent when a seek operation compstatic letes.
     */
    case seeked
    
    /** Sent when a seek operation begins.
     */
    case seeking
    
    /** Sent when track info is available.
     */
    case tracksAvailable
    
    /** Sent when replay happened.
     */
    case replay
    
    /** Sent event that notify about changes in the playback parameters. When bitrate of the video or audio track changes or new media loaded. Holds the PlaybackInfo.java object with relevant data.
     */
    case playbackInfoUpdated // playbackInfo in PlayKit PlayerEvent

    /** Sent when stop player api is called
     */
    case stopped
    
    /** Sent when there is metadata available for this entry.
     */
    case metadataAvailable // timedMetadata, but it is deprecated from iOS 13 need to update to AVPlayerItemMetadataOutput. FEC-12060
    
    /** Sent when the source was selected.
     */
    case sourceSelected
    
    /** Send player position every 100 Milisec
     */
    case playheadUpdated
    
    case videoTrackChanged
    
    case audioTrackChanged
    
    case textTrackChanged
    
    case playbackRateChanged // playbackRate
    
    /** Send when updating the Surface Vide Aspect Ratio size mode.
     */
    case loadedTimeRanges
    
    // MARK: Missing events from the iOS side
//    case errorLog
//    case playbackStalled
}

// MARK: - Player Events that are in Android but not in iOS

enum KalturaPlayerUnsupportedRNEvents: String, CaseIterable {

    /** Sent when volume is changed.
     */
    case volumeChanged
    
    /** Send event streams received from manifest
     */
    case eventStreamChanged
    
    case imageTrackChanged
    
    case connectionAcquired
    
    /** Video frames were dropped; see PlayerEvent.VideoFramesDropped
     */
    case videoFramesDropped
    
    case outputBufferCountUpdate

    /** Bytes were downloaded from the network
     */
    case bytesLoaded
    
    /** Subtitle style is changed.
     */
    case subtitlesStyleChanged
    
    case surfaceAspectRationSizeModeChanged

    case drmInitialized
    
    case thumbnailInfoResponse
}
