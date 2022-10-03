## Installation

```sh
npm install kaltura-player-rn
```

## Usage

```js
import { KalturaPlayer } from "kaltura-player-rn";

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
[Check Constants](./player-functions.md#constants)

```js
import {
  PLAYER_TYPE
} from 'kaltura-player-rn';

```
