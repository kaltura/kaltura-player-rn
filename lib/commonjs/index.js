"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.default = exports.PLAYER_TYPE = exports.MEDIA_FORMAT = exports.MEDIA_ENTRY_TYPE = exports.KalturaPlayer = void 0;

var _reactNative = require("react-native");

var _react = _interopRequireDefault(require("react"));

var _propTypes = _interopRequireDefault(require("prop-types"));

var _PlayerEvents = _interopRequireDefault(require("src/events/PlayerEvents"));

function _interopRequireDefault(obj) { return obj && obj.__esModule ? obj : { default: obj }; }

function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

const RNKalturaPlayer = (0, _reactNative.requireNativeComponent)('KalturaPlayerView');
var _default = _PlayerEvents.default;
exports.default = _default;
let PLAYER_TYPE;
exports.PLAYER_TYPE = PLAYER_TYPE;

(function (PLAYER_TYPE) {
  PLAYER_TYPE["OVP"] = "ovp";
  PLAYER_TYPE["OTT"] = "ott";
  PLAYER_TYPE["BASIC"] = "basic";
})(PLAYER_TYPE || (exports.PLAYER_TYPE = PLAYER_TYPE = {}));

let MEDIA_FORMAT;
exports.MEDIA_FORMAT = MEDIA_FORMAT;

(function (MEDIA_FORMAT) {
  MEDIA_FORMAT["DASH"] = "dash";
  MEDIA_FORMAT["HLS"] = "hls";
  MEDIA_FORMAT["WVM"] = "wvm";
  MEDIA_FORMAT["MP4"] = "mp4";
  MEDIA_FORMAT["MP3"] = "mp3";
  MEDIA_FORMAT["UDP"] = "udp";
})(MEDIA_FORMAT || (exports.MEDIA_FORMAT = MEDIA_FORMAT = {}));

let MEDIA_ENTRY_TYPE;
exports.MEDIA_ENTRY_TYPE = MEDIA_ENTRY_TYPE;

(function (MEDIA_ENTRY_TYPE) {
  MEDIA_ENTRY_TYPE["VOD"] = "Vod";
  MEDIA_ENTRY_TYPE["LIVE"] = "Live";
  MEDIA_ENTRY_TYPE["DVRLIVE"] = "DvrLive";
})(MEDIA_ENTRY_TYPE || (exports.MEDIA_ENTRY_TYPE = MEDIA_ENTRY_TYPE = {}));

class KalturaPlayer extends _react.default.Component {
  constructor() {
    var _this;

    super(...arguments);
    _this = this;

    _defineProperty(this, "nativeComponentRef", void 0);

    _defineProperty(this, "eventListener", void 0);

    _defineProperty(this, "playerType", void 0);

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

    _defineProperty(this, "setup", function (options) {
      let id = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;
      console.log("Setting up the Player");

      _this.setNativeProps({
        partnerId: id
      });

      _this.setNativeProps({
        playerInitOptions: options
      });
    });

    _defineProperty(this, "loadMedia", (id, asset) => {
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
    console.log("componentDidMount from Library.");
  }

  componentWillUnmount() {
    console.log("componentWillUnmount from Library");
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
//# sourceMappingURL=index.js.map