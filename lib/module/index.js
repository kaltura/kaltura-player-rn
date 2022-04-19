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
  }

  componentDidMount() {// this.eventListener = KalturaPlayerEmitter.addListener('playing', (event: any) => {
    //   console.log(event)
    // });
  }

  componentWillUnmount() {//this.eventListener.remove(); //Removes the listener
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
  assetId: PropTypes.string,
  partnerId: PropTypes.number,
  baseUrl: PropTypes.string,
  prepare: PropTypes.bool
};
//# sourceMappingURL=index.js.map