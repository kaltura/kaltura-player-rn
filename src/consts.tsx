export enum PLAYER_TYPE {
    OVP = 'ovp',
    OTT = 'ott',
    BASIC = 'basic',
  }
  
  export enum MEDIA_FORMAT {
    DASH = 'dash',
    HLS = 'hls',
    WVM = 'wvm',
    MP4 = 'mp4',
    MP3 = 'mp3',
    UDP = 'udp',
  }
  
  export enum MEDIA_ENTRY_TYPE {
    VOD = 'Vod',
    LIVE = 'Live',
    DVRLIVE = 'DvrLive',
  }
  
  export enum DRM_SCHEME {
    WIDEVINE_CENC = 'WidevineCENC',
    PLAYREADY_CENC = 'PlayReadyCENC',
    WIDEVINE_CENC_CLASSIC = 'WidevineClassic',
    PLAYREADY_CLASSIC = 'PlayReadyClassic',
  }
  
  export enum PLAYER_PLUGIN {
    IMA = 'ima',
    IMADAI = 'imadai',
    YOUBORA = 'youbora',
    KAVA = 'kava',
    OTT_ANALYTICS = 'ottAnalytics',
    BROADPEAK = 'broadpeak',
  }
  
  export enum PLAYER_RESIZE_MODES {
    FIT = 'fit',
    FIXED_WIDTH = 'fixedWidth',
    FIXED_HEIGHT = 'fixedHeight',
    FILL = 'fill',
    ZOOM = 'zoom',
  }

  export enum WAKEMODE {
    NONE = 'NONE',
    LOCAL = 'LOCAL',
    NETWORK = 'NETWORK'
  }

  export enum SUBTITLE_PREFERENCE {
    OFF = 'OFF',
    INTERNAL = 'INTERNAL',
    EXTERNAL = 'EXTERNAL'
  }

  export enum VIDEO_CODEC {
    HEVC = 'HEVC',
    AV1 = 'AV1',
    VP9 = 'VP9',
    VP8 = 'VP8',
    AVC = 'AVC',
  }

  export enum AUDIO_CODEC {
    AAC = 'AAC',
    AC3 = 'AC3',
    E_AC3 = 'E_AC3',
    OPUS = 'OPUS',
  }
  