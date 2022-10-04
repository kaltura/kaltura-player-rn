## Frequently Asked Questions

> What is `kaltura-player-rn` ?

Kaltura Player react native bridge is a npm package designed on top of iOS and Android Native Kaltura Player SDK. Here are [Kaltura Player iOS](https://github.com/kaltura/kaltura-player-ios) [Kaltura Player Android](https://github.com/kaltura/kaltura-player-android) links.

> What Kaltura Player Native SDK use ?

Native Kaltura Player uses ExoPlayer and AVPlayer for Android & iOS repectively.

> What `kaltura-player-rn` exposing to react-native FE apps ?

`kaltura-player-rn` is exposing a player view <KalturaPlayer> and a Player instance using what FE can call player functions directly. 
Please check [here](../README.md) to know more.

> I am new to react-native what knowledge do i need to test my media or develop an app ?

Feel free to check out [`example`](https://github.com/kaltura/kaltura-player-rn/tree/develop/example). It is a test app. Under **src -> test -> PlayerTest.json**, you can add your media.
To test you media, please add you media under 'Basic'. You can add your media like the way it is there.

> What is Basic, OVP and OTT Player in `kaltura-player-rn` ?

**Basic**: For Non-Kaltura customers. From player setup point of view there is no change. If you want to test your media then in **src -> test -> PlayerTest.json** you can add and test your media. You just need to add your media URL there.

**OVP / OTT** : This is for Kaltura BE customers. The difference here is while calling `setup` on `KalturaPlayerAPI`, they need to pass `partnerId` and `serverUrl ` on `options`. And while calling `loadMedia` they might want to pass `ks` which is kaltura secret to authenticate the valid requests.

> Does Player support Advertisement Playback ?

Yes. Please check our [Plugins doc](./player-plugins.md).

> Does Player support Chromecast ?

No for the time, it is in our roadmap.

> Does Player support Offline download ?

No for the time, it is in our roadmap.

> Can we control the `KalturaPlayer` component on FE app like resizing etc ?

Yes, we are giving the component which FE app can easily control. FE can using `resizeModes` to change the video frame of the player.

> What is the difference `KalturaPlayer` component and video frame ?
`KalturaPlayer` component is the view which renders on the screen. But video frame is the actual media video frame which renders in the `KalturaPlayer` component. Video frame resolution / height / width depends on the media manifest.

> What are the format Player support ?

It supports DASH, HLS, MP3, MP4, UDP (Multicast) media.

> What are the Ad format Player support ?

It supports VAST and VMAP ad tags.

> Does Player support live media playback ?

Yes.

> How to install `kaltura-player-rn` package ?

Simply run `yarn add "kaltura-player-rn"`. It will add `kaltura-player-rn` in `package.json`.

> How to setup `Kaltura Player` ?

Please check our [set up page](./player-functions.md#setup)

> How to load the media ?

Please check our [load the media section](./player-functions.md#load-the-player)

> What is `setup()` and `loadMedia()` ?

`setup()` creates a player instance on the native side and `loadMedia()` actually loads the media to the player.

> How to play the next media or do a change media ?

There is no seperate method to do that calling again `loadMedia()` with new media is just fine.

> How to handle background and foreground lifecycle of a mobile app ?

Please check this [bg/fg lifecycle handling](./player-functions.md#handle-background-and-foreground-application-behaviour) 
