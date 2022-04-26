import {
  requireNativeComponent,
  ViewStyle,
} from 'react-native';
import React from 'react';
import PropTypes from 'prop-types';

const RNKalturaPlayer = requireNativeComponent('KalturaPlayerView');

interface KalturaPlayerProps {
  style: ViewStyle
}

export class KalturaPlayer extends React.Component<KalturaPlayerProps> {
  nativeComponentRef: any;
  eventListeners: any[] = [];

  componentDidMount() {
    const supportedEvents = [
      "canPlay", "durationChanged", "stopped", "ended", "loadedMetadata", "play", "pause", "playing", "seeking", "seeked", "replay",
      "tracksAvailable", "textTrackChanged", "audioTrackChanged", "videoTrackChanged", "playbackInfo", "stateChanged",
      "timedMetadata", "sourceSelected", "loadedTimeRanges", "playheadUpdate", "error", "errorLog", "playbackStalled", "playbackRate"
    ];
    supportedEvents.forEach(type => {
      this.eventListeners.push(KalturaPlayerEmitter.addListener(type, (event: any) => {
        KalturaPlayerEmitter.emit('KPlayerEvent', { type, ...event })
      }));
    })
    console.log("componentDidMount from Library.");
  }

  componentWillUnmount() {
    this.eventListeners.forEach(event => event.remove());
    console.log("componentWillUnmount from Library");
  }

  static propTypes: {
    style: object
  };

  setNativeProps = (nativeProps: any) => {
    this.nativeComponentRef.setNativeProps(nativeProps);
  }

  addListeners = () => {
    console.log("Calling Native Prop addListeners()")
    this.setNativeProps({ addListeners: true });
  }

  removeListeners = () => {
    console.log("Calling Native Prop removeListeners()")
    this.setNativeProps({ removeListeners: true });
  }

  setup = (id: number, options: string) => {
    console.log("Setting up the Kaltura Player")
    this.setNativeProps({ partnerId: id });
    this.setNativeProps({ playerInitOptions: options });
  }

  load = (id: string, asset: string) => {
    console.log("Loading the media.")
    this.setNativeProps({ assetId: id });
    this.setNativeProps({ mediaAsset: asset });
    this.setNativeProps({ load: true });
  }

  play = () => {
    console.log("Calling Native Prop play()")
    this.setNativeProps({ play: true });
  };

  pause = () => {
    console.log("Calling Native Prop pause()")
    this.setNativeProps({ pause: true });
  };

  stop = () => {
    console.log("Calling Native Prop stop()")
    this.setNativeProps({ stop: true });
  }

  replay = () => {
    console.log("Calling Native Prop replay()")
    this.setNativeProps({ replay: true });
  }

  seekTo = (position: number) => {
    console.log("Calling Native Prop seekTo()")
    this.setNativeProps({ seek: position });
  }

  changeTrack = (trackId: string) => {
    console.log("Calling Native Prop changeTrack()")
    this.setNativeProps({ changeTrack: trackId })
  }

  setPlaybackRate = (rate: number) => {
    console.log("Calling Native Prop setPlaybackRate()")
    this.setNativeProps({ playbackRate: rate })
  }

  setVolume = (vol: number) => {
    console.log("Calling Native Prop setVolume()")
    this.setNativeProps({ volume: vol })
  }

  setAutoPlay = (isAutoPlay: boolean) => {
    console.log("Calling Native Prop setAutoPlay()")
    this.setNativeProps({ autoPlay: isAutoPlay})
  }

  setKS = (KS: string) => {
    console.log("Calling Native Prop setKS()")
    this.setNativeProps({ ks: KS })
  }

  setZIndex = (index: number) => {
    console.log("Calling Native Prop setZIndex()")
    this.setNativeProps({ zIndex: index })
  }

  render() {
    return <RNKalturaPlayer {...this.props} ref={(nativeRef) => this.nativeComponentRef = nativeRef} />;
  }
}

KalturaPlayer.propTypes = {
  style: PropTypes.object,
};


