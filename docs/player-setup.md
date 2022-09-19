## Player Setup

### Setup API

`setup = async (playerType: PLAYER_TYPE, options: string, id: number = 0)`

This method creates a Player instance internally (Basic, OVP/OTT Player)
With this, it take the PlayerInitOptions which are having essential Player settings values. 

`playerType`: The Player Type, Basic/OVP/OTT. [Check Constants](#Constants)

`options` : `PlayerInitOptions` JSON String (We will talk about it later in the document)

 `id` : PartnerId (Don't pass this parameter for BasicPlayer. For OVP/OTT player this value should be always greater than 0 and should be valid otherwise, we will not be able to featch the details for the mediaId or the entryId)

### Adding and removing the Player Listerners

`addListeners()`

Add the listners for the Kaltura Player. This will enable the Apps to listen to the Kaltura Player events, Ad events and Analytics events.

`removeListeners()`

Remove the added Player listeners.

### Load the Player 

`loadMedia = async (id: string, asset: string)`

Load the media with the given **assetId** OR **mediaId** OR **entryID** for OVP/OTT Kaltura Player and **playbackURL** for Basic Kaltura Player.

`id` : Playback URL for Kaltura Basic Player OR MediaId for Kaltura OTT Player OR EntryId for Kaltura OVP Player

`asset` : Media Asset JSON String

### Handle background and foreground Application behaviour

An important step for mobile apps where user can move to another app. So to handle this app can call the following methods,

`onApplicationPaused()`: Call when the app goes to background, ideally `onPause()` as per the Android lifecycle.

`onApplicationResumed()`: Call when the app comes to foreground, ideally `onResume()` as per the Android lifecycle.

### Add or remove the Player View component (Native Level)

By default Player view component is attached to the Player so no need to call `addPlayerView` explicitely.

`addPlayerView()`: Adds the Native Player View to the Player if not attached.
`removePlayerView`: Removes the Native Player View from the Player if it is attached.

### Constants

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
