import React from 'react';

import { StyleSheet, View, Text, TouchableOpacity } from 'react-native';
import TrackList from "../src/components/TrackList";
import {
  KalturaPlayer,
  MEDIA_ENTRY_TYPE,
  MEDIA_FORMAT,
  PLAYER_TYPE,
  DRM_SCHEME,
  PLAYER_PLUGIN,
} from 'react-native-kaltura-player';
import { NativeEventEmitter } from 'react-native';
import PlayerEvents from 'react-native-kaltura-player';

const playerEventEmitter = new NativeEventEmitter();

export default class App extends React.Component<any, any> {

  constructor(props: any) {
    super(props);
    this.state = {
      title: "Tracks Title",
      trackList: [
        {
          "bitrate": 0,
          "isSelected": true,
          "isAdaptive": true,
          "height": 0,
          "width": 0,
          "id": "NONE"
        }
      ]
    }
  }

  componentDidMount() {
    console.log('componentDidMount from App.');

    // OTT Configuration
    // this.player.setup(JSON.stringify(initOptions), OttPartnerId);
    // this.player.addListeners();
    // this.player.loadMedia(OttMediaId, JSON.stringify(mediaAsset));

    // OVP Configuration
    // this.player.setup(JSON.stringify(ovpInitOptions), OvpPartnerId);
    // this.player.addListeners();
    // this.player.loadMedia(OvpEntryId, JSON.stringify(ovpMediaAsset));

    // BASIC Configuration
    this.player.setup(JSON.stringify(basicInitOptions));
    this.player.addListeners();
    this.player.loadMedia(playbackUrl, JSON.stringify(basicMediaAsset));

    // Subscribe to Player Events
    this.subscribeToPlayerListeners();
  }
  
  componentWillUnmount() {
    console.log('componentWillUnmount from App.');
    this.player.removeListeners();
  }

  player: KalturaPlayer;

  doPause = () => {
    this.player.pause();
  };

  doPlay = () => {
    this.player.play();
  };

  changePlaybackRate = (rate: number) => {
    this.player.setPlaybackRate(rate);
  };

  changeMedia = (assetId: string, mediaAsset: string) => {
    this.player.updatePluginConfig(
      PLAYER_PLUGIN.YOUBORA,
      getUpdatedYouboraConfig
    );
    this.player.loadMedia(assetId, mediaAsset);
  };

  onTrackChangeListener = (trackId) => {
    console.log('Clicked Track from TrackList component is: ' + trackId);
    this.player.changeTrack(trackId);
  }

  /**
   * Add the Kaltura Player listeners to 
   * add the Player, Ad and other Analytics 
   * events
   * 
   * @param player Kaltura Player
   */
  subscribeToPlayerListeners = () => {

    playerEventEmitter.addListener(PlayerEvents.TRACKS_AVAILABLE, (payload) => {
      console.log('TRACKS_AVAILABLE PlayerEvent: ' + JSON.stringify(payload));
      //console.log('TRACKS_AVAILABLE tracks length: ' + Object.keys(payload).length);
      const videoList = payload.video
      console.log('Video Track list: ' + videoList);
      this.setState(() => ({
        title: 'Video Tracks',
        trackList: videoList
      }));
    });
    
    playerEventEmitter.addListener(PlayerEvents.DRM_INITIALIZED, (payload) => {
      console.log('*** DRM_INITIALIZED PlayerEvent: ' + JSON.stringify(payload));
    });
    
  }

  render() {
    
    return (
      <View>
        <Text style={styles.red} >Kaltura Player Demo</Text>

        <TrackList title = {this.state.title} 
        trackList = {this.state.trackList}
        onTrackChangeListener = {this.onTrackChangeListener}
        />

        <KalturaPlayer
          ref={(ref: KalturaPlayer) => {
            this.player = ref;
          }}
          style={styles.center}
          playerType={PLAYER_TYPE.BASIC}
        ></KalturaPlayer>

        <TouchableOpacity
          onPress={() => {
            this.doPlay();
          }}
        >
          <Text style={[styles.bigBlue]}>Play Media</Text>
        </TouchableOpacity>

        <TouchableOpacity
          onPress={() => {
            this.doPause();
          }}
        >
          <Text style={[styles.bigBlue]}>Pause Media</Text>
        </TouchableOpacity>

        <TouchableOpacity
          onPress={() => {
            this.changePlaybackRate(2.0);
          }}
        >
          <Text style={[styles.bigBlue]}>PlaybackRate 2.0</Text>
        </TouchableOpacity>

        <TouchableOpacity
          onPress={() => {
            this.changePlaybackRate(0.5);
          }}
        >
          <Text style={[styles.bigBlue]}>PlaybackRate 0.5</Text>
        </TouchableOpacity>

        <TouchableOpacity
          onPress={() => {
            this.changeMedia(
              playbackUrlChangeMedia,
              JSON.stringify(basicMediaAsset)
            );
          }}
        >
          <Text style={[styles.bigBlue]}>Change Media</Text>
        </TouchableOpacity>
      </View>
    );
  }
}

const styles = StyleSheet.create({
  container: {
    marginTop: 50,
  },
  bigBlue: {
    color: 'blue',
    fontWeight: 'bold',
    fontSize: 12,
    margin: 3
  },
  red: {
    color: 'red',
    textAlign: 'center',
    fontSize: 20,
    margin: 10,
  },
  center: {
    flex: 1,
    padding: 100,
    height: 300,
    alignItems: 'center',
  },
});

/**
 * **********************
 *                      *
 * Below TEST JSONs for *
 * Basic/OTT/OVP Player *
 *                      *
 * **********************
 */

// Kaltura Basic Player Test JSON
const playbackUrl =
  'http://cdnapi.kaltura.com/p/243342/sp/24334200/playManifest/entryId/0_uka1msg4/flavorIds/1_vqhfu6uy,1_80sohj7p/format/applehttp/protocol/http/a.m3u8';
const playbackUrlChangeMedia =
  'http://d3rlna7iyyu8wu.cloudfront.net/skip_armstrong/skip_armstrong_multi_language_subs.m3u8';

const basicUrlWithDrm =
  'https://storage.googleapis.com/wvmedia/cenc/h264/tears/tears.mpd';
const basicDRMUrl =
  'https://proxy.uat.widevine.com/proxy?video_id=2015_tears&provider=widevine_test';

const playbackFormat = MEDIA_FORMAT.HLS;
const entryType = MEDIA_ENTRY_TYPE.VOD;
const id = 'basicId';
const name = 'basicName';
const duration = 120;

var basicMediaAsset = {
  id: id,
  name: name,
  duration: duration,
  mediaEntryType: entryType,
  mediaFormat: playbackFormat,
  // "drmData" : [
  //   {
  //     "licenseUri" : basicDRMUrl,
  //     "scheme" : DRM_SCHEME.WIDEVINE_CENC
  //   }
  // ]
};

var basicInitOptions = {
  autoplay: true,
  preload: true,
  requestConfig: {
    crossProtocolRedirectEnabled: true,
    readTimeoutMs: 8000,
    connectTimeoutMs: 8000,
  },
  allowCrossProtocolRedirect: true,
  allowFairPlayOnExternalScreens: true,
  shouldPlayImmediately: true,
  networkSettings: {
    autoBuffer: true,
    preferredForwardBufferDuration: 30000,
    automaticallyWaitsToMinimizeStalling: true,
  },
  trackSelection: {
    textMode: 'AUTO',
    textLanguage: 'en',
    audioMode: 'AUTO',
    audioLanguage: 'en',
  },
  handleAudioFocus: true,
  plugins: {
    ima: {
      //"adTagUrl" : "",
      adTagUrl:
        'https://pubads.g.doubleclick.net/gampad/ads?sz=640x480&iu=/124319096/external/single_ad_samples&ciu_szs=300x250&impl=s&gdfp_req=1&env=vp&output=vast&unviewed_position_start=1&cust_params=deployment%3Ddevsite%26sample_ct%3Dskippablelinear&correlator=',
      alwaysStartWithPreroll: true,
      enableDebugMode: false,
    },
    youbora: {
      accountCode: 'kalturatest',
      username: 'gourav_rn',
      userEmail: 'gourav_rn@mobile.com',
      userAnonymousId: 'user_anonymous_Id',
      userType: 'user_type',
      houseHoldId: 'zxzxz',
      userObfuscateIp: true,
      httpSecure: true,
      transportFormat: 'transportFormat',
      urlToParse: 'urlToParse',
      linkedViewId: 'linkedViewId',
      isAutoStart: true,
      isAutoDetectBackground: true,
      isEnabled: true,
      isForceInit: true,
      isOffline: false,
      haltOnError: false,
      enableAnalytics: true,
      enableSmartAds: true,
      content: {
        contentBitrate: 640000,
        contentCdn: 'a',
        contentCdnNode: 'b',
        contentCdnType: 'c',
        contentChannel: 'd',
        contentContractedResolution: '720p',
        contentCost: '122',
        contentDrm: 'e',
        contentDuration: 1200000,
        contentEncodingAudioCodec: 'ec-3',
        contentEncodingCodecProfile: 'f',
        contentEncodingContainerFormat: 'g',
        contentEncodingVideoCodec: 'h',
        contentEpisodeTitle: 'title2',
        contentFps: 60,
        contentGenre: 'drama',
        contentGracenoteId: 'i',
        contentId: '1st_media',
        contentImdbId: 'j',
        contentIsLive: false,
        contentIsLiveNoSeek: false,
        contentLanguage: 'en',
        contentPackage: 'aaa',
        contentPlaybackType: 'bbb',
        contentPrice: 10000,
        contentProgram: 'program',
        contentRendition: '22223',
        // "contentResource": "http://ssss.m3u8", // TODO: THIS IS CREATING CRASH ON ANDROID NATIVE SIDE WHILE PARSGING THE JSON
        contentSaga: 'ccc',
        contentSeason: 'ddd',
        contentStreamingProtocol: 'applehttp',
        contentSubtitles: 'en',
        contentThroughput: 1230000,
        contentTitle: 'title',
        contentTransactionCode: 'dssd',
        contentTotalBytes: 123344,
        contentSendTotalBytes: false,
        contentTvShow: 'sadsa',
        contentType: 'drama',
      },
      app: {
        appName: 'MyTestApp',
        appReleaseVersion: 'v1.0',
      },
      parse: {
        parseManifest: true,
        parseCdnNode: true,
        parseCdnSwitchHeader: true,
        cdnNodeList: ['Akamai', 'Cloudfront', 'Level3', 'Fastly', 'Highwinds'],
        cdnNameHeaders: 'x-cdn-forward',
        parseCdnTTL: 60,
      },
      network: {
        networkIP: '1.1.1.1',
        networkConnectionType: 'cellular',
        networkIsp: 'orange',
      },
      device: {
        deviceBrand: 'deviceBrand',
        deviceCode: 'deviceCode',
        deviceId: 'deviceId',
        deviceModel: 'deviceModel',
        deviceOsName: 'deviceOsName',
        deviceOsVersion: 'deviceOsVersion',
        deviceType: 'deviceType',
        deviceIsAnonymous: false,
      },
      errors: {
        errorsIgnore: ['exception1', 'exception2'],
        errorsFatal: ['exception3', 'exception4'],
        errorsNonFatal: ['exception5', 'exception6'],
      },
      ads: {
        adBreaksTime: [0, 15, 60],
        adCampaign: 'AdCmap',
        adCreativeId: 'adCreativeId',
        adExpectedBreaks: 3,
        adGivenAds: 5,
        adGivenBreaks: 3,
        adProvider: 'adProvider',
        adResource: 'adResource',
        adTitle: 'adTitle',
        adCustomDimensions: {
          adCustomDimension1: 'adCustomDimension1',
          adCustomDimension2: 'adCustomDimension2',
          adCustomDimension3: 'adCustomDimension3',
          adCustomDimension4: 'adCustomDimension4',
          adCustomDimension5: 'adCustomDimension5',
        },
      },
      properties: {
        year: 'your_year',
        cast: 'your_cast',
        director: 'your_director',
        owner: 'your_owner',
        parental: 'your_parental',
        rating: 'your_rating',
        audioChannels: 'your_audio_channels',
        device: 'your_device',
      },
      contentCustomDimensions: {
        contentCustomDimension1: 'customDimension1',
        contentCustomDimension2: 'customDimension2',
        contentCustomDimension3: 'customDimension3',
        contentCustomDimension4: 'customDimension4',
        contentCustomDimension5: 'customDimension5',
      },
    },
  },
};

// Kaltura OTT and OVP Player Test JSON for OTT/OVP media PlayerInitOptions

//OTT 3009
const PhoenixBaseUrl = 'https://rest-us.ott.kaltura.com/v4_5/api_v3/';
const OttPartnerId = 3009;
const OttMediaId = '548576';
const OttMediaFormat = 'Mobile_Main';

const ottMediaIdChangeMedia = '548575';

const OttMediaProtocol = 'http'; // "https"
const OttAssetType = 'media';
const OttPlaybackContextType = 'playback';

// OVP 2215841
const OvpBaseUrl = 'https://cdnapisec.kaltura.com';
const OvpPartnerId = 2215841;
const OvpEntryId = '1_w9zx2eti';

const OvpEntryIdChangeMedia = '1_ebs5e9cy';

var ovpInitOptions = {
  serverUrl: OvpBaseUrl,
  autoplay: true,
  preload: true,
  requestConfig: {
    crossProtocolRedirectEnabled: true,
    readTimeoutMs: 8000,
    connectTimeoutMs: 8000,
  },
  allowCrossProtocolRedirect: true,
  allowFairPlayOnExternalScreens: true,
  shouldPlayImmediately: true,
  networkSettings: {
    autoBuffer: true,
    preferredForwardBufferDuration: 30000,
    automaticallyWaitsToMinimizeStalling: true,
  },
  multicastSettings: {
    useExoDefaultSettings: true,
    maxPacketSize: 3000,
    socketTimeoutMillis: 10000,
    extractorMode: 'MODE_MULTI_PMT',
    firstSampleTimestampUs: 0,
  },
  mediaEntryCacheConfig: {
    allowMediaEntryCaching: true,
    maxMediaEntryCacheSize: 15,
    timeoutMs: 3600000,
  },
  abrSettings: {
    minVideoBitrate: 600000,
    maxVideoBitrate: 1500000,
  },
  trackSelection: {
    textMode: 'AUTO',
    textLanguage: 'en',
    audioMode: 'AUTO',
    audioLanguage: 'en',
  },
  handleAudioFocus: true,
  plugins: {
    ima: {},
  },
};

var ovpMediaAsset = {
  initialVolume: 1.0,
  format: OttMediaFormat,
  assetType: OttAssetType,
  protocol: OttMediaProtocol,
  playbackContextType: OttPlaybackContextType,
  urlType: 'PLAYMANIFEST',
  // "urlType": "DIRECT",
  // "ks":"KS",
  // "streamerType": "Mpegdash",
  // "streamerType": "Multicast",

  startPosition: 0,
};

var initOptions = {
  serverUrl: PhoenixBaseUrl,
  autoplay: true,
  preload: true,
  requestConfig: {
    crossProtocolRedirectEnabled: true,
    readTimeoutMs: 8000,
    connectTimeoutMs: 8000,
  },
  allowCrossProtocolRedirect: true,
  allowFairPlayOnExternalScreens: true,
  shouldPlayImmediately: true,
  networkSettings: {
    autoBuffer: true,
    preferredForwardBufferDuration: 30000,
    automaticallyWaitsToMinimizeStalling: true,
  },
  multicastSettings: {
    useExoDefaultSettings: true,
    maxPacketSize: 3000,
    socketTimeoutMillis: 10000,
    extractorMode: 'MODE_MULTI_PMT',
    firstSampleTimestampUs: 0,
  },
  mediaEntryCacheConfig: {
    allowMediaEntryCaching: true,
    maxMediaEntryCacheSize: 15,
    timeoutMs: 3600000,
  },
  abrSettings: {
    minVideoBitrate: 600000,
    maxVideoBitrate: 1500000,
  },
  trackSelection: {
    textMode: 'AUTO',
    textLanguage: 'en',
    audioMode: 'AUTO',
    audioLanguage: 'en',
  },
  handleAudioFocus: true,
  plugins: {
    ima: {},
  },
};

// MediaAsset for Player

var mediaAsset = {
  initialVolume: 1.0,
  format: OttMediaFormat,
  assetType: OttAssetType,
  protocol: OttMediaProtocol,
  playbackContextType: OttPlaybackContextType,
  urlType: 'PLAYMANIFEST',
  // "urlType": "DIRECT",
  // "ks":"KS",
  // "streamerType": "Mpegdash",
  // "streamerType": "Multicast",

  startPosition: 0,
};

var getUpdatedYouboraConfig = {
  accountCode: 'kalturatest',
  username: 'gourav_rn',
  userEmail: 'gourav_rn@mobile.com',
  userAnonymousId: 'user_anonymous_Id',
  userType: 'user_type',
  houseHoldId: 'zxzxz',
  userObfuscateIp: true,
  httpSecure: true,
  transportFormat: 'transportFormat',
  urlToParse: 'urlToParse',
  linkedViewId: 'linkedViewId',
  isAutoStart: true,
  isAutoDetectBackground: true,
  isEnabled: true,
  isForceInit: true,
  isOffline: false,
  haltOnError: false,
  enableAnalytics: true,
  enableSmartAds: true,
  content: {
    contentBitrate: 640000,
    contentCdn: 'a',
    contentCdnNode: 'b',
    contentCdnType: 'c',
    contentChannel: 'd',
    contentContractedResolution: '720p',
    contentCost: '122',
    contentDrm: 'e',
    contentDuration: 1200000,
    contentEncodingAudioCodec: 'ec-3',
    contentEncodingCodecProfile: 'f',
    contentEncodingContainerFormat: 'g',
    contentEncodingVideoCodec: 'h',
    contentEpisodeTitle: 'title2',
    contentFps: 60,
    contentGenre: 'drama',
    contentGracenoteId: 'i',
    contentId: '2nd_media',
    contentImdbId: 'j',
    contentIsLive: false,
    contentIsLiveNoSeek: false,
    contentLanguage: 'en',
    contentPackage: 'aaa',
    contentPlaybackType: 'bbb',
    contentPrice: 10000,
    contentProgram: 'program',
    contentRendition: '22223',
    // "contentResource": "http://ssss.m3u8", // TODO: THIS IS CREATING CRASH ON ANDROID NATIVE SIDE WHILE PARSGING THE JSON
    contentSaga: 'ccc',
    contentSeason: 'ddd',
    contentStreamingProtocol: 'applehttp',
    contentSubtitles: 'en',
    contentThroughput: 1230000,
    contentTitle: 'title',
    contentTransactionCode: 'dssd',
    contentTotalBytes: 123344,
    contentSendTotalBytes: false,
    contentTvShow: 'sadsa',
    contentType: 'drama',
  },
  app: {
    appName: 'MyTestApp',
    appReleaseVersion: 'v1.0',
  },
  parse: {
    parseManifest: true,
    parseCdnNode: true,
    parseCdnSwitchHeader: true,
    cdnNodeList: ['Akamai', 'Cloudfront', 'Level3', 'Fastly', 'Highwinds'],
    cdnNameHeaders: 'x-cdn-forward',
    parseCdnTTL: 60,
  },
  network: {
    networkIP: '1.1.1.1',
    networkConnectionType: 'cellular',
    networkIsp: 'orange',
  },
  device: {
    deviceBrand: 'deviceBrand',
    deviceCode: 'deviceCode',
    deviceId: 'deviceId',
    deviceModel: 'deviceModel',
    deviceOsName: 'deviceOsName',
    deviceOsVersion: 'deviceOsVersion',
    deviceType: 'deviceType',
    deviceIsAnonymous: false,
  },
  errors: {
    errorsIgnore: ['exception1', 'exception2'],
    errorsFatal: ['exception3', 'exception4'],
    errorsNonFatal: ['exception5', 'exception6'],
  },
  ads: {
    adBreaksTime: [0, 15, 60],
    adCampaign: 'AdCmap',
    adCreativeId: 'adCreativeId',
    adExpectedBreaks: 3,
    adGivenAds: 5,
    adGivenBreaks: 3,
    adProvider: 'adProvider',
    adResource: 'adResource',
    adTitle: 'adTitle',
    adCustomDimensions: {
      adCustomDimension1: 'adCustomDimension1',
      adCustomDimension2: 'adCustomDimension2',
      adCustomDimension3: 'adCustomDimension3',
      adCustomDimension4: 'adCustomDimension4',
      adCustomDimension5: 'adCustomDimension5',
    },
  },
  properties: {
    year: 'your_year',
    cast: 'your_cast',
    director: 'your_director',
    owner: 'your_owner',
    parental: 'your_parental',
    rating: 'your_rating',
    audioChannels: 'your_audio_channels',
    device: 'your_device',
  },
  contentCustomDimensions: {
    contentCustomDimension1: 'customDimension1',
    contentCustomDimension2: 'customDimension2',
    contentCustomDimension3: 'customDimension3',
    contentCustomDimension4: 'customDimension4',
    contentCustomDimension5: 'customDimension5',
  },
};
