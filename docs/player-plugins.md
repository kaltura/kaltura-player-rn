## Player Plugins

We have plugins for Ad playback, VR and Youbora analytics. (Chromecast and Offline download plugins are in roadmap)

### Supported Plugins

Player supports [these plugins](./player-functions.md#constants#PLAYER_PLUGIN). `Kava` and `OttAnalytics` are part of the package and it is only for the Kaltura BE customers.

### Build setup

Kaltura Player allows the following plugins for Android and iOS. You can get the version from the release notes / Maven or Cocoapods of the relevant plugin.

- **Android**
  - [Ad Plugin (IMA)](https://github.com/kaltura/playkit-android-ima)
  - [Youbora Plugin](https://github.com/kaltura/playkit-android-youbora)
  - [Broadpeak Plugin](https://github.com/kaltura/playkit-android-broadpeak-smartlib/)

- **Kaltura Player iOS contains following Native SDK**
  - [Ad Plugin (IMA)](https://github.com/kaltura/playkit-ios-ima)
  - [Youbora Plugin](https://github.com/kaltura/playkit-ios-youbora)
  - [VR Plugin](https://github.com/kaltura/playkit-ios-vr)


1. For Android
	
	- Open `android` folder of your react-native app in 'Android Studio'.
	- Open `build.gradle` file of the app.
	- In `dependencies` add the following plugin dependencies,

		```gradle
		implementation "com.kaltura.playkit:imaplugin:$latest_version"
    	implementation "com.kaltura.playkit:youboraplugin:$latest_version"
		implementation "com.kaltura.playkit:broadpeakplugin:$latest_version"
		```

	> Note: For Android, VR plugin is not required to be added. It is part of the package itself.
	> Current Android Player version is v4.24.1. It is always recommended to use the same version for plugins like player.

2. For iOS

	🔴 TODO for iOS

	- Open `Podfile` of 'iOS' folder of your react-native app.
	- Add the following to 

		```ruby
		pod 'PlayKit_IMA'
		pod 'PlayKitYoubora'
		pod 'PlayKitVR'
		```

	> Note: For iOS, VR plugin is required to be added.
	

This is it for the setup on the FE app side. To setup any plugin pass plugin config under `plugins` in the `PlayerInitOptions` json.

```js
{
   plugins: {
     pluginName: {
	// Plugin Config
     }
   }
}
```

`pluginName` should be taken from [Constants](./player-functions.md#constants#PLAYER_PLUGIN)


### Plugin's setup

- **IMA/IMADAI Ads Configuration**

  This configuration can we done when App wants to play the ads along with the content. Our Ad plugin is built on top of Google IMA SDK. 

	IMA plugin config. It supports VOD ads.

	```js
	"plugins": {
        "ima": {
            "adTagUrl": "https://kaltura.github.io/playkit-admanager-samples/vast/pod-inline-someskip.xml",
            "alwaysStartWithPreroll": true,
            "enableDebugMode": false
        }
    }
	```

	IMADAI plugin config. It supports DAI Live/VOD ads.

	```js
	"plugins": {
        "imadai": {
            "assetTitle": "VOD - Tears of Steel",
            "contentSourceId": "2528370",
            "videoId": "tears-of-steel",
            "alwaysStartWithPreroll": true
        }
    }

	```

	More config for `IMA` ads,

	```js
	{
	"language": String,
     	"adTagUrl":String,
     	"adTagResponse":String,
    	"adTagType": [AdtagType](./player-functions.md#constants#IMA_AD_TAG_TYPE),
    	"enableBackgroundPlayback": Boolean,
     	"videoBitrate" : Number,
    	"adAttribution": Boolean,
    	"adCountDown": Boolean,
    	"enableDebugMode": Boolean,
   		"alwaysStartWithPreroll": Boolean,
    	"enableFocusSkipButton": Boolean,
    	"enableCustomTabs": Boolean,
     	"adLoadTimeOut": Number,
     	"maxRedirects": Number,
    	"contentDuration" : Float Number,
     	"playerType": String,
     	"playerVersion": String,
     	"sessionId": String,
    	"videoMimeTypes": List_of_String,
	}
	```

	> Note: Currently our package does not support `FriendlyObstruction` and `CompanionAds`.

	More config for `IMADAI` ads,

	```js
	{
		"assetTitle": String,
		"assetKey":String,
		"apiKey":String,
		"contentSourceId": String,
		"videoId" : String,
		"streamFormat": [Stream Format](./player-functions.md#constants#IMADAI_STREAM_FORMAT),
		"licenseUrl": String,
		"adTagParams": {{"key": "value"}},
		"streamActivityMonitorId" : String,
		"authToken": String ,
		"language": String,
		"adCountDown": Boolean,
		"enableDebugMode": Boolean,
		"alwaysStartWithPreroll": Boolean,
		"enableFocusSkipButton": Boolean,
		"enableCustomTabs": Boolean,
		"adLoadTimeOut": Number,
		"maxRedirects": Number,
		"playerType": String,
		"playerVersion": String,
		"sessionId": String
	}
	```

- **Youbora Configuration**

	Following configuration can be passed for the youbora setup,

	```js
	plugins: {
	   pluginName: {
	      params:{
		 // Plugin Config
	      }		
	   }
	}

	```

	> Note: For `Youbora` plugin, under `pluginName`, `params` are mandatory to be passed. Under `params` youbora config should  be passed.

	```js

	"plugins": {
              "youbora": {
                "params": {
                  "accountCode": "kalturatest",
                  "username": "test_rn"
                  }
                }
              }
            }
	```

	Other helpful `Youbora` configs,

	```js
		      "youbora": {
			"params": {
			  "accountCode": "kalturatest",
			  "username": "test_rn",
			  "userEmail": "test_rn@mobile.com",
			  "userAnonymousId": "user_anonymous_Id",
			  "userType": "user_type",
			  "houseHoldId": "zxzxz",
			  "userObfuscateIp": true,
			  "httpSecure": true,
			  "transportFormat": "transportFormat",
			  "urlToParse": "urlToParse",
			  "linkedViewId": "linkedViewId",
			  "isAutoStart": true,
			  "isAutoDetectBackground": true,
			  "isEnabled": true,
			  "isForceInit": true,
			  "isOffline": false,
			  "haltOnError": false,
			  "enableAnalytics": true,
			  "enableSmartAds": true,
			  "content": {
			  "contentBitrate": 640000,
			  "contentCdn": "a",
			  "contentCdnNode": "b",
			  "contentCdnType": "c",
			  "contentChannel": "d",
			  "contentContractedResolution": "720p",
			  "contentCost": "122",
			  "contentDrm": "e",
			  "contentDuration": 1200000,
			  "contentEncodingAudioCodec": "ec-3",
			  "contentEncodingCodecProfile": "f",
			  "contentEncodingContainerFormat": "g",
			  "contentEncodingVideoCodec": "h",
			  "contentEpisodeTitle": "title2",
			  "contentFps": 60,
			  "contentGenre": "drama",
			  "contentGracenoteId": "i",
			  "contentId": "1st_media",
			  "contentImdbId": "j",
			  "contentIsLive": false,
			  "contentIsLiveNoSeek": false,
			  "contentLanguage": "en",
			  "contentPackage": "aaa",
			  "contentPlaybackType": "bbb",
			  "contentPrice": 10000,
			  "contentProgram": "program",
			  "contentRendition": "22223",
			  "contentSaga": "ccc",
			  "contentSeason": "ddd",
			  "contentStreamingProtocol": "applehttp",
			  "contentSubtitles": "en",
			  "contentThroughput": 1230000,
			  "contentTitle": "title",
			  "contentTransactionCode": "dssd",
			  "contentTotalBytes": 123344,
			  "contentSendTotalBytes": false,
			  "contentTvShow": "sadsa",
			  "contentType": "drama"
			  }
			}
		      }
	```

- **Broadpeak Configuration**

	```js

	        "plugins": {
              "broadpeak": {
                "analyticsAddress": String,
                "nanoCDNHost": String,
                "broadpeakDomainNames": String, // "*" for everything
                "uuid": String,
				"deviceType": String,
				"userAgent": String,
				"nanoCDNResolvingRetryDelay": 10000, // In Milliseconds
				"nanoCDNHttpsEnabled": Boolean,
				"adCustomReference": String,
				"adParameters": json with <String String> Key/Value pairs,
				"customParameters": json with <String String> Key/Value pairs,
				"options": json with <Number Object> Key/Value pairs,
              }
            }


	```

	More info about Broadpeak Plugin android integration in Android side can be found [here](https://github.com/kaltura/playkit-android-broadpeak-smartlib/#kaltura-plugin-for-broadpeak-smartlib-for-kaltura-player-on-android).

	> 🔴 Currently not available in iOS


### Update Plugin Config

Updating the plugin config is handy while changing to another media. Suppose FE wants to play another 'Ad' for the next media then FE can pass another config with new Ad to the player.

App can call the following with the updated plugin's config,

```js
	const updatedIMAPlugin = {
		   "ima": {
			"adTagUrl": "https://kaltura.github.io/playkit-admanager-samples/vast/pod-inline-someskip.xml",
			"alwaysStartWithPreroll": true,
			"enableDebugMode": false
		   }
		  };

	KalturaPlayerAPI.updatePluginConfigs(JSON.stringify(updatedIMAPlugin));
```
