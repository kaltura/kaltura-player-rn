import {
  requireNativeComponent,
  NativeModules,
  ViewStyle,
  NativeEventEmitter,
} from 'react-native';
import React from 'react';
import PropTypes, { Requireable } from 'prop-types';

export const KalturaPlayerModule = NativeModules.KalturaPlayerViewManager;
const KalturaPlayerEvents = NativeModules.KalturaPlayerEvents;
export const KalturaPlayerEmitter = new NativeEventEmitter(KalturaPlayerEvents);

const RNKalturaPlayer = requireNativeComponent('KalturaPlayerView');

console.log(KalturaPlayerEvents)
interface KalturaPlayerProps {
  style: ViewStyle,
  partnerId: number,
  assetId: string,
  playerInitOptions: string,
  mediaAsset: string
}

export class KalturaPlayer extends React.Component<KalturaPlayerProps> {
  nativeComponentRef: any;
  eventListener: any;

  componentDidMount() {
    // this.eventListener = KalturaPlayerEmitter.addListener('playing', (event: any) => {
    //   console.log(event)
    // });
   // console.log("componentDidMount");
    //this.prepare()
  }

  componentWillUnmount() {
    //this.eventListener.remove(); //Removes the listener
    console.log("componentWillUnmount");
  }

  static propTypes: {
    style: object,
    partnerId: Requireable<number>,
    assetId: Requireable<string>,
    prepare: Requireable<boolean>,
    playerInitOptions: Requireable<string>,
    mediaAsset: Requireable<string>;
  };

  addListeners = () => {
    console.log("Calling Native Prop addListeners()")
    this.nativeComponentRef.setNativeProps({ addListeners: true });
  }

  removeListeners = () => {
    console.log("Calling Native Prop removeListeners()")
    this.nativeComponentRef.setNativeProps({ removeListeners: true });
  }

  setAssetId = (id: string) => {
    console.log("Calling Native Prop assetId()")
    this.nativeComponentRef.setNativeProps({ assetId: id });
  }

  setPlayerInitOptions = (options: string) => {
    console.log("Calling Native Prop setPlayerInitOptions()")
    this.nativeComponentRef.setNativeProps({ playerInitOptions: options });
  }

  setMediaAsset = (asset: string) => {
    console.log("Calling Native Prop setMediaAsset()")
    this.nativeComponentRef.setNativeProps({ mediaAsset: asset });
  }

  prepare = () => {
    console.log("Calling Native Prop prepare()")
    this.nativeComponentRef.setNativeProps({ prepare: true });
  };

  play = () => {
    console.log("Calling Native Prop play()")
    this.nativeComponentRef.setNativeProps({ play: true });
  };

  pause = () => {
    console.log("Calling Native Prop pause()")
    this.nativeComponentRef.setNativeProps({ pause: true });
  };

  stop = () => {
    console.log("Calling Native Prop stop()")
    this.nativeComponentRef.setNativeProps({ stop: true });
  }

  replay = () => {
    console.log("Calling Native Prop replay()")
    this.nativeComponentRef.setNativeProps({ replay: true });
  }

  seekTo = (position: number) => {
    console.log("Calling Native Prop seekTo()")
    this.nativeComponentRef.setNativeProps({ seek: position });
  }

  changeTrack = (trackId: string) => {
    console.log("Calling Native Prop changeTrack()")
    this.nativeComponentRef.setNativeProps({ changeTrack: trackId })
  }

  setPlaybackRate = (rate: number) => {
    console.log("Calling Native Prop setPlaybackRate()")
    this.nativeComponentRef.setNativeProps({ playbackRate: rate })
  }

  setVolume = (vol: number) => {
    console.log("Calling Native Prop setVolume()")
    this.nativeComponentRef.setNativeProps({ volume: vol })
  }

  setAutoPlay = (isAutoPlay: boolean) => {
    console.log("Calling Native Prop setAutoPlay()")
    this.nativeComponentRef.setNativeProps({ autoPlay: isAutoPlay})
  }

  setKS = (KS: string) => {
    console.log("Calling Native Prop setKS()")
    this.nativeComponentRef.setNativeProps({ ks: KS })
  }

  setZIndex = (index: number) => {
    console.log("Calling Native Prop setZIndex()")
    this.nativeComponentRef.setNativeProps({ zIndex: index })
  }

  updateMediaAsset = (updatedMediaAsset: string) => {
    console.log("Calling Native Prop updateMediaAsset()")
    this.nativeComponentRef.setNativeProps({ mediaAsset: updatedMediaAsset });
  };

  updateAssetId = (updatedAssetId: string) => {
    console.log("Calling Native Prop updateMediaAsset()")
    this.nativeComponentRef.setNativeProps({ assetId: updatedAssetId });
  };

  render() {
    return <RNKalturaPlayer {...this.props} ref={(nativeRef) => this.nativeComponentRef = nativeRef} />;
  }
}

KalturaPlayer.propTypes = {
  style: PropTypes.object,
  partnerId: PropTypes.number,
  assetId: PropTypes.string,
  prepare: PropTypes.bool,
  playerInitOptions: PropTypes.string,
  mediaAsset: PropTypes.string,
};


