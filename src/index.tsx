import {
  requireNativeComponent,
  NativeModules,
  ViewStyle,
  NativeEventEmitter,
} from 'react-native';
import React from 'react';
import PropTypes from 'prop-types';

export const KalturaPlayerModule = NativeModules.KalturaPlayerViewManager;
const KalturaPlayerEvents = NativeModules.KalturaPlayerEvents;
export const KalturaPlayerEmitter = new NativeEventEmitter(KalturaPlayerEvents);

const RNKalturaPlayer = requireNativeComponent('KalturaPlayerView');

interface KalturaPlayerProps {
  style: ViewStyle
}

export class KalturaPlayer extends React.Component<KalturaPlayerProps> {
  nativeComponentRef: any;
  eventListeners: any[] = [];

  componentDidMount() {
    const supportedEvents = [
      "canPlay",
      "durationChanged",
      "stopped",
      "ended",
      "loadedMetadata",
      "play",
      "pause",
      "playing",
      "seeking",
      "seeked",
      "replay",
      "tracksAvailable",
      "textTrackChanged",
      "audioTrackChanged",
      "videoTrackChanged",
      "playbackInfo",
      "stateChanged",
      "timedMetadata",
      "sourceSelected",
      "loadedTimeRanges",
      "playheadUpdate",
      "error",
      "errorLog",
      "playbackStalled",
      "playbackRate",
      "timeUpdate",
      "bookmarkError",
      "loadMediaFailed",
      "loadMediaSuccess",
      "concurrencyError",
    ]
    
    supportedEvents.forEach(type => {
      this.eventListeners.push(KalturaPlayerEmitter.addListener(type, (event: any) => {
        KalturaPlayerEmitter.emit('KPlayerEvent', { type, ...event })
      }));
    })
  }

  componentWillUnmount() {
    this.eventListeners.forEach(event => event.remove());
  }

  static propTypes: {
    style: object;
  };

  render() {
    return <RNKalturaPlayer {...this.props} ref={(nativeRef) => this.nativeComponentRef = nativeRef} />;
  }
}

KalturaPlayer.propTypes = {
  style: PropTypes.object
};

export class KalturaPlayerAPI {
  static setup = (partnerId: number, options: {
    preload: boolean
    autoplay: boolean
    serverUrl: string
    ks?: string
  }) => {
    return KalturaPlayerModule.setup(partnerId, options)
  }
  static load = (assetId: string, options: {
    autoplay: boolean
    assetType: "media" | "recording" | "epg"
    protocol: "http" | "https"
    playbackContextType?: "playback" | "catchup" | "trailer" | "startOver"
    assetReferenceType?: "media" | "epgInternal" | "epgExternal" | "npvr"
    urlType?: string
    format?: string[]
    fileId?: string[]
    streamerType?: string
    startPosition?: number
  }) => {
    return KalturaPlayerModule.load(assetId, options);
  }
  static destroy = () => {
    return KalturaPlayerModule.destroy();
  }
  static setVolume = (volume: number) => {
    return KalturaPlayerModule.setVolume(volume);
  }
  static seekTo = (position: number) => {
    return KalturaPlayerModule.seekTo(position);
  }
  static setPlayerVisibility = (isVisible: boolean) => {
    return KalturaPlayerModule.setPlayerVisibility(isVisible);
  }
  static setKeepAwake = (value: boolean) => {
    return KalturaPlayerModule.setKeepAwake(value);
  }
  static play = () => {
    return KalturaPlayerModule.play();
  }
  static replay = () => {
    return KalturaPlayerModule.replay();
  }
  static pause = () => {
    return KalturaPlayerModule.pause();
  }
  static stop = () => {
    return KalturaPlayerModule.stop();
  }
  static changeTrack = (trackId: string) => {
    return KalturaPlayerModule.changeTrack(trackId);
  }
  static setMaxBitrate = (bitrate: number) => {
    return KalturaPlayerModule.setMaxBitrate(bitrate);
  }
  static prepare = () => {
    return KalturaPlayerModule.prepare();
  }
  static setAutoplay = (value: boolean) => {
    return KalturaPlayerModule.setAutoplay(value);
  }
}
