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

    const playerEventPlayerInitialized = playerEventEmitter.addListener(
      "playerInitialized",
      (event) => {
        console.log("playerInitialized");
        KalturaPlayerModule.load(OttMediaId, JSON.stringify(asset));
      }
    );

    const playerEventLoadMediaSuccess = playerEventEmitter.addListener(
      "loadMediaSuccess",
      (event) => {
        console.log("loadMediaSuccess " + JSON.stringify(event.payload));
      }
    );

    const playerEventLoadMediaFailed  = playerEventEmitter.addListener("loadMediaFailed", payload => {
      console.log("*** loadMediaFailed PlayerEvent: " + JSON.stringify(payload));
      // {
      //   "code": "500007",
      //   "extra": "KalturaAPIException", //Android only
      //   "message": "Asset not found",
      //   "name": "OTTError" //Android only
      // }
      }
    );

    const playerEventEnded = playerEventEmitter.addListener("ended", payload => {
      console.log("*** ended PlayerEvent: " + JSON.stringify(payload));
     }
    );

    const playerEventCanPlay = playerEventEmitter.addListener("canPlay", payload => {
      console.log("*** canPlay PlayerEvent");
     }
    );

    const playerEventPlaying = playerEventEmitter.addListener("playing", payload => {
      console.log("*** playing PlayerEvent: " + JSON.stringify(payload));
    }
    );

    const playerEventPlay = playerEventEmitter.addListener("play", payload => {
      console.log("*** play PlayerEvent: " + JSON.stringify(payload));
     }
    );

    const playerEventPause = playerEventEmitter.addListener("pause", payload => {
      console.log("*** pause PlayerEvent: " + JSON.stringify(payload));
      }
    );

    const playerEventStopped = playerEventEmitter.addListener("stopped", payload => {
      console.log("*** stopped PlayerEvent: " + JSON.stringify(payload));
     }
    );

    const playerEventDurationChanged = playerEventEmitter.addListener("durationChanged", payload => {
      console.log("*** durationChanged PlayerEvent: " + JSON.stringify(payload));
      //{ "duration": 156.131 }
      }
    );

    const playerEventTimeUpdateChanged = playerEventEmitter.addListener("timeUpdate", payload => {
      console.log("*** timeUpdate PlayerEvent: " + JSON.stringify(payload));
      //{ "position": 58.542, "bufferPosition": 70.0}
      //{ "position": 58.542, "bufferPosition": 70.0, "currentProgramTime": 4000 } - currentProgramTime is for Live  and when available
     }
    );

    const playerEventLoadedTimeRanges = playerEventEmitter.addListener("loadedTimeRanges", payload => {
      // {
      //   "timeRanges": [
      //     {
      //       "start": 0,
      //       "end": 100.0
      //     }
      //   ]
      // }
      console.log("*** loadedTimeRanges PlayerEvent: " + JSON.stringify(payload));    
     }
    );

    const playerEventStateChanged = playerEventEmitter.addListener("stateChanged", payload => {
      console.log("*** stateChanged PlayerEvent: " + JSON.stringify(payload));
     //{ "newState": "BUFFERING" }
     //{ "newState": "LOADING" }
     //{ "newState": "READY" }
     //{ "newState": "IDLE" }
      }
    );

    const playerEventTacksAvailable = playerEventEmitter.addListener("tracksAvailable", payload => {
      console.log("*** tracksAvailable PlayerEvent: " + JSON.stringify(payload));
      console.log("*** tracksAvailable length: " + Object.keys(payload).length);
      //{"audio":[],"text":[],"video":[{"id":"Video:0,0,-1","height":0,"isAdaptive":true,"isSelected":true,"bitrate":0,"width":0},{"id":"Video:0,0,0","height":360,"isAdaptive":false,"isSelected":false,"bitrate":508450,"width":640},{"id":"Video:0,0,1","height":360,"isAdaptive":false,"isSelected":false,"bitrate":713517,"width":640},{"id":"Video:0,0,2","height":360,"isAdaptive":false,"isSelected":false,"bitrate":966712,"width":640},{"id":"Video:0,0,3","height":720,"isAdaptive":false,"isSelected":false,"bitrate":1343366,"width":1280}]}
      
      //{
      //   "audio": [
      //     {
      //       "bitrate": 67071,
      //       "channelCount": 2,
      //       "id": "Audio:1,0,0",
      //       "isSelected": true,
      //       "language": "Unknown"
      //     }
      //   ],
      //   "image": [
      //     {
      //       "bitrate": 12000,
      //       "isSelected": true,
      //       "id": "Image:3,3,0",
      //       "cols": 10,
      //       "width": 1024,
      //       "duration": 635000,
      //       "url": "http://dash.edgesuite.net/akamai/bbb_30fps/thumbnails_102x58/tile_$Number$.jpg",
      //       "height": 1152,
      //       "label": "thumbnails_102x58",
      //       "rows": 20
      //     },
      //     {
      //       "bitrate": 24000,
      //       "isSelected": false,
      //       "id": "Image:3,3,1",
      //       "cols": 8,
      //       "width": 2048,
      //       "duration": 635000,
      //       "url": "http://dash.edgesuite.net/akamai/bbb_30fps/thumbnails_256x144/tile_$Number$.jpg",
      //       "height": 1152,
      //       "label": "thumbnails_256x144",
      //       "rows": 8
      //     }
      //   ],
      //   "text": [],
      //   "video": [
      //     {
      //       "id": "Video:0,0,-1",
      //       "width": 0,
      //       "height": 0,
      //       "isAdaptive": true,
      //       "isSelected": true,
      //       "bitrate": 0
      //     },
      //     {
      //       "id": "Video:0,0,5",
      //       "width": 480,
      //       "height": 270,
      //       "isAdaptive": false,
      //       "isSelected": false,
      //       "bitrate": 759798
      //     }
      //   ]
      // }
      }
    );

    const playerEventVideoTrackChanged = playerEventEmitter.addListener("videoTrackChanged", payload => {
      console.log("*** videoTrackChanged PlayerEvent: " + JSON.stringify(payload));  
      //{"id":"Video:0,0,-1", "width":0, "height":0, "isAdaptive":true, "isSelected":true, "bitrate":0 }
      }
    );

    const playerEventAudioTrackChanged = playerEventEmitter.addListener("audioTrackChanged", payload => {
      //{"id": "Audio:0,0,-1", "bitrate": 1234, "language": "en", "label": "English", "channelCount": 1, "isSelected": true}
      console.log("*** audioTrackChanged PlayerEvent: " + JSON.stringify(payload));
     }
    );

    const playerEventTextTrackChanged = playerEventEmitter.addListener("textTrackChanged", payload => {
      console.log("*** textTrackChanged PlayerEvent: " + JSON.stringify(payload));
       //{"id": "Text:0,0,-1", "language": "en", "label": "English", "isSelected": true}
      }
    );

    const playerEventImageTrackChanged = playerEventEmitter.addListener("imageTrackChanged", payload => {
      console.log("*** imageTrackChanged PlayerEvent: " + JSON.stringify(payload));
      //{"bitrate":24000,"isSelected":true,"id":"Image:3,3,1","cols":8,"width":2048,"duration":635000,"url":"http://dash.edgesuite.net/akamai/bbb_30fps/thumbnails_256x144/tile_$Number$.jpg","height":1152,"label":"thumbnails_256x144","rows":8} 
      }
    );

    const playerEvenThumbnailInfoResponse = playerEventEmitter.addListener("thumbnailInfoResponse", payload => {
      console.log("*** thumbnailInfoResponse PlayerEvent: " + JSON.stringify(payload));
      //{"position":12500,"thumbnailInfo":{"height":57.6,"x":307.2,"url":"http://dash.edgesuite.net/akamai/bbb_30fps/thumbnails_102x58/tile_1.jpg","width":102.4,"y":0}}
      //{"position":12500,"thumbnailInfo":{"height":144,"x":256,"url":"http://dash.edgesuite.net/akamai/bbb_30fps/thumbnails_256x144/tile_1.jpg","width":256,"y":0}}
     }
    );

    const playerEventPlaybackInfoUpdated = playerEventEmitter.addListener("playbackInfoUpdated", payload => {
      console.log("*** playbackInfoUpdated PlayerEvent: " + JSON.stringify(payload));
      //iOS     { "totalBitrate":  100000 }
      //Android { "totalBitrate":  100000, "videoBitrate": 400000,  "audioBitrate": 65000 }
     }
   );

   const playerEventSeeking = playerEventEmitter.addListener("seeking", payload => {
      console.log("*** seeking PlayerEvent: " + JSON.stringify(payload));
      //{ "targetPosition":  100 }
      }
    );

    const playerEventSeeked = playerEventEmitter.addListener("seeked", payload => {
      console.log("*** seeked PlayerEvent: " + JSON.stringify(payload));  
     }
    );

    const playerEventPlayerError = playerEventEmitter.addListener("error", payload => {
      console.log("*** error PlayerEvent: " + JSON.stringify(payload));
      //{"errorType": "SOURCE_ERROR","errorCode": "7000","errorSeverity": "Fatal","errorMessage": "com.kaltura.android.exoplayer2.upstream.HttpDataSource$HttpDataSourceException: Unable to connect","errorCause":"com.kaltura.android.exoplayer2.upstream.HttpDataSource$HttpDataSourceException: Unable to connect"}
      }
    );

    const playerEventBookmarkError = playerEventEmitter.addListener("bookmarkError", payload => {
      console.log("*** bookmarkError PlayerEvent: " + JSON.stringify(payload));   
     }
    );

    const playerEventConcurrencyErrorError = playerEventEmitter.addListener("concurrencyError", payload => {
      console.log("*** concurrencyError PlayerEvent: " + JSON.stringify(payload));
      }
    );

    const playerEventBroadpeakError = playerEventEmitter.addListener("broadpeakError", payload => {
      console.log("*** broadpeakError PlayerEvent: " + JSON.stringify(payload)); 
      }
    );

    //ADS

    const playerEventAdProgress = playerEventEmitter.addListener("adProgress", payload => {
      console.log("*** adProgress AdEvent: " + JSON.stringify(payload));
      //{ "currentAdPosition": 2 }   
     }
    );

    const playerEventAdCuepointsChanged = playerEventEmitter.addListener("adCuepointsChanged", payload => {
      console.log("*** adCuepointsChanged AdEvent: " + JSON.stringify(payload));
      //{"adPluginName":"ima","cuePoints":[0],"hasPreRoll":true,"hasMidRoll":false,"hasPostRoll":false}   
     }
    );

    const playerEventAdStarted = playerEventEmitter.addListener("adStarted", payload => {
      console.log("*** adStarted AdEvent: " + JSON.stringify(payload)); 
      }
    );

    const playerEventAdCompleted= playerEventEmitter.addListener("adCompleted", payload => {
      console.log("*** adCompleted AdEvent: " + JSON.stringify(payload));   
     }
    );

    const playerEventAdPaused = playerEventEmitter.addListener("adPaused", payload => {
      console.log("*** adPaused AdEvent: " + JSON.stringify(payload));  
      }
    );

    const playerEventAdResumed = playerEventEmitter.addListener("adResumed", payload => {
      console.log("*** adResumed AdEvent: " + JSON.stringify(payload));  
     }
    );

    const playerEventAdBufferStart = playerEventEmitter.addListener("adBufferStart", payload => {
      console.log("*** adBufferStart AdEvent: " + JSON.stringify(payload));  
      }
    );

    const playerEventAdSkipped = playerEventEmitter.addListener("adSkipped", payload => {
      console.log("*** adSkipped AdEvent: " + JSON.stringify(payload));
      }
    );

    const playerEventAdClicked = playerEventEmitter.addListener("adClicked", payload => {
      console.log("*** adClicked AdEvent: " + JSON.stringify(payload));
      //{ "clickThruUrl":  "http://google.com" }
     }
    );

    const playerEventAdRequested = playerEventEmitter.addListener("adRequested", payload => {
      console.log("*** adRequested AdEvent: " + JSON.stringify(payload));
      //{ "adTagUrl": "https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=" }
     }
    );

    const playerEventAdContentPauseRequested = playerEventEmitter.addListener("adContentPauseRequested", payload => {
      console.log("*** adContentPauseRequested AdEvent: " + JSON.stringify(payload));
      }
    );

    const playerEventAdContentResumeRequested = playerEventEmitter.addListener("adContentResumeRequested", payload => {
      console.log("*** adContentResumeRequested AdEvent: " + JSON.stringify(payload));  
      }
    );

    const playerEventAllAdsCompleted= playerEventEmitter.addListener("allAdsCompleted", payload => {
      console.log("*** allAdsCompleted AdEvent: " + JSON.stringify(payload));
      }
    );

    const playerEventAdError = playerEventEmitter.addListener("adError", payload => {
      console.log("*** adError AdEvent: " + JSON.stringify(payload));
      //{ "errorType":"FAILED_TO_REQUEST_ADS", "errorSeverity":"Fatal", "errorMessage":"There was a problem requesting ads from the server. Caused by: 6 adTagUrl=https://google.123.pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=", "errorCause":"null"}
     }
    );

    return () => {
      eventReminderListener.remove();
     
      //PLAYER
      playerEventPlayerInitialized.remove();
      playerEventLoadMediaSuccess.remove();
      playerEventLoadMediaFailed.remove();
      playerEventEnded.remove();
      playerEventCanPlay.remove();
      playerEventPlaying.remove();
      playerEventPlay.remove();
      playerEventPause.remove();
      playerEventStopped.remove();
      playerEventDurationChanged.remove();
      playerEventTimeUpdateChanged.remove();
      playerEventLoadedTimeRanges.remove();
      playerEventStateChanged.remove();
      playerEventTacksAvailable.remove();
      playerEventVideoTrackChanged.remove();
      playerEventAudioTrackChanged.remove();
      playerEventTextTrackChanged.remove();
      playerEventImageTrackChanged.remove();
      playerEvenThumbnailInfoResponse.remove();
      playerEventPlaybackInfoUpdated.remove();
      playerEventSeeking.remove();
      playerEventSeeked.remove();
      playerEventPlayerError.remove();
      playerEventBookmarkError.remove();
      playerEventConcurrencyErrorError.remove();
      playerEventBroadpeakError.remove();
      //ADS
      playerEventAdProgress.remove();
      playerEventAdCuepointsChanged.remove();
      playerEventAdStarted.remove();
      playerEventAdCompleted.remove();
      playerEventAdPaused.remove();
      playerEventAdResumed.remove();
      playerEventAdBufferStart.remove();
      playerEventAdSkipped.remove();
      playerEventAdClicked.remove();
      playerEventAdRequested.remove();
      playerEventAdContentPauseRequested.remove();
      playerEventAdContentResumeRequested.remove();
      playerEventAllAdsCompleted.remove();
      playerEventAdError.remove();
    };
  }, []);

  return (
    <View>
      <Text>Player</Text>
      <Button onPress={onPress} title="Kaltura Player event" />
    </View>
  );
};







//////////////////////////////////////////



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
