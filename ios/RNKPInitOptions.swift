//
//  RNKPInitOptions.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 23/05/2022.
//

import Foundation
import PlayKit
//import PlayKitYoubora

struct RNKPInitOptions: Decodable {
    let serverUrl: String?
    let autoplay: Bool?
    let preload: Bool?
    let plugins: Plugins?
    let requestConfig: RequestConfig?
    let allowCrossProtocolRedirect: Bool?
//    let warmupUrls: [String]?
    let ks: String?
    let referrer: String?
    let abrSettings: ABRSettings?
    let networkSettings: NetworkSettings?
    let trackSelection: TrackSelection?
//    let preferredMediaFormat: PKMediaFormat?
//    let lowLatencyConfig: PKLowLatencyConfig?
//    let allowClearLead: Bool?
//    let enableDecoderFallback: Bool?
//    let secureSurface: Bool?
//    let adAutoPlayOnResume: Bool?
//    let isVideoViewHidden: Bool?
//    let forceSinglePlayerEngine: Bool?
//    let aspectRatioResizeMode: PKAspectRatioResizeMode?
//    let isTunneledAudioPlayback: Bool?
//    let handleAudioBecomingNoisyEnabled: Bool?
//    let handleAudioFocus: Bool?
//    let maxVideoSize: PKMaxVideoSize?
//    let maxVideoBitrate: Int?
//    let maxAudioBitrate: Int?
//    let maxAudioChannelCount: Int?
    let multicastSettings: MulticastSettings?
    let mediaEntryCacheConfig: MediaEntryCacheConfig?
//    let subtitleStyling: SubtitleStyling?
//    let wakeMode: PKWakeMode?
//    let subtitlePreference: PKSubtitlePreference?
//    let videoCodecSettings: VideoCodecSettings?
//    let audioCodecSettings: AudioCodecSettings?
//    let loadControlBuffers: LoadControlBuffers?
//    let vrSettings: VRSettings?
    
    let allowFairPlayOnExternalScreens: Bool?
    let shouldPlayImmediately: Bool?
    let handleAudioFocus: Bool?
}

struct RequestConfig: Decodable {
    let crossProtocolRedirectEnabled: Bool?
    let readTimeoutMs: Double?
    let connectTimeoutMs: Double?
}

struct NetworkSettings: Decodable {
    let autoBuffer: Bool?
    let preferredForwardBufferDuration: Double?
    let automaticallyWaitsToMinimizeStalling: Bool?
}

struct MulticastSettings: Decodable {
    let useExoDefaultSettings: Bool?
    let maxPacketSize: Double?
    let socketTimeoutMillis: Double?
    let extractorMode: String?
    let firstSampleTimestampUs: Double?
}

struct MediaEntryCacheConfig: Decodable {
    let allowMediaEntryCaching: Bool?
    let maxMediaEntryCacheSize: Double?
    let timeoutMs: Double?
}

struct ABRSettings: Decodable {
    let minVideoBitrate: Double?
    let maxVideoBitrate: Double?
}

struct TrackSelection: Decodable {
    let textMode: String?
    let textLanguage: String?
    let audioMode: String?
    let audioLanguage: String?
}

// MARK: -

struct Plugins: Decodable {
    let ima: IMA?
    let imadai: IMADAI? // Needs FEC-12531 & FEC-12532
    let youbora: Youbora?
}

// MARK: -

struct IMA: Decodable, Loopable {
    // IMAAdsRequest
    let adTagUrl: String?
    let adsResponse: String?
    let vastLoadTimeout: Double?
    
    // IMASettings
    let ppid: String?
    let language: String?
    let maxRedirects: Int?
    let playerType: String?
    let playerVersion: String?
    let enableDebugMode: Bool?

    let alwaysStartWithPreroll: Bool?
}

// MARK: -

struct IMADAI: Decodable, Loopable {
    // Media Data
//    let streamType: PKIMADAIStreamType = .vod // We will need to fix it inside IMADAI to decide this via the data sent.
    let assetTitle: String?
    let assetKey: String? // Needed for Live
    let apiKey: String?
    let contentSourceId: String? // Needed for VOD
    let videoId: String? // Needed for VOD
    let licenseUrl: String?
    
    // IMASettings
    let ppid: String?
    let language: String?
    let maxRedirects: Int?
    let enableBackgroundPlayback: Bool?
    let autoPlayAdBreaks: Bool?
    let disableNowPlayingInfo: Bool?
    let playerType: String?
    let playerVersion: String?
    let enableDebugMode: Bool?
    
    let alwaysStartWithPreroll: Bool?
    
    let adAttribution: Bool?
    let adCountDown: Bool?
    let disablePersonalizedAds: Bool? // adTagParameters.put("npa", 1);
    let enableAgeRestriction: Bool? // adTagParameters.put("tfua", 1);
}

// MARK: -

struct Youbora: Decodable, Loopable {
    let params: [String: Any]?
    
    enum CodingKeys: String, CodingKey {
        case params
    }

    init(from decoder: Decoder) throws {
        let values = try decoder.container(keyedBy: CodingKeys.self)
        self.params = try values.decode([String: Any].self, forKey: .params)
    }
}
