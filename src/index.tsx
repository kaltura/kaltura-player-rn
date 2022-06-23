import { requireNativeComponent, NativeModules, ViewStyle } from 'react-native';
import React from 'react';
import PropTypes from 'prop-types';
import { PlayerEvents } from './events/PlayerEvents';
import { AdEvents } from './events/AdEvents';
import { AnalyticsEvents } from './events/AnalyticsEvents';
import {
  PLAYER_TYPE,
  MEDIA_FORMAT,
  MEDIA_ENTRY_TYPE,
  DRM_SCHEME,
  PLAYER_PLUGIN,
  PLAYER_RESIZE_MODES,
  WAKEMODE,
  SUBTITLE_STYLE,
  SUBTITLE_PREFERENCE,
  VIDEO_CODEC,
  AUDIO_CODEC,
  VR_INTERACTION_MODE,
} from './consts';

export {
  PlayerEvents,
  AdEvents,
  AnalyticsEvents,
  PLAYER_TYPE,
  MEDIA_FORMAT,
  MEDIA_ENTRY_TYPE,
  DRM_SCHEME,
  PLAYER_PLUGIN,
  PLAYER_RESIZE_MODES,
  WAKEMODE,
  SUBTITLE_STYLE,
  SUBTITLE_PREFERENCE,
  VIDEO_CODEC,
  AUDIO_CODEC,
  VR_INTERACTION_MODE,
};

const RNKalturaPlayer = requireNativeComponent('KalturaPlayerView');
const { KalturaPlayerModule } = NativeModules;

const POSITION_UNSET: number = -1;

interface KalturaPlayerProps {
  style: ViewStyle;
  playerType: PLAYER_TYPE;
}

export class KalturaPlayer extends React.Component<KalturaPlayerProps> {
  nativeComponentRef: any;
  playerType: PLAYER_TYPE | undefined;

  static propTypes: {
    style: object;
  };

  componentDidMount() {
    console.log('componentDidMount from Library.');
  }

  componentWillUnmount() {
    console.log('componentWillUnmount from Library');
  }

  render() {
    return (
      <RNKalturaPlayer
        {...this.props}
        ref={(nativeRef) => (this.nativeComponentRef = nativeRef)}
      />
    );
  }
}

KalturaPlayer.propTypes = {
  style: PropTypes.object,
};

export class KalturaPlayerAPI {
  /**
   * This method creates a Player instance internally (Basic, OVP/OTT Player)
   * With this, it take the PlayerInitOptions which are having essential Player settings values
   *
   * @param playerType The Player Type, Basic/OVP/OTT.
   * @param options PlayerInitOptions JSON String.
   * @param id PartnerId (Don't pass this parameter for BasicPlayer. For OVP/OTT player this value
   * should be always greater than 0 and should be valid otherwise, we will not be able to featch the details
   * for the mediaId or the entryId)
   */
  static setup = async (playerType: PLAYER_TYPE, options: string, id: number = 0) => {
    if (playerType == null) {
      console.error(`Invalid playerType = ${playerType}`);
      return;
    }

    if (!options) {
      console.error(`setup, invalid options = ${options}`);
      return;
    }
    console.log('Setting up the Player');
    return await setupKalturaPlayer(playerType, options, id);
  };

  /**
   * Load the media with the given
   *
   * assetId OR mediaId OR entryID for OVP/OTT Kaltura Player
   *
   * playbackURL for Basic Kaltura Player
   *
   * @param id Playback URL for Kaltura Basic Player OR
   * MediaId for Kaltura OTT Player OR
   * EntryId for Kaltura OVP Player
   * @param asset Media Asset JSON String
   */
  static loadMedia = (id: string, asset: string) => {
    if (!id || !asset) {
      console.error(`loadMedia, invalid id = ${id} or asset = ${asset}`);
      return;
    }

    console.log(
      `Loading the media. assetId is: ${id} and media asset is: ${asset}`
    );

    KalturaPlayerModule.load(id, asset);
  };

  /**
   * Add the listners for the Kaltura Player
   */
  static addListeners = () => {
    console.log('Calling Native Prop addListeners()');
    KalturaPlayerModule.addKalturaPlayerListeners();
  };

  /**
   * Add the listners for the Kaltura Player
   */
  static removeListeners = () => {
    console.log('Calling Native Prop removeListeners()');
    KalturaPlayerModule.removeKalturaPlayerListeners();
  };

  /**
   * Should be called when the application is in background
   */
  static onApplicationPaused = () => {
    console.log('Calling Native Prop onApplicationPaused()');
    KalturaPlayerModule.onApplicationPaused();
  };

  /**
   * Should be called when the application comes back to
   * foreground
   */
  static onApplicationResumed = () => {
    console.log('Calling Native Prop onApplicationResumed()');
    KalturaPlayerModule.onApplicationResumed();
  };

  /**
   * Update a Plugin Config
   *
   * @param pluginName Plugin Name (Youbora, IMA etc)
   * @param config Updated Plugin Config (YouboraConfig JSON, IMAConfig JSON etc)
   */
  static updatePluginConfig = (pluginName: PLAYER_PLUGIN, config: object) => {
    if (pluginName == null || !config) {
      console.error(
        `updatePluginConfig, either pluginName ${pluginName} OR config is invalid: ${config}`
      );
      return;
    }

    const pluginJson = {
      pluginName: pluginName,
      pluginConfig: config,
    };
    const stringifiedJson = JSON.stringify(pluginJson);
    console.log(`Updated Plugin is: ${stringifiedJson}`);

    KalturaPlayerModule.updatePluginConfigs(stringifiedJson);
  };

  /**
   * Play the player if it is not playing
   */
  static play = () => {
    console.log('Calling Native Prop play()');
    KalturaPlayerModule.play();
  };

  /**
   * Pause the player if it is playing
   */
  static pause = () => {
    console.log('Calling Native Prop pause()');
    KalturaPlayerModule.pause();
  };

  /**
   * Stops the player to the initial state
   */
  static stop = () => {
    console.log('Calling Native Prop stop()');
    KalturaPlayerModule.stop();
  };

  /**
   * Destroy the Kaltura Player instance
   */
  static destroy = () => {
    console.log('Calling Native Prop destroy()');
    KalturaPlayerModule.destroy();
  };

  /**
   * Replays the media from the beginning
   */
  static replay = () => {
    console.log('Calling Native Prop replay()');
    KalturaPlayerModule.replay();
  };

  /**
   * Seek the player to the specified position
   * @param position in miliseconds (Ms)
   */
  static seekTo = (position: number) => {
    console.log(`Calling Native Prop seekTo() position is: ${position}`);
    KalturaPlayerModule.seekTo(position);
  };

  /**
   * Change a specific track (Video, Audio or Text track)
   * @param trackId Unique track ID which was sent in `tracksAvailable` event
   */
  static changeTrack = (trackId: string) => {
    if (!trackId) {
      console.error(`trackId is invalid which is: ${trackId}`);
      return;
    }
    console.log('Calling Native Prop changeTrack()');
    KalturaPlayerModule.changeTrack(trackId);
  };

  /**
   * Change the playback rate (ff or slow motion). Default is 1.0f
   * @param rate Desired playback rate (Ex: 0.5f, 1.5f 2.0f etc)
   */
  static setPlaybackRate = (rate: number) => {
    console.log(`Calling Native Prop setPlaybackRate() rate is: ${rate}`);
    KalturaPlayerModule.changePlaybackRate(rate);
  };

  /**
   * Change the volume of the current audio track.
   * Accept values between 0.0 and 1.0. Where 0.0 is mute and 1.0 is maximum volume.
   * If the volume parameter is higher then 1.0, it will be converted to 1.0.
   * If the volume parameter is lower then 0.0, it be converted to 0.0.
   *
   * @param vol - volume to set.
   */
  static setVolume = (vol: number) => {
    console.log('Calling Native Prop setVolume()');
    KalturaPlayerModule.setVolume(vol);
  };

  /**
   * Set the media to play automatically at the start (load)
   * if false, user will have to click on UI play button
   *
   * @param isAutoPlay media should be autoplayed at the start or not
   */
  static setAutoPlay = (isAutoPlay: boolean) => {
    console.log('Calling Native Prop setAutoPlay()');
    KalturaPlayerModule.setAutoplay(isAutoPlay);
  };

  /**
   * Set the KS for the media (only for OVP/OTT users)
   * Call this before calling {@link loadMedia}
   * @param KS Kaltura Secret key
   */
  static setKS = (KS: string) => {
    if (!KS) {
      console.error('KS is invalid which is: ' + KS);
      return;
    }
    console.log('Calling Native Prop setKS()');
    KalturaPlayerModule.setKS(KS);
  };

  /**
   * NOOP
   * @param index
   */
  //static setZIndex = (index: number) => {
  //  console.log('Calling Native Prop setZIndex()');
  //};

  /**
   * Only for Live Media.
   * Seek player to Live Default Position.
   */
  static seekToLiveDefaultPosition = () => {
    console.log('Calling Native Prop seekToLiveDefaultPosition()');
    KalturaPlayerModule.seekToLiveDefaultPosition();
  };

  /**
   * Update the existing subtitle styling
   */
  static updateSubtitleStyle = (subtitleStyle: string) => {
    if (!subtitleStyle) {
      console.error(`subtitleStyle is invalid which is: ${subtitleStyle}`);
      return;
    }
    console.log('Calling Native Prop updateSubtitleStyle()');
    KalturaPlayerModule.updateSubtitleStyle(subtitleStyle);
  };

  /**
   * Update the Resize Mode
   */
  static updateResizeMode = (mode: PLAYER_RESIZE_MODES) => {
    console.log('Calling Native Prop updateSurfaceAspectRatioResizeMode()');
    KalturaPlayerModule.updateResizeMode(mode);
  };

  /**
   * Update the ABR Settings
   */
  static updateAbrSettings = (abrSettings: string) => {
    if (!abrSettings) {
      console.error(`abrSettings is invalid which is: ${abrSettings}`);
      return;
    }
    console.log('Calling Native Prop updateABRSettings()');
    KalturaPlayerModule.updateAbrSettings(abrSettings);
  };

  /**
   * Reset the ABR Settings
   */
  static resetAbrSettings = () => {
    console.log('Calling Native Prop resetABRSettings()');
    KalturaPlayerModule.resetAbrSettings();
  };

  /**
   * Update the Low Latency Config
   * Only for Live Media
   */
  static updateLowLatencyConfig = (lowLatencyConfig: string) => {
    if (!lowLatencyConfig) {
      console.error(
        `lowLatencyConfig is invalid which is: ${lowLatencyConfig}`
      );
      return;
    }
    console.log('Calling Native Prop updateLowLatencyConfig()');
    KalturaPlayerModule.updateLlConfig(lowLatencyConfig);
  };

  /**
   * Reset the Low Latency Config
   * Only for Live Media
   */
  static resetLowLatencyConfig = () => {
    console.log('Calling Native Prop resetLowLatencyConfig()');
    KalturaPlayerModule.resetLlConfig();
  };

  /**
   * Getter for the current playback position.
   * @returns string: Position of the player or {@link POSITION_UNSET}
   */
  static getCurrentPosition = async () => {
    console.log('Calling Native Prop getCurrentPosition()');
    return await getCurrentPosition();
  };

  /**
   * Checks if Player is currently playing or not
   * @returns boolean
   */
  static isPlaying = async () => {
    console.log('Calling Native Prop isPlaying');
    return await isPlaying();
  };

  /**
   * Checks if the stream is Live or Not
   * @returns boolean
   */
  static isLive = async () => {
    console.log('Calling Native Prop isLive');
    return await isLive();
  };
}

async function setupKalturaPlayer(playerType: PLAYER_TYPE, options: string, id: number) {
  try {
    const kalturaPlayerSetup = await KalturaPlayerModule.setUpPlayer(
      playerType,
      id,
      options
    );
    console.log(`Player is created: ${kalturaPlayerSetup}`);
    return kalturaPlayerSetup;
  } catch (e) {
    console.error(`Exception: ${e}`);
    return false;
  }
}

async function getCurrentPosition() {
  try {
    const currentPosition = await KalturaPlayerModule.getCurrentPosition();
    console.log(`Current Position: ${currentPosition}`);
    return currentPosition;
  } catch (e) {
    console.error(`Exception: ${e}`);
    return POSITION_UNSET;
  }
}

async function isPlaying() {
  try {
    const isPlayerPlaying = await KalturaPlayerModule.isPlaying();
    console.log(`isPlayerPlaying ${isPlayerPlaying}`);
    return isPlayerPlaying;
  } catch (e) {
    console.error(`Exception: ${e}`);
    return false;
  }
}

async function isLive() {
  try {
    const isPlayerLive = await KalturaPlayerModule.isLive();
    console.log(`isPlayerLive ${isPlayerLive}`);
    return isPlayerLive;
  } catch (e) {
    console.error(`Exception: ${e}`);
    return false;
  }
}
