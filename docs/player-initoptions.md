## Player Configurations

`playerInitOptions` includes the important parameters required for the Player setup. 

* [Kaltura BE configs](#kaltura-be-configs)
* [Network Settings](#network-settings)
* [ABR (Adaptive Bitrate) Settings](#abr-adaptive-bitrate-settings)
* [TrackSelection](#trackselection)
* [Low Latency Config for Live Media ](#low-latency-config-for-live-media)
* [MulticastSettings](#multicastsettings)
* [Subtitle Styling and Positioning](#subtitle-styling-and-positioning)
* [Subtitle Preference](#subtitle-preference)
* [VideoCodecSettings](#videocodecsettings)
* [AudioCodecSettings](#audiocodecsettings)
* [LoadControlBuffers](#loadcontrolbuffers)
* [VRSettings](#vrsettings)
* [Player Resize Modes](#player-resize-modes)
* [WakeLock mode Preference](#wakelock-mode-preference)
* [More `PlayerInitOptions`](#more-playerinitoptions)

### Kaltura BE configs

  > Required only for OTT/OVP Customers. Server url and ks will be provided by Kaltura BE.

  ```js
  var initOptions = {
    serverUrl: 'server_url',
    ks: 'kaltura_secret'
  }
  ```

### Network Settings 

  ✅ Android

  ```js
  var initOptions = {
  requestConfig: {
      crossProtocolRedirectEnabled: true,
      readTimeoutMs: 8000,
      connectTimeoutMs: 8000,
      maxRetries: 0
    }
  }
  ```
  `crossProtocolRedirectEnabled` : Default is `false`. Set if network request are going from http to https OR https to http servers.

  `readTimeoutMs` : Read time out for the network requests. Default is 8000ms.

  `connectTimeoutMs` : Connection time out for the network requests. Default is 8000ms.

  `maxRetries` : Maximum number of times to retry a load in the case of a load error, before propagating the error. Default is 0 means no retries.

  -----

  ✅ iOS

  ```js

  requestConfig: {
      autoBuffer: true,
      automaticallyWaitsToMinimizeStalling: true,
      preferredForwardBufferDuration: 8000,
    }
  }

  ```

  🔴 TODO DOC FOR IOS


### ABR (Adaptive Bitrate) Settings

  ❌ iOS    ✅ Android

  ```js
  var initOptions = {
  abrSettings: {
      minVideoBitrate: 600000,
      maxVideoBitrate: 1500000,
    }
  }
  ```

  `initialBitrateEstimate` : Sets the initial bitrate estimate in bits per second that should be assumed when a bandwidth estimate is unavailable. To reset it, set it to null. Initial bitrate is only meant at the start of the playback.

  `minVideoBitrate` : Set minVideoBitrate in ABR

  `maxVideoBitrate` : Set maxVideoBitrate in ABR

  `maxVideoHeight` : Set maxVideoHeight in ABR

  `minVideoHeight` : Set minVideoHeight in ABR

  `maxVideoWidth` : Set maxVideoWidth in ABR

  `minVideoWidth` : Set minVideoWidth in ABR


### TrackSelection 

  ```js
  var initOptions = {
  trackSelection: {
      textMode: 600000,
      textLanguage: String,
      audioMode: 1500000,
      audioLanguage: String,
    }
  }
  ```

  For `textMode` and `audioMode` please check [`Constants`](./player-functions.md#constants#TRACK_SELECTION_MODE)


### Low Latency Config for Live Media 

  ```js
  var initOptions = {
  lowLatencyConfig: {
      targetOffsetMs: Number,
      minOffsetMs: Number,
      maxOffsetMs: Number,
      minPlaybackSpeed: Float Number,
      maxPlaybackSpeed: Float Number,
    }
  }
  ```

  `targetOffsetMs` : Player takes an account of the bandwidth as well where it tries to avoid re-buffer while
   approaching to the `targetOffsetMs`. 

   Following Settings are only available in ✅ Android not in ❌ iOS.

   `minOffsetMs` : The minimum allowed live offset, in milliseconds, or to use the media-defined default. Even when adjusting the offset to current network conditions, the player will not attempt to get below this offset during playback.

   `maxOffsetMs` : The maximum allowed live offset, in milliseconds, or to use the media-defined default. Even when adjusting the offset to current network conditions, the player will not attempt to get above this offset during playback.

   `minPlaybackSpeed` : Minimum playback speed, or to use the media-defined default. The minimum playback speed the player can use to fall back when trying to reach the target live offset. Default value is **0.97f**.

   `maxPlaybackSpeed` : Maximum playback speed, or {@link Consts#RATE_UNSET} to use the media-defined default. The maximum playback speed the player can use to catch up when trying to reach the target live offset. Default value is **1.03f**.


### MulticastSettings

  ❌ iOS    ✅ Android

  ```js
  var initOptions = {
  multicastSettings: {
      useExoDefaultSettings: Boolean,
      maxPacketSize: Number,
      socketTimeoutMillis: Number,
      extractorMode: Number,
      firstSampleTimestampUs: Number,
    }
  }
  ```

  `useExoDefaultSettings`: Whether mulicast playback will use exo default config or app config. Default is `true`. 

  `maxPacketSize`: The maximum datagram packet size, in bytes. Default value is 3000.

  `socketTimeoutMillis`: The socket timeout in milliseconds. A timeout of zero is interpreted. Default value is 10000.

  `extractorMode`: Modes for the extractor. One of `MODE_MULTI_PMT`, `MODE_SINGLE_PMT`, `MODE_HLS`. please check [`Constants`](./player-functions.md#constants#MULTICAST_EXTRACTOR_MODE)

  `firstSampleTimestampUs`: The desired value of the first adjusted sample timestamp in microseconds - for no offset give MAX_LONG.



### Subtitle Styling and Positioning

  🔴 TODO: Update DOC for iOS Because it is partially implemented there.

  App can change the subtitltes' text color, background color etc along with the subtitle's position on the view.

  Following subtitle Configuration can be used. please check [`Constants`](./player-functions.md#constants#SUBTITLE_STYLE)

  ```js
  import { SUBTITLE_STYLE } from "kaltura-player-rn";
  ```

  ```js
  var initOptions = {
  subtitleStyling: {
      subtitleStyleName: 'MyCustomSubtitleStyle',
      subtitleTextColor: '#FFFFFF',
      subtitleBackgroundColor: '#FF00FF',
      subtitleWindowColor: '#FF00FF',
      subtitleEdgeColor: '#0000FF',
      subtitleTextSizeFraction: SUBTITLE_STYLE.FRACTION_50,
      subtitleStyleTypeface: SUBTITLE_STYLE.MONOSPACE,
      subtitleEdgeType: SUBTITLE_STYLE.EDGE_TYPE_DROP_SHADOW,
      overrideInlineCueConfig: true,
      verticalPositionPercentage: 50,
      horizontalPositionPercentage: 50,
      horizontalAlignment: SUBTITLE_STYLE.HORIZONTAL_ALIGNMENT_CENTER,
    }
  }
  ```

### Subtitle Preference

  If the same language text track is available for internal and external subtitles then app can give preference to any one of them. Default is set to internal subtitle.

  ```js
  import { SUBTITLE_PREFERENCE } from "kaltura-player-rn";
  ```

  ```js
  var initOptions = {
    subtitlePreference: SUBTITLE_PREFERENCE.EXTERNAL,
  }
  ```

### VideoCodecSettings

  ❌ iOS    ✅ Android

  ```js
  var initOptions = {
    videoCodecSettings : {
      codecPriorityList: List of Video Codecs,
      allowSoftwareDecoder : false,
      allowMixedCodecAdaptiveness : false
    }
  }
  ```

  `codecPriorityList` : List of Video codecs can be found in [`Constants`](./player-functions.md#constants#VIDEO_CODEC). Based on the priority list player will try to pick video codec if more than one codec is present in manifest.

  `allowSoftwareDecoder` : Allow to switch from H/W codec to S/W codec id required. Default is disabled.

  `allowMixedCodecAdaptiveness`: Default is disabled.


### AudioCodecSettings

  ❌ iOS    ✅ Android

  ```js
  var initOptions = {
    audioCodecSettings : {
      codecPriorityList: List of Audio Codecs,
      allowMixedCodecs : false,
      allowMixedBitrates : false
    }
  }
  ```

  `codecPriorityList` : List of Audio codecs can be found in [`Constants`](./player-functions.md#constants#VIDEO_CODEC). Based on the priority list player will try to pick audio codec if more than one codec is present in manifest.

  `allowMixedCodecs` : Default is disabled.

  `allowMixedBitrates`: Default is disabled.


### LoadControlBuffers

  Control the player buffering capabilities.

  ❌ iOS    ✅ Android

  ```js
  var initOptions = {
    loadControlBuffers : {
      minPlayerBufferMs: 50000,
      maxPlayerBufferMs : 50000,
      minBufferAfterInteractionMs : 2500,
      minBufferAfterReBufferMs: 5000,
      backBufferDurationMs: 0,
      retainBackBufferFromKeyframe: false,
      allowedVideoJoiningTimeMs: 5000,
    }
  }
  ```

  `minPlayerBufferMs` : The default minimum duration of media that the player will attempt to ensure is buffered at all.

  `maxPlayerBufferMs` : The default maximum duration of media that the player will attempt to buffer.

  `minBufferAfterInteractionMs` : The default duration of media that must be buffered for playback to start or resume following a user action such as a seek.

  `minBufferAfterReBufferMs` : The default duration of media that must be buffered for playback after re-buffering.

  `backBufferDurationMs` : Value for back buffer. Default is 0.

  `retainBackBufferFromKeyframe` : The default for whether the back buffer is retained from the previous keyframe. 

  `allowedVideoJoiningTimeMs` : Maximum duration for which a video renderer can attempt to seamlessly join an ongoing playback. Default is 5000ms.

### VRSettings

  VR/360 media control settings.

  ```js
  var initOptions = {
    vrSettings: {
      vrModeEnabled: true,
      zoomWithPinchEnabled: false,
      flingEnabled: true,
      interactionMode: "MotionWithTouch"
    }
  }
  ```

  List of Interation modes can be found in [`Constants`](./player-functions.md#constants#VR_INTERACTION_MODE)


### Player Resize Modes

  ```js
  import { PLAYER_RESIZE_MODES } from "kaltura-player-rn";
  ```

  ```js
  var initOptions = {
  aspectRatioResizeMode: PLAYER_RESIZE_MODES.FIT
  }
  ```
  There are other resize modes available like **FIXED_WIDTH**, **FIXED_HEIGHT**, **FILL** and **ZOOM**



### WakeLock mode Preference

Set WakeLock Mode  - Sets whether the player should not handle wakeLock or should handle a wake lock only or both wakeLock & wifiLock when the screen is off

  ```js
  import { WAKEMODE } from "kaltura-player-rn";
  ```

  ```js
  var initOptions = {
    wakeMode: WAKEMODE.NETWORK,
  }
  ```


### More `PlayerInitOptions`


|API         |iOS     | Android |  Remarks |
|------------|--------|---------|----------|
|`autoplay`     | ✅     |  ✅    |    `Boolean` Default is `true`       |
|`preload`     | ✅     |  ✅     |    `Boolean` Default is `true`      |
|`allowCrossProtocolRedirect`     | ❌     |  ✅    |   `Boolean` Default is `true`.       |
|`warmupUrls`     | ❌     |  ✅    |    `List<String>`. This config will help to decrease the jointime by doing the handshake with the servers before doing the actual media playback calls. Pass the list of server URLs. It is recommended to pass before the loading the media.      |
|`referrer`     | ✅     |  ✅    |    Referrer `String`.      |
|`preferredMediaFormat`     | ❌     |  ✅    |   First priority is given to DASH media in Android. In case the multirequest has various media formats, and no  formats or fileIds are explicitly given, while going over the sources from the getPlaybackContext of the multirequest, the player will check the preferredMediaFormat if provided. Please check the [`Constants`](./player-functions.md#constants#MEDIA_FORMAT)       |
|`allowClearLead`     | ❌     |  ✅    |   `Boolean` If the media has clear segments at the start of a DRM protected content. Allow the player to play those. Default is enabled.      |
|`enableDecoderFallback`     | ❌     |  ✅    |  `Boolean` Decide whether to enable fallback to lower-priority decoders if decoder initialization fails. Default is enabled.      |
|`secureSurface`     | ❌    |  ✅    |   `Boolean` Decide if player should use secure rendering on the surface. Default is disabled. |
|`adAutoPlayOnResume`     | ❌     |  ✅    |    `Boolean` Decide the Ad will be auto played when comes to foreground from background. Default is `true` only with IMA plugin.     |
|`isVideoViewHidden`     | ❌     |  ✅    |    `Boolean` If set to `true` then video frame will be hidden. Only audio will be there.     |
|`forceSinglePlayerEngine`     | ❌     |  ✅    | `Boolean` Allowed with IMA Plugin only. Do not prepare the content player when the Ad starts(if exists); instead content player will be prepared when `content_resume_requested` is called. |        
|`isTunneledAudioPlayback`     | ❌     |  ✅    |  `Boolean` Default is `false`. Set Tunneled Audio Playback  |      
|`handleAudioBecomingNoisyEnabled`     | ❌     |   ✅   |  `Boolean` Default is `false`. Set HandleAudioBecomingNoisy - Sets whether the player should pause automatically when audio is rerouted from a headset to device speakers.  |      
|`handleAudioFocus`     |  ❌   |   ✅    |    `Boolean` Default is `false`. Set HandleAudioFocus - Support for automatic audio focus handling   |
|`maxAudioBitrate`     | ❌     |   ✅   |  Sets the maximum allowed audio bitrate in bits per second.  |    
|`maxAudioChannelCount`     |  ❌    |  ✅    |    Sets the maximum allowed audio channel count.     |




