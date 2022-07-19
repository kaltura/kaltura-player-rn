function _extends() { _extends = Object.assign ? Object.assign.bind() : function (target) { for (var i = 1; i < arguments.length; i++) { var source = arguments[i]; for (var key in source) { if (Object.prototype.hasOwnProperty.call(source, key)) { target[key] = source[key]; } } } return target; }; return _extends.apply(this, arguments); }

function _defineProperty(obj, key, value) { if (key in obj) { Object.defineProperty(obj, key, { value: value, enumerable: true, configurable: true, writable: true }); } else { obj[key] = value; } return obj; }

import { requireNativeComponent, NativeModules } from 'react-native';
import React from 'react';
import PropTypes from 'prop-types';
import { PlayerEvents } from './events/PlayerEvents';
import { AdEvents } from './events/AdEvents';
import { AnalyticsEvents } from './events/AnalyticsEvents';
import { PLAYER_TYPE, MEDIA_FORMAT, MEDIA_ENTRY_TYPE, DRM_SCHEME, PLAYER_PLUGIN, PLAYER_RESIZE_MODES, WAKEMODE, SUBTITLE_STYLE, SUBTITLE_PREFERENCE, VIDEO_CODEC, AUDIO_CODEC, VR_INTERACTION_MODE } from './consts';
export { PlayerEvents, AdEvents, AnalyticsEvents, PLAYER_TYPE, MEDIA_FORMAT, MEDIA_ENTRY_TYPE, DRM_SCHEME, PLAYER_PLUGIN, PLAYER_RESIZE_MODES, WAKEMODE, SUBTITLE_STYLE, SUBTITLE_PREFERENCE, VIDEO_CODEC, AUDIO_CODEC, VR_INTERACTION_MODE };
const RNKalturaPlayer = requireNativeComponent('KalturaPlayerView');
const {
  KalturaPlayerModule
} = NativeModules;
const POSITION_UNSET = -1;
const enableConsoleLogs = true;
export class KalturaPlayer extends React.Component {
  constructor() {
    super(...arguments);

    _defineProperty(this, "nativeComponentRef", void 0);

    _defineProperty(this, "playerType", void 0);
  }

  componentDidMount() {
    printConsoleLog('componentDidMount from Library.');
  }

  componentWillUnmount() {
    printConsoleLog('componentWillUnmount from Library');
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

_defineProperty(KalturaPlayerAPI, "setup", async function (playerType, options) {
  let id = arguments.length > 2 && arguments[2] !== undefined ? arguments[2] : 0;

  if (playerType == null) {
    printConsoleLog(`Invalid playerType = ${playerType}`, LogType.ERROR);
    return;
  }

  if (!options) {
    printConsoleLog(`setup, invalid options = ${options}`, LogType.ERROR);
    return;
  }

  printConsoleLog('Setting up the Player');
  return await setupKalturaPlayer(playerType, options, id);
});

_defineProperty(KalturaPlayerAPI, "loadMedia", async (id, asset) => {
  if (!id || !asset) {
    printConsoleLog(`loadMedia, invalid id = ${id} or asset = ${asset}`, LogType.ERROR);
    return;
  }

  printConsoleLog(`Loading the media. assetId is: ${id} and media asset is: ${asset}`);
  return await loadMediaKalturaPlayer(id, asset);
});

_defineProperty(KalturaPlayerAPI, "addListeners", () => {
  printConsoleLog('Calling Native Prop addListeners()');
  KalturaPlayerModule.addKalturaPlayerListeners();
});

_defineProperty(KalturaPlayerAPI, "removeListeners", () => {
  printConsoleLog('Calling Native Prop removeListeners()');
  KalturaPlayerModule.removeKalturaPlayerListeners();
});

_defineProperty(KalturaPlayerAPI, "onApplicationPaused", () => {
  printConsoleLog('Calling Native Prop onApplicationPaused()');
  KalturaPlayerModule.onApplicationPaused();
});

_defineProperty(KalturaPlayerAPI, "onApplicationResumed", () => {
  printConsoleLog('Calling Native Prop onApplicationResumed()');
  KalturaPlayerModule.onApplicationResumed();
});

_defineProperty(KalturaPlayerAPI, "updatePluginConfig", (pluginName, config) => {
  if (pluginName == null || !config) {
    printConsoleLog(`updatePluginConfig, either pluginName ${pluginName} OR config is invalid: ${config}`, LogType.ERROR);
    return;
  }

  const pluginJson = {
    pluginName: pluginName,
    pluginConfig: config
  };
  const stringifiedJson = JSON.stringify(pluginJson);
  printConsoleLog(`Updated Plugin is: ${stringifiedJson}`);
  KalturaPlayerModule.updatePluginConfigs(stringifiedJson);
});

_defineProperty(KalturaPlayerAPI, "play", () => {
  printConsoleLog('Calling Native Prop play()');
  KalturaPlayerModule.play();
});

_defineProperty(KalturaPlayerAPI, "pause", () => {
  printConsoleLog('Calling Native Prop pause()');
  KalturaPlayerModule.pause();
});

_defineProperty(KalturaPlayerAPI, "stop", () => {
  printConsoleLog('Calling Native Prop stop()');
  KalturaPlayerModule.stop();
});

_defineProperty(KalturaPlayerAPI, "destroy", () => {
  printConsoleLog('Calling Native Prop destroy()');
  KalturaPlayerModule.destroy();
});

_defineProperty(KalturaPlayerAPI, "replay", () => {
  printConsoleLog('Calling Native Prop replay()');
  KalturaPlayerModule.replay();
});

_defineProperty(KalturaPlayerAPI, "seekTo", position => {
  printConsoleLog(`Calling Native Prop seekTo() position is: ${position}`);
  KalturaPlayerModule.seekTo(position);
});

_defineProperty(KalturaPlayerAPI, "changeTrack", trackId => {
  if (!trackId) {
    printConsoleLog(`trackId is invalid which is: ${trackId}`, LogType.ERROR);
    return;
  }

  printConsoleLog('Calling Native Prop changeTrack()');
  KalturaPlayerModule.changeTrack(trackId);
});

_defineProperty(KalturaPlayerAPI, "setPlaybackRate", rate => {
  printConsoleLog(`Calling Native Prop setPlaybackRate() rate is: ${rate}`);
  KalturaPlayerModule.changePlaybackRate(rate);
});

_defineProperty(KalturaPlayerAPI, "setVolume", vol => {
  printConsoleLog('Calling Native Prop setVolume()');
  KalturaPlayerModule.setVolume(vol);
});

_defineProperty(KalturaPlayerAPI, "setAutoPlay", isAutoPlay => {
  printConsoleLog('Calling Native Prop setAutoPlay()');
  KalturaPlayerModule.setAutoplay(isAutoPlay);
});

_defineProperty(KalturaPlayerAPI, "setKS", KS => {
  if (!KS) {
    printConsoleLog('KS is invalid which is: ' + KS, LogType.ERROR);
    return;
  }

  printConsoleLog('Calling Native Prop setKS()');
  KalturaPlayerModule.setKS(KS);
});

_defineProperty(KalturaPlayerAPI, "seekToLiveDefaultPosition", () => {
  printConsoleLog('Calling Native Prop seekToLiveDefaultPosition()');
  KalturaPlayerModule.seekToLiveDefaultPosition();
});

_defineProperty(KalturaPlayerAPI, "updateSubtitleStyle", subtitleStyle => {
  if (!subtitleStyle) {
    printConsoleLog(`subtitleStyle is invalid which is: ${subtitleStyle}`, LogType.ERROR);
    return;
  }

  printConsoleLog('Calling Native Prop updateSubtitleStyle()');
  KalturaPlayerModule.updateSubtitleStyle(subtitleStyle);
});

_defineProperty(KalturaPlayerAPI, "updateResizeMode", mode => {
  printConsoleLog('Calling Native Prop updateSurfaceAspectRatioResizeMode()');
  KalturaPlayerModule.updateResizeMode(mode);
});

_defineProperty(KalturaPlayerAPI, "updateAbrSettings", abrSettings => {
  if (!abrSettings) {
    printConsoleLog(`abrSettings is invalid which is: ${abrSettings}`, LogType.ERROR);
    return;
  }

  printConsoleLog('Calling Native Prop updateABRSettings()');
  KalturaPlayerModule.updateAbrSettings(abrSettings);
});

_defineProperty(KalturaPlayerAPI, "resetAbrSettings", () => {
  printConsoleLog('Calling Native Prop resetABRSettings()');
  KalturaPlayerModule.resetAbrSettings();
});

_defineProperty(KalturaPlayerAPI, "updateLowLatencyConfig", lowLatencyConfig => {
  if (!lowLatencyConfig) {
    printConsoleLog(`lowLatencyConfig is invalid which is: ${lowLatencyConfig}`, LogType.ERROR);
    return;
  }

  printConsoleLog('Calling Native Prop updateLowLatencyConfig()');
  KalturaPlayerModule.updateLlConfig(lowLatencyConfig);
});

_defineProperty(KalturaPlayerAPI, "resetLowLatencyConfig", () => {
  printConsoleLog('Calling Native Prop resetLowLatencyConfig()');
  KalturaPlayerModule.resetLlConfig();
});

_defineProperty(KalturaPlayerAPI, "getCurrentPosition", async () => {
  printConsoleLog('Calling Native Prop getCurrentPosition()');
  return await getCurrentPosition();
});

_defineProperty(KalturaPlayerAPI, "isPlaying", async () => {
  printConsoleLog('Calling Native Prop isPlaying');
  return await isPlaying();
});

_defineProperty(KalturaPlayerAPI, "isLive", async () => {
  printConsoleLog('Calling Native Prop isLive');
  return await isLive();
});

async function setupKalturaPlayer(playerType, options, id) {
  try {
    const kalturaPlayerSetup = await KalturaPlayerModule.setUpPlayer(playerType, id, options);
    printConsoleLog(`Player is created: ${kalturaPlayerSetup}`);
    return kalturaPlayerSetup;
  } catch (exception) {
    printConsoleLog(`setupKalturaPlayer Exception: ${exception}`, LogType.ERROR);
    return exception;
  }
}

async function loadMediaKalturaPlayer(id, asset) {
  try {
    const loadMedia = KalturaPlayerModule.load(id, asset);
    printConsoleLog(`Media Loaded ${loadMedia}`);
    return loadMedia;
  } catch (exception) {
    printConsoleLog(`loadMediaKalturaPlayer Exception: ${exception}`, LogType.ERROR);
    return exception;
  }
}

async function getCurrentPosition() {
  try {
    const currentPosition = await KalturaPlayerModule.getCurrentPosition();
    printConsoleLog(`Current Position: ${currentPosition}`);
    return currentPosition;
  } catch (exception) {
    printConsoleLog(`Exception: ${exception}`, LogType.ERROR);
    return POSITION_UNSET;
  }
}

async function isPlaying() {
  try {
    const isPlayerPlaying = await KalturaPlayerModule.isPlaying();
    printConsoleLog(`isPlayerPlaying ${isPlayerPlaying}`);
    return isPlayerPlaying;
  } catch (exception) {
    printConsoleLog(`Exception: ${exception}`, LogType.ERROR);
    return false;
  }
}

async function isLive() {
  try {
    const isPlayerLive = await KalturaPlayerModule.isLive();
    printConsoleLog(`isPlayerLive ${isPlayerLive}`);
    return isPlayerLive;
  } catch (exception) {
    printConsoleLog(`Exception: ${exception}`, LogType.ERROR);
    return false;
  }
}

function printConsoleLog(message) {
  let logType = arguments.length > 1 && arguments[1] !== undefined ? arguments[1] : LogType.LOG;

  if (enableConsoleLogs) {
    switch (logType) {
      case LogType.LOG:
        {
          console.log(message);
          break;
        }

      case LogType.WARN:
        {
          console.warn(message);
          break;
        }

      case LogType.ERROR:
        {
          console.error(message);
          break;
        }

      default:
        {
          console.log(message);
        }
    }
  }
}

var LogType;

(function (LogType) {
  LogType[LogType["LOG"] = 0] = "LOG";
  LogType[LogType["WARN"] = 1] = "WARN";
  LogType[LogType["ERROR"] = 2] = "ERROR";
})(LogType || (LogType = {}));
//# sourceMappingURL=index.js.map