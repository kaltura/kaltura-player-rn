function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import { requireNativeComponent, NativeModules, NativeEventEmitter } from 'react-native';
import React from 'react';
import PropTypes from 'prop-types';
export const KalturaPlayerModule = NativeModules.KalturaPlayerViewManager;
const KalturaPlayerEvents = NativeModules.KalturaPlayerEvents;
export const KalturaPlayerEmitter = new NativeEventEmitter(KalturaPlayerEvents);
const RNKalturaPlayer = requireNativeComponent('KalturaPlayerView');
export class KalturaPlayer extends React.Component {
  constructor() {
    super(...arguments);

    _defineProperty(this, "nativeComponentRef", void 0);

    _defineProperty(this, "eventListeners", []);

    _defineProperty(this, "setNativeProps", nativeProps => {
      this.nativeComponentRef.setNativeProps(nativeProps);
    });

    _defineProperty(this, "addListeners", () => {
      console.log("Calling Native Prop addListeners()");
      this.setNativeProps({
        addListeners: true
      });
    });

    _defineProperty(this, "removeListeners", () => {
      console.log("Calling Native Prop removeListeners()");
      this.setNativeProps({
        removeListeners: true
      });
    });

    _defineProperty(this, "setup", (id, options) => {
      console.log("Setting up the Kaltura Player");
      this.setNativeProps({
        partnerId: id
      });
      this.setNativeProps({
        playerInitOptions: options
      });
    });

    _defineProperty(this, "load", (id, asset) => {
      console.log("Loading the media.");
      this.setNativeProps({
        assetId: id
      });
      this.setNativeProps({
        mediaAsset: asset
      });
      this.setNativeProps({
        load: true
      });
    });

    _defineProperty(this, "play", () => {
      console.log("Calling Native Prop play()");
      this.setNativeProps({
        play: true
      });
    });

    _defineProperty(this, "pause", () => {
      console.log("Calling Native Prop pause()");
      this.setNativeProps({
        pause: true
      });
    });

    _defineProperty(this, "stop", () => {
      console.log("Calling Native Prop stop()");
      this.setNativeProps({
        stop: true
      });
    });

    _defineProperty(this, "replay", () => {
      console.log("Calling Native Prop replay()");
      this.setNativeProps({
        replay: true
      });
    });

    _defineProperty(this, "seekTo", position => {
      console.log("Calling Native Prop seekTo()");
      this.setNativeProps({
        seek: position
      });
    });

    _defineProperty(this, "changeTrack", trackId => {
      console.log("Calling Native Prop changeTrack()");
      this.setNativeProps({
        changeTrack: trackId
      });
    });

    _defineProperty(this, "setPlaybackRate", rate => {
      console.log("Calling Native Prop setPlaybackRate()");
      this.setNativeProps({
        playbackRate: rate
      });
    });

    _defineProperty(this, "setVolume", vol => {
      console.log("Calling Native Prop setVolume()");
      this.setNativeProps({
        volume: vol
      });
    });

    _defineProperty(this, "setAutoPlay", isAutoPlay => {
      console.log("Calling Native Prop setAutoPlay()");
      this.setNativeProps({
        autoPlay: isAutoPlay
      });
    });

    _defineProperty(this, "setKS", KS => {
      console.log("Calling Native Prop setKS()");
      this.setNativeProps({
        ks: KS
      });
    });

    _defineProperty(this, "setZIndex", index => {
      console.log("Calling Native Prop setZIndex()");
      this.setNativeProps({
        zIndex: index
      });
    });
  }

  componentDidMount() {
    const supportedEvents = ["canPlay", "durationChanged", "stopped", "ended", "loadedMetadata", "play", "pause", "playing", "seeking", "seeked", "replay", "tracksAvailable", "textTrackChanged", "audioTrackChanged", "videoTrackChanged", "playbackInfo", "stateChanged", "timedMetadata", "sourceSelected", "loadedTimeRanges", "playheadUpdate", "error", "errorLog", "playbackStalled", "playbackRate"];
    supportedEvents.forEach(type => {
      this.eventListeners.push(KalturaPlayerEmitter.addListener(type, event => {
        KalturaPlayerEmitter.emit('KPlayerEvent', {
          type,
          ...event
        });
      }));
    });
  }

  componentWillUnmount() {
    this.eventListeners.forEach(event => event.remove());
  }

  render() {
    return /*#__PURE__*/React.createElement(RNKalturaPlayer, _extends({}, this.props, {
      ref: nativeRef => this.nativeComponentRef = nativeRef
    }));
  }

}

_defineProperty(KalturaPlayer, "propTypes", void 0);

KalturaPlayer.propTypes = {
  style: PropTypes.object
};
//# sourceMappingURL=index.js.map