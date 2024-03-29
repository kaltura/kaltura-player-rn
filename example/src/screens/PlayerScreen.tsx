import React from 'react';
import NetInfo, { NetInfoSubscription } from '@react-native-community/netinfo';
import { RootSiblingParent } from 'react-native-root-siblings';
import {
  Platform,
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
  LOG_LEVEL,
} from 'kaltura-player-rn';
import { NativeModules, NativeEventEmitter } from 'react-native';
import {
  PlayerEvents,
  AdEvents,
  AnalyticsEvents,
} from 'kaltura-player-rn';
import { showToast, hideToast } from '../components/ScreenMessage';
import { PlayerUI } from '../components/PlayerUI';
import { Navigation } from 'react-native-navigation';
import { PLAYER_SCREEN } from '../../index';
import { ActivitySpinner } from '../components/ActivitySpinner';
import { Dropdown } from 'react-native-element-dropdown';
import type { EmitterSubscription } from 'react-native';
import { object, string } from 'prop-types';

const kalturaPlayerEvents = NativeModules.KalturaPlayerEvents;
const playerEventEmitter = new NativeEventEmitter(kalturaPlayerEvents);

const platform_android = 'android';
const platform_ios = 'ios';

let playerType: PLAYER_TYPE = null;
let networkUnsubscribe: NetInfoSubscription | null = null;

let eventsSubscriptionList: Array<EmitterSubscription> = [];
const BTN_REMOVE_VIEW_TXT = "Remove PlayerView";
const BTN_ADD_VIEW_TXT = "Add PlayerView";
const timerInterval = 500; // in ms

const MediaListModel = {
  mediaId: string,
  mediaAsset: object,
  plugins: object
}

export default class App extends React.Component<any, any> {
  player = KalturaPlayerAPI;
  videoTracks = [];
  appStateSubscription: any;
  isSliderSeeking: boolean = false;
  _isMounted: boolean = false;
  contentDuration: number = 0;
  progressInterval: Timer = null;

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
      isPlayerViewVisible: true,
      btnTextPlayerView: BTN_REMOVE_VIEW_TXT,

      // Change Media helpers
      isChangeMediaAvailable: false,
      currentChangeMediaIndex: 0,

      isDAIPluginAvailable: false,
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

    console.log(`PlayerScreen incomingJson: ${this.props.incomingJson}`);
    console.log(`PlayerScreen playertype: ${this.props.playerType}`);

    if (this.props.playerType == 'basic') {
      playerType = PLAYER_TYPE.BASIC;
    } else if (this.props.playerType == 'ovp') {
      playerType = PLAYER_TYPE.OVP;
    } else {
      playerType = PLAYER_TYPE.OTT;
    }

    var asset = null;
    var mediaId = null;
    var partnerId = this.props.incomingJson.partnerId; // Required only for OTT/OVP Player
    var options = this.props.incomingJson.initOptions;
    var mediaList: Array<typeof MediaListModel> = this.props.incomingJson.mediaList;
    if (mediaList != null && mediaList.length > 0) {
      asset = mediaList[this.state.currentChangeMediaIndex].mediaAsset;
      mediaId = mediaList[this.state.currentChangeMediaIndex].mediaId;
      if (!mediaId || (playerType !== PLAYER_TYPE.BASIC && partnerId <= 0)) {
        console.error(`Invalid MediaId: ${mediaId} or partnerId ${partnerId}`);
        return;
      }
      if (mediaList.length > 1) {
        this.setState(() => ({
          isChangeMediaAvailable: true,
          currentChangeMediaIndex: 0,
        }));
      }
    } else {
      console.error(`Invalid MediaId: ${mediaId} or partnerId ${partnerId}`);
      return;
    }

    if (options != null && options.plugins != null) {
      var isDaiPlugin = options.plugins.imadai;
      console.log(`isDaiPlugin = ${JSON.stringify(isDaiPlugin)}`);
      if (isDaiPlugin) {
        console.log(`isDaiPlugin = true`);
        this.setState(() => ({
          isDAIPluginAvailable: true,
        }));
      }
    }
    
    this.subscribeToAppLifecyle();
    //this.player.enableDebugLogs(true);

    setupKalturaPlayer(
      this.player,
      playerType,
      options != null ? JSON.stringify(options) : "",
      JSON.stringify(asset),
      mediaId,
      partnerId
    ).then((_) => {
      // Subscribe to Player Events
      this.subscribeToPlayerListeners()

      if (this.state.isDAIPluginAvailable) {
        this.startDAIPositionTimer();
      }
    }).catch((error) => {
      console.error(`${error}`);
    });
  }

  componentWillUnmount() {
    this._isMounted = false;
    if (this.progressInterval != null) {
      clearInterval(this.progressInterval);
    }
    this.removePlayerListeners();
    if (networkUnsubscribe != null) {
      networkUnsubscribe();
    }
    this.player.removeListeners();
    if (this.appStateSubscription != null) {
      this.appStateSubscription.remove();
    }
    this.player.destroy();
    playerType = null;
    console.log('componentWillUnmount from App.');
  }

  getPlayerCurrentPosition(): any {
    this.player.getCurrentPosition().then((value: any) => {
      //console.log(`getPlayerCurrentPosition ${value}`);
      if (value >= 0) {
        this.setState(() => ({
          currentPosition: value,
        }));
      }
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
      return;
    }
    this.player.setPlaybackRate(rate);
  };

  changeAspectRatio = (resizeMode: PLAYER_RESIZE_MODES) => {
    this.player.updateResizeMode(resizeMode);
  };

  changeSubtitleStyling = () => {
    if (this.state.isAdPlaying) {
      showToast('Subtitle Styling can not be changed when Ad is playing');
      return;
    }
    this.player.updateSubtitleStyle(JSON.stringify(updatedSubtitleStyling));
  };

  onTrackChangeListener = (trackId: string) => {
    if (this.state.isAdPlaying) {
      showToast('Track can not be changed when Ad is playing');
      return;
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

  removePlayerListeners = () => {
    if (eventsSubscriptionList.length > 0) {
      eventsSubscriptionList.forEach((event) => {
        console.log(`removing player subscription ${event}`);
        event.remove();
      });
    }
  };

  addRemovePlayerView = () => {
    if (this.state.isPlayerViewVisible) {
      this.player.removePlayerView();
    } else {
      this.player.addPlayerView();
    }
    this.setState(() => ({
      isPlayerViewVisible: !this.state.isPlayerViewVisible,
      btnTextPlayerView: ((this.state.isPlayerViewVisible === false) ? BTN_REMOVE_VIEW_TXT : BTN_ADD_VIEW_TXT)
    }));
  };

  startDAIPositionTimer() {
    this.progressInterval = setInterval(() => {
      //console.log(`Inside progressInterval timer`);
      this.getPlayerCurrentPosition();
    }, timerInterval);
  }

  /**
   * Add the Kaltura Player listeners to
   * add the Player, Ad and other Analytics
   * events
   *
   */
  subscribeToPlayerListeners = () => {
    eventsSubscriptionList.push(
      playerEventEmitter.addListener(
        PlayerEvents.DURATION_CHANGE,
        (payload) => {
          console.log(
            'PlayerEvent DURATION_CHANGE : ' +
            (payload.duration != null
              ? payload.duration
              : ' Empty duration change')
          );

          if (payload.duration != null) {
            this.contentDuration = payload.duration;
          }
        }
      )
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(
        PlayerEvents.PLAYHEAD_UPDATED,
        (payload) => {
          //console.log('PlayerEvent PLAYHEAD_UPDATED position : ' + payload.position + ' bufferPosition: ' + payload.bufferPosition);
          if (
            this._isMounted &&
            !this.isSliderSeeking &&
            !this.state.isAdPlaying
          ) {
            if (!this.state.isDAIPluginAvailable) {
              this.setState(() => ({
                currentPosition: payload.position,
              }));
            }

            this.setState(() => ({
              totalDuration: this.contentDuration,
            }));
          }
        }
      )
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(PlayerEvents.PLAY, () => {
        console.log('PlayerEvent PLAY');
        if (this._isMounted) {
          this.setState(() => ({
            isContentPlaying: true,
          }));
        }
      })
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(PlayerEvents.PAUSE, () => {
        console.log('PlayerEvent PAUSE');
        if (this._isMounted) {
          this.setState(() => ({
            isContentPlaying: false,
          }));
        }
      })
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(PlayerEvents.VOLUME_CHANGED, (payload) => {
        console.log(`PlayerEvent VOLUME_CHANGED ${payload.volume}`);
      })
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(
        PlayerEvents.LOAD_TIME_RANGES,
        (payload) => {
          console.log('PlayerEvent LOAD_TIME_RANGES : ' + JSON.stringify(payload));
        }
      )
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(PlayerEvents.ERROR, (payload) => {
        console.log('PlayerEvent ERROR : ' + JSON.stringify(payload));
      })
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(AdEvents.ERROR, (payload) => {
        console.log(`AdEvent ERROR = ${JSON.stringify(payload)}`);
      })
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(
        PlayerEvents.SOURCE_SELECTED,
        (payload) => {
          console.log(
            'PlayerEvent SOURCE_SELECTED : ' + JSON.stringify(payload)
          );
        }
      )
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(
        PlayerEvents.PLAYBACK_INFO_UPDATED,
        (payload) => {
          if (Platform.OS === platform_android) {
            // console.log(
            //   'PlayerEvent PLAYBACK_INFO_UPDATED : ' + JSON.stringify(payload)
            // );
          } else if (Platform.OS === platform_ios) {
            //TODO: for iOS
          }
        }
      )
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(
        PlayerEvents.METADATA_AVAILABLE,
        (payload) => {
          if (Platform.OS === platform_android) {
            console.log(
              'PlayerEvent METADATA_AVAILABLE : ' + JSON.stringify(payload)
            );
          } else if (Platform.OS === platform_ios) {
            //TODO: for iOS
          }
        }
      )
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(
        PlayerEvents.EVENT_STREAM_CHANGED,
        (payload) => {
          if (Platform.OS === platform_android) {
            console.log(
              'PlayerEvent EVENT_STREAM_CHANGED : ' + JSON.stringify(payload)
            );
          } else if (Platform.OS === platform_ios) {
            //TODO: for iOS
          }
        }
      )
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(
        PlayerEvents.TRACKS_AVAILABLE,
        (payload) => {
          console.log(
            'PlayerEvent TRACKS_AVAILABLE : ' + JSON.stringify(payload)
          );
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
        }
      )
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(
        PlayerEvents.DRM_INITIALIZED,
        (payload) => {
          console.log(
            'PlayerEvent DRM_INITIALIZED : ' + JSON.stringify(payload)
          );
        }
      )
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(
        PlayerEvents.LOAD_TIME_RANGES,
        (payload) => {
          console.log('PlayerEvent LOAD_TIME_RANGES : ' + JSON.stringify(payload));
        }
      )
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(
        PlayerEvents.VIDEO_TRACK_CHANGED,
        (payload) => {
          console.log('PlayerEvent VIDEO_TRACK_CHANGED : ' + JSON.stringify(payload));
        }
      )
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(
        PlayerEvents.AUDIO_TRACK_CHANGED,
        (payload) => {
          console.log('PlayerEvent AUDIO_TRACK_CHANGED : ' + JSON.stringify(payload));
        }
      )
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(
        PlayerEvents.TEXT_TRACK_CHANGED,
        (payload) => {
          console.log('PlayerEvent TEXT_TRACK_CHANGED : ' + JSON.stringify(payload));
        }
      )
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(AdEvents.CONTENT_PAUSE_REQUESTED, (_) => {
        console.log('AdEvent CONTENT_PAUSE_REQUESTED');
        if (this._isMounted) {
          this.setState(() => ({
            isAdPlaying: true,
            isContentPlaying: false,
          }));
        }
      })
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(AdEvents.CONTENT_RESUME_REQUESTED, (_) => {
        console.log('AdEvent CONTENT_RESUME_REQUESTED');
        if (this._isMounted) {
          this.setState(() => ({
            isAdPlaying: false,
            isContentPlaying: true,
          }));
        }
      })
    );

    eventsSubscriptionList.push(
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
      })
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(AdEvents.AD_PROGRESS, (payload) => {
        console.log('AdEvent AD_PROGRESS : ' + payload.currentAdPosition);
        if (this._isMounted && payload.currentAdPosition != null) {
          if (!this.state.isDAIPluginAvailable) {
            this.setState(() => ({
              currentPosition: payload.currentAdPosition,
            }));
          }
        }
      })
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(AdEvents.CUEPOINTS_CHANGED, (payload) => {
        console.log('AdEvent CUEPOINTS_CHANGED : ' + JSON.stringify(payload));
      })
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(PlayerEvents.STATE_CHANGED, (payload) => {
        console.log('PlayerEvents STATE_CHANGED : ' + JSON.stringify(payload.newState));
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
      })
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(AdEvents.AD_BUFFER_START, () => {
        console.log('AdEvent AD_BUFFER_START');
        if (this._isMounted) {
          this.setState(() => ({
            isShowing: true,
          }));
        }
      })
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(AdEvents.AD_BUFFER_END, () => {
        console.log('AdEvent AD_BUFFER_END');
        if (this._isMounted) {
          this.setState(() => ({
            isShowing: false,
          }));
        }
      })
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(AnalyticsEvents.BROADPEAK_ERROR, (payload) => {
        console.log('AnalyticsEvents BROADPEAK_ERROR is ' + JSON.stringify(payload));
        console.log('AnalyticsEvents BROADPEAK_ERROR errorCode is ' + payload.errorCode);
        console.log('AnalyticsEvents BROADPEAK_ERROR errorMessage is ' + payload.errorMessage);
      })
    );

    eventsSubscriptionList.push(
      playerEventEmitter.addListener(AnalyticsEvents.YOUBORA_REPORT_SENT, (payload) => {
        console.log('AnalyticsEvents YOUBORA_REPORT_SENT is ' + JSON.stringify(payload));
      })
    );
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

          <Dropdown
            style={styles.dropdown}
            selectedTextStyle={styles.selectedTextStyle}
            iconStyle={styles.iconStyle}
            maxHeight={200}
            data={aspectRatios}
            valueField="ratio"
            labelField="name"
            placeholder="Aspect Ratio"
            onChange={(ratios) => {
              console.log('Selected Aspect ratio is: ' + ratios.ratio);
              {
                this.changeAspectRatio(ratios.ratio);
              }
            }}
          />

          {this.state.isChangeMediaAvailable ? (
            <TouchableOpacity
              style={[styles.button]}
              onPress={() => {
                changeMedia(this.props.incomingJson, this.player, this.state.currentChangeMediaIndex);
                this.setState(() => ({
                  currentChangeMediaIndex: (this.state.currentChangeMediaIndex + 1),
                }));
              }}
            >
              <Text style={[styles.bigWhite]}>Play Next Media</Text>
            </TouchableOpacity>
          ) : (
            <></> // Empty JSX
          )}
        </ScrollView>
      </RootSiblingParent>
    );
  }
}

function updatePluginConfigsIfAvailable(pluginJson: typeof MediaListModel.plugins, player: KalturaPlayerAPI) {
  player.updatePluginConfigs(pluginJson);
}

function changeMedia(playbackJson: object, player: KalturaPlayerAPI, mediaIndex: number) {
  var asset = null;
  var mediaId = null;

  var currentChangeMediaIndex = mediaIndex + 1;
  var mediaList: Array<typeof MediaListModel> = playbackJson.mediaList;

  if (currentChangeMediaIndex >= mediaList.length) {
    showToast(`We have played all the available medias.`);
    return;
  }

  if (mediaList != null && mediaList.length > 0) {
    asset = mediaList[currentChangeMediaIndex].mediaAsset;
    mediaId = mediaList[currentChangeMediaIndex].mediaId;
    console.log(`Change media asset is ${JSON.stringify(asset)} `);
    console.log(`Change media mediaId is ${mediaId} `);
    if (!mediaId) {
      showToast(`Next item can not be played`);
      return;
    }
  } else {
    showToast(`Next item can not be played`);
    return;
  }

  var pluginJson = mediaList[currentChangeMediaIndex].plugins;
  console.log(`pluginJson is ${JSON.stringify(pluginJson)}`);
  // Update the plugin configs
  updatePluginConfigsIfAvailable(pluginJson, player);

  // Load the media
  loadMediaToKalturaPlayer(player, mediaId, JSON.stringify(asset));
}

function loadMediaToKalturaPlayer(player: KalturaPlayerAPI, mediaId: String, mediaAsset: String) {
  player
    .loadMedia(mediaId, mediaAsset)
    .then((response: any) => console.log(`mediaLoaded => ${response}`)).catch((error: any) => {
      console.error(`${error}`);
    });
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
      loadMediaToKalturaPlayer(player, mediaId, mediaAsset);
    } else {
      console.error('Player is not created.');
    }
  } catch (err) {
    console.log(`setupKalturaPlayer ${err}`);
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

const aspectRatios = [
  {
    name: 'Fit',
    ratio: PLAYER_RESIZE_MODES.FIT,
  },
  {
    name: 'Fixed Width',
    ratio: PLAYER_RESIZE_MODES.FIXED_WIDTH,
  },
  {
    name: 'Fixed Height',
    ratio: PLAYER_RESIZE_MODES.FIXED_HEIGHT,
  },
  {
    name: 'Fill',
    ratio: PLAYER_RESIZE_MODES.FILL,
  },
  {
    name: 'Zoom',
    ratio: PLAYER_RESIZE_MODES.ZOOM,
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
