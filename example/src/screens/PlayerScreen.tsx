import React from 'react';
import NetInfo, { NetInfoSubscription } from '@react-native-community/netinfo';
import { RootSiblingParent } from 'react-native-root-siblings';
import {
  AppState,
  StyleSheet,
  View,
  ScrollView,
  Text,
  TouchableOpacity,
} from 'react-native';
import TrackList from '../components/TrackList';
import {
  KalturaPlayer,
  KalturaPlayerAPI,
  MEDIA_ENTRY_TYPE,
  MEDIA_FORMAT,
  PLAYER_TYPE,
  DRM_SCHEME,
  PLAYER_PLUGIN,
  PLAYER_RESIZE_MODES,
  WAKEMODE,
  SUBTITLE_STYLE,
  SUBTITLE_PREFERENCE,
  VIDEO_CODEC,
  AUDIO_CODEC,
  VR_INTERACTION_MODE,
} from 'react-native-kaltura-player';
import { NativeModules, NativeEventEmitter } from 'react-native';
import {
  PlayerEvents,
  AdEvents,
  AnalyticsEvents,
} from 'react-native-kaltura-player';
import { showToast, hideToast } from '../components/ScreenMessage';
import { PlayerUI } from '../components/PlayerUI';
import { Navigation } from 'react-native-navigation';
import { PLAYER_SCREEN } from '../../index';
import { ActivitySpinner } from '../components/ActivitySpinner';
import { Dropdown } from 'react-native-element-dropdown';

const kalturaPlayerEvents = NativeModules.KalturaPlayerEvents;
const playerEventEmitter = new NativeEventEmitter(kalturaPlayerEvents);

var playerType: PLAYER_TYPE = null;
let networkUnsubscribe: NetInfoSubscription | null = null;

export default class App extends React.Component<any, any> {
  player = KalturaPlayerAPI;
  videoTracks = [];
  appStateSubscription: any;
  isSliderSeeking: boolean = false;
  _isMounted: boolean = false;
  contentDuration: number = 0;

  constructor(props: any) {
    super(props);
    console.log('in constructor from App.');
    this._isMounted = false;
    this.state = {
      // Track List Props default States
      videoTitle: 'No Video Tracks',
      videoTrackList: [],
      audioTitle: 'No Audio Tracks',
      audioTrackList: [],
      textTitle: 'No Text Tracks',
      textTrackList: [],

      // Application lifecycle default States
      appState: AppState.currentState,

      // Seekbar Props default States
      isAdPlaying: false,
      isContentPlaying: false,
      currentPosition: 0,
      totalDuration: 0,
      isShowing: false,
    };
    // Subscribe
    networkUnsubscribe = NetInfo.addEventListener((state) => {
      console.log('Connection type', state.type);
      console.log('Is connected?', state.isConnected);
      if (!state.isConnected) {
        showToast('No internet connection');
      }
    });
  }

  componentDidMount() {
    console.log('componentDidMount from App.');
    this._isMounted = true;
    this.subscribeToAppLifecyle();
    console.log(`PlayerScreen incomingJSON: ${this.props.incomingJson}`);
    console.log(`PlayerScreen playertype: ${this.props.playerType}`);

    if (this.props.playerType == 'basic') {
      playerType = PLAYER_TYPE.BASIC;
    } else if (this.props.playerType == 'ovp') {
      playerType = PLAYER_TYPE.OVP;
    } else {
      playerType = PLAYER_TYPE.OTT;
    }

    var partnerId = this.props.incomingJson.partnerId; // Required only for OTT/OVP Player
    var options = this.props.incomingJson.initOptions;
    var asset = this.props.incomingJson.mediaAsset;
    var mediaId = this.props.incomingJson.mediaId;

    setupKalturaPlayer(
      this.player,
      playerType,
      JSON.stringify(options),
      JSON.stringify(asset),
      mediaId,
      partnerId
    ).then((_) => this.subscribeToPlayerListeners()); // Subscribe to Player Events
  }

  componentWillUnmount() {
    console.log('componentWillUnmount from App.');
    this._isMounted = false;
    if (networkUnsubscribe != null) {
      networkUnsubscribe();
    }
    this.player.removeListeners();
    this.appStateSubscription.remove();
    this.player.destroy();
    playerType = null;
  }

  getPlayerCurrentPosition(): any {
    this.player.getCurrentPosition().then((value: any) => {
      console.log(`getPlayerCurrentPosition getCurrentPosition ${value}`);
      return value;
    });
  }

  checkIfPlayerIsPlaying = () => {
    this.player
      .isPlaying()
      .then((value: any) =>
        console.log(`checkIfPlayerIsPlaying isPlaying ${value}`)
      );
  };

  checkIfMediaIsLive = () => {
    this.player.isLive().then((value: any) => console.log(`isLive ${value}`));
  };

  playPauseIconPressed = () => {
    if (this.state.isContentPlaying) {
      this.player.pause();
    } else {
      this.player.play();
    }
  };

  replayButtonPressed = () => {
    if (this.state.isContentPlaying) {
      this.player.replay();
    }
  };

  muteUnmuteButtonPressed = (isPlayerMute: boolean) => {
    console.log(
      'muteUnmuteButtonPressed pressed isPlayerMute: ' + isPlayerMute
    );
    if (this.state.isContentPlaying) {
      if (isPlayerMute) {
        this.player.setVolume(0);
      } else {
        this.player.setVolume(1);
      }
    }
  };

  seekButtonPressed = (isSeekForward: boolean) => {
    //console.log(`seekButtonPressed ${isSeekForward}  this.state.currentPosition is ${this.state.currentPosition}`);
    if (this.state.isContentPlaying) {
      if (isSeekForward) {
        this.player.seekTo(this.state.currentPosition + 10);
      } else {
        this.player.seekTo(this.state.currentPosition - 10);
      }
    }
  };

  // Only works for Android. for iOS this does not work.
  // TODO: Need to find the solution for iOS
  changeOrientation(isLandscape: boolean) {
    console.log('changeOrientation isLandscape: ' + isLandscape);
    Navigation.mergeOptions(PLAYER_SCREEN, {
      layout: {
        orientation: [isLandscape ? 'landscape' : 'portrait'],
      },
    });
  }

  changePlaybackRate = (rate: number) => {
    if (this.state.isAdPlaying) {
      showToast('Playrate can not be changed when Ad is playing');
    }
    this.player.setPlaybackRate(rate);
  };

  changeSubtitleStyling = () => {
    if (this.state.isAdPlaying) {
      showToast('Subtitle Styling can not be changed when Ad is playing');
    }
    this.player.updateSubtitleStyle(JSON.stringify(updatedSubtitleStyling));
  };

  changeMedia = (assetId: string, mediaAsset: string) => {
    this.player.updatePluginConfig(
      PLAYER_PLUGIN.YOUBORA,
      getUpdatedYouboraConfig
    );
    this.player.loadMedia(assetId, mediaAsset);
  };

  onTrackChangeListener = (trackId: string) => {
    if (this.state.isAdPlaying) {
      showToast('Track can not be changed when Ad is playing');
    }
    console.log('Clicked Track from TrackList component is: ' + trackId);
    this.player.changeTrack(trackId);
  };

  onSeekBarScrubbed = (seekedPosition: number) => {
    console.log('Scrubbed seek position is: ' + seekedPosition);
    this.player.seekTo(seekedPosition);
  };

  onSeekBarScrubbing = (isSeeking: boolean) => {
    console.log('onSeekBarScrubbing is: ' + isSeeking);
    this.isSliderSeeking = isSeeking;
  };

  subscribeToAppLifecyle = () => {
    this.appStateSubscription = AppState.addEventListener(
      'change',
      (nextAppState) => {
        if (nextAppState === 'active') {
          if (this.player != null) {
            this.player.onApplicationResumed(); // <TODO Add a condition if player is playing or not />
          }
        } else if (nextAppState === 'background') {
          if (this.player != null) {
            this.player.onApplicationPaused();
          }
        }
        console.log('App has come to the! ' + nextAppState);
        if (this._isMounted) {
          this.setState({ appState: nextAppState });
        }
      }
    );
  };

  /**
   * Add the Kaltura Player listeners to
   * add the Player, Ad and other Analytics
   * events
   *
   * @param player Kaltura Player
   */
  subscribeToPlayerListeners = () => {
    playerEventEmitter.addListener(PlayerEvents.DURATION_CHANGE, (payload) => {
      console.log(
        'PlayerEvent DURATION_CHANGE : ' +
          (payload.duration != null
            ? payload.duration
            : ' Empty duration change')
      );

      if (payload.duration != null) {
        this.contentDuration = payload.duration;
      }
    });

    playerEventEmitter.addListener(PlayerEvents.PLAYHEAD_UPDATED, (payload) => {
      //console.log('PlayerEvent PLAYHEAD_UPDATED position : ' + payload.position + ' bufferPosition: ' + payload.bufferPosition);
      if (this._isMounted && !this.isSliderSeeking && !this.state.isAdPlaying) {
        this.setState(() => ({
          currentPosition: payload.position,
          totalDuration: this.contentDuration,
        }));
      }
    });

    playerEventEmitter.addListener(PlayerEvents.PLAY, () => {
      console.log('PlayerEvent PLAY');
      if (this._isMounted) {
        this.setState(() => ({
          isContentPlaying: true,
        }));
      }
    });

    playerEventEmitter.addListener(PlayerEvents.PAUSE, () => {
      console.log('PlayerEvent PAUSE');
      if (this._isMounted) {
        this.setState(() => ({
          isContentPlaying: false,
        }));
      }
    });

    playerEventEmitter.addListener(PlayerEvents.VOLUME_CHANGED, (payload) => {
      console.log(`PlayerEvent VOLUME_CHANGED ${payload.volume}`);
    });

    playerEventEmitter.addListener(PlayerEvents.LOAD_TIME_RANGES, (payload) => {
      console.log('PlayerEvent LOAD_TIME_RANGES : ' + payload);
    });

    playerEventEmitter.addListener(PlayerEvents.ERROR, (payload) => {
      console.log('PlayerEvent ERROR : ' + payload.message);
    });

    playerEventEmitter.addListener(PlayerEvents.TRACKS_AVAILABLE, (payload) => {
      console.log('PlayerEvent TRACKS_AVAILABLE : ' + JSON.stringify(payload));
      const videoTracks = payload.video;

      if (this._isMounted && videoTracks.length > 0) {
        this.setState(() => ({
          videoTitle: 'Video Tracks',
          videoTrackList: videoTracks,
        }));
      }

      const audioTracks = payload.audio;

      if (this._isMounted && audioTracks.length > 0) {
        this.setState(() => ({
          audioTitle: 'Audio Tracks',
          audioTrackList: audioTracks,
        }));
      }

      const textTracks = payload.text;

      if (this._isMounted && textTracks.length > 0) {
        this.setState(() => ({
          textTitle: 'Text Tracks',
          textTrackList: textTracks,
        }));
      }
    });

    playerEventEmitter.addListener(PlayerEvents.DRM_INITIALIZED, (payload) => {
      console.log('PlayerEvent DRM_INITIALIZED : ' + JSON.stringify(payload));
    });

    playerEventEmitter.addListener(PlayerEvents.LOAD_TIME_RANGES, (payload) => {
      console.log('PlayerEvent LOAD_TIME_RANGES : ' + payload);
    });

    playerEventEmitter.addListener(AdEvents.CONTENT_PAUSE_REQUESTED, (_) => {
      console.log('AdEvent CONTENT_PAUSE_REQUESTED');
      if (this._isMounted) {
        this.setState(() => ({
          isAdPlaying: true,
          isContentPlaying: false,
        }));
      }
    });

    playerEventEmitter.addListener(AdEvents.CONTENT_RESUME_REQUESTED, (_) => {
      console.log('AdEvent CONTENT_RESUME_REQUESTED');
      if (this._isMounted) {
        this.setState(() => ({
          isAdPlaying: false,
          isContentPlaying: true,
        }));
      }
    });

    playerEventEmitter.addListener(AdEvents.LOADED, (payload) => {
      console.log(
        'AdEvents LOADED : ' +
          (payload.adDuration != null
            ? payload.adDuration
            : ' Empty Ad duration')
      );
      if (this._isMounted && payload.adDuration != null) {
        this.setState(() => ({
          totalDuration: payload.adDuration / 1000,
        }));
      }
    });

    playerEventEmitter.addListener(AdEvents.AD_PROGRESS, (payload) => {
      //console.log('AdEvent AD_PROGRESS : ' + payload.currentAdPosition);
      if (this._isMounted && payload.currentAdPosition != null) {
        this.setState(() => ({
          currentPosition: payload.currentAdPosition,
        }));
      }
    });

    playerEventEmitter.addListener(AdEvents.CUEPOINTS_CHANGED, (payload) => {
      //console.log('AdEvent CUEPOINTS_CHANGED : ' + payload.adCuePoints);
    });

    playerEventEmitter.addListener(PlayerEvents.STATE_CHANGED, (payload) => {
      console.log('PlayerEvents STATE_CHANGED : ' + payload.newState);
      if (this._isMounted) {
        if (
          !this.state.isAdPlaying &&
          (payload.newState === 'LOADING' || payload.newState === 'BUFFERING')
        ) {
          this.setState(() => ({
            isShowing: true,
          }));
        } else {
          this.setState(() => ({
            isShowing: false,
          }));
        }
      }
    });

    playerEventEmitter.addListener(AdEvents.AD_BUFFER_START, () => {
      console.log('AdEvent AD_BUFFER_START');
      if (this._isMounted) {
        this.setState(() => ({
          isShowing: true,
        }));
      }
    });

    playerEventEmitter.addListener(AdEvents.AD_BUFFER_END, () => {
      console.log('AdEvent AD_BUFFER_END');
      if (this._isMounted) {
        this.setState(() => ({
          isShowing: false,
        }));
      }
    });
  };

  render() {
    // console.log("IN render Playertype is this.props.playerTyp : " + this.props.playerType);
    // console.log("IN render Playertype is: " + playerType);

    return (
      <RootSiblingParent>
        <ScrollView>
          <View
            style={[
              styles.flex_container,
              {
                flexDirection: 'row',
              },
            ]}
          >
            {this.state.videoTrackList.length > 0 ? (
              <TrackList
                style={{ flex: 1 }}
                trackType={'video'}
                title={this.state.videoTitle}
                trackList={this.state.videoTrackList}
                onTrackChangeListener={this.onTrackChangeListener}
              />
            ) : (
              <Text></Text>
            )}
            {this.state.audioTrackList.length > 0 ? (
              <TrackList
                style={{ flex: 1 }}
                trackType={'audio'}
                title={this.state.audioTitle}
                trackList={this.state.audioTrackList}
                onTrackChangeListener={this.onTrackChangeListener}
              />
            ) : (
              <Text></Text>
            )}
            {this.state.textTrackList.length > 0 ? (
              <TrackList
                style={{ flex: 1 }}
                trackType={'text'}
                title={this.state.textTitle}
                trackList={this.state.textTrackList}
                onTrackChangeListener={this.onTrackChangeListener}
              />
            ) : (
              <Text></Text>
            )}
          </View>

          <View style={styles.playerViewRoot}>
            <PlayerUI
              isAdPlaying={this.state.isAdPlaying}
              position={this.state.currentPosition}
              duration={this.state.totalDuration}
              onSeekBarScrubbed={this.onSeekBarScrubbed}
              onSeekBarScrubbing={this.onSeekBarScrubbing}
              isContentPlaying={this.state.isContentPlaying}
              playPauseIconPressed={this.playPauseIconPressed}
              replayButtonPressed={this.replayButtonPressed}
              muteUnmuteButtonPressed={this.muteUnmuteButtonPressed}
              seekButtonPressed={this.seekButtonPressed}
              changeOrientation={this.changeOrientation}
            ></PlayerUI>

            <ActivitySpinner isShowing={this.state.isShowing} />
          </View>

          <Dropdown
            style={styles.dropdown}
            selectedTextStyle={styles.selectedTextStyle}
            iconStyle={styles.iconStyle}
            maxHeight={200}
            data={playrates}
            valueField="rate"
            labelField="name"
            placeholder="Playrate"
            onChange={(playrates) => {
              console.log('Selected Playback rate is: ' + playrates.rate);
              {
                this.changePlaybackRate(playrates.rate);
              }
            }}
          />

          <TouchableOpacity
            style={[styles.button]}
            onPress={() => {
              this.changeMedia(
                playbackUrlChangeMedia,
                JSON.stringify(basicMediaAsset)
              );
            }}
          >
            <Text style={[styles.bigWhite]}>Change Media</Text>
          </TouchableOpacity>
        </ScrollView>
      </RootSiblingParent>
    );
  }
}

async function setupKalturaPlayer(
  player: KalturaPlayerAPI,
  playerType: PLAYER_TYPE,
  options: String,
  mediaAsset: String,
  mediaId: String, // PlaybackUrl or EntryId (OVP) or MediaId (OTT)
  partnerId: number = 0
) {
  try {
    const playerCreated = await player.setup(playerType, options, partnerId);
    console.log(`playerCreated ON APP SIDE => ${playerCreated}`);
    if (playerCreated != null) {
      player.addListeners();
      const mediaLoaded = await player.loadMedia(mediaId, mediaAsset);
      console.log(`mediaLoaded => ${mediaLoaded}`);
    } else {
      console.error('Player is not created.');
    }
  } catch (err) {
    console.log(err);
    throw err;
  }
}

const playrates = [
  {
    name: 'Rate 0.5',
    rate: 0.5,
  },
  {
    name: 'Rate 1.0',
    rate: 1.0,
  },
  {
    name: 'Rate 1.5',
    rate: 1.5,
  },
  {
    name: 'Rate 2.0',
    rate: 2.0,
  },
];

const styles = StyleSheet.create({
  container: {
    marginTop: 50,
  },
  flex_container: {
    flex: 1,
    flexWrap: 'wrap',
  },
  bigBlue: {
    color: 'blue',
    fontWeight: 'bold',
    fontSize: 12,
    margin: 3,
  },
  bigWhite: {
    color: 'white',
    fontWeight: 'bold',
    fontSize: 12,
    margin: 3,
  },
  row: {
    flexDirection: 'row',
    flexWrap: 'wrap',
    margin: 10,
  },
  blue_center: {
    color: 'blue',
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
  button: {
    paddingHorizontal: 8,
    paddingVertical: 6,
    borderRadius: 4,
    backgroundColor: 'blue',
    alignSelf: 'flex-start',
    marginHorizontal: '1%',
    marginBottom: 6,
    minWidth: '20%',
    textAlign: 'center',
  },
  playerViewRoot: {
    flex: 1,
    backgroundColor: '#fff',
    alignItems: 'center',
    justifyContent: 'center',
  },
  dropdown: {
    margin: 5,
    height: 30,
    width: 140,
    backgroundColor: '#EEEEEE',
    borderRadius: 210,
    paddingHorizontal: 8,
  },
  selectedTextStyle: {
    fontSize: 16,
    marginLeft: 8,
  },
  iconStyle: {
    width: 20,
    height: 20,
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

// const playbackUrl =
//  'https://livesim.dashif.org/livesim/chunkdur_1/ato_7/testpic4_8s/Manifest300.mpd'; // LIVE MEDIA WITH LOW LATENCY

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
  startPosition: 0,
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
  aspectRatioResizeMode: PLAYER_RESIZE_MODES.FIT,
  handleAudioFocus: true,
  plugins: {
    ima: {
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

var updatedSubtitleStyling = {
  subtitleStyleName: 'MyCustomSubtitleStyle',
  subtitleTextColor: '#FFFFFF',
  subtitleBackgroundColor: '#FF00FF',
  subtitleWindowColor: '#FF00FF',
  subtitleEdgeColor: '#0000FF',
  subtitleTextSizeFraction: 'SUBTITLE_FRACTION_100',
  subtitleEdgeType: 'EDGE_TYPE_DROP_SHADOW',
  overrideInlineCueConfig: true,
  verticalPositionPercentage: 50,
  horizontalPositionPercentage: 50,
  horizontalAlignment: 'ALIGN_CENTER',
};

var updatedAbrSettings = {
  //minVideoBitrate: 500000, // For Basic 1st media, Harold
  maxVideoBitrate: 800000, // For Basic 1st media, Harold
};

var updatedLowLatencyConfig = {
  targetOffsetMs: 5000,
  maxOffsetMs: 5000,
  maxPlaybackSpeed: 4,
};
