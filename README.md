# react-native-kaltura-player

React Native library for Kaltura-Player [iOS and Android]

## Installation

```sh
npm install react-native-kaltura-player
```

## Usage

```js
import { KalturaPlayer } from "react-native-kaltura-player";

 <KalturaPlayer
 		style={styles.center}
 ></KalturaPlayer>

 const styles = StyleSheet.create({
  center: {
    flex: 1,
    padding: 100,
    height: 300,
    alignItems: 'center',
  },
});       
```

## Player Setup

### Adding KalturaPlayer component

App can get the KalturaPlayer component reference in the following way,

```js
import { KalturaPlayerAPI } from 'react-native-kaltura-player';

```

Now this KalturaPlayer reference can be used to call the methods

### Setup

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

`onApplicationPaused`: Call when the app goes to background, ideally `onPause` as per the Android lifecycle.

`onApplicationResumed`: Call when the app comes to foreground, ideally `onResume` as per the Android lifecycle.

### Constants

There are various constants available which are essential to be used while setting up the Player configs.

- `PLAYER_TYPE`: OVP, OTT, BASIC 
- `	MEDIA_FORMAT`: DASH, HLS, WVM, MP4, MP3, UDP
- `MEDIA_ENTRY_TYPE`: VOD, LIVE, DVRLIVE
- `DRM_SCHEME`: WIDEVINE_CENC, PLAYREADY_CENC, WIDEVINE_CLASSIC, PLAYREADY_CLASSIC
- `PLAYER_PLUGIN`: IMA, IMADAI, YOUBORA, KAVA, OTT_ANALYTICS, BROADPEAK

More constants can be found [here](https://github.com/kaltura/kaltura-player-rn/tree/rn-kaltura-player-with-methods/src).

## Player Configurations

`PlayerInitOptions` includes the important parameters required for the Player setup. Following is an example for OVP player type.

### Kaltura BE configs

> Required only for OTT/OVP Customers. Server url and ks will be provided by Kaltura BE.

```js
var initOptions = {
  serverUrl: 'server_url'
  ks: 'kaltura_secret'
}
```

### Network Settings

```js
var initOptions = {
requestConfig: {
    crossProtocolRedirectEnabled: true,
    readTimeoutMs: 8000,
    connectTimeoutMs: 8000,
  }
}
```
`crossProtocolRedirectEnabled` : Default is `false`. Set if network request are going from http to https OR https to http servers.

`readTimeoutMs` : Read time out for the network requests. Default is 8000ms.

`connectTimeoutMs` : Connection time out for the network requests. Default is 8000ms.


### ABR (Adaptive Bitrate) Settings

```js
var initOptions = {
abrSettings: {
    minVideoBitrate: 600000,
    maxVideoBitrate: 1500000,
  }
}
```

`initialBitrateEstimate` : Sets the initial bitrate estimate in bits per second that should be assumed when a bandwidth estimate is unavailable. To reset it, set it to null. Initial bitrate is only meant at the start of the playback

`minVideoBitrate` : Set minVideoBitrate in ABR

`maxVideoBitrate` : Set maxVideoBitrate in ABR

`maxVideoHeight` : Set maxVideoHeight in ABR

`minVideoHeight` : Set minVideoHeight in ABR

`maxVideoWidth` : Set maxVideoWidth in ABR

`minVideoWidth` : Set minVideoWidth in ABR
 
### Player Resize Modes

```js
import { PLAYER_RESIZE_MODES } from "react-native-kaltura-player";
```

```js
var initOptions = {
aspectRatioResizeMode: PLAYER_RESIZE_MODES.FIT
}
```
There are other resize modes available like **FIXED_WIDTH**, **FIXED_HEIGHT**, **FILL** and **ZOOM**


### Set MediaFormat

```js
import { MEDIA_FORMAT } from "react-native-kaltura-player";
```

```js
var initOptions = {
	mediaFormat: MEDIA_FORMAT.DASH
}
```

There are other media format present **HLS**, **WVM**, **MP4**, **MP3**, **UDP**.
This setting is mostly important to pass for all 3 types of Players.

### Subtitle Styling and Positioning

App can change the subtitltes' text color, background color etc along with the subtitle's position on the view.

Following subtitle Configuration can be used,

```js
import { SUBTITLE_STYLE } from "react-native-kaltura-player";
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
import { SUBTITLE_PREFERENCE } from "react-native-kaltura-player";
```

```js
var initOptions = {
   subtitlePreference: SUBTITLE_PREFERENCE.EXTERNAL,
}
```

### WakeLock mode Preference

Set WakeLock Mode  - Sets whether the player should not handle wakeLock or should handle a wake lock only or both wakeLock & wifiLock when the screen is off

```js
import { WAKEMODE } from "react-native-kaltura-player";
```

```js
var initOptions = {
   wakeMode: WAKEMODE.NETWORK,
}
```

### More APIs

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


## Player Plugins' setup

We have plugins for Ad playback and analytics. This does not need any extra library. It is a part of `PlayerInitOptions` which is passed while settings up the Player.

- **IMA/IMADAI Ads Configuration**

  This configuration can we done when App wants to play the ads along with the content. Our Ad plugin is built on top of Google IMA SDK. 


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
