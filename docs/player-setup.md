## Player Functions

### Setup
---

`setup = async (playerType: PLAYER_TYPE, options: string, id: number = 0)`

This method creates a Player instance internally (Basic, OVP/OTT Player)
With this, it take the PlayerInitOptions which are having essential Player settings values. 

`playerType`: The Player Type, Basic/OVP/OTT. [Check Constants](#Constants)

`options` : `PlayerInitOptions` JSON String [Check PlayInitOptions](./player-apis.md)

 `id` : PartnerId (Don't pass this parameter for BasicPlayer. For OVP/OTT player this value should be always greater than 0 and should be valid otherwise, we will not be able to featch the details for the mediaId or the entryId)

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

`id` : Playback URL for Kaltura Basic Player OR MediaId for Kaltura OTT Player OR EntryId for Kaltura OVP Player

`asset` : Media Asset JSON String

**For Basic Player**, app can use the following config. Should only be used by non-Kaltura BE users.

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
* `id` *String*  - Random id for this media.
* `name` *String* - Name given to this media.
* `duration`*Number* - Optional. Duration of this media content.
* `mediaEntryType`*String* - [Check Constants](#Constants) for media type. It can be VOD, Live etc.
* `mediaFormat` *String* -  [Check Constants](#Constants) for media format. It can be Dash, HLS etc.
* `startPosition` *Number* - Optional. If you want the player to start from a certain position. Default is 0.
* `isVRMediaType` *Boolean* - Only to be passed while using VR media.
* `drmData` *JSON* - 🔴TODO
* `metadata` *JSON* - 

 ```json
 "other":"other",
  "metaData": {
    "key": "value"
  } 
 ```
* `externalSubtitleList` *JSONArray* - 🔴TODO
* `externalVttThumbnailUrl` - Pass the VTT thumbnail URL. These images will be shown when the user scrubs on the seekbar. To show the images logic should be handled by the FE app. You will recieve the additional information the `ImageTracks` of `TracksAvailable` event. [Check PlayerEvents](./player-event.md)

**For OVP/OTT Player**, app can use the following config. Should only be used by the Kaltura BE users.

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
* `ks` *String* - Kaltra Secret. It will be provided by our BE.
* `format` *JSONArray String* - Formats provided for the playback. Defined in our BE.
* `fileId` *JSONArray String* - FileIds for the playback. Defined in our BE.
* `assetType` *String* - [Check Constants](#Constants) for AssetTypes.
* `playbackContextType` *String* - [Check Constants](#Constants) for PlaybackContextType.
* `assetReferenceType` *String* - [Check Constants](#Constants) for AssetReferenceType.
* `protocol` *String* - `http` or `https`.
* `urlType` *String* - [Check Constants](#Constants) for URLType.
* `streamerType` *String* - [Check Constants](#Constants) for StreamerType.
* `adapterData` *JSONObject* - Additional Adapter data suppied in config for our BE.
  
  ```json
 "other":"other",
  "metaData": {
    "key": "value"
  } 
 ```
* `referrer` *String* - Optional. Referer string.
* `redirectFromEntryId` *Boolean* - Default is `true`. **Only for OVP BE users**
* `useApiCaptions` *Boolean* - Default is `false`. **Only for OVP BE users**
* `startPosition` *Number* - Optional. If you want the player to start from a certain position. Default is 0.
* `initialVolume` *Number* - Default is 1.0f. Change the volume of the current audio track. It's a decimal value. Accept values between 0.0 and 1.0. Where 0.0 is mute and 1.0 is maximum volume. If the volume parameter is higher then 1.0, it will be converted to 1.0. If the volume parameter is lower then 0.0, it be converted to 0.0.

### Handle background and foreground application behaviour
---

An important step for mobile apps where user can move to another app. So to handle this app can call the following methods,

`onApplicationPaused()`: Call when the app goes to background, ideally `onPause()` as per the Android lifecycle.

`onApplicationResumed()`: Call when the app comes to foreground, ideally `onResume()` as per the Android lifecycle.

### Add or remove the Player View component (Native Level)
---

By default Player view component is attached to the Player so no need to call `addPlayerView` explicitely.

`addPlayerView()`: Adds the Native Player View to the Player if not attached.

`removePlayerView`: Removes the Native Player View from the Player if it is attached.

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
|`setAutoPlay(isAutoPlay: boolean)`     | ✅     |  ✅    |   Set the media to play automatically at the start (load). If `false`, user will have to click on UI play button       |
|`setKS(KS: string)`     | ✅     |  ✅    |    Set the KS for the media _(only for OVP/OTT users)_. Call this before calling `loadMedia`. `KS` Kaltura Secret key.      |
|`seekToLiveDefaultPosition()`     | ✅     |  ✅    |    Seek player to Live Default Position. _Only for Live Media._      |
|`updateSubtitleStyle(subtitleStyle: string)()`     | ✅     |  ✅    | Update the existing subtitle styling  |        
|`updateResizeMode(mode: PLAYER_RESIZE_MODES)()`     | ✅     |  ✅    |  Update the Resize Mode  |      
|`updateAbrSettings(abrSettings: string)()`     | ❌     |   ✅   |  Update the ABR Settings  |      
|`resetAbrSettings()`     |  ❌   |   ✅    |    Reset the ABR Settings      |
|`updateLowLatencyConfig(lowLatencyConfig: string)()`     | ❌     |   ✅   |  Update the Low Latency Config. _Only for Live Media_    |    
|`resetLowLatencyConfig()`     |  ❌    |  ✅    |    Reset the Low Latency Config. _Only for Live Media_      |
|`getCurrentPosition()`     | ✅     |  ✅    |    **Async** function. Getter for the current playback position. Returns `string` Position of the player or -1.     |
|`isPlaying()`     | ✅     |  ✅    |   **Async** function. Checks if Player is currently playing or not. Returns `boolean`.     |
|`isLive()`     | ✅     |  ✅    |    **Async** function. Checks if the stream is Live or Not. Returns `boolean`.     |
|`enableDebugLogs()`     | ✅     |  ✅    |   Enable the console logs for the JS bridge. By default it is disabled. Set `true` to enable the logs.       |


### Constants
---

🔴Some Constants are left to add

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


- **MEDIA_FORMAT** Supports Playback formats.

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

- **MEDIA_ ENTRY_TYPE** Type of the media VOD, Live or DvrLive.

```js
export enum MEDIA_ENTRY_TYPE {
  VOD = 'Vod',
  LIVE = 'Live',
  DVRLIVE = 'DvrLive'
}

```

- **DRM_SCHEME** DRM Type WidevineCENC, Fairplay, Playready etc.

```js
export enum DRM_SCHEME {
  WIDEVINE_CENC = 'WidevineCENC',
  PLAYREADY_CENC = 'PlayReadyCENC',
  WIDEVINE_CLASSIC = 'WidevineClassic',
  PLAYREADY_CLASSIC = 'PlayReadyClassic',
  FAIRPLAY = 'FairPlay'
}

```

- **PLAYER_PLUGIN** Player Plugins like IMA for Ads, Youbora for Analytics etc.

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

- **KALTURA_ STREAMER_TYPE** ONLY FOR THE KALTURA BE CUSTOMERS. This constant is helpful for the BE request.

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

- **KALTURA_ URL_TYPE** ONLY FOR THE KALTURA BE CUSTOMERS. This constant is helpful for the BE request.

```js
export enum KALTURA_URL_TYPE {
  PLAYMANIFEST = 'PLAYMANIFEST',
  DIRECT = 'DIRECT'
}

```

- **KALTURA_ PLAYBACK_ CONTEXT_TYPE** ONLY FOR THE KALTURA BE CUSTOMERS. This constant is helpful for the BE request.

```js
export enum KALTURA_PLAYBACK_CONTEXT_TYPE {
  TRAILER = 'TRAILER',
  CATCHUP = 'CATCHUP',
  START_OVER = 'START_OVER',
  PLAYBACK = 'PLAYBACK'
}
```

- **KALTURA_ ASSET_TYPE** ONLY FOR THE KALTURA BE CUSTOMERS. This constant is helpful for the BE request.

```js
export enum KALTURA_ASSET_TYPE {
  MEDIA = 'media',
  EPG = 'epg',
  RECORDING = 'recording'
}

``` 

- **KALTURA_ LIVE_ STREAMING_TYPE** ONLY FOR THE KALTURA BE CUSTOMERS. This constant is helpful for the BE request.

```js
export enum KALTURA_LIVE_STREAMING_TYPE {
  CATCHUP = 'catchup',
  START_OVER = 'startOver',
  TRICK_PLAY = 'trickPlay'
}
```

- **KALTURA_ ASSET_ REFERENCE_TYPE** ONLY FOR THE KALTURA BE CUSTOMERS. This constant is helpful for the BE request.

```js
export enum KALTURA_ASSET_REFERENCE_TYPE {
  MEDIA = 'media',
  INTERNAL_EPG = 'epg_internal',
  EXTERNAL_EPG = 'epg_external',
  NPVR = 'npvr'
}

```
