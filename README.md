# react-native-kaltura-player

React Native library for Kaltura-Player [iOS and Android]

### Installation

```sh
npm install react-native-kaltura-player
```

### Usage

```js
import { KalturaPlayer } from "react-native-kaltura-player";

 <KalturaPlayer
      ref={(ref: KalturaPlayer) => {
         this.player = ref;
      }}
      style={styles.center}
      playerType={PLAYER_TYPE.OTT}
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

### Configurable Props

##### `playerType` 

import { PLAYER_TYPE } from "react-native-kaltura-player";

`PLAYER_TYPE.BASIC`

This is for non-Kaltura users. This type is used when you want to give a playback url and DRM license from outside.

`PLAYER_TYPE.OTT`

This is for Kaltura OTT customers. They can pass the mediaId, ks (Kaltura Secret) and server url. Internally, we will fetch the media details like playback url and DRM license from our backend.

`PLAYER_TYPE.OVP`

This is for Kaltura OVP customers. They can pass the entryId, ks (Kaltura Secret) and server url. Internally, we will fetch the media details like playback url and DRM license from our backend.

##### `style`

Style can be passed to the `KalturaPlayer` component using stylesheet.

### Player Setup

#### Adding KalturaPlayer component

App can get the KalturaPlayer component reference in the following way,

```js
export default class App extends React.Component<any, any> {
player: KalturaPlayer;
..
..
render() {
    return (
<KalturaPlayer
     ref={(ref: KalturaPlayer) => {
          this.player = ref;
     }}
     ...
></KalturaPlayer>
)
}
}

```

Now this KalturaPlayer reference can be used to call the methods

#### Player Setup

`setup = (options: string, id: number = 0)`

This method creates a Player instance internally (Basic, OVP/OTT Player)
With this, it take the PlayerInitOptions which are having essential Player settings values. 

`options` : `PlayerInitOptions` JSON String (We will talk about it later in the document)

 `id` : PartnerId (Don't pass this parameter for BasicPlayer. For OVP/OTT player this value should be always greater than 0 and should be valid otherwise, we will not be able to featch the details for the mediaId or the entryId)

#### Adding and removing the Player Listerners

`addListeners()`

Add the listners for the Kaltura Player. This will enable the Apps to listen to the Kaltura Player events, Ad events and Analytics events.

`removeListeners()`

Remove the added Player listeners.

#### Load the Player 

`loadMedia = (id: string, asset: string)`

Load the media with the given **assetId** OR **mediaId** OR **entryID** for OVP/OTT Kaltura Player and **playbackURL** for Basic Kaltura Player.

`id` : Playback URL for Kaltura Basic Player OR MediaId for Kaltura OTT Player OR EntryId for Kaltura OVP Player

`asset` : Media Asset JSON String

#### Handle background and foreground Application behaviour

An important step for mobile apps where user can move to another app. So to handle this app can call the following methods,

`onApplicationPaused`: Call when the app goes to background, ideally `onPause` as per the Android lifecycle.

`onApplicationResumed`: Call when the app comes to foreground, ideally `onResume` as per the Android lifecycle.

#### Plugin Setup


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
