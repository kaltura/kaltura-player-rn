import React, { useEffect } from "react";
import {
  View,
  Text,
  NativeModules,
  NativeEventEmitter,
  Button,
} from "react-native";

const { KalturaPlayerModule } = NativeModules;

const KalturaPlayer = () => {
  console.log(KalturaPlayerModule);
  const onPress = () => {
    const url =
      "https://cdnapisec.kaltura.com/p/2215841/sp/221584100/playManifest/entryId/1_w9zx2eti/protocol/https/format/applehttp/falvorIds/1_1obpcggb,1_yyuvftfz,1_1xdbzoa6,1_k16ccgto,1_djdf6bk8/a.m3u8";
    //KalturaPlayerModule.createKalturaPlayerEvent(url, "BBB");
    KalturaPlayerModule.setup(OttPartnerId, JSON.stringify(initOptions));
  };

  useEffect(() => {
    const playerEventEmitter = new NativeEventEmitter(KalturaPlayerModule);
    const eventReminderListener = playerEventEmitter.addListener(
      "EventReminder",
      (event) => {
        console.log("EventReminder " + event.eventProperty1 + " " +  event.eventProperty2);
      }
    );

    const playerInitializedListener = playerEventEmitter.addListener(
      "playerInitialized",
      (event) => {
        console.log("playerInitializedListener");
        KalturaPlayerModule.load(OttMediaId, JSON.stringify(asset));
      }
    );

    const loadMediaSuccessListener = playerEventEmitter.addListener(
      "loadMediaSuccess",
      (event) => {
        console.log("loadMediaSuccess " + JSON.stringify(event.payload));
      }
    );

    
    return () => {
      eventReminderListener.remove();
      playerInitializedListener.remove();
      loadMediaSuccessListener.remove();
    };
  }, []);

  return (
    <View>
      <Text>Player</Text>
      <Button onPress={onPress} title="Kaltura Player event" />
    </View>
  );
};

//OTT 3009
const PhoenixBaseUrl = "https://rest-us.ott.kaltura.com/v4_5/api_v3/";
const OttPartnerId = 3009;
const OttMediaId = "548576";
const OttMediaFormat = "Mobile_Main";

const OttMediaProtocol = "http"; // "https"
const OttAssetType = "media";
const OttPlaybackContextType = "playback";

var initOptions = {
  "serverUrl": PhoenixBaseUrl,
  "autoplay": true,
  "preload": true,
  "requestConfig": {
    "crossProtocolRedirectEnabled": true,
    "readTimeoutMs": 8000,
    "connectTimeoutMs": 8000
  },
  "allowCrossProtocolRedirect": true,
  "allowFairPlayOnExternalScreens" : true,
  "shouldPlayImmediately": true,
  "networkSettings": {
     "autoBuffer": true,
     "preferredForwardBufferDuration": 30000,
     "automaticallyWaitsToMinimizeStalling": true
  },
  "multicastSettings": {
    "useExoDefaultSettings": true,
    "maxPacketSize": 3000,
    "socketTimeoutMillis": 10000,
    "extractorMode": "MODE_MULTI_PMT",
    "firstSampleTimestampUs": 0
  },
  "mediaEntryCacheConfig": {
    "allowMediaEntryCaching": true,
    "maxMediaEntryCacheSize": 15,
    "timeoutMs": 3600000
  },
  "abrSettings": {
    "minVideoBitrate": 600000,
    "maxVideoBitrate": 1500000
  },
  "trackSelection": {
    "textMode": "AUTO",
    "textLanguage": "en",
    "audioMode": "AUTO",
    "audioLanguage": "en",
  },
  "handleAudioFocus": true,
  "plugins": {
    "ima": {},
    "youbora": {
      "accountCode": "yourCode",
      "username": "your_app_logged_in_user_email_or_userId",
      "userEmail": "user_email",
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
        "contentId": "22222",
        "contentImdbId": "j",
        "contentIsLive": false,
        "contentIsLiveNoSeek": false,
        "contentLanguage": "en",
        "contentPackage": "aaa",
        "contentPlaybackType": "bbb",
        "contentPrice": 10000,
        "contentProgram": "program",
        "contentRendition": "22223",
        "contentResource": "http:/ssss.m3u8",
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
      },
      "app": {
        "appName": "MyTestApp",
        "appReleaseVersion": "v1.0"
      },
      "parse": {
        "parseManifest": true,
        "parseCdnNode": true,
        "parseCdnSwitchHeader": true,
        "cdnNodeList": [
          "Akamai",
          "Cloudfront",
          "Level3",
          "Fastly",
          "Highwinds"
        ],
        "cdnNameHeaders": "x-cdn-forward",
        "parseCdnTTL": 60
      },
      "network": {
        "networkIP": "1.1.1.1",
        "networkConnectionType": "cellular",
        "networkIsp": "orange"
      },
      "device": {
        "deviceBrand": "deviceBrand",
        "deviceCode": "deviceCode",
        "deviceId": "deviceId",
        "deviceModel": "deviceModel",
        "deviceOsName": "deviceOsName",
        "deviceOsVersion": "deviceOsVersion",
        "deviceType": "deviceType",
        "deviceIsAnonymous": false
      },
      "errors": {
        "errorsIgnore": [
          "exception1",
          "exception2"
        ],
        "errorsFatal": [
          "exception3",
          "exception4"
        ],
        "errorsNonFatal": [
          "exception5",
          "exception6"
        ]
      },
      "ads": {
        "adBreaksTime": [
          0,
          15,
          60
        ],
        "adCampaign": "AdCmap",
        "adCreativeId": "adCreativeId",
        "adExpectedBreaks": 3,
        "adGivenAds": 5,
        "adGivenBreaks": 3,
        "adProvider": "adProvider",
        "adResource": "adResource",
        "adTitle": "adTitle",
        "adCustomDimensions": {
          "adCustomDimension1": "adCustomDimension1",
          "adCustomDimension2": "adCustomDimension2",
          "adCustomDimension3": "adCustomDimension3",
          "adCustomDimension4": "adCustomDimension4",
          "adCustomDimension5": "adCustomDimension5"
        }
      },
      "properties": {
        "year": "your_year",
        "cast": "your_cast",
        "director": "your_director",
        "owner": "your_owner",
        "parental": "your_parental",
        "rating": "your_rating",
        "audioChannels": "your_audio_channels",
        "device": "your_device"
      },
      "contentCustomDimensions": {
        "contentCustomDimension1": "customDimension1",
        "contentCustomDimension2": "customDimension2",
        "contentCustomDimension3": "customDimension3",
        "contentCustomDimension4": "customDimension4",
        "contentCustomDimension5": "customDimension5"
      }
    }
  }
}

var asset = {
  "initialVolume" : 1.0,
  "format": OttMediaFormat,
  "assetType": OttAssetType,
  "protocol": OttMediaProtocol,
  "playbackContextType": OttPlaybackContextType,
  "urlType": "PLAYMANIFEST",
  // "urlType": "DIRECT",
  // "ks":"KS",
  // "streamerType": "Mpegdash",
  // "streamerType": "Multicast",

  "startPosition": 0,
  "plugins" : {
    "ima" : {
      "adTagUrl" : "",
      //"adTagUrl" : "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=",
      "alwaysStartWithPreroll" : true,
      "enableDebugMode" : false
    },
    "youbora" : {
      "accountCode": "yourCode",
      "username": "your_app_logged_in_user_email_or_userId",
      "userEmail": "user_email",
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
        "contentId": "22222",
        "contentImdbId": "j",
        "contentIsLive": false,
        "contentIsLiveNoSeek": false,
        "contentLanguage": "en",
        "contentPackage": "aaa",
        "contentPlaybackType": "bbb",
        "contentPrice": 10000,
        "contentProgram": "program",
        "contentRendition": "22223",
        "contentResource": "http:/ssss.m3u8",
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
      },
      "app": {
        "appName": "MyTestApp",
        "appReleaseVersion": "v1.0"
      },
      "parse": {
        "parseManifest": true,
        "parseCdnNode": true,
        "parseCdnSwitchHeader": true,
        "cdnNodeList": [
          "Akamai",
          "Cloudfront",
          "Level3",
          "Fastly",
          "Highwinds"
        ],
        "cdnNameHeaders": "x-cdn-forward",
        "parseCdnTTL": 60
      },
      "network": {
        "networkIP": "1.1.1.1",
        "networkConnectionType": "cellular",
        "networkIsp": "orange"
      },
      "device": {
        "deviceBrand": "deviceBrand",
        "deviceCode": "deviceCode",
        "deviceId": "deviceId",
        "deviceModel": "deviceModel",
        "deviceOsName": "deviceOsName",
        "deviceOsVersion": "deviceOsVersion",
        "deviceType": "deviceType",
        "deviceIsAnonymous": false
      },
      "errors": {
        "errorsIgnore": [
          "exception1",
          "exception2"
        ],
        "errorsFatal": [
          "exception3",
          "exception4"
        ],
        "errorsNonFatal": [
          "exception5",
          "exception6"
        ]
      },
      "ads": {
        "adBreaksTime": [
          0,
          15,
          60
        ],
        "adCampaign": "AdCmap",
        "adCreativeId": "adCreativeId",
        "adExpectedBreaks": 3,
        "adGivenAds": 5,
        "adGivenBreaks": 3,
        "adProvider": "adProvider",
        "adResource": "adResource",
        "adTitle": "adTitle",
        "adCustomDimensions": {
          "adCustomDimension1": "adCustomDimension1",
          "adCustomDimension2": "adCustomDimension2",
          "adCustomDimension3": "adCustomDimension3",
          "adCustomDimension4": "adCustomDimension4",
          "adCustomDimension5": "adCustomDimension5"
        }
      },
      "properties": {
        "year": "your_year",
        "cast": "your_cast",
        "director": "your_director",
        "owner": "your_owner",
        "parental": "your_parental",
        "rating": "your_rating",
        "audioChannels": "your_audio_channels",
        "device": "your_device"
      },
     "contentCustomDimensions": {
        "contentCustomDimension1": "customDimension1",
        "contentCustomDimension2": "customDimension2",
        "contentCustomDimension3": "customDimension3",
        "contentCustomDimension4": "customDimension4",
        "contentCustomDimension5": "customDimension5"
      }
    }
  }
}

export default KalturaPlayer;
