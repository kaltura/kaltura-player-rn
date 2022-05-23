import { requireNativeComponent } from 'react-native';
import React from 'react';
import PropTypes from 'prop-types';
import PlayerEvents from './events/PlayerEvents';

const RNKalturaPlayer = requireNativeComponent('KalturaPlayerView');

export default PlayerEvents;

export enum PLAYER_TYPE {
  OVP = 'ovp',
  OTT = 'ott',
  BASIC = 'basic',
}

export enum MEDIA_FORMAT {
  DASH = 'dash',
  HLS = 'hls',
  WVM = 'wvm',
  MP4 = 'mp4',
  MP3 = 'mp3',
  UDP = 'udp',
}

export enum MEDIA_ENTRY_TYPE {
  VOD = 'Vod',
  LIVE = 'Live',
  DVRLIVE = 'DvrLive',
}

export enum DRM_SCHEME {
  WIDEVINE_CENC = 'WidevineCENC',
  PLAYREADY_CENC = 'PlayReadyCENC',
  WIDEVINE_CENC_CLASSIC = 'WidevineClassic',
  PLAYREADY_CLASSIC = 'PlayReadyClassic',
}

export enum PLAYER_PLUGIN {
  IMA = 'ima',
  IMADAI = 'imadai',
  YOUBORA = 'youbora',
  KAVA = 'kava',
  OTT_ANALYTICS = 'ottAnalytics',
  BROADPEAK = 'broadpeak',
}

export class KalturaPlayer extends React.Component {
  nativeComponentRef: any;
  eventListener: any;
  
  static propTypes: { 
    style: PropTypes.Requireable<object>;
    playerType: PropTypes.Requireable<String>;
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
   * This method creates a Player instance internally (Basic, OVP/OTT Player)
   * With this, it take the PlayerInitOptions which are having essential Player settings values
   *
   * @param options PlayerInitOptions JSON String
   * @param id PartnerId (Don't pass this parameter for BasicPlayer. For OVP/OTT player this value
   * should be always greater than 0 and should be valid otherwise, we will not be able to featch the details
   * for the mediaId or the entryId)
   */
  setup = (options: string, id: number = 0) => {
    console.log('Setting up the Player');
    this.setNativeProps({ partnerId: id });
    this.setNativeProps({ playerInitOptions: options });
  };

  /**
   * Update a Plugin Config
   *
   * @param pluginName Plugin Name (Youbora, IMA etc)
   * @param config Updated Plugin Config (YouboraConfig JSON, IMAConfig JSON etc)
   */
  updatePluginConfig = (pluginName: PLAYER_PLUGIN, config: object) => {
    const pluginJson = {
      pluginName: pluginName,
      pluginConfig: config,
    };
    const stringifiedJson = JSON.stringify(pluginJson);
    console.log('Updated Plugin is: ' + stringifiedJson);
    this.setNativeProps({ updatePluginConfig: stringifiedJson });
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
    console.log(
      'Loading the media. assetId: ' + id + ' and Media asset id: ' + asset
    );
    this.setNativeProps({ assetId: id });
    this.setNativeProps({ mediaAsset: asset });
    this.setNativeProps({ load: true });
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
    console.log('Calling Native Prop seekTo()');
    this.setNativeProps({ seek: position });
  };

  /**
   * Change a specific track (Video, Audio or Text track)
   * @param trackId Unique track ID which was sent in `tracksAvailable` event
   */
  changeTrack = (trackId: string) => {
    console.log('Calling Native Prop changeTrack()');
    this.setNativeProps({ changeTrack: trackId });
  };

  /**
   * Change the playback rate (ff or slow motion). Default is 1.0f
   * @param rate Desired playback rate (Ex: 0.5f, 1.5f 2.0f etc)
   */
  setPlaybackRate = (rate: number) => {
    console.log('Calling Native Prop setPlaybackRate()');
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

  setAutoPlay = (isAutoPlay: boolean) => {
    console.log('Calling Native Prop setAutoPlay()');
    this.setNativeProps({ autoPlay: isAutoPlay });
  };

  setKS = (KS: string) => {
    console.log('Calling Native Prop setKS()');
    this.setNativeProps({ ks: KS });
  };

  setZIndex = (index: number) => {
    console.log('Calling Native Prop setZIndex()');
    this.setNativeProps({ zIndex: index });
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
  /**
   * The Styling of the player.
   */
  style: PropTypes.object,

  /**
   * A String value that determines the player type.
   * Basic / OVP / OTT Player
   * Use PLAYER_TYPE.
   */
  playerType: PropTypes.string
};
