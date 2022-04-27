import {
  requireNativeComponent,
  ViewStyle,
} from 'react-native';
import React from 'react';
import PropTypes from 'prop-types';

const RNKalturaPlayer = requireNativeComponent('KalturaPlayerView');

interface KalturaPlayerProps {
  style: ViewStyle,
  playerType: PLAYER_TYPE
}

export enum PLAYER_TYPE {
  OVP = "ovp",
  OTT = "ott",
  BASIC = "basic"
}

export enum MEDIA_FORMAT {
  DASH = "dash",
  HLS = "hls",
  WVM = "wvm",
  MP4 = "mp4",
  MP3 = "mp3",
  UDP = "udp"
}

export enum MEDIA_ENTRY_TYPE {
  VOD = "Vod",
  LIVE = "Live",
  DVRLIVE = "DvrLive"
}

// http://cdnapi.kaltura.com/p/243342/sp/24334200/playManifest/entryId/0_uka1msg4/flavorIds/1_vqhfu6uy,1_80sohj7p/format/applehttp/protocol/http/a.m3u8
export class KalturaPlayer extends React.Component<KalturaPlayerProps> {
  nativeComponentRef: any;
  eventListener: any;
  playerType: PLAYER_TYPE | undefined;

  static propTypes: {
    style: object
  };

  componentDidMount() {
    console.log("componentDidMount from Library.");
  }

  componentWillUnmount() {
    console.log("componentWillUnmount from Library");
  }

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

  loadMedia = (id: string, asset: string) => {
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


