//
//  OTTMediaAsset.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 07/07/2022.
//

import Foundation
import KalturaPlayer
import PlayKitProviders

struct OTTMediaAsset: Codable {
    var ks: String?
    var startPosition: Double?
    
    var assetType: String?
    var assetReferenceType: String?
    var formats: [String]?
    var fileIds: [String]?
    var playbackContextType: String?
    var networkProtocol: String?
    var urlType: String?
    var streamerType: String?
    var adapterData: [String: String]?
//    TODO: Missing the following, need to add
//    var disableMediaHit: Bool?
//    var disableMediaMark: Bool?
//    var isExperimentalLiveMediaHit: Bool?
    
//    TODO: Need to fix in RN
//    private String format;
//    private String fileId;
//    private String protocol;
//    private String referrer;
    
    func getMediaOptions() -> OTTMediaOptions {
        let mediaOptions = OTTMediaOptions()
        
        if let ks = ks {
            mediaOptions.ks =  ks
        }
        if let startPosition = startPosition {
            mediaOptions.startTime = startPosition
        }
        if let assetType = assetType {
            mediaOptions.assetType = AssetType(string: assetType)
        }
        if let assetReferenceType = assetReferenceType {
            mediaOptions.assetReferenceType = AssetReferenceType(string: assetReferenceType)
        }
        if let formats = formats {
            mediaOptions.formats = formats
        }
        if let fileIds = fileIds {
            mediaOptions.fileIds = fileIds
        }
        if let playbackContextType = playbackContextType {
            mediaOptions.playbackContextType = PlaybackContextType(string: playbackContextType)
        }
        if let networkProtocol = networkProtocol {
            mediaOptions.networkProtocol = networkProtocol
        }
        if let urlType = urlType {
            mediaOptions.urlType = urlType
        }
        if let streamerType = streamerType {
            mediaOptions.streamerType = streamerType
        }
        if let adapterData = adapterData {
            mediaOptions.adapterData = adapterData
        }
        
        return mediaOptions
    }
}

extension AssetType {
    init(string: String) {
        switch string {
        case "epg":
            self = .epg
        case "recording":
            self = .recording
        case "media":
            self = .media
        default:
            self = .unset
        }
    }
}

extension AssetReferenceType {
    init(string: String) {
        switch string {
        case "media":
            self = .media
        case "epgInternal":
            self = .epgInternal
        case "epgExternal":
            self = .epgExternal
        case "npvr":
            self = .npvr
        default:
            self = .unset
        }
    }
}

extension PlaybackContextType {
    init(string: String) {
        switch string {
        case "trailer":
            self = .trailer
        case "catchup":
            self = .catchup
        case "startOver":
            self = .startOver
        case "playback":
            self = .playback
        default:
            self = .unset
        }
    }
}



