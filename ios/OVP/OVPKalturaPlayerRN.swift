//
//  OVPKalturaPlayerRN.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 17/06/2022.
//

import KalturaPlayer
import PlayKit

class OVPKalturaPlayerRN: KalturaPlayerRN {
    
    override var kalturaPlayer: KalturaPlayer? {
        return kalturaOVPPlayer
    }
    
    private var kalturaOVPPlayer: KalturaOVPPlayer! // Created upon init
    
    init(withOptions initOptions: RNKPInitOptions, partnerId: Int) {
        super.init(withOptions: initOptions)
        
        KalturaOVPPlayer.setup(partnerId: Int64(partnerId), serverURL: initOptions.serverUrl, referrer: initOptions.referrer)
        kalturaOVPPlayer = KalturaOVPPlayer(options: playerOptions)
    }
    
    override func load(assetId: String, mediaAsset: String, callback: @escaping (_ error: KalturaPlayerRNError?) -> Void) {
        guard let ovpMediaAsset = parseOVPMediaAsset(mediaAsset) else {
            let message = "Parsing the OVPMediaAsset failed."
            let error = KalturaPlayerRNError.loadMediaFailed(message: message)
            callback(error)
            return
        }
        
        let mediaOptions = ovpMediaAsset.getMediaOptions()
        mediaOptions.entryId = assetId
        
        kalturaOVPPlayer.loadMedia(options: mediaOptions) { [weak self] error in
            if let error = error {
                let message = error.localizedDescription
                let kpRNError = KalturaPlayerRNError.loadMediaFailed(message: message)
                PKLog.debug("Load media failed with error: \(message)")
                callback(kpRNError)
            } else {
                if self?.initOptions.autoplay == false && self?.initOptions.preload == false {
                    self?.kalturaOVPPlayer.prepare()
                }
                callback(nil)
            }
        }
    }
}

extension OVPKalturaPlayerRN {
    
    private func parseOVPMediaAsset(_ mediaAsset: String) -> OVPMediaAsset? {
        let data = Data(mediaAsset.utf8)
        let mediaAsset: OVPMediaAsset?
        do {
            mediaAsset = try JSONDecoder().decode(OVPMediaAsset.self, from: data)
        } catch let error as NSError {
            PKLog.debug("Couldn't parse OVP MediaAsset, error: \(error)")
            return nil
        }
        return mediaAsset
    }
}
