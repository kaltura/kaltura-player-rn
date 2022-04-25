function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import { requireNativeComponent, NativeModules, NativeEventEmitter } from 'react-native';
import React from 'react';
import PropTypes from 'prop-types';
export const KalturaPlayerModule = NativeModules.KalturaPlayerViewManager;
const KalturaPlayerEvents = NativeModules.KalturaPlayerEvents;
export const KalturaPlayerEmitter = new NativeEventEmitter(KalturaPlayerEvents);
const RNKalturaPlayer = requireNativeComponent('KalturaPlayerView');
console.log(KalturaPlayerEvents);
export class KalturaPlayer extends React.Component {
  constructor() {
    super(...arguments);

    _defineProperty(this, "nativeComponentRef", void 0);

    _defineProperty(this, "eventListener", void 0);

    _defineProperty(this, "setAssetId", id => {
      console.log("Calling Native Prop assetId()");
      this.nativeComponentRef.setNativeProps({
        assetId: id
      });
    });

    _defineProperty(this, "setPlayerInitOptions", options => {
      console.log("Calling Native Prop setPlayerInitOptions()");
      this.nativeComponentRef.setNativeProps({
        playerInitOptions: options
      });
    });

    _defineProperty(this, "setMediaAsset", asset => {
      console.log("Calling Native Prop setMediaAsset()");
      this.nativeComponentRef.setNativeProps({
        mediaAsset: asset
      });
    });

    _defineProperty(this, "prepare", () => {
      console.log("Calling Native Prop prepare()");
      this.nativeComponentRef.setNativeProps({
        prepare: true
      });
    });

    _defineProperty(this, "play", () => {
      console.log("Calling Native Prop play()");
      this.nativeComponentRef.setNativeProps({
        play: true
      });
    });

    _defineProperty(this, "pause", () => {
      console.log("Calling Native Prop pause()");
      this.nativeComponentRef.setNativeProps({
        pause: true
      });
    });

    _defineProperty(this, "stop", () => {
      console.log("Calling Native Prop stop()");
      this.nativeComponentRef.setNativeProps({
        stop: true
      });
    });

    _defineProperty(this, "replay", () => {
      console.log("Calling Native Prop replay()");
      this.nativeComponentRef.setNativeProps({
        replay: true
      });
    });

    _defineProperty(this, "seekTo", position => {
      console.log("Calling Native Prop seekTo()");
      this.nativeComponentRef.setNativeProps({
        seek: position
      });
    });

    _defineProperty(this, "changeTrack", trackId => {
      console.log("Calling Native Prop changeTrack()");
      this.nativeComponentRef.setNativeProps({
        changeTrack: trackId
      });
    });

    _defineProperty(this, "setPlaybackRate", rate => {
      console.log("Calling Native Prop setPlaybackRate()");
      this.nativeComponentRef.setNativeProps({
        playbackRate: rate
      });
    });

    _defineProperty(this, "setVolume", vol => {
      console.log("Calling Native Prop setVolume()");
      this.nativeComponentRef.setNativeProps({
        volume: vol
      });
    });

    _defineProperty(this, "setAutoPlay", isAutoPlay => {
      console.log("Calling Native Prop setAutoPlay()");
      this.nativeComponentRef.setNativeProps({
        autoPlay: isAutoPlay
      });
    });

    _defineProperty(this, "setKS", KS => {
      console.log("Calling Native Prop setKS()");
      this.nativeComponentRef.setNativeProps({
        ks: KS
      });
    });

    _defineProperty(this, "setZIndex", index => {
      console.log("Calling Native Prop setZIndex()");
      this.nativeComponentRef.setNativeProps({
        zIndex: index
      });
    });

    _defineProperty(this, "updateMediaAsset", updatedMediaAsset => {
      console.log("Calling Native Prop updateMediaAsset()");
      this.nativeComponentRef.setNativeProps({
        mediaAsset: updatedMediaAsset
      });
    });

    _defineProperty(this, "updateAssetId", updatedAssetId => {
      console.log("Calling Native Prop updateMediaAsset()");
      this.nativeComponentRef.setNativeProps({
        assetId: updatedAssetId
      });
    });
  }

  componentDidMount() {
    // this.eventListener = KalturaPlayerEmitter.addListener('playing', (event: any) => {
    //   console.log(event)
    // });
    console.log("componentDidMount"); //this.prepare()
  }

  componentWillUnmount() {
    //this.eventListener.remove(); //Removes the listener
    console.log("componentWillUnmount");
  }

  render() {
    return /*#__PURE__*/React.createElement(RNKalturaPlayer, _extends({}, this.props, {
      ref: nativeRef => this.nativeComponentRef = nativeRef
    }));
  }

}

_defineProperty(KalturaPlayer, "propTypes", void 0);

KalturaPlayer.propTypes = {
  style: PropTypes.object,
  partnerId: PropTypes.number,
  assetId: PropTypes.string,
  prepare: PropTypes.bool,
  playerInitOptions: PropTypes.string,
  mediaAsset: PropTypes.string
};
//# sourceMappingURL=index.js.map