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
import { PLAYER_RESIZE_MODES } from "kaltura-player-rn";
```

```js
var initOptions = {
aspectRatioResizeMode: PLAYER_RESIZE_MODES.FIT
}
```
There are other resize modes available like **FIXED_WIDTH**, **FIXED_HEIGHT**, **FILL** and **ZOOM**


### Set MediaFormat

```js
import { MEDIA_FORMAT } from "kaltura-player-rn";
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

