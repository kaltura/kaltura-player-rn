## Player Plugins' setup

We have plugins for Ad playback and analytics. This does not need any extra library. It is a part of `PlayerInitOptions` which is passed while settings up the Player.

To setup any plugin pass plugin config under `plugins` in the `PlayerInitOptions` json.

```js
{
	plugins: {
	// Plugin Config
	}
}
```

- **IMA/IMADAI Ads Configuration**

  This configuration can we done when App wants to play the ads along with the content. Our Ad plugin is built on top of Google IMA SDK. 

***TODO***