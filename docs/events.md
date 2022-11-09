## Events

Events can be listened by the FE apps inorder to understand more details about the Player. We have PlayerEvents, AdEvents and AnalyticsEvents.

1. App can import the events from our package,

    ```js
    import {
    PlayerEvents,
    AdEvents,
    AnalyticsEvents,
    } from 'kaltura-player-rn';
    ```

2. When you setup the player by calling `setup = async (playerType: PLAYER_TYPE, options: string, id: number = 0)` then after successful player setup, App should listen to the events, App should call the following method on `KalturaPlayerAPI`,

    ```js
    KalturaPlayerAPI.addListeners();
    ```

3. Now to listen to the Native Events, app should take help of `NativeEventEmitter`. App should take `KalturaPlayerEvents` from `NativeModules`.

    ```js

    const kalturaPlayerEvents = NativeModules.KalturaPlayerEvents;
    const playerEventEmitter = new NativeEventEmitter(kalturaPlayerEvents);

    ```

4. Now create a list of `EmitterSubscription` where you will add the listeners. Why this is important so that when user quits the app then app can remove/release all the events. Otherwise, it may lead to 'MemoryLeak'.

    ```js
    let eventsSubscriptionList: Array<EmitterSubscription> = [];
    ```

5. Listen to an events,

    ```js
    eventsSubscriptionList.push(
        playerEventEmitter.addListener(
            PlayerEvents.DURATION_CHANGE,
            (payload) => {
            console.log(
                'PlayerEvent DURATION_CHANGE : ' + JSON.stringify(payload));
            }
        )
        );

    ```

6. When the app goes to background or user quits the app then App should remove the listeners from the list creates in the 4th step and should call `removeListeners` on `KalturaPlayerAPI`.

    ```js

    if (eventsSubscriptionList.length > 0) {
        eventsSubscriptionList.forEach((event) => {
            console.log(`removing player subscription ${event}`);
            event.remove();
        });
        }

    KalturaPlayerAPI.removeListeners();

    ```


### Player Events
It talks about the various states of the player like when it is buffering, when there is a video bitrate change etc.


|PlayerEvents         |Data sent to RN |  iOS | Android |
|------------|--------|----------|----------|
|`STATE_CHANGED`|`{ "newState": <String> }`|✅ |✅ |
|`CAN_PLAY`|*No data sent*|✅ |✅ |
|`DURATION_CHANGE`|`{ "duration": <Number> }`|✅ |✅ |
|`ENDED`|*No data sent*|✅ |✅ |
|`ERROR`|<code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "errorType": errorType,<br>&nbsp;&nbsp;&nbsp;&nbsp; "errorCode": errorCode,<br>&nbsp;&nbsp;&nbsp;&nbsp; "errorSeverity":"Fatal",<br> &nbsp;&nbsp;&nbsp;&nbsp; "errorMessage": errorMessage, <br>&nbsp;&nbsp;&nbsp;&nbsp; "errorCause": errorCause <br>&nbsp;&nbsp;&nbsp;&nbsp; }</code> <br><br>***The `errorSeverity` in iOS is always Fatal when we get an error from the AVPlayer.***|✅ |✅ |
|`LOADED_METADATA`|*No data sent*|✅ |✅ |
|`PAUSE`|*No data sent*|✅ |✅ |
|`PLAY`|*No data sent*|✅ |✅ |
|`PLAYING`|*No data sent*|✅ |✅ |
|`SEEKED`|*No data sent*|✅ |✅ |
|`SEEKING`|`{ "targetPosition": targetSeekPosition }`|✅ |✅ |
|`TRACKS_AVAILABLE`|<code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "audio": audioTracks,<br>&nbsp;&nbsp;&nbsp;&nbsp; "text": textTracks,<br>&nbsp;&nbsp;&nbsp;&nbsp; "video":videoTracks,<br> &nbsp;&nbsp;&nbsp;&nbsp; "image": imageTracks <br>&nbsp;&nbsp;&nbsp;&nbsp;}</code><br><br> **Audio Track:** <br> <code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp;"id": String,<br>&nbsp;&nbsp;&nbsp;&nbsp;"bitrate":Number,<br>&nbsp;&nbsp;&nbsp;&nbsp;"language": String,<br>&nbsp;&nbsp;&nbsp;&nbsp;"label": String,<br>&nbsp;&nbsp;&nbsp;&nbsp;"channelCount": Number,<br>&nbsp;&nbsp;&nbsp;&nbsp;"isSelected": Boolean <br>&nbsp;&nbsp;&nbsp;&nbsp;}</code> <br> <br> **Video Track:** <br> <code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp;"id": String,<br>&nbsp;&nbsp;&nbsp;&nbsp;"width":Number,<br>&nbsp;&nbsp;&nbsp;&nbsp;"height": Number,<br>&nbsp;&nbsp;&nbsp;&nbsp;"bitrate": Number,<br>&nbsp;&nbsp;&nbsp;&nbsp;"isSelected": Boolean,<br>&nbsp;&nbsp;&nbsp;&nbsp;"isAdaptive": Boolean <br>&nbsp;&nbsp;&nbsp;&nbsp;}</code><br><br>**Text Track:**<br><code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "id": String,<br>&nbsp;&nbsp;&nbsp;&nbsp; "language": String,<br>&nbsp;&nbsp;&nbsp;&nbsp; "label":String,<br> &nbsp;&nbsp;&nbsp;&nbsp; "isSelected": Boolean <br>&nbsp;&nbsp;&nbsp;&nbsp;}</code><br><br>**Image Track:**<br> <code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "id": String,<br>&nbsp;&nbsp;&nbsp;&nbsp; "label": String,<br>&nbsp;&nbsp;&nbsp;&nbsp; "bitrate":Number,<br> &nbsp;&nbsp;&nbsp;&nbsp; "width": Number, <br>&nbsp;&nbsp;&nbsp;&nbsp; "height": Number,<br>&nbsp;&nbsp;&nbsp;&nbsp; "cols":Number,<br>&nbsp;&nbsp;&nbsp;&nbsp; "rows":Number,<br>&nbsp;&nbsp;&nbsp;&nbsp; "duration":Number,<br>&nbsp;&nbsp;&nbsp;&nbsp; "url":String,<br>&nbsp;&nbsp;&nbsp;&nbsp; "isSelected":Boolean<br> &nbsp;&nbsp;&nbsp;&nbsp;}</code><br><br> **Notes for iOS:**<br> *Video and Image tracks don't exist, therefore an empty array will be returned.*<br>*Audio Tracks, the bitrate and channelCount are not available will return -1, UNSET.*|✅ |✅ |
|`REPLAY`|*No data sent*|✅ |✅ |
|`PLAYBACK_INFO_UPDATED`|<code>{ <br> &nbsp;&nbsp;&nbsp;"android":&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "videoBitrate": Number,<br>&nbsp;&nbsp;&nbsp;&nbsp; "audioBitrate": Number,<br>&nbsp;&nbsp;&nbsp;&nbsp; "videoThroughput":Number,<br> &nbsp;&nbsp;&nbsp;&nbsp; "videoWidth": Number, <br>&nbsp;&nbsp;&nbsp;&nbsp; "videoHeight": Number <br>&nbsp;&nbsp;&nbsp;&nbsp; },<br> &nbsp;&nbsp;&nbsp;"ios":&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "bitrate": Number,<br>&nbsp;&nbsp;&nbsp;&nbsp; "indicatedBitrate": Number,<br>&nbsp;&nbsp;&nbsp;&nbsp; "observedBitrate":Number,<br> &nbsp;&nbsp;&nbsp;&nbsp; "averageVideoBitrate": Number, <br>&nbsp;&nbsp;&nbsp;&nbsp; "averageAudioBitrate": Number, <br>&nbsp;&nbsp;&nbsp;&nbsp; "uri": String<br>&nbsp;&nbsp;&nbsp;&nbsp; }<br> &nbsp;}</code><br>|✅ |✅ |
|`VOLUME_CHANGED`|`{ "volume": Number }`|❌ |✅ |
|`STOPPED`|*No data sent*|✅ |✅ |
|`METADATA_AVAILABLE`|<code>[<br>&nbsp;&nbsp;{<br>&nbsp;&nbsp;&nbsp;"schemeIdUri": "urn:scte:scte35:2013:xml",<br>&nbsp;&nbsp;&nbsp;"value": "999",<br>&nbsp;&nbsp;&nbsp;"messageData": [<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;60,<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;62<br>&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;],<br>&nbsp;&nbsp;&nbsp;"id": 165882295,<br>&nbsp;&nbsp;&nbsp;"durationMs": 10000<br>&nbsp;&nbsp;}<br>&nbsp;]</code><br> <br>**For iOS** <br> [`AVMetadataItem`](https://developer.apple.com/documentation/avfoundation/avmetadataitem)|❌ |✅ |
|`EVENT_STREAM_CHANGED`|**TODO**|❌ |✅ |
|`SOURCE_SELECTED`|**TODO**|✅ |✅ |
|`PLAYHEAD_UPDATED`|**TODO**|✅ |✅ |
|`VIDEO_TRACK_CHANGED`| **TODO** |✅ |✅ |
|`AUDIO_TRACK_CHANGED`| **TODO** |✅ |✅ |
|`TEXT_TRACK_CHANGED`| **TODO** |✅ |✅ |
|`IMAGE_TRACK_CHANGED`| **TODO** |❌ |✅ |
|`PLAYBACK_RATE_CHANGED`|`{ "rate": rate }` <br><br> **TODO in iOS**|✅ |✅ |
|`CONNECTION_ACQUIRED`|`{ "newState": <String> }`|❌|✅ |
|`VIDEO_FRAMES_DROPPED`|**TODO**| ❌ |✅ |
|`OUTPUT_BUFFER_COUNT_UPDATE`|**TODO**|❌ |✅ |
|`BYTES_LOADED`|**TODO**|❌ |✅ |
|`SUBTITLE_STYLE_CHANGED`|`{ "subtitlesStyleChanged": styleName }`|❌ |✅ |
|`ASPECT_RATIO_RESIZE_MODE_CHANGED`|`{ "surfaceAspectRationSizeModeChanged": resizeMode }`|❌ |✅ |
|`LOAD_TIME_RANGES`|**TODO**|✅ |✅ |
|`DRM_INITIALIZED`|**TODO**|✅ |✅ |


### AdEvents 
It talks about the ad's info like cuepoint, no of ads present in VMAP ad URL etc.<br>

|AdEvents         |Data sent to RN |  iOS | Android |
|------------|--------|----------|----------|
|`AD_REQUESTED`|`{ "adTagUrl": <String> }`|✅ |✅ |
|`AD_FIRST_PLAY`|*No data sent*|✅ |✅ |
|`STARTED`|*No data sent*|✅ |✅ |
|`PAUSED`|*No data sent*|✅ |✅ |
|`RESUMED`|*No data sent*|✅ |✅ |
|`COMPLETED`|*No data sent*|✅ |✅ |
|`FIRST_QUARTILE`|*No data sent*|✅ |✅ |
|`MIDPOINT`|*No data sent*|✅ |✅ |
|`THIRD_QUARTILE`|*No data sent*|✅ |✅ |
|`SKIPPED`|*No data sent*|✅ |✅ |
|`SKIPPABLE_STATE_CHANGED`|*No data sent*|✅ |✅ |
|`CLICKED`|`{ "clickThroughUrl": <String> }`|❌ |✅ |
|`TAPPED`|*No data sent*|✅ |✅ |
|`AD_BREAK_READY`|*No data sent*|✅ |✅ |
|`AD_PROGRESS`|`{ "currentAdPosition": <Number> }`|❌ |✅ |
|`AD_BREAK_STARTED`|*No data sent*|✅ |✅ |
|`AD_BREAK_ENDED`|*No data sent*|✅ |✅ |
|`AD_BREAK_FETCH_ERROR`|*No data sent*|❌ |✅ |
|`AD_BREAK_IGNORED`|*No data sent*|❌ |✅ |
|`CUEPOINTS_CHANGED`|<code>{<br> &nbsp;"ima": { <br> &nbsp;&nbsp;&nbsp; "cuePoints": [Number], <br> &nbsp;&nbsp;&nbsp; "count": Number, <br> &nbsp;&nbsp;&nbsp; "hasPreRoll": Boolean, <br> &nbsp;&nbsp;&nbsp; "hasMidRoll": Boolean, <br> &nbsp;&nbsp;&nbsp; "hasPostRoll": Boolean <br> &nbsp;} <br>} <code>|✅ |✅ |
|`LOADED`|**TODO**|❌ |✅ |
|`CONTENT_PAUSE_REQUESTED`|*No data sent*|❌ |✅ |
|`CONTENT_RESUME_REQUESTED`|*No data sent*|❌|✅ |
|`ALL_ADS_COMPLETED`|*No data sent*|❌ |✅ |
|`AD_BUFFER_START`|`{ "adPosition": <Number> }`|❌ |✅ |
|`AD_BUFFER_END`|`{ "adPosition": <Number> }`|❌ |✅ |
|`ERROR`|<code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "errorType": errorType,<br>&nbsp;&nbsp;&nbsp;&nbsp; "errorCode": errorCode,<br>&nbsp;&nbsp;&nbsp;&nbsp; "errorSeverity":"Fatal",<br> &nbsp;&nbsp;&nbsp;&nbsp; "errorMessage": errorMessage, <br>&nbsp;&nbsp;&nbsp;&nbsp; "errorCause": errorCause <br>&nbsp;&nbsp;&nbsp;&nbsp; }</code> |✅ |✅ |
|`ICON_FALLBACK_IMAGE_CLOSED`|*No data sent*|❌ |✅ |
|`ICON_TAPPED`|*No data sent*|❌ |✅ |
|`PLAY_HEAD_CHANGED`|`{ "adPlayHead": <Number> }`|❌ |✅ |
|`AD_PLAYBACK_INFO_UPDATED`|<code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "bitrate": Number,<br>&nbsp;&nbsp;&nbsp;&nbsp; "height": Number,<br>&nbsp;&nbsp;&nbsp;&nbsp; "width":Number<br>&nbsp;&nbsp;&nbsp;&nbsp; }</code> |❌ |✅ |
|`DAI_SOURCE_SELECTED`|`{ "sourceURL": <String> }`|❌|✅ |
|`adWaterFalling`|`{ "newState": <String> }`|❌ |❌ |
|`adWaterFallingFailed`|`{ "newState": <String> }`|❌ |❌ |


### AnalyticsEvents
It talks about more about the concurrency level errors on the stream. These events are only for Kaltura BE customers.


|AnalyticsEvents         |Data sent to RN |  iOS | Android |
|------------|--------|----------|----------|
|`PHOENIX_CONCURRENCY_ERROR`|<code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "code": 4001(Number) ,<br>&nbsp;&nbsp;&nbsp;&nbsp; "extra": KalturaAPIException(String), <br>&nbsp;&nbsp;&nbsp;&nbsp; "message": Concurrent play limitation(String) <br> &nbsp;&nbsp;&nbsp;&nbsp;}</code> <br><br> <code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "code": ConcurrencyLimitation(String) ,<br>&nbsp;&nbsp;&nbsp;&nbsp; "extra": KalturaAccessControlMessage(String), <br>&nbsp;&nbsp;&nbsp;&nbsp; "message": Concurrency limitation(String)<br>&nbsp;&nbsp;&nbsp;&nbsp; }</code>|✅ |❌ |
|`PHOENIX_BOOKMARK_ERROR`|**TODO**|✅ |❌ |
|`PHOENIX_ERROR`|<code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "code": 500015(Number) ,<br>&nbsp;&nbsp;&nbsp;&nbsp; "extra": KalturaAPIException(String), <br>&nbsp;&nbsp;&nbsp;&nbsp; "message": Invalid KS format(String), <br>&nbsp;&nbsp;&nbsp;&nbsp; "name": OTTError(String)<br> &nbsp;&nbsp;&nbsp;&nbsp; }</code> <br><br> <code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "code": 500016(Number) ,<br>&nbsp;&nbsp;&nbsp;&nbsp; "extra": KalturaAPIException(String), <br>&nbsp;&nbsp;&nbsp;&nbsp; "message": KS expired(String), <br>&nbsp;&nbsp;&nbsp;&nbsp; "name": OTTError(String)<br> &nbsp;&nbsp;&nbsp;&nbsp; }</code>|✅ |❌ |
|`SOURCE_URL_SWITCHED`|<code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "originalUrl": String,<br>&nbsp;&nbsp;&nbsp;&nbsp; "updatedUrl": String<br>&nbsp;&nbsp;&nbsp;&nbsp; }</code>|✅ |❌ |
|`CDN_SWITCHED`|<code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "cdnCode": String<br>&nbsp;&nbsp;&nbsp;&nbsp; }</code>|✅ |❌ |
|`BROADPEAK_ERROR`|<code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "errorMessage": String,<br>&nbsp;&nbsp;&nbsp;&nbsp; "errorCode": String<br>&nbsp;&nbsp;&nbsp;&nbsp; }</code>|✅ |❌ |
|`PHOENIX_REPORT_SENT`|<code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "eventName": String<br>&nbsp;&nbsp;&nbsp;&nbsp; }</code>|✅ |❌ |
|`KAVA_REPORT_SENT`|<code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "eventName": String<br>&nbsp;&nbsp;&nbsp;&nbsp; }</code>|✅ |❌ |
|`YOUBORA_REPORT_SENT`|<code>&nbsp;&nbsp;&nbsp;{ <br>&nbsp;&nbsp;&nbsp;&nbsp; "eventName": String<br>&nbsp;&nbsp;&nbsp;&nbsp; }</code>|✅ |❌ |