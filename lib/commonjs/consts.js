"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.PLAYER_TYPE = exports.PLAYER_RESIZE_MODES = exports.PLAYER_PLUGIN = exports.MEDIA_FORMAT = exports.MEDIA_ENTRY_TYPE = exports.DRM_SCHEME = void 0;
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

let DRM_SCHEME;
exports.DRM_SCHEME = DRM_SCHEME;

(function (DRM_SCHEME) {
  DRM_SCHEME["WIDEVINE_CENC"] = "WidevineCENC";
  DRM_SCHEME["PLAYREADY_CENC"] = "PlayReadyCENC";
  DRM_SCHEME["WIDEVINE_CENC_CLASSIC"] = "WidevineClassic";
  DRM_SCHEME["PLAYREADY_CLASSIC"] = "PlayReadyClassic";
})(DRM_SCHEME || (exports.DRM_SCHEME = DRM_SCHEME = {}));

let PLAYER_PLUGIN;
exports.PLAYER_PLUGIN = PLAYER_PLUGIN;

(function (PLAYER_PLUGIN) {
  PLAYER_PLUGIN["IMA"] = "ima";
  PLAYER_PLUGIN["IMADAI"] = "imadai";
  PLAYER_PLUGIN["YOUBORA"] = "youbora";
  PLAYER_PLUGIN["KAVA"] = "kava";
  PLAYER_PLUGIN["OTT_ANALYTICS"] = "ottAnalytics";
  PLAYER_PLUGIN["BROADPEAK"] = "broadpeak";
})(PLAYER_PLUGIN || (exports.PLAYER_PLUGIN = PLAYER_PLUGIN = {}));

let PLAYER_RESIZE_MODES;
exports.PLAYER_RESIZE_MODES = PLAYER_RESIZE_MODES;

(function (PLAYER_RESIZE_MODES) {
  PLAYER_RESIZE_MODES["FIT"] = "fit";
  PLAYER_RESIZE_MODES["FIXED_WIDTH"] = "fixedWidth";
  PLAYER_RESIZE_MODES["FIXED_HEIGHT"] = "fixedHeight";
  PLAYER_RESIZE_MODES["FILL"] = "fill";
  PLAYER_RESIZE_MODES["ZOOM"] = "zoom";
})(PLAYER_RESIZE_MODES || (exports.PLAYER_RESIZE_MODES = PLAYER_RESIZE_MODES = {}));
//# sourceMappingURL=consts.js.map