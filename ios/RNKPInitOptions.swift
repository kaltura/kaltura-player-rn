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
    let requestConfig: RequestConfig?
    let allowCrossProtocolRedirect: Bool?
    let allowFairPlayOnExternalScreens: Bool?
    let shouldPlayImmediately: Bool?
    let networkSettings: NetworkSettings?
    let multicastSettings: MulticastSettings?
    let mediaEntryCacheConfig: MediaEntryCacheConfig?
    let abrSettings: ABRSettings?
    let trackSelection: TrackSelection?
    let handleAudioFocus: Bool?
    let plugins: Plugins?
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

struct Plugins: Decodable {
    let ima: IMA?
//    let youbora: YouboraConfig?
}

struct IMA: Decodable {
    let adTagUrl: String?
    let alwaysStartWithPreroll: Bool?
    let enableDebugMode: Bool?
}

extension RNKPInitOptions {
    static func parse(initOptions: [String: Any]) -> RNKPInitOptions? {
        if !JSONSerialization.isValidJSONObject(initOptions) {
            PKLog.error("Options is not a valid JSON Object")
            return nil
        }
        
        do {
            let data = try JSONSerialization.data(withJSONObject: initOptions, options: .prettyPrinted)
            let decodedInitOptions = try JSONDecoder().decode(RNKPInitOptions.self, from: data)
            return decodedInitOptions
            
        } catch let error as NSError {
            PKLog.error("Couldn't parse data into RNKPInitOptions error: \(error)")
        }
        
        return nil
    }
}
