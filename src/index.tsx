import { requireNativeComponent, ViewStyle } from 'react-native';
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

const RNKalturaPlayer = requireNativeComponent('KalturaPlayerView');

interface KalturaPlayerProps {
  style: ViewStyle;
  playerType: PLAYER_TYPE;
}

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

export class KalturaPlayer extends React.Component<KalturaPlayerProps> {
  nativeComponentRef: any;
  eventListener: any;
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

  setNativeProps = (nativeProps: any) => {
    this.nativeComponentRef.setNativeProps(nativeProps);
  };

  /**
   * This method creates a Player instance internally (Basic, OVP/OTT Player)
   * With this, it take the PlayerInitOptions which are having essential Player settings values
   *
   * @param options PlayerInitOptions JSON String
   * @param id PartnerId (Don't pass this parameter for BasicPlayer. For OVP/OTT player this value
   * should be always greater than 0 and should be valid otherwise, we will not be able to featch the details
   * for the mediaId or the entryId)
   */
  setup = (options: string, id: number = 0) => {
    if (!options) {
      console.error(`setup, invalid options = ${options}`);
      return;
    }
    console.log('Setting up the Player');
    this.setNativeProps({ partnerId: id });
    this.setNativeProps({ playerInitOptions: options });
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
  loadMedia = (id: string, asset: string) => {
    if (!id || !asset) {
      console.error(`loadMedia, invalid id = ${id} or asset = ${asset}`);
      return;
    }

    console.log(
      `Loading the media. assetId is: ${id} and media asset is: ${asset}`
    );
    this.setNativeProps({ assetId: id });
    this.setNativeProps({ mediaAsset: asset });
    this.setNativeProps({ load: true });
  };

  /**
   * Add the listners for the Kaltura Player
   */
  addListeners = () => {
    console.log('Calling Native Prop addListeners()');
    this.setNativeProps({ addListeners: true });
  };

  /**
   * Add the listners for the Kaltura Player
   */
  removeListeners = () => {
    console.log('Calling Native Prop removeListeners()');
    this.setNativeProps({ removeListeners: true });
  };

  /**
   * Should be called when the application is in background
   */
  onApplicationPaused = () => {
    console.log('Calling Native Prop onApplicationPaused()');
    this.setNativeProps({ onApplicationPaused: true });
  };

  /**
   * Should be called when the application comes back to
   * foreground
   */
  onApplicationResumed = () => {
    console.log('Calling Native Prop onApplicationResumed()');
    this.setNativeProps({ onApplicationResumed: true });
  };

  /**
   * Update a Plugin Config
   *
   * @param pluginName Plugin Name (Youbora, IMA etc)
   * @param config Updated Plugin Config (YouboraConfig JSON, IMAConfig JSON etc)
   */
  updatePluginConfig = (pluginName: PLAYER_PLUGIN, config: object) => {
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
    this.setNativeProps({ updatePluginConfig: stringifiedJson });
  };

  /**
   * Play the player if it is not playing
   */
  play = () => {
    console.log('Calling Native Prop play()');
    this.setNativeProps({ play: true });
  };

  /**
   * Pause the player if it is playing
   */
  pause = () => {
    console.log('Calling Native Prop pause()');
    this.setNativeProps({ pause: true });
  };

  /**
   * Stops the player to the initial state
   */
  stop = () => {
    console.log('Calling Native Prop stop()');
    this.setNativeProps({ stop: true });
  };

  /**
   * Destroy the Kaltura Player instance
   */
  destroy = () => {
    console.log('Calling Native Prop destroy()');
    this.setNativeProps({ onDestroy: true });
  };

  /**
   * Replays the media from the beginning
   */
  replay = () => {
    console.log('Calling Native Prop replay()');
    this.setNativeProps({ replay: true });
  };

  /**
   * Seek the player to the specified position
   * @param position in miliseconds (Ms)
   */
  seekTo = (position: number) => {
    console.log(`Calling Native Prop seekTo() position is: ${position}`);
    this.setNativeProps({ seek: position });
  };

  /**
   * Change a specific track (Video, Audio or Text track)
   * @param trackId Unique track ID which was sent in `tracksAvailable` event
   */
  changeTrack = (trackId: string) => {
    if (!trackId) {
      console.error(`trackId is invalid which is: ${trackId}`);
      return;
    }
    console.log('Calling Native Prop changeTrack()');
    this.setNativeProps({ changeTrack: trackId });
  };

  /**
   * Change the playback rate (ff or slow motion). Default is 1.0f
   * @param rate Desired playback rate (Ex: 0.5f, 1.5f 2.0f etc)
   */
  setPlaybackRate = (rate: number) => {
    console.log(`Calling Native Prop setPlaybackRate() rate is: ${rate}`);
    this.setNativeProps({ playbackRate: rate });
  };

  /**
   * Change the volume of the current audio track.
   * Accept values between 0.0 and 1.0. Where 0.0 is mute and 1.0 is maximum volume.
   * If the volume parameter is higher then 1.0, it will be converted to 1.0.
   * If the volume parameter is lower then 0.0, it be converted to 0.0.
   *
   * @param vol - volume to set.
   */
  setVolume = (vol: number) => {
    console.log('Calling Native Prop setVolume()');
    this.setNativeProps({ volume: vol });
  };

  /**
   * Set the media to play automatically at the start (load)
   * if false, user will have to click on UI play button
   *
   * @param isAutoPlay media should be autoplayed at the start or not
   */
  setAutoPlay = (isAutoPlay: boolean) => {
    console.log('Calling Native Prop setAutoPlay()');
    this.setNativeProps({ autoPlay: isAutoPlay });
  };

  /**
   * Set the KS for the media (only for OVP/OTT users)
   * Call this before calling {@link loadMedia}
   * @param KS Kaltura Secret key
   */
  setKS = (KS: string) => {
    if (!KS) {
      console.error('KS is invalid which is: ' + KS);
      return;
    }
    console.log('Calling Native Prop setKS()');
    this.setNativeProps({ ks: KS });
  };

  /**
   * NOOP
   * @param index
   */
  setZIndex = (index: number) => {
    console.log('Calling Native Prop setZIndex()');
    this.setNativeProps({ zIndex: index });
  };

  /**
   * Only for Live Media.
   * Seek player to Live Default Position.
   */
  seekToLiveDefaultPosition = () => {
    console.log('Calling Native Prop seekToLiveDefaultPosition()');
    this.setNativeProps({ seekToLiveDefaultPosition: true });
  };

  /**
   * Update the existing subtitle styling
   */
  updateSubtitleStyle = (subtitleStyle: string) => {
    if (!subtitleStyle) {
      console.error(`subtitleStyle is invalid which is: ${subtitleStyle}`);
      return;
    }
    console.log('Calling Native Prop updateSubtitleStyle()');
    this.setNativeProps({ updateSubtitleStyle: subtitleStyle });
  };

  /**
   * Update the Resize Mode
   */
  updateResizeMode = (mode: PLAYER_RESIZE_MODES) => {
    console.log('Calling Native Prop updateSurfaceAspectRatioResizeMode()');
    this.setNativeProps({ updateSurfaceAspectRatioResizeMode: mode });
  };

  /**
   * Update the ABR Settings
   */
  updateAbrSettings = (abrSettings: string) => {
    if (!abrSettings) {
      console.error(`abrSettings is invalid which is: ${abrSettings}`);
      return;
    }
    console.log('Calling Native Prop updateABRSettings()');
    this.setNativeProps({ updateABRSettings: abrSettings });
  };

  /**
   * Reset the ABR Settings
   */
  resetAbrSettings = () => {
    console.log('Calling Native Prop resetABRSettings()');
    this.setNativeProps({ resetABRSettings: true });
  };

  /**
   * Update the Low Latency Config
   * Only for Live Media
   */
  updateLowLatencyConfig = (lowLatencyConfig: string) => {
    if (!lowLatencyConfig) {
      console.error(
        `lowLatencyConfig is invalid which is: ${lowLatencyConfig}`
      );
      return;
    }
    console.log('Calling Native Prop updateLowLatencyConfig()');
    this.setNativeProps({ updateLowLatencyConfig: lowLatencyConfig });
  };

  /**
   * Reset the Low Latency Config
   * Only for Live Media
   */
  resetLowLatencyConfig = () => {
    console.log('Calling Native Prop resetLowLatencyConfig()');
    this.setNativeProps({ resetLowLatencyConfig: true });
  };

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
