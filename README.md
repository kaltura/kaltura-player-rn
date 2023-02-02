[![npm](https://img.shields.io/npm/v/kaltura-player-rn)](https://www.npmjs.com/package/kaltura-player-rn)
![ReleaseType](https://img.shields.io/badge/Release%20Type-Alpha-blue)
![Platform](https://img.shields.io/badge/Platform-React--Native%20Video%20Player-green)
[![build CI](https://github.com/kaltura/kaltura-player-rn/actions/workflows/build.yml/badge.svg)](https://github.com/kaltura/kaltura-player-rn/actions/workflows/build.yml)
[![build_pull_request CI](https://github.com/kaltura/kaltura-player-rn/actions/workflows/build_pull_request.yml/badge.svg)](https://github.com/kaltura/kaltura-player-rn/actions/workflows/build_pull_request.yml)

## Kaltura Player React Native

Video Player library for react-native. Library is developed on [Kaltura Player](https://developer.kaltura.com/player/).
It has a video component to render the player and supports various Player APIs.
Along with this, Player has the capability to play the Ads, VR media etc. Player has the capability to use prominent analytics
like Youbora.

## Installation

```sh
npm install kaltura-player-rn
```
OR 

```sh
yarn add kaltura-player-rn
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

- [Setup Development Environment](./docs/setup-dev-environment.md)
- [Player Functions](./docs/player-functions.md)
- [PlayerInitOptions or PlayerSettings](./docs/player-initoptions.md)
- [Player Plugins](./docs/player-plugins.md)
- [Events](./docs/events.md)
- [FAQ](./docs/faq.md)


## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT
