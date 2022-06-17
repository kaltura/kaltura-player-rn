"use strict";

Object.defineProperty(exports, "__esModule", {
  value: true
});
exports.WAKEMODE = exports.VR_INTERACTION_MODE = exports.VIDEO_CODEC = exports.SUBTITLE_STYLE = exports.SUBTITLE_PREFERENCE = exports.PLAYER_TYPE = exports.PLAYER_RESIZE_MODES = exports.PLAYER_PLUGIN = exports.MEDIA_FORMAT = exports.MEDIA_ENTRY_TYPE = exports.DRM_SCHEME = exports.AUDIO_CODEC = void 0;
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

let WAKEMODE;
/**
 * Subtitle Style Settings helper
 * constants
 */

exports.WAKEMODE = WAKEMODE;

(function (WAKEMODE) {
  WAKEMODE["NONE"] = "NONE";
  WAKEMODE["LOCAL"] = "LOCAL";
  WAKEMODE["NETWORK"] = "NETWORK";
})(WAKEMODE || (exports.WAKEMODE = WAKEMODE = {}));

let SUBTITLE_STYLE;
exports.SUBTITLE_STYLE = SUBTITLE_STYLE;

(function (SUBTITLE_STYLE) {
  SUBTITLE_STYLE["EDGE_TYPE_NONE"] = "EDGE_TYPE_NONE";
  SUBTITLE_STYLE["EDGE_TYPE_OUTLINE"] = "EDGE_TYPE_OUTLINE";
  SUBTITLE_STYLE["EDGE_TYPE_DROP_SHADOW"] = "EDGE_TYPE_DROP_SHADOW";
  SUBTITLE_STYLE["EDGE_TYPE_RAISED"] = "EDGE_TYPE_RAISED";
  SUBTITLE_STYLE["EDGE_TYPE_DEPRESSED"] = "EDGE_TYPE_DEPRESSED";
  SUBTITLE_STYLE["FRACTION_50"] = "SUBTITLE_FRACTION_50";
  SUBTITLE_STYLE["FRACTION_75"] = "SUBTITLE_FRACTION_75";
  SUBTITLE_STYLE["FRACTION_100"] = "SUBTITLE_FRACTION_100";
  SUBTITLE_STYLE["FRACTION_125"] = "SUBTITLE_FRACTION_125";
  SUBTITLE_STYLE["FRACTION_150"] = "SUBTITLE_FRACTION_150";
  SUBTITLE_STYLE["FRACTION_200"] = "SUBTITLE_FRACTION_200";
  SUBTITLE_STYLE["TYPEFACE_DEFAULT"] = "DEFAULT";
  SUBTITLE_STYLE["TYPEFACE_DEFAULT_BOLD"] = "DEFAULT_BOLD";
  SUBTITLE_STYLE["TYPEFACE_MONOSPACE"] = "MONOSPACE";
  SUBTITLE_STYLE["TYPEFACE_SERIF"] = "SERIF";
  SUBTITLE_STYLE["TYPEFACE_SANS_SERIF"] = "SANS_SERIF";
  SUBTITLE_STYLE["TYPEFACE_STYLE_NORMAL"] = "NORMAL";
  SUBTITLE_STYLE["TYPEFACE_STYLE_BOLD"] = "BOLD";
  SUBTITLE_STYLE["TYPEFACE_STYLE_ITALIC"] = "ITALIC";
  SUBTITLE_STYLE["TYPEFACE_STYLE_BOLD_ITALIC"] = "BOLD_ITALIC";
  SUBTITLE_STYLE["HORIZONTAL_ALIGNMENT_NORMAL"] = "ALIGN_NORMAL";
  SUBTITLE_STYLE["HORIZONTAL_ALIGNMENT_CENTER"] = "ALIGN_CENTER";
  SUBTITLE_STYLE["HORIZONTAL_ALIGNMENT_OPPOSITE"] = "ALIGN_OPPOSITE";
})(SUBTITLE_STYLE || (exports.SUBTITLE_STYLE = SUBTITLE_STYLE = {}));

let SUBTITLE_PREFERENCE;
exports.SUBTITLE_PREFERENCE = SUBTITLE_PREFERENCE;

(function (SUBTITLE_PREFERENCE) {
  SUBTITLE_PREFERENCE["OFF"] = "OFF";
  SUBTITLE_PREFERENCE["INTERNAL"] = "INTERNAL";
  SUBTITLE_PREFERENCE["EXTERNAL"] = "EXTERNAL";
})(SUBTITLE_PREFERENCE || (exports.SUBTITLE_PREFERENCE = SUBTITLE_PREFERENCE = {}));

let VIDEO_CODEC;
exports.VIDEO_CODEC = VIDEO_CODEC;

(function (VIDEO_CODEC) {
  VIDEO_CODEC["HEVC"] = "HEVC";
  VIDEO_CODEC["AV1"] = "AV1";
  VIDEO_CODEC["VP9"] = "VP9";
  VIDEO_CODEC["VP8"] = "VP8";
  VIDEO_CODEC["AVC"] = "AVC";
})(VIDEO_CODEC || (exports.VIDEO_CODEC = VIDEO_CODEC = {}));

let AUDIO_CODEC;
exports.AUDIO_CODEC = AUDIO_CODEC;

(function (AUDIO_CODEC) {
  AUDIO_CODEC["AAC"] = "AAC";
  AUDIO_CODEC["AC3"] = "AC3";
  AUDIO_CODEC["E_AC3"] = "E_AC3";
  AUDIO_CODEC["OPUS"] = "OPUS";
})(AUDIO_CODEC || (exports.AUDIO_CODEC = AUDIO_CODEC = {}));

let VR_INTERACTION_MODE;
exports.VR_INTERACTION_MODE = VR_INTERACTION_MODE;

(function (VR_INTERACTION_MODE) {
  VR_INTERACTION_MODE["MOTION"] = "Motion";
  VR_INTERACTION_MODE["TOUCH"] = "Touch";
  VR_INTERACTION_MODE["MOTION_WITH_TOUCH"] = "MotionWithTouch";
  VR_INTERACTION_MODE["CARD_BOARD_MOTION"] = "CardboardMotion";
  VR_INTERACTION_MODE["CARD_BOARD_MOTION_WITH_TOUCH"] = "CardboardMotionWithTouch";
})(VR_INTERACTION_MODE || (exports.VR_INTERACTION_MODE = VR_INTERACTION_MODE = {}));
//# sourceMappingURL=consts.js.map