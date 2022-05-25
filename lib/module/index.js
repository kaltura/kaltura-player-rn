function _extends() { _extends = Object.assign || function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import { requireNativeComponent } from 'react-native';
import React from 'react';
import PropTypes from 'prop-types';
import { PlayerEvents } from './events/PlayerEvents';
import { AdEvents } from './events/AdEvents';
import { AnalyticsEvents } from './events/AnalyticsEvents';
import { PLAYER_TYPE, MEDIA_FORMAT, MEDIA_ENTRY_TYPE, DRM_SCHEME, PLAYER_PLUGIN, PLAYER_RESIZE_MODES } from './consts';
const RNKalturaPlayer = requireNativeComponent('KalturaPlayerView');
export { PlayerEvents, AdEvents, AnalyticsEvents, PLAYER_TYPE, MEDIA_FORMAT, MEDIA_ENTRY_TYPE, DRM_SCHEME, PLAYER_PLUGIN, PLAYER_RESIZE_MODES };
export class KalturaPlayer extends React.Component {
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

    _defineProperty(this, "setup", function (options) {
      let id = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : 0;

      if (!options) {
        console.error(`setup, invalid options = ${options}`);
        return;
      }

      console.log('Setting up the Player');

      _this.setNativeProps({
        partnerId: id
      });

      _this.setNativeProps({
        playerInitOptions: options
      });
    });

    _defineProperty(this, "loadMedia", (id, asset) => {
      if (!id || !asset) {
        console.error(`loadMedia, invalid id = ${id} or asset = ${asset}`);
        return;
      }

      console.log(`Loading the media. assetId is: ${id} and media asset is: ${asset}`);
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

    _defineProperty(this, "addListeners", () => {
      console.log('Calling Native Prop addListeners()');
      this.setNativeProps({
        addListeners: true
      });
    });

    _defineProperty(this, "removeListeners", () => {
      console.log('Calling Native Prop removeListeners()');
      this.setNativeProps({
        removeListeners: true
      });
    });

    _defineProperty(this, "onApplicationPaused", () => {
      console.log('Calling Native Prop onApplicationPaused()');
      this.setNativeProps({
        onApplicationPaused: true
      });
    });

    _defineProperty(this, "onApplicationResumed", () => {
      console.log('Calling Native Prop onApplicationResumed()');
      this.setNativeProps({
        onApplicationResumed: true
      });
    });

    _defineProperty(this, "updatePluginConfig", (pluginName, config) => {
      if (pluginName == null || !config) {
        console.error(`updatePluginConfig, either pluginName ${pluginName} OR config is invalid: ${config}`);
        return;
      }

      const pluginJson = {
        pluginName: pluginName,
        pluginConfig: config
      };
      const stringifiedJson = JSON.stringify(pluginJson);
      console.log(`Updated Plugin is: ${stringifiedJson}`);
      this.setNativeProps({
        updatePluginConfig: stringifiedJson
      });
    });

    _defineProperty(this, "play", () => {
      console.log('Calling Native Prop play()');
      this.setNativeProps({
        play: true
      });
    });

    _defineProperty(this, "pause", () => {
      console.log('Calling Native Prop pause()');
      this.setNativeProps({
        pause: true
      });
    });

    _defineProperty(this, "stop", () => {
      console.log('Calling Native Prop stop()');
      this.setNativeProps({
        stop: true
      });
    });

    _defineProperty(this, "destroy", () => {
      console.log('Calling Native Prop destroy()');
      this.setNativeProps({
        onDestroy: true
      });
    });

    _defineProperty(this, "replay", () => {
      console.log('Calling Native Prop replay()');
      this.setNativeProps({
        replay: true
      });
    });

    _defineProperty(this, "seekTo", position => {
      console.log(`Calling Native Prop seekTo() position is: ${position}`);
      this.setNativeProps({
        seek: position
      });
    });

    _defineProperty(this, "changeTrack", trackId => {
      if (!trackId) {
        console.error(`trackId is invalid which is: ${trackId}`);
        return;
      }

      console.log('Calling Native Prop changeTrack()');
      this.setNativeProps({
        changeTrack: trackId
      });
    });

    _defineProperty(this, "setPlaybackRate", rate => {
      console.log(`Calling Native Prop setPlaybackRate() rate is: ${rate}`);
      this.setNativeProps({
        playbackRate: rate
      });
    });

    _defineProperty(this, "setVolume", vol => {
      console.log('Calling Native Prop setVolume()');
      this.setNativeProps({
        volume: vol
      });
    });

    _defineProperty(this, "setAutoPlay", isAutoPlay => {
      console.log('Calling Native Prop setAutoPlay()');
      this.setNativeProps({
        autoPlay: isAutoPlay
      });
    });

    _defineProperty(this, "setKS", KS => {
      if (!KS) {
        console.error('KS is invalid which is: ' + KS);
        return;
      }

      console.log('Calling Native Prop setKS()');
      this.setNativeProps({
        ks: KS
      });
    });

    _defineProperty(this, "setZIndex", index => {
      console.log('Calling Native Prop setZIndex()');
      this.setNativeProps({
        zIndex: index
      });
    });

    _defineProperty(this, "seekToLiveDefaultPosition", () => {
      console.log('Calling Native Prop seekToLiveDefaultPosition()');
      this.setNativeProps({
        seekToLiveDefaultPosition: true
      });
    });

    _defineProperty(this, "updateSubtitleStyle", subtitleStyle => {
      if (!subtitleStyle) {
        console.error(`subtitleStyle is invalid which is: ${subtitleStyle}`);
        return;
      }

      console.log('Calling Native Prop updateSubtitleStyle()');
      this.setNativeProps({
        updateSubtitleStyle: subtitleStyle
      });
    });

    _defineProperty(this, "updateResizeMode", mode => {
      console.log('Calling Native Prop updateSurfaceAspectRatioResizeMode()');
      this.setNativeProps({
        updateSurfaceAspectRatioResizeMode: mode
      });
    });

    _defineProperty(this, "updateAbrSettings", abrSettings => {
      if (!abrSettings) {
        console.error(`abrSettings is invalid which is: ${abrSettings}`);
        return;
      }

      console.log('Calling Native Prop updateABRSettings()');
      this.setNativeProps({
        updateABRSettings: abrSettings
      });
    });

    _defineProperty(this, "resetAbrSettings", () => {
      console.log('Calling Native Prop resetABRSettings()');
      this.setNativeProps({
        resetABRSettings: true
      });
    });

    _defineProperty(this, "updateLowLatencyConfig", lowLatencyConfig => {
      if (!lowLatencyConfig) {
        console.error(`lowLatencyConfig is invalid which is: ${lowLatencyConfig}`);
        return;
      }

      console.log('Calling Native Prop updateLowLatencyConfig()');
      this.setNativeProps({
        updateLowLatencyConfig: lowLatencyConfig
      });
    });

    _defineProperty(this, "resetLowLatencyConfig", () => {
      console.log('Calling Native Prop resetLowLatencyConfig()');
      this.setNativeProps({
        resetLowLatencyConfig: true
      });
    });
  }

  componentDidMount() {
    console.log('componentDidMount from Library.');
  }

  componentWillUnmount() {
    console.log('componentWillUnmount from Library');
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