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

### Adding KalturaPlayer component

App can get the KalturaPlayer component reference in the following way,

```js
import { KalturaPlayerAPI } from 'react-native-kaltura-player';

```

Now this `KalturaPlayerAPI` reference can be used to call the methods.

### Other Important imports

App can use import the `PlayerEvents`, `AdEvents` and `AnalyticsEvents` to listen.

```js
import {
  PlayerEvents,
  AdEvents,
  AnalyticsEvents,
} from 'react-native-kaltura-player';

```

App should use the library defined constants only while passing the configuration params in the JSON.

```js
import {
  PLAYER_TYPE
} from 'react-native-kaltura-player';

```