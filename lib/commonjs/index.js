"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.KalturaPlayerModule = exports.KalturaPlayerEmitter = exports.KalturaPlayerAPI = exports.KalturaPlayer = void 0;

var _reactNative = require("react-native");

var _react = _interopRequireDefault(require("react"));

var _propTypes = _interopRequireDefault(require("prop-types"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

const KalturaPlayerModule = _reactNative.NativeModules.KalturaPlayerViewManager;
exports.KalturaPlayerModule = KalturaPlayerModule;
const KalturaPlayerEvents = _reactNative.NativeModules.KalturaPlayerEvents;
const KalturaPlayerEmitter = new _reactNative.NativeEventEmitter(KalturaPlayerEvents);
exports.KalturaPlayerEmitter = KalturaPlayerEmitter;
const RNKalturaPlayer = (0, _reactNative.requireNativeComponent)('KalturaPlayerView');

class KalturaPlayer extends _react.default.Component {
  constructor() {
    super(...arguments);

    _defineProperty(this, "nativeComponentRef", void 0);

    _defineProperty(this, "eventListeners", []);
  }

  componentDidMount() {
    const supportedEvents = ["canPlay", "durationChanged", "stopped", "ended", "loadedMetadata", "play", "pause", "playing", "seeking", "seeked", "replay", "tracksAvailable", "textTrackChanged", "audioTrackChanged", "videoTrackChanged", "playbackInfo", "stateChanged", "timedMetadata", "sourceSelected", "loadedTimeRanges", "playheadUpdate", "error", "errorLog", "playbackStalled", "playbackRate", "timeUpdate", "bookmarkError", "loadMediaFailed", "loadMediaSuccess", "concurrencyError"];
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
    return /*#__PURE__*/_react.default.createElement(RNKalturaPlayer, _extends({}, this.props, {
      ref: nativeRef => this.nativeComponentRef = nativeRef
    }));
  }

}

exports.KalturaPlayer = KalturaPlayer;

_defineProperty(KalturaPlayer, "propTypes", void 0);

KalturaPlayer.propTypes = {
  style: _propTypes.default.object
};

class KalturaPlayerAPI {}

exports.KalturaPlayerAPI = KalturaPlayerAPI;

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

_defineProperty(KalturaPlayerAPI, "setKeepAwake", value => {
  return KalturaPlayerModule.setKeepAwake(value);
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

_defineProperty(KalturaPlayerAPI, "changeTrack", trackId => {
  return KalturaPlayerModule.changeTrack(trackId);
});

_defineProperty(KalturaPlayerAPI, "prepare", () => {
  return KalturaPlayerModule.prepare();
});

_defineProperty(KalturaPlayerAPI, "setAutoplay", value => {
  return KalturaPlayerModule.setAutoplay(value);
});
//# sourceMappingURL=index.js.map