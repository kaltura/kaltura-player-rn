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
    
    override var kalturaPlayer: KalturaPlayer? {
        return kalturaBasicPlayer
    }
    
    private var kalturaBasicPlayer: KalturaBasicPlayer! // Created upon init
    
    override init(withOptions initOptions: RNKPInitOptions) {
        super.init(withOptions: initOptions)
        
        KalturaBasicPlayer.setup()
        self.kalturaBasicPlayer = KalturaBasicPlayer(options: self.playerOptions)
        updateSettings()
    }
    
    override func load(assetId: String, mediaAsset: String, callback: @escaping (_ error: KalturaPlayerRNError?) -> Void) {
        guard let contentUrl = URL(string: assetId) else {
            let message = "The content URL is not valid."
            let error = KalturaPlayerRNError.loadMediaFailed(message: message)
            callback(error)
            return
        }
        guard let basicMediaAsset = parseBasicMediaAsset(mediaAsset) else {
            let message = "Parsing the Media Asset failed."
            let error = KalturaPlayerRNError.loadMediaFailed(message: message)
            callback(error)
            return
        }
        
        let mediaOptions = basicMediaAsset.getMediaOptions()
        
        kalturaBasicPlayer.setupMediaEntry(id: basicMediaAsset.id ?? basicMediaAsset.name ?? assetId,
                                           contentUrl: contentUrl,
                                           drmData: nil,
                                           mediaFormat: PKMediaSource.MediaFormat(string: basicMediaAsset.mediaFormat ?? ""),
                                           mediaType: MediaType(string: basicMediaAsset.mediaEntryType ?? ""),
                                           mediaOptions: mediaOptions)
        
        // If the autoPlay and preload was set to false, prepare will not be called automatically
        if initOptions.autoplay == false && initOptions.preload == false {
            kalturaBasicPlayer.prepare()
        }
        
        callback(nil)
    }
}

extension BasicKalturaPlayerRN {
    
    private func parseBasicMediaAsset(_ mediaAsset: String) -> BasicMediaAsset? {
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
}
