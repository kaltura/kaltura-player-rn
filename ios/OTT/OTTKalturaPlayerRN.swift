//
//  OTTKalturaPlayerRN.swift
//  RNKalturaPlayer
//
//  Created by Nilit Danan on 17/06/2022.
//

import KalturaPlayer
import PlayKit

class OTTKalturaPlayerRN: KalturaPlayerRN {
    
    override var kalturaPlayer: KalturaPlayer? {
        return kalturaOTTPlayer
    }
    
    private var kalturaOTTPlayer: KalturaOTTPlayer! // Created upon init
    private let defaultServerURL = "https://rest-us.ott.kaltura.com/v4_5/api_v3/"
    
    convenience init(withOptions initOptions: RNKPInitOptions, partnerId: Int) {
        self.init(withOptions: initOptions)
        
        var serverURL: String = initOptions.serverUrl ?? ""
        if serverURL.isEmpty == true {
            PKLog.debug("The serverUrl is missing or empty in the initOptions.")
            serverURL = defaultServerURL
        }
        
        KalturaOTTPlayer.setup(partnerId: Int64(partnerId), serverURL: serverURL)
        kalturaOTTPlayer = KalturaOTTPlayer(options: playerOptions)
    }
    
    // TODO: Need to fix load to return the error
    override func load(assetId: String, mediaAsset: String) {
        guard let ottMediaAsset = parseOTTMediaAsset(mediaAsset) else { return }
        
        let mediaOptions = ottMediaAsset.getMediaOptions()
        mediaOptions.assetId = assetId
        
        kalturaOTTPlayer.loadMedia(options: mediaOptions) { [weak self] error in
            if error != nil {
                PKLog.debug("Load media faild with error: \(String(describing: error?.localizedDescription))")
            } else {
                if self?.initOptions.autoplay == false && self?.initOptions.preload == false {
                    self?.kalturaOTTPlayer.prepare()
                }
            }
        }
    }
}

extension OTTKalturaPlayerRN {
    
    private func parseOTTMediaAsset(_ mediaAsset: String) -> OTTMediaAsset? {
        let data = Data(mediaAsset.utf8)
        let mediaAsset: OTTMediaAsset?
        do {
            mediaAsset = try JSONDecoder().decode(OTTMediaAsset.self, from: data)
        } catch let error as NSError {
            PKLog.debug("Couldn't parse OTT MediaAsset, error: \(error)")
            return nil
        }
        return mediaAsset
    }
}
