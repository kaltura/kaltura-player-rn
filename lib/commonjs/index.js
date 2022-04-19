"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.KalturaPlayerModule = exports.KalturaPlayerEmitter = exports.KalturaPlayer = void 0;

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
console.log(KalturaPlayerEvents);

class KalturaPlayer extends _react.default.Component {
  constructor() {
    super(...arguments);

    _defineProperty(this, "nativeComponentRef", void 0);

    _defineProperty(this, "eventListener", void 0);
  }

  componentDidMount() {// this.eventListener = KalturaPlayerEmitter.addListener('playing', (event: any) => {
    //   console.log(event)
    // });
  }

  componentWillUnmount() {//this.eventListener.remove(); //Removes the listener
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
  style: _propTypes.default.object,
  assetId: _propTypes.default.string,
  partnerId: _propTypes.default.number,
  baseUrl: _propTypes.default.string,
  prepare: _propTypes.default.bool
};
//# sourceMappingURL=index.js.map