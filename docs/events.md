## Events

Events can be listened by the FE apps inorder to understand more details about the Player. We have PlayerEvents, AdEvents and AnalyticsEvents.
<br>
<br>
As the name says, **Player events** talk about the various states of the player like when it is buffering, when there is a video bitrate change etc.<br>
**Ad events** talk about the ad's info like cuepoint, no of ads present in VMAP ad URL etc.<br>
**Analytics events** talk about more about the concurrency level errors on the stream. This event is only meant for Kaltura BE customers.
<br>
<br>


|Player Event Name         |Data sent to RN |  iOS | Android |
|------------|--------|----------|----------|
|`stateChanged`|`{ "newState": <String> }`|✅ |✅ |
|`canPlay`|*No data sent*|✅ |✅ |
|`durationChanged`|`{ "duration": <Number> }`|✅ |✅ |
|`ended`|*No data sent*|✅ |✅ |
|`error`|<code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "errorType": errorType,<br>&nbsp;&nbsp;&nbsp;&nbsp; "errorCode": errorCode,<br>&nbsp;&nbsp;&nbsp;&nbsp; "errorSeverity":"Fatal",<br> &nbsp;&nbsp;&nbsp;&nbsp; "errorMessage": errorMessage, <br>&nbsp;&nbsp;&nbsp;&nbsp; "errorCause": errorCause <br>&nbsp;&nbsp;&nbsp;&nbsp; }</code> <br><br>***The `errorSeverity` in iOS is always Fatal when we get an error from the AVPlayer.***|✅ |✅ |
|`loadedMetadata`|*No data sent*|✅ |✅ |
|`pause`|*No data sent*|✅ |✅ |
|`play`|*No data sent*|✅ |✅ |
|`playing`|*No data sent*|✅ |✅ |
|`seeked`|*No data sent*|✅ |✅ |
|`seeking`|`{ "targetPosition": targetSeekPosition }`|✅ |✅ |
|`tracksAvailable`|<code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "audio": audioTracks,<br>&nbsp;&nbsp;&nbsp;&nbsp; "text": textTracks,<br>&nbsp;&nbsp;&nbsp;&nbsp; "video":videoTracks,<br> &nbsp;&nbsp;&nbsp;&nbsp; "image": imageTracks <br>&nbsp;&nbsp;&nbsp;&nbsp;}</code><br><br> **Audio Track:** <br> <code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp;"id": String,<br>&nbsp;&nbsp;&nbsp;&nbsp;"bitrate":Number,<br>&nbsp;&nbsp;&nbsp;&nbsp;"language": String,<br>&nbsp;&nbsp;&nbsp;&nbsp;"label": String,<br>&nbsp;&nbsp;&nbsp;&nbsp;"channelCount": Number,<br>&nbsp;&nbsp;&nbsp;&nbsp;"isSelected": Boolean <br>&nbsp;&nbsp;&nbsp;&nbsp;}</code> <br> <br> **Video Track:** <br> <code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp;"id": String,<br>&nbsp;&nbsp;&nbsp;&nbsp;"width":Number,<br>&nbsp;&nbsp;&nbsp;&nbsp;"height": Number,<br>&nbsp;&nbsp;&nbsp;&nbsp;"bitrate": Number,<br>&nbsp;&nbsp;&nbsp;&nbsp;"isSelected": Boolean,<br>&nbsp;&nbsp;&nbsp;&nbsp;"isAdaptive": Boolean <br>&nbsp;&nbsp;&nbsp;&nbsp;}</code><br><br>**Text Track:**<br><code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "id": String,<br>&nbsp;&nbsp;&nbsp;&nbsp; "language": String,<br>&nbsp;&nbsp;&nbsp;&nbsp; "label":String,<br> &nbsp;&nbsp;&nbsp;&nbsp; "isSelected": Boolean <br>&nbsp;&nbsp;&nbsp;&nbsp;}</code><br><br>**Image Track:**<br> <code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "id": String,<br>&nbsp;&nbsp;&nbsp;&nbsp; "label": String,<br>&nbsp;&nbsp;&nbsp;&nbsp; "bitrate":Number,<br> &nbsp;&nbsp;&nbsp;&nbsp; "width": Number, <br>&nbsp;&nbsp;&nbsp;&nbsp; "height": Number,<br>&nbsp;&nbsp;&nbsp;&nbsp; "cols":Number,<br>&nbsp;&nbsp;&nbsp;&nbsp; "rows":Number,<br>&nbsp;&nbsp;&nbsp;&nbsp; "duration":Number,<br>&nbsp;&nbsp;&nbsp;&nbsp; "url":String,<br>&nbsp;&nbsp;&nbsp;&nbsp; "isSelected":Boolean<br> &nbsp;&nbsp;&nbsp;&nbsp;}</code><br><br> **Notes for iOS:**<br> *Video and Image tracks don't exist, therefore an empty array will be returned.*<br>*Audio Tracks, the bitrate and channelCount are not available will return -1, UNSET.*|✅ |✅ |
|`replay`|*No data sent*|✅ |✅ |
|`playbackInfoUpdated`|<code>{ <br> &nbsp;&nbsp;&nbsp;"android":&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "videoBitrate": Number,<br>&nbsp;&nbsp;&nbsp;&nbsp; "audioBitrate": Number,<br>&nbsp;&nbsp;&nbsp;&nbsp; "videoThroughput":Number,<br> &nbsp;&nbsp;&nbsp;&nbsp; "videoWidth": Number, <br>&nbsp;&nbsp;&nbsp;&nbsp; "videoHeight": Number <br>&nbsp;&nbsp;&nbsp;&nbsp; },<br> &nbsp;&nbsp;&nbsp;"ios":&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "bitrate": Number,<br>&nbsp;&nbsp;&nbsp;&nbsp; "indicatedBitrate": Number,<br>&nbsp;&nbsp;&nbsp;&nbsp; "observedBitrate":Number,<br> &nbsp;&nbsp;&nbsp;&nbsp; "averageVideoBitrate": Number, <br>&nbsp;&nbsp;&nbsp;&nbsp; "averageAudioBitrate": Number, <br>&nbsp;&nbsp;&nbsp;&nbsp; "uri": String<br>&nbsp;&nbsp;&nbsp;&nbsp; }<br> &nbsp;}</code><br>|✅ |✅ |
|`volumeChanged`|`{ "volume": Number }`|❌ |✅ |
|`stopped`|*No data sent*|✅ |✅ |
|`metadataAvailable`|<code>[<br>&nbsp;&nbsp;{<br>&nbsp;&nbsp;&nbsp;"schemeIdUri": "urn:scte:scte35:2013:xml",<br>&nbsp;&nbsp;&nbsp;"value": "999",<br>&nbsp;&nbsp;&nbsp;"messageData": [<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;60,<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;62<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;],<br>&nbsp;&nbsp;&nbsp;"id": 165882295,<br>&nbsp;&nbsp;&nbsp;"durationMs": 10000<br>&nbsp;&nbsp;}<br>&nbsp;]</code><br> <br>**For iOS** <br> [`AVMetadataItem`](https://developer.apple.com/documentation/avfoundation/avmetadataitem)|❌ |✅ |
|`eventStreamChanged`|TODO|✅ |✅ |
|`sourceSelected`|TODO|✅ |✅ |
|`playheadUpdated`|TODO|✅ |✅ |
|`videoTrackChanged`| TODO |✅ |✅ |
|`audioTrackChanged`| TODO |✅ |✅ |
|`textTrackChanged`| TODO |✅ |✅ |
|`imageTrackChanged`| TODO |❌ |✅ |
|`playbackRateChanged`|`{ "rate": rate }` <br><br> **TODO in iOS**|✅ |✅ |
|`connectionAcquired`|`{ "newState": <String> }`|❌|✅ |
|`videoFramesDropped`|TODO| ❌ |✅ |
|`outputBufferCountUpdate`|TODO|❌ |✅ |
|`bytesLoaded`|TODO|❌ |✅ |
|`subtitlesStyleChanged`|`{ "subtitlesStyleChanged": styleName }`|❌ |✅ |
|`surfaceAspectRationSizeModeChanged`|`{ "surfaceAspectRationSizeModeChanged": resizeMode }`|❌ |✅ |
|`loadedTimeRanges`|TODO|✅ |✅ |
|`drmInitialized`|TODO|✅ |✅ |
|`thumbnailInfoResponse`|TODO|✅ |✅ |



