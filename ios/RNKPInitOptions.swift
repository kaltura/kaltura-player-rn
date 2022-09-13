//
//  RNKPInitOptions.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 23/05/2022.
//

import Foundation
import PlayKit

struct RNKPInitOptions: Decodable {
    let serverUrl: String?
    let autoplay: Bool?
    let preload: Bool?
    let plugins: Plugins?
    let ks: String?
    let referrer: String?
    let abrSettings: ABRSettings?
    let networkSettings: NetworkSettings?
    let trackSelection: TrackSelection?
    let lowLatencyConfig: PKLowLatencyConfig?
    let aspectRatioResizeMode: String? // Translate to the contentMode in the PlayerView. Available options are fit/fill/zoom.
    let subtitleStyling: SubtitleStyling?
    let vrSettings: VRSettings?
}

struct ABRSettings: Decodable {
    let maxVideoBitrate: Double? // preferredPeakBitRate in PKNetworkSettings
}

// PKNetworkSettings
struct NetworkSettings: Decodable {
    let autoBuffer: Bool?
    let preferredForwardBufferDuration: Double? // from iOS 10.0
    let automaticallyWaitsToMinimizeStalling: Bool? // from iOS 10.0, tvOS 10.0
}

struct TrackSelection: Decodable {
    let textMode: String?
    let textLanguage: String?
    let audioMode: String?
    let audioLanguage: String?
}

struct PKLowLatencyConfig: Decodable {
    let targetOffsetMs: UInt?
}

struct SubtitleStyling: Decodable {
    
}

struct VRSettings: Decodable {
    let vrModeEnabled: Bool?
}

// MARK: -

struct Plugins: Decodable {
    let ima: IMA?
    let imadai: IMADAI?
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
