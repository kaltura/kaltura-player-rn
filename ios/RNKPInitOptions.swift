//
//  RNKPInitOptions.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 23/05/2022.
//

import Foundation
import PlayKit
//import PlayKitYoubora

struct RNKPInitOptions: Codable {
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

struct RequestConfig: Codable {
    let crossProtocolRedirectEnabled: Bool?
    let readTimeoutMs: Double?
    let connectTimeoutMs: Double?
}

struct NetworkSettings: Codable {
    let autoBuffer: Bool?
    let preferredForwardBufferDuration: Double?
    let automaticallyWaitsToMinimizeStalling: Bool?
}

struct MulticastSettings: Codable {
    let useExoDefaultSettings: Bool?
    let maxPacketSize: Double?
    let socketTimeoutMillis: Double?
    let extractorMode: String?
    let firstSampleTimestampUs: Double?
}

struct MediaEntryCacheConfig: Codable {
    let allowMediaEntryCaching: Bool?
    let maxMediaEntryCacheSize: Double?
    let timeoutMs: Double?
}

struct ABRSettings: Codable {
    let minVideoBitrate: Double?
    let maxVideoBitrate: Double?
}

struct TrackSelection: Codable {
    let textMode: String?
    let textLanguage: String?
    let audioMode: String?
    let audioLanguage: String?
}

struct Plugins: Codable {
    let ima: IMA?
//    let youbora: YouboraConfig?
}

struct IMA: Codable {
    let adTagUrl: String?
    let alwaysStartWithPreroll: Bool?
    let enableDebugMode: Bool?
}
