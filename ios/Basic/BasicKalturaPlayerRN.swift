//
//  BasicKalturaPlayerRN.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 17/06/2022.
//

import Foundation
import KalturaPlayer
import PlayKit

class BasicKalturaPlayerRN: KalturaPlayerRN {
    
    var kalturaBasicPlayer: KalturaBasicPlayer?
    
    override init(withOptions initOptions: RNKPInitOptions) {
        super.init(withOptions: initOptions)
        
        KalturaBasicPlayer.setup()
        self.kalturaBasicPlayer = KalturaBasicPlayer(options: self.playerOptions)
    }
    
    override func connectView(_ view: KalturaPlayerView) {
        kalturaBasicPlayer?.view = view
    }
    
    override func load(assetId: String, mediaAsset: String) {
        guard let kalturaPlayer = kalturaBasicPlayer else { return }
        guard let contentUrl = URL(string: assetId) else { return }
        guard let basicMediaAsset = parseBasicMediaAsset(mediaAsset) else { return }
        
        let mediaOptions = MediaOptions()
        if let startPosition = basicMediaAsset.startPosition {
            mediaOptions.startTime = startPosition
        }
        
        kalturaPlayer.setupMediaEntry(id: basicMediaAsset.id ?? basicMediaAsset.name ?? assetId,
                                      contentUrl: contentUrl,
                                      drmData: nil,
                                      mediaFormat: mediaFormat(basicMediaAsset.mediaFormat ?? ""),
                                      mediaType: mediaType(basicMediaAsset.mediaEntryType ?? ""),
                                      mediaOptions: mediaOptions)
        
        // If the autoPlay and preload was set to false, prepare will not be called automatically
        if initOptions.autoplay == false && initOptions.preload == false {
            kalturaPlayer.prepare()
        }
    }
}

extension BasicKalturaPlayerRN {
    
    func parseBasicMediaAsset(_ mediaAsset: String) -> BasicMediaAsset? {
        let data = Data(mediaAsset.utf8)
        let mediaAsset: BasicMediaAsset?
        do {
            mediaAsset = try JSONDecoder().decode(BasicMediaAsset.self, from: data)
        } catch let error as NSError {
            PKLog.debug("Couldn't parse Basic MediaAsset, error: \(error)")
            return nil
        }
        return mediaAsset
    }
    
    func mediaFormat(_ format: String) -> PKMediaSource.MediaFormat {
        switch format.lowercased() {
        case "hls":
            return PKMediaSource.MediaFormat.hls
        case "wvm":
            return PKMediaSource.MediaFormat.wvm
        case "mp4":
            return PKMediaSource.MediaFormat.mp4
        case "mp3":
            return PKMediaSource.MediaFormat.mp3
        default:
            return PKMediaSource.MediaFormat.unknown
        }
    }
    
    func mediaType(_ type: String) -> MediaType {
        switch type.lowercased() {
        case "dvrlive":
            return MediaType.dvrLive
        case "live":
            return MediaType.live
        case "vod":
            return MediaType.vod
        default:
            return .unknown
        }
    }
}



