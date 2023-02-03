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
export class KalturaPlayerAPI {}

_defineProperty(KalturaPlayerAPI, "setup", (partnerId, options) => {
  return KalturaPlayerModule.setup(partnerId, options);
});

_defineProperty(KalturaPlayerAPI, "load", (assetId, options) => {
  return KalturaPlayerModule.load(assetId, options);
});

_defineProperty(KalturaPlayerAPI, "destroy", () => {
  return KalturaPlayerModule.destroy();
});

_defineProperty(KalturaPlayerAPI, "setVolume", volume => {
  return KalturaPlayerModule.setVolume(volume);
});

_defineProperty(KalturaPlayerAPI, "seekTo", position => {
  return KalturaPlayerModule.seekTo(position);
});

_defineProperty(KalturaPlayerAPI, "setPlayerVisibility", isVisible => {
  return KalturaPlayerModule.setPlayerVisibility(isVisible);
});

_defineProperty(KalturaPlayerAPI, "play", () => {
  return KalturaPlayerModule.play();
});

_defineProperty(KalturaPlayerAPI, "replay", () => {
  return KalturaPlayerModule.replay();
});

_defineProperty(KalturaPlayerAPI, "pause", () => {
  return KalturaPlayerModule.pause();
});

_defineProperty(KalturaPlayerAPI, "stop", () => {
  return KalturaPlayerModule.stop();
});

_defineProperty(KalturaPlayerAPI, "setMaxBitrate", maxBitrate => {
  return KalturaPlayerModule.setMaxBitrate(maxBitrate)
});

_defineProperty(KalturaPlayerAPI, "setAutoplay", value => {
  return KalturaPlayerModule.setAutoplay(value);
});
//# sourceMappingURL=index.js.map