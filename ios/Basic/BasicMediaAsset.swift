//
//  BasicMediaAsset.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 24/06/2022.
//

import Foundation
import KalturaPlayer
import PlayKit

struct BasicMediaAsset: Codable {
    var ks: String?
    var startPosition: Double?
    
    var id: String?
    var name: String?
    var duration: Double?
    var mediaEntryType: String?
    var mediaFormat: String?
    // TODO: Add drmData
//    var drmData: [DRMParams]?
    
    func getMediaOptions() -> MediaOptions {
        let mediaOptions = MediaOptions()
        
        if let ks = ks {
            mediaOptions.ks =  ks
        }
        if let startPosition = startPosition {
            mediaOptions.startTime = startPosition
        }
        
        return mediaOptions
    }
}

extension PKMediaSource.MediaFormat {
    init(string: String) {
        switch string.lowercased() {
        case "hls":
            self = .hls
        case "wvm":
            self = .wvm
        case "mp4":
            self = .mp4
        case "mp3":
            self = .mp3
        default:
            self = .unknown
        }
    }
}

extension MediaType {
    init(string: String) {
        switch string.lowercased() {
        case "dvrlive":
            self = .dvrLive
        case "live":
            self = .live
        case "vod":
            self = .vod
        default:
            self = .unknown
        }
    }
}
