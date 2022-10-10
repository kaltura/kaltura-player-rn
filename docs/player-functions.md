## Player Functions

* [Setup](#setup)
  * [Adding and removing the Player Listerners](#adding-and-removing-the-player-listerners)
  * [Load the Player](#load-the-player)
* [Changing the media (Play the next/previous media)](#changing-the-media-play-the-nextprevious-media)
* [Handle background and foreground application behaviour](#handle-background-and-foreground-application-behaviour)
* [Add or remove the Player View component (Native Level)](#add-or-remove-the-player-view-component-native-level)
* [More Player Functions](#more-player-functions)
* [Constants](#constants)

### Setup
---

`setup = async (playerType: PLAYER_TYPE, options: string, id: number = 0)`

This method creates a Player instance internally (Basic, OVP/OTT Player)
With this, it take the PlayerInitOptions which are having essential Player settings values. 

`playerType`: **Mandatory** The Player Type, Basic/OVP/OTT. [Check Constants](#Constants)

`options` : For PlayerType 'Basic', this is _**Optional**_. For OVP/OTT, it is **Mandatory**. `playerInitOptions` JSON String [Check playerInitOptions](./player-initoptions.md)

 `id` : For PlayerType 'Basic', this is _**Not Required**_. For OVP/OTT, it is **Mandatory**. For OVP/OTT player this value should be always greater than 0 and should be valid; otherwise, we will not be able to featch the details for the mediaId or the entryId)

### Adding and removing the Player Listerners
---

`addListeners()`

Add the listners for the Kaltura Player. This will enable the Apps to listen to the Kaltura Player events, Ad events and Analytics events.

`removeListeners()`

Remove the added Player listeners.

[Check PlayerEvents for more info](./player-event.md)

### Load the Player 
---

`loadMedia = async (id: string, asset: string)`

Load the media with the given **assetId** OR **mediaId** OR **entryID** for OVP/OTT Kaltura Player and **playbackURL** for Basic Kaltura Player.

`id` : **Mandatory playback URL** for Kaltura Basic Player OR **MediaId** for Kaltura OTT Player OR **EntryId** for Kaltura OVP Player.

`asset` : **Optional**. Media Asset JSON String.

[**`asset` for Basic Player**: _**Optional**_](). App can use the following config. Should only be used by non-Kaltura BE users. 

```json
          "mediaAsset": {
            "id": "basicId",
            "name": "basicName",
            "duration": 120,
            "mediaEntryType": "Vod",
            "mediaFormat": "hls",
            "startPosition": 0,
            "isVRMediaType": true
          }

```
* `id` *String*  - _**Optional**_ Random id for this media.
* `name` *String* - _**Optional**_ Name given to this media.
* `duration`*Number* - _**Optional**_ Duration of this media content.
* `mediaEntryType`*String* - _**Optional**_ [Check Constants](#Constants) for media type. It can be VOD, Live etc.
* `mediaFormat` *String* -  **Mandatory** [Check Constants](#Constants) for media format. It can be Dash, HLS etc.
* `startPosition` *Number* - _**Optional**_ If you want the player to start from a certain position. Default is 0.
* `isVRMediaType` *Boolean* - _**Optional**_ Only to be passed while using VR media.
* `drmData` *JSON* - _**Optional**_ 🔴TODO
* `metadata` *JSON* - _**Optional**_ 

 ```json
 "startPosition": 120,
  "metaData": {
    "key": "value"
  } 
 ```
* `externalSubtitleList` *JSONArray* - _**Optional**_  🔴TODO
* `externalVttThumbnailUrl` - _**Optional**_  Pass the VTT thumbnail URL. These images will be shown when the user scrubs on the seekbar. To show the images logic should be handled by the FE app. You will recieve the additional information the `ImageTracks` of `TracksAvailable` event. [Check PlayerEvents](./player-event.md)

[**`asset` for  OVP/OTT Player**: _**Optional**_]() App can use the following config. Should only be used by the Kaltura BE users.

```json
          "mediaAsset": {
            "initialVolume": 1.0,
            "format": ["Mobile_Main"],
            "assetType": "media",
            "networkProtocol": "http",
            "playbackContextType": "playback",
            "startPosition": 0
          },

```
* `ks` *String* - _**Optional for OTT/OVP.**_ Kaltra Secret. It will be provided by our BE.
* `format` *JSONArray String* - _**Optional for OTT.**_ Formats provided for the playback. Defined in our BE.
* `fileId` *JSONArray String* - _**Optional for OTT.**_ FileIds for the playback. Defined in our BE.
* `assetType` *String* - _**Optional for OTT.**_ [Check Constants](#Constants) for AssetTypes.
* `playbackContextType` *String* - _**Optional for OTT.**_ [Check Constants](#Constants) for PlaybackContextType.
* `assetReferenceType` *String* - _**Optional for OTT.**_ [Check Constants](#Constants) for AssetReferenceType.
* `protocol` *String* - _**Optional for OTT.**_ `http` or `https`.
* `urlType` *String* - _**Optional for OTT.**_ [Check Constants](#Constants) for URLType.
* `streamerType` *String* - _**Optional for OTT.**_ [Check Constants](#Constants) for StreamerType.
* `adapterData` *JSONObject* - _**Optional for OTT.**_ Additional Adapter data supplied in config for our BE.
    ```json
    "other":"other",
    "metaData": {
    "key": "value"
   } 
   ```
 
* `referrer` *String* - _**Optional for OTT/OVP.**_ Referer string.
* `startPosition` *Number* - _**Optional for OTT/OVP.**_ If you want the player to start from a certain position. Default is 0.
* `initialVolume` *Number* - _**Optional for OTT/OVP.**_ Default is 1.0f. Change the volume of the current audio track. It's a decimal value. Accept values between 0.0 and 1.0. Where 0.0 is mute and 1.0 is maximum volume. If the volume parameter is higher then 1.0, it will be converted to 1.0. If the volume parameter is lower then 0.0, it be converted to 0.0.

* `redirectFromEntryId` *Boolean* - _**Optional for OVP.**_ Default is `true`. **Only for OVP BE users**
* `useApiCaptions` *Boolean* - _**Optional for OVP.**_ Default is `false`. **Only for OVP BE users**

### Changing the media (Play the next/previous media)
---

App can simply call `loadMedia = async (id: string, asset: string)` with the next/previous media using the steps given [here](./player-functions.md#load-the-player).

> Note: If app is setting up 'OVP' or 'OTT' Player they can still pass the playback URL like it can be does using 'Basic' Player. 

_Example:_ Here OVP Player is created but for the next media (changeMedia) app is simply passing a playback URL.

```js
        {
          "id": "ChangeMedia OVP -> Basic Media",
          "partnerId": 2215841,
          "initOptions": {
            "serverUrl": "https://cdnapisec.kaltura.com",
            "autoplay": true
          },
          "mediaList": [
            {
              "mediaId": "1_w9zx2eti"
            },
            {
              "mediaId": "https://storage.googleapis.com/wvmedia/clear/h264/tears/tears.mpd"
            }
          ]
        }

```


### Handle background and foreground application behaviour
---

An important step for mobile apps where user can move to another app. So to handle this app can call the following methods,

`onApplicationPaused()`: Call when the app goes to background, ideally `onPause()` as per the Android lifecycle.

`onApplicationResumed()`: Call when the app comes to foreground, ideally `onResume()` as per the Android lifecycle.

> **Not available for iOS for the time.**

### Add or remove the Player View component (Native Level)
---

By default Player view component is attached to the Player so no need to call `addPlayerView` explicitely.

`addPlayerView()`: Adds the Native Player View to the Player if not attached.

`removePlayerView`: Removes the Native Player View from the Player if it is attached.

> **Not available for iOS for the time.**


### More Player Functions
---

|API         |iOS     | Android |  Remarks |
|------------|--------|---------|----------|
|`play()`      | ✅     |  ✅    |    Play the content/Ad if it is not playing |
|`pause()`     | ✅     |  ✅    |    Pause the content/Ad if it is playing       |
|`stop()`     | ✅     |  ✅     |    Stops the player to the initial state      |
|`destroy()`     | ✅     |  ✅    |   Destroy the Kaltura Player instance       |
|`replay()`     | ✅     |  ✅    |    Replays the media from the beginning      |
|`seekTo(position: number)`     | ✅     |  ✅    |    Seek the player to the specified position, position in miliseconds      |
|`changeTrack(trackId: string)`     | ✅     |  ✅    |   Change a specific track (Video, Audio or Text track) `trackId` Unique track ID which was sent in `tracksAvailable` event       |
|`setPlaybackRate(rate: number)`     | ✅     |  ✅    |   Change the playback rate (ff or slow motion). Default is 1.0f. `rate` Desired playback rate (Ex: 0.5f, 1.5f 2.0f etc)       |
|`setVolume(vol: number)`     | ✅     |  ✅    |   Change the volume of the current audio track. Accept values between 0.0 and 1.0. Where 0.0 is mute and 1.0 is maximum volume. If the volume parameter is higher then 1.0, it will be converted to 1.0. If the volume parameter is lower then 0.0, it be converted to 0.0.       |
|`setAutoPlay(isAutoPlay: boolean)`     | ❌    |  ✅    |   Set the media to play automatically at the start (load). If `false`, user will have to click on UI play button       |
|`setKS(KS: string)`     | ❌     |  ✅    |    Set the KS for the media _(only for OVP/OTT users)_. Call this before calling `loadMedia`. `KS` Kaltura Secret key.      |
|`seekToLiveDefaultPosition()`     | ✅     |  ✅    |    Seek player to Live Default Position. _Only for Live Media._      |
|`updateSubtitleStyle(subtitleStyle: string)()`     | ❌     |  ✅    | Update the existing subtitle styling  |        
|`updateResizeMode(mode: PLAYER_RESIZE_MODES)()`     | ✅     |  ✅    |  Update the Resize Mode  |      
|`updateAbrSettings(abrSettings: string)()`     | ❌     |   ✅   |  Update the ABR Settings  |      
|`resetAbrSettings()`     |  ❌   |   ✅    |    Reset the ABR Settings      |
|`updateLowLatencyConfig(lowLatencyConfig: string)()`     | ❌     |   ✅   |  Update the Low Latency Config. _Only for Live Media_    |    
|`resetLowLatencyConfig()`     |  ❌    |  ✅    |    Reset the Low Latency Config. _Only for Live Media_      |
|`getCurrentPosition()`     | ✅     |  ✅    |    **Async** function. Getter for the current playback position. Returns `string` Position of the player or -1.     |
|`isPlaying()`     | ✅     |  ✅    |   **Async** function. Checks if Player is currently playing or not. Returns `boolean`.     |
|`isLive()`     | ✅     |  ✅    |    **Async** function. Checks if the stream is Live or Not. Returns `boolean`.     |
|`requestThumbnailInfo(positionMs: number)`     | ❌     |  ✅    |    **Async** function. Get the Information for a thumbnail image by position. Returns **ThumbnailInfo JSON** object.     |
|`enableDebugLogs = (enabled: boolean, logLevel: LOG_LEVEL = LOG_LEVEL.DEBUG)`     | ✅     |  ✅    |   Enable the console logs for the JS bridge and Player. By default the logs are disabled. For logLevel options check [constants](#Constants#LOG_LEVEL). Just set `enabled` to `false` to disable all the logs. Default logLevel `LOG_LEVEL.DEBUG` if set to `LOG_LEVEL.OFF` will turn off the logs.      |


### Constants
---

There are various constants available which are essential to be used while setting up the Player configs.

- **PLAYER_TYPE**

   - `OVP` 
	  Kaltura OVP BE customers should pass this. Only EntryId should be passed on `MediaAsset` level. Media URL and DRM license will be fetched from BE by the player.
	
	- `OTT` : Kaltura OTT BE customers should pass this. Only MediaId should be passed on `MediaAsset` level. Media URL and DRM license will be fetched from BE by the player.
	
	- `BASIC` : Non Kaltura users should pass this. Media URL and DRM license should be passed on the `MediaAsset` level.


```js
export enum PLAYER_TYPE {
  OVP = 'ovp',
  OTT = 'ott',
  BASIC = 'basic'
}

```


- **MEDIA_FORMAT** <br>
  Supports Playback formats.

```js
export enum MEDIA_FORMAT {
  DASH = 'dash',
  HLS = 'hls',
  WVM = 'wvm',
  MP4 = 'mp4',
  MP3 = 'mp3',
  UDP = 'udp'
}

```

- **MEDIA_ ENTRY_TYPE** <br>
  Type of the media VOD, Live or DvrLive.

```js
export enum MEDIA_ENTRY_TYPE {
  VOD = 'Vod',
  LIVE = 'Live',
  DVRLIVE = 'DvrLive'
}

```

- **DRM_SCHEME** <br>
  DRM Type WidevineCENC, Fairplay, Playready etc.

```js
export enum DRM_SCHEME {
  WIDEVINE_CENC = 'WidevineCENC',
  PLAYREADY_CENC = 'PlayReadyCENC',
  WIDEVINE_CLASSIC = 'WidevineClassic',
  PLAYREADY_CLASSIC = 'PlayReadyClassic',
  FAIRPLAY = 'FairPlay'
}

```

- **PLAYER_PLUGIN** <br>
  Player Plugins like IMA for Ads, Youbora for Analytics etc.

```js
export enum PLAYER_PLUGIN {
  IMA = 'ima',
  IMADAI = 'imadai',
  YOUBORA = 'youbora',
  KAVA = 'kava',
  OTT_ANALYTICS = 'ottAnalytics',
  BROADPEAK = 'broadpeak'
}
```

- **KALTURA_ STREAMER_TYPE** <br>
  ONLY FOR THE KALTURA BE CUSTOMERS. This constant is helpful for the BE request.

```js
export enum KALTURA_STREAMER_TYPE {
  APPLE_HTTP = 'applehttp',
  MPEG_DASH = 'mpegdash',
  URL = 'url',
  SMOTH_STREAMING = 'smothstreaming',
  MULTICAST = 'multicast',
  NONE = 'none'
}

```

- **KALTURA_ URL_TYPE** <br>
  ONLY FOR THE KALTURA BE CUSTOMERS. This constant is helpful for the BE request.

```js
export enum KALTURA_URL_TYPE {
  PLAYMANIFEST = 'PLAYMANIFEST',
  DIRECT = 'DIRECT'
}

```

- **KALTURA_ PLAYBACK_ CONTEXT_TYPE** <br>
  ONLY FOR THE KALTURA BE CUSTOMERS. This constant is helpful for the BE request.

```js
export enum KALTURA_PLAYBACK_CONTEXT_TYPE {
  TRAILER = 'TRAILER',
  CATCHUP = 'CATCHUP',
  START_OVER = 'START_OVER',
  PLAYBACK = 'PLAYBACK'
}
```

- **KALTURA_ ASSET_TYPE** <br>
  ONLY FOR THE KALTURA BE CUSTOMERS. This constant is helpful for the BE request.

```js
export enum KALTURA_ASSET_TYPE {
  MEDIA = 'media',
  EPG = 'epg',
  RECORDING = 'recording'
}

``` 

- **KALTURA_ LIVE_ STREAMING_TYPE** <br>
  ONLY FOR THE KALTURA BE CUSTOMERS. This constant is helpful for the BE request.

```js
export enum KALTURA_LIVE_STREAMING_TYPE {
  CATCHUP = 'catchup',
  START_OVER = 'startOver',
  TRICK_PLAY = 'trickPlay'
}
```

- **KALTURA_ ASSET_ REFERENCE_TYPE** <br>
  ONLY FOR THE KALTURA BE CUSTOMERS. This constant is helpful for the BE request.

```js
export enum KALTURA_ASSET_REFERENCE_TYPE {
  MEDIA = 'media',
  INTERNAL_EPG = 'epg_internal',
  EXTERNAL_EPG = 'epg_external',
  NPVR = 'npvr'
}

```

- **PLAYER_ RESIZE_ MODES** <br>
  Resize modes for Player view component. FE can pass this config on [`PlayerInitOptions`](./player-apis.md)
  This changes the aspect ratio of the native Player view.

```js
export enum PLAYER_RESIZE_MODES {
  FIT = 'fit',
  FIXED_WIDTH = 'fixedWidth',
  FIXED_HEIGHT = 'fixedHeight',
  FILL = 'fill',
  ZOOM = 'zoom'
}
```

- **WAKEMODE** <br>
  Sets whether the player should not handle wakeLock or should handle a wake lock only or both wakeLock & wifiLock when the screen is off.
  default - NONE - not handling wake lock
  FE can pass this config on [`PlayerInitOptions`](./player-apis.md)

```js
export enum WAKEMODE {
  NONE = 'NONE',
  LOCAL = 'LOCAL',
  NETWORK = 'NETWORK'
}
```

- **SUBTITLE_STYLE** <br>
  This is helpful to set/update the Subtitles' styling and positioning to the player. FE can pass this config on [`PlayerInitOptions`](./player-apis.md)

```js
export enum SUBTITLE_STYLE {
  EDGE_TYPE_NONE = 'EDGE_TYPE_NONE',
  EDGE_TYPE_OUTLINE = 'EDGE_TYPE_OUTLINE',
  EDGE_TYPE_DROP_SHADOW = 'EDGE_TYPE_DROP_SHADOW',
  EDGE_TYPE_RAISED = 'EDGE_TYPE_RAISED',
  EDGE_TYPE_DEPRESSED = 'EDGE_TYPE_DEPRESSED',
  FRACTION_50 = 'SUBTITLE_FRACTION_50',
  FRACTION_75 = 'SUBTITLE_FRACTION_75',
  FRACTION_100 = 'SUBTITLE_FRACTION_100',
  FRACTION_125 = 'SUBTITLE_FRACTION_125',
  FRACTION_150 = 'SUBTITLE_FRACTION_150',
  FRACTION_200 = 'SUBTITLE_FRACTION_200',
  TYPEFACE_DEFAULT = 'DEFAULT',
  TYPEFACE_DEFAULT_BOLD = 'DEFAULT_BOLD',
  TYPEFACE_MONOSPACE = 'MONOSPACE',
  TYPEFACE_SERIF = 'SERIF',
  TYPEFACE_SANS_SERIF = 'SANS_SERIF',
  TYPEFACE_STYLE_NORMAL = 'NORMAL',
  TYPEFACE_STYLE_BOLD = 'BOLD',
  TYPEFACE_STYLE_ITALIC = 'ITALIC',
  TYPEFACE_STYLE_BOLD_ITALIC = 'BOLD_ITALIC',
  HORIZONTAL_ALIGNMENT_NORMAL = 'ALIGN_NORMAL',
  HORIZONTAL_ALIGNMENT_CENTER = 'ALIGN_CENTER',
  HORIZONTAL_ALIGNMENT_OPPOSITE = 'ALIGN_OPPOSITE'
}
```

- **SUBTITLE_PREFERENCE** <br>
  If the manifest contains the text track (subtitles) and FE is passing the subtitles from outside then FE can use this preference to tell Player in [`PlayerInitOptions`](./player-apis.md), which one to use.

```js 
export enum SUBTITLE_PREFERENCE {
  OFF = 'OFF',
  INTERNAL = 'INTERNAL',
  EXTERNAL = 'EXTERNAL'
}

```

- **VIDEO_CODEC** <br>
  It can be used on `videoCodecSettings` in [`PlayerInitOptions`](./player-apis.md).

```js 
export enum VIDEO_CODEC {
  HEVC = 'HEVC',
  AV1 = 'AV1',
  VP9 = 'VP9',
  VP8 = 'VP8',
  AVC = 'AVC'
}
```

- **AUDIO_CODEC** <br>
  It can be used on `audioCodecSettings` in [`PlayerInitOptions`](./player-apis.md).

```js 
export enum AUDIO_CODEC {
  AAC = 'AAC',
  AC3 = 'AC3',
  E_AC3 = 'E_AC3',
  OPUS = 'OPUS'
}

```

- **VR_INTERACTION_MODE** <br>
  In case for the VR/360 media, this can be used as a part of `vrSettings` in [`PlayerInitOptions`](./player-apis.md).

```js 
export enum VR_INTERACTION_MODE {
  MOTION = 'Motion',
  TOUCH = 'Touch',
  MOTION_WITH_TOUCH = 'MotionWithTouch',
  CARD_BOARD_MOTION = 'CardboardMotion',
  CARD_BOARD_MOTION_WITH_TOUCH = 'CardboardMotionWithTouch'
}
```

- **TRACK_SELECTION_MODE** <br>
  This can be used for `trackSelection` in [`PlayerInitOptions`](./player-apis.md). It's part of `PKTrackConfig.Mode` inside `trackSelection`.

```js 
export enum TRACK_SELECTION_MODE {
  OFF = 'OFF',
  AUTO = 'AUTO',
  SELECTION = 'SELECTION'
}
```

- **MULTICAST_EXTRACTOR_MODE** <br>

```js
export enum MULTICAST_EXTRACTOR_MODE {
  MODE_MULTI_PMT = 'MODE_MULTI_PMT',
  MODE_SINGLE_PMT = 'MODE_SINGLE_PMT',
  MODE_HLS = 'MODE_HLS'
}
```

- **IMA_AD_TAG_TYPE** <br>

```js
export enum IMA_AD_TAG_TYPE {
  VMAP = "VMAP",
  VAST = "VAST"
}
```

- **IMADAI_STREAM_FORMAT** <br>

```js
export enum IMADAI_STREAM_FORMAT {
  DASH = "DASH",
  HLS = "HLS"
}
```

- **LOG_LEVEL**

```js
export enum LOG_LEVEL {
  VERBOSE = "VERBOSE",
  DEBUG = "DEBUG",
  WARN = "WARN",
  INFO = "INFO",
  ERROR = "ERROR",
  OFF = "OFF"
}
```