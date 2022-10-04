![ReleaseType](https://img.shields.io/badge/Release%20Type-Alpha-blue)
![Platform](https://img.shields.io/badge/Platform-React--Native%20Video%20Player-green)


## Kaltura Player React Native

Video Player library for react-native. Library is developed on [Kaltura Player](https://developer.kaltura.com/player/).
It has a video component to render the player and supports various Player APIs.
Along with this, Player has the capability to play the Ads, VR media etc. Player has the capability to use prominent analytics
like Youbora.

## Installation

```sh
npm install kaltura-player-rn
```

> **npm** : https://www.npmjs.com/package/kaltura-player-rn 

## Usage

```js
import { KalturaPlayer } from "kaltura-player-rn";

 <KalturaPlayer style={styles.center} ></KalturaPlayer>

 const styles = StyleSheet.create({
  center: {
    flex: 1,
    padding: 100,
    height: 300,
    alignItems: 'center',
  },
});       
```

### Adding KalturaPlayer API reference

App can get the KalturaPlayerAPI reference in the following way,

```js
import { KalturaPlayerAPI } from 'kaltura-player-rn';

```

Now this `KalturaPlayerAPI` reference can be used to call the methods.

### Other Important imports

App can use import the `PlayerEvents`, `AdEvents` and `AnalyticsEvents` to listen.

```js
import {
  PlayerEvents,
  AdEvents,
  AnalyticsEvents,
} from 'kaltura-player-rn';

```

App should use the library defined constants only while passing the configuration params in the JSON.
[Check Constants](./docs/player-functions.md#constants)

```js
import {
  PLAYER_TYPE
} from 'kaltura-player-rn';

```

## Development Resources

- [Player Functions](./docs/player-functions.md)
- [Player InitOptions or Settings](./docs/player-initoptions.md)
- [Player Plugins](./docs/player-plugins.md)
- [Events](./docs/events.md)
- [Setup Development Environment](./docs/setup-dev-environment.md)
- [FAQ](./docs/faq.md)


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
