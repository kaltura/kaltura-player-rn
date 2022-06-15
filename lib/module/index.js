function _extends() { _extends = Object.assign ? Object.assign.bind() : function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import { requireNativeComponent, NativeModules } from 'react-native';
import React from 'react';
import PropTypes from 'prop-types';
import { PlayerEvents } from './events/PlayerEvents';
import { AdEvents } from './events/AdEvents';
import { AnalyticsEvents } from './events/AnalyticsEvents';
import { PLAYER_TYPE, MEDIA_FORMAT, MEDIA_ENTRY_TYPE, DRM_SCHEME, PLAYER_PLUGIN, PLAYER_RESIZE_MODES, WAKEMODE, SUBTITLE_STYLE, SUBTITLE_PREFERENCE, VIDEO_CODEC, AUDIO_CODEC, VR_INTERACTION_MODE } from './consts';
const RNKalturaPlayer = requireNativeComponent('KalturaPlayerView');
const {
  KalturaPlayerModule
} = NativeModules;
export { PlayerEvents, AdEvents, AnalyticsEvents, PLAYER_TYPE, MEDIA_FORMAT, MEDIA_ENTRY_TYPE, DRM_SCHEME, PLAYER_PLUGIN, PLAYER_RESIZE_MODES, WAKEMODE, SUBTITLE_STYLE, SUBTITLE_PREFERENCE, VIDEO_CODEC, AUDIO_CODEC, VR_INTERACTION_MODE };
export class KalturaPlayer extends React.Component {
  constructor() {
    super(...arguments);

    _defineProperty(this, "nativeComponentRef", void 0);

    _defineProperty(this, "eventListener", void 0);

    _defineProperty(this, "playerType", void 0);

    _defineProperty(this, "setNativeProps", nativeProps => {
      this.nativeComponentRef.setNativeProps(nativeProps);
    });

    _defineProperty(this, "setup", function (options, isPlayerCreated) {
      let id = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : 0;

      if (!options) {
        console.error(`setup, invalid options = ${options}`);
        return;
      }

      console.log('Setting up the Player');
      KalturaPlayerModule.setUpPlayer(id, options, playerCreated => {
        isPlayerCreated(playerCreated);
      });
    });

    _defineProperty(this, "loadMedia", (id, asset) => {
      if (!id || !asset) {
        console.error(`loadMedia, invalid id = ${id} or asset = ${asset}`);
        return;
      }

      console.log(`Loading the media. assetId is: ${id} and media asset is: ${asset}`);
      KalturaPlayerModule.load(id, asset);
    });

    _defineProperty(this, "addListeners", () => {
      console.log('Calling Native Prop addListeners()');
      KalturaPlayerModule.addKalturaPlayerListeners();
    });

    _defineProperty(this, "removeListeners", () => {
      console.log('Calling Native Prop removeListeners()');
      KalturaPlayerModule.removeKalturaPlayerListeners();
    });

    _defineProperty(this, "onApplicationPaused", () => {
      console.log('Calling Native Prop onApplicationPaused()');
      KalturaPlayerModule.onApplicationPaused();
    });

    _defineProperty(this, "onApplicationResumed", () => {
      console.log('Calling Native Prop onApplicationResumed()');
      KalturaPlayerModule.onApplicationResumed();
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
      KalturaPlayerModule.updatePluginConfigs(stringifiedJson);
    });

    _defineProperty(this, "play", () => {
      console.log('Calling Native Prop play()');
      KalturaPlayerModule.play();
    });

    _defineProperty(this, "pause", () => {
      console.log('Calling Native Prop pause()');
      KalturaPlayerModule.pause();
    });

    _defineProperty(this, "stop", () => {
      console.log('Calling Native Prop stop()');
      KalturaPlayerModule.stop();
    });

    _defineProperty(this, "destroy", () => {
      console.log('Calling Native Prop destroy()');
      KalturaPlayerModule.destroy();
    });

    _defineProperty(this, "replay", () => {
      console.log('Calling Native Prop replay()');
      KalturaPlayerModule.replay();
    });

    _defineProperty(this, "seekTo", position => {
      console.log(`Calling Native Prop seekTo() position is: ${position}`);
      KalturaPlayerModule.seekTo(position);
    });

    _defineProperty(this, "changeTrack", trackId => {
      if (!trackId) {
        console.error(`trackId is invalid which is: ${trackId}`);
        return;
      }

      console.log('Calling Native Prop changeTrack()');
      KalturaPlayerModule.changeTrack(trackId);
    });

    _defineProperty(this, "setPlaybackRate", rate => {
      console.log(`Calling Native Prop setPlaybackRate() rate is: ${rate}`);
      KalturaPlayerModule.changePlaybackRate(rate);
    });

    _defineProperty(this, "setVolume", vol => {
      console.log('Calling Native Prop setVolume()');
      KalturaPlayerModule.setVolume(vol);
    });

    _defineProperty(this, "setAutoPlay", isAutoPlay => {
      console.log('Calling Native Prop setAutoPlay()');
      KalturaPlayerModule.setAutoplay(isAutoPlay);
    });

    _defineProperty(this, "setKS", KS => {
      if (!KS) {
        console.error('KS is invalid which is: ' + KS);
        return;
      }

      console.log('Calling Native Prop setKS()');
      KalturaPlayerModule.setKS(KS);
    });

    _defineProperty(this, "seekToLiveDefaultPosition", () => {
      console.log('Calling Native Prop seekToLiveDefaultPosition()');
      KalturaPlayerModule.seekToLiveDefaultPosition();
    });

    _defineProperty(this, "updateSubtitleStyle", subtitleStyle => {
      if (!subtitleStyle) {
        console.error(`subtitleStyle is invalid which is: ${subtitleStyle}`);
        return;
      }

      console.log('Calling Native Prop updateSubtitleStyle()');
      KalturaPlayerModule.updateSubtitleStyle(subtitleStyle);
    });

    _defineProperty(this, "updateResizeMode", mode => {
      console.log('Calling Native Prop updateSurfaceAspectRatioResizeMode()');
      KalturaPlayerModule.updateResizeMode(mode);
    });

    _defineProperty(this, "updateAbrSettings", abrSettings => {
      if (!abrSettings) {
        console.error(`abrSettings is invalid which is: ${abrSettings}`);
        return;
      }

      console.log('Calling Native Prop updateABRSettings()');
      KalturaPlayerModule.updateAbrSettings(abrSettings);
    });

    _defineProperty(this, "resetAbrSettings", () => {
      console.log('Calling Native Prop resetABRSettings()');
      KalturaPlayerModule.resetAbrSettings();
    });

    _defineProperty(this, "updateLowLatencyConfig", lowLatencyConfig => {
      if (!lowLatencyConfig) {
        console.error(`lowLatencyConfig is invalid which is: ${lowLatencyConfig}`);
        return;
      }

      console.log('Calling Native Prop updateLowLatencyConfig()');
      KalturaPlayerModule.updateLlConfig(lowLatencyConfig);
    });

    _defineProperty(this, "resetLowLatencyConfig", () => {
      console.log('Calling Native Prop resetLowLatencyConfig()');
      KalturaPlayerModule.resetLlConfig();
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