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
        
        // TODO: Need to get the referrer in the initOptions and pass it in the setup.
        let serverURL: String = initOptions.serverUrl ?? ""
        if serverURL.isEmpty == true {
            KalturaOVPPlayer.setup(partnerId: Int64(partnerId))
        } else {
            KalturaOVPPlayer.setup(partnerId: Int64(partnerId), serverURL: serverURL)
        }
        
        kalturaOVPPlayer = KalturaOVPPlayer(options: playerOptions)
    }
    
    override func load(assetId: String, mediaAsset: String) {
        guard let ovpMediaAsset = parseOVPMediaAsset(mediaAsset) else { return }
        
        let mediaOptions = ovpMediaAsset.getMediaOptions()
        mediaOptions.entryId = assetId
        
        kalturaOVPPlayer.loadMedia(options: mediaOptions) { [weak self] error in
            if error != nil {
                PKLog.debug("Load media faild with error: \(String(describing: error?.localizedDescription))")
            } else {
                if self?.initOptions.autoplay == false && self?.initOptions.preload == false {
                    self?.kalturaOVPPlayer.prepare()
                }
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
