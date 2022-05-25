export let PLAYER_TYPE;

(function (PLAYER_TYPE) {
  PLAYER_TYPE["OVP"] = "ovp";
  PLAYER_TYPE["OTT"] = "ott";
  PLAYER_TYPE["BASIC"] = "basic";
})(PLAYER_TYPE || (PLAYER_TYPE = {}));

export let MEDIA_FORMAT;

(function (MEDIA_FORMAT) {
  MEDIA_FORMAT["DASH"] = "dash";
  MEDIA_FORMAT["HLS"] = "hls";
  MEDIA_FORMAT["WVM"] = "wvm";
  MEDIA_FORMAT["MP4"] = "mp4";
  MEDIA_FORMAT["MP3"] = "mp3";
  MEDIA_FORMAT["UDP"] = "udp";
})(MEDIA_FORMAT || (MEDIA_FORMAT = {}));

export let MEDIA_ENTRY_TYPE;

(function (MEDIA_ENTRY_TYPE) {
  MEDIA_ENTRY_TYPE["VOD"] = "Vod";
  MEDIA_ENTRY_TYPE["LIVE"] = "Live";
  MEDIA_ENTRY_TYPE["DVRLIVE"] = "DvrLive";
})(MEDIA_ENTRY_TYPE || (MEDIA_ENTRY_TYPE = {}));

export let DRM_SCHEME;

(function (DRM_SCHEME) {
  DRM_SCHEME["WIDEVINE_CENC"] = "WidevineCENC";
  DRM_SCHEME["PLAYREADY_CENC"] = "PlayReadyCENC";
  DRM_SCHEME["WIDEVINE_CENC_CLASSIC"] = "WidevineClassic";
  DRM_SCHEME["PLAYREADY_CLASSIC"] = "PlayReadyClassic";
})(DRM_SCHEME || (DRM_SCHEME = {}));

export let PLAYER_PLUGIN;

(function (PLAYER_PLUGIN) {
  PLAYER_PLUGIN["IMA"] = "ima";
  PLAYER_PLUGIN["IMADAI"] = "imadai";
  PLAYER_PLUGIN["YOUBORA"] = "youbora";
  PLAYER_PLUGIN["KAVA"] = "kava";
  PLAYER_PLUGIN["OTT_ANALYTICS"] = "ottAnalytics";
  PLAYER_PLUGIN["BROADPEAK"] = "broadpeak";
})(PLAYER_PLUGIN || (PLAYER_PLUGIN = {}));

export let PLAYER_RESIZE_MODES;

(function (PLAYER_RESIZE_MODES) {
  PLAYER_RESIZE_MODES["FIT"] = "fit";
  PLAYER_RESIZE_MODES["FIXED_WIDTH"] = "fixedWidth";
  PLAYER_RESIZE_MODES["FIXED_HEIGHT"] = "fixedHeight";
  PLAYER_RESIZE_MODES["FILL"] = "fill";
  PLAYER_RESIZE_MODES["ZOOM"] = "zoom";
})(PLAYER_RESIZE_MODES || (PLAYER_RESIZE_MODES = {}));
//# sourceMappingURL=consts.js.map